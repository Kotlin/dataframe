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
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs.STATISTIC_VERB
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
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveOrMixedNumber
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region docs

/**
 * {@comment
 *    The Std Operation KDoc-topic; it also holds all common `std` KDoc-snippets.
 *    Link to it with `{@include [StdDocsLink]}`.
 * }
 *
 * ## The Std Operation
 *
 * Computes the [standard deviation](https://en.wikipedia.org/wiki/Standard_deviation) of values —
 * a measure of how spread out the values are around their [mean][DataFrame.mean].
 *
 * @include [SupportedTypesSnippet]
 *
 * ### Std Modes
 *
 * Depending on what exactly you want the standard deviation of, there are several modes.
 * They are shown here for [DataFrame], but they exist for the other receivers too:
 *
 * - [`std`][DataFrame.std]`()` — the standard deviation of each suitable column separately.
 * - [`std`][DataFrame.std]` { columns }` — a single standard deviation of all values in all selected columns.
 * - [`stdFor`][DataFrame.stdFor]` { columns }` — the standard deviation of each selected column separately.
 * - [`stdOf`][DataFrame.stdOf]` { expression }` — the standard deviation of the values that the given
 *   expression returns for each row.
 *
 * ### Delta Degrees of Freedom (ddof)
 *
 * All `std` operations take a [`ddof`][DdofParam] ("Delta Degrees of Freedom") argument.
 * The divisor used in the calculation is `N - ddof`, where `N` is the number of values.
 * The default is `1`, meaning DataFrame applies
 * [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
 * "unbiased sample standard deviation" by default (as is standard in languages like R).
 * This is different from the "population standard deviation" (`ddof = 0`), which is the default
 * in libraries like Numpy.
 *
 * Related operation:
 * - [`mean`][DataFrame.mean] — the average of values (the standard deviation measures the spread around it).
 *
 * For more information: {@include [DocumentationUrls.Std]}
 *
 * For more information about [unifying numbers][UnifyingNumbers]:
 * @include [DocumentationUrls.NumberUnification]
 *
 * See all summary statistics:
 * @include [DocumentationUrls.Statistics]
 */
