package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.NALink
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.columnValues
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.remainingColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.valuesImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region docs

/**
 * Extracts the values of the selected columns of this [DataFrame] into a lazy [Sequence].
 *
 * By default, the values come column by column: all values of the first selected column,
 * then all values of the second one, and so on. With the row-by-row traversal enabled, the
 * values come row by row instead: for each row, the values of the selected columns in the
 * order the columns were selected.
 *
 * The [Sequence] is lazy — the values are pulled from this [DataFrame] one by one, as the
 * sequence is consumed.
 *
 * Don't confuse this `values` with the aggregation [values][Grouped.values], which collects
 * the values of the selected columns for every group of a [groupBy][DataFrame.groupBy] or
 * [pivot][DataFrame.pivot] result.
 *
 * See also:
 * - [valuesNotNull][DataFrame.valuesNotNull] — the same, but `null` values are skipped.
 * - [asSequence][DataFrame.asSequence] — a [Sequence] over the [rows][DataRow] of this [DataFrame].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * For more information: {@include [DocumentationUrls.Values]}
 *
 * ### This `values` overload
 */
@ExcludeFromSources
internal interface ValuesDocs {

    /**
     * @param [\byRow] When `true`, the values come row by row: for each row, the values of
     *   all selected columns in the order the columns were selected. When `false` (default),
     *   the values come column by column: all values of the first selected column, then all
     *   values of the second one, and so on.
     */
    typealias ByRowSnippet = Nothing

    /**
     * @param [\columns] The [ColumnsSelector] used to select the columns to extract
     *   the values from.
     */
    typealias ColumnsSelectorParamSnippet = Nothing

    /**
     * @return A lazy [Sequence] of the values of the selected columns.
     */
    typealias ReturnSnippet = Nothing
}

/**
 * Extracts the values of the selected columns of this [DataFrame] into a lazy [Sequence],
 * skipping `null` values.
 *
 * The order of the values is the same as in [values][DataFrame.values]: column by column
 * by default, row by row with `byRow = true`.
 *
 * See also:
 * - [values][DataFrame.values] — the same, but `null` values are included.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * For more information: {@include [DocumentationUrls.Values]}
 *
 * ### This `valuesNotNull` overload
 */
@ExcludeFromSources
internal interface ValuesNotNullDocs {

    /**
     * @return A lazy [Sequence] of the non-`null` values of the selected columns.
     */
    typealias ReturnSnippet = Nothing
}

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [values][DataFrame.values] on a [DataFrame], which
 * extracts the values of the selected columns into a lazy [Sequence], without any grouping.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * For more information: {@include [DocumentationUrls.Values]}
 *
 * ### This `values` overload
 */
@ExcludeFromSources
internal interface ValuesInAggregationDocs {

    /**
     * @param [\dropNA] When `true`, {@include [NALink]} values are removed from the collected
     *   values. `false` by default. Each column is filtered independently of the others.
     */
    typealias DropNASnippet = Nothing

    /**
     * @param [\distinct] When `true`, only distinct values are collected: the first occurrence
     *   of each value, in the order the values appear. `false` by default.
     */
    typealias DistinctSnippet = Nothing

    /**
     * @param [\separate] When several columns are selected: when `true`, each selected column
     *   becomes its own column group with one nested column per pivot value; when `false`
     *   (default), each pivot value becomes its own column group with one nested column per
     *   selected column. When a single column is selected, its values go directly into the
     *   pivot cells, and this parameter has no effect.
     */
    typealias SeparateSnippet = Nothing

    /**
     * @param [\columns] The [ColumnsForAggregateSelector] used to select the columns to
     *   collect the values from.
     */
    typealias ColumnsForAggregateSelectorParamSnippet = Nothing

    /**
     * @param [\columns] The names of the columns to collect the values from.
     */
    typealias ColumnNamesParamSnippet = Nothing

    /**
     * When the groups have lost track of the key columns — this can happen after
     * [updateGroups][GroupBy.updateGroups], [filter][GroupBy.filter], or [add][GroupBy.add]
     * changed the columns inside the groups — the collected columns can collide with the key
     * columns, and a [DuplicateColumnPathInsertException] is thrown with a hint about the
     * cause. Try renaming the columns inside the groups or aggregate them manually instead.
     */
    typealias LostKeyColumnsSnippet = Nothing

