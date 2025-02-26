package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.api.createConverter
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
 *            BigDecimal
 *            /      \
 *      BigInteger    \
 *        /   \        \
 *    ULong   Long    Double
 * ..   |    /   |   /   |  \..
 *   \  |   /    |  /    |
 *     UInt     Int    Float
 * ..   |    /   |   /      \..
 *   \  |   /    |  /
 *    UShort   Short
 *      |    /   |
 *      |   /    |
 *    UByte     Byte
 * ```
 *
 * For any two numbers, we can find the nearest common ancestor in this graph
 * by calling [DirectedAcyclicGraph.findNearestCommonVertex].
 * @see getUnifiedNumberClass
 * @see unifiedNumberClass
 * @see UnifyingNumbers
 */
internal val unifiedNumberTypeGraph: DirectedAcyclicGraph<KType> by lazy {
    buildDag {
        addEdge(typeOf<BigDecimal>(), typeOf<BigInteger>())
        addEdge(typeOf<BigDecimal>(), typeOf<Double>())

        addEdge(typeOf<BigInteger>(), typeOf<ULong>())
        addEdge(typeOf<BigInteger>(), typeOf<Long>())

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
    }
}

/** Number type graph, structured in terms of number complexity.
 * A number can always be expressed lossless by a number of a more complex type (any of its parents).
 *
 * ```
 *            BigDecimal
 *            /      \
 *      BigInteger    \
 *        /   \        \
 *    ULong   Long    Double
 * ..   |    /   |   /   |  \..
 *   \  |   /    |  /    |
 *     UInt     Int    Float
 * ..   |    /   |   /      \..
 *   \  |   /    |  /
 *    UShort   Short
 *      |    /   |
 *      |   /    |
 *    UByte     Byte
 * ```
 *
 * For any two numbers, we can find the nearest common ancestor in this graph
 * by calling [DirectedAcyclicGraph.findNearestCommonVertex][org.jetbrains.kotlinx.dataframe.impl.DirectedAcyclicGraph.findNearestCommonVertex].
 * @see getUnifiedNumberClass
 * @see unifiedNumberClass
 * @see UnifyingNumbers */
internal val unifiedNumberClassGraph: DirectedAcyclicGraph<KClass<*>> by lazy {
    unifiedNumberTypeGraph.map { it.classifier as KClass<*> }
}

/**
 * Determines the nearest common numeric type, in terms of complexity, between two given classes/types.
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless two unsigned types are provided in the input, it will never be returned.
 * Meaning, a single [Number] input, the output will always be a [Number].
 *
 * @param first The first numeric type to compare. Can be null, in which case the second to is returned.
 * @param second The second numeric to compare. Cannot be null.
 * @return The nearest common numeric type between the two input classes.
 *   If no common class is found, [IllegalStateException] is thrown.
 * @see UnifyingNumbers
 */
internal fun getUnifiedNumberType(first: KType?, second: KType): KType {
    if (first == null) return second

    val firstWithoutNullability = first.withNullability(false)
    val secondWithoutNullability = second.withNullability(false)

    val result = if (firstWithoutNullability == secondWithoutNullability) {
        firstWithoutNullability
    } else {
        unifiedNumberTypeGraph.findNearestCommonVertex(firstWithoutNullability, secondWithoutNullability)
            ?: error("Can not find common number type for $first and $second")
    }

    return if (first.isMarkedNullable || second.isMarkedNullable) result.withNullability(true) else result
}

/** Determines the nearest common numeric type, in terms of complexity, between two given classes/types.
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless two unsigned types are provided in the input, it will never be returned.
 * Meaning, a single [Number] input, the output will always be a [Number].
 *
 * @param first The first numeric type to compare. Can be null, in which case the second to is returned.
 * @param second The second numeric to compare. Cannot be null.
 * @return The nearest common numeric type between the two input classes.
 *   If no common class is found, [IllegalStateException] is thrown.
 * @see UnifyingNumbers */
@Suppress("IntroduceWhenSubject")
internal fun getUnifiedNumberClass(first: KClass<*>?, second: KClass<*>): KClass<*> =
    when {
        first == null -> second

        first == second -> first

        else -> unifiedNumberClassGraph.findNearestCommonVertex(first, second)
            ?: error("Can not find common number type for $first and $second")
    }

/**
 * Determines the nearest common numeric type, in terms of complexity, all types in [this].
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless the input solely exists of unsigned numbers, it will never be returned.
 * Meaning, given a [Number] in the input, the output will always be a [Number].
 *
 * @return The nearest common numeric type between the input types.
 *   If no common type is found, it returns [Number].
 * @see UnifyingNumbers
 */
internal fun Iterable<KType>.unifiedNumberType(): KType =
    fold(null as KType?, ::getUnifiedNumberType) ?: typeOf<Number>()

/** Determines the nearest common numeric type, in terms of complexity, all types in [this].
 *
 * Unsigned types are supported too even though they are not a [Number] instance,
 * but unless the input solely exists of unsigned numbers, it will never be returned.
 * Meaning, given a [Number] in the input, the output will always be a [Number].
 *
 * @return The nearest common numeric type between the input types.
 *   If no common type is found, it returns [Number].
 * @see UnifyingNumbers */
internal fun Iterable<KClass<*>>.unifiedNumberClass(): KClass<*> =
    fold(null as KClass<*>?, ::getUnifiedNumberClass) ?: Number::class

/**
 * Converts the elements of the given iterable of numbers into a common numeric type based on complexity.
 * The common numeric type is determined using the provided [commonNumberType] parameter
 * or calculated with [Iterable.unifiedNumberType] from the iterable's elements if not explicitly specified.
 *
 * @param commonNumberType The desired common numeric type to convert the elements to.
 *   This is determined by default using the types of the elements in the iterable.
 * @return A new iterable of numbers where each element is converted to the specified or inferred common number type.
 * @throws IllegalStateException if an element cannot be converted to the common number type.
 * @see UnifyingNumbers
 */
@Suppress("UNCHECKED_CAST")
internal fun Iterable<Number>.convertToUnifiedNumberType(
    commonNumberType: KType = this.types().unifiedNumberType(),
): Iterable<Number> {
    val converter = createConverter(typeOf<Number>(), commonNumberType)!! as (Number) -> Number?
    return map {
        converter(it) ?: error("Can not convert $it to $commonNumberType")
    }
}