internal interface StdDocs : CommonStatisticsDocs {

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
     * @include [CommonStatisticsDocs.NaNHandlingSnippet]
     *
     * The result is always a [Double] and never `null`.
     * Converting [Long] values to [Double] may lose precision for very large values.
     *
     * For more information about the resulting types:
     * @include [DocumentationUrls.Std.TypeConversion]
     */
    @ExcludeFromSources
    typealias SupportedTypesSnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the modes with a single result. KDoc-snippet.}
     *
     * When there is nothing to compute the standard deviation of, for instance, when the input is empty
     * or contains only `null` values, the result is [Double.NaN]
     */
    @ExcludeFromSources
    typealias NanOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the modes with multiple results. KDoc-snippet.}
     *
     * Result cells for which there is nothing to compute the standard deviation of
     * (for instance, because the input was empty or contained only `null` values)
     * simply become [Double.NaN].
     *
     * For more information about the resulting types:
     * @include [DocumentationUrls.Std.TypeConversion]
     */
    @ExcludeFromSources
    typealias NanCellOnEmptySnippet = Nothing

    /**
     * {@comment Note about the behavior on empty input for the Pivot functions. KDoc-snippet.}
     *
     * Result cells for which there exists a group, but there is nothing to compute the
     * standard deviation of (for instance, because the group was empty or contained only `null` values)
     * simply become [Double.NaN].
     *
     * For more information about the resulting types:
     * @include [DocumentationUrls.Std.TypeConversion]
     *
     * @include [StdDocs.EmptyPivotIntersectionSnippet]
     */
    @ExcludeFromSources
    typealias NanCellOnEmptyPivotSnippet = Nothing

    /**
     * {@comment Note about which columns the no-argument `std` modes take into account. KDoc-snippet.}
     * @include [CommonStatisticsDocs.AllSuitableNumberColumnsSnippet] {@include [SetStdStatisticArgs]}
     */
    @ExcludeFromSources
    typealias AllSuitableColumnsSnippet = Nothing

    /** @include [CommonStatisticsDocs.ColumnGroupsIgnoredSnippet] {@include [SetStdStatisticArgs]} */
    @ExcludeFromSources
    typealias ColumnGroupsIgnoredSnippet = Nothing

    /**
     * @comment Version of [SelectingColumns] with correctly filled in examples
     * @include [SelectingColumns] {@include [SetStdOperationArg]}
     */
    typealias StdSelectingOptions = Nothing

    /**
     * @comment Version of [SelectingColumns] with correctly filled in examples
     * @include [SelectingColumns] {@include [SetStdForOperationArg]}
     */
    typealias StdForSelectingOptions = Nothing

    /**
     * @include [StdDocs.NanOnEmptySnippet]
     *
     * See also:
     * - [`stdOf`][DataColumn.stdOf] — the standard deviation of the values an expression returns for each element.
     * - [`mean`][DataColumn.mean] — the mean of the values in this column.
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * For more information:
     * @include [DocumentationUrls.Std]
     *
     * ### Example
     */
    @ExcludeFromSources
    typealias DataColumnStdSnippet = Nothing

    /**
     * {@comment The parts all [DataColumn.stdOf] overloads have in common. KDoc-snippet.}
     *
     * @include [StdDocs.ExpressionResultIsInputSnippet]
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanOnEmptySnippet]
     *
     * See also:
     * - [`std`][DataColumn.std] — the standard deviation of the values in this column itself.
     * - [`meanOf`][DataColumn.meanOf] — the mean of those values.
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * For more information:
     * @include [DocumentationUrls.Std]
     *
     * ### Example
     * $[EXAMPLE]
     *
     * @param [expression\] A function that returns the value to include for each element of this column.
     */
    @ExcludeFromSources
    interface DataColumnStdOfSnippet {

        // The example to render for this stdOf overload
        typealias EXAMPLE = Nothing
    }

    /**
     * @comment The parts all [DataRow.rowStdOf] overloads have in common. KDoc-snippet.
     *
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanOnEmptySnippet]
     *
     * See also:
     * - [`rowStd`][DataRow.rowStd] — the standard deviation of all the numbers in this row, of any number type.
     * - [`std`][DataFrame.std] — the standard deviation of the values in specific columns of a [DataFrame].
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * For more information:
     * @include [DocumentationUrls.RowStatistics]
     *
     * ### Example
     */
    @ExcludeFromSources
    typealias RowStdOfSnippet = Nothing

    /**
     * {@comment The parts all [DataFrame.stdFor] overloads have in common. KDoc-snippet.}
     *
     * Returns the standard deviation of the values of each selected column of this [DataFrame] separately.
     *
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanCellOnEmptySnippet]
     * $[NOTE]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][StdDocs.StdForSelectingOptions].
     *
     * See also:
     * - [`std`][DataFrame.std]`()` — the same, but for all suitable columns at once.
     * - [`std`][DataFrame.std]` { columns }` — a single standard deviation of all values in the selected columns.
     * - [`meanFor`][DataFrame.meanFor] — the mean of each selected column.
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * For more information: {@include [DocumentationUrls.Std]}
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface DataFrameStdForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this stdFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * @comment The parts all column-selecting [DataFrame.std] overloads have in common.
     *    KDoc-snippet.
     *
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanOnEmptySnippet]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See also:
     * - [`stdFor`][DataFrame.stdFor] — the standard deviation of each selected column separately.
     * - [`stdOf`][DataFrame.stdOf] — the standard deviation of the values a row expression returns for each row.
     * $[SEE_ALSO_TAIL]
     *
     * For more information: {@include [DocumentationUrls.Std]}
     *
     * $[COLUMNS_API]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface DataFrameStdSnippet {

        // The remaining "See also" bullets of this std overload.
        typealias SEE_ALSO_TAIL = Nothing

        // How this overload selects its columns: the Columns Selection DSL or column names
        typealias COLUMNS_API = Nothing

        // The example to render for this std overload
        typealias EXAMPLE = Nothing
    }

    /**
     * @comment The parts all [DataFrame.stdOf] overloads have in common.
     *    KDoc-snippet.
     *
     * @include [StdDocs.RowExpressionSnippet]
     *
     * @include [StdDocs.ExpressionResultIsInputSnippet]
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanOnEmptySnippet]
     *
     * See also:
     * - [`std`][DataFrame.std]` { columns }` — a single standard deviation of all values in the selected columns.
     * $[SEE_ALSO_TAIL]
     *
     * For more information: {@include [DocumentationUrls.Std]}
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface DataFrameStdOfSnippet {

        // The remaining "See also" bullets of this stdOf overload.
        typealias SEE_ALSO_TAIL = Nothing

        // The example to render for this stdOf overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [Grouped.stdFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [GroupBy] by computing the standard deviation of the values of
     * each selected column separately, per group.
     *
     * Returns a new [DataFrame] with one row per group, containing the group key columns
     * and a column with the standard deviation for each selected column.
     *
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanCellOnEmptySnippet]
     * $[NOTE]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][StdDocs.StdForSelectingOptions].
     *
     * See also:
     * - [`std`][Grouped.std]`()` — the same, but for all suitable columns at once.
     * - [`std`][Grouped.std]` { columns }` — a single standard deviation of all values in the selected columns,
     *   per group.
     * - [`meanFor`][Grouped.meanFor] — the mean of each selected column, per group.
     * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * @include [StdDocs.GroupByUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface GroupedStdForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this stdFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [Grouped.std] overloads have in common.
     *    KDoc-snippet.}
     *
     * Aggregates this [GroupBy] by computing a single standard deviation of all the values
     * in the selected columns, per group.
     *
     * Returns a new [DataFrame] with one row per group, containing the group key columns and
     * a single column with the standard deviation per group.
     * That column is named [name\], or, if [name\] is `null`, after the selected column
     * if exactly one column is selected, and `"std"` otherwise.
     *
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanCellOnEmptySnippet]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][StdDocs.StdSelectingOptions].
     *
     * See also:
     * - [`stdFor`][Grouped.stdFor] — the standard deviation of each selected column separately, per group.
     * - [`stdOf`][Grouped.stdOf] — the standard deviation of the values a row expression returns
     *   for each row of a group.
     * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * @include [StdDocs.GroupByUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface GroupedStdSnippet {

        // The example to render for this std overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [Pivot.stdFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [Pivot] by computing the standard deviation of the values of
     * each selected column separately, per group.
     *
     * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the standard
     * deviation of each selected column of the corresponding group.
     *
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanCellOnEmptyPivotSnippet]
     * $[NOTE]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][StdDocs.StdForSelectingOptions], or check out the
     * [`Pivot` Grammar][PivotDocs.Grammar].
     *
     * See also:
     * - [`std`][Pivot.std]`()` — the same, but for all suitable columns at once.
     * - [`std`][Pivot.std]` { columns }` — a single standard deviation of all values in the selected columns,
     *   per group.
     * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * @include [StdDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotStdForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this stdFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [Pivot.std] overloads have in common.
     *    KDoc-snippet.}
     *
     * Aggregates this [Pivot] by computing a single standard deviation of all the values
     * in the selected columns, per group.
     *
     * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the standard
     * deviation of all the values in the selected columns of the corresponding group.
     *
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanCellOnEmptyPivotSnippet]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][StdDocs.StdSelectingOptions], or check out the
     * [`Pivot` Grammar][PivotDocs.Grammar].
     *
     * See also:
     * - [`std`][Pivot.std]`()` — the standard deviation of each suitable column separately, per group.
     * - [`stdFor`][Pivot.stdFor] — the standard deviation of each selected column separately, per group.
     * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * @include [StdDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotStdSnippet {

        // The example to render for this std overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all [PivotGroupBy.stdFor] overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [PivotGroupBy] by computing the standard deviation of the values of
     * each selected column separately, per group.
     *
     * Returns a [DataFrame] where each cell contains the standard deviation of each selected column
     * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
     *
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanCellOnEmptyPivotSnippet]
     * $[NOTE]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][StdDocs.StdForSelectingOptions], or check out the
     * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
     *
     * See also:
     * - [`std`][PivotGroupBy.std]`()` — the same, but for all suitable columns at once.
     * - [`std`][PivotGroupBy.std]` { columns }` — a single standard deviation of all values in the
     *   selected columns, per group.
     * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
     *   a [PivotGroupBy].
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * @include [StdDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotGroupByStdForSnippet {

        // The note about the aggregate columns selector; can be omitted
        typealias NOTE = Nothing

        // The example to render for this stdFor overload
        typealias EXAMPLE = Nothing
    }

    /**
     * {@comment The parts all column-selecting [PivotGroupBy.std] overloads have in common.
     *    KDoc-snippet.}
     *
     * Aggregates this [PivotGroupBy] by computing a single standard deviation of all the values
     * in the selected columns, per group.
     *
     * Returns a [DataFrame] where each cell contains the standard deviation of all the values in the
     * selected columns of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
     *
     * @include [StdDocs.SupportedTypesSnippet]
     * @include [StdDocs.NanCellOnEmptyPivotSnippet]
     * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
     *
     * See [Selecting Columns][StdDocs.StdSelectingOptions], or check out the
     * [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
     *
     * See also:
     * - [`std`][PivotGroupBy.std]`()` — the standard deviation of each suitable column separately, per group.
     * - [`stdFor`][PivotGroupBy.stdFor] — the standard deviation of each selected column separately, per group.
     * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
     *   a [PivotGroupBy].
     * - {@include [StdDocsLink]} — an overview of all `std` modes.
     *
     * @include [StdDocs.PivotUrlsSnippet]
     *
     * ### Example
     * $[EXAMPLE]
     */
    @ExcludeFromSources
    interface PivotGroupByStdSnippet {

        // The example to render for this std overload
        typealias EXAMPLE = Nothing
    }

    /** @include [CommonStatisticsDocs.ColumnsSelectorParam] {@include [SetStdStatisticArgs]} */
    @ExcludeFromSources
    typealias ColumnsSelectorParam = Nothing

    /** @include [CommonStatisticsDocs.AggregateColumnsSelectorParam] {@include [SetStdStatisticArgs]} */
    @ExcludeFromSources
    typealias AggregateColumnsSelectorParam = Nothing

    /**
     * @include [CommonStatisticsDocs.ColumnNamesParam] {@include [SetStdStatisticArgs]}
     *   These must be primitive number columns, else an [IllegalArgumentException] is thrown.
     */
    @ExcludeFromSources
    typealias ColumnNamesParam = Nothing

    /** @include [CommonStatisticsDocs.ExpressionParam] {@include [SetStdStatisticArgs]} */
    @ExcludeFromSources
    typealias ExpressionParam = Nothing

    /** @include [CommonStatisticsDocs.RowValuesTypeParam] {@include [SetStdStatisticArgs]} */
    @ExcludeFromSources
    typealias RowValuesTypeParam = Nothing

    /** @include [CommonStatisticsDocs.ResultColumnNameParam] {@include [SetStdStatisticArgs]} */
    @ExcludeFromSources
    typealias ResultColumnNameParam = Nothing

    /** @include [CommonStatisticsDocs.ExpressionResultColumnNameParam] {@include [SetStdStatisticArgs]} */
    @ExcludeFromSources
    typealias ExpressionResultColumnNameParam = Nothing

    /**
     * @comment The shared `ddof` parameter documentation. KDoc-snippet.
     *
     * @param [ddof\] "Delta Degrees of Freedom". The divisor used in the calculation is `N - ddof`,
     *   where `N` is the number of values. The default is `1`, which applies
     *   [Bessel's correction](https://en.wikipedia.org/wiki/Bessel%27s_correction) and computes the
     *   unbiased sample standard deviation (as in R). Use `0` for the population standard deviation
     *   (as in Numpy).
     */
    @ExcludeFromSources
    typealias DdofParam = Nothing
}

