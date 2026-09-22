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
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfRow
import org.jetbrains.kotlinx.dataframe.impl.aggregation.primitiveOrMixedNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveOrMixedNumber
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.MEAN_NO_SKIPNAN
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region docs

/**
 * {@comment
 *    The Mean Operation KDoc-topic; it also holds all common `mean` KDoc-snippets.
 *    Link to it with `{@include [MeanDocsLink]}`.
 * }
 *
 * ## The Mean Operation
 *
 * Computes the [mean (average)](https://en.wikipedia.org/wiki/Arithmetic_mean) of values —
 * the [sum][DataFrame.sum] divided by the number of values.
 *
 * @include [SupportedTypesSnippet]
 *
 * ### Mean Modes
 *
 * Depending on what exactly you want the mean of, there are several modes.
 * They are shown here for [DataFrame], but they exist for the other receivers too:
 *
 * - [`mean`][DataFrame.mean]`()` — the mean of each suitable column separately.
 * - [`mean`][DataFrame.mean]` { columns }` — a single mean of all values in all selected columns.
 * - [`meanFor`][DataFrame.meanFor]` { columns }` — the mean of each selected column separately.
 * - [`meanOf`][DataFrame.meanOf]` { expression }` — the mean of the values that the given expression
 *   returns for each row.
 *
 * Related operation:
 * - [`sum`][DataFrame.sum] — the sum of values (mean is the sum divided by the number of values).
 *
 * For more information: {@include [DocumentationUrls.Mean]}
 *
 * For more information about [unifying numbers][UnifyingNumbers]:
 * @include [DocumentationUrls.NumberUnification]
 *
 * See all summary statistics:
 * @include [DocumentationUrls.Statistics]
 */
internal interface MeanDocs : CommonStatisticsDocs {

    /**
     * {@comment Note about which values are supported, how `null` and `NaN` values are treated,
     *    and what type the result has. KDoc-snippet.}
     *
     * All primitive number types are supported: [Byte], [Short], [Int], [Long], [Float], and [Double].
     * "Mixed" [Number] input is supported too, as long as it consists solely of those primitive numbers;
     * its values are then first converted to their common type using
     * [UnifiedNumberTypeOptions.PRIMITIVES_ONLY][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.PRIMITIVES_ONLY],
     * see [number unification][UnifyingNumbers].
     * Big numbers ([`BigInteger`][java.math.BigInteger], [`BigDecimal`][java.math.BigDecimal]) are not
     * supported; [`convert`][DataFrame.convert] them to a primitive number type first.
     *
     * @include [CommonStatisticsDocs.NullHandlingSnippet]
     * {@get [NAN_NOTE] {@include [CommonStatisticsDocs.NaNHandlingSnippet]}}
     *
     * The result is always a [Double] and never `null`.
     * Converting [Long] values to [Double] may lose precision for very large values.
     *
     * For more information about the resulting types:
     * @include [DocumentationUrls.Mean.TypeConversion]
     */
    @ExcludeFromSources
    interface SupportedTypesSnippet {

        // The note about `NaN` handling. Filled in by default; must be set to nothing for the
        // overloads of a fixed integer type, as those have no `skipNaN` parameter to refer to.
        // (Mean always has skipNaN, but the slot is kept for structural parity with sum.)
        typealias NAN_NOTE = Nothing
    }

