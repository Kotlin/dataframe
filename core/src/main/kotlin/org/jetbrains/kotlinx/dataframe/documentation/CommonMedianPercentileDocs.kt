package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianBy
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileBy
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC

/**
 * {@comment
 *    Holds all KDoc-snippets that the `median` and `percentile` operations have in common.
 *    Both `MedianDocs` and `PercentileDocs` inherit from this interface, so the snippets can be
 *    included from either of them, like `{@include [MedianDocs.InputValuesSnippet]}`.
 *    The snippets that all summary statistics have in common are inherited from
 *    [CommonStatisticsDocs] and can be included in the same way.
 *
 *    Some snippets mention the statistic by name, using the [STATISTIC] key.
 *    `MedianDocs` and `PercentileDocs` wrap those with their own `Set...StatisticArgs`.
 *
 *    NOTE: this cannot be @ExcludedFromSources because `MedianDocs` and `PercentileDocs` use it as supertype.
 * }
 */
internal interface CommonMedianPercentileDocs : CommonStatisticsDocs {

    /**
     * ## Quantile Estimation Methods
     *
     * Both the [median][DataFrame.median] and the [percentile][DataFrame.percentile] are
     * [quantiles](https://en.wikipedia.org/wiki/Quantile); the median is simply the 50th percentile.
     * Unless a quantile lands exactly on one of the values, it needs to be estimated from the values around it.
     * There are [several common methods](https://en.wikipedia.org/wiki/Quantile#Estimating_quantiles_from_a_sample)
     * to do this. DataFrame follows the definitions of
     * [Hyndman, R. J. & Fan, Y. (1996). Sample Quantiles in Statistical Packages](https://doi.org/10.1080/00031305.1996.10473566)
     * and [Apache Commons Statistics](https://commons.apache.org/proper/commons-statistics/commons-statistics-descriptive/javadocs/api-1.1/org/apache/commons/statistics/descriptive/Quantile.EstimationMethod.html),
     * which call these methods R1 to R9.
     *
     * DataFrame uses:
     * - **R8** for primitive numbers ([Byte], [Short], [Int], [Long], [Float], and [Double]).
     *   It interpolates linearly between the two values around the quantile, so the result is a [Double].
     *   R8 is the method recommended by Hyndman and Fan.
     *   Other libraries, like [Numpy](https://numpy.org/doc/2.1/reference/generated/numpy.quantile.html),
     *   default to R7, so slightly different results are to be expected.
     *   For the median, R8 gives the middle value, or the mean of the two middle values
     *   if the number of values is even.
     * - **R3** for all other self-comparable values (like strings or dates), since those cannot be interpolated,
     *   and for all `-By` operations (like [medianBy][DataFrame.medianBy] and
     *   [percentileBy][DataFrame.percentileBy]), since those select an existing row or element,
     *   even if they compare numbers.
     *   R3 selects the value whose rank (its 1-based position among the sorted values) is the closest to `N * p`,
     *   where `N` is the number of values and `p` is the requested quantile, like `0.5` for the median.
     *   Ties are rounded to the even rank. The result is thus always one of the values itself.
     *
     * @include [DefaultMethodsOnlySnippet]
     *
     * For more information: {@include [DocumentationUrls.Percentile.QuantileEstimationMethods]}
     */
    typealias QuantileEstimationMethods = Nothing

    /** [quantile estimation method][CommonMedianPercentileDocs.QuantileEstimationMethods] */
    @ExcludeFromSources
    typealias QuantileEstimationMethodLink = Nothing

    /**
     * {@comment Note that the quantile estimation methods cannot be chosen (yet). KDoc-snippet.}
     *
     * Currently, only the default quantile estimation methods can be used.
     * In the future, it might become possible to choose a different one, see #1121.
     */
    @ExcludeFromSources
    typealias DefaultMethodsOnlySnippet = Nothing

    /**
     * {@comment Note about which values are supported, and how `null` and `NaN` values are treated.
     *    KDoc-snippet. Only include this in the KDoc of overloads that have a `skipNaN` parameter;
     *    the others use [ComparableInputValuesSnippet].}
     *
     * @include [CommonStatisticsDocs.SelfComparableValuesSnippet]
     *
     * @include [CommonStatisticsDocs.NullAndNaNHandlingSnippet]
     */
    @ExcludeFromSources
    typealias InputValuesSnippet = Nothing

    /**
     * {@comment Note about which values are supported, and how `null` values are treated.
     *    KDoc-snippet for the overloads without a `skipNaN` parameter.}
     *
     * @include [CommonStatisticsDocs.SelfComparableValuesSnippet]
     *
     * @include [CommonStatisticsDocs.NullHandlingSnippet]
     */
    @ExcludeFromSources
    typealias ComparableInputValuesSnippet = Nothing

    /**
     * {@comment Note about which columns the no-argument modes take into account.
     *    KDoc-snippet. Uses the [STATISTIC] key.}
     *
     * All columns whose values are mutually comparable are taken into account;
     * the other columns are simply left out of the result.
     * @include [CommonStatisticsDocs.ColumnGroupsIgnoredSnippet]
     */
    @ExcludeFromSources
    typealias AllComparableColumnsSnippet = Nothing

    /**
     * {@comment Note about big numbers not being supported. KDoc-snippet.}
     *
     * Big numbers ([`BigInteger`][java.math.BigInteger], [`BigDecimal`][java.math.BigDecimal]) are not
     * explicitly supported. They are self-comparable, so some overloads select a value without interpolating it,
     * while others throw an exception at runtime.
     * Don't rely on this; [`convert`][DataFrame.convert] them to a primitive number type first.
     */
    @ExcludeFromSources
    typealias BigNumbersSnippet = Nothing