/** [The Std Operation][StdDocs] */
@ExcludeFromSources
private typealias StdDocsLink = Nothing

/** {@set [STATISTIC] standard deviation}{@set [STATISTIC_VERB] include}{@set [STATISTIC_COLUMN_NAME] `"std"`} */
@ExcludeFromSources
private typealias SetStdStatisticArgs = Nothing

/** {@set [SelectingColumns.OPERATION] [std][std]} */
@ExcludeFromSources
private typealias SetStdOperationArg = Nothing

/** {@set [SelectingColumns.OPERATION] [stdFor][stdFor]} */
@ExcludeFromSources
private typealias SetStdForOperationArg = Nothing

// endregion

// region DataColumn

/**
 * Returns the standard deviation of the values in this [DataColumn], as a [Double].
 *
 * @include [StdDocs.SupportedTypesSnippet]
 * @include [StdDocs.DataColumnStdSnippet]
 * ```kotlin
 * // The standard deviation of all ages in the "age" Int column
 * df.age.std()
 * // The population standard deviation of all weights in the "weight" `Double?` column
 * df.weight.std(ddof = 0)
 * ```
 *
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return The standard deviation of the values in this column, as a [Double].
 */
public fun DataColumn<Number?>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): Double =
    Aggregators.std(skipNaN, ddof).aggregateSingleColumn(this)

