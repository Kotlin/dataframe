package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
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
 * then all values of the second one, and so on. With `byRow = true`, the values come row by
 * row instead: for each row, the values of the selected columns in the order the columns
 * were selected.
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
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [updateGroups][GroupBy.updateGroups], [filter][GroupBy.filter], or [add][GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
     * the single value if there is exactly one, a [List] if there are several, and an empty
     *   cell if there is nothing to collect. An empty cell is `null`, or an empty [List] when
     *   the other cells of that column hold lists; it appears when the cell has no rows, or
     *   when `dropNA` removed all the values.
     */
    typealias OneOrManyCellSnippet = Nothing

    /**
     * @return A new [DataFrame] with one row per group: the group keys, and, per selected
     *   column, a [List] with all values from the group.
     */
    typealias GroupedReturnSnippet = Nothing

    /**
     * @return A new [DataFrame] with one row per group: the group keys, and, per selected
     *   column, the value from the row that the reduction (for example, `minBy`) picked
     *   in that group.
     */
    typealias ReducedGroupByReturnSnippet = Nothing

    /**
     * @return A single [DataRow] with one (nested) column per unique combination of the pivot
     *   column values; every cell holds the collected values of the rows with that pivot
     *   value: {@include [OneOrManyCellSnippet]}.
     */
    typealias PivotReturnSnippet = Nothing

    /**
     * @return A new [DataFrame] with one row per combination of the [groupBy][DataFrame.groupBy]
     *   key values and one (nested) column per unique combination of the pivot column values;
     *   every cell holds the collected values of the corresponding rows: {@include [OneOrManyCellSnippet]}.
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
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInGroupBy_strings]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnNamesParamSnippet]
 * @include [ValuesInAggregationDocs.DropNASnippet]
 * @include [ValuesInAggregationDocs.DistinctSnippet]
 * @include [ValuesInAggregationDocs.GroupedReturnSnippet]
 */
public fun <T> Grouped<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
): DataFrame<T> = values(dropNA, distinct) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInGroupBy_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInGroupByDropNA_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInGroupByDistinct_properties]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnsForAggregateSelectorParamSnippet]
 * @include [ValuesInAggregationDocs.DropNASnippet]
 * @include [ValuesInAggregationDocs.DistinctSnippet]
 * @include [ValuesInAggregationDocs.GroupedReturnSnippet]
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
 * @include [ValuesInAggregationDocs]
 *
 * This overload collects the values from all columns except the group keys.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInGroupByAll_properties]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @include [ValuesInAggregationDocs.DropNASnippet]
 * @include [ValuesInAggregationDocs.DistinctSnippet]
 * @include [ValuesInAggregationDocs.GroupedReturnSnippet]
 */
public fun <T> Grouped<T>.values(dropNA: Boolean = false, distinct: Boolean = false): DataFrame<T> =
    values(dropNA, distinct, remainingColumnsSelector())

// endregion

// region ReducedGroupBy

/**
 * @include [ValuesInAggregationDocs]
 *
 * This overload collects the values from all columns except the group keys.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedGroupBy_properties]
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
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedGroupBy_strings]
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
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedGroupBy_properties]
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
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInPivot_properties]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnsForAggregateSelectorParamSnippet]
 * @include [ValuesInAggregationDocs.DropNASnippet]
 * @include [ValuesInAggregationDocs.DistinctSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.PivotReturnSnippet]
 */
public fun <T> Pivot<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataRow<T> = delegate { values(dropNA, distinct, separate, columns) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.values(
    vararg columns: ColumnReference<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInPivot_strings]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnNamesParamSnippet]
 * @include [ValuesInAggregationDocs.DropNASnippet]
 * @include [ValuesInAggregationDocs.DistinctSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.PivotReturnSnippet]
 */
public fun <T> Pivot<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.values(
    vararg columns: KProperty<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 *
 * This overload collects the values from all columns except the pivot columns.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInPivot_properties]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.DropNASnippet]
 * @include [ValuesInAggregationDocs.DistinctSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.PivotReturnSnippet]
 */
public fun <T> Pivot<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<T> =
    delegate {
        values(dropNA, distinct, separate)
    }

// endregion

// region ReducedPivot

/**
 * @include [ValuesInAggregationDocs]
 *
 * This overload collects the values from all columns except the pivot columns.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedPivot_properties]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.ReducedPivotReturnSnippet]
 */
public fun <T> ReducedPivot<T>.values(separate: Boolean = false): DataRow<T> =
    pivot.delegate { reduce(reducer).values(separate = separate) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: ColumnReference<*>, separate: Boolean = false): DataRow<T> =
    values(separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnNamesApi]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedPivot_strings]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnNamesParamSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.ReducedPivotReturnSnippet]
 */
public fun <T> ReducedPivot<T>.values(vararg columns: String, separate: Boolean = false): DataRow<T> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataRow<T> =
    values(separate) { columns.toColumnSet() }

/**
 * @include [ValuesInAggregationDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ValuesSamples.valuesInReducedPivot_properties]
 *
 * @param [T] The schema marker type of the original [DataFrame].
 * @include [ValuesInAggregationDocs.ColumnsForAggregateSelectorParamSnippet]
 * @include [ValuesInAggregationDocs.SeparateSnippet]
 * @include [ValuesInAggregationDocs.ReducedPivotReturnSnippet]
 */
public fun <T> ReducedPivot<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataRow<T> = pivot.delegate { reduce(reducer).values(separate = separate, columns = columns) }

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
public fun <T> PivotGroupBy<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<T> = values(dropNA, distinct, separate, remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> PivotGroupBy<T>.values(
    vararg columns: ColumnReference<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

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
public fun <T> PivotGroupBy<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> PivotGroupBy<T>.values(
    vararg columns: KProperty<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataFrame<T> = values(dropNA, distinct, separate) { columns.toColumnSet() }

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
public fun <T> PivotGroupBy<T>.values(
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataFrame<T> =
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
public fun <T> ReducedPivotGroupBy<T>.values(separate: Boolean = false): DataFrame<T> =
    values(separate, pivot.remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivotGroupBy<T>.values(
    vararg columns: ColumnReference<*>,
    separate: Boolean = false,
): DataFrame<T> = values(separate) { columns.toColumnSet() }

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
public fun <T> ReducedPivotGroupBy<T>.values(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivotGroupBy<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataFrame<T> =
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
public fun <T> ReducedPivotGroupBy<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataFrame<T> =
    try {
        pivot.aggregate(separate = separate) { internal().columnValues(columns, reducer) }
    } catch (e: DuplicateColumnPathInsertException) {
        throw DuplicateColumnPathInsertException("${e.message!!} $GROUP_BY_VALUES_EXCEPTION_MESSAGE")
    }

// endregion