    /**
     * {@comment Note about the behavior on empty input for the modes with a single result. KDoc-snippet.}
     *
     * When there is nothing to average, for instance, when the input is empty or contains only `null` values,
     * the result is [Double.NaN]
     */
    @ExcludeFromSources
    typealias NanOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the modes with multiple results. KDoc-snippet.}
     *
     * Result cells for which there is nothing to average
     * (for instance, because the input was empty or contained only `null` values)
     * simply become [Double.NaN].
     *
     * For more information about the resulting types:
     * @include [DocumentationUrls.Mean.TypeConversion]
     */
    @ExcludeFromSources
    typealias NanCellOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the Pivot functions. KDoc-snippet.}
     *
     * Result cells for which there exists a group, but there is nothing to average
     * (for instance, because the group was empty or contained only `null` values)
     * simply become [Double.NaN].
     *
     * For more information about the resulting types:
     * @include [DocumentationUrls.Mean.TypeConversion]
     *
     * For empty pivot intersections, `null` or the [set default][PivotGroupBy.default] are used.
     */
    @ExcludeFromSources
    typealias NanCellOnEmptyPivotSnippet = Nothing

    /**
     * {@comment Note about which columns the no-argument `mean` modes take into account. KDoc-snippet.}
     *
     * All columns of a primitive number type (and all "mixed" [Number] columns) are taken into account;
     * the other columns are simply left out of the result.
     *
     * This includes columns inside [column groups][ColumnGroup].
     * To include those in the mean, [flatten][DataFrame.flatten] the DataFrame first.
     */
    @ExcludeFromSources
    typealias AllSuitableColumnsSnippet = Nothing

    /**
     * @comment Version of [SelectingColumns] with correctly filled in examples
     * @include [SelectingColumns] {@include [SetMeanOperationArg]}
     */
    typealias MeanSelectingOptions = Nothing

    /**
     * @comment Version of [SelectingColumns] with correctly filled in examples
     * @include [SelectingColumns] {@include [SetMeanForOperationArg]}
     */
    typealias MeanForSelectingOptions = Nothing

    /**
     * @include [MeanDocs.NanOnEmptySnippet]
     *
     * See also:
     * - [`meanOf`][DataColumn.meanOf] — the mean of the values an expression returns for each element.
     * - [`sum`][DataColumn.sum] — the sum of the values in this column.
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * For more information:
     * @include [DocumentationUrls.Mean]
     *
     * ### Example
     */
    @ExcludeFromSources
    typealias DataColumnMeanSnippet = Nothing

    /**
     * {@comment The parts all [DataColumn.meanOf] overloads have in common. KDoc-snippet.}
     *
     * The result of [expression\] is treated as the 'input' of this operation.
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanOnEmptySnippet]
     *
     * See also:
     * - [`mean`][DataColumn.mean] — the mean of the values in this column itself.
     * - [`sumOf`][DataColumn.sumOf] — the sum of those values.
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * For more information:
     * @include [DocumentationUrls.Mean]
     *
     * ### Example
     * $[EXAMPLE]
     *
     * @param [expression\] A function that returns the value to average for each element of this column.
     */
    @ExcludeFromSources
    interface DataColumnMeanOfSnippet {

        // The example to render for this meanOf overload
        typealias EXAMPLE = Nothing
    }

    /**
     * @comment The parts all [DataRow.rowMeanOf] overloads have in common. KDoc-snippet.
     *
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanOnEmptySnippet]
     *
     * See also:
     * - [`rowMean`][DataRow.rowMean] — the mean of all the numbers in this row, of any number type.
     * - [`mean`][DataFrame.mean] — the mean of the values in specific columns of a [DataFrame].
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * For more information:
     * @include [DocumentationUrls.RowStatistics]
     *
     * ### Example
     */
    @ExcludeFromSources
    typealias RowMeanOfSnippet = Nothing

    /**
     * {@comment The parts all [DataFrame.meanFor] overloads have in common. KDoc-snippet.}
     *
     * Returns the mean of the values of each selected column of this [DataFrame] separately.
     *
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanCellOnEmptySnippet]
     * $[NOTE]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MeanDocs.MeanForSelectingOptions].
     *
     * See also:
     * - [`mean`][DataFrame.mean]`()` — the same, but for all suitable columns at once.
     * - [`mean`][DataFrame.mean]` { columns }` — a single mean of all values in the selected columns.
     * - [`sumFor`][DataFrame.sumFor] — the sum of each selected column.
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * For more information: {@include [DocumentationUrls.Mean]}
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface DataFrameMeanForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this meanFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * @comment The parts all column-selecting [DataFrame.mean] overloads have in common.
     *    KDoc-snippet.
     *
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanOnEmptySnippet]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See also:
     * - [`meanFor`][DataFrame.meanFor] — the mean of each selected column separately.
     * - [`meanOf`][DataFrame.meanOf] — the mean of the values a row expression returns for each row.
     * $[SEE_ALSO_TAIL]
     *
     * For more information: {@include [DocumentationUrls.Mean]}
     *
     * $[COLUMNS_API]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface DataFrameMeanSnippet {

        // The remaining "See also" bullets of this mean overload.
        typealias SEE_ALSO_TAIL = Nothing

        // How this overload selects its columns: the Columns Selection DSL or column names
        typealias COLUMNS_API = Nothing

        // The example to render for this mean overload
        typealias EXAMPLE = Nothing
    }

    /**
     * @comment The parts all [DataFrame.meanOf] overloads have in common.
     *    KDoc-snippet.
     *
     * @include [MeanDocs.RowExpressionSnippet]
     *
     * The result of the [expression\] is considered the 'input' of this operation.
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanOnEmptySnippet]
     *
     * See also:
     * - [`mean`][DataFrame.mean]` { columns }` — a single mean of all values in the selected columns.
     * $[SEE_ALSO_TAIL]
     *
     * For more information: {@include [DocumentationUrls.Mean]}
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface DataFrameMeanOfSnippet {

        // The remaining "See also" bullets of this meanOf overload.
        typealias SEE_ALSO_TAIL = Nothing

        // The example to render for this meanOf overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [Grouped.meanFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [GroupBy] by computing the mean of the values of
     * each selected column separately, per group.
     *
     * Returns a new [DataFrame] with one row per group, containing the group key columns
     * and a column with the mean for each selected column.
     *
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanCellOnEmptySnippet]
     * $[NOTE]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MeanDocs.MeanForSelectingOptions].
     *
     * See also:
     * - [`mean`][Grouped.mean]`()` — the same, but for all suitable columns at once.
     * - [`mean`][Grouped.mean]` { columns }` — a single mean of all values in the selected columns, per group.
     * - [`sumFor`][Grouped.sumFor] — the sum of each selected column, per group.
     * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * @include [MeanDocs.GroupByUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface GroupedMeanForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this meanFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [Grouped.mean] overloads have in common.
     *    KDoc-snippet.}
     *
     * Aggregates this [GroupBy] by computing a single mean of all the values
     * in the selected columns, per group.
     *
     * Returns a new [DataFrame] with one row per group, containing the group key columns and
     * a single column with the mean per group.
     * That column is named [name\], or, if [name\] is `null`, after the selected column
     * if exactly one column is selected, and `"mean"` otherwise.
     *
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanCellOnEmptySnippet]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MeanDocs.MeanSelectingOptions].
     *
     * See also:
     * - [`meanFor`][Grouped.meanFor] — the mean of each selected column separately, per group.
     * - [`meanOf`][Grouped.meanOf] — the mean of the values a row expression returns
     *   for each row of a group.
     * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * @include [MeanDocs.GroupByUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface GroupedMeanSnippet {

        // The example to render for this mean overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [Pivot.meanFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [Pivot] by computing the mean of the values of
     * each selected column separately, per group.
     *
     * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the mean
     * of each selected column of the corresponding group.
     *
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanCellOnEmptyPivotSnippet]
     * $[NOTE]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MeanDocs.MeanForSelectingOptions], or check out the
     * [`Pivot` Grammar][PivotDocs.Grammar].
     *
     * See also:
     * - [`mean`][Pivot.mean]`()` — the same, but for all suitable columns at once.
     * - [`mean`][Pivot.mean]` { columns }` — a single mean of all values in the selected columns, per group.
     * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * @include [MeanDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotMeanForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this meanFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [Pivot.mean] overloads have in common.
     *    KDoc-snippet.}
     *
     * Aggregates this [Pivot] by computing a single mean of all the values
     * in the selected columns, per group.
     *
     * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the mean of all
     * the values in the selected columns of the corresponding group.
     *
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanCellOnEmptyPivotSnippet]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MeanDocs.MeanSelectingOptions], or check out the
     * [`Pivot` Grammar][PivotDocs.Grammar].
     *
     * See also:
     * - [`mean`][Pivot.mean]`()` — the mean of each suitable column separately, per group.
     * - [`meanFor`][Pivot.meanFor] — the mean of each selected column separately, per group.
     * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * @include [MeanDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotMeanSnippet {

        // The example to render for this mean overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [PivotGroupBy.meanFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [PivotGroupBy] by computing the mean of the values of
     * each selected column separately, per group.
     *
     * Returns a [DataFrame] where each cell contains the mean of each selected column
     * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
     *
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanCellOnEmptyPivotSnippet]
     * $[NOTE]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MeanDocs.MeanForSelectingOptions], or check out the
     * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
     *
     * See also:
     * - [`mean`][PivotGroupBy.mean]`()` — the same, but for all suitable columns at once.
     * - [`mean`][PivotGroupBy.mean]` { columns }` — a single mean of all values in the selected columns,
     *   per group.
     * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
     *   a [PivotGroupBy].
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * @include [MeanDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotGroupByMeanForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this meanFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [PivotGroupBy.mean] overloads have in common.
     *    KDoc-snippet.}
     *
     * Aggregates this [PivotGroupBy] by computing a single mean of all the values
     * in the selected columns, per group.
     *
     * Returns a [DataFrame] where each cell contains the mean of all the values in the selected columns
     * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
     *
     * @include [MeanDocs.SupportedTypesSnippet]
     * @include [MeanDocs.NanCellOnEmptyPivotSnippet]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][MeanDocs.MeanSelectingOptions], or check out the
     * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
     *
     * See also:
     * - [`mean`][PivotGroupBy.mean]`()` — the mean of each suitable column separately, per group.
     * - [`meanFor`][PivotGroupBy.meanFor] — the mean of each selected column separately, per group.
     * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
     *   a [PivotGroupBy].
     * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
     *
     * @include [MeanDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotGroupByMeanSnippet {

        // The example to render for this mean overload
        typealias EXAMPLE = Nothing
    }

    /**
     * @comment The `columns` parameter of the [ColumnsSelector] overloads. KDoc-snippet.
     *
     * @param [columns\] The [ColumnsSelector] used to select the columns to compute the mean of.
     */
    @ExcludeFromSources
    typealias ColumnsSelectorParam = Nothing

    /**
     * @comment The `columns` parameter of the [ColumnsForAggregateSelector] overloads. KDoc-snippet.
     *
     * @param [columns\] The [ColumnsForAggregateSelector] used to select the columns
     *   to compute the mean of.
     */
    @ExcludeFromSources
    typealias AggregateColumnsSelectorParam = Nothing

    /**
     * @comment The `columns` parameter of the [String] overloads. KDoc-snippet.
     *
     * @param [columns\] The names of the columns to compute the mean of.
     *   These must be primitive number columns, else an [IllegalArgumentException] is thrown.
     */
    @ExcludeFromSources
    typealias ColumnNamesParam = Nothing

    /**
     * @comment The `expression` parameter of the `meanOf` overloads. KDoc-snippet.
     *
     * @param [expression\] The [RowExpression] to compute the value to average for each row.
     */
    @ExcludeFromSources
    typealias ExpressionParam = Nothing

    /**
     * @comment The `name` parameter of the [Grouped.mean] overloads. KDoc-snippet.
     *
     * @param [name\] The name of the resulting column.
     *   If `null` (the default), the name of the selected column is used if exactly one column
     *   is selected, and `"mean"` otherwise.
     *   This name needs to be unique, else a [DuplicateColumnPathInsertException] is thrown.
     */
    @ExcludeFromSources
    typealias ResultColumnNameParam = Nothing
}

