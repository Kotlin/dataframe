package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl
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
 *
 *    NOTE: this cannot be @ExcludedFromSources because `MinDocs` and `MaxDocs` use it as supertype.
 * }
 */
internal interface CommonMinMaxDocs {

    /**
     * {@comment Note about the self-comparability requirement and how `null` and `NaN` values
     *    are treated. KDoc-snippet.}
     *
     * Only self-comparable values are supported: values of a type `T : Comparable<T>`
     * that are mutually comparable (like strings, primitive numbers, or dates).
     * This includes all primitive number types, but no mix of different number types.
     *
     * `null` values in the input are always ignored.
     *
     * If the input contains [`NaN`][NaN] values, the result will be `NaN`,
     * unless `skipNaN` is set to `true`.
     */
    @ExcludeFromSources
    typealias InputValuesSnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for non-`-OrNull` overloads. KDoc-snippet.}
     *
     * Throws a [NoSuchElementException] when there is nothing left to compare,
     * for instance when the input is empty or contains only `null`
     * (or, if `skipNaN` is `true`, only `null` and [`NaN`][NaN]) values.
     */
    @ExcludeFromSources
    typealias ThrowsOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for `-OrNull` overloads. KDoc-snippet.}
     *
     * Returns `null` when there is nothing left to compare,
     * for instance when the input is empty or contains only `null`
     * (or, if `skipNaN` is `true`, only `null` and [`NaN`][NaN]) values.
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
     * {@comment Note about the row expression argument. KDoc-snippet.}
     *
     * The given [RowExpression] is evaluated for each row of the dataframe.
     * The row is both the receiver and the argument (`it`) of the expression,
     * so the values in it can be accessed directly.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowExpression]}
     */
    @ExcludeFromSources
    typealias RowExpressionSnippet = Nothing

    /**
     * {@comment Note about the aggregate columns selector of the `-For` modes. KDoc-snippet.}
     *
     * The columns are selected with the [ColumnsForAggregateSelectionDsl] — an extension of the
     * Columns Selection DSL which lets you rename the result of a column with
     * [into][ColumnsForAggregateSelectionDsl.into] and supply a
     * [default][ColumnsForAggregateSelectionDsl.default] value for columns without any values.
     */
    @ExcludeFromSources
    typealias AggregateColumnsSelectorSnippet = Nothing

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

    /**
     * {@comment The shared `skipNaN` parameter documentation. KDoc-snippet.}
     *
     * @param [skipNaN\] If `true`, [`NaN`][NaN] values are ignored, just like `null` values.
     *   If `false` (the default), a [`NaN`][NaN] in the input is propagated to the result.
     *   Only has an effect on [Double] and [Float] values.
     */
    @ExcludeFromSources
    typealias SkipNaNParam = Nothing

    /**
     * {@comment The shared `separate` parameter documentation. KDoc-snippet.}
     *
     * @param [separate\] If `false` (the default), the resulting columns are indexed
     *   first by the pivot key(s) and then by the names of the aggregated columns.
     *   If `true`, this order is reversed: the results are grouped by aggregated column first.
     */
    @ExcludeFromSources
    typealias SeparateParam = Nothing
}
