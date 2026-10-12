package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.quantileInterpolatingOrNullImpl
import org.jetbrains.kotlinx.dataframe.impl.api.quantileSelectingOrNullImpl
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull

/** Estimators for column quantiles, separated by whether they select an observation or interpolate numbers. */
public sealed interface QuantileMethod {

    /** Methods that select an existing observation and preserve its value type. */
    public enum class Selecting : QuantileMethod {
        /** R1: select the observation at the ceiling of n times p, clipped to the sample bounds. */
        R1,

        /** R3: select the observation nearest n times p, with ties to an even one-based position. */
        R3,
    }

    /** Methods that interpolate primitive numeric observations and return Double. */
    public enum class Interpolating : QuantileMethod {
        /** R7: interpolate at the one-based position 1 + (n - 1) times p. */
        R7,

        /** R8: interpolate at the one-based position 1/3 + (n + 1/3) times p. */
        R8,
    }

    /** Shortcuts whose declared types select the matching quantile overload. */
    public companion object {
        /** Shortcut for [Selecting.R1]. */
        public val R1: Selecting = Selecting.R1

        /** Shortcut for [Selecting.R3]. */
        public val R3: Selecting = Selecting.R3

        /** Shortcut for [Interpolating.R7]. */
        public val R7: Interpolating = Interpolating.R7

        /** Shortcut for [Interpolating.R8]. */
        public val R8: Interpolating = Interpolating.R8
    }
}

// Adapted from the quantile/column-statistics documentation in math/quantile.kt and api/cumSum.kt.

/**
 * Computes a column quantile using an explicitly selected [QuantileMethod].
 * [p\] is a probability in `[0, 1]`, unlike [percentile]'s `[0, 100]` scale.
 * Existing percentile defaults are unchanged: R8 for primitive numbers, R3 for other self-comparables.
 *
 * Null cells are ignored. NaN propagates unless [skipNaN\] is `true`.
 * `quantileOrNull` returns null if no observations remain; `quantile` throws [NoSuchElementException].
 * Invalid probabilities are rejected even for empty input.
 *
 * Selecting methods work on self-comparables, including strings, dates, and exact decimals.
 * They preserve the original value type, including singleton primitive columns and Float NaN.
 * Interpolating methods support only primitive numeric columns and return Double. Conversion can lose
 * precision for large Long values; arbitrary-precision and mixed Number columns are not interpolated.
 *
 * Comparisons use Kotlin Comparable ordering, including signed zeros. Input values are not modified.
 * @include [InterpolatedQuantileInfinityDocs]
 *
 * @param [p\] The probability, finite and in `[0, 1]`.
 * @param [method\] A selecting or interpolating estimator; there is no implicit estimator.
 * @param [skipNaN\] Whether to ignore NaN observations (default: `false`, matching percentile).
 * @throws IllegalArgumentException If p is invalid or the column is unsupported for interpolation.
 * @see [percentile]
 * @see {@include [DocumentationUrls.QuantileOnColumn]}
 */
@ExcludeFromSources
private interface QuantileDocs

/**
 * Interpolation returns an exact observation directly when the estimated position is integral,
 * and preserves identical infinite observations. Other interpolation arithmetic can produce NaN,
 * including combining opposite infinities, or lose precision through Double conversion.
 * Returning an exact observation also preserves its signed zero.
 */
@ExcludeFromSources
internal interface InterpolatedQuantileInfinityDocs

/** @include [QuantileDocs] */
public fun <T : Comparable<T & Any>?> DataColumn<T>.quantile(
    p: Double,
    method: QuantileMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
): T & Any = quantileOrNull(p, method, skipNaN).suggestIfNull("quantile")

/** @include [QuantileDocs] */
public fun <T : Comparable<T & Any>?> DataColumn<T>.quantileOrNull(
    p: Double,
    method: QuantileMethod.Selecting,
    skipNaN: Boolean = skipNaNDefault,
): T? = quantileSelectingOrNullImpl(p, method, skipNaN)

/** @include [QuantileDocs] */
public fun <T> DataColumn<T>.quantile(
    p: Double,
    method: QuantileMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T & Any>?, T : Number? = quantileOrNull(p, method, skipNaN).suggestIfNull("quantile")

/** @include [QuantileDocs] */
public fun <T> DataColumn<T>.quantileOrNull(
    p: Double,
    method: QuantileMethod.Interpolating,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T & Any>?, T : Number? = quantileInterpolatingOrNullImpl(p, method, skipNaN)
