package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.flatten
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

/**
 * @comment
 *   Holds all KDoc-snippets that the summary statistics operations have in common.
 *   The KDoc-topic of each statistic (like [CommonMinMaxDocs] for `min`/`max`, or `SumDocs` for
 *   `sum`) inherits from this interface, so the snippets can be included from any of them,
 *   like `{@include [CommonMinMaxDocs.SkipNanParam]}`.
 *
 *   Some snippets mention the statistic by name. Those use the [STATISTIC], [STATISTIC_VERB], and
 *   [STATISTIC_COLUMN_NAME] keys, which each statistic sets in a single `Set...StatisticArgs` alias,
 *   like `{@set [STATISTIC] mean}`. The statistic KDoc-topics then wrap these snippets, like:
 *   `@include [CommonStatisticsDocs.ColumnsSelectorParam] {@include [SetMeanStatisticArgs]}`.
 *
 *   NOTE: this cannot be @ExcludedFromSources because [CommonMinMaxDocs] and the other statistics
 *   KDoc-topics use it as supertype.
 */
internal interface CommonStatisticsDocs {

    /*
     * The key for a @set that defines the name of the statistic, like "mean" or "minimum".
     */
    @ExcludeFromSources
    typealias STATISTIC = Nothing

    /*
     * The key for a @set that defines what the statistic does with each value,
     * like "average" or "compare".
     */
    @ExcludeFromSources
    typealias STATISTIC_VERB = Nothing

    /*
     * The key for a @set that defines the default name of a resulting column, in code format,
     * like `"mean"` or `"min"` (including the backticks).
     */
    @ExcludeFromSources
    typealias STATISTIC_COLUMN_NAME = Nothing

    /**
     * {@comment Note about how `null` values in the input are treated. KDoc-snippet.}
     *
     * `null` values in the input are always ignored.
     */
    @ExcludeFromSources
    typealias NullHandlingSnippet = Nothing

    /**
     * {@comment Note about how NaN values in the input are treated. KDoc-snippet.
     *    Only include this in the KDoc of overloads that actually have a `skipNaN` parameter;
     *    overloads of a fixed integer type ([Byte], [Short], [Int], [Long]) have none,
     *    as those types have no `NaN`.}
     *
     * If the input contains {@include [NaNLink]} values, the result will be `NaN`,
     * unless [skipNaN\] is set to `true`.
     */
    @ExcludeFromSources
    typealias NaNHandlingSnippet = Nothing

    /**
     * @comment [NullHandlingSnippet] and [NaNHandlingSnippet] combined. KDoc-snippet.
     * @include [NullHandlingSnippet]
     * @include [NaNHandlingSnippet]
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
     * @comment The shared `skipNaN` parameter documentation. KDoc-snippet.
     *
     * @param [skipNaN\] If `true`, {@include [NaNLink]} values are ignored, just like `null` values.
     *   If `false` (the default), a {@include [NaNLink]} in the input is propagated to the result.
     *   This only has an effect on [Double] and [Float] values.
     */
    @ExcludeFromSources
    typealias SkipNanParam = Nothing

    /**
     * @comment The shared `separate` parameter documentation. KDoc-snippet.
     *
     * @param [separate\] If `false` (the default), the resulting columns are indexed
     *   first by the pivot key(s) and then by the names of the aggregated columns.
     *   If `true`, this order is reversed: the results are grouped by aggregated column first.
     */
    @ExcludeFromSources
    typealias SeparateParam = Nothing

    /**
     * {@comment The documentation website links all [Grouped] KDocs end with. KDoc-snippet.}
     *
     * For more information: {@include [DocumentationUrls.GroupByStatistics]}, and
     * {@include [DocumentationUrls.GroupByAggregationStatistics]}
     */
    @ExcludeFromSources
    typealias GroupByUrlsSnippet = Nothing

    /**
     * {@comment The documentation website links all [Pivot] and [PivotGroupBy] KDocs end with.
     *    KDoc-snippet.}
     *
     * For more information: {@include [DocumentationUrls.PivotStatistics]}, and
     * {@include [DocumentationUrls.PivotAggregationStatistics]}
     */
    @ExcludeFromSources
    typealias PivotUrlsSnippet = Nothing