/** [The Mean Operation][MeanDocs] */
@ExcludeFromSources
private typealias MeanDocsLink = Nothing

/** {@set [SelectingColumns.OPERATION] [mean][mean]} */
@ExcludeFromSources
private typealias SetMeanOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [meanFor][meanFor]} */
@ExcludeFromSources
private typealias SetMeanForOperationArg = Nothing

// endregion

// region DataColumn

/**
 * Returns the mean of the values in this [DataColumn], as a [Double].
 *
 * @include [MeanDocs.SupportedTypesSnippet]
 * @include [MeanDocs.DataColumnMeanSnippet]
 * ```kotlin
 * // The mean of all ages in the "age" Int column
 * df.age.mean()
 * // The mean of all weights in the "weight" `Double?` column, ignoring `null` values
 * df.weight.mean()
 * ```
 *
 * @include [MeanDocs.SkipNanParam]
 * @return The mean of the values in this column, as a [Double].
 */
public fun DataColumn<Number?>.mean(skipNaN: Boolean = skipNaNDefault): Double =
    Aggregators.mean(skipNaN).aggregateSingleColumn(this)

/**
 * Returns the mean of the values that the given [expression] returns
 * for each element of this [DataColumn], as a [Double].
 *
 * @include [MeanDocs.DataColumnMeanOfSnippet]
 * @set [MeanDocs.DataColumnMeanOfSnippet.EXAMPLE]
 * ```kotlin
 * // The mean length of all first names in the "name"/"firstName" column
 * df.name.firstName.meanOf { it.length }
 * ```
 * @include [MeanDocs.SkipNanParam]
 * @return The mean of the values [expression] returns, as a [Double].
 */
