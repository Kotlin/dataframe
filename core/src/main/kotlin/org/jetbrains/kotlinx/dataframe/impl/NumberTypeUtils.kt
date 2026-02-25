package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.api.createConverter
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * @param useBigNumbers Whether to include [BigDecimal] and [BigInteger] in the graph.
 *   If set to `false`, consider setting [allowLongToDoubleConversion] to `true` to have a single "most complex" number type.
 * @param allowLongToDoubleConversion Whether to allow [Long]/[ULong] -> [Double] conversion.
 *   If set to `true`, [Long] and [ULong] will be joined to [Double] in the graph.
 */
internal data class UnifiedNumberTypeOptions(val useBigNumbers: Boolean, val allowLongToDoubleConversion: Boolean) {
    companion object {
        val DEFAULT = UnifiedNumberTypeOptions(
            useBigNumbers = true,
            allowLongToDoubleConversion = false,
        )
        val PRIMITIVES_ONLY = UnifiedNumberTypeOptions(
            useBigNumbers = false,
            allowLongToDoubleConversion = true,
        )
    }
}

private val unifiedNumberTypeGraphs = mutableMapOf<UnifiedNumberTypeOptions, DirectedAcyclicGraph<KType>>()

/**
 * Number type graph, structured in terms of number complexity.
 * A number can always be expressed lossless by a number of a more complex type (any of its parents).
 *
 * {@include [UnifyingNumbers.Graph]}
 *
 * For any two numbers, we can find the nearest common ancestor in this graph
 * by calling [DirectedAcyclicGraph.findNearestCommonVertex].
 *
 * @param options See [UnifiedNumberTypeOptions]
 * @see getUnifiedNumberClassOrNull
 * @see unifiedNumberClassOrNull
 * @see UnifyingNumbers
 */
internal fun getUnifiedNumberTypeGraph(
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
): DirectedAcyclicGraph<KType> =
    unifiedNumberTypeGraphs.getOrPut(options) {
        buildDag {
            if (options.useBigNumbers) {
                addEdge(typeOf<BigDecimal>(), typeOf<BigInteger>())
                addEdge(typeOf<BigDecimal>(), typeOf<Double>())

                addEdge(typeOf<BigInteger>(), typeOf<ULong>())
                addEdge(typeOf<BigInteger>(), typeOf<Long>())
            }
            if (options.allowLongToDoubleConversion) {
                addEdge(typeOf<Double>(), typeOf<Long>())
                addEdge(typeOf<Double>(), typeOf<ULong>())
            }

            addEdge(typeOf<ULong>(), typeOf<UInt>())

            addEdge(typeOf<Long>(), typeOf<UInt>())
            addEdge(typeOf<Long>(), typeOf<Int>())

            addEdge(typeOf<Double>(), typeOf<Int>())
            addEdge(typeOf<Double>(), typeOf<Float>())
            addEdge(typeOf<Double>(), typeOf<UInt>())

            addEdge(typeOf<UInt>(), typeOf<UShort>())

            addEdge(typeOf<Int>(), typeOf<UShort>())
            addEdge(typeOf<Int>(), typeOf<Short>())

            addEdge(typeOf<Float>(), typeOf<Short>())
            addEdge(typeOf<Float>(), typeOf<UShort>())

            addEdge(typeOf<UShort>(), typeOf<UByte>())

            addEdge(typeOf<Short>(), typeOf<UByte>())
            addEdge(typeOf<Short>(), typeOf<Byte>())

            addEdge(typeOf<UByte>(), nothingType)
            addEdge(typeOf<Byte>(), nothingType)
        }
    }

/** @include [getUnifiedNumberTypeGraph] */
internal fun getUnifiedNumberClassGraph(
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
): DirectedAcyclicGraph<KClass<*>> = getUnifiedNumberTypeGraph(options).map { it.classifier as KClass<*> }

/**
 * Determines the nearest common numeric type, in terms of complexity, between two given classes/types.
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless two unsigned types are provided in the input, it will never be returned.
 * Meaning, a single [Number] input, the output will always be a [Number].
 *
 * @param first The first numeric type to compare. Can be null, in which case the second to is returned.
 * @param second The second numeric to compare. Cannot be null.
 * @param options See [UnifiedNumberTypeOptions]
 * @return The nearest common numeric type between the two input classes.
 *   If no common class is found, [IllegalStateException] is thrown.
 * @see UnifyingNumbers
 */
internal fun getUnifiedNumberTypeOrNull(
    first: KType?,
    second: KType,
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
): KType? {
    if (first == null) return second

    val firstWithoutNullability = first.withNullability(false)
    val secondWithoutNullability = second.withNullability(false)

    val result = if (firstWithoutNullability == secondWithoutNullability) {
        firstWithoutNullability
    } else {
        getUnifiedNumberTypeGraph(options).findNearestCommonVertex(firstWithoutNullability, secondWithoutNullability)
            ?: return null
    }

    return if (first.isMarkedNullable || second.isMarkedNullable) {
        result.withNullability(true)
    } else {
        result
    }
}

/** @include [getUnifiedNumberTypeOrNull] */
@Suppress("IntroduceWhenSubject")
internal fun getUnifiedNumberClassOrNull(
    first: KClass<*>?,
    second: KClass<*>,
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
): KClass<*>? =
    when {
        first == null -> second
        first == second -> first
        else -> getUnifiedNumberClassGraph(options).findNearestCommonVertex(first, second)
    }

