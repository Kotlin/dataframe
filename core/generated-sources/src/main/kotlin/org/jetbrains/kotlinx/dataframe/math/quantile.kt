package org.jetbrains.kotlinx.dataframe.math

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.impl.canBeNaN
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod.InterpolatingEstimation
import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod.SelectingEstimation
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

private val logger = KotlinLogging.logger { }

/**
 * p-quantile: the k'th q-quantile, where p = k/q.
 *
 */
internal fun <T : Comparable<T>> Sequence<Any>.quantileOrNull(
    p: Double,
    type: KType,
    skipNaN: Boolean,
    method: QuantileEstimationMethod<T, *>,
    name: String = "quantile",
): Any? {
    when {
        p !in 0.0..1.0 -> error("Quantile must be in range [0, 1]")

        type.isMarkedNullable ->
            error("Encountered nullable type ${renderType(type)} in $name function. This should not occur.")

        // this means the sequence is empty
        type == nothingType -> return null

        !type.isIntraComparable() ->
            error(
                "Unable to compute the $name for ${
                    renderType(type)
                }. Only primitive numbers or self-comparables are supported.",
            )

        type == typeOf<BigDecimal>() || type == typeOf<BigInteger>() ->
            throw IllegalArgumentException(
                "Cannot calculate the $name for big numbers in DataFrame. Only primitive numbers are supported.",
            )

        type == typeOf<Long>() ->
            logger.warn { "Converting Longs to Doubles to calculate the $name, loss of precision may occur." }
    }

    // propagate NaN to return if they are not to be skipped
    if (type.canBeNaN && !skipNaN && any { it.isNaN }) return Double.NaN

    val list = when {
        type.canBeNaN -> filter { !it.isNaN }
        else -> this
    }.toList()

    val size = list.size
    if (size == 0) return null

    if (size == 1) {
        val single = list.single()
        return if (type.isPrimitiveNumber()) (single as Number).toDouble() else single
    }

    return when (method) {
        is SelectingEstimation ->
            method.quantile<T>(p, list as List<T>)

        is InterpolatingEstimation -> {
            require(type.isPrimitiveNumber()) {
                "Cannot calculate the $name for non-primitive numbers with estimation method $method."
            }
            @Suppress("UNCHECKED_CAST")
            val convertedList =
                if (type == typeOf<Double>()) {
                    list as List<Double>
                } else {
                    list.map { (it as Number).toDouble() }
                }

            method.quantile(p, convertedList)
        }
    }
}

/**
 * p-quantile: the k'th q-quantile, where p = k/q.
 *
 */
internal fun <T : Comparable<T & Any>?, Index : Number> Sequence<Any?>.indexOfQuantile(
    p: Double,
    type: KType,
    skipNaN: Boolean,
    method: QuantileEstimationMethod<T & Any, Index>,
    name: String = "quantile",
): Index {
    val nonNullType = type.withNullability(false)
    when {
        p !in 0.0..1.0 -> error("Quantile must be in range [0, 1]")

        !nonNullType.isIntraComparable() ->
            error(
                "Unable to compute the $name for ${
                    renderType(type)
                }. Only primitive numbers or self-comparables are supported.",
            )

        method is InterpolatingEstimation && !nonNullType.isPrimitiveNumber() ->
            error(
                "Cannot calculate the $name for type ${renderType(type)} with estimation method $method." +
                    "For piecewise linear methods, only primitive numbers are supported",
            )
    }

    @Suppress("UNCHECKED_CAST")
    fun Number.toIndex(): Index =
        when (method) {
            is SelectingEstimation -> this.toInt()
            is InterpolatingEstimation -> this.toDouble()
        } as Index

    // propagate NaN to return if they are not to be skipped
    if (nonNullType.canBeNaN && !skipNaN) {
        for ((i, it) in this.withIndex()) {
            if (it.isNaN) return i.toIndex()
        }
    }

    val indexedSequence = this.mapIndexedNotNull { i, it ->
        if (it == null) {
            null
        } else {
            IndexedComparable(i, it as Comparable<Any>)
        }
    }
    val list = when {
        nonNullType.canBeNaN -> indexedSequence.filterNot { it.value.isNaN }
        else -> indexedSequence
    }.toList()

    val size = list.size
    if (size == 0) return (-1).toIndex()
    if (size == 1) return 0.toIndex()

    return method.indexOfQuantile(p, size)
}

/**
 * Inspired by Hyndman and Fan (1996) Sample Quantiles in Statistical Packages. The American Statistician, 50, 361-365.
 * DOI:10.1080/00031305.1996.10473566
 *
 * and https://commons.apache.org/proper/commons-statistics/commons-statistics-descriptive/javadocs/api-1.1/org/apache/commons/statistics/descriptive/Quantile.EstimationMethod.html
 *
 * They are split in [SelectingEstimation] (where [oneBasedIndexOfQuantile] gives an exact index of the quantile in a sorted list of type [Int])
 * and [InterpolatingEstimation] (where [oneBasedIndexOfQuantile] gives an approximation for that index of type [Double]).
 * For the [SelectingEstimation], the returned quantile is thus todo
 */
