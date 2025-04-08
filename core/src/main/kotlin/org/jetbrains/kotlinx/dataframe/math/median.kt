package org.jetbrains.kotlinx.dataframe.math

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.canBeNaN
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.math.quickSelect
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

private val logger = KotlinLogging.logger { }

// TODO median always returns the same type, but this can be confusing for iterables of even length
// TODO (e.g. median of [1, 2] should be 1.5, but the type is Int, so it returns 1), Issue #558

/**
 * Returns the median of the comparable input:
 * - `null` if empty and primitive number
 * - `Double.NaN` if empty and primitive number
 * - `Double` if primitive number
 * - `Double.NaN` if ![skipNaN] and contains NaN
 * - (lower) middle else
 *
 * TODO migrate back to percentile when it's flexible enough
 */
@PublishedApi
internal fun <T : Comparable<T>> Sequence<T>.medianOrNull(type: KType, skipNaN: Boolean): Any? {
    when {
        type.isMarkedNullable ->
            error("Encountered nullable type ${renderType(type)} in median function. This should not occur.")

        !type.isIntraComparable() ->
            error(
                "Unable to compute the median for ${
                    renderType(type)
                }. Only primitive numbers or self-comparables are supported.",
            )

        type == typeOf<BigDecimal>() || type == typeOf<BigInteger>() ->
            throw IllegalArgumentException(
                "Cannot calculate the median for big numbers in DataFrame. Only primitive numbers are supported.",
            )

        type == typeOf<Long>() ->
            logger.warn { "Converting Longs to Doubles to calculate the median, loss of precision may occur." }

        // this means the sequence is empty
        type == nothingType -> return null
    }

    // propagate NaN to return if they are not to be skipped
    if (type.canBeNaN && !skipNaN && any { it.isNaN }) return Double.NaN

    val list = when {
        type.canBeNaN -> filter { !it.isNaN }
        else -> this
    }.toList()

    val size = list.size
    if (size == 0) return if (type.isPrimitiveNumber()) Double.NaN else null

    val isOdd = size % 2 != 0

    val middleIndex = (size - 1) / 2
    val lower = list.quickSelect(middleIndex)
    val upper = list.quickSelect(middleIndex + 1)

    // check for quickSelect
    if (isOdd && lower.compareTo(upper) != 0) {
        error("lower and upper median are not equal while list-size is odd. This should not happen.")
    }

    return when {
        isOdd && type.isPrimitiveNumber() -> (lower as Number).toDouble()
        isOdd -> lower
        type == typeOf<Double>() -> (lower as Double + upper as Double) / 2.0
        type == typeOf<Float>() -> ((lower as Float).toDouble() + (upper as Float).toDouble()) / 2.0
        type == typeOf<Int>() -> ((lower as Int).toDouble() + (upper as Int).toDouble()) / 2.0
        type == typeOf<Short>() -> ((lower as Short).toDouble() + (upper as Short).toDouble()) / 2.0
        type == typeOf<Byte>() -> ((lower as Byte).toDouble() + (upper as Byte).toDouble()) / 2.0
        type == typeOf<Long>() -> ((lower as Long).toDouble() + (upper as Long).toDouble()) / 2.0
        else -> lower
    }
}

/**
 * Primitive Number -> Double
 * T : Comparable<T> -> T?
 */
internal val medianConversion: CalculateReturnType = { type, isEmpty ->
    when {
        // uses linear interpolation, number 7 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isPrimitiveNumber() -> typeOf<Double>()

        // closest rank method, preferring lower middle,
        // number 3 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isIntraComparable() -> type.withNullability(isEmpty)

        else -> error("Can not calculate median for type ${renderType(type)}")
    }
}

/**
 * Returns the index of the median of the comparable input:
 * - `-1` if empty or all `null`
 * - index of first NaN if ![skipNaN] and contains NaN
 * - index (lower) middle else
 * NOTE: For primitive numbers the `seq.elementAt(seq.indexOfMedian())` might be different from `seq.medianOrNull()`
 *
 * TODO migrate back to percentile when it's flexible enough
 */
internal fun <T : Comparable<T & Any>?> Sequence<T>.indexOfMedian(type: KType, skipNaN: Boolean): Int {
    val nonNullType = type.withNullability(false)
    when {
        !nonNullType.isIntraComparable() ->
            error(
                "Unable to compute the median for ${
                    renderType(type)
                }. Only primitive numbers or self-comparables are supported.",
            )

        nonNullType == typeOf<BigDecimal>() || nonNullType == typeOf<BigInteger>() ->
            throw IllegalArgumentException(
                "Cannot calculate the median for big numbers in DataFrame. Only primitive numbers are supported.",
            )

        // this means the sequence is empty
        nonNullType == nothingType -> return -1
    }

    // propagate NaN to return if they are not to be skipped
    if (nonNullType.canBeNaN && !skipNaN) {
        for ((i, it) in this.withIndex()) {
            if (it.isNaN) return i
        }
    }

    val indexedSequence = this.mapIndexedNotNull { i, it ->
        if (it == null) {
            null
        } else {
            IndexedComparable(i, it)
        }
    }
    val list = when {
        nonNullType.canBeNaN -> indexedSequence.filterNot { it.value.isNaN }
        else -> indexedSequence
    }.toList()

    val size = list.size
    if (size == 0) return -1

    val isOdd = size % 2 != 0

    val middleIndex = (size - 1) / 2
    val lower = list.quickSelect(middleIndex)
    val upper = list.quickSelect(middleIndex + 1)

    // check for quickSelect
    if (isOdd && lower.compareTo(upper) != 0) {
        error("lower and upper median are not equal while list-size is odd. This should not happen.")
    }

    return lower.index
}

private data class IndexedComparable<T : Comparable<T>>(val index: Int, val value: T) :
    Comparable<IndexedComparable<T>> {
    override fun compareTo(other: IndexedComparable<T>): Int = value.compareTo(other.value)
}