    /**
     * the single value if there is exactly one, a [List] if there are several, and an empty
     *   cell if there is nothing to collect. When the other cells of that column hold
     *   lists, a cell holds a [List] too: a single value as a one-element [List], a cell
     *   with no rows as an empty [List]; a cell with no rows is `null` otherwise, and a
     *   cell emptied by `dropNA` is always `null`. A selected column group is collected
     *   as a nested [DataFrame] with the collected rows of the columns inside it, instead
     *   of a [List].
     */
    typealias OneOrManyCellSnippet = Nothing

    /**
     * @return A new [DataFrame] with one row per group: the group keys, and, per selected
     *   column, a [List] with all values from the group. A selected column group is
     *   collected as a nested [DataFrame] with the collected rows of the columns inside it.
     */
    typealias GroupedReturnSnippet = Nothing

    /**
     * @return A new [DataFrame] with one row per group: the group keys, and, per selected
     *   column, the value from the row that the reduction (for example, `minBy`) picked in
     *   that group, or `null` if the reduction picked no row.
     */
    typealias ReducedGroupByReturnSnippet = Nothing

    /**
     * @return A single [DataRow] with one (nested) column per unique combination of the pivot
     *   column values; every cell holds the collected values of the rows with that pivot
     *   value: {@include [OneOrManyCellSnippet]}
     */
    typealias PivotReturnSnippet = Nothing

    /**
     * @return A new [DataFrame] with one row per combination of the [groupBy][DataFrame.groupBy]
     *   key values and one (nested) column per unique combination of the pivot column values;
     *   every cell holds the collected values of the corresponding rows: {@include [OneOrManyCellSnippet]}
     */
    typealias PivotGroupByReturnSnippet = Nothing

    /**
     * @return A single [DataRow] with one (nested) column per unique combination of the pivot
     *   column values; every cell holds, per selected column, the value from the row that the
     *   reduction (for example, `minBy`) picked for that pivot value, or `null` if the
     *   reduction picked no row.
     */
    typealias ReducedPivotReturnSnippet = Nothing

    /**
     * @return A new [DataFrame] with one row per combination of the [groupBy][DataFrame.groupBy]
     *   key values and one (nested) column per unique combination of the pivot column values;
     *   every cell holds, per selected column, the value from the row that the reduction
     *   (for example, `minBy`) picked in that cell, or `null` if the reduction picked no row.
     */
    typealias ReducedPivotGroupByReturnSnippet = Nothing
}

/**
 * {@comment KDoc-helpers for the `values` aggregation on [Grouped] and [Pivot].}
 */
@ExcludeFromSources
internal interface GroupedAndPivotValuesDocs {

    /**
     * {@comment The parts all [Grouped.values] aggregation overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [GroupBy] by collecting the values of {@get [COLUMNS]}
     * from all rows of each group into a [List].
     *
     * Returns a new [DataFrame] with one row per group, containing the grouping key columns
     * and one result column per selected column. Each result column is named after
     * its source column and contains the [List] of that column's values
     * collected from all rows of the group.
     *
     * Values from a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * are collected into a [DataFrame] instead of a [List],
     * making the corresponding result column a [FrameColumn].
     *
     * @include [ValuesInAggregationDocs.LostKeyColumnsSnippet]
     *
     * Check out [`groupBy` Grammar][GroupByDocs.Grammar].
     *
     * For more information: {@include [DocumentationUrls.GroupBy]} {@include [DocumentationUrls.Values]}
     *
     * Don't confuse this with [values][ReducedGroupBy.values] on a [ReducedGroupBy],
     * which takes the values of the single reduced row of each group instead of all its rows.
     *
     * See also:
     * - [into][GroupBy.into] — collects the values computed for each row of a group into a new column,
     *   or stores the groups themselves in a [FrameColumn].
     * - [aggregate][Grouped.aggregate] — the general way to aggregate groups.
     * - [GroupBy aggregation][GroupByDocs.Aggregation] — all other ways to aggregate a [GroupBy].
     */
    interface GroupedValuesSnippet {

        /*
         * The key for a @set that defines how the columns to collect the values from are selected.
         */
        typealias COLUMNS = Nothing
    }

