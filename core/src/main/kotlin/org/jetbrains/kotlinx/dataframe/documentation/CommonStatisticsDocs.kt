package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl

/**
 * @comment
 *   Holds all KDoc-snippets that the summary statistics operations have in common.
 *   The KDoc-topic of each statistic (like [CommonMinMaxDocs] for `min`/`max`, or `SumDocs` for
 *   `sum`) inherits from this interface, so the snippets can be included from any of them,
 *   like `{@include [CommonMinMaxDocs.SkipNaNParam]}`.
 *
 *   NOTE: this cannot be @ExcludedFromSources because [CommonMinMaxDocs] and the other statistics
 *   KDoc-topics use it as supertype.
 */
internal interface CommonStatisticsDocs {

    /**
     * {@comment Note about how `null` and NaN values in the input are treated. KDoc-snippet.}
     *
     * `null` values in the input are always ignored.
     *
     * If the input contains {@include [NaNLink]} values, the result will be `NaN`,
     * unless [skipNaN\] is set to `true`.
     */
    @ExcludeFromSources
    typealias NullAndNaNHandlingSnippet = Nothing

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
     * [`into`][ColumnsForAggregateSelectionDsl.into] and supply a
     * [`default`][ColumnsForAggregateSelectionDsl.default] value for columns without any values.
     */
    @ExcludeFromSources
    typealias AggregateColumnsSelectorSnippet = Nothing

    /**
     * {@comment The shared `skipNaN` parameter documentation. KDoc-snippet.}
     *
     * @param [skipNaN\] If `true`, {@include [NaNLink]} values are ignored, just like `null` values.
     *   If `false` (the default), a {@include [NaNLink]} in the input is propagated to the result.
     *   This only has an effect on [Double] and [Float] values.
     */
    @ExcludeFromSources
    typealias SkipNanParam = Nothing

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
