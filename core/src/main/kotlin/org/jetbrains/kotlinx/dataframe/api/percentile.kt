@file:OptIn(ExperimentalTypeInference::class)

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.CommonMedianPercentileDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC_VERB
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Issues
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.intraComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateByOrNull
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfRow
import org.jetbrains.kotlinx.dataframe.impl.columns.toComparableColumns
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.ROW_PERCENTILE
import org.jetbrains.kotlinx.dataframe.util.ROW_PERCENTILE_OR_NULL
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region docs

/**
 * {@comment
 *    The Percentile Operation KDoc-topic; it also holds all common `percentile` KDoc-snippets.
 *    Link to it with `{@include [PercentileDocsLink]}`.
 *    The snippets `median` and `percentile` have in common live in [CommonMedianPercentileDocs].
 * }
 *
 * ## The Percentile Operation
 *
 * Computes the given [percentile](https://en.wikipedia.org/wiki/Percentile) of values:
 * the value below which the given percentage of the sorted values falls.
 * This is also called the "centile", or the 100-[quantile](https://en.wikipedia.org/wiki/Quantile).
 *
 * The 25th percentile is also known as the first [quartile](https://en.wikipedia.org/wiki/Quartile) (Q1),
 * the 50th percentile as the [median][DataFrame.median] or second quartile (Q2),
 * and the 75th percentile as the third quartile (Q3).
 *
 * @include [CommonMedianPercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [CommonMedianPercentileDocs.BigNumbersSnippet]
 *
 * ### Percentile Modes
 *
 * Depending on what exactly you want the percentile of, there are several modes.
 * They are shown here for [DataFrame], but they exist for the other receivers too:
 *
 * - [`percentile`][DataFrame.percentile]`(percentile)` — the percentile of each suitable column separately.
 * - [`percentile`][DataFrame.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in all selected columns.
 * - [`percentileFor`][DataFrame.percentileFor]`(percentile) { columns }` — the percentile of each selected column
 *   separately.
 * - [`percentileOf`][DataFrame.percentileOf]`(percentile) { expression }` — the percentile of the values that
 *   the given expression returns for each row.
 * - [`percentileBy`][DataFrame.percentileBy]`(percentile) { expression }` — the row at the percentile of the values
 *   that the given expression returns for each row.
 *
 * [`percentile`][DataFrame.percentile]`(percentile) { columns }`, [`percentileOf`][DataFrame.percentileOf], and
 * [`percentileBy`][DataFrame.percentileBy] all have an `-OrNull` counterpart which returns `null` instead of
 * throwing an exception when there is nothing to compute the percentile of.
 *
 * Due to a limitation in Kotlin's overload resolution ({@include [Issues.OverloadResolutionByLambdaReturnTypeLink]}),
 * computing the percentile of non-number comparable values with
 * [`percentile`][DataFrame.percentile]`(percentile) { columns }` or [`percentileOf`][DataFrame.percentileOf]
 * requires either explicit type arguments, like `df.percentile<_, String>(25.0) { name.firstName }`,
 * or passing the lambda inside the parentheses, like `df.percentile(25.0, { name.firstName })`.
 *
 * @include [CommonMedianPercentileDocs.EagerLambdaAnalysisSnippet]
 *
 * Related operation:
 * - [`median`][DataFrame.median] — the 50th percentile; the same as `percentile(50.0)`.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * See all summary statistics: {@include [DocumentationUrls.Statistics]}
 */