/**
 * Returns the standard deviation of the values that the given [expression] returns
 * for each element of this [DataColumn], as a [Double].
 *
 * @include [StdDocs.DataColumnStdOfSnippet]
 * @set [StdDocs.DataColumnStdOfSnippet.EXAMPLE]
 * ```kotlin
 * // The standard deviation of the lengths of all first names in the "name"/"firstName" column
 * df.name.firstName.stdOf { it.length }
 * ```
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return The standard deviation of the values [expression] returns, as a [Double].
 */
public inline fun <T, reified R : Number?> DataColumn<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    noinline expression: (T) -> R,
): Double = Aggregators.std(skipNaN, ddof).aggregateOf(this, expression)

// endregion

// region DataRow

/**
 * Returns the standard deviation of all the numbers in this [DataRow], as a [Double].
 *
 * Only the values in the columns of a primitive number type (and in "mixed" [Number] columns)
 * are taken into account; all other columns of the row are ignored.
 * @include [StdDocs.ColumnGroupsIgnoredSnippet]
 *
 * Since the values of different columns are combined together, the result is the standard deviation
 * of all those values converted to their common type.
 *
 * @include [StdDocs.SupportedTypesSnippet]
 * @include [StdDocs.NanOnEmptySnippet]
 *
 * See also:
 * - [`rowStdOf<Type>()`][DataRow.rowStdOf] — the standard deviation of the values of one specific number type in this row.
 * - [`rowMean`][DataRow.rowMean] — the mean of all the numbers in this row.
 * - [`std`][DataFrame.std] — the standard deviation of the values in specific columns of a [DataFrame].
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 *
 * For more information: {@include [DocumentationUrls.RowStatistics]}
 *
 * ### Example
 * ```kotlin
 * // The standard deviation of all numbers ("age" and "weight") in the first row
 * // Columns of other types ("name" and "address") are ignored
 * df[0].rowStd()
 * ```
 *
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return The standard deviation of all the numbers in this row, as a [Double].
 */
public fun DataRow<*>.rowStd(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): Double =
    Aggregators.std(skipNaN, ddof).aggregateOfRow(this, primitiveOrMixedNumberColumns())

/**
 * Returns the standard deviation of the values of type [T] in this [DataRow], as a [Double].
 *
 * Only the values in the columns of type [T] (or its nullable variant) are taken into account;
 * all other columns of the row are ignored.
 * @include [StdDocs.ColumnGroupsIgnoredSnippet]
 *
 * [T] must be a primitive number type or [Number] itself.
 *
 * @include [StdDocs.RowStdOfSnippet]
 * ```kotlin
 * // The standard deviation of all `Int` values ("age" and "weight") in the first row
 * df[0].rowStdOf<Int>()
 * // The population standard deviation of all `Double` values in the first row, ignoring `NaN` values
 * df[0].rowStdOf<Double>(skipNaN = true, ddof = 0)
 * ```
 *
 * @include [StdDocs.RowValuesTypeParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return The standard deviation of the values of type [T] in this row, as a [Double].
 * @throws IllegalArgumentException if [T] is not a primitive number type or [Number] itself.
 */