public inline fun <T, reified R : Number?> DataColumn<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (T) -> R,
): Double = Aggregators.mean(skipNaN).aggregateOf(this, expression)

// endregion

// region DataRow

/**
 * Returns the mean of all the numbers in this [DataRow], as a [Double].
 *
 * Only the values in the columns of a primitive number type (and in "mixed" [Number] columns)
 * are taken into account; all other columns of the row are ignored.
 * This includes columns inside [column groups][ColumnGroup].
 * To include those in the mean, [flatten][DataFrame.flatten] the DataFrame first.
 *
 * Since the values of different columns are averaged together, the result is the mean of all those
 * values converted to their common type.
 *
 * @include [MeanDocs.SupportedTypesSnippet]
 * @include [MeanDocs.NanOnEmptySnippet]
 *
 * See also:
 * - [`rowMeanOf<Type>()`][DataRow.rowMeanOf] — the mean of the values of one specific number type in this row.
 * - [`rowSum`][DataRow.rowSum] — the sum of all the numbers in this row.
 * - [`mean`][DataFrame.mean] — the mean of the values in specific columns of a [DataFrame].
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The mean of all numbers ("age" and "weight") in the first row
 * // Columns of other types ("name" and "address") are ignored
 * df[0].rowMean()
 * ```
 *
 * @include [MeanDocs.SkipNanParam]
 * @return The mean of all the numbers in this row, as a [Double].
 */
public fun DataRow<*>.rowMean(skipNaN: Boolean = skipNaNDefault): Double =
    Aggregators.mean(skipNaN).aggregateOfRow(this, primitiveOrMixedNumberColumns())

/**
 * Returns the mean of the values of type [T] in this [DataRow], as a [Double].
 *
 * Only the values in the columns of type [T] (or its nullable variant) are taken into account;
 * all other columns of the row are ignored.
 * This includes columns inside [column groups][ColumnGroup].
 * To include those in the mean, [flatten][DataFrame.flatten] the DataFrame first.
 *
 * [T] must be a primitive number type or [Number] itself.
 *
 * @include [MeanDocs.RowMeanOfSnippet]
 * ```kotlin
 * // The mean of all `Int` values ("age" and "weight") in the first row
 * df[0].rowMeanOf<Int>()
 * // The mean of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowMeanOf<Double>(skipNaN = true)
 * ```
 *
 * @param [T] The type of the values to average. Only columns of this type are taken into account.
 * @include [MeanDocs.SkipNanParam]
 * @return The mean of the values of type [T] in this row, as a [Double].
 * @throws IllegalArgumentException if [T] is not a primitive number type or [Number] itself.
 */
public inline fun <reified T : Number> DataRow<*>.rowMeanOf(skipNaN: Boolean = skipNaNDefault): Double {
    require(typeOf<T>().isPrimitiveOrMixedNumber()) {
        "Type ${T::class.simpleName} is not a primitive number type. Mean only supports primitive number types."
    }
    return Aggregators.mean(skipNaN).aggregateOfRow(this) { colsOf<T?>() }
}

// endregion

// region DataFrame

