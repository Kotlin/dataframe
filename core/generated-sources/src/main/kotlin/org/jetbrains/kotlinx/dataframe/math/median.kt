package org.jetbrains.kotlinx.dataframe.math

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.canBeNaN
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

private val logger = KotlinLogging.logger { }

/**
 * Returns the median of the comparable input:
 * - `null` if empty
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

        // this means the sequence is empty
        type == nothingType -> return null

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

    val isOdd = size % 2 != 0

    val middleIndex = (size - 1) / 2
    val lower = list.quickSelect(middleIndex)
    val upper = list.quickSelect(middleIndex + 1)

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
 * Primitive Number -> Double?
 * T : Comparable<T> -> T?
 */
internal val medianConversion: CalculateReturnType = { type, isEmpty ->
    when {
        // uses linear interpolation, number 7 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isPrimitiveNumber() -> typeOf<Double>()

        // closest rank method, preferring lower middle,
        // number 3 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isIntraComparable() -> type

        else -> error("Can not calculate median for type ${renderType(type)}")
    }.withNullability(isEmpty)
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
        // this means the sequence is empty
        nonNullType == nothingType -> return -1

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
    if (size == 1) return 0

    val middleIndex = (size - 1) / 2
    val lower = list.quickSelect(middleIndex)

    return lower.index
}