internal interface PercentileDocs : CommonMedianPercentileDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetPercentileOperationArg]}
     */
    typealias PercentileSelectingOptions = Nothing

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetPercentileForOperationArg]}
     */
    typealias PercentileForSelectingOptions = Nothing

    /**
     * @include [CommonMedianPercentileDocs.ResultTypeSnippet]
     *
     * For more information about the resulting types: {@include [DocumentationUrls.Percentile.TypeConversion]}
     */
    @ExcludeFromSources
    typealias ResultTypeSnippet = Nothing

    /**
     * @include [CommonMedianPercentileDocs.NumberResultSnippet]
     *
     * For more information about the resulting types: {@include [DocumentationUrls.Percentile.TypeConversion]}
     */
    @ExcludeFromSources
    typealias NumberResultSnippet = Nothing

    /**
     * @include [CommonMedianPercentileDocs.ComparableResultSnippet]
     *
     * For more information about the resulting types: {@include [DocumentationUrls.Percentile.TypeConversion]}
     */
    @ExcludeFromSources
    typealias ComparableResultSnippet = Nothing

    /** @include [CommonMedianPercentileDocs.ThrowsOnEmptySnippet] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias ThrowsOnEmptySnippet = Nothing

    /** @include [CommonMedianPercentileDocs.NullOnEmptySnippet] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias NullOnEmptySnippet = Nothing

    /** @include [CommonMedianPercentileDocs.NullCellOnEmptySnippet] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias NullCellOnEmptySnippet = Nothing

    /** @include [CommonMedianPercentileDocs.NullCellOnEmptyPivotSnippet] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias NullCellOnEmptyPivotSnippet = Nothing

    /** @include [CommonMedianPercentileDocs.BySelectionSnippet] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias BySelectionSnippet = Nothing

    /** @include [CommonMedianPercentileDocs.AllComparableColumnsSnippet] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias AllComparableColumnsSnippet = Nothing

    /** @include [CommonStatisticsDocs.ColumnGroupsIgnoredSnippet] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias ColumnGroupsIgnoredSnippet = Nothing

    /** @include [CommonStatisticsDocs.ColumnsSelectorParam] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias ColumnsSelectorParam = Nothing

    /** @include [CommonStatisticsDocs.AggregateColumnsSelectorParam] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias AggregateColumnsSelectorParam = Nothing

    /** @include [CommonStatisticsDocs.ColumnNamesParam] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias ColumnNamesParam = Nothing

    /**
     * @include [CommonStatisticsDocs.ColumnNamesParam] {@include [SetPercentileStatisticArgs]}
     *   The values in these columns must be mutually comparable, else an [IllegalStateException] is thrown.
     */
    @ExcludeFromSources
    typealias ComparableColumnNamesParam = Nothing

    /** @include [CommonStatisticsDocs.ExpressionParam] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias ExpressionParam = Nothing

    /** @include [CommonStatisticsDocs.RowValuesTypeParam] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias RowValuesTypeParam = Nothing

    /** @include [CommonStatisticsDocs.ResultColumnNameParam] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias ResultColumnNameParam = Nothing

    /** @include [CommonStatisticsDocs.ExpressionResultColumnNameParam] {@include [SetPercentileStatisticArgs]} */
    @ExcludeFromSources
    typealias ExpressionResultColumnNameParam = Nothing

    /**
     * @param [percentile\] The percentile to compute, in the range `[0.0, 100.0]`,
     *   like `25.0` for the first quartile, `50.0` for the median, or `75.0` for the third quartile.
     *   A percentile outside this range causes an [IllegalStateException].
     * @comment The shared `percentile` parameter documentation. KDoc-snippet.
     */
    @ExcludeFromSources
    typealias PercentileParam = Nothing

    /**
     * {@comment The parts all [DataFrame.percentileFor] overloads have in common. KDoc-snippet.}
     *
     * Returns the given percentile of the values of each selected column of this [DataFrame] separately.
     *
     * @include [PercentileDocs.InputValuesSnippet]
     *
     * @include [PercentileDocs.ResultTypeSnippet]
     *
     * @include [PercentileDocs.NullCellOnEmptySnippet]
     *
     * $[NOTE]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][PercentileDocs.PercentileForSelectingOptions].
     *
     * See also:
     * - [`percentile`][DataFrame.percentile]`(percentile)` — the same, but for all suitable columns at once.
     * - [`percentile`][DataFrame.percentile]`(percentile) { columns }` — a single percentile of all values
     *   in the selected columns.
     * - [`medianFor`][DataFrame.medianFor] — the median (50th percentile) of each selected column.
     * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
     *
     * For more information: {@include [DocumentationUrls.Percentile]}
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface DataFramePercentileForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this percentileFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [Grouped.percentileFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [GroupBy] by computing the given percentile of the values of
     * each selected column separately, per group.
     *
     * Returns a new [DataFrame] with one row per group, containing the group key columns
     * and a column with the percentile for each selected column.
     *
     * $[INPUT]
     *
     * @include [PercentileDocs.ResultTypeSnippet]
     *
     * @include [PercentileDocs.NullCellOnEmptySnippet]
     *
     * $[NOTE]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][PercentileDocs.PercentileForSelectingOptions].
     *
     * See also:
     * - [`percentile`][Grouped.percentile]`(percentile)` — the same, but for all suitable columns at once.
     * - [`percentile`][Grouped.percentile]`(percentile) { columns }` — a single percentile of all values
     *   in the selected columns, per group.
     * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
     * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
     *
     * @include [PercentileDocs.GroupByUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface GroupedPercentileForSnippet {

        // The note about the supported input values; differs on whether there's a `skipNaN` parameter
        typealias INPUT = Nothing

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this percentileFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [Grouped.percentile] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [GroupBy] by computing a single percentile of all the values
     * in the selected columns, per group.
     *
     * Returns a new [DataFrame] with one row per group, containing the group key columns and
     * a single column with the percentile per group.
     * That column is named [name\], or, if [name\] is `null`, after the selected column
     * if exactly one column is selected, and `"percentile"` otherwise.
     *
     * @include [PercentileDocs.InputValuesSnippet]
     *
     * @include [PercentileDocs.ResultTypeSnippet]
     *
     * @include [PercentileDocs.NullCellOnEmptySnippet]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][PercentileDocs.PercentileSelectingOptions].
     *
     * See also:
     * - [`percentileFor`][Grouped.percentileFor] — the percentile of each selected column separately, per group.
     * - [`percentileOf`][Grouped.percentileOf] — the percentile of the values a row expression returns
     *   for each row of a group.
     * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
     * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
     *
     * @include [PercentileDocs.GroupByUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface GroupedPercentileSnippet {

        // The example to render for this percentile overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [Pivot.percentileFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [Pivot] by computing the given percentile of the values of
     * each selected column separately, per group.
     *
     * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the percentile
     * of each selected column of the corresponding group.
     *
     * @include [PercentileDocs.InputValuesSnippet]
     *
     * @include [PercentileDocs.ResultTypeSnippet]
     *
     * @include [PercentileDocs.NullCellOnEmptyPivotSnippet]
     *
     * $[NOTE]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][PercentileDocs.PercentileForSelectingOptions], or check out the
     * [`Pivot` Grammar][PivotDocs.Grammar].
     *
     * See also:
     * - [`percentile`][Pivot.percentile]`(percentile)` — the same, but for all suitable columns at once.
     * - [`percentile`][Pivot.percentile]`(percentile) { columns }` — a single percentile of all values
     *   in the selected columns, per group.
     * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
     * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
     *
     * @include [PercentileDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotPercentileForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this percentileFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [Pivot.percentile] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [Pivot] by computing a single percentile of all the values
     * in the selected columns, per group.
     *
     * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the percentile
     * of all the values in the selected columns of the corresponding group.
     *
     * @include [PercentileDocs.InputValuesSnippet]
     *
     * @include [PercentileDocs.ResultTypeSnippet]
     *
     * @include [PercentileDocs.NullCellOnEmptyPivotSnippet]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][PercentileDocs.PercentileSelectingOptions], or check out the
     * [`Pivot` Grammar][PivotDocs.Grammar].
     *
     * See also:
     * - [`percentile`][Pivot.percentile]`(percentile)` — the percentile of each suitable column separately,
     *   per group.
     * - [`percentileFor`][Pivot.percentileFor] — the percentile of each selected column separately, per group.
     * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
     * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
     *
     * @include [PercentileDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotPercentileSnippet {

        // The example to render for this percentile overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [PivotGroupBy.percentileFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [PivotGroupBy] by computing the given percentile of the values of
     * each selected column separately, per group.
     *
     * Returns a [DataFrame] where each cell contains the percentile of each selected column
     * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
     *
     * @include [PercentileDocs.InputValuesSnippet]
     *
     * @include [PercentileDocs.ResultTypeSnippet]
     *
     * @include [PercentileDocs.NullCellOnEmptyPivotSnippet]
     *
     * $[NOTE]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][PercentileDocs.PercentileForSelectingOptions], or check out the
     * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
     *
     * See also:
     * - [`percentile`][PivotGroupBy.percentile]`(percentile)` — the same, but for all suitable columns at once.
     * - [`percentile`][PivotGroupBy.percentile]`(percentile) { columns }` — a single percentile of all values
     *   in the selected columns, per group.
     * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
     *   a [PivotGroupBy].
     * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
     *
     * @include [PercentileDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotGroupByPercentileForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this percentileFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [PivotGroupBy.percentile] overloads have in common.
     *    KDoc-snippet.}
     *
     * Aggregates this [PivotGroupBy] by computing a single percentile of all the values
     * in the selected columns, per group.
     *
     * Returns a [DataFrame] where each cell contains the percentile of all the values in the
     * selected columns of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
     *
     * @include [PercentileDocs.InputValuesSnippet]
     *
     * @include [PercentileDocs.ResultTypeSnippet]
     *
     * @include [PercentileDocs.NullCellOnEmptyPivotSnippet]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][PercentileDocs.PercentileSelectingOptions], or check out the
     * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
     *
     * See also:
     * - [`percentile`][PivotGroupBy.percentile]`(percentile)` — the percentile of each suitable column
     *   separately, per group.
     * - [`percentileFor`][PivotGroupBy.percentileFor] — the percentile of each selected column separately,
     *   per group.
     * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
     *   a [PivotGroupBy].
     * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
     *
     * @include [PercentileDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotGroupByPercentileSnippet {

        // The example to render for this percentile overload
        typealias EXAMPLE = Nothing
    }
}

/** [The Percentile Operation][PercentileDocs] */
@ExcludeFromSources
private typealias PercentileDocsLink = Nothing

/** {@set [STATISTIC] percentile}{@set [STATISTIC_VERB] include}{@set [STATISTIC_COLUMN_NAME] `"percentile"`} */
@ExcludeFromSources
private typealias SetPercentileStatisticArgs = Nothing

/** {@set [SelectingColumns.OPERATION] [percentile][percentile]} */
@ExcludeFromSources
private typealias SetPercentileOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [percentileFor][percentileFor]} */
@ExcludeFromSources
private typealias SetPercentileForOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [percentileOrNull][percentileOrNull]} */
@ExcludeFromSources
private typealias SetPercentileOrNullOperationArg = Nothing

// endregion

// region DataColumn

/**
 * Returns the given [percentile] of the comparable values in this [DataColumn].
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * Columns of primitive numbers use the [percentile][DataColumn.percentile] overload with a `skipNaN` parameter,
 * which returns an interpolated [Double].
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * See also:
 * - [percentileOrNull][DataColumn.percentileOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [percentileOf][DataColumn.percentileOf] — the percentile of the values an expression returns for each element.
 * - [percentileBy][DataColumn.percentileBy] — the element at the percentile of the values a selector returns.
 * - [median][DataColumn.median] — the median (50th percentile) of the values in this column.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The first quartile of the first names in the "name"/"firstName" column (in alphabetical order)
 * df.name.firstName.percentile(25.0)
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @return The given percentile of the values in this column.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public fun <T : Comparable<T & Any>?> DataColumn<T>.percentile(percentile: Double): T & Any =
    percentileOrNull(percentile).suggestIfNull("percentile")

/**
 * Returns the given [percentile] of the comparable values in this [DataColumn],
 * or `null` if there is nothing to compute the percentile of.
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * Columns of primitive numbers use the [percentileOrNull][DataColumn.percentileOrNull] overload with a `skipNaN`
 * parameter, which returns an interpolated [Double].
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * See also:
 * - [percentile][DataColumn.percentile] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [percentileOfOrNull][DataColumn.percentileOfOrNull] — the percentile of the values an expression returns
 *   for each element.
 * - [percentileByOrNull][DataColumn.percentileByOrNull] — the element at the percentile of the values
 *   a selector returns.
 * - [medianOrNull][DataColumn.medianOrNull] — the median (50th percentile) of the values in this column.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of the cities in the "city" column (in alphabetical order),
 * // or `null` if the column contains no values other than `null`
 * df.city.percentileOrNull(75.0)
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @return The given percentile of the values in this column,
 *   or `null` if there are no values to compute the percentile of.
 */