public inline fun <reified T : Number?> DataRow<*>.rowStdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): Double {
    require(typeOf<T>().isPrimitiveOrMixedNumber()) {
        "Type ${T::class.simpleName} is not a primitive number type. Std only supports primitive number types."
    }
    return Aggregators.std(skipNaN, ddof).aggregateOfRow(this) { colsOf<T>() }
}

// endregion

// region DataFrame

/**
 * Returns the standard deviation of the values of each suitable column of this [DataFrame] separately.
 *
 * @include [StdDocs.AllSuitableColumnsSnippet]
 * @include [StdDocs.SupportedTypesSnippet]
 * @include [StdDocs.NanCellOnEmptySnippet]
 *
 * See also:
 * - [`stdFor`][DataFrame.stdFor] — the same, but for an explicit selection of columns.
 * - [`std`][DataFrame.std]` { columns }` — a single standard deviation of all values in the selected columns.
 * - [`mean`][DataFrame.mean] — the mean of each column.
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 *
 * For more information: {@include [DocumentationUrls.Std]}
 *
 * ### Example
 * ```kotlin
 * // A single row with the standard deviation of each number column ("age" and "weight")
 * df.std()
 * ```
 *
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A single [DataRow] with the standard deviation of each suitable column of this [DataFrame].
 */
@Refine
@Interpretable("Std0")
public fun <T> DataFrame<T>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): DataRow<T> =
    stdFor(skipNaN, ddof, primitiveOrMixedNumberColumns())

/**
 * @include [StdDocs.DataFrameStdForSnippet]
 * @set [StdDocs.DataFrameStdForSnippet.NOTE] {@include [StdDocs.AggregateColumnsSelectorSnippet]}
 * @set [StdDocs.DataFrameStdForSnippet.EXAMPLE]
 * ```kotlin
 * // A single row with the standard deviation of the "age" values and of the "weight" values
 * df.stdFor { age and weight }
 * // The same, ignoring `NaN` values, and naming the results explicitly
 * df.stdFor(skipNaN = true) { age into "stdAge" and (weight into "stdWeight") }
 * ```
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.AggregateColumnsSelectorParam]
 * @return A single [DataRow] with the standard deviation of each selected column.
 */