internal sealed interface QuantileEstimationMethod<Value : Comparable<Value>, Index : Number> {

    /**
     * Gives the (1-based) index of the [p]-quantile for a distribution of size [count].
     * If the result `h` is a whole number, the `h`'th smallest of the [count] values is the quantile estimate.
     * If not, `h` is an estimation of the index of the p-quantile. Rounding or interpolation needs to occur to get
     * the actual quantile estimate.
     */
    fun oneBasedIndexOfQuantile(p: Double, count: Int): Index

    fun quantile(p: Double, values: List<Value>): Value

    interface SelectingEstimation : QuantileEstimationMethod<Comparable<Any>, Int>

    interface InterpolatingEstimation : QuantileEstimationMethod<Double, Double> {

        override fun quantile(p: Double, values: List<Double>): Double {
            val h = oneBasedIndexOfQuantile(p, values.size)
            return values.quickSelect((floor(h).toInt() - 1).coerceIn(0..<values.size)) +
                (h - floor(h)) * (
                    values.quickSelect((ceil(h).toInt() - 1).coerceIn(0..<values.size)) -
                        values.quickSelect((floor(h).toInt() - 1).coerceIn(0..<values.size))
                )
        }
    }

    /** Inverse of the empirical distribution function. */
    data object R1 : SelectingEstimation {
        override fun oneBasedIndexOfQuantile(p: Double, count: Int): Int = ceil(p * count).toInt()

        @Suppress("UNCHECKED_CAST")
        override fun quantile(p: Double, values: List<Comparable<Any>>): Comparable<Any> {
            val h = indexOfQuantile(p, values.size).toInt()
            return values.quickSelect(h.coerceIn(0..<values.size))
        }
    }

    /** The observation closest to `count * p` */
    data object R3 : SelectingEstimation {
        // following apache commons + paper instead of wikipedia
        override fun oneBasedIndexOfQuantile(p: Double, count: Int): Int = round(count * p).toInt()

        @Suppress("UNCHECKED_CAST")
        override fun quantile(p: Double, values: List<Comparable<Any>>): Comparable<Any> {
            val h = indexOfQuantile(p, values.size).toInt()
            return values.quickSelect(h.coerceIn(0..<values.size))
        }
    }

    /** Linear interpolation of the modes for the order statistics for the uniform distribution on [0, 1]. */
    data object R7 : InterpolatingEstimation {
        override fun oneBasedIndexOfQuantile(p: Double, count: Int): Double = (count - 1.0) * p + 1.0
    }

    /** Linear interpolation of the approximate medians for order statistics. */
    data object R8 : InterpolatingEstimation {
        override fun oneBasedIndexOfQuantile(p: Double, count: Int): Double = (count + 1.0 / 3.0) * p + 1.0 / 3.0
    }
}

// overload to get the right comparable type
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "UNCHECKED_CAST")
internal fun <T : Comparable<T>> SelectingEstimation.quantile(p: Double, values: List<T>): T =
    quantile(p, values as List<Comparable<Any>>) as T

// corrects oneBasedIndexOfQuantile to zero-based index
@Suppress("UNCHECKED_CAST")
internal fun <IndexType : Number> QuantileEstimationMethod<*, IndexType>.indexOfQuantile(
    p: Double,
    count: Int,
): IndexType {
    val oneBased = oneBasedIndexOfQuantile(p = p, count = count)
    return when (this) {
        is InterpolatingEstimation -> oneBased as Double - 1.0
        is SelectingEstimation -> oneBased as Int - 1
    } as IndexType
}

@PublishedApi
internal fun <T : Comparable<T>> List<T>.quickSelect(k: Int): T {
    if (k < 0 || k >= size) throw IndexOutOfBoundsException("k = $k, size = $size")

    var list = this
    var temp = mutableListOf<T>()
    var less = mutableListOf<T>()
    var k = k
    var greater = mutableListOf<T>()
    while (list.size > 1) {
        var equal = 0
        val x = list.random()
        greater.clear()
        less.clear()
        for (v in list) {
            val comp = v.compareTo(x)
            when {
                comp < 0 -> less.add(v)
                comp > 0 -> greater.add(v)
                else -> equal++
            }
        }
        when {
            k < less.size -> {
                list = less
                less = temp
                temp = list
            }

            k < less.size + equal -> {
                return x
            }

            else -> {
                list = greater
                greater = temp
                temp = list
                k -= less.size + equal
            }
        }
    }
    return list[0]
}

internal data class IndexedComparable<T : Comparable<T>>(val index: Int, val value: T) :
    Comparable<IndexedComparable<T>> {
    override fun compareTo(other: IndexedComparable<T>): Int = value.compareTo(other.value)
}