public fun <T : Comparable<T & Any>?> DataColumn<T>.percentileOrNull(percentile: Double): T? =
    Aggregators.percentileComparables<T>(percentile).aggregateSingleColumn(this)

/**
 * Returns the given [percentile] of the numbers in this [DataColumn], as a [Double].
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * See also:
 * - [percentileOrNull][DataColumn.percentileOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [percentileOf][DataColumn.percentileOf] — the percentile of the values an expression returns for each element.
 * - [percentileBy][DataColumn.percentileBy] — the element at the percentile of the values a selector returns.
 * - [median][DataColumn.median] — the median (50th percentile) of the values in this column.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The first quartile of the ages in the "age" column
 * df.age.percentile(25.0)
 * // The 90th percentile of the weights in the "weight" column, ignoring `NaN` values
 * df.weight.percentile(90.0, skipNaN = true)
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return The given percentile of the values in this column, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public fun <T> DataColumn<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T & Any>?, T : Number? =
    percentileOrNull(percentile = percentile, skipNaN = skipNaN).suggestIfNull("percentile")

/**
 * Returns the given [percentile] of the numbers in this [DataColumn], as a [Double],
 * or `null` if there is nothing to compute the percentile of.
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * See also:
 * - [percentile][DataColumn.percentile] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [percentileOfOrNull][DataColumn.percentileOfOrNull] — the percentile of the values an expression returns
 *   for each element.
 * - [percentileByOrNull][DataColumn.percentileByOrNull] — the element at the percentile of the values
 *   a selector returns.
 * - [medianOrNull][DataColumn.medianOrNull] — the median (50th percentile) of the values in this column.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of the weights in the "weight" column,
 * // or `null` if the column contains no values other than `null`
 * df.weight.percentileOrNull(75.0)
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return The given percentile of the values in this column, as a [Double],
 *   or `null` if there are no values to compute the percentile of.
 */
public fun <T> DataColumn<T>.percentileOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T & Any>?, T : Number? =
    Aggregators.percentileNumbers<T>(percentile, skipNaN).aggregateSingleColumn(this)

/**
 * Returns the element of this [DataColumn] at the given [percentile] of the values that the given [selector]
 * returns for each element.
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * Don't confuse [percentileBy] with [percentileOf][DataColumn.percentileOf], which returns the percentile
 * of the [selector] values itself instead of the element it belongs to.
 *
 * See also:
 * - [percentileByOrNull][DataColumn.percentileByOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [medianBy][DataColumn.medianBy] — the element at the median (50th percentile) of the values
 *   a selector returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The first name at the first quartile of the lengths of all names in the "name"/"firstName" column
 * df.name.firstName.percentileBy(25.0) { it.length }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The element at the given percentile of the values [selector] returns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = percentileByOrNull(percentile, skipNaN, selector).suggestIfNull("percentileBy")

/**
 * Returns the element of this [DataColumn] at the given [percentile] of the values that the given [selector]
 * returns for each element, or `null` if there is nothing to compute the percentile of.
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * Don't confuse [percentileByOrNull] with [percentileOfOrNull][DataColumn.percentileOfOrNull], which returns
 * the percentile of the [selector] values itself instead of the element it belongs to.
 *
 * See also:
 * - [percentileBy][DataColumn.percentileBy] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [medianByOrNull][DataColumn.medianByOrNull] — the element at the median (50th percentile) of the values
 *   a selector returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The first name at the first quartile of the lengths of all names in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.percentileByOrNull(25.0) { it.length }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The element at the given percentile of the values [selector] returns,
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileByOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.percentileCommon<R>(percentile, skipNaN).aggregateByOrNull(this, selector)

/**
 * Returns the given [percentile] of the comparable values that the given [expression] returns
 * for each element of this [DataColumn].
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [percentileOf][DataColumn.percentileOf] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [Double].
 *
 * @include [PercentileDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * Don't confuse [percentileOf] with [percentileBy][DataColumn.percentileBy], which returns the element
 * the percentile of the [expression] values belongs to instead of that value.
 *
 * See also:
 * - [percentileOfOrNull][DataColumn.percentileOfOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [medianOf][DataColumn.medianOf] — the median (50th percentile) of the values an expression returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of all last names in the "name" column group, in upper case
 * df.name.percentileOf<_, String>(25.0) { it.lastName.uppercase() }
 * // The same, with the lambda inside the parentheses
 * df.name.percentileOf(25.0, { it.lastName.uppercase() })
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The given percentile of the values [expression] returns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileOf(
    percentile: Double,
    crossinline expression: (T) -> R,
): R & Any = percentileOfOrNull(percentile, expression).suggestIfNull("percentileOf")

/**
 * Returns the given [percentile] of the comparable values that the given [expression] returns
 * for each element of this [DataColumn], or `null` if there is nothing to compute the percentile of.
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [percentileOfOrNull][DataColumn.percentileOfOrNull] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [Double].
 *
 * @include [PercentileDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * Don't confuse [percentileOfOrNull] with [percentileByOrNull][DataColumn.percentileByOrNull], which returns
 * the element the percentile of the [expression] values belongs to instead of that value.
 *
 * See also:
 * - [percentileOf][DataColumn.percentileOf] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [medianOfOrNull][DataColumn.medianOfOrNull] — the median (50th percentile) of the values
 *   an expression returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of all last names in the "name" column group, in upper case,
 * // or `null` if the column group is empty
 * df.name.percentileOfOrNull<_, String>(25.0) { it.lastName.uppercase() }
 * // The same, with the lambda inside the parentheses
 * df.name.percentileOfOrNull(25.0, { it.lastName.uppercase() })
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The given percentile of the values [expression] returns,
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.percentileOfOrNull(
    percentile: Double,
    crossinline expression: (T) -> R,
): R? = Aggregators.percentileComparables<R>(percentile).aggregateOf(this, expression)

/**
 * Returns the given [percentile] of the numbers that the given [expression] returns
 * for each element of this [DataColumn], as a [Double].
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * Don't confuse [percentileOf] with [percentileBy][DataColumn.percentileBy], which returns the element
 * the percentile of the [expression] values belongs to instead of that value.
 *
 * See also:
 * - [percentileOfOrNull][DataColumn.percentileOfOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the percentile of.
 * - [medianOf][DataColumn.medianOf] — the median (50th percentile) of the values an expression returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The 90th percentile of the lengths of all first names in the "name"/"firstName" column
 * df.name.firstName.percentileOf(90.0) { it.length }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The given percentile of the values [expression] returns, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    percentileOfOrNull(percentile, skipNaN, expression).suggestIfNull("percentileOf")

/**
 * Returns the given [percentile] of the numbers that the given [expression] returns
 * for each element of this [DataColumn], as a [Double],
 * or `null` if there is nothing to compute the percentile of.
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * Don't confuse [percentileOfOrNull] with [percentileByOrNull][DataColumn.percentileByOrNull], which returns
 * the element the percentile of the [expression] values belongs to instead of that value.
 *
 * See also:
 * - [percentileOf][DataColumn.percentileOf] — throws instead of returning `null` for a column
 *   with nothing to compute the percentile of.
 * - [medianOfOrNull][DataColumn.medianOfOrNull] — the median (50th percentile) of the values
 *   an expression returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The 90th percentile of the lengths of all first names in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.percentileOfOrNull(90.0) { it.length }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The given percentile of the values [expression] returns, as a [Double],
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.percentileOfOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.percentileNumbers<R>(percentile, skipNaN).aggregateOf(this, expression)

// endregion

// region DataRow

@Deprecated(ROW_PERCENTILE_OR_NULL, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowPercentileOrNull(): Nothing? = error(ROW_PERCENTILE_OR_NULL)

@Deprecated(ROW_PERCENTILE, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowPercentile(): Nothing = error(ROW_PERCENTILE)

/**
 * Returns the given [percentile] of the comparable values of type [T] in this [DataRow],
 * or `null` if there is nothing to compute the percentile of.
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 * @include [PercentileDocs.ColumnGroupsIgnoredSnippet]
 *
 * This overload is meant for non-number self-comparable types, like [String] or dates.
 *
 * @include [PercentileDocs.RowComparableOverloadResolutionSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * See also:
 * - [rowPercentileOf][DataRow.rowPercentileOf] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [percentileOrNull][DataFrame.percentileOrNull] — the percentile of the values in specific columns
 *   of a [DataFrame].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The first quartile of all `String` values ("name"/"firstName", "name"/"lastName", and "city")
 * // in the first row, or `null` if there are none
 * df[0].rowPercentileOfOrNull<String>(25.0)
 * ```
 *
 * @include [PercentileDocs.RowValuesTypeParam]
 * @include [PercentileDocs.PercentileParam]
 * @return The given percentile of the values of type [T] in this row,
 *   or `null` if there are no values to compute the percentile of.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowPercentileOfOrNull(percentile: Double): T? =
    Aggregators.percentileComparables<T>(percentile).aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the given [percentile] of the comparable values of type [T] in this [DataRow].
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 * @include [PercentileDocs.ColumnGroupsIgnoredSnippet]
 *
 * This overload is meant for non-number self-comparable types, like [String] or dates.
 *
 * @include [PercentileDocs.RowComparableOverloadResolutionSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * See also:
 * - [rowPercentileOfOrNull][DataRow.rowPercentileOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compute the percentile of.
 * - [percentile][DataFrame.percentile] — the percentile of the values in specific columns of a [DataFrame].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The first quartile of all `String` values ("name"/"firstName", "name"/"lastName", and "city")
 * // in the first row
 * df[0].rowPercentileOf<String>(25.0)
 * ```
 *
 * @include [PercentileDocs.RowValuesTypeParam]
 * @include [PercentileDocs.PercentileParam]
 * @return The given percentile of the values of type [T] in this row.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowPercentileOf(percentile: Double): T =
    rowPercentileOfOrNull<T>(percentile).suggestIfNull("rowPercentileOf")

/**
 * Returns the given [percentile] of the numbers of type [T] in this [DataRow], as a [Double],
 * or `null` if there is nothing to compute the percentile of.
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 * @include [PercentileDocs.ColumnGroupsIgnoredSnippet]
 *
 * @include [PercentileDocs.RowNumberOverloadResolutionSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * See also:
 * - [rowPercentileOf][DataRow.rowPercentileOf] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [percentileOrNull][DataFrame.percentileOrNull] — the percentile of the values in specific columns
 *   of a [DataFrame].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all `Int` values ("age" and "weight") in the first row, or `null` if there are none
 * df[0].rowPercentileOfOrNull<Int>(75.0, skipNaN = false)
 * ```
 *
 * @include [PercentileDocs.RowValuesTypeParam]
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return The given percentile of the values of type [T] in this row, as a [Double],
 *   or `null` if there are no values to compute the percentile of.
 */