    /**
     * {@comment Note about the type of the result of the overloads for primitive numbers. KDoc-snippet.}
     *
     * The result is a [Double], interpolated between the values using {@include [QuantileEstimationMethodLink]} R8.
     * Converting [Long] values to [Double] may lose precision for very large values.
     * @include [DefaultMethodsOnlySnippet]
     *
     * @include [BigNumbersSnippet]
     */
    @ExcludeFromSources
    typealias NumberResultSnippet = Nothing

    /**
     * {@comment Note about the type of the result of the overloads for comparable values. KDoc-snippet.}
     *
     * The result is selected from the values using {@include [QuantileEstimationMethodLink]} R3,
     * so it has the same type as the values (minus nullability, if the values were nullable).
     * @include [DefaultMethodsOnlySnippet]
     */
    @ExcludeFromSources
    typealias ComparableResultSnippet = Nothing

    /**
     * {@comment Note about the type of the result of the overloads that handle both primitive numbers
     *    and other comparable values. KDoc-snippet.}
     *
     * For primitive numbers, the result is a [Double], interpolated between the values using
     * {@include [QuantileEstimationMethodLink]} R8.
     * Converting [Long] values to [Double] may lose precision for very large values.
     * For all other self-comparable values, the result is selected from the values using
     * {@include [QuantileEstimationMethodLink]} R3, so it has the same type as those values.
     * @include [DefaultMethodsOnlySnippet]
     */
    @ExcludeFromSources
    typealias ResultTypeSnippet = Nothing

    /**
     * {@comment Note about KT-76683: the lambda overloads for comparable values need help
     *    from the user to be picked. KDoc-snippet.}
     *
     * __Note:__ Due to a limitation in Kotlin's overload resolution
     * ({@include [Issues.OverloadResolutionByLambdaReturnTypeLink]}), calling this overload with a lambda
     * returning non-number comparable values, like strings or dates, requires either explicit type arguments,
     * or passing the lambda inside the parentheses (see the examples below).
     * For a lambda returning primitive numbers, the overload returning a [Double] is picked automatically.
     */
    @ExcludeFromSources
    typealias ExplicitTypeArgumentsSnippet = Nothing

    /**
     * {@comment Note for the [DataRow] overloads for comparable values about KT-76683-like overload resolution.
     *    KDoc-snippet.}
     *
     * __Careful:__ Calling this function with a primitive number type, like `<Int>`, without passing `skipNaN`
     * also resolves to this overload, instead of the one returning a [Double].
     * The interpolated [Double] result is then returned as [T\], which may truncate it or throw a
     * [ClassCastException]. Pass `skipNaN` explicitly (like `skipNaN = false`) to call the overload for numbers.
     */
    @ExcludeFromSources
    typealias RowComparableOverloadResolutionSnippet = Nothing

    /**
     * {@comment Note for the [DataRow] overloads for numbers about overload resolution. KDoc-snippet.}
     *
     * __Careful:__ To call this overload, [skipNaN\] must be passed explicitly (like `skipNaN = false`).
     * Without it, a call like `<Int>` resolves to the overload for comparable values instead,
     * which returns the interpolated result as [T\], possibly truncating it or throwing a [ClassCastException].
     */
    @ExcludeFromSources
    typealias RowNumberOverloadResolutionSnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for non-`-OrNull` overloads.
     *    KDoc-snippet. Uses the [STATISTIC] key.}
     *
     * Throws a [NoSuchElementException] when there is nothing to compute the {@get [STATISTIC] statistic} of,
     * for instance, when the input is empty or contains only `null` values.
     */
    @ExcludeFromSources
    typealias ThrowsOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for `-OrNull` overloads.
     *    KDoc-snippet. Uses the [STATISTIC] key.}
     *
     * Returns `null` when there is nothing to compute the {@get [STATISTIC] statistic} of,
     * for instance, when the input is empty or contains only `null` values.
     */
    @ExcludeFromSources
    typealias NullOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the modes with multiple results.
     *    KDoc-snippet. Uses the [STATISTIC] key.}
     *
     * Result cells for which there is nothing to compute the {@get [STATISTIC] statistic} of
     * (for instance, because the input was empty or contained only `null` values)
     * simply become `null`.
     */
    @ExcludeFromSources
    typealias NullCellOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the Pivot functions.
     *    KDoc-snippet. Uses the [STATISTIC] key.}
     *
     * Result cells for which there exists a group, but there is nothing to compute the
     * {@get [STATISTIC] statistic} of (for instance, because the group was empty or contained only `null` values)
     * simply become `null`.
     *
     * @include [CommonStatisticsDocs.EmptyPivotIntersectionSnippet]
     */
    @ExcludeFromSources
    typealias NullCellOnEmptyPivotSnippet = Nothing

    /**
     * {@comment Note about how the `-By` operations select their result.
     *    KDoc-snippet. Uses the [STATISTIC] key.}
     *
     * Which value lies at the {@get [STATISTIC] statistic} is determined using
     * {@include [QuantileEstimationMethodLink]} R3, so no interpolation takes place, not even for numbers.
     * If [skipNaN\] is `false` and {@include [NaNLink]} values are encountered,
     * the first one with a {@include [NaNLink]} value is selected.
     * @include [DefaultMethodsOnlySnippet]
     */
    @ExcludeFromSources
    typealias BySelectionSnippet = Nothing
}
