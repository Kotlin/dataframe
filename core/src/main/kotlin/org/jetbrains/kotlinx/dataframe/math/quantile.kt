package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.impl.canBeNaN
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Returns the p-quantile: the k'th q-quantile, where p = k/q.
 *
 * When [method] is a [QuantileEstimationMethod.Selecting] method,
 * [this] can be a sequence with any self-comparable type.
 * The returned value will be selected from the sequence.
 *
 * Otherwise, when [method] is a [QuantileEstimationMethod.Interpolating] method,
 * [this] can only be a sequence with primitive number types.
 * The returned value will be [Double].
 *
 * Nulls are not allowed. If NaN is among the values, it will be returned.
 *
 * @see QuantileEstimationMethod
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
    }

    // propagate NaN to return if they are not to be skipped
    if (type.canBeNaN && !skipNaN && any { it.isNaN }) {
        // ensure that using a selecting quantile estimation method always returns the same type as the input
        if (type == typeOf<Float>() && method is QuantileEstimationMethod.Selecting) return Float.NaN

        return Double.NaN
    }

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
        is QuantileEstimationMethod.Selecting ->
            method.quantile<T>(p, list as List<T>)

        is QuantileEstimationMethod.Interpolating -> {
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
 * Returns the index `i` of the [p]-quantile: the k'th q-quantile, where p = k/q.
 *
 * The returned index `i` is either exactly or approaching the index of the quantile in the sequence [this]
 * (when it's sorted and NaN's removed).
 * Returns -1.0 if the sequence [this] is empty.
 * Returns [Double.NaN] if `!`[skipNaN] and a NaN is encountered.
 */
internal fun <T : Comparable<T>> Sequence<Any?>.quantileIndexEstimation(
    p: Double,
    type: KType,
    skipNaN: Boolean,
    method: QuantileEstimationMethod<T, *>,
    name: String = "quantile",
): Double {
    val nonNullType = type.withNullability(false)

    when {
        p !in 0.0..1.0 -> error("Quantile must be in range [0, 1]")

        type.isMarkedNullable ->
            error("Encountered nullable type ${renderType(type)} in $name function. This should not occur.")

        // this means the sequence is empty
        type == nothingType -> return -1.0

        !nonNullType.isIntraComparable() ->
            error(
                "Unable to compute the $name for ${
                    renderType(type)
                }. Only primitive numbers or self-comparables are supported.",
            )

        method is QuantileEstimationMethod.Interpolating && !nonNullType.isPrimitiveNumber() ->
            error(
                "Cannot calculate the $name for type ${renderType(type)} with estimation method $method." +
                    "For piecewise linear methods, only primitive numbers are supported",
            )
    }

    // propagate NaN to return if they are not to be skipped
    if (nonNullType.canBeNaN && !skipNaN) {
        if (any { it.isNaN }) return Double.NaN
    }
    val list = when {
        nonNullType.canBeNaN -> this.filterNot { it.isNaN }
        else -> this
    }.toList()

    val size = list.size
    if (size == 0) return -1.0
    if (size == 1) return 0.0

    return method.indexOfQuantile(p, size).toDouble()
}