    /**
     * {@comment The parts all [Pivot.values] aggregation overloads have in common. KDoc-snippet.}
     *
     * Aggregates this [Pivot] by collecting the values of {@get [COLUMNS]}
     * from all rows of each pivot group into a [List].
     *
     * Returns a new [DataRow] with the [pivot] keys as top-level columns
     * (or as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * for multiple or nested pivot keys).
     * The value of each [pivot] key is aggregated from the corresponding group:
     *
     * @include [CommonStatisticsDocs.PivotCellOneOrManyResultSnippet]
     *
     * If a single column is selected, its values are stored directly under the [pivot] keys.
     * If several columns are selected, each [pivot] key contains one subcolumn
     * per selected column, named after it
     * (or, if [separate\] is `true`, the results are grouped
     * by the selected columns first, and by the [pivot] keys second).
     *
     * Values from a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * follow the same rule: a single row gives a [DataRow],
     * and several rows are collected into a [DataFrame].
     *
     * Check out [`pivot` Grammar][PivotDocs.Grammar].
     *
     * For more information: {@include [DocumentationUrls.Pivot]} {@include [DocumentationUrls.Values]}
     *
     * Don't confuse this with [values][ReducedPivot.values] on a [ReducedPivot],
     * which takes the values of the single reduced row of each group instead of all its rows.
     *
     * See also:
     * - [aggregate][Pivot.aggregate] — the general way to aggregate pivot groups.
     * - [Pivot aggregation][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot].
     */
    interface PivotValuesSnippet {

        /*
         * The key for a @set that defines how the columns to collect the values from are selected.
         */
        typealias COLUMNS = Nothing
    }

    /**
     * @param [dropNA\] If `true`, {@include [NALink]} values (`null`, [Double.NaN] or [Float.NaN])
     *   are removed from the collected values. `false` by default.
     *   Each column is filtered independently of the others.
     * @param [distinct\] If `true`, duplicate values are removed from the collected values,
     *   keeping the first occurrence of each. `false` by default.
     * @comment The shared `dropNA` and `distinct` parameters documentation. KDoc-snippet.
     */
    typealias DropNAAndDistinctParams = Nothing
}

// endregion

// region DataFrame

/**
 * @include [ValuesDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.values_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesByRow_properties]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [C] The common type of the selected columns.
 * @include [ValuesDocs.ByRowSnippet]
 * @include [ValuesDocs.ColumnsSelectorParamSnippet]
 * @include [ValuesDocs.ReturnSnippet]
 */
public fun <T, C> DataFrame<T>.values(byRow: Boolean = false, columns: ColumnsSelector<T, C>): Sequence<C> =
    valuesImpl(byRow, columns)

/**
 * @include [ValuesDocs]
 *
 * This overload extracts the values from all columns of this [DataFrame].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesDf]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesDfByRows]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [byRows] When `true`, the values come row by row: for each row, the values of all
 *   columns in the order the columns appear in this [DataFrame]. When `false` (default), the
 *   values come column by column: all values of the first column, then all values of the
 *   second one, and so on.
 * @return A lazy [Sequence] of the values of all columns of this [DataFrame].
 */
public fun <T> DataFrame<T>.values(byRows: Boolean = false): Sequence<Any?> = values(byRows) { all() }

/**
 * @include [ValuesNotNullDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesNotNull_properties]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [C] The common type of the values of the selected columns.
 * @include [ValuesDocs.ByRowSnippet]
 * @include [ValuesDocs.ColumnsSelectorParamSnippet]
 * @include [ValuesNotNullDocs.ReturnSnippet]
 */
public fun <T, C> DataFrame<T>.valuesNotNull(byRow: Boolean = false, columns: ColumnsSelector<T, C?>): Sequence<C> =
    values(byRow, columns).filterNotNull()

/**
 * @include [ValuesNotNullDocs]
 *
 * This overload extracts the values from all columns of this [DataFrame].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesNotNullDf]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @include [ValuesDocs.ByRowSnippet]
 * @return A lazy [Sequence] of the non-`null` values of all columns of this [DataFrame].
 */
public fun <T> DataFrame<T>.valuesNotNull(byRow: Boolean = false): Sequence<Any> = valuesNotNull(byRow) { all() }

// endregion

// region GroupBy

/**
 * Message appended to a [DuplicateColumnPathInsertException] rethrown by the aggregation
 * `values` overloads when the groups have lost track of the key columns.
 */
internal const val GROUP_BY_VALUES_EXCEPTION_MESSAGE =
    "This grouped dataframe might have lost track of key-columns in the groups, " +
        "causing this duplicate-column exception. " +
        "This can happen when using `.updateGroups {}`, `.filter {}`, or `.add() {}`. " +
        "Try renaming the columns inside the groups or aggregate them manually."

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Grouped<T>.values(
    vararg columns: ColumnReference<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
): DataFrame<T> = values(dropNA, distinct) { columns.toColumnSet() }

