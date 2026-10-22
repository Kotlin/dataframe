package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Returns the median of the comparable input:
 * - `null` if empty
 * - `Double` if primitive number
 * - `Double.NaN` if ![skipNaN] and contains NaN
 * - (lower) middle else
 *
 * Based on quantile implementation;
 * uses [QuantileEstimationMethod.R8] for primitive numbers, else [QuantileEstimationMethod.R3].
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

        // TODO kdocs: note about loss of precision for Long
    }

    val p = 0.5

    // TODO make configurable? https://github.com/Kotlin/dataframe/issues/1121
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
        name = "median",
    )
}

/**
 * Primitive Number -> Double?
 * T : Comparable<T> -> T?
 */
internal val medianConversion: CalculateReturnType = { type, isEmpty ->
    when {
        // uses linear interpolation, R8 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isPrimitiveNumber() -> typeOf<Double>()

        // closest rank method, preferring lower middle,
        // R3 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isIntraComparable() -> type

        else -> error("Can not calculate median for type ${renderType(type)}")
    }.withNullability(isEmpty)
}

/**
 * Returns the index of the median in the comparable input:
 * - `-1` if empty or all `null`
 * - index of first NaN if ![skipNaN] and contains NaN
 * - index (lower) middle else
 * NOTE: For primitive numbers the `seq.elementAt(seq.indexOfMedian())` might be different from `seq.medianOrNull()`
 *
 * Based on quantile implementation; uses [QuantileEstimationMethod.R3].
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
    }

    // TODO make configurable? https://github.com/Kotlin/dataframe/issues/1121
    return quantileIndex(
        p = 0.5,
        type = type,
        skipNaN = skipNaN,
        method = QuantileEstimationMethod.R3,
        name = "median",
    )
}