/**
 * Determines the nearest common numeric type, in terms of complexity, all types in [this].
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless the input solely exists of unsigned numbers, it will never be returned.
 * Meaning, given a [Number] in the input, the output will always be a [Number].
 *
 * @param options See [UnifiedNumberTypeOptions]
 * @return The nearest common numeric type between the input types.
 *   If no common type is found, it returns `null`.
 * @see UnifyingNumbers
 */
internal fun Iterable<KType>.unifiedNumberTypeOrNull(
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
): KType? =
    fold(null as KType?) { a, b ->
        getUnifiedNumberTypeOrNull(a, b, options) ?: return null
    }

/** @include [unifiedNumberTypeOrNull] */
internal fun Iterable<KClass<*>>.unifiedNumberClassOrNull(
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
): KClass<*>? =
    fold(null as KClass<*>?) { a, b ->
        getUnifiedNumberClassOrNull(a, b, options) ?: return null
    }

/**
 * Converts the elements of the given iterable of numbers into a common numeric type based on complexity.
 * The common numeric type is determined using the provided [commonNumberType] parameter
 * or calculated with [Iterable.unifiedNumberTypeOrNull] from the iterable's elements if not explicitly specified.
 *
 * @param commonNumberType The desired common numeric type to convert the elements to.
 *   By default, (or if `null`), this is determined using the types of the elements in the iterable.
 * @return A new iterable of numbers where each element is converted to the specified or inferred common number type.
 * @throws IllegalStateException if an element cannot be converted to the common number type.
 * @see UnifyingNumbers
 */
@Suppress("UNCHECKED_CAST")
@JvmName("convertNullableIterableToUnifiedNumberType")
internal fun Iterable<Number?>.convertToUnifiedNumberType(
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
    commonNumberType: KType? = null,
): Iterable<Number?> {
    val commonNumberType = commonNumberType ?: this.types().let { types ->
        types.unifiedNumberTypeOrNull(options)
            ?: throw IllegalArgumentException(
                "Cannot find unified number type of types: ${types.joinToString { renderType(it) }}",
            )
    }
    val converter = createConverter(typeOf<Number>(), commonNumberType)!! as (Number) -> Number?
    return map {
        if (it == null) return@map null
        converter(it) ?: error("Can not convert $it to $commonNumberType")
    }
}

/** @include [Iterable.convertToUnifiedNumberType] */
@Suppress("UNCHECKED_CAST")
@JvmName("convertIterableToUnifiedNumberType")
internal fun Iterable<Number>.convertToUnifiedNumberType(
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
    commonNumberType: KType? = null,
): Iterable<Number> =
    (this as Iterable<Number?>)
        .convertToUnifiedNumberType(options, commonNumberType) as Iterable<Number>

/** @include [Iterable.convertToUnifiedNumberType] */
@Suppress("UNCHECKED_CAST")
@JvmName("convertNullableSequenceToUnifiedNumberType")
internal fun Sequence<Number?>.convertToUnifiedNumberType(
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
    commonNumberType: KType? = null,
): Sequence<Number?> {
    val commonNumberType = commonNumberType ?: this.asIterable().types().let { types ->
        types.unifiedNumberTypeOrNull(options)
            ?: throw IllegalArgumentException(
                "Cannot find unified number type of types: ${types.joinToString { renderType(it) }}",
            )
    }
    require(commonNumberType.isSubtypeOf(typeOf<Number?>())) {
        "Cannot convert numbers to $commonNumberType; it is not a subtype of Number?"
    }
    return when (commonNumberType) {
        nothingType -> {
            require(null !in this) { "Cannot unify numbers to Nothing; it contains nulls" }
            this
        }

        nullableNothingType -> this

        else -> {
            val converter = createConverter(typeOf<Number>(), commonNumberType)!! as (Number) -> Number?
            this.map {
                if (it == null) return@map null
                converter(it) ?: error("Can not convert $it to $commonNumberType")
            }
        }
    }
}

/** @include [Iterable.convertToUnifiedNumberType] */
@Suppress("UNCHECKED_CAST")
@JvmName("convertSequenceToUnifiedNumberType")
internal fun Sequence<Number>.convertToUnifiedNumberType(
    options: UnifiedNumberTypeOptions = UnifiedNumberTypeOptions.DEFAULT,
    commonNumberType: KType? = null,
): Sequence<Number> =
    (this as Sequence<Number?>)
        .convertToUnifiedNumberType(options, commonNumberType) as Sequence<Number>

@PublishedApi
internal val primitiveNumberTypes: Set<KType> =
    setOf(
        typeOf<Byte>(),
        typeOf<Short>(),
        typeOf<Int>(),
        typeOf<Long>(),
        typeOf<Float>(),
        typeOf<Double>(),
    )

/** Returns `true` only when this type is exactly `Number` or `Number?`. */
@PublishedApi
internal fun KType.isMixedNumber(): Boolean = this == typeOf<Number>() || this == typeOf<Number?>()

/**
 * Returns `true` when this type is one of the following (nullable) types:
 * [Byte], [Short], [Int], [Long], [Float], or [Double].
 */
@PublishedApi
internal fun KType.isPrimitiveNumber(): Boolean = this.withNullability(false) in primitiveNumberTypes

/**
 * Returns `true` when this type is one of the following (nullable) types:
 * [Byte], [Short], [Int], [Long], [Float], [Double], or [Number].
 *
 * Careful: Will return `true` for `Number`.
 * This type may arise as a supertype from multiple non-primitive number types.
 */
@PublishedApi
internal fun KType.isPrimitiveOrMixedNumber(): Boolean = isPrimitiveNumber() || isMixedNumber()

internal fun Number.isPrimitiveNumber(): Boolean =
    this is Byte ||
        this is Short ||
        this is Int ||
        this is Long ||
        this is Float ||
        this is Double