public inline fun <reified T> DataRow<*>.rowPercentileOfOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T>, T : Number =
    Aggregators.percentileNumbers<T>(percentile, skipNaN).aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the given [percentile] of the numbers of type [T] in this [DataRow], as a [Double].
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 * @include [PercentileDocs.ColumnGroupsIgnoredSnippet]
 *
 * @include [PercentileDocs.RowNumberOverloadResolutionSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * See also:
 * - [rowPercentileOfOrNull][DataRow.rowPercentileOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compute the percentile of.
 * - [rowMedianOf][DataRow.rowMedianOf] — the median (50th percentile) of the numbers of one type in this row.
 * - [percentile][DataFrame.percentile] — the percentile of the values in specific columns of a [DataFrame].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all `Int` values ("age" and "weight") in the first row
 * df[0].rowPercentileOf<Int>(75.0, skipNaN = false)
 * // The 90th percentile of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowPercentileOf<Double>(90.0, skipNaN = true)
 * ```
 *
 * @include [PercentileDocs.RowValuesTypeParam]
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return The given percentile of the values of type [T] in this row, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public inline fun <reified T> DataRow<*>.rowPercentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T>, T : Number =
    rowPercentileOfOrNull<T>(percentile, skipNaN).suggestIfNull("rowPercentileOf")

// endregion

// region DataFrame

/**
 * Returns the given [percentile] of the values of each suitable column of this [DataFrame] separately.
 *
 * @include [PercentileDocs.AllComparableColumnsSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [PercentileDocs.NullCellOnEmptySnippet]
 *
 * See also:
 * - [percentileFor][DataFrame.percentileFor] — the same, but for an explicit selection of columns.
 * - [percentile][DataFrame.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns.
 * - [median][DataFrame.median] — the median (50th percentile) of each column.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the first quartile of each comparable column
 * // ("name"/"firstName", "name"/"lastName", "age", "city", "weight", and "isHappy")
 * df.percentile(25.0)
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A single [DataRow] with the given percentile of each suitable column of this [DataFrame].
 */
@Refine
@Interpretable("Percentile0")
public fun <T> DataFrame<T>.percentile(percentile: Double, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    percentileFor(percentile, skipNaN, intraComparableColumns())

/**
 * @include [PercentileDocs.DataFramePercentileForSnippet]
 * @set [PercentileDocs.DataFramePercentileForSnippet.NOTE] {@include [PercentileDocs.AggregateColumnsSelectorSnippet]}
 * @set [PercentileDocs.DataFramePercentileForSnippet.EXAMPLE]
 * ```kotlin
 * // A single row with the first quartile of the "age" values and of the "weight" values
 * df.percentileFor(25.0) { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.percentileFor(25.0, skipNaN = true) { age into "q1Age" and (weight into "q1Weight") }
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.AggregateColumnsSelectorParam]
 * @return A single [DataRow] with the given percentile of each selected column.
 */
@Refine
@Interpretable("Percentile1")
public fun <T, C : Comparable<*>?> DataFrame<T>.percentileFor(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.percentile.invoke(percentile, skipNaN).aggregateFor(this, columns)

/**
 * @include [PercentileDocs.DataFramePercentileForSnippet]
 * @set [PercentileDocs.DataFramePercentileForSnippet.EXAMPLE]
 * ```kotlin
 * // A single row with the first quartile of the "age" values and of the "weight" values
 * df.percentileFor(25.0, "age", "weight")
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ColumnNamesParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A single [DataRow] with the given percentile of each selected column.
 */
@Refine
@StringApiInterpretable(interpreter = "Percentile1", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.percentileFor(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.percentileFor(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

/**
 * Returns a single [percentile] of all the comparable values in the selected columns of this [DataFrame].
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * For columns of primitive numbers, the [percentile][DataFrame.percentile] overload with a `skipNaN` parameter
 * is used, which returns an interpolated [Double].
 *
 * @include [PercentileDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [percentileOrNull][DataFrame.percentileOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [percentileFor][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [percentileOf][DataFrame.percentileOf] — the percentile of the values a row expression returns for each row.
 * - [median][DataFrame.median] — the median (50th percentile) of all values in the selected columns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetPercentileOperationArg]}
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of all first and last names in the "name" column group
 * df.percentile<_, String>(25.0) { name.firstName and name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.percentile(25.0, { name.firstName and name.lastName })
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ColumnsSelectorParam]
 * @return The given percentile of all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentile(
    percentile: Double,
    columns: ColumnsSelector<T, C>,
): C & Any = percentileOrNull(percentile, columns).suggestIfNull("percentile")

/**
 * Returns a single [percentile] of all the comparable values in the selected columns of this [DataFrame],
 * or `null` if there is nothing to compute the percentile of.
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * For columns of primitive numbers, the [percentileOrNull][DataFrame.percentileOrNull] overload with a `skipNaN`
 * parameter is used, which returns an interpolated [Double].
 *
 * @include [PercentileDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [percentile][DataFrame.percentile] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [percentileFor][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [percentileOfOrNull][DataFrame.percentileOfOrNull] — the percentile of the values a row expression
 *   returns for each row.
 * - [medianOrNull][DataFrame.medianOrNull] — the median (50th percentile) of all values in the selected columns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetPercentileOrNullOperationArg]}
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of all first and last names in the "name" column group,
 * // or `null` if there are no values to compute the percentile of
 * df.percentileOrNull<_, String>(25.0) { name.firstName and name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.percentileOrNull(25.0, { name.firstName and name.lastName })
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ColumnsSelectorParam]
 * @return The given percentile of all the values in the selected columns,
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileOrNull(
    percentile: Double,
    columns: ColumnsSelector<T, C>,
): C? = Aggregators.percentileComparables<C>(percentile).aggregateAll(this, columns)

/**
 * Returns a single [percentile] of all the numbers in the selected columns of this [DataFrame], as a [Double].
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [percentileOrNull][DataFrame.percentileOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [percentileFor][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [percentileOf][DataFrame.percentileOf] — the percentile of the values a row expression returns for each row.
 * - [median][DataFrame.median] — the median (50th percentile) of all values in the selected columns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetPercentileOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all values in the "age" and "weight" columns
 * df.percentile(75.0) { age and weight }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ColumnsSelectorParam]
 * @return The given percentile of all the values in the selected columns, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public fun <T, C> DataFrame<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double
    where C : Number?, C : Comparable<C & Any>? =
    percentileOrNull(percentile, skipNaN, columns).suggestIfNull("percentile")

/**
 * Returns a single [percentile] of all the numbers in the selected columns of this [DataFrame], as a [Double],
 * or `null` if there is nothing to compute the percentile of.
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [percentile][DataFrame.percentile] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [percentileFor][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [percentileOfOrNull][DataFrame.percentileOfOrNull] — the percentile of the values a row expression
 *   returns for each row.
 * - [medianOrNull][DataFrame.medianOrNull] — the median (50th percentile) of all values in the selected columns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetPercentileOrNullOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compute the percentile of
 * df.percentileOrNull(75.0) { age and weight }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ColumnsSelectorParam]
 * @return The given percentile of all the values in the selected columns, as a [Double],
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C> DataFrame<T>.percentileOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    Aggregators.percentileNumbers<C>(percentile, skipNaN).aggregateAll(this, columns)

/**
 * Returns a single [percentile] of all the values in the columns of this [DataFrame] with the given names.
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [percentileOrNull][DataFrame.percentileOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [percentileFor][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [median][DataFrame.median] — the median (50th percentile) of all values in the selected columns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * @include [SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample] {@include [SetPercentileOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all values in the "age" and "weight" columns, as a `Double`
 * df.percentile(75.0, "age", "weight")
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ComparableColumnNamesParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return The given percentile of all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public fun <T> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): Any = percentileOrNull(percentile, *columns, skipNaN = skipNaN).suggestIfNull("percentile")

/**
 * Returns a single [percentile] of all the values in the columns of this [DataFrame] with the given names,
 * or `null` if there is nothing to compute the percentile of.
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [percentile][DataFrame.percentile] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [percentileFor][DataFrame.percentileFor] — the percentile of each selected column separately.
 * - [medianOrNull][DataFrame.medianOrNull] — the median (50th percentile) of all values in the selected columns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * @include [SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample] {@include [SetPercentileOrNullOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of all values in the "age" and "weight" columns, as a `Double`,
 * // or `null` if there are no values to compute the percentile of
 * df.percentileOrNull(75.0, "age", "weight")
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ComparableColumnNamesParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return The given percentile of all the values in the selected columns,
 *   or `null` if there are no values to compute the percentile of.
 */
public fun <T> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): Any? =
    Aggregators.percentileCommon<Comparable<Any>?>(percentile, skipNaN).aggregateAll(this) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
): C & Any = percentileOrNull(percentile, *columns).suggestIfNull("percentile")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: ColumnReference<C>,
): C? = percentileOrNull<T, C>(percentile) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where C : Comparable<C & Any>?, C : Number? =
    percentileOrNull(percentile, *columns, skipNaN = skipNaN).suggestIfNull("percentile")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    percentileOrNull(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
): C & Any = percentileOrNull(percentile, *columns).suggestIfNull("percentile")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: KProperty<C>,
): C? = percentileOrNull<T, C>(percentile) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where C : Comparable<C & Any>?, C : Number? =
    percentileOrNull(percentile, *columns, skipNaN = skipNaN).suggestIfNull("percentile")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.percentileOrNull(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    percentileOrNull(percentile, skipNaN) { columns.toColumnSet() }

/**
 * Returns the given [percentile] of the comparable values that the given [expression] returns
 * for each row of this [DataFrame].
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [percentileOf][DataFrame.percentileOf] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [Double].
 *
 * @include [PercentileDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * Don't confuse [percentileOf] with [percentileBy][DataFrame.percentileBy], which returns the row the
 * percentile of the [expression] values belongs to instead of that value.
 *
 * See also:
 * - [percentileOfOrNull][DataFrame.percentileOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [percentile][DataFrame.percentile] — a single percentile of all values in the selected columns.
 * - [medianOf][DataFrame.medianOf] — the median (50th percentile) of the values a row expression returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of the full names of all rows
 * df.percentileOf<_, String>(25.0) { name.firstName + " " + name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.percentileOf(25.0, { name.firstName + " " + name.lastName })
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ExpressionParam]
 * @return The given percentile of the values [expression] returns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.percentileOf(
    percentile: Double,
    crossinline expression: RowExpression<T, R>,
): R & Any = percentileOfOrNull(percentile, expression).suggestIfNull("percentileOf")

/**
 * Returns the given [percentile] of the comparable values that the given [expression] returns
 * for each row of this [DataFrame], or `null` if there is nothing to compute the percentile of.
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [percentileOfOrNull][DataFrame.percentileOfOrNull] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [Double].
 *
 * @include [PercentileDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.ComparableResultSnippet]
 *
 * Don't confuse [percentileOfOrNull] with [percentileByOrNull][DataFrame.percentileByOrNull], which returns
 * the row the percentile of the [expression] values belongs to instead of that value.
 *
 * See also:
 * - [percentileOf][DataFrame.percentileOf] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [percentileOrNull][DataFrame.percentileOrNull] — a single percentile of all values in the selected columns.
 * - [medianOfOrNull][DataFrame.medianOfOrNull] — the median (50th percentile) of the values
 *   a row expression returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Examples
 * ```kotlin
 * // The first quartile of the full names of all rows, or `null` if this dataframe is empty
 * df.percentileOfOrNull<_, String>(25.0) { name.firstName + " " + name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.percentileOfOrNull(25.0, { name.firstName + " " + name.lastName })
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ExpressionParam]
 * @return The given percentile of the values [expression] returns,
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.percentileOfOrNull(
    percentile: Double,
    crossinline expression: RowExpression<T, R>,
): R? = Aggregators.percentileComparables<R>(percentile).aggregateOf(this, expression)

/**
 * Returns the given [percentile] of the numbers that the given [expression] returns
 * for each row of this [DataFrame], as a [Double].
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * Don't confuse [percentileOf] with [percentileBy][DataFrame.percentileBy], which returns the row the
 * percentile of the [expression] values belongs to instead of that value.
 *
 * See also:
 * - [percentileOfOrNull][DataFrame.percentileOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [percentile][DataFrame.percentile] — a single percentile of all values in the selected columns.
 * - [medianOf][DataFrame.medianOf] — the median (50th percentile) of the values a row expression returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of the weight-to-age ratios of all rows
 * df.percentileOf(75.0) { (weight ?: 0) / age }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ExpressionParam]
 * @return The given percentile of the values [expression] returns, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    percentileOfOrNull(percentile, skipNaN, expression).suggestIfNull("percentileOf")

/**
 * Returns the given [percentile] of the numbers that the given [expression] returns
 * for each row of this [DataFrame], as a [Double],
 * or `null` if there is nothing to compute the percentile of.
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * @include [PercentileDocs.NumberResultSnippet]
 *
 * Don't confuse [percentileOfOrNull] with [percentileByOrNull][DataFrame.percentileByOrNull], which returns
 * the row the percentile of the [expression] values belongs to instead of that value.
 *
 * See also:
 * - [percentileOf][DataFrame.percentileOf] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [percentileOrNull][DataFrame.percentileOrNull] — a single percentile of all values in the selected columns.
 * - [medianOfOrNull][DataFrame.medianOfOrNull] — the median (50th percentile) of the values
 *   a row expression returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The third quartile of the weight-to-age ratios of all rows, or `null` if this dataframe is empty
 * df.percentileOfOrNull(75.0) { (weight ?: 0) / age }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ExpressionParam]
 * @return The given percentile of the values [expression] returns, as a [Double],
 *   or `null` if there are no values to compute the percentile of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.percentileOfOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.percentileNumbers<R>(percentile, skipNaN).aggregateOf(this, expression)

/**
 * Returns the row of this [DataFrame] at the given [percentile] of the values that the given [expression]
 * returns for each row.
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * Don't confuse [percentileBy] with [percentileOf][DataFrame.percentileOf], which returns the percentile
 * of the [expression] values itself instead of the row it belongs to.
 *
 * See also:
 * - [percentileByOrNull][DataFrame.percentileByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [medianBy][DataFrame.medianBy] — the row at the median (50th percentile) of the values
 *   a row expression returns.
 * - [sortBy][DataFrame.sortBy] — orders all rows instead of taking just one.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The row at the first quartile of "age"
 * df.percentileBy(25.0) { age }
 * // The row at the 90th percentile of the weight-to-age ratios
 * df.percentileBy(90.0) { (weight ?: 0) / age }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to compare the rows by.
 * @return The [DataRow] at the given percentile of the values [expression] returns.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = percentileByOrNull(percentile, skipNaN, expression).suggestIfNull("percentileBy")

/**
 * Returns the row of this [DataFrame] at the given [percentile] of the values in the column with the given name.
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * @include [PercentileDocs.ThrowsOnEmptySnippet]
 *
 * Don't confuse [percentileBy] with [percentile][DataFrame.percentile], which returns the percentile value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [percentileByOrNull][DataFrame.percentileByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the percentile of.
 * - [medianBy][DataFrame.medianBy] — the row at the median (50th percentile) of the values in a column.
 * - [sortBy][DataFrame.sortBy] — orders all rows instead of taking just one.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The row at the first quartile of "age"
 * df.percentileBy(25.0, "age")
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @param [column] The name of the column of this [DataFrame] to compare the rows by.
 * @include [PercentileDocs.SkipNanParam]
 * @return The [DataRow] at the given percentile of the values in the given column.
 * @throws NoSuchElementException if there are no values to compute the percentile of.
 */
public fun <T> DataFrame<T>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, column, skipNaN).suggestIfNull("percentileBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, column, skipNaN).suggestIfNull("percentileBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileByOrNull(percentile, column, skipNaN).suggestIfNull("percentileBy")

/**
 * Returns the row of this [DataFrame] at the given [percentile] of the values that the given [expression]
 * returns for each row, or `null` if there is nothing to compute the percentile of.
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * Don't confuse [percentileByOrNull] with [percentileOfOrNull][DataFrame.percentileOfOrNull], which returns
 * the percentile of the [expression] values itself instead of the row it belongs to.
 *
 * See also:
 * - [percentileBy][DataFrame.percentileBy] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [medianByOrNull][DataFrame.medianByOrNull] — the row at the median (50th percentile) of the values
 *   a row expression returns.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The row at the first quartile of "age", or `null` if this dataframe is empty
 * df.percentileByOrNull(25.0) { age }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to compare the rows by.
 * @return The [DataRow] at the given percentile of the values [expression] returns,
 *   or `null` if there are no values to compute the percentile of.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateByOrNull(this, expression)

/**
 * Returns the row of this [DataFrame] at the given [percentile] of the values in the column with the given name,
 * or `null` if there is nothing to compute the percentile of.
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * @include [PercentileDocs.NullOnEmptySnippet]
 *
 * Don't confuse [percentileByOrNull] with [percentileOrNull][DataFrame.percentileOrNull], which returns
 * the percentile value itself instead of the row it belongs to.
 *
 * See also:
 * - [percentileBy][DataFrame.percentileBy] — throws instead of returning `null` when there's nothing
 *   to compute the percentile of.
 * - [medianByOrNull][DataFrame.medianByOrNull] — the row at the median (50th percentile) of the values
 *   in a column.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // The row at the first quartile of "age", or `null` if this dataframe is empty
 * df.percentileByOrNull(25.0, "age")
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @param [column] The name of the column of this [DataFrame] to compare the rows by.
 * @include [PercentileDocs.SkipNanParam]
 * @return The [DataRow] at the given percentile of the values in the given column,
 *   or `null` if there are no values to compute the percentile of.
 */
public fun <T> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = percentileByOrNull(percentile, column.toColumnOf<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateByOrNull(this, column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.percentileByOrNull(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = percentileByOrNull(percentile, column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy

/**
 * Aggregates this [GroupBy] by computing the given [percentile] of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the percentile for each suitable column.
 *
 * @include [PercentileDocs.AllComparableColumnsSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [PercentileDocs.NullCellOnEmptySnippet]
 *
 * See also:
 * - [percentileFor][Grouped.percentileFor] — the same, but for an explicit selection of columns.
 * - [percentile][Grouped.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [median][Grouped.median] — the median (50th percentile) of each column, per group.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * @include [PercentileDocs.GroupByUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of each comparable column
 * df.groupBy { city }.percentile(25.0)
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and the given percentile of each suitable column per group.
 */
@Refine
@Interpretable("GroupByPercentile1")
public fun <T> Grouped<T>.percentile(percentile: Double, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    percentileFor(percentile, skipNaN, intraComparableColumns())

/**
 * @include [PercentileDocs.GroupedPercentileForSnippet]
 * @set [PercentileDocs.GroupedPercentileForSnippet.INPUT] {@include [PercentileDocs.InputValuesSnippet]}
 * @set [PercentileDocs.GroupedPercentileForSnippet.NOTE] {@include [PercentileDocs.AggregateColumnsSelectorSnippet]}
 * @set [PercentileDocs.GroupedPercentileForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the first quartile of the "age" values and of the "weight" values
 * df.groupBy { city }.percentileFor(25.0) { age and weight }
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.AggregateColumnsSelectorParam]
 * @return A new [DataFrame] with the group keys and the given percentile of each selected column per group.
 */
@Refine
@Interpretable("GroupByPercentile0")
public fun <T, C : Comparable<*>?> Grouped<T>.percentileFor(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentile.invoke(percentile, skipNaN).aggregateFor(this, columns)

/**
 * @include [PercentileDocs.GroupedPercentileForSnippet]
 * @set [PercentileDocs.GroupedPercentileForSnippet.INPUT] {@include [PercentileDocs.ComparableInputValuesSnippet]}
 * @set [PercentileDocs.GroupedPercentileForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the first quartile of the "age" values and of the "weight" values
 * df.groupBy { city }.percentileFor(25.0, "age", "weight")
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ColumnNamesParam]
 * @return A new [DataFrame] with the group keys and the given percentile of each selected column per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByPercentile0", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.percentileFor(percentile: Double, vararg columns: String): DataFrame<T> =
    percentileFor(percentile) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.percentileFor(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, skipNaN) { columns.toColumnSet() }

/**
 * @include [PercentileDocs.GroupedPercentileSnippet]
 * @set [PercentileDocs.GroupedPercentileSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the first quartile of all values in the "age" and "weight" columns,
 * // in a column called "q1"
 * df.groupBy { city }.percentile(25.0, "q1") { age and weight }
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ResultColumnNameParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ColumnsSelectorParam]
 * @return A new [DataFrame] with the group keys and a single percentile per group.
 */
@Refine
@Interpretable("GroupByPercentile2")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentile(
    percentile: Double,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateAll(this, name, columns)

/**
 * @include [PercentileDocs.GroupedPercentileSnippet]
 * @set [PercentileDocs.GroupedPercentileSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the first quartile of all values in the "age" and "weight" columns,
 * // in a column called "q1"
 * df.groupBy { city }.percentile(25.0, "age", "weight", name = "q1")
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ComparableColumnNamesParam]
 * @include [PercentileDocs.ResultColumnNameParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and a single percentile per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByPercentile2", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.percentile(
    percentile: Double,
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, name, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, name, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [GroupBy] by computing the given [percentile] of the values that the given [expression]
 * returns for each row of a group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the percentile per group, named [name] (or `"percentile"` if [name] is `null`).
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [PercentileDocs.NullCellOnEmptySnippet]
 *
 * Don't confuse [percentileOf] with [percentileBy][GroupBy.percentileBy], which returns the row of each group
 * at the percentile of the values the expression returns, instead of that value.
 *
 * See also:
 * - [percentile][Grouped.percentile] — a single percentile of all values in the selected columns, per group.
 * - [medianOf][Grouped.medianOf] — the median (50th percentile) of the values a row expression returns,
 *   per group.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * @include [PercentileDocs.GroupByUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the third quartile of the weight-to-age ratios, in a column called "q3Ratio"
 * df.groupBy { city }.percentileOf(75.0, "q3Ratio") { (weight ?: 0) / age }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ExpressionResultColumnNameParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ExpressionParam]
 * @return A new [DataFrame] with the group keys and a single percentile per group.
 */
@Refine
@Interpretable("GroupByPercentileOf")
public inline fun <T, reified R : Comparable<R & Any>?> Grouped<T>.percentileOf(
    percentile: Double,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.percentileCommon<R>(percentile, skipNaN).aggregateOf(this, name, expression)

/**
 * Reduces each group of this [GroupBy] to the row at the given [percentile] of the values that the given
 * [rowExpression] returns for each row of that group.
 *
 * @include [PercentileDocs.ReducedGroupBySnippet]
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [percentileBy] with [percentileOf][Grouped.percentileOf], which returns the percentile value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [medianBy][GroupBy.medianBy] — the row at the median (50th percentile), per group.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person at the first quartile of "age"
 * df.groupBy { city }.percentileBy(25.0) { age }.concat()
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to compute the value to compare the rows by.
 * @return A [ReducedGroupBy] with, for each group, the row at the given percentile
 *   of the values [rowExpression] returns.
 */
@Interpretable("GroupByReduceExpression") // TODO?
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { percentileByOrNull(percentile, skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { percentileByOrNull(percentile, column, skipNaN) }

/**
 * Reduces each group of this [GroupBy] to the row at the given [percentile] of the values
 * in the column with the given name.
 *
 * @include [PercentileDocs.ReducedGroupBySnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [percentileBy] with [percentile][Grouped.percentile], which returns the percentile value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [medianBy][GroupBy.medianBy] — the row at the median (50th percentile), per group.
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person at the first quartile of "age"
 * df.groupBy { city }.percentileBy(25.0, "age").concat()
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @param [column] The name of the column to compare the rows by.
 * @include [PercentileDocs.SkipNanParam]
 * @return A [ReducedGroupBy] with, for each group, the row at the given percentile
 *   of the values in the given column.
 */
public fun <T, G> GroupBy<T, G>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = percentileBy(percentile, column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = percentileBy(percentile, column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

/**
 * Aggregates this [Pivot] by computing the given [percentile] of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the percentile
 * of each suitable column of the corresponding group.
 *
 * @include [PercentileDocs.AllComparableColumnsSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [PercentileDocs.NullCellOnEmptyPivotSnippet]
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [percentileFor][Pivot.percentileFor] — the same, but for an explicit selection of columns.
 * - [percentile][Pivot.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * @include [PercentileDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the first quartile of each comparable column
 * df.pivot { city }.percentile(25.0)
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SeparateParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A single [DataRow] with the given percentile of each suitable column per [pivot] group.
 */
public fun <T> Pivot<T>.percentile(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN, intraComparableColumns())

/**
 * @include [PercentileDocs.PivotPercentileForSnippet]
 * @set [PercentileDocs.PivotPercentileForSnippet.NOTE] {@include [PercentileDocs.AggregateColumnsSelectorSnippet]}
 * @set [PercentileDocs.PivotPercentileForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the first quartile of the "age" values and of the "weight" values
 * df.pivot { city }.percentileFor(25.0) { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.percentileFor(25.0, separate = true) { age and weight }
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SeparateParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.AggregateColumnsSelectorParam]
 * @return A single [DataRow] with the given percentile of each selected column per [pivot] group.
 */
public fun <T, C : Comparable<*>?> Pivot<T>.percentileFor(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { percentileFor(percentile, separate, skipNaN, columns) }

/**
 * @include [PercentileDocs.PivotPercentileForSnippet]
 * @set [PercentileDocs.PivotPercentileForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the first quartile of the "age" values and of the "weight" values
 * df.pivot { city }.percentileFor(25.0, "age", "weight")
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ColumnNamesParam]
 * @include [PercentileDocs.SeparateParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A single [DataRow] with the given percentile of each selected column per [pivot] group.
 */
public fun <T> Pivot<T>.percentileFor(
    percentile: Double,
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Pivot<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Pivot<T>.percentileFor(
    percentile: Double,
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

/**
 * @include [PercentileDocs.PivotPercentileSnippet]
 * @set [PercentileDocs.PivotPercentileSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the third quartile of all values in the "age" and "weight" columns
 * df.pivot { city }.percentile(75.0) { age and weight }
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ColumnsSelectorParam]
 * @return A single [DataRow] with, per [pivot] group, the given percentile of all the values
 *   in the selected columns.
 */
public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { percentile(percentile, skipNaN, columns) }

/**
 * @include [PercentileDocs.PivotPercentileSnippet]
 * @set [PercentileDocs.PivotPercentileSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the third quartile of all values in the "age" and "weight" columns
 * df.pivot { city }.percentile(75.0, "age", "weight")
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ComparableColumnNamesParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A single [DataRow] with, per [pivot] group, the given percentile of all the values
 *   in the selected columns.
 */
public fun <T> Pivot<T>.percentile(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentile(percentile, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentile(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = percentile(percentile, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [Pivot] by computing the given [percentile] of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the percentile
 * of the expression's results for the rows of the corresponding group.
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [PercentileDocs.NullCellOnEmptyPivotSnippet]
 *
 * Don't confuse [percentileOf] with [percentileBy][Pivot.percentileBy], which returns the row of each group
 * at the percentile of the values the expression returns, instead of that value.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [percentile][Pivot.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * @include [PercentileDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the third quartile of the weight-to-age ratios
 * df.pivot { city }.percentileOf(75.0) { (weight ?: 0) / age }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ExpressionParam]
 * @return A single [DataRow] with, per [pivot] group, the given percentile of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { percentileOf(percentile, skipNaN, expression) }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the [row][DataRow]
 * at the given [percentile] of the values that the given [rowExpression] returns for each row of that group.
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 * @include [PercentileDocs.ReducedPivotSnippet]
 *
 * Don't confuse [percentileBy] with [percentileOf][Pivot.percentileOf], which returns the percentile of the values
 * the expression returns itself, instead of the row.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [medianBy][Pivot.medianBy] — the row at the median (50th percentile), per group.
 * - [Pivot reducing][PivotDocs.Reducing] — all other ways to reduce a [Pivot].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person at the third quartile of the weight-to-age ratios
 * df.pivot { city }.percentileBy(75.0) { (weight ?: 0) / age }.with { name }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to compute the value to compare the rows by.
 * @return A [ReducedPivot] holding, per group,
 *   the row at the given percentile of the values [rowExpression] returns.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { percentileByOrNull(percentile, skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { percentileByOrNull(percentile, column, skipNaN) }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the [row][DataRow]
 * at the given [percentile] of the values in the given [column].
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 * @include [PercentileDocs.ReducedPivotSnippet]
 *
 * Don't confuse [percentileBy] with [percentile][Pivot.percentile], which returns the percentile value itself,
 * instead of the row.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [medianBy][Pivot.medianBy] — the row at the median (50th percentile), per group.
 * - [Pivot reducing][PivotDocs.Reducing] — all other ways to reduce a [Pivot].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person at the first quartile of "age"
 * df.pivot { city }.percentileBy(25.0, "age").with { name }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @param [column] The name of the column to compare the rows by.
 * @include [PercentileDocs.SkipNanParam]
 * @return A [ReducedPivot] holding, per group, the row at the given percentile of the values in the given column.
 */
public fun <T> Pivot<T>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = percentileBy(percentile, column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = percentileBy(percentile, column.toColumnAccessor(), skipNaN)
// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by computing the given [percentile] of the values of
 * each suitable column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the percentile of each suitable column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * @include [PercentileDocs.AllComparableColumnsSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [PercentileDocs.NullCellOnEmptyPivotSnippet]
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [percentileFor][PivotGroupBy.percentileFor] — the same, but for an explicit selection of columns.
 * - [percentile][PivotGroupBy.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * @include [PercentileDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the first quartile of each comparable column
 * df.pivot { city }.groupBy { name.lastName }.percentile(25.0)
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SeparateParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A [DataFrame] with the given percentile of each suitable column per [pivot] and [groupBy] group.
 */
public fun <T> PivotGroupBy<T>.percentile(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN, intraComparableColumns())

/**
 * @include [PercentileDocs.PivotGroupByPercentileForSnippet]
 * @set [PercentileDocs.PivotGroupByPercentileForSnippet.NOTE] {@include [PercentileDocs.AggregateColumnsSelectorSnippet]}
 * @set [PercentileDocs.PivotGroupByPercentileForSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the first quartile of the "age" values and of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.percentileFor(25.0) { age and weight }
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SeparateParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.AggregateColumnsSelectorParam]
 * @return A [DataFrame] with the given percentile of each selected column per [pivot] and [groupBy] group.
 */
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.percentile.invoke(percentile, skipNaN).aggregateFor(this, separate, columns)

/**
 * @include [PercentileDocs.PivotGroupByPercentileForSnippet]
 * @set [PercentileDocs.PivotGroupByPercentileForSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the first quartile of the "age" values and of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.percentileFor(25.0, "age", "weight")
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ColumnNamesParam]
 * @include [PercentileDocs.SeparateParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A [DataFrame] with the given percentile of each selected column per [pivot] and [groupBy] group.
 */
public fun <T> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.percentileFor(
    percentile: Double,
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentileFor(percentile, separate, skipNaN) { columns.toColumnSet() }

/**
 * @include [PercentileDocs.PivotGroupByPercentileSnippet]
 * @set [PercentileDocs.PivotGroupByPercentileSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the third quartile of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.percentile(75.0) { age and weight }
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ColumnsSelectorParam]
 * @return A [DataFrame] with, per [pivot] and [groupBy] group, the given percentile of all the values
 *   in the selected columns.
 */
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentile(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.percentileCommon<C>(percentile, skipNaN).aggregateAll(this, columns)

/**
 * @include [PercentileDocs.PivotGroupByPercentileSnippet]
 * @set [PercentileDocs.PivotGroupByPercentileSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the third quartile of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.percentile(75.0, "age", "weight")
 * ```
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.ComparableColumnNamesParam]
 * @include [PercentileDocs.SkipNanParam]
 * @return A [DataFrame] with, per [pivot] and [groupBy] group, the given percentile of all the values
 *   in the selected columns.
 */
public fun <T> PivotGroupBy<T>.percentile(
    percentile: Double,
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentile(
    percentile: Double,
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.percentile(
    percentile: Double,
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = percentile(percentile, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [PivotGroupBy] by computing the given [percentile] of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a [DataFrame] where each cell contains the percentile of the expression's results for the rows
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ExpressionResultIsInputSnippet]
 *
 * @include [PercentileDocs.InputValuesSnippet]
 *
 * @include [PercentileDocs.ResultTypeSnippet]
 *
 * @include [PercentileDocs.NullCellOnEmptyPivotSnippet]
 *
 * Don't confuse [percentileOf] with [percentileBy][PivotGroupBy.percentileBy], which returns the row of each
 * group at the percentile of the values the expression returns, instead of that value.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [percentile][PivotGroupBy.percentile]`(percentile) { columns }` — a single percentile of all values
 *   in the selected columns, per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * @include [PercentileDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the third quartile of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.percentileOf(75.0) { (weight ?: 0) / age }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @include [PercentileDocs.ExpressionParam]
 * @return A [DataFrame] with, per [pivot] and [groupBy] group, the given percentile of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.percentileOf(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.percentileCommon<R>(percentile, skipNaN).aggregateOf(this, expression)

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking from each group
 * the [row][DataRow] at the given [percentile] of the values that the given [rowExpression] returns
 * for each row of that group.
 *
 * @include [PercentileDocs.RowExpressionSnippet]
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 * @include [PercentileDocs.ReducedPivotGroupBySnippet]
 *
 * Don't confuse [percentileBy] with [percentileOf][PivotGroupBy.percentileOf], which returns the percentile
 * of the values the expression returns itself, instead of the row.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [medianBy][PivotGroupBy.medianBy] — the row at the median (50th percentile), per group.
 * - [PivotGroupBy reducing][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [PivotGroupBy].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person at the third quartile of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.percentileBy(75.0) { (weight ?: 0) / age }.with { name.firstName }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @include [PercentileDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to compute the value to compare the rows by.
 * @return A [ReducedPivotGroupBy] holding, per group,
 *   the row at the given percentile of the values [rowExpression] returns.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { percentileByOrNull(percentile, skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { percentileByOrNull(percentile, column, skipNaN) }

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking from each group
 * the [row][DataRow] at the given [percentile] of the values in the given [column].
 *
 * @include [PercentileDocs.ComparableInputValuesSnippet]
 *
 * @include [PercentileDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the percentile of cannot select a row, and produce `null` values instead.
 *
 * @include [PercentileDocs.ReducedPivotGroupBySnippet]
 *
 * Don't confuse [percentileBy] with [percentile][PivotGroupBy.percentile], which returns the percentile value
 * itself, instead of the row.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [medianBy][PivotGroupBy.medianBy] — the row at the median (50th percentile), per group.
 * - [PivotGroupBy reducing][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [PivotGroupBy].
 * - {@include [PercentileDocsLink]} — an overview of all `percentile` modes.
 *
 * For more information: {@include [DocumentationUrls.Percentile]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person at the first quartile of "age"
 * df.pivot { city }.groupBy { name.lastName }.percentileBy(25.0, "age").with { name.firstName }
 * ```
 *
 * @include [PercentileDocs.PercentileParam]
 * @param [column] The name of the column to compare the rows by.
 * @include [PercentileDocs.SkipNanParam]
 * @return A [ReducedPivotGroupBy] holding, per group, the row at the given percentile
 *   of the values in the given column.
 */
public fun <T> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    column: String,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = percentileBy(percentile, column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.percentileBy(
    percentile: Double,
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = percentileBy(percentile, column.toColumnAccessor(), skipNaN)

// endregion