/**
 * @include [GroupedAndPivotValuesDocs.GroupedValuesSnippet]
 * {@set [GroupedAndPivotValuesDocs.GroupedValuesSnippet.COLUMNS] the columns with the given names}
 *
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.valuesOnGroupBySelectedColumns_strings]
 *
 * @param [columns] The names of the columns to collect the values from.
 * @include [GroupedAndPivotValuesDocs.DropNAAndDistinctParams]
 * @return A new [DataFrame] with the grouping key columns
 *   and the collected values of the columns with the given names.
 */
public fun <T> Grouped<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
): DataFrame<T> = values(dropNA, distinct) { columns.toColumnSet() }

/**
 * @include [GroupedAndPivotValuesDocs.GroupedValuesSnippet]
 * {@set [GroupedAndPivotValuesDocs.GroupedValuesSnippet.COLUMNS] the selected columns}
 *
 * @include [CommonStatisticsDocs.AggregateColumnsSelectorWithoutDefaultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.valuesOnGroupBySelectedColumns_properties]
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.valuesOnGroupByRenameColumns_properties]
 *
 * @include [GroupedAndPivotValuesDocs.DropNAAndDistinctParams]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns to collect the values from.
 * @return A new [DataFrame] with the grouping key columns and the collected values of the selected columns.
 */
public fun <T> Grouped<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataFrame<T> =
    try {
        aggregate { internal().columnValues(columns, true, dropNA, distinct) }
    } catch (e: DuplicateColumnPathInsertException) {
        throw DuplicateColumnPathInsertException("${e.message!!} $GROUP_BY_VALUES_EXCEPTION_MESSAGE")
    }

/**
 * @include [GroupedAndPivotValuesDocs.GroupedValuesSnippet]
 * {@set [GroupedAndPivotValuesDocs.GroupedValuesSnippet.COLUMNS] all remaining columns (all group columns except the grouping keys)}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.valuesOnGroupByAllColumns_properties]
 *
 * @include [GroupedAndPivotValuesDocs.DropNAAndDistinctParams]
 * @return A new [DataFrame] with the grouping key columns
 *   and the collected values of all remaining columns.
 */
public fun <T> Grouped<T>.values(dropNA: Boolean = false, distinct: Boolean = false): DataFrame<T> =
    values(dropNA, distinct, remainingColumnsSelector())

// endregion

// region ReducedGroupBy

/**
 * @include [ValuesInAggregationDocs]
 * @include [ValuesInAggregationDocs.LostKeyColumnsSnippet]
 * @include [GroupByDocs.ReducedGroupByFinalStepSnippet]
 * @include [GroupByDocs.ReducedGroupByNoSelectedRowSnippet]
 *
 * This overload collects the values from all columns except the group keys.
 *
 * For more information: {@include [DocumentationUrls.GroupByReducing]}
 *
 * See also:
 * - [into][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [concat][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedGroupBy_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.groupByValuesAllColumns]
 *
 * @param [T] The schema marker type of the [DataFrame] the groups were created from.
 * @param [G] The schema marker type of the group data.
 * @include [ValuesInAggregationDocs.ReducedGroupByReturnSnippet]
 */
