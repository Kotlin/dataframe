package org.jetbrains.kotlinx.dataframe.documentation

/**
 * {@comment
 *    Holds all KDoc-snippets that the `min` and `max` operations have in common.
 *    Both `MinDocs` and `MaxDocs` inherit from this interface, so the snippets can be
 *    included from either of them, like `{@include [MaxDocs.SkipNanParam]}`.
 *    The snippets that all summary statistics have in common are inherited from
 *    [CommonStatisticsDocs] and can be included in the same way.
 *
 *    NOTE: this cannot be @ExcludedFromSources because `MinDocs` and `MaxDocs` use it as supertype.
 * }
 */
internal interface CommonMinMaxDocs : CommonStatisticsDocs {

    /**
     * {@comment Note about the self-comparability requirement and how `null` and `NaN` values
     *    are treated. KDoc-snippet.}
     *
     * @include [CommonStatisticsDocs.SelfComparableValuesSnippet]
     *
     * {@include [CommonStatisticsDocs.NullAndNaNHandlingSnippet]}
     */
    @ExcludeFromSources
    typealias InputValuesSnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for non-`-OrNull` overloads. KDoc-snippet.}
     *
     * Throws a [NoSuchElementException] when there is nothing left to compare,
     * for instance when the input is empty or contains only `null`
     * (or, if [skipNaN\] is `true`, only `null` and {@include [NaNLink]}) values.
     */
    @ExcludeFromSources
    typealias ThrowsOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for `-OrNull` overloads. KDoc-snippet.}
     *
     * Returns `null` when there is nothing left to compare,
     * for instance when the input is empty or contains only `null`
     * (or, if [skipNaN\] is `true`, only `null` and {@include [NaNLink]}) values.
     */
    @ExcludeFromSources
    typealias NullOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the modes with multiple results.}
     *
     * Result cells for which there is nothing left to compare
     * (for instance, because the input was empty or contained only `null` values)
     * simply become `null`.
     *
     * For more information about the resulting types:
     * {@include [DocumentationUrls.MinMax.TypeConversion]}
     */
    @ExcludeFromSources
    typealias NullCellOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the Pivot functions. KDoc-snippet.}
     *
     * Result cells for which there exists a group, but there is nothing left to compare
     * (for instance, because the group was empty or contained only `null` values)
     * simply become `null`.
     *
     * For more information about the resulting types:
     * {@include [DocumentationUrls.MinMax.TypeConversion]}
     *
     * @include [CommonStatisticsDocs.EmptyPivotIntersectionSnippet]
     */
    @ExcludeFromSources
    typealias NullCellOnEmptyPivotSnippet = Nothing

    /**
     * {@comment Note about the type of the result for the modes with a single result. KDoc-snippet.}
     *
     * The result has the same type as the input values (minus nullability, if the input values were nullable).
     *
     * For more information about the resulting types:
     * {@include [DocumentationUrls.MinMax.TypeConversion]}
     */
    @ExcludeFromSources
    typealias ResultTypeSnippet = Nothing
}
