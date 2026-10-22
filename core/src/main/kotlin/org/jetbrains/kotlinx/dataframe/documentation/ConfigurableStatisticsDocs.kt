package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod

/** Shared additions to the existing median/percentile documentation, pending #2137. */
internal interface ConfigurableStatisticsDocs {

    /**
     * Computes the statistic with an explicit [QuantileEstimationMethod].
     * Percentiles use a value in `[0, 100]`; the median is the 50th percentile.
     * Omitting the method uses the existing overloads and their defaults: R8 for primitive numbers,
     * R3 for other self-comparables and all `-By` operations.
     *
     * Nulls are ignored. NaN propagates unless `skipNaN` is true. For `-By`, the first NaN selects its
     * original row or element; skipping NaN preserves the original positions of the remaining values.
     * `-OrNull` returns null for empty input; a scalar operation without `-OrNull` throws
     * [NoSuchElementException]. Empty aggregate cells are null.
     *
     * Selecting methods (R1/R3) preserve the input value type. Interpolating methods (R7/R8) require
     * primitive numbers and produce Double values. When aggregating several columns, the explicit
     * method applies to every selected column: select only numeric columns for interpolation.
     *
     * Existing calls and deprecated property/reference overloads retain their signatures.
     * Use a selector lambda or a column name to configure a method.
     * Compiler-plugin generation of result accessors for the explicit-method aggregation overloads
     * is not yet supported. Access result columns by name or with explicitly typed selectors.
     *
     * @param method The estimation method; `-By` operations accept only selecting methods.
     * @param skipNaN Whether to ignore NaN values.
     * @throws IllegalArgumentException If the percentile is outside `[0, 100]` or interpolation is unsupported.
     */
    @ExcludeFromSources
    typealias Common = Nothing

    /**
     * Null comparison values are ignored. If `skipNaN` is false, the first NaN selects its original
     * element or row. If true, NaN comparison values are excluded before ranking, while original
     * element and row positions are retained. With no remaining comparison values, `-OrNull` returns
     * null; a scalar `-By` throws [NoSuchElementException].
     */
    @ExcludeFromSources
    typealias ByNaN = Nothing

    /**
     * @include [Common]
     *
     * R1 selects rank `ceil(n * p)`; R3 selects the rank nearest `n * p`, rounding ties to the even rank.
     * Ranks are clipped to the sample bounds. The result is an existing observation, including for
     * primitive numbers, exact Long values, strings, dates, and arbitrary-precision comparable numbers.
     * `-By` returns the original element or row rather than the comparison value.
     */
    @ExcludeFromSources
    typealias Selecting = Nothing

    /**
     * @include [Common]
     *
     * The result is Double, including for singleton input. Conversion of large Long values can lose
     * precision. Arbitrary-precision numbers and mixed Number columns cannot be interpolated.
     * @include [InterpolationArithmetic]
     */
    @ExcludeFromSources
    typealias Interpolating = Nothing

    /**
     * Interpolation returns an exact observation at integral positions and preserves identical infinities;
     * other interpolation arithmetic, including opposite infinities, can produce NaN or overflow.
     */
    @ExcludeFromSources
    typealias InterpolationArithmetic = Nothing
}
