package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.math.round
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Uses [QuantileEstimationMethod.R8] for primitive numbers, else [QuantileEstimationMethod.R3]
 */
internal fun <T : Comparable<T>> Sequence<T>.percentileOrNull(percentile: Double, type: KType, skipNaN: Boolean): Any? {
    when {
        percentile !in 0.0..100.0 -> error("Percentile must be in range [0, 100]")

        type.isMarkedNullable ->
            error("Encountered nullable type ${renderType(type)} in percentile function. This should not occur.")

        // this means the sequence is empty
        type == nothingType -> return null

        !type.isIntraComparable() ->
            error(
                "Unable to compute the percentile for ${
                    renderType(type)
                }. Only primitive numbers or self-comparables are supported.",
            )

        // TODO kdocs: note about loss of precision for Long
    }

    // percentile of 25.0 means the 25th 100-quantile, so 25 / 100 = 0.25
    val p = percentile / 100.0

    // TODO make configurable https://github.com/Kotlin/dataframe/issues/1121
    val (values, method) =
        when {
            type.isPrimitiveNumber() ->
                this.map { (it as Number).toDouble() } to QuantileEstimationMethod.Interpolating.R8

            else ->
                this to QuantileEstimationMethod.Selecting.R3
        }

    return values.quantileOrNull(
        p = p,
        type = type,
        skipNaN = skipNaN,
        method = method,
        name = "percentile",
    )
}

internal val percentileConversion: CalculateReturnType = { type, isEmpty ->
    when {
        // uses linear interpolation, R8 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isPrimitiveNumber() -> typeOf<Double>()

        // closest rank method, preferring lower middle,
        // R3 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isIntraComparable() -> type

        else -> error("Can not calculate percentile for type ${renderType(type)}")
    }.withNullability(isEmpty)
}

/**
 * Returns the index of the [percentile] in the unsorted sequence [this].
 * If `!`[skipNaN] and the sequence [this] contains NaN, the index of the first NaN will be returned.
 * Returns -1 if the sequence is empty.
 */
internal fun <T : Comparable<T & Any>?> Sequence<T>.indexOfPercentile(
    percentile: Double,
    type: KType,
    skipNaN: Boolean,
): Int {
    val nonNullType = type.withNullability(false)
    when {
        percentile !in 0.0..100.0 -> error("Percentile must be in range [0, 100]")

        // this means the sequence is empty
        nonNullType == nothingType -> return -1

        !nonNullType.isIntraComparable() ->
            error(
                "Unable to compute the percentile for ${
                    renderType(type)
                }. Only primitive numbers or self-comparables are supported.",
            )
    }

    val indexedSequence = this.mapIndexedNotNull { i, it ->
        if (it == null) {
            null
        } else {
            IndexedComparable(i, it)
        }
    }

    // TODO make configurable https://github.com/Kotlin/dataframe/issues/1121
    val method = QuantileEstimationMethod.R3

    // percentile of 25.0 means the 25th 100-quantile, so 25 / 100 = 0.25
    val p = percentile / 100.0

    // get the index where the percentile can be found in the sorted sequence
    val indexEstimation = indexedSequence.quantileIndexEstimation(
        p = p,
        type = typeOf<IndexedComparable<Nothing>>(),
        skipNaN = skipNaN,
        method = method,
        name = "percentile",
    )
    if (indexEstimation.isNaN()) return this.indexOfFirst { it.isNaN }
    if (indexEstimation < 0.0) return -1
    require(indexEstimation == round(indexEstimation)) {
        "percentile expected a whole number index from quantileIndexEstimation but was $indexEstimation"
    }

    val percentileResult = indexedSequence.toList().quickSelect(k = indexEstimation.toInt())

    // return the original unsorted index of the found result
    return percentileResult.index
}
