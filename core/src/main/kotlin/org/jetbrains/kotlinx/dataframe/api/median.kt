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
import org.jetbrains.kotlinx.dataframe.util.ROW_MEDIAN
import org.jetbrains.kotlinx.dataframe.util.ROW_MEDIAN_OR_NULL
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region docs

/**
 * {@comment
 *    The Median Operation KDoc-topic; it also holds all common `median` KDoc-snippets.
 *    Link to it with `{@include [MedianDocsLink]}`.
 *    The snippets `median` and `percentile` have in common live in [CommonMedianPercentileDocs].
 * }
 *
 * ## The Median Operation
 *
 * Computes the [median](https://en.wikipedia.org/wiki/Median) of values:
 * the value in the "middle" of the sorted values.
 * This is also called the 50th [percentile][DataFrame.percentile],
 * or the 2-[quantile](https://en.wikipedia.org/wiki/Quantile).
 *
 * @include [CommonMedianPercentileDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [CommonMedianPercentileDocs.BigNumbersSnippet]
 *
 * ### Median Modes
 *
 * Depending on what exactly you want the median of, there are several modes.
 * They are shown here for [DataFrame], but they exist for the other receivers too:
 *
 * - [`median`][DataFrame.median]`()` — the median of each suitable column separately.
 * - [`median`][DataFrame.median]` { columns }` — a single median of all values in all selected columns.
 * - [`medianFor`][DataFrame.medianFor]` { columns }` — the median of each selected column separately.
 * - [`medianOf`][DataFrame.medianOf]` { expression }` — the median of the values that the given expression
 *   returns for each row.
 * - [`medianBy`][DataFrame.medianBy]` { expression }` — the row at the median of the values that the given
 *   expression returns for each row.
 *
 * [`median`][DataFrame.median]` { columns }`, [`medianOf`][DataFrame.medianOf], and
 * [`medianBy`][DataFrame.medianBy] all have an `-OrNull` counterpart which returns `null` instead of
 * throwing an exception when there is nothing to compute the median of.
 *
 * Due to a limitation in Kotlin's overload resolution ({@include [Issues.OverloadResolutionByLambdaReturnTypeLink]}),
 * computing the median of non-number comparable values with [`median`][DataFrame.median]` { columns }` or
 * [`medianOf`][DataFrame.medianOf] requires either explicit type arguments, like
 * `df.median<_, String> { name.firstName }`, or passing the lambda inside the parentheses, like
 * `df.median({ name.firstName })`.
 *
 * @include [CommonMedianPercentileDocs.EagerLambdaAnalysisSnippet]
 *
 * Related operation:
 * - [`percentile`][DataFrame.percentile] — any percentile of values; `median` is the same as `percentile(50.0)`.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * See all summary statistics: {@include [DocumentationUrls.Statistics]}
 */
internal interface MedianDocs : CommonMedianPercentileDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetMedianOperationArg]}
     */
    typealias MedianSelectingOptions = Nothing

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetMedianForOperationArg]}
     */
    typealias MedianForSelectingOptions = Nothing

    /**
     * @include [CommonMedianPercentileDocs.ResultTypeSnippet]
     *
     * For more information about the resulting types: {@include [DocumentationUrls.Median.TypeConversion]}
     */
    @ExcludeFromSources
    typealias ResultTypeSnippet = Nothing

    /**
     * @include [CommonMedianPercentileDocs.NumberResultSnippet]
     *
     * For more information about the resulting types: {@include [DocumentationUrls.Median.TypeConversion]}
     */
    @ExcludeFromSources
    typealias NumberResultSnippet = Nothing

    /**
     * @include [CommonMedianPercentileDocs.ComparableResultSnippet]
     *
     * For more information about the resulting types: {@include [DocumentationUrls.Median.TypeConversion]}
     */
    @ExcludeFromSources
    typealias ComparableResultSnippet = Nothing

    /** @include [CommonMedianPercentileDocs.ThrowsOnEmptySnippet] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias ThrowsOnEmptySnippet = Nothing

    /** @include [CommonMedianPercentileDocs.NullOnEmptySnippet] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias NullOnEmptySnippet = Nothing

    /** @include [CommonMedianPercentileDocs.NullCellOnEmptySnippet] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias NullCellOnEmptySnippet = Nothing

    /** @include [CommonMedianPercentileDocs.NullCellOnEmptyPivotSnippet] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias NullCellOnEmptyPivotSnippet = Nothing

    /** @include [CommonMedianPercentileDocs.BySelectionSnippet] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias BySelectionSnippet = Nothing

    /** @include [CommonMedianPercentileDocs.AllComparableColumnsSnippet] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias AllComparableColumnsSnippet = Nothing

    /** @include [CommonStatisticsDocs.ColumnGroupsIgnoredSnippet] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias ColumnGroupsIgnoredSnippet = Nothing

    /** @include [CommonStatisticsDocs.ColumnsSelectorParam] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias ColumnsSelectorParam = Nothing

    /** @include [CommonStatisticsDocs.AggregateColumnsSelectorParam] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias AggregateColumnsSelectorParam = Nothing

    /** @include [CommonStatisticsDocs.ColumnNamesParam] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias ColumnNamesParam = Nothing

    /**
     * @include [CommonStatisticsDocs.ColumnNamesParam] {@include [SetMedianStatisticArgs]}
     *   The values in these columns must be mutually comparable, else an [IllegalStateException] is thrown.
     */
    @ExcludeFromSources
    typealias ComparableColumnNamesParam = Nothing

    /** @include [CommonStatisticsDocs.ExpressionParam] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias ExpressionParam = Nothing

    /** @include [CommonStatisticsDocs.RowValuesTypeParam] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias RowValuesTypeParam = Nothing

    /** @include [CommonStatisticsDocs.ResultColumnNameParam] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias ResultColumnNameParam = Nothing

    /** @include [CommonStatisticsDocs.ExpressionResultColumnNameParam] {@include [SetMedianStatisticArgs]} */
    @ExcludeFromSources
    typealias ExpressionResultColumnNameParam = Nothing

    /**
     * {@comment The parts all [DataFrame.medianFor] overloads have in common. KDoc-snippet.}
     *
     * Returns the median of the values of each selected column of this [DataFrame] separately.
     *
     * @include [MedianDocs.InputValuesSnippet]
     *
     * @include [MedianDocs.ResultTypeSnippet]
     *
     * @include [MedianDocs.NullCellOnEmptySnippet]
     *
     * $[NOTE]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MedianDocs.MedianForSelectingOptions].
     *
     * See also:
     * - [`median`][DataFrame.median]`()` — the same, but for all suitable columns at once.
     * - [`median`][DataFrame.median]` { columns }` — a single median of all values in the selected columns.
     * - [`percentileFor`][DataFrame.percentileFor] — any other percentile of each selected column.
     * - {@include [MedianDocsLink]} — an overview of all `median` modes.
     *
     * For more information: {@include [DocumentationUrls.Median]}
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface DataFrameMedianForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this medianFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [Grouped.medianFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [GroupBy] by computing the median of the values of
     * each selected column separately, per group.
     *
     * Returns a new [DataFrame] with one row per group, containing the group key columns
     * and a column with the median for each selected column.
     *
     * $[INPUT]
     *
     * @include [MedianDocs.ResultTypeSnippet]
     *
     * @include [MedianDocs.NullCellOnEmptySnippet]
     *
     * $[NOTE]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MedianDocs.MedianForSelectingOptions].
     *
     * See also:
     * - [`median`][Grouped.median]`()` — the same, but for all suitable columns at once.
     * - [`median`][Grouped.median]` { columns }` — a single median of all values in the selected columns,
     *   per group.
     * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
     * - {@include [MedianDocsLink]} — an overview of all `median` modes.
     *
     * @include [MedianDocs.GroupByUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface GroupedMedianForSnippet {

        // The note about the supported input values; differs on whether there's a `skipNaN` parameter
        typealias INPUT = Nothing

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this medianFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [Grouped.median] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [GroupBy] by computing a single median of all the values
     * in the selected columns, per group.
     *
     * Returns a new [DataFrame] with one row per group, containing the group key columns and
     * a single column with the median per group.
     * That column is named [name\], or, if [name\] is `null`, after the selected column
     * if exactly one column is selected, and `"median"` otherwise.
     *
     * @include [MedianDocs.InputValuesSnippet]
     *
     * @include [MedianDocs.ResultTypeSnippet]
     *
     * @include [MedianDocs.NullCellOnEmptySnippet]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MedianDocs.MedianSelectingOptions].
     *
     * See also:
     * - [`medianFor`][Grouped.medianFor] — the median of each selected column separately, per group.
     * - [`medianOf`][Grouped.medianOf] — the median of the values a row expression returns
     *   for each row of a group.
     * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
     * - {@include [MedianDocsLink]} — an overview of all `median` modes.
     *
     * @include [MedianDocs.GroupByUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface GroupedMedianSnippet {

        // The example to render for this median overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [Pivot.medianFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [Pivot] by computing the median of the values of
     * each selected column separately, per group.
     *
     * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the median
     * of each selected column of the corresponding group.
     *
     * @include [MedianDocs.InputValuesSnippet]
     *
     * @include [MedianDocs.ResultTypeSnippet]
     *
     * @include [MedianDocs.NullCellOnEmptyPivotSnippet]
     *
     * $[NOTE]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MedianDocs.MedianForSelectingOptions], or check out the
     * [`Pivot` Grammar][PivotDocs.Grammar].
     *
     * See also:
     * - [`median`][Pivot.median]`()` — the same, but for all suitable columns at once.
     * - [`median`][Pivot.median]` { columns }` — a single median of all values in the selected columns,
     *   per group.
     * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
     * - {@include [MedianDocsLink]} — an overview of all `median` modes.
     *
     * @include [MedianDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotMedianForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this medianFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [Pivot.median] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [Pivot] by computing a single median of all the values
     * in the selected columns, per group.
     *
     * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the median
     * of all the values in the selected columns of the corresponding group.
     *
     * @include [MedianDocs.InputValuesSnippet]
     *
     * @include [MedianDocs.ResultTypeSnippet]
     *
     * @include [MedianDocs.NullCellOnEmptyPivotSnippet]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MedianDocs.MedianSelectingOptions], or check out the
     * [`Pivot` Grammar][PivotDocs.Grammar].
     *
     * See also:
     * - [`median`][Pivot.median]`()` — the median of each suitable column separately, per group.
     * - [`medianFor`][Pivot.medianFor] — the median of each selected column separately, per group.
     * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
     * - {@include [MedianDocsLink]} — an overview of all `median` modes.
     *
     * @include [MedianDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotMedianSnippet {

        // The example to render for this median overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [PivotGroupBy.medianFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [PivotGroupBy] by computing the median of the values of
     * each selected column separately, per group.
     *
     * Returns a [DataFrame] where each cell contains the median of each selected column
     * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
     *
     * @include [MedianDocs.InputValuesSnippet]
     *
     * @include [MedianDocs.ResultTypeSnippet]
     *
     * @include [MedianDocs.NullCellOnEmptyPivotSnippet]
     *
     * $[NOTE]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MedianDocs.MedianForSelectingOptions], or check out the
     * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
     *
     * See also:
     * - [`median`][PivotGroupBy.median]`()` — the same, but for all suitable columns at once.
     * - [`median`][PivotGroupBy.median]` { columns }` — a single median of all values in the
     *   selected columns, per group.
     * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
     *   a [PivotGroupBy].
     * - {@include [MedianDocsLink]} — an overview of all `median` modes.
     *
     * @include [MedianDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotGroupByMedianForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this medianFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [PivotGroupBy.median] overloads have in common.
     *    KDoc-snippet.}
     *
     * Aggregates this [PivotGroupBy] by computing a single median of all the values
     * in the selected columns, per group.
     *
     * Returns a [DataFrame] where each cell contains the median of all the values in the
     * selected columns of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
     *
     * @include [MedianDocs.InputValuesSnippet]
     *
     * @include [MedianDocs.ResultTypeSnippet]
     *
     * @include [MedianDocs.NullCellOnEmptyPivotSnippet]
     *
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MedianDocs.MedianSelectingOptions], or check out the
     * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
     *
     * See also:
     * - [`median`][PivotGroupBy.median]`()` — the median of each suitable column separately, per group.
     * - [`medianFor`][PivotGroupBy.medianFor] — the median of each selected column separately, per group.
     * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
     *   a [PivotGroupBy].
     * - {@include [MedianDocsLink]} — an overview of all `median` modes.
     *
     * @include [MedianDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotGroupByMedianSnippet {

        // The example to render for this median overload
        typealias EXAMPLE = Nothing
    }
}

/** [The Median Operation][MedianDocs] */
@ExcludeFromSources
private typealias MedianDocsLink = Nothing