/**
 * Inspired by Hyndman and Fan (1996) Sample Quantiles in Statistical Packages. The American Statistician, 50, 361-365.
 * DOI:10.1080/00031305.1996.10473566
 *
 * and https://commons.apache.org/proper/commons-statistics/commons-statistics-descriptive/javadocs/api-1.1/org/apache/commons/statistics/descriptive/Quantile.EstimationMethod.html
 *
 * They are split in [Selecting] (where [oneBasedIndexOfQuantile] gives an exact index of the quantile in a sorted list of type [Int])
 * and [Interpolating] (where [oneBasedIndexOfQuantile] gives an approximation for that index of type [Double]).
 * For the [Selecting], the [Value] type can thus be any self-comparable type, but for [Interpolating],
 * [Value] can only be of type [Double], because it needs to perform calculations on the values.
 *
 * TODO https://github.com/Kotlin/dataframe/issues/1121
 *   - add R2, R4, R5, R6, R9
 *   - make public if configurable for percentile function
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

    sealed interface Selecting : QuantileEstimationMethod<Comparable<Any>, Int> {

        /** Inverse of the empirical distribution function. */
        data object R1 : Selecting {
            override fun oneBasedIndexOfQuantile(p: Double, count: Int): Int =
                ceil(p * count).toInt()
                    .coerceIn(1..count)

            @Suppress("UNCHECKED_CAST")
            override fun quantile(p: Double, values: List<Comparable<Any>>): Comparable<Any> {
                val h = indexOfQuantile(p, values.size).toInt()
                return values.quickSelect(h)
            }
        }

        /** The observation closest to `count * p` */
        data object R3 : Selecting {
            // following apache commons + paper instead of wikipedia
            override fun oneBasedIndexOfQuantile(p: Double, count: Int): Int =
                round(count * p).toInt()
                    .coerceIn(1..count)

            @Suppress("UNCHECKED_CAST")
            override fun quantile(p: Double, values: List<Comparable<Any>>): Comparable<Any> {
                val h = indexOfQuantile(p, values.size).toInt()
                return values.quickSelect(h)
            }
        }
    }

    // TODO add R2, R4, R5, R6, R9 https://github.com/Kotlin/dataframe/issues/1121
    sealed interface Interpolating : QuantileEstimationMethod<Double, Double> {

        /** Linear interpolation of the modes for the order statistics for the uniform distribution on [0, 1]. */
        data object R7 : Interpolating, PieceWiseLinear {
            override fun oneBasedIndexOfQuantile(p: Double, count: Int): Double =
                ((count - 1.0) * p + 1.0)
                    .coerceIn(1.0..count.toDouble())
        }

        /** Linear interpolation of the approximate medians for order statistics. Recommended by H & F. */
        data object R8 : Interpolating, PieceWiseLinear {
            override fun oneBasedIndexOfQuantile(p: Double, count: Int): Double =
                ((count + 1.0 / 3.0) * p + 1.0 / 3.0)
                    .coerceIn(1.0..count.toDouble())
        }

        private interface PieceWiseLinear : Interpolating {
            override fun quantile(p: Double, values: List<Double>): Double {
                val h = oneBasedIndexOfQuantile(p, values.size)
                return values.quickSelect(floor(h).toInt() - 1) + (h - floor(h)) * (
                    values.quickSelect(ceil(h).toInt() - 1) -
                        values.quickSelect(floor(h).toInt() - 1)
                )
            }
        }
    }

    // shortcuts to the various estimation methods
    // TODO add R2, R4, R5, R6, R9 https://github.com/Kotlin/dataframe/issues/1121
    companion object {
        val R1 = Selecting.R1
        val R3 = Selecting.R3
        val R7 = Interpolating.R7
        val R8 = Interpolating.R8
    }
}

// overload to get the right comparable type
@Suppress("UNCHECKED_CAST")
internal fun <T : Comparable<T>> QuantileEstimationMethod.Selecting.quantile(p: Double, values: List<T>): T =
    quantile(p, values as List<Comparable<Any>>) as T

@Suppress("UNCHECKED_CAST")
internal fun <T : Comparable<T>> QuantileEstimationMethod.Selecting.cast(): QuantileEstimationMethod<T, Int> =
    this as QuantileEstimationMethod<T, Int>

// corrects oneBasedIndexOfQuantile to zero-based index
@Suppress("UNCHECKED_CAST")
internal fun <IndexType : Number> QuantileEstimationMethod<*, IndexType>.indexOfQuantile(
    p: Double,
    count: Int,
): IndexType {
    val oneBased = oneBasedIndexOfQuantile(p = p, count = count)
    return when (this) {
        is QuantileEstimationMethod.Interpolating -> oneBased as Double - 1.0
        is QuantileEstimationMethod.Selecting -> oneBased as Int - 1
    } as IndexType
}

/**
 * Select the k't "smallest" element from list [this]
 */
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