    /**
     * {@comment Note that columns inside column groups are not taken into account. KDoc-snippet.
     *    Uses the [STATISTIC] key.}
     *
     * This includes columns inside [column groups][ColumnGroup].
     * To include those in the {@get [STATISTIC] statistic}, [flatten][DataFrame.flatten] the DataFrame first.
     */
    @ExcludeFromSources
    typealias ColumnGroupsIgnoredSnippet = Nothing

    /**
     * {@comment Note about which columns the no-argument modes of the number statistics take into account.
     *    KDoc-snippet. Uses the [STATISTIC] key.}
     *
     * All columns of a primitive number type (and all "mixed" [Number] columns) are taken into account;
     * the other columns are simply left out of the result.
     *
     * @include [ColumnGroupsIgnoredSnippet]
     */
    @ExcludeFromSources
    typealias AllSuitableNumberColumnsSnippet = Nothing

    /**
     * {@comment Note that the results of the expression are the input of the `-Of` modes. KDoc-snippet.}
     *
     * The result of the [expression\] is considered the 'input' of this operation.
     */
    @ExcludeFromSources
    typealias ExpressionResultIsInputSnippet = Nothing

    /**
     * {@comment Note about what empty pivot intersections become. KDoc-snippet.}
     *
     * For empty pivot intersections, `null` or the [set default][PivotGroupBy.default] are used.
     */
    @ExcludeFromSources
    typealias EmptyPivotIntersectionSnippet = Nothing

    /**
     * @comment The `columns` parameter of the [ColumnsSelector] overloads. KDoc-snippet.
     *    Uses the [STATISTIC] key.
     *
     * @param [columns\] The [ColumnsSelector] used to select the columns
     *   to compute the {@get [STATISTIC] statistic} of.
     */
    @ExcludeFromSources
    typealias ColumnsSelectorParam = Nothing

    /**
     * @comment The `columns` parameter of the [ColumnsForAggregateSelector] overloads. KDoc-snippet.
     *    Uses the [STATISTIC] key.
     *
     * @param [columns\] The [ColumnsForAggregateSelector] used to select the columns
     *   to compute the {@get [STATISTIC] statistic} of.
     */
    @ExcludeFromSources
    typealias AggregateColumnsSelectorParam = Nothing

    /**
     * @comment The `columns` parameter of the [String] overloads. KDoc-snippet.
     *    Uses the [STATISTIC] key.
     *
     * @param [columns\] The names of the columns to compute the {@get [STATISTIC] statistic} of.
     */
    @ExcludeFromSources
    typealias ColumnNamesParam = Nothing

    /**
     * @comment The `expression` parameter of the `-Of` overloads. KDoc-snippet.
     *    Uses the [STATISTIC_VERB] key.
     *
     * @param [expression\] The [RowExpression] to compute the value to {@get [STATISTIC_VERB] aggregate}
     *   for each row.
     */
    @ExcludeFromSources
    typealias ExpressionParam = Nothing

    /**
     * @comment The type parameter `T` of the reified row overloads. KDoc-snippet.
     *    Uses the [STATISTIC_VERB] key.
     *
     * @param [T\] The type of the values to {@get [STATISTIC_VERB] aggregate}.
     *   Only columns of this type are taken into account.
     */
    @ExcludeFromSources
    typealias RowValuesTypeParam = Nothing

    /**
     * @comment The `name` parameter of the column-selecting [Grouped] overloads. KDoc-snippet.
     *    Uses the [STATISTIC_COLUMN_NAME] key.
     *
     * @param [name\] The name of the resulting column.
     *   If `null` (the default), the name of the selected column is used if exactly one column
     *   is selected, and {@get [STATISTIC_COLUMN_NAME] `"statistic"`} otherwise.
     *   This name needs to be unique, else a [DuplicateColumnPathInsertException] is thrown.
     */
    @ExcludeFromSources
    typealias ResultColumnNameParam = Nothing

    /**
     * @comment The `name` parameter of the [Grouped] `-Of` overloads. KDoc-snippet.
     *    Uses the [STATISTIC_COLUMN_NAME] key.
     *
     * @param [name\] The name of the resulting column.
     *   If `null` (the default), {@get [STATISTIC_COLUMN_NAME] `"statistic"`} is used.
     *   This name needs to be unique, else a [DuplicateColumnPathInsertException] is thrown.
     */
    @ExcludeFromSources
    typealias ExpressionResultColumnNameParam = Nothing
}
