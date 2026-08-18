package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupByDocs
import org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy
import org.jetbrains.kotlinx.dataframe.api.ReducedPivot
import org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.with

/**
 * {@comment
 *    Holds all KDoc-snippets that the `min` and `max` operations have in common.
 *    Both `MinDocs` and `MaxDocs` inherit from this interface, so the snippets can be
 *    included from either of them, like `{@include [MaxDocs.SkipNaNParam]}`.
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
     * Only self-comparable values are supported: values of a type `T : Comparable<T>`
     * that are mutually comparable (like strings, primitive numbers, or dates).
     * This includes all primitive number types, but no mix of different number types.
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
     * {@comment Note about [ReducedGroupBy] being an intermediate step. KDoc-snippet.}
     *
     * This operation does not produce a result right away.
     * Instead, it returns a [ReducedGroupBy] — an intermediate step which can be finished with
     * [concat][ReducedGroupBy.concat] (to get a [DataFrame] with the selected rows),
     * [values][ReducedGroupBy.values], or [into][ReducedGroupBy.into].
     *
     * See [GroupBy reducing][GroupByDocs.Reducing] for more details.
     */
    @ExcludeFromSources
    typealias ReducedGroupBySnippet = Nothing

    /**
     * {@comment Note about [ReducedPivot] being an intermediate step. KDoc-snippet.}
     *
     * This operation does not produce a result right away.
     * Instead, it returns a [ReducedPivot] — an intermediate step which can be finished with
     * [values][ReducedPivot.values] or [with][ReducedPivot.with].
     */
    @ExcludeFromSources
    typealias ReducedPivotSnippet = Nothing

    /**
     * {@comment Note about [ReducedPivotGroupBy] being an intermediate step. KDoc-snippet.}
     *
     * This operation does not produce a result right away.
     * Instead, it returns a [ReducedPivotGroupBy] — an intermediate step which can be finished with
     * [values][ReducedPivotGroupBy.values] or [with][ReducedPivotGroupBy.with].
     */
    @ExcludeFromSources
    typealias ReducedPivotGroupBySnippet = Nothing
}