/** {@set [STATISTIC] median}{@set [STATISTIC_VERB] include}{@set [STATISTIC_COLUMN_NAME] `"median"`} */
@ExcludeFromSources
private typealias SetMedianStatisticArgs = Nothing

/** {@set [SelectingColumns.OPERATION] [median][median]} */
@ExcludeFromSources
private typealias SetMedianOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [medianFor][medianFor]} */
@ExcludeFromSources
private typealias SetMedianForOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [medianOrNull][medianOrNull]} */
@ExcludeFromSources
private typealias SetMedianOrNullOperationArg = Nothing

// endregion

// region DataColumn

/**
 * Returns the median of the comparable values in this [DataColumn].
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * Columns of primitive numbers use the [median][DataColumn.median] overload with a `skipNaN` parameter,
 * which returns an interpolated [Double].
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * See also:
 * - [medianOrNull][DataColumn.medianOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [medianOf][DataColumn.medianOf] — the median of the values an expression returns for each element.
 * - [medianBy][DataColumn.medianBy] — the element at the median of the values a selector returns.
 * - [percentile][DataColumn.percentile] — any other percentile of the values in this column.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The median first name in the "name"/"firstName" column (in alphabetical order)
 * df.name.firstName.median()
 * ```
 *
 * @return The median of the values in this column.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public fun <T : Comparable<T & Any>?> DataColumn<T>.median(): T & Any = medianOrNull().suggestIfNull("median")

/**
 * Returns the median of the comparable values in this [DataColumn],
 * or `null` if there is nothing to compute the median of.
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * Columns of primitive numbers use the [medianOrNull][DataColumn.medianOrNull] overload with a `skipNaN`
 * parameter, which returns an interpolated [Double].
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * See also:
 * - [median][DataColumn.median] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [medianOfOrNull][DataColumn.medianOfOrNull] — the median of the values an expression returns
 *   for each element.
 * - [medianByOrNull][DataColumn.medianByOrNull] — the element at the median of the values a selector returns.
 * - [percentileOrNull][DataColumn.percentileOrNull] — any other percentile of the values in this column.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The median city in the "city" column (in alphabetical order),
 * // or `null` if the column contains no values other than `null`
 * df.city.medianOrNull()
 * ```
 *
 * @return The median of the values in this column, or `null` if there are no values to compute the median of.
 */
public fun <T : Comparable<T & Any>?> DataColumn<T>.medianOrNull(): T? =
    Aggregators.medianComparables<T>().aggregateSingleColumn(this)

/**
 * Returns the median of the numbers in this [DataColumn], as a [Double].
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * See also:
 * - [medianOrNull][DataColumn.medianOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [medianOf][DataColumn.medianOf] — the median of the values an expression returns for each element.
 * - [medianBy][DataColumn.medianBy] — the element at the median of the values a selector returns.
 * - [percentile][DataColumn.percentile] — any other percentile of the values in this column.
 * - [mean][DataColumn.mean] — the average of the values in this column.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The median age in the "age" column
 * df.age.median()
 * // The median weight in the "weight" column, ignoring `NaN` values
 * df.weight.median(skipNaN = true)
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @return The median of the values in this column, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public fun <T> DataColumn<T>.median(
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T & Any>?, T : Number? = medianOrNull(skipNaN = skipNaN).suggestIfNull("median")

/**
 * Returns the median of the numbers in this [DataColumn], as a [Double],
 * or `null` if there is nothing to compute the median of.
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * See also:
 * - [median][DataColumn.median] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [medianOfOrNull][DataColumn.medianOfOrNull] — the median of the values an expression returns
 *   for each element.
 * - [medianByOrNull][DataColumn.medianByOrNull] — the element at the median of the values a selector returns.
 * - [percentileOrNull][DataColumn.percentileOrNull] — any other percentile of the values in this column.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The median weight in the "weight" column,
 * // or `null` if the column contains no values other than `null`
 * df.weight.medianOrNull()
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @return The median of the values in this column, as a [Double],
 *   or `null` if there are no values to compute the median of.
 */