@Refine
@Interpretable("Std1")
public fun <T, C : Number?> DataFrame<T>.stdFor(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.std(skipNaN, ddof).aggregateFor(this, columns)

/**
 * @include [StdDocs.DataFrameStdForSnippet]
 * @set [StdDocs.DataFrameStdForSnippet.EXAMPLE]
 * ```kotlin
 * // A single row with the standard deviation of the "age" values and of the "weight" values
 * df.stdFor("age", "weight")
 * ```
 * @include [StdDocs.ColumnNamesParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A single [DataRow] with the standard deviation of each selected column.
 */
@Refine
@StringApiInterpretable(interpreter = "Std1", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.stdFor(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(skipNaN, ddof) { columns.toColumnsSetOf() }

public fun <T, C : Number?> DataFrame<T>.stdFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.stdFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

/**
 * Returns a single standard deviation of all the values in the selected columns of this [DataFrame],
 * as a [Double].
 *
 * @include [StdDocs.DataFrameStdSnippet]
 * @set [StdDocs.DataFrameStdSnippet.SEE_ALSO_TAIL]
 * - [`mean`][DataFrame.mean] — the mean of all values in the selected columns.
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 * @set [StdDocs.DataFrameStdSnippet.COLUMNS_API] {@include [SelectingColumns.ColumnsSelectionDsl]}
 * @set [StdDocs.DataFrameStdSnippet.EXAMPLE]
 * ```kotlin
 * // The standard deviation of all values in the "age" and "weight" columns together
 * df.std { age and weight }
 * ```
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.ColumnsSelectorParam]
 * @return The standard deviation of all the values in the selected columns, as a [Double].
 */
public fun <T> DataFrame<T>.std(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, Number?>,
): Double = Aggregators.std(skipNaN, ddof).aggregateAll(this, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.std(vararg columns: ColumnReference<C>): Double = std { columns.toColumnSet() }

/**
 * Returns a single standard deviation of all the values in the selected columns of this [DataFrame],
 * as a [Double].
 *
 * @include [StdDocs.DataFrameStdSnippet]
 * @set [StdDocs.DataFrameStdSnippet.SEE_ALSO_TAIL]
 * - [`mean`][DataFrame.mean] — the mean of all values in the selected columns.
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 * @set [StdDocs.DataFrameStdSnippet.COLUMNS_API] {@include [SelectingColumns.ColumnNamesApi]}
 * @set [StdDocs.DataFrameStdSnippet.EXAMPLE]
 * ```kotlin
 * // The standard deviation of all values in the "age" and "weight" columns together
 * df.std("age", "weight")
 * ```
 * @include [StdDocs.ColumnNamesParam]
 * @return The standard deviation of all the values in the selected columns, as a [Double].
 */
public fun <T> DataFrame<T>.std(vararg columns: String): Double = std { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.std(vararg columns: KProperty<C>): Double = std { columns.toColumnSet() }

/**
 * Returns the standard deviation of the values that the given [expression] returns
 * for each row of this [DataFrame], as a [Double].
 *
 * @include [StdDocs.DataFrameStdOfSnippet]
 * @set [StdDocs.DataFrameStdOfSnippet.SEE_ALSO_TAIL]
 * - [`meanOf`][DataFrame.meanOf] — the mean of those values.
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 * @set [StdDocs.DataFrameStdOfSnippet.EXAMPLE]
 * ```kotlin
 * // The standard deviation of the weight-to-age ratios of all rows
 * df.stdOf { (weight ?: 0) / age }
 * ```
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.ExpressionParam]
 * @return The standard deviation of the values [expression] returns, as a [Double].
 */
public inline fun <T, reified R : Number?> DataFrame<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, R>,
): Double = Aggregators.std(skipNaN, ddof).aggregateOf(this, expression)

// endregion

// region GroupBy

/**
 * Aggregates this [GroupBy] by computing the standard deviation of the values of
 * each suitable column separately, per group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns
 * and a column with the standard deviation for each suitable column.
 *
 * @include [StdDocs.AllSuitableColumnsSnippet]
 * @include [StdDocs.SupportedTypesSnippet]
 * @include [StdDocs.NanCellOnEmptySnippet]
 *
 * See also:
 * - [`stdFor`][Grouped.stdFor] — the same, but for an explicit selection of columns.
 * - [`std`][Grouped.std]` { columns }` — a single standard deviation of all values in the selected columns, per group.
 * - [`mean`][Grouped.mean] — the mean of each column, per group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 *
 * @include [StdDocs.GroupByUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of each number column ("age" and "weight")
 * df.groupBy { city }.std()
 * ```
 *
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A new [DataFrame] with the group keys and the standard deviation of each suitable column per group.
 */
@Refine
@Interpretable("GroupByStd1")
public fun <T> Grouped<T>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): DataFrame<T> =
    stdFor(skipNaN, ddof, primitiveOrMixedNumberColumns())

/**
 * @include [StdDocs.GroupedStdForSnippet]
 * @set [StdDocs.GroupedStdForSnippet.NOTE] {@include [StdDocs.AggregateColumnsSelectorSnippet]}
 * @set [StdDocs.GroupedStdForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the standard deviation of the "age" values and of the "weight" values
 * df.groupBy { city }.stdFor { age and weight }
 * ```
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.AggregateColumnsSelectorParam]
 * @return A new [DataFrame] with the group keys and the standard deviation of each selected column per group.
 */
@Refine
@Interpretable("GroupByStd0")
public fun <T, C : Number?> Grouped<T>.stdFor(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateFor(this, columns)

/**
 * @include [StdDocs.GroupedStdForSnippet]
 * @set [StdDocs.GroupedStdForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the standard deviation of the "age" values and of the "weight" values
 * df.groupBy { city }.stdFor("age", "weight")
 * ```
 * @include [StdDocs.ColumnNamesParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A new [DataFrame] with the group keys and the standard deviation of each selected column per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByStd0", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.stdFor(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.stdFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.stdFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(skipNaN, ddof) { columns.toColumnSet() }

/**
 * @include [StdDocs.GroupedStdSnippet]
 * @set [StdDocs.GroupedStdSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the standard deviation of all values in the "age" and "weight" columns,
 * // in a column called "deviation"
 * df.groupBy { city }.std("deviation") { age and weight }
 * ```
 * @include [StdDocs.ResultColumnNameParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.ColumnsSelectorParam]
 * @return A new [DataFrame] with the group keys and a single standard deviation per group.
 */
@Refine
@Interpretable("GroupByStd2")
public fun <T, C : Number?> Grouped<T>.std(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateAll(this, name, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.std(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(name, skipNaN, ddof) { columns.toColumnSet() }

/**
 * @include [StdDocs.GroupedStdSnippet]
 * @set [StdDocs.GroupedStdSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the standard deviation of all values in the "age" and "weight" columns,
 * // in a column called "deviation"
 * df.groupBy { city }.std("age", "weight", name = "deviation")
 * ```
 * @include [StdDocs.ColumnNamesParam]
 * @include [StdDocs.ResultColumnNameParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A new [DataFrame] with the group keys and a single standard deviation per group.
 */
@Refine
@StringApiInterpretable(interpreter = "GroupByStd2", stringArgument = "columns", targetArgument = "columns")
public fun <T> Grouped<T>.std(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(name, skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.std(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(name, skipNaN, ddof) { columns.toColumnSet() }

/**
 * Aggregates this [GroupBy] by computing the standard deviation of the values that the given [expression]
 * returns for each row of a group.
 *
 * Returns a new [DataFrame] with one row per group, containing the group key columns and
 * a single column with the standard deviation per group, named [name] (or `"std"` if [name] is `null`).
 *
 * @include [StdDocs.RowExpressionSnippet]
 *
 * @include [StdDocs.ExpressionResultIsInputSnippet]
 * @include [StdDocs.SupportedTypesSnippet]
 * @include [StdDocs.NanCellOnEmptySnippet]
 *
 * See also:
 * - [`std`][Grouped.std] — a single standard deviation of all values in the selected columns, per group.
 * - [`meanOf`][Grouped.meanOf] — the mean of those values, per group.
 * - [`aggregate`][Grouped.aggregate] — the general way to aggregate groups.
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 *
 * @include [StdDocs.GroupByUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of the weight-to-age ratios, in a column called "stdRatio"
 * df.groupBy { city }.stdOf("stdRatio") { (weight ?: 0) / age }
 * ```
 *
 * @include [StdDocs.ExpressionResultColumnNameParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.ExpressionParam]
 * @return A new [DataFrame] with the group keys and a single standard deviation per group.
 */
@Refine
@Interpretable("GroupByStdOf")
public inline fun <T, reified R : Number?> Grouped<T>.stdOf(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateOf(this, name, expression)

// endregion

// region Pivot

/**
 * Aggregates this [Pivot] by computing the standard deviation of the values of
 * each suitable column separately, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the standard
 * deviation of each suitable column of the corresponding group.
 *
 * @include [StdDocs.AllSuitableColumnsSnippet]
 * @include [StdDocs.SupportedTypesSnippet]
 * @include [StdDocs.NanCellOnEmptySnippet]
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`stdFor`][Pivot.stdFor] — the same, but for an explicit selection of columns.
 * - [`std`][Pivot.std]` { columns }` — a single standard deviation of all values in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 *
 * @include [StdDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of each number column ("age" and "weight")
 * df.pivot { city }.std()
 * ```
 *
 * @include [StdDocs.SeparateParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A single [DataRow] with the standard deviation of each suitable column per [pivot] group.
 */
public fun <T> Pivot<T>.std(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof, primitiveOrMixedNumberColumns())

/**
 * @include [StdDocs.PivotStdForSnippet]
 * @set [StdDocs.PivotStdForSnippet.NOTE] {@include [StdDocs.AggregateColumnsSelectorSnippet]}
 * @set [StdDocs.PivotStdForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the standard deviation of the "age" values and of the "weight" values
 * df.pivot { city }.stdFor { age and weight }
 * // The same, but with the results grouped by aggregated column instead of by city
 * df.pivot { city }.stdFor(separate = true) { age and weight }
 * ```
 * @include [StdDocs.SeparateParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.AggregateColumnsSelectorParam]
 * @return A single [DataRow] with the standard deviation of each selected column per [pivot] group.
 */
public fun <T, R : Number?> Pivot<T>.stdFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { stdFor(separate, skipNaN, ddof, columns) }

/**
 * @include [StdDocs.PivotStdForSnippet]
 * @set [StdDocs.PivotStdForSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the standard deviation of the "age" values and of the "weight" values
 * df.pivot { city }.stdFor("age", "weight")
 * ```
 * @include [StdDocs.ColumnNamesParam]
 * @include [StdDocs.SeparateParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A single [DataRow] with the standard deviation of each selected column per [pivot] group.
 */
public fun <T> Pivot<T>.stdFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnsSetOf() }

public fun <T, C : Number?> Pivot<T>.stdFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.stdFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

/**
 * @include [StdDocs.PivotStdSnippet]
 * @set [StdDocs.PivotStdSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the standard deviation of all values in the "age" and "weight" columns
 * df.pivot { city }.std { age and weight }
 * ```
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.ColumnsSelectorParam]
 * @return A single [DataRow] with, per [pivot] group, the standard deviation of all the values
 *   in the selected columns.
 */
public fun <T, C : Number?> Pivot<T>.std(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { std(skipNaN, ddof, columns) }

public fun <T, C : Number?> Pivot<T>.std(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = std(skipNaN, ddof) { columns.toColumnSet() }

/**
 * @include [StdDocs.PivotStdSnippet]
 * @set [StdDocs.PivotStdSnippet.EXAMPLE]
 * ```kotlin
 * // For each city, the standard deviation of all values in the "age" and "weight" columns
 * df.pivot { city }.std("age", "weight")
 * ```
 * @include [StdDocs.ColumnNamesParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A single [DataRow] with, per [pivot] group, the standard deviation of all the values
 *   in the selected columns.
 */
public fun <T> Pivot<T>.std(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = std(skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.std(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataRow<T> = std(skipNaN, ddof) { columns.toColumnSet() }

/**
 * Aggregates this [Pivot] by computing the standard deviation of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a single [DataRow] with the [pivot] keys as (nested) columns, containing the standard
 * deviation of the expression's results for the rows of the corresponding group.
 *
 * @include [StdDocs.RowExpressionSnippet]
 *
 * @include [StdDocs.ExpressionResultIsInputSnippet]
 * @include [StdDocs.SupportedTypesSnippet]
 * @include [StdDocs.NanCellOnEmptySnippet]
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * See also:
 * - [`std`][Pivot.std]` { columns }` — a single standard deviation of all values in the selected columns, per group.
 * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 *
 * @include [StdDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // For each city, the standard deviation of the weight-to-age ratios
 * df.pivot { city }.stdOf { (weight ?: 0) / age }
 * ```
 *
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.ExpressionParam]
 * @return A single [DataRow] with, per [pivot] group, the standard deviation of the expression's results.
 */
public inline fun <reified T : Number?> Pivot<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, T>,
): DataRow<T> = delegate { stdOf(skipNaN, ddof, expression) }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by computing the standard deviation of the values of
 * each suitable column separately, per group.
 *
 * Returns a [DataFrame] where each cell contains the standard deviation of each suitable column
 * of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * @include [StdDocs.AllSuitableColumnsSnippet]
 * @include [StdDocs.SupportedTypesSnippet]
 * @include [StdDocs.NanCellOnEmptyPivotSnippet]
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`stdFor`][PivotGroupBy.stdFor] — the same, but for an explicit selection of columns.
 * - [`std`][PivotGroupBy.std]` { columns }` — a single standard deviation of all values in the
 *   selected columns, per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 *
 * @include [StdDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the standard deviation of each number column ("age" and "weight")
 * df.pivot { city }.groupBy { name.lastName }.std()
 * ```
 *
 * @include [StdDocs.SeparateParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A [DataFrame] with the standard deviation of each suitable column per group.
 */
public fun <T> PivotGroupBy<T>.std(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof, primitiveOrMixedNumberColumns())

/**
 * @include [StdDocs.PivotGroupByStdForSnippet]
 * @set [StdDocs.PivotGroupByStdForSnippet.NOTE] {@include [StdDocs.AggregateColumnsSelectorSnippet]}
 * @set [StdDocs.PivotGroupByStdForSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the standard deviation of the "age" values and of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.stdFor { age and weight }
 * ```
 * @include [StdDocs.SeparateParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.AggregateColumnsSelectorParam]
 * @return A [DataFrame] with the standard deviation of each selected column per group.
 */
public fun <T, R : Number?> PivotGroupBy<T>.stdFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateFor(this, separate, columns)

/**
 * @include [StdDocs.PivotGroupByStdForSnippet]
 * @set [StdDocs.PivotGroupByStdForSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the standard deviation of the "age" values and of the "weight" values
 * df.pivot { city }.groupBy { name.lastName }.stdFor("age", "weight")
 * ```
 * @include [StdDocs.ColumnNamesParam]
 * @include [StdDocs.SeparateParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A [DataFrame] with the standard deviation of each selected column per group.
 */
public fun <T> PivotGroupBy<T>.stdFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.stdFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.stdFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = stdFor(separate, skipNaN, ddof) { columns.toColumnSet() }

/**
 * @include [StdDocs.PivotGroupByStdSnippet]
 * @set [StdDocs.PivotGroupByStdSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the standard deviation of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.std { age and weight }
 * ```
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.ColumnsSelectorParam]
 * @return A [DataFrame] with, per group, the standard deviation of all the values in the selected columns.
 */
public fun <T, C : Number?> PivotGroupBy<T>.std(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateAll(this, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.std(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(skipNaN, ddof) { columns.toColumnSet() }

/**
 * @include [StdDocs.PivotGroupByStdSnippet]
 * @set [StdDocs.PivotGroupByStdSnippet.EXAMPLE]
 * ```kotlin
 * // Per city and last name, the standard deviation of all values in the "age" and "weight" columns
 * df.pivot { city }.groupBy { name.lastName }.std("age", "weight")
 * ```
 * @include [StdDocs.ColumnNamesParam]
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @return A [DataFrame] with, per group, the standard deviation of all the values in the selected columns.
 */
public fun <T> PivotGroupBy<T>.std(
    vararg columns: String,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(skipNaN, ddof) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.std(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
): DataFrame<T> = std(skipNaN, ddof) { columns.toColumnSet() }

/**
 * Aggregates this [PivotGroupBy] by computing the standard deviation of the values that the given [expression]
 * returns for each row, per group.
 *
 * Returns a [DataFrame] where each cell contains the standard deviation of the expression's results for the
 * rows of the group corresponding to that [pivot] key (column) and [groupBy] key (row).
 *
 * @include [StdDocs.RowExpressionSnippet]
 *
 * @include [StdDocs.ExpressionResultIsInputSnippet]
 * @include [StdDocs.SupportedTypesSnippet]
 * @include [StdDocs.NanCellOnEmptyPivotSnippet]
 *
 * Check out the [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also:
 * - [`std`][PivotGroupBy.std]` { columns }` — a single standard deviation of all values in the
 *   selected columns, per group.
 * - [PivotGroupBy aggregation][PivotGroupByDocs.Aggregation] — all other ways to aggregate
 *   a [PivotGroupBy].
 * - {@include [StdDocsLink]} — an overview of all `std` modes.
 *
 * @include [StdDocs.PivotUrlsSnippet]
 *
 * ### Example
 * ```kotlin
 * // Per city and last name, the standard deviation of the weight-to-age ratios
 * df.pivot { city }.groupBy { name.lastName }.stdOf { (weight ?: 0) / age }
 * ```
 *
 * @include [StdDocs.SkipNanParam]
 * @include [StdDocs.DdofParam]
 * @include [StdDocs.ExpressionParam]
 * @return A [DataFrame] with, per group, the standard deviation of the expression's results.
 */
public inline fun <T, reified R : Number?> PivotGroupBy<T>.stdOf(
    skipNaN: Boolean = skipNaNDefault,
    ddof: Int = ddofDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.std(skipNaN, ddof).aggregateOf(this, expression)

// endregion