public fun <T, G> ReducedGroupBy<T, G>.values(): DataFrame<G> = values(groupBy.remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: ColumnReference<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [ValuesInAggregationDocs.LostKeyColumnsSnippet]
 * @include [GroupByDocs.ReducedGroupByFinalStepSnippet]
 * @include [GroupByDocs.ReducedGroupByNoSelectedRowSnippet]
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * For more information: {@include [DocumentationUrls.GroupByReducing]}
 *
 * See also:
 * - [into][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [concat][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedGroupBy_strings]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.groupByValues_strings]
 *
 * @param [T] The schema marker type of the [DataFrame] the groups were created from.
 * @param [G] The schema marker type of the group data.
 * @include [ValuesInAggregationDocs.ColumnNamesParamSnippet]
 * @include [ValuesInAggregationDocs.ReducedGroupByReturnSnippet]
 */
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: String): DataFrame<G> = values { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: KProperty<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [ValuesInAggregationDocs.LostKeyColumnsSnippet]
 * @include [GroupByDocs.ReducedGroupByFinalStepSnippet]
 * @include [GroupByDocs.ReducedGroupByNoSelectedRowSnippet]
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * For more information: {@include [DocumentationUrls.GroupByReducing]}
 *
 * See also:
 * - [into][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [concat][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedGroupBy_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.groupByValues_properties]
 *
 * @param [T] The schema marker type of the [DataFrame] the groups were created from.
 * @param [G] The schema marker type of the group data.
 * @include [ValuesInAggregationDocs.ColumnsForAggregateSelectorParamSnippet]
 * @include [ValuesInAggregationDocs.ReducedGroupByReturnSnippet]
 */
public fun <T, G> ReducedGroupBy<T, G>.values(columns: ColumnsForAggregateSelector<G, *>): DataFrame<G> =
    try {
        groupBy.aggregate { internal().columnValues(columns, reducer) }
    } catch (e: DuplicateColumnPathInsertException) {
        throw DuplicateColumnPathInsertException("${e.message!!} $GROUP_BY_VALUES_EXCEPTION_MESSAGE")
    }

// endregion

// region Pivot

/**
 * @include [GroupedAndPivotValuesDocs.PivotValuesSnippet]
 * {@set [GroupedAndPivotValuesDocs.PivotValuesSnippet.COLUMNS] the selected columns}
 *
 * @include [CommonStatisticsDocs.AggregateColumnsSelectorSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotValuesAggregate_properties]
 *
 * @param [T] The schema marker type of the pivoted [DataFrame].
 * @include [GroupedAndPivotValuesDocs.DropNAAndDistinctParams]
 * @include [CommonStatisticsDocs.SeparateParam]
 * @param [columns] The [ColumnsForAggregateSelector] used to select the columns to collect the values from.
 * @return A new [DataRow] with the [pivot] keys and the collected values of the selected columns.
 */
public fun <T> Pivot<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataRow<*> = delegate { values(dropNA, distinct, separate, columns) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.values(
    vararg columns: ColumnReference<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<*> = values(dropNA, distinct, separate) { columns.toColumnSet() }

/**
 * @include [GroupedAndPivotValuesDocs.PivotValuesSnippet]
 * {@set [GroupedAndPivotValuesDocs.PivotValuesSnippet.COLUMNS] the columns with the given names}
 *
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotValuesAggregate_strings]
 *
 * @param [T] The schema marker type of the pivoted [DataFrame].
 * @param [columns] The names of the columns to collect the values from.
 * @include [GroupedAndPivotValuesDocs.DropNAAndDistinctParams]
 * @include [CommonStatisticsDocs.SeparateParam]
 * @return A new [DataRow] with the [pivot] keys
 *   and the collected values of the columns with the given names.
 */
public fun <T> Pivot<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<*> = values(dropNA, distinct, separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.values(
    vararg columns: KProperty<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<*> = values(dropNA, distinct, separate) { columns.toColumnSet() }

/**
 * @include [GroupedAndPivotValuesDocs.PivotValuesSnippet]
 * {@set [GroupedAndPivotValuesDocs.PivotValuesSnippet.COLUMNS] all remaining columns (all columns except the pivoted columns)}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotValuesAggregateAll_properties]
 *
 * @param [T] The schema marker type of the pivoted [DataFrame].
 * @include [GroupedAndPivotValuesDocs.DropNAAndDistinctParams]
 * @include [CommonStatisticsDocs.SeparateParam]
 * @return A new [DataRow] with the [pivot] keys and the collected values of all remaining columns.
 */
public fun <T> Pivot<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<*> =
    delegate {
        values(dropNA, distinct, separate)
    }

// endregion

// region ReducedPivot

/**
 * @include [ValuesInAggregationDocs]
 * @include [PivotDocs.ReducedPivotFinalStepSnippet]
 *
 * This overload collects the values from all columns except the pivot columns.
 *
 * For more information: {@include [DocumentationUrls.PivotReducing]}
 *
 * See also:
 * - [with][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedPivot_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotValues_properties]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.ReducedPivotReturnSnippet]
 */
public fun <T> ReducedPivot<T>.values(separate: Boolean = false): DataRow<*> =
    pivot.delegate { reduce(reducer).values(separate = separate) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: ColumnReference<*>, separate: Boolean = false): DataRow<*> =
    values(separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [PivotDocs.ReducedPivotFinalStepSnippet]
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * For more information: {@include [DocumentationUrls.PivotReducing]}
 *
 * See also:
 * - [with][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedPivot_strings]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotValuesSelectedColumns_strings]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnNamesParamSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.ReducedPivotReturnSnippet]
 */
public fun <T> ReducedPivot<T>.values(vararg columns: String, separate: Boolean = false): DataRow<*> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataRow<*> =
    values(separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [PivotDocs.ReducedPivotFinalStepSnippet]
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * For more information: {@include [DocumentationUrls.PivotReducing]}
 *
 * See also:
 * - [with][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedPivot_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotValuesSelectedColumns_properties]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnsForAggregateSelectorParamSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.ReducedPivotReturnSnippet]
 */
public fun <T> ReducedPivot<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataRow<*> = pivot.delegate { reduce(reducer).values(separate = separate, columns = columns) }

// endregion

// region PivotGroupBy

/**
 * @include [ValuesInAggregationDocs]
 *
 * This overload collects the values from all columns except the pivot columns and
 * the [groupBy][DataFrame.groupBy] keys.
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.DropNASnippet]
 * @include [ValuesInAggregationDocs.DistinctSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.PivotGroupByReturnSnippet]
 */
@Refine
@Interpretable("PivotGroupByValues0")
public fun <T> PivotGroupBy<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<*> = values(dropNA, distinct, separate, remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> PivotGroupBy<T>.values(
    vararg columns: ColumnReference<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<*> = values(dropNA, distinct, separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInPivotGroupBy_strings]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnNamesParamSnippet]
 * @include [ValuesInAggregationDocs.DropNASnippet]
 * @include [ValuesInAggregationDocs.DistinctSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.PivotGroupByReturnSnippet]
 */
@Refine
@StringApiInterpretable(interpreter = "PivotGroupByValues1", stringArgument = "columns", targetArgument = "columns")
public fun <T> PivotGroupBy<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<*> = values(dropNA, distinct, separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> PivotGroupBy<T>.values(
    vararg columns: KProperty<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<*> = values(dropNA, distinct, separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInPivotGroupBy_properties]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnsForAggregateSelectorParamSnippet]
 * @include [ValuesInAggregationDocs.DropNASnippet]
 * @include [ValuesInAggregationDocs.DistinctSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.PivotGroupByReturnSnippet]
 */
@Refine
@Interpretable("PivotGroupByValues1")
public fun <T> PivotGroupBy<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataFrame<*> =
    try {
        aggregate(separate = separate) { internal().columnValues(columns, false, dropNA, distinct) }
    } catch (e: DuplicateColumnPathInsertException) {
        throw DuplicateColumnPathInsertException("${e.message!!} $GROUP_BY_VALUES_EXCEPTION_MESSAGE")
    }

// endregion

// region ReducedPivotGroupBy

/**
 * @include [ValuesInAggregationDocs]
 *
 * This overload collects the values from all columns except the pivot columns and
 * the [groupBy][DataFrame.groupBy] keys.
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.ReducedPivotGroupByReturnSnippet]
 */
@Refine
@Interpretable("ReducedPivotGroupByValues0")
public fun <T> ReducedPivotGroupBy<T>.values(separate: Boolean = false): DataFrame<*> =
    values(separate, pivot.remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivotGroupBy<T>.values(
    vararg columns: ColumnReference<*>,
    separate: Boolean = false,
): DataFrame<*> = values(separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedPivotGroupBy_strings]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnNamesParamSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.ReducedPivotGroupByReturnSnippet]
 */
@Refine
@StringApiInterpretable(
    interpreter = "ReducedPivotGroupByValues1",
    stringArgument = "columns",
    targetArgument = "columns",
)
public fun <T> ReducedPivotGroupBy<T>.values(vararg columns: String, separate: Boolean = false): DataFrame<*> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivotGroupBy<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataFrame<*> =
    values(separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedPivotGroupBy_properties]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnsForAggregateSelectorParamSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.ReducedPivotGroupByReturnSnippet]
 */
@Refine
@Interpretable("ReducedPivotGroupByValues1")
public fun <T> ReducedPivotGroupBy<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataFrame<*> =
    try {
        pivot.aggregate(separate = separate) { internal().columnValues(columns, reducer) }
    } catch (e: DuplicateColumnPathInsertException) {
        throw DuplicateColumnPathInsertException("${e.message!!} $GROUP_BY_VALUES_EXCEPTION_MESSAGE")
    }

// endregion