public fun <T> DataColumn<T>.medianOrNull(
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T & Any>?, T : Number? =
    Aggregators.medianNumbers<T>(skipNaN).aggregateSingleColumn(this)

/**
 * Returns the element of this [DataColumn] at the median of the values that the given [selector]
 * returns for each element.
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * Don't confuse [medianBy] with [medianOf][DataColumn.medianOf], which returns the median [selector] value itself
 * instead of the element it belongs to.
 *
 * See also:
 * - [medianByOrNull][DataColumn.medianByOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [percentileBy][DataColumn.percentileBy] — the element at any other percentile of the values a selector returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The first name of median length in the "name"/"firstName" column
 * df.name.firstName.medianBy { it.length }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The element at the median of the values [selector] returns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T & Any = medianByOrNull(skipNaN, selector).suggestIfNull("medianBy")

/**
 * Returns the element of this [DataColumn] at the median of the values that the given [selector]
 * returns for each element, or `null` if there is nothing to compute the median of.
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * Don't confuse [medianByOrNull] with [medianOfOrNull][DataColumn.medianOfOrNull], which returns the median
 * [selector] value itself instead of the element it belongs to.
 *
 * See also:
 * - [medianBy][DataColumn.medianBy] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [percentileByOrNull][DataColumn.percentileByOrNull] — the element at any other percentile of the values
 *   a selector returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The first name of median length in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.medianByOrNull { it.length }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @param [selector] A function that returns the value to compare for each element of this column.
 * @return The element at the median of the values [selector] returns,
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline selector: (T) -> R,
): T? = Aggregators.medianCommon<R>(skipNaN).aggregateByOrNull(this, selector)

/**
 * Returns the median of the comparable values that the given [expression] returns
 * for each element of this [DataColumn].
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [medianOf][DataColumn.medianOf] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [Double].
 *
 * @include [MedianDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * Don't confuse [medianOf] with [medianBy][DataColumn.medianBy], which returns the element the median
 * [expression] value belongs to instead of that value.
 *
 * See also:
 * - [medianOfOrNull][DataColumn.medianOfOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [percentileOf][DataColumn.percentileOf] — any other percentile of the values an expression returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Examples
 * ```kotlin
 * // The median of all last names in the "name" column group, in upper case
 * df.name.medianOf<_, String> { it.lastName.uppercase() }
 * // The same, with the lambda inside the parentheses
 * df.name.medianOf({ it.lastName.uppercase() })
 * ```
 *
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The median of the values [expression] returns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianOf(
    crossinline expression: (T) -> R,
): R & Any = medianOfOrNull(expression).suggestIfNull("medianOf")

/**
 * Returns the median of the comparable values that the given [expression] returns
 * for each element of this [DataColumn], or `null` if there is nothing to compute the median of.
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [medianOfOrNull][DataColumn.medianOfOrNull] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [Double].
 *
 * @include [MedianDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * Don't confuse [medianOfOrNull] with [medianByOrNull][DataColumn.medianByOrNull], which returns the element
 * the median [expression] value belongs to instead of that value.
 *
 * See also:
 * - [medianOf][DataColumn.medianOf] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [percentileOfOrNull][DataColumn.percentileOfOrNull] — any other percentile of the values
 *   an expression returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Examples
 * ```kotlin
 * // The median of all last names in the "name" column group, in upper case,
 * // or `null` if the column group is empty
 * df.name.medianOfOrNull<_, String> { it.lastName.uppercase() }
 * // The same, with the lambda inside the parentheses
 * df.name.medianOfOrNull({ it.lastName.uppercase() })
 * ```
 *
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The median of the values [expression] returns,
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataColumn<T>.medianOfOrNull(
    crossinline expression: (T) -> R,
): R? = Aggregators.medianComparables<R>().aggregateOf(this, expression)

/**
 * Returns the median of the numbers that the given [expression] returns
 * for each element of this [DataColumn], as a [Double].
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * Don't confuse [medianOf] with [medianBy][DataColumn.medianBy], which returns the element the median
 * [expression] value belongs to instead of that value.
 *
 * See also:
 * - [medianOfOrNull][DataColumn.medianOfOrNull] — returns `null` instead of throwing for a column
 *   with nothing to compute the median of.
 * - [percentileOf][DataColumn.percentileOf] — any other percentile of the values an expression returns.
 * - [meanOf][DataColumn.meanOf] — the average of the values an expression returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The median length of all first names in the "name"/"firstName" column
 * df.name.firstName.medianOf { it.length }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The median of the values [expression] returns, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    medianOfOrNull(skipNaN, expression).suggestIfNull("medianOf")

/**
 * Returns the median of the numbers that the given [expression] returns
 * for each element of this [DataColumn], as a [Double],
 * or `null` if there is nothing to compute the median of.
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * Don't confuse [medianOfOrNull] with [medianByOrNull][DataColumn.medianByOrNull], which returns the element
 * the median [expression] value belongs to instead of that value.
 *
 * See also:
 * - [medianOf][DataColumn.medianOf] — throws instead of returning `null` for a column
 *   with nothing to compute the median of.
 * - [percentileOfOrNull][DataColumn.percentileOfOrNull] — any other percentile of the values
 *   an expression returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The median length of all first names in the "name"/"firstName" column,
 * // or `null` if the column is empty
 * df.name.firstName.medianOfOrNull { it.length }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @param [expression] A function that returns the value to include for each element of this column.
 * @return The median of the values [expression] returns, as a [Double],
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataColumn<T>.medianOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.medianNumbers<R>(skipNaN).aggregateOf(this, expression)

// endregion

// region DataRow

@Deprecated(ROW_MEDIAN_OR_NULL, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowMedianOrNull(): Nothing? = error(ROW_MEDIAN_OR_NULL)

@Deprecated(ROW_MEDIAN, level = DeprecationLevel.ERROR)
public fun DataRow<*>.rowMedian(): Nothing = error(ROW_MEDIAN)

/**
 * Returns the median of the comparable values of type [T] in this [DataRow],
 * or `null` if there is nothing to compute the median of.
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 * @include [MedianDocs.ColumnGroupsIgnoredSnippet]
 *
 * This overload is meant for non-number self-comparable types, like [String] or dates.
 *
 * @include [MedianDocs.RowComparableOverloadResolutionSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * See also:
 * - [rowMedianOf][DataRow.rowMedianOf] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [medianOrNull][DataFrame.medianOrNull] — the median of the values in specific columns of a [DataFrame].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The median of all `String` values ("name"/"firstName", "name"/"lastName", and "city") in the first row,
 * // or `null` if there are none
 * df[0].rowMedianOfOrNull<String>()
 * ```
 *
 * @include [MedianDocs.RowValuesTypeParam]
 * @return The median of the values of type [T] in this row,
 *   or `null` if there are no values to compute the median of.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMedianOfOrNull(): T? =
    Aggregators.medianComparables<T>().aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the median of the comparable values of type [T] in this [DataRow].
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 * @include [MedianDocs.ColumnGroupsIgnoredSnippet]
 *
 * This overload is meant for non-number self-comparable types, like [String] or dates.
 *
 * @include [MedianDocs.RowComparableOverloadResolutionSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * See also:
 * - [rowMedianOfOrNull][DataRow.rowMedianOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compute the median of.
 * - [median][DataFrame.median] — the median of the values in specific columns of a [DataFrame].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The median of all `String` values ("name"/"firstName", "name"/"lastName", and "city") in the first row
 * df[0].rowMedianOf<String>()
 * ```
 *
 * @include [MedianDocs.RowValuesTypeParam]
 * @return The median of the values of type [T] in this row.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public inline fun <reified T : Comparable<T>> DataRow<*>.rowMedianOf(): T =
    rowMedianOfOrNull<T>().suggestIfNull("rowMedianOf")

/**
 * Returns the median of the numbers of type [T] in this [DataRow], as a [Double],
 * or `null` if there is nothing to compute the median of.
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 * @include [MedianDocs.ColumnGroupsIgnoredSnippet]
 *
 * @include [MedianDocs.RowNumberOverloadResolutionSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * See also:
 * - [rowMedianOf][DataRow.rowMedianOf] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [medianOrNull][DataFrame.medianOrNull] — the median of the values in specific columns of a [DataFrame].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The median of all `Int` values ("age" and "weight") in the first row, or `null` if there are none
 * df[0].rowMedianOfOrNull<Int>(skipNaN = false)
 * ```
 *
 * @include [MedianDocs.RowValuesTypeParam]
 * @include [MedianDocs.SkipNanParam]
 * @return The median of the values of type [T] in this row, as a [Double],
 *   or `null` if there are no values to compute the median of.
 */
public inline fun <reified T> DataRow<*>.rowMedianOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where T : Comparable<T>, T : Number =
    Aggregators.medianNumbers<T>(skipNaN).aggregateOfRow(this) { colsOf<T?>() }

/**
 * Returns the median of the numbers of type [T] in this [DataRow], as a [Double].
 *
 * Only the values in the columns of type [T] (or `T?`) are taken into account;
 * all other columns of the row are ignored.
 * @include [MedianDocs.ColumnGroupsIgnoredSnippet]
 *
 * @include [MedianDocs.RowNumberOverloadResolutionSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * See also:
 * - [rowMedianOfOrNull][DataRow.rowMedianOfOrNull] — returns `null` instead of throwing
 *   when there's nothing to compute the median of.
 * - [rowMean][DataRow.rowMean] — the average of all the numbers in this row.
 * - [median][DataFrame.median] — the median of the values in specific columns of a [DataFrame].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The median of all `Int` values ("age" and "weight") in the first row
 * df[0].rowMedianOf<Int>(skipNaN = false)
 * // The median of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowMedianOf<Double>(skipNaN = true)
 * ```
 *
 * @include [MedianDocs.RowValuesTypeParam]
 * @include [MedianDocs.SkipNanParam]
 * @return The median of the values of type [T] in this row, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public inline fun <reified T> DataRow<*>.rowMedianOf(
    skipNaN: Boolean = skipNaNDefault,
): Double
    where T : Comparable<T>, T : Number = rowMedianOfOrNull<T>(skipNaN).suggestIfNull("rowMedianOf")

// endregion

// region DataFrame

/**
 * Returns the median of the values of each suitable column of this [DataFrame] separately.
 *
 * @include [MedianDocs.AllComparableColumnsSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [MedianDocs.NullCellOnEmptySnippet]
 *
 * See also:
 * - [medianFor][DataFrame.medianFor] — the same, but for an explicit selection of columns.
 * - [median][DataFrame.median]` { columns }` — a single median of all values in the selected columns.
 * - [percentile][DataFrame.percentile] — any other percentile of each column.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the median value of each comparable column
 * // ("name"/"firstName", "name"/"lastName", "age", "city", "weight", and "isHappy")
 * df.median()
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @return A single [DataRow] with the median of each suitable column of this [DataFrame].
 */
@Refine
@Interpretable("Median0")
public fun <T> DataFrame<T>.median(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianFor(skipNaN, intraComparableColumns())

/**
 * @include [MedianDocs.DataFrameMedianForSnippet]
 * @set [MedianDocs.DataFrameMedianForSnippet.NOTE] {@include [MedianDocs.AggregateColumnsSelectorSnippet]}
 * @set [MedianDocs.DataFrameMedianForSnippet.EXAMPLE]
 * ```kotlin
 * // A single row with the median "age" and the median "weight"
 * df.medianFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.medianFor(skipNaN = true) { age into "medianAge" and (weight into "medianWeight") }
 * ```
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.AggregateColumnsSelectorParam]
 * @return A single [DataRow] with the median of each selected column.
 */
@Refine
@Interpretable("Median1")
public fun <T, C : Comparable<*>?> DataFrame<T>.medianFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.median.invoke(skipNaN).aggregateFor(this, columns)

/**
 * @include [MedianDocs.DataFrameMedianForSnippet]
 * @set [MedianDocs.DataFrameMedianForSnippet.EXAMPLE]
 * ```kotlin
 * // A single row with the median "age" and the median "weight"
 * df.medianFor("age", "weight")
 * ```
 * @include [MedianDocs.ColumnNamesParam]
 * @include [MedianDocs.SkipNanParam]
 * @return A single [DataRow] with the median of each selected column.
 */
@Refine
@StringApiInterpretable(interpreter = "Median1", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.medianFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianFor(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.medianFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> DataFrame<T>.medianFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(skipNaN) { columns.toColumnSet() }

/**
 * Returns a single median of all the comparable values in the selected columns of this [DataFrame].
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * For columns of primitive numbers, the [median][DataFrame.median] overload with a `skipNaN` parameter is used,
 * which returns an interpolated [Double].
 *
 * @include [MedianDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [medianOrNull][DataFrame.medianOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [medianFor][DataFrame.medianFor] — the median of each selected column separately.
 * - [medianOf][DataFrame.medianOf] — the median of the values a row expression returns for each row.
 * - [percentile][DataFrame.percentile] — any other percentile of all values in the selected columns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetMedianOperationArg]}
 *
 * ### Examples
 * ```kotlin
 * // The median of all first and last names in the "name" column group
 * df.median<_, String> { name.firstName and name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.median({ name.firstName and name.lastName })
 * ```
 *
 * @include [MedianDocs.ColumnsSelectorParam]
 * @return The median of all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.median(columns: ColumnsSelector<T, C>): C & Any =
    medianOrNull(columns).suggestIfNull("median")

/**
 * Returns a single median of all the comparable values in the selected columns of this [DataFrame],
 * or `null` if there is nothing to compute the median of.
 *
 * This overload is meant for non-number self-comparable values, like strings or dates.
 * For columns of primitive numbers, the [medianOrNull][DataFrame.medianOrNull] overload with a `skipNaN`
 * parameter is used, which returns an interpolated [Double].
 *
 * @include [MedianDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [median][DataFrame.median] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [medianFor][DataFrame.medianFor] — the median of each selected column separately.
 * - [medianOfOrNull][DataFrame.medianOfOrNull] — the median of the values a row expression
 *   returns for each row.
 * - [percentileOrNull][DataFrame.percentileOrNull] — any other percentile of all values in the selected columns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetMedianOrNullOperationArg]}
 *
 * ### Examples
 * ```kotlin
 * // The median of all first and last names in the "name" column group,
 * // or `null` if there are no values to compute the median of
 * df.medianOrNull<_, String> { name.firstName and name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.medianOrNull({ name.firstName and name.lastName })
 * ```
 *
 * @include [MedianDocs.ColumnsSelectorParam]
 * @return The median of all the values in the selected columns,
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.medianOrNull(columns: ColumnsSelector<T, C>): C? =
    Aggregators.medianComparables<C>().aggregateAll(this, columns)

/**
 * Returns a single median of all the numbers in the selected columns of this [DataFrame], as a [Double].
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [medianOrNull][DataFrame.medianOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [medianFor][DataFrame.medianFor] — the median of each selected column separately.
 * - [medianOf][DataFrame.medianOf] — the median of the values a row expression returns for each row.
 * - [percentile][DataFrame.percentile] — any other percentile of all values in the selected columns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetMedianOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The median of all values in the "age" and "weight" columns
 * df.median { age and weight }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ColumnsSelectorParam]
 * @return The median of all the values in the selected columns, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public fun <T, C> DataFrame<T>.median(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double
    where C : Number?, C : Comparable<C & Any>? = medianOrNull(skipNaN, columns).suggestIfNull("median")

/**
 * Returns a single median of all the numbers in the selected columns of this [DataFrame], as a [Double],
 * or `null` if there is nothing to compute the median of.
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [median][DataFrame.median] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [medianFor][DataFrame.medianFor] — the median of each selected column separately.
 * - [medianOfOrNull][DataFrame.medianOfOrNull] — the median of the values a row expression
 *   returns for each row.
 * - [percentileOrNull][DataFrame.percentileOrNull] — any other percentile of all values in the selected columns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * @include [SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample] {@include [SetMedianOrNullOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The median of all values in the "age" and "weight" columns,
 * // or `null` if there are no values to compute the median of
 * df.medianOrNull { age and weight }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ColumnsSelectorParam]
 * @return The median of all the values in the selected columns, as a [Double],
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
@Suppress("UNCHECKED_CAST")
public fun <T, C> DataFrame<T>.medianOrNull(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double?
    where C : Comparable<C & Any>?, C : Number? =
    Aggregators.medianNumbers<C>(skipNaN).aggregateAll(this, columns)

/**
 * Returns a single median of all the values in the columns of this [DataFrame] with the given names.
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [medianOrNull][DataFrame.medianOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [medianFor][DataFrame.medianFor] — the median of each selected column separately.
 * - [percentile][DataFrame.percentile] — any other percentile of all values in the selected columns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * @include [SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample] {@include [SetMedianOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The median of all values in the "age" and "weight" columns, as a `Double`
 * df.median("age", "weight")
 * ```
 *
 * @include [MedianDocs.ComparableColumnNamesParam]
 * @include [MedianDocs.SkipNanParam]
 * @return The median of all the values in the selected columns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public fun <T> DataFrame<T>.median(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Any =
    medianOrNull(*columns, skipNaN = skipNaN).suggestIfNull("median")

/**
 * Returns a single median of all the values in the columns of this [DataFrame] with the given names,
 * or `null` if there is nothing to compute the median of.
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See also:
 * - [median][DataFrame.median] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [medianFor][DataFrame.medianFor] — the median of each selected column separately.
 * - [percentileOrNull][DataFrame.percentileOrNull] — any other percentile of all values in the selected columns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * @include [SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample] {@include [SetMedianOrNullOperationArg]}
 *
 * ### Example
 * ```kotlin
 * // The median of all values in the "age" and "weight" columns, as a `Double`,
 * // or `null` if there are no values to compute the median of
 * df.medianOrNull("age", "weight")
 * ```
 *
 * @include [MedianDocs.ComparableColumnNamesParam]
 * @include [MedianDocs.SkipNanParam]
 * @return The median of all the values in the selected columns,
 *   or `null` if there are no values to compute the median of.
 */
public fun <T> DataFrame<T>.medianOrNull(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Any? =
    Aggregators.medianCommon<Comparable<Any>?>(skipNaN).aggregateAll(this) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.median(vararg columns: ColumnReference<C>): C & Any =
    medianOrNull(*columns).suggestIfNull("median")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.medianOrNull(vararg columns: ColumnReference<C>): C? =
    medianOrNull<T, C> { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.median(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where C : Comparable<C & Any>?, C : Number? =
    medianOrNull(*columns, skipNaN = skipNaN).suggestIfNull("median")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.medianOrNull(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where C : Comparable<C & Any>?, C : Number? = medianOrNull(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.median(vararg columns: KProperty<C>): C & Any =
    medianOrNull(*columns).suggestIfNull("median")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> DataFrame<T>.medianOrNull(vararg columns: KProperty<C>): C? =
    medianOrNull<T, C> { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.median(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double
    where C : Comparable<C & Any>?, C : Number? =
    medianOrNull(*columns, skipNaN = skipNaN).suggestIfNull("median")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.medianOrNull(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double?
    where C : Comparable<C & Any>?, C : Number? = medianOrNull(skipNaN) { columns.toColumnSet() }

/**
 * Returns the median of the comparable values that the given [expression] returns
 * for each row of this [DataFrame].
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [medianOf][DataFrame.medianOf] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [Double].
 *
 * @include [MedianDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * Don't confuse [medianOf] with [medianBy][DataFrame.medianBy], which returns the row the median
 * [expression] value belongs to instead of that value.
 *
 * See also:
 * - [medianOfOrNull][DataFrame.medianOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [median][DataFrame.median] — a single median of all values in the selected columns.
 * - [percentileOf][DataFrame.percentileOf] — any other percentile of the values a row expression returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Examples
 * ```kotlin
 * // The median of the full names of all rows
 * df.medianOf<_, String> { name.firstName + " " + name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.medianOf({ name.firstName + " " + name.lastName })
 * ```
 *
 * @include [MedianDocs.ExpressionParam]
 * @return The median of the values [expression] returns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.medianOf(
    crossinline expression: RowExpression<T, R>,
): R & Any = medianOfOrNull(expression).suggestIfNull("medianOf")

/**
 * Returns the median of the comparable values that the given [expression] returns
 * for each row of this [DataFrame], or `null` if there is nothing to compute the median of.
 *
 * This overload is meant for expressions returning non-number self-comparable values, like strings or dates.
 * For expressions returning primitive numbers, the [medianOfOrNull][DataFrame.medianOfOrNull] overload
 * with a `skipNaN` parameter is used, which returns an interpolated [Double].
 *
 * @include [MedianDocs.ExplicitTypeArgumentsSnippet]
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.ComparableResultSnippet]
 *
 * Don't confuse [medianOfOrNull] with [medianByOrNull][DataFrame.medianByOrNull], which returns the row the
 * median [expression] value belongs to instead of that value.
 *
 * See also:
 * - [medianOf][DataFrame.medianOf] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [medianOrNull][DataFrame.medianOrNull] — a single median of all values in the selected columns.
 * - [percentileOfOrNull][DataFrame.percentileOfOrNull] — any other percentile of the values
 *   a row expression returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Examples
 * ```kotlin
 * // The median of the full names of all rows, or `null` if this dataframe is empty
 * df.medianOfOrNull<_, String> { name.firstName + " " + name.lastName }
 * // The same, with the lambda inside the parentheses
 * df.medianOfOrNull({ name.firstName + " " + name.lastName })
 * ```
 *
 * @include [MedianDocs.ExpressionParam]
 * @return The median of the values [expression] returns,
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R : Comparable<R & Any>?> DataFrame<T>.medianOfOrNull(
    crossinline expression: RowExpression<T, R>,
): R? = Aggregators.medianComparables<R>().aggregateOf(this, expression)

/**
 * Returns the median of the numbers that the given [expression] returns
 * for each row of this [DataFrame], as a [Double].
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * Don't confuse [medianOf] with [medianBy][DataFrame.medianBy], which returns the row the median
 * [expression] value belongs to instead of that value.
 *
 * See also:
 * - [medianOfOrNull][DataFrame.medianOfOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [median][DataFrame.median] — a single median of all values in the selected columns.
 * - [percentileOf][DataFrame.percentileOf] — any other percentile of the values a row expression returns.
 * - [meanOf][DataFrame.meanOf] — the average of the values a row expression returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The median weight-to-age ratio of all rows
 * df.medianOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ExpressionParam]
 * @return The median of the values [expression] returns, as a [Double].
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double
    where R : Comparable<R & Any>?, R : Number? =
    medianOfOrNull(skipNaN, expression).suggestIfNull("medianOf")

/**
 * Returns the median of the numbers that the given [expression] returns
 * for each row of this [DataFrame], as a [Double], or `null` if there is nothing to compute the median of.
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * @include [MedianDocs.NumberResultSnippet]
 *
 * Don't confuse [medianOfOrNull] with [medianByOrNull][DataFrame.medianByOrNull], which returns the row the
 * median [expression] value belongs to instead of that value.
 *
 * See also:
 * - [medianOf][DataFrame.medianOf] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [medianOrNull][DataFrame.medianOrNull] — a single median of all values in the selected columns.
 * - [percentileOfOrNull][DataFrame.percentileOfOrNull] — any other percentile of the values
 *   a row expression returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The median weight-to-age ratio of all rows, or `null` if this dataframe is empty
 * df.medianOfOrNull { (weight ?: 0) / age }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ExpressionParam]
 * @return The median of the values [expression] returns, as a [Double],
 *   or `null` if there are no values to compute the median of.
 */
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified R> DataFrame<T>.medianOfOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): Double?
    where R : Comparable<R & Any>?, R : Number? =
    Aggregators.medianNumbers<R>(skipNaN).aggregateOf(this, expression)

/**
 * Returns the row of this [DataFrame] at the median of the values that the given [expression]
 * returns for each row.
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * Don't confuse [medianBy] with [medianOf][DataFrame.medianOf], which returns the median [expression] value
 * itself instead of the row it belongs to.
 *
 * See also:
 * - [medianByOrNull][DataFrame.medianByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [percentileBy][DataFrame.percentileBy] — the row at any other percentile of the values
 *   a row expression returns.
 * - [sortBy][DataFrame.sortBy] — orders all rows instead of taking just the middle one.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The row with the median "age"
 * df.medianBy { age }
 * // The row with the median weight-to-age ratio
 * df.medianBy { (weight ?: 0) / age }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to compare the rows by.
 * @return The [DataRow] at the median of the values [expression] returns.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T> = medianByOrNull(skipNaN, expression).suggestIfNull("medianBy")

/**
 * Returns the row of this [DataFrame] at the median of the values in the column with the given name.
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * @include [MedianDocs.ThrowsOnEmptySnippet]
 *
 * Don't confuse [medianBy] with [median][DataFrame.median], which returns the median value itself
 * instead of the row it belongs to.
 *
 * See also:
 * - [medianByOrNull][DataFrame.medianByOrNull] — returns `null` instead of throwing when there's
 *   nothing to compute the median of.
 * - [percentileBy][DataFrame.percentileBy] — the row at any other percentile of the values in a column.
 * - [sortBy][DataFrame.sortBy] — orders all rows instead of taking just the middle one.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The row with the median "age"
 * df.medianBy("age")
 * ```
 *
 * @param [column] The name of the column of this [DataFrame] to compare the rows by.
 * @include [MedianDocs.SkipNanParam]
 * @return The [DataRow] at the median of the values in the given column.
 * @throws NoSuchElementException if there are no values to compute the median of.
 */
public fun <T> DataFrame<T>.medianBy(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianByOrNull(column, skipNaN).suggestIfNull("medianBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianByOrNull(column, skipNaN).suggestIfNull("medianBy")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianByOrNull(column, skipNaN).suggestIfNull("medianBy")

/**
 * Returns the row of this [DataFrame] at the median of the values that the given [expression]
 * returns for each row, or `null` if there is nothing to compute the median of.
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * Don't confuse [medianByOrNull] with [medianOfOrNull][DataFrame.medianOfOrNull], which returns the median
 * [expression] value itself instead of the row it belongs to.
 *
 * See also:
 * - [medianBy][DataFrame.medianBy] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [percentileByOrNull][DataFrame.percentileByOrNull] — the row at any other percentile of the values
 *   a row expression returns.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The row with the median "age", or `null` if this dataframe is empty
 * df.medianByOrNull { age }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @param [expression] The [RowExpression] to compute the value to compare the rows by.
 * @return The [DataRow] at the median of the values [expression] returns,
 *   or `null` if there are no values to compute the median of.
 */
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianByOrNull(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): DataRow<T>? = Aggregators.medianCommon<C>(skipNaN).aggregateByOrNull(this, expression)

/**
 * Returns the row of this [DataFrame] at the median of the values in the column with the given name,
 * or `null` if there is nothing to compute the median of.
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * @include [MedianDocs.NullOnEmptySnippet]
 *
 * Don't confuse [medianByOrNull] with [medianOrNull][DataFrame.medianOrNull], which returns the median
 * value itself instead of the row it belongs to.
 *
 * See also:
 * - [medianBy][DataFrame.medianBy] — throws instead of returning `null` when there's nothing
 *   to compute the median of.
 * - [percentileByOrNull][DataFrame.percentileByOrNull] — the row at any other percentile of the values
 *   in a column.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // The row with the median "age", or `null` if this dataframe is empty
 * df.medianByOrNull("age")
 * ```
 *
 * @param [column] The name of the column of this [DataFrame] to compare the rows by.
 * @include [MedianDocs.SkipNanParam]
 * @return The [DataRow] at the median of the values in the given column,
 *   or `null` if there are no values to compute the median of.
 */
public fun <T> DataFrame<T>.medianByOrNull(column: String, skipNaN: Boolean = skipNaNDefault): DataRow<T>? =
    medianByOrNull(column.toColumnOf<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianByOrNull(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = Aggregators.medianCommon<C>(skipNaN).aggregateByOrNull(this, column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> DataFrame<T>.medianByOrNull(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T>? = medianByOrNull(column.toColumnAccessor(), skipNaN)

// endregion

// region GroupBy

/**
 * Aggregates this [GroupBy] by computing the median of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the median for each suitable column.
 *
 * @include [MedianDocs.AllComparableColumnsSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [MedianDocs.NullCellOnEmptySnippet]
 *
 * See also:
 * - [medianFor][Grouped.medianFor] — the same, but for an explicit selection of columns.
 * - [median][Grouped.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [percentile][Grouped.percentile] — any other percentile of each column, per group.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * @include [MedianDocs.GroupByUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the median value of each comparable column
 * df.groupBy { city }.median()
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and the median of each suitable column per group.
 */
@Refine
@Interpretable("GroupByMedian1")
public fun <T> Grouped<T>.median(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    medianFor(skipNaN, intraComparableColumns())

/**
 * @include [MedianDocs.GroupedMedianForSnippet]
 * @set [MedianDocs.GroupedMedianForSnippet.INPUT] {@include [MedianDocs.InputValuesSnippet]}
 * @set [MedianDocs.GroupedMedianForSnippet.NOTE] {@include [MedianDocs.AggregateColumnsSelectorSnippet]}
 * @set [MedianDocs.GroupedMedianForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the median "age" and the median "weight"
 * df.groupBy { city }.medianFor { age and weight }
 * ```
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.AggregateColumnsSelectorParam]
 * @return A new [DataFrame] with the group keys and the median of each selected column per group.
 */
@Refine
@Interpretable("GroupByMedian0")
public fun <T, C : Comparable<*>?> Grouped<T>.medianFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.median.invoke(skipNaN).aggregateFor(this, columns)

/**
 * @include [MedianDocs.GroupedMedianForSnippet]
 * @set [MedianDocs.GroupedMedianForSnippet.INPUT] {@include [MedianDocs.ComparableInputValuesSnippet]}
 * @set [MedianDocs.GroupedMedianForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the median "age" and the median "weight"
 * df.groupBy { city }.medianFor("age", "weight")
 * ```
 * @include [MedianDocs.ColumnNamesParam]
 * @return A new [DataFrame] with the group keys and the median of each selected column per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByMedian0", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.medianFor(vararg columns: String): DataFrame<T> = medianFor { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.medianFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Grouped<T>.medianFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(skipNaN) { columns.toColumnSet() }

/**
 * @include [MedianDocs.GroupedMedianSnippet]
 * @set [MedianDocs.GroupedMedianSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the median of all values in the "age" and "weight" columns,
 * // in a column called "medianValue"
 * df.groupBy { city }.median("medianValue") { age and weight }
 * ```
 * @include [MedianDocs.ResultColumnNameParam]
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ColumnsSelectorParam]
 * @return A new [DataFrame] with the group keys and a single median per group.
 */
@Refine
@Interpretable("GroupByMedian2")
public fun <T, C : Comparable<C & Any>?> Grouped<T>.median(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.medianCommon<C>(skipNaN).aggregateAll(this, name, columns)

/**
 * @include [MedianDocs.GroupedMedianSnippet]
 * @set [MedianDocs.GroupedMedianSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the median of all values in the "age" and "weight" columns,
 * // in a column called "medianValue"
 * df.groupBy { city }.median("age", "weight", name = "medianValue")
 * ```
 * @include [MedianDocs.ComparableColumnNamesParam]
 * @include [MedianDocs.ResultColumnNameParam]
 * @include [MedianDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and a single median per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByMedian2", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.median(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(name, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.median(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Grouped<T>.median(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(name, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [GroupBy] by computing the median of the values that the given [expression]
 * returns for each row of a group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the median per group, named [name] (or `"median"` if [name] is `null`).
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [MedianDocs.NullCellOnEmptySnippet]
 *
 * Don't confuse [medianOf] with [medianBy][GroupBy.medianBy], which returns the row of each group at
 * the median of the values the expression returns, instead of that value.
 *
 * See also:
 * - [median][Grouped.median] — a single median of all values in the selected columns, per group.
 * - [percentileOf][Grouped.percentileOf] — any other percentile of the values a row expression returns,
 *   per group.
 * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * @include [MedianDocs.GroupByUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the median weight-to-age ratio, in a column called "medianRatio"
 * df.groupBy { city }.medianOf("medianRatio") { (weight ?: 0) / age }
 * ```
 *
 * @include [MedianDocs.ExpressionResultColumnNameParam]
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ExpressionParam]
 * @return A new [DataFrame] with the group keys and a single median per group.
 */
@Refine
@Interpretable("GroupByMedianOf")
public inline fun <T, reified R : Comparable<R & Any>?> Grouped<T>.medianOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.medianCommon<R>(skipNaN).aggregateOf(this, name, expression)

/**
 * Reduces each group of this [GroupBy] to the row at the median of the values that the given
 * [rowExpression] returns for each row of that group.
 *
 * @include [MedianDocs.ReducedGroupBySnippet]
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [medianBy] with [medianOf][Grouped.medianOf], which returns the median value itself
 * instead of the row it belongs to.
 *
 * See also:
 * - [percentileBy][GroupBy.percentileBy] — the row at any other percentile, per group.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the median "age"
 * df.groupBy { city }.medianBy { age }.concat()
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to compute the value to compare the rows by.
 * @return A [ReducedGroupBy] with, for each group, the row at the median of the values [rowExpression] returns.
 */
@Interpretable("GroupByReduceExpression") // TODO?
public inline fun <T, G, reified R : Comparable<R & Any>?> GroupBy<T, G>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<G, R>,
): ReducedGroupBy<T, G> = reduce { medianByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.medianBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = reduce { medianByOrNull(column, skipNaN) }

/**
 * Reduces each group of this [GroupBy] to the row at the median of the values
 * in the column with the given name.
 *
 * @include [MedianDocs.ReducedGroupBySnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 * Don't confuse [medianBy] with [median][Grouped.median], which returns the median value itself
 * instead of the row it belongs to.
 *
 * See also:
 * - [percentileBy][GroupBy.percentileBy] — the row at any other percentile, per group.
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the full row of the person with the median "age"
 * df.groupBy { city }.medianBy("age").concat()
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @include [MedianDocs.SkipNanParam]
 * @return A [ReducedGroupBy] with, for each group, the row at the median of the values in the given column.
 */
public fun <T, G> GroupBy<T, G>.medianBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedGroupBy<T, G> =
    medianBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified C : Comparable<C & Any>?> GroupBy<T, G>.medianBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedGroupBy<T, G> = medianBy(column.toColumnAccessor(), skipNaN)

// endregion

// region Pivot

/**
 * Aggregates this [Pivot] by computing the median of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the median
 * of each suitable column of the corresponding group.
 *
 * @include [MedianDocs.AllComparableColumnsSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [MedianDocs.NullCellOnEmptyPivotSnippet]
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [medianFor][Pivot.medianFor] — the same, but for an explicit selection of columns.
 * - [median][Pivot.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * @include [MedianDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the median value of each comparable column
 * df.pivot { city }.median()
 * ```
 *
 * @include [MedianDocs.SeparateParam]
 * @include [MedianDocs.SkipNanParam]
 * @return A single [DataRow] with the median of each suitable column per [pivot] group.
 */
public fun <T> Pivot<T>.median(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    medianFor(separate, skipNaN, intraComparableColumns())

/**
 * @include [MedianDocs.PivotMedianForSnippet]
 * @set [MedianDocs.PivotMedianForSnippet.NOTE] {@include [MedianDocs.AggregateColumnsSelectorSnippet]}
 * @set [MedianDocs.PivotMedianForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the median "age" and the median "weight"
 * df.pivot { city }.medianFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.medianFor(separate = true) { age and weight }
 * ```
 * @include [MedianDocs.SeparateParam]
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.AggregateColumnsSelectorParam]
 * @return A single [DataRow] with the median of each selected column per [pivot] group.
 */
public fun <T, C : Comparable<*>?> Pivot<T>.medianFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { medianFor(separate, skipNaN, columns) }

/**
 * @include [MedianDocs.PivotMedianForSnippet]
 * @set [MedianDocs.PivotMedianForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the median "age" and the median "weight"
 * df.pivot { city }.medianFor("age", "weight")
 * ```
 * @include [MedianDocs.ColumnNamesParam]
 * @include [MedianDocs.SeparateParam]
 * @include [MedianDocs.SkipNanParam]
 * @return A single [DataRow] with the median of each selected column per [pivot] group.
 */
public fun <T> Pivot<T>.medianFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Pivot<T>.medianFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> Pivot<T>.medianFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = medianFor(separate, skipNaN) { columns.toColumnSet() }

/**
 * @include [MedianDocs.PivotMedianSnippet]
 * @set [MedianDocs.PivotMedianSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the median of all values in the "age" and "weight" columns
 * df.pivot { city }.median { age and weight }
 * ```
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ColumnsSelectorParam]
 * @return A single [DataRow] with, per [pivot] group, the median of all the values in the selected columns.
 */
public fun <T, C : Comparable<C & Any>?> Pivot<T>.median(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { median(skipNaN, columns) }

/**
 * @include [MedianDocs.PivotMedianSnippet]
 * @set [MedianDocs.PivotMedianSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the median of all values in the "age" and "weight" columns
 * df.pivot { city }.median("age", "weight")
 * ```
 * @include [MedianDocs.ComparableColumnNamesParam]
 * @include [MedianDocs.SkipNanParam]
 * @return A single [DataRow] with, per [pivot] group, the median of all the values in the selected columns.
 */
public fun <T> Pivot<T>.median(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    median(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.median(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = median(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> Pivot<T>.median(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = median(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [Pivot] by computing the median of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the median
 * of the expression's results for the rows of the corresponding group.
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [MedianDocs.NullCellOnEmptyPivotSnippet]
 *
 * Don't confuse [medianOf] with [medianBy][Pivot.medianBy], which returns the row of each group at
 * the median of the values the expression returns, instead of that value.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [median][Pivot.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * @include [MedianDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the median weight-to-age ratio
 * df.pivot { city }.medianOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ExpressionParam]
 * @return A single [DataRow] with, per [pivot] group, the median of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { medianOf(skipNaN, expression) }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the [row][DataRow]
 * at the median of the values that the given [rowExpression] returns for each row of that group.
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 * @include [MedianDocs.ReducedPivotSnippet]
 *
 * Don't confuse [medianBy] with [medianOf][Pivot.medianOf], which returns the median value the expression
 * returns itself, instead of the row.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [percentileBy][Pivot.percentileBy] — the row at any other percentile, per group.
 * - [Pivot reducing][PivotDocs.Reducing] — all other ways to reduce a [Pivot].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person with the median weight-to-age ratio
 * df.pivot { city }.medianBy { (weight ?: 0) / age }.with { name }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to compute the value to compare the rows by.
 * @return A [ReducedPivot] holding, per group,
 *   the row at the median of the values [rowExpression] returns.
 */
public inline fun <T, reified R : Comparable<R & Any>?> Pivot<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivot<T> = reduce { medianByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.medianBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = reduce { medianByOrNull(column, skipNaN) }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the [row][DataRow]
 * at the median of the values in the given [column].
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 * @include [MedianDocs.ReducedPivotSnippet]
 *
 * Don't confuse [medianBy] with [median][Pivot.median], which returns the median value itself,
 * instead of the row.
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [percentileBy][Pivot.percentileBy] — the row at any other percentile, per group.
 * - [Pivot reducing][PivotDocs.Reducing] — all other ways to reduce a [Pivot].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // For each city, the "name" of the person with the median "age"
 * df.pivot { city }.medianBy("age").with { name }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @include [MedianDocs.SkipNanParam]
 * @return A [ReducedPivot] holding, per group, the row at the median of the values in the given column.
 */
public fun <T> Pivot<T>.medianBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivot<T> =
    medianBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> Pivot<T>.medianBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivot<T> = medianBy(column.toColumnAccessor(), skipNaN)
// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by computing the median of the values of
 * each suitable column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the median of each suitable column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * @include [MedianDocs.AllComparableColumnsSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [MedianDocs.NullCellOnEmptyPivotSnippet]
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [medianFor][PivotGroupBy.medianFor] — the same, but for an explicit selection of columns.
 * - [median][PivotGroupBy.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * @include [MedianDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the median value of each comparable column
 * df.pivot { city }.groupBy { name.lastName }.median()
 * ```
 *
 * @include [MedianDocs.SeparateParam]
 * @include [MedianDocs.SkipNanParam]
 * @return A [DataFrame] with the median of each suitable column per [pivot] and [groupBy] group.
 */
public fun <T> PivotGroupBy<T>.median(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    medianFor(separate, skipNaN, intraComparableColumns())

/**
 * @include [MedianDocs.PivotGroupByMedianForSnippet]
 * @set [MedianDocs.PivotGroupByMedianForSnippet.NOTE] {@include [MedianDocs.AggregateColumnsSelectorSnippet]}
 * @set [MedianDocs.PivotGroupByMedianForSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the median "age" and the median "weight"
 * df.pivot { city }.groupBy { name.lastName }.medianFor { age and weight }
 * ```
 * @include [MedianDocs.SeparateParam]
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.AggregateColumnsSelectorParam]
 * @return A [DataFrame] with the median of each selected column per [pivot] and [groupBy] group.
 */
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.medianFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.median.invoke(skipNaN).aggregateFor(this, separate, columns)

/**
 * @include [MedianDocs.PivotGroupByMedianForSnippet]
 * @set [MedianDocs.PivotGroupByMedianForSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the median "age" and the median "weight"
 * df.pivot { city }.groupBy { name.lastName }.medianFor("age", "weight")
 * ```
 * @include [MedianDocs.ColumnNamesParam]
 * @include [MedianDocs.SeparateParam]
 * @include [MedianDocs.SkipNanParam]
 * @return A [DataFrame] with the median of each selected column per [pivot] and [groupBy] group.
 */
public fun <T> PivotGroupBy<T>.medianFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(separate, skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.medianFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<*>?> PivotGroupBy<T>.medianFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = medianFor(separate, skipNaN) { columns.toColumnSet() }

/**
 * @include [MedianDocs.PivotGroupByMedianSnippet]
 * @set [MedianDocs.PivotGroupByMedianSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the median of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.median { age and weight }
 * ```
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ColumnsSelectorParam]
 * @return A [DataFrame] with, per [pivot] and [groupBy] group, the median of all the values
 *   in the selected columns.
 */
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.median(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.medianCommon<C>(skipNaN).aggregateAll(this, columns)

/**
 * @include [MedianDocs.PivotGroupByMedianSnippet]
 * @set [MedianDocs.PivotGroupByMedianSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the median of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.median("age", "weight")
 * ```
 * @include [MedianDocs.ComparableColumnNamesParam]
 * @include [MedianDocs.SkipNanParam]
 * @return A [DataFrame] with, per [pivot] and [groupBy] group, the median of all the values
 *   in the selected columns.
 */
public fun <T> PivotGroupBy<T>.median(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    median(skipNaN) { columns.toComparableColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.median(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Comparable<C & Any>?> PivotGroupBy<T>.median(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = median(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [PivotGroupBy] by computing the median of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a [DataFrame] where each cell contains the median of the expression's results for the rows
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ExpressionResultIsInputSnippet]
 *
 * @include [MedianDocs.InputValuesSnippet]
 *
 * @include [MedianDocs.ResultTypeSnippet]
 *
 * @include [MedianDocs.NullCellOnEmptyPivotSnippet]
 *
 * Don't confuse [medianOf] with [medianBy][PivotGroupBy.medianBy], which returns the row of each group at
 * the median of the values the expression returns, instead of that value.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [median][PivotGroupBy.median]` { columns }` — a single median of all values in the selected columns,
 *   per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * @include [MedianDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the median weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.medianOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @include [MedianDocs.ExpressionParam]
 * @return A [DataFrame] with, per [pivot] and [groupBy] group, the median of the expression's results.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.medianOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.medianCommon<R>(skipNaN).aggregateOf(this, expression)

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking from each group
 * the [row][DataRow] at the median of the values that the given [rowExpression] returns
 * for each row of that group.
 *
 * @include [MedianDocs.RowExpressionSnippet]
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 * @include [MedianDocs.ReducedPivotGroupBySnippet]
 *
 * Don't confuse [medianBy] with [medianOf][PivotGroupBy.medianOf], which returns the median value the
 * expression returns itself, instead of the row.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [percentileBy][PivotGroupBy.percentileBy] — the row at any other percentile, per group.
 * - [PivotGroupBy reducing][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [PivotGroupBy].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person with the median weight-to-age ratio
 * df.pivot { city }.groupBy { name.lastName }.medianBy { (weight ?: 0) / age }.with { name.firstName }
 * ```
 *
 * @include [MedianDocs.SkipNanParam]
 * @param [rowExpression] The [RowExpression] to compute the value to compare the rows by.
 * @return A [ReducedPivotGroupBy] holding, per group,
 *   the row at the median of the values [rowExpression] returns.
 */
public inline fun <T, reified R : Comparable<R & Any>?> PivotGroupBy<T>.medianBy(
    skipNaN: Boolean = skipNaNDefault,
    crossinline rowExpression: RowExpression<T, R>,
): ReducedPivotGroupBy<T> = reduce { medianByOrNull(skipNaN, rowExpression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.medianBy(
    column: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = reduce { medianByOrNull(column, skipNaN) }

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking from each group
 * the [row][DataRow] at the median of the values in the given [column].
 *
 * @include [MedianDocs.ComparableInputValuesSnippet]
 *
 * @include [MedianDocs.BySelectionSnippet]
 *
 * Groups that have no values to compute the median of cannot select a row, and produce `null` values instead.
 *
 * @include [MedianDocs.ReducedPivotGroupBySnippet]
 *
 * Don't confuse [medianBy] with [median][PivotGroupBy.median], which returns the median value itself,
 * instead of the row.
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [percentileBy][PivotGroupBy.percentileBy] — the row at any other percentile, per group.
 * - [PivotGroupBy reducing][PivotGroupByDocs.Reducing] — all other ways to reduce
 *   a [PivotGroupBy].
 * - {@include [MedianDocsLink]} — an overview of all `median` modes.
 *
 * For more information: {@include [DocumentationUrls.Median]}
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the "firstName" of the person with the median "age"
 * df.pivot { city }.groupBy { name.lastName }.medianBy("age").with { name.firstName }
 * ```
 *
 * @param [column] The name of the column to compare the rows by.
 * @include [MedianDocs.SkipNanParam]
 * @return A [ReducedPivotGroupBy] holding, per group, the row at the median of the values in the given column.
 */
public fun <T> PivotGroupBy<T>.medianBy(column: String, skipNaN: Boolean = skipNaNDefault): ReducedPivotGroupBy<T> =
    medianBy(column.toColumnAccessor().cast<Comparable<Any>?>(), skipNaN)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified C : Comparable<C & Any>?> PivotGroupBy<T>.medianBy(
    column: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): ReducedPivotGroupBy<T> = medianBy(column.toColumnAccessor(), skipNaN)

// endregion