/**
 * Returns the mean of the values of each suitable column of this [DataFrame] separately.
 *
 * @include [MeanDocs.AllSuitableColumnsSnippet]
 * @include [MeanDocs.SupportedTypesSnippet]
 * @include [MeanDocs.NanCellOnEmptySnippet]
 *
 * See also:
 * - [`meanFor`][DataFrame.meanFor] — the same, but for an explicit selection of columns.
 * - [`mean`][DataFrame.mean]` { columns }` — a single mean of all values in the selected columns.
 * - [`sum`][DataFrame.sum] — the sum of each column.
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 *
 * For more information: {@include [DocumentationUrls.Mean]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the mean of each number column ("age" and "weight")
 * df.mean()
 * ```
 *
 * @include [MeanDocs.SkipNanParam]
 * @return A single [DataRow] with the mean of each suitable column of this [DataFrame].
 */
@Refine
@Interpretable("Mean0")
public fun <T> DataFrame<T>.mean(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    meanFor(skipNaN, primitiveOrMixedNumberColumns())

/**
 * @include [MeanDocs.DataFrameMeanForSnippet]
 * @set [MeanDocs.DataFrameMeanForSnippet.NOTE] {@include [MeanDocs.AggregateColumnsSelectorSnippet]}
 * @set [MeanDocs.DataFrameMeanForSnippet.EXAMPLE]
 * ```kotlin
 * // A single row with the mean of the "age" values and the mean of the "weight" values
 * df.meanFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.meanFor(skipNaN = true) { age into "meanAge" and (weight into "meanWeight") }
 * ```
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.AggregateColumnsSelectorParam]
 * @return A single [DataRow] with the mean of each selected column.
 */
@Refine
@Interpretable("Mean1")
public fun <T, C : Number?> DataFrame<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.mean(skipNaN).aggregateFor(this, columns)

/**
 * @include [MeanDocs.DataFrameMeanForSnippet]
 * @set [MeanDocs.DataFrameMeanForSnippet.EXAMPLE]
 * ```kotlin
 * // A single row with the mean of the "age" values and the mean of the "weight" values
 * df.meanFor("age", "weight")
 * ```
 * @include [MeanDocs.ColumnNamesParam]
 * @include [MeanDocs.SkipNanParam]
 * @return A single [DataRow] with the mean of each selected column.
 */
@Refine
@StringApiInterpretable(interpreter = "Mean1", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.meanFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    meanFor(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.meanFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = meanFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.meanFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = meanFor(skipNaN) { columns.toColumnSet() }

/**
 * Returns a single mean of all the values in the selected columns of this [DataFrame], as a [Double].
 *
 * @include [MeanDocs.DataFrameMeanSnippet]
 * @set [MeanDocs.DataFrameMeanSnippet.SEE_ALSO_TAIL]
 * - [`sum`][DataFrame.sum] — the sum of all values in the selected columns.
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 * @set [MeanDocs.DataFrameMeanSnippet.COLUMNS_API] {@include [SelectingColumns.ColumnsSelectionDsl]}
 * @set [MeanDocs.DataFrameMeanSnippet.EXAMPLE]
 * ```kotlin
 * // The mean of all values in the "age" and "weight" columns together
 * df.mean { age and weight }
 * ```
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.ColumnsSelectorParam]
 * @return The mean of all the values in the selected columns, as a [Double].
 */
public fun <T, C : Number?> DataFrame<T>.mean(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): Double = Aggregators.mean(skipNaN).aggregateAll(this, columns)

/**
 * Returns a single mean of all the values in the selected columns of this [DataFrame], as a [Double].
 *
 * @include [MeanDocs.DataFrameMeanSnippet]
 * @set [MeanDocs.DataFrameMeanSnippet.SEE_ALSO_TAIL]
 * - [`sum`][DataFrame.sum] — the sum of all values in the selected columns.
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 * @set [MeanDocs.DataFrameMeanSnippet.COLUMNS_API] {@include [SelectingColumns.ColumnNamesApi]}
 * @set [MeanDocs.DataFrameMeanSnippet.EXAMPLE]
 * ```kotlin
 * // The mean of all values in the "age" and "weight" columns together
 * df.mean("age", "weight")
 * ```
 * @include [MeanDocs.ColumnNamesParam]
 * @include [MeanDocs.SkipNanParam]
 * @return The mean of all the values in the selected columns, as a [Double].
 */
public fun <T> DataFrame<T>.mean(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Double =
    mean(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.mean(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double = mean(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.mean(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): Double = mean(skipNaN) { columns.toColumnSet() }

/**
 * Returns the mean of the values that the given [expression] returns
 * for each row of this [DataFrame], as a [Double].
 *
 * @include [MeanDocs.DataFrameMeanOfSnippet]
 * @set [MeanDocs.DataFrameMeanOfSnippet.SEE_ALSO_TAIL]
 * - [`sumOf`][DataFrame.sumOf] — the sum of those values.
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 * @set [MeanDocs.DataFrameMeanOfSnippet.EXAMPLE]
 * ```kotlin
 * // The mean of the weight-to-age ratios of all rows
 * df.meanOf { (weight ?: 0) / age }
 * ```
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.ExpressionParam]
 * @return The mean of the values [expression] returns, as a [Double].
 */
public inline fun <T, reified D : Number?> DataFrame<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, D>,
): Double = Aggregators.mean(skipNaN).aggregateOf(this, expression)

// endregion

// region GroupBy

/**
 * Aggregates this [GroupBy] by computing the mean of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the mean for each suitable column.
 *
 * @include [MeanDocs.AllSuitableColumnsSnippet]
 * @include [MeanDocs.SupportedTypesSnippet]
 * @include [MeanDocs.NanCellOnEmptySnippet]
 *
 * See also:
 * - [`meanFor`][Grouped.meanFor] — the same, but for an explicit selection of columns.
 * - [`mean`][Grouped.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [`sum`][Grouped.sum] — the sum of each column, per group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 *
 * @include [MeanDocs.GroupByUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of each number column ("age" and "weight")
 * df.groupBy { city }.mean()
 * ```
 *
 * @include [MeanDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and the mean of each suitable column per group.
 */
@Refine
@Interpretable("GroupByMean1")
public fun <T> Grouped<T>.mean(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    meanFor(skipNaN, primitiveOrMixedNumberColumns())

/**
 * @include [MeanDocs.GroupedMeanForSnippet]
 * @set [MeanDocs.GroupedMeanForSnippet.NOTE] {@include [MeanDocs.AggregateColumnsSelectorSnippet]}
 * @set [MeanDocs.GroupedMeanForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the mean of the "age" values and the mean of the "weight" values
 * df.groupBy { city }.meanFor { age and weight }
 * ```
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.AggregateColumnsSelectorParam]
 * @return A new [DataFrame] with the group keys and the mean of each selected column per group.
 */
@Refine
@Interpretable("GroupByMean0")
public fun <T, C : Number?> Grouped<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateFor(this, columns)

/**
 * @include [MeanDocs.GroupedMeanForSnippet]
 * @set [MeanDocs.GroupedMeanForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the mean of the "age" values and the mean of the "weight" values
 * df.groupBy { city }.meanFor("age", "weight")
 * ```
 * @include [MeanDocs.ColumnNamesParam]
 * @include [MeanDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and the mean of each selected column per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByMean0", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.meanFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    meanFor(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.meanFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.meanFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN) { columns.toColumnSet() }

/**
 * @include [MeanDocs.GroupedMeanSnippet]
 * @set [MeanDocs.GroupedMeanSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the mean of all values in the "age" and "weight" columns,
 * // in a column called "average"
 * df.groupBy { city }.mean("average") { age and weight }
 * ```
 * @include [MeanDocs.ResultColumnNameParam]
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.ColumnsSelectorParam]
 * @return A new [DataFrame] with the group keys and a single mean per group.
 */
@Refine
@Interpretable("GroupByMean2")
public fun <T, C : Number?> Grouped<T>.mean(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateAll(this, name, columns)

/**
 * @include [MeanDocs.GroupedMeanSnippet]
 * @set [MeanDocs.GroupedMeanSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the mean of all values in the "age" and "weight" columns,
 * // in a column called "average"
 * df.groupBy { city }.mean("age", "weight", name = "average")
 * ```
 * @include [MeanDocs.ColumnNamesParam]
 * @include [MeanDocs.ResultColumnNameParam]
 * @include [MeanDocs.SkipNanParam]
 * @return A new [DataFrame] with the group keys and a single mean per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByMean2", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.mean(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(name, skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.mean(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.mean(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(name, skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [GroupBy] by computing the mean of the values that the given [expression]
 * returns for each row of a group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the mean per group, named [name] (or `"mean"` if [name] is `null`).
 *
 * @include [MeanDocs.RowExpressionSnippet]
 *
 * The result of the [expression\] is considered the 'input' of this operation.
 * @include [MeanDocs.SupportedTypesSnippet]
 * @include [MeanDocs.NanCellOnEmptySnippet]
 *
 * See also:
 * - [`mean`][Grouped.mean] — a single mean of all values in the selected columns, per group.
 * - [`sumOf`][Grouped.sumOf] — the sum of those values, per group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 *
 * @include [MeanDocs.GroupByUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of the weight-to-age ratios, in a column called "meanRatio"
 * df.groupBy { city }.meanOf("meanRatio") { (weight ?: 0) / age }
 * ```
 *
 * @param [name] The name of the resulting column. If `null` (the default), `"mean"` is used.
 *   This name needs to be unique, else a [DuplicateColumnPathInsertException] is thrown.
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.ExpressionParam]
 * @return A new [DataFrame] with the group keys and a single mean per group.
 */
@Refine
@Interpretable("GroupByMeanOf")
public inline fun <T, reified R : Number?> Grouped<T>.meanOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateOf(this, name, expression)

// endregion

// region Pivot

/**
 * Aggregates this [Pivot] by computing the mean of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the mean
 * of each suitable column of the corresponding group.
 *
 * @include [MeanDocs.AllSuitableColumnsSnippet]
 * @include [MeanDocs.SupportedTypesSnippet]
 * @include [MeanDocs.NanCellOnEmptySnippet]
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`meanFor`][Pivot.meanFor] — the same, but for an explicit selection of columns.
 * - [`mean`][Pivot.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 *
 * @include [MeanDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of each number column ("age" and "weight")
 * df.pivot { city }.mean()
 * ```
 *
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.SeparateParam]
 * @return A single [DataRow] with the mean of each suitable column per [pivot] group.
 */
public fun <T> Pivot<T>.mean(skipNaN: Boolean = skipNaNDefault, separate: Boolean = false): DataRow<T> =
    meanFor(skipNaN, separate, primitiveOrMixedNumberColumns())

/**
 * @include [MeanDocs.PivotMeanForSnippet]
 * @set [MeanDocs.PivotMeanForSnippet.NOTE] {@include [MeanDocs.AggregateColumnsSelectorSnippet]}
 * @set [MeanDocs.PivotMeanForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the mean of the "age" values and the mean of the "weight" values
 * df.pivot { city }.meanFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.meanFor(separate = true) { age and weight }
 * ```
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.SeparateParam]
 * @include [MeanDocs.AggregateColumnsSelectorParam]
 * @return A single [DataRow] with the mean of each selected column per [pivot] group.
 */
public fun <T, C : Number?> Pivot<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = delegate { meanFor(skipNaN, separate, columns) }

/**
 * @include [MeanDocs.PivotMeanForSnippet]
 * @set [MeanDocs.PivotMeanForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the mean of the "age" values and the mean of the "weight" values
 * df.pivot { city }.meanFor("age", "weight")
 * ```
 * @include [MeanDocs.ColumnNamesParam]
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.SeparateParam]
 * @return A single [DataRow] with the mean of each selected column per [pivot] group.
 */
public fun <T> Pivot<T>.meanFor(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNaN, separate) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.meanFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNaN, separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.meanFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNaN, separate) { columns.toColumnSet() }

/**
 * @include [MeanDocs.PivotMeanSnippet]
 * @set [MeanDocs.PivotMeanSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the mean of all values in the "age" and "weight" columns
 * df.pivot { city }.mean { age and weight }
 * ```
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.ColumnsSelectorParam]
 * @return A single [DataRow] with, per [pivot] group, the mean of all the values
 *   in the selected columns.
 */
public fun <T, R : Number?> Pivot<T>.mean(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataRow<T> = delegate { mean(skipNaN, columns) }

/**
 * Aggregates this [Pivot] by computing the mean of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the mean
 * of the expression's results for the rows of the corresponding group.
 *
 * @include [MeanDocs.RowExpressionSnippet]
 *
 * The result of [expression\] is treated as the 'input' of this operation.
 * @include [MeanDocs.SupportedTypesSnippet]
 * @include [MeanDocs.NanCellOnEmptySnippet]
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`mean`][Pivot.mean]` { columns }` — a single mean of all values in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 *
 * @include [MeanDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the mean of the weight-to-age ratios
 * df.pivot { city }.meanOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.ExpressionParam]
 * @return A single [DataRow] with, per [pivot] group, the mean of the expression's results.
 */
public inline fun <T, reified R : Number?> Pivot<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { meanOf(skipNaN, expression) }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by computing the mean of the values of
 * each suitable column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the mean of each suitable column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * @include [MeanDocs.AllSuitableColumnsSnippet]
 * @include [MeanDocs.SupportedTypesSnippet]
 * @include [MeanDocs.NanCellOnEmptyPivotSnippet]
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`meanFor`][PivotGroupBy.meanFor] — the same, but for an explicit selection of columns.
 * - [`mean`][PivotGroupBy.mean]` { columns }` — a single mean of all values in the selected columns,
 *   per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 *
 * @include [MeanDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the mean of each number column ("age" and "weight")
 * df.pivot { city }.groupBy { name.lastName }.mean()
 * ```
 *
 * @include [MeanDocs.SeparateParam]
 * @include [MeanDocs.SkipNanParam]
 * @return A [DataFrame] with the mean of each suitable column per group.
 */
public fun <T> PivotGroupBy<T>.mean(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    meanFor(skipNaN, separate, primitiveOrMixedNumberColumns())

/**
 * @include [MeanDocs.PivotGroupByMeanForSnippet]
 * @set [MeanDocs.PivotGroupByMeanForSnippet.NOTE] {@include [MeanDocs.AggregateColumnsSelectorSnippet]}
 * @set [MeanDocs.PivotGroupByMeanForSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the mean of the "age" values and the mean of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.meanFor { age and weight }
 * ```
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.SeparateParam]
 * @include [MeanDocs.AggregateColumnsSelectorParam]
 * @return A [DataFrame] with the mean of each selected column per group.
 */
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    skipNaN: Boolean = skipNaNDefault,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateFor(this, separate, columns)

/**
 * @include [MeanDocs.PivotGroupByMeanForSnippet]
 * @set [MeanDocs.PivotGroupByMeanForSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the mean of the "age" values and the mean of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.meanFor("age", "weight")
 * ```
 * @include [MeanDocs.ColumnNamesParam]
 * @include [MeanDocs.SeparateParam]
 * @include [MeanDocs.SkipNanParam]
 * @return A [DataFrame] with the mean of each selected column per group.
 */
public fun <T> PivotGroupBy<T>.meanFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN, separate) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN, separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = meanFor(skipNaN, separate) { columns.toColumnSet() }

/**
 * @include [MeanDocs.PivotGroupByMeanSnippet]
 * @set [MeanDocs.PivotGroupByMeanSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the mean of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.mean { age and weight }
 * ```
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.ColumnsSelectorParam]
 * @return A [DataFrame] with, per group, the mean of all the values in the selected columns.
 */
public fun <T, R : Number?> PivotGroupBy<T>.mean(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, R>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateAll(this, columns)

/**
 * @include [MeanDocs.PivotGroupByMeanSnippet]
 * @set [MeanDocs.PivotGroupByMeanSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the mean of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.mean("age", "weight")
 * ```
 * @include [MeanDocs.ColumnNamesParam]
 * @include [MeanDocs.SkipNanParam]
 * @return A [DataFrame] with, per group, the mean of all the values in the selected columns.
 */
public fun <T> PivotGroupBy<T>.mean(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    mean(skipNaN) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Number?> PivotGroupBy<T>.mean(
    vararg columns: ColumnReference<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R : Number?> PivotGroupBy<T>.mean(
    vararg columns: KProperty<R>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = mean(skipNaN) { columns.toColumnSet() }

/**
 * Aggregates this [PivotGroupBy] by computing the mean of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a [DataFrame] where each cell contains the mean of the expression's results for the
 * rows of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * @include [MeanDocs.RowExpressionSnippet]
 *
 * The result of the [expression\] is considered the 'input' of this operation.
 * @include [MeanDocs.SupportedTypesSnippet]
 * @include [MeanDocs.NanCellOnEmptyPivotSnippet]
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`mean`][PivotGroupBy.mean]` { columns }` — a single mean of all values in the selected columns,
 *   per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [MeanDocsLink]} — an overview of all `mean` modes.
 *
 * @include [MeanDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the mean of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.meanOf { (weight ?: 0) / age }
 * ```
 *
 * @include [MeanDocs.SkipNanParam]
 * @include [MeanDocs.ExpressionParam]
 * @return A [DataFrame] with, per group, the mean of the expression's results.
 */
public inline fun <T, reified R : Number?> PivotGroupBy<T>.meanOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.mean(skipNaN).aggregateOf(this, expression)

// endregion

// region binary compatibility

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun DataColumn<Number?>.mean(): Double = mean(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> DataColumn<T>.meanOf(crossinline expression: (T) -> R): Double =
    meanOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun DataRow<*>.rowMean(): Double = rowMean(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Number> DataRow<*>.rowMeanOf(): Double = rowMeanOf<T>(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.mean(): DataRow<T> = mean(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number> DataFrame<T>.meanFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> =
    meanFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.meanFor(vararg columns: String): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.meanFor(vararg columns: ColumnReference<C>): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.meanFor(vararg columns: KProperty<C>): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.mean(columns: ColumnsSelector<T, C>): Double =
    mean(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.mean(vararg columns: String): Double = mean(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.mean(vararg columns: ColumnReference<C>): Double =
    mean(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.mean(vararg columns: KProperty<C>): Double =
    mean(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified D : Number?> DataFrame<T>.meanOf(crossinline expression: RowExpression<T, D>): Double =
    meanOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.mean(): DataFrame<T> = mean(skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.meanFor(columns: ColumnsForAggregateSelector<T, C>): DataFrame<T> =
    meanFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.meanFor(vararg columns: String): DataFrame<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.meanFor(vararg columns: ColumnReference<C>): DataFrame<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.meanFor(vararg columns: KProperty<C>): DataFrame<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.mean(name: String? = null, columns: ColumnsSelector<T, C>): DataFrame<T> =
    mean(name, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.mean(vararg columns: String, name: String? = null): DataFrame<T> =
    mean(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.mean(vararg columns: ColumnReference<C>, name: String? = null): DataFrame<T> =
    mean(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.mean(vararg columns: KProperty<C>, name: String? = null): DataFrame<T> =
    mean(columns = columns, name = name, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> Grouped<T>.meanOf(
    name: String? = null,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = meanOf(name, skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.mean(separate: Boolean = false): DataRow<T> =
    mean(skipNaN = skipNaNDefault, separate = separate)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.meanFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = meanFor(skipNaN = skipNaNDefault, separate = separate, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.meanFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault, separate = separate)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.meanFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
): DataRow<T> = meanFor(columns = columns, skipNaN = skipNaNDefault, separate = separate)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.meanFor(vararg columns: KProperty<C>, separate: Boolean = false): DataRow<T> =
    meanFor(columns = columns, skipNaN = skipNaNDefault, separate = separate)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> Pivot<T>.mean(columns: ColumnsSelector<T, R>): DataRow<T> =
    mean(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> Pivot<T>.meanOf(crossinline expression: RowExpression<T, R>): DataRow<T> =
    meanOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.mean(separate: Boolean = false): DataFrame<T> = mean(separate, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = meanFor(skipNaN = skipNaNDefault, separate = separate, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.meanFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    meanFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
): DataFrame<T> = meanFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.meanFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
): DataFrame<T> = meanFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> PivotGroupBy<T>.mean(columns: ColumnsSelector<T, R>): DataFrame<T> =
    mean(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.mean(vararg columns: String): DataFrame<T> =
    mean(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> PivotGroupBy<T>.mean(vararg columns: ColumnReference<R>): DataFrame<T> =
    mean(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> PivotGroupBy<T>.mean(vararg columns: KProperty<R>): DataFrame<T> =
    mean(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(MEAN_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> PivotGroupBy<T>.meanOf(
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = meanOf(skipNaN = skipNaNDefault, expression = expression)

// endregion
