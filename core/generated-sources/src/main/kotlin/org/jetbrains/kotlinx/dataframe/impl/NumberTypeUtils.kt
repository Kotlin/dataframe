package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.impl.api.createConverter
import org.jetbrains.kotlinx.dataframe.impl.commonNumberType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Number type graph, structured in terms of number complexity.
 * A number can always be expressed lossless by a number of a more complex type (any of its parents).
 *
 * ```
 *         BigDecimal
 *         /      \
 *   BigInteger    |
 *         |       |
 *       ULong     |
 *         |       |
 *       Long    Double
 *          \   /    |
 *           UInt    |
 *            |      |
 *           Int   Float
 *            |   /
 *           UShort
 *             |
 *           Short
 *             |
 *           UByte
 *            |
 *           Byte
 * ```
 *
 * For any two numbers, we can find the nearest common ancestor in this graph
 * by calling [DirectedAcyclicGraph.findNearestCommonVertex].
 * @see getCommonNumberClass
 * @see commonNumberClass
 */
internal val numberTypeGraph: DirectedAcyclicGraph<KType> by lazy {
    dagOf(
        typeOf<BigDecimal>() to typeOf<BigInteger>(),
        typeOf<BigDecimal>() to typeOf<Double>(),
        typeOf<BigInteger>() to typeOf<ULong>(),
        typeOf<ULong>() to typeOf<Long>(),
        typeOf<Long>() to typeOf<UInt>(),
        typeOf<Double>() to typeOf<UInt>(),
        typeOf<Double>() to typeOf<Float>(),
        typeOf<UInt>() to typeOf<Int>(),
        typeOf<Int>() to typeOf<UShort>(),
        typeOf<Float>() to typeOf<UShort>(),
        typeOf<UShort>() to typeOf<Short>(),
        typeOf<Short>() to typeOf<UByte>(),
        typeOf<UByte>() to typeOf<Byte>(),
    )
}

/** Number type graph, structured in terms of number complexity.
 * A number can always be expressed lossless by a number of a more complex type (any of its parents).
 *
 * ```
 *         BigDecimal
 *         /      \
 *   BigInteger    |
 *         |       |
 *       ULong     |
 *         |       |
 *       Long    Double
 *          \   /    |
 *           UInt    |
 *            |      |
 *           Int   Float
 *            |   /
 *           UShort
 *             |
 *           Short
 *             |
 *           UByte
 *            |
 *           Byte
 * ```
 *
 * For any two numbers, we can find the nearest common ancestor in this graph
 * by calling [DirectedAcyclicGraph.findNearestCommonVertex][org.jetbrains.kotlinx.dataframe.impl.DirectedAcyclicGraph.findNearestCommonVertex].
 * @see getCommonNumberClass
 * @see commonNumberClass */
internal val numberClassGraph: DirectedAcyclicGraph<KClass<*>> by lazy {
    numberTypeGraph.map { it.classifier as KClass<*> }
}

/**
 * Determines the nearest common numeric type, in terms of complexity, between two given classes/types.
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless an unsigned type is provided in the input, it will never be returned.
 * Meaning, given two [Number] inputs, the output will always be a [Number].
 *
 * @param first The first numeric type to compare. Can be null, in which case the second to is returned.
 * @param second The second numeric to compare. Cannot be null.
 * @return The nearest common numeric type between the two input classes.
 *   If no common class is found, [IllegalStateException] is thrown.
 * @see numberTypeGraph
 */
internal fun getCommonNumberType(first: KType?, second: KType): KType {
    if (first == null) return second

    val firstWithoutNullability = first.withNullability(false)
    val secondWithoutNullability = second.withNullability(false)

    val result = if (firstWithoutNullability == secondWithoutNullability) {
        firstWithoutNullability
    } else {
        numberTypeGraph.findNearestCommonVertex(firstWithoutNullability, secondWithoutNullability)
            ?: error("Can not find common number type for $first and $second")
    }

    return if (first.isMarkedNullable || second.isMarkedNullable) result.withNullability(true) else result
}

/** Determines the nearest common numeric type, in terms of complexity, between two given classes/types.
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless an unsigned type is provided in the input, it will never be returned.
 * Meaning, given two [Number] inputs, the output will always be a [Number].
 *
 * @param first The first numeric type to compare. Can be null, in which case the second to is returned.
 * @param second The second numeric to compare. Cannot be null.
 * @return The nearest common numeric type between the two input classes.
 *   If no common class is found, [IllegalStateException] is thrown.
 * @see numberTypeGraph */
@Suppress("IntroduceWhenSubject")
internal fun getCommonNumberClass(first: KClass<*>?, second: KClass<*>): KClass<*> =
    when {
        first == null -> second

        first == second -> first

        else -> numberClassGraph.findNearestCommonVertex(first, second)
            ?: error("Can not find common number type for $first and $second")
    }

/**
 * Determines the nearest common numeric type, in terms of complexity, all types in [this].
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless an unsigned type is provided in the input, it will never be returned.
 * Meaning, given just [Number] inputs, the output will always be a [Number].
 *
 * @return The nearest common numeric type between the input types.
 *   If no common type is found, it returns [Number].
 * @see numberTypeGraph
 */
internal fun Iterable<KType>.commonNumberType(): KType = fold(null as KType?, ::getCommonNumberType) ?: typeOf<Number>()

/** Determines the nearest common numeric type, in terms of complexity, all types in [this].
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless an unsigned type is provided in the input, it will never be returned.
 * Meaning, given just [Number] inputs, the output will always be a [Number].
 *
 * @return The nearest common numeric type between the input types.
 *   If no common type is found, it returns [Number].
 * @see numberTypeGraph */
internal fun Iterable<KClass<*>>.commonNumberClass(): KClass<*> =
    fold(null as KClass<*>?, ::getCommonNumberClass) ?: Number::class

/**
 * Converts the elements of the given iterable of numbers into a common numeric type based on complexity.
 * The common numeric type is determined using the provided [commonNumberType] parameter
 * or calculated with [Iterable.commonNumberType] from the iterable's elements if not explicitly specified.
 *
 * @param commonNumberType The desired common numeric type to convert the elements to.
 *   This is determined by default using the types of the elements in the iterable.
 * @return A new iterable of numbers where each element is converted to the specified or inferred common number type.
 * @throws IllegalStateException if an element cannot be converted to the common number type.
 * @see Iterable.commonNumberType
 */
@Suppress("UNCHECKED_CAST")
internal fun Iterable<Number>.convertToCommonNumberType(
    commonNumberType: KType = this.types().commonNumberType(),
): Iterable<Number> {
    val converter = createConverter(typeOf<Number>(), commonNumberType)!! as (Number) -> Number?
    return map {
        converter(it) ?: error("Can not convert $it to $commonNumberType")
    }
}
