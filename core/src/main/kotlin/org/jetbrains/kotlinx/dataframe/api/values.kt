package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.columnValues
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.remainingColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.valuesImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

public fun <T, C> DataFrame<T>.values(byRow: Boolean = false, columns: ColumnsSelector<T, C>): Sequence<C> =
    valuesImpl(byRow, columns)

public fun <T> DataFrame<T>.values(byRows: Boolean = false): Sequence<Any?> = values(byRows) { all() }

public fun <T, C> DataFrame<T>.valuesNotNull(byRow: Boolean = false, columns: ColumnsSelector<T, C?>): Sequence<C> =
    values(byRow, columns).filterNotNull()

public fun <T> DataFrame<T>.valuesNotNull(byRow: Boolean = false): Sequence<Any> = valuesNotNull(byRow) { all() }

// endregion

// region GroupBy

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

public fun <T> Grouped<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
): DataFrame<T> = values(dropNA, distinct) { columns.toColumnSet() }

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

public fun <T> Grouped<T>.values(dropNA: Boolean = false, distinct: Boolean = false): DataFrame<T> =
    values(dropNA, distinct, remainingColumnsSelector())

// endregion

// region ReducedGroupBy

/**
 * Returns a [DataFrame] with one row per group of this [ReducedGroupBy]:
 * the grouping key columns and the values of all other columns of the reduced row.
 *
 * @include [GroupByDocs.ReducedGroupByFinalStepSnippet]
 *
 * @include [GroupByDocs.ReducedGroupByNoSelectedRowSnippet]
 *
 * For more information: {@include [DocumentationUrls.GroupByReducing]}
 *
 * Don't confuse this with [values][Grouped.values] on a [GroupBy], which collects the values of all rows
 * of each group into lists.
 *
 * See also:
 * - [into][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [concat][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.groupByValuesAllColumns]
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [GroupBy].
 * @param [G] The schema marker type of the groups.
 * @return A new [DataFrame] with the grouping key columns and the values of the reduced rows.
 */
public fun <T, G> ReducedGroupBy<T, G>.values(): DataFrame<G> = values(groupBy.remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: ColumnReference<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * Returns a [DataFrame] with one row per group of this [ReducedGroupBy]:
 * the grouping key columns and the values of the reduced row
 * in the [columns] with the given names.
 *
 * @include [GroupByDocs.ReducedGroupByFinalStepSnippet]
 *
 * @include [GroupByDocs.ReducedGroupByNoSelectedRowSnippet]
 *
 * For more information: {@include [DocumentationUrls.GroupByReducing]}
 *
 * Don't confuse this with [values][Grouped.values] on a [GroupBy], which collects the values of all rows
 * of each group into lists.
 *
 * See also:
 * - [into][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [concat][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.groupByValues_strings]
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [GroupBy].
 * @param [G] The schema marker type of the groups.
 * @param [columns] The names of the columns to take the values from.
 * @return A new [DataFrame] with the grouping key columns and the values of the reduced rows
 *   in the given columns.
 */
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: String): DataFrame<G> = values { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: KProperty<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * Returns a [DataFrame] with one row per group of this [ReducedGroupBy]:
 * the grouping key columns and the values of the reduced row in the selected [columns].
 *
 * @include [GroupByDocs.ReducedGroupByFinalStepSnippet]
 *
 * @include [GroupByDocs.ReducedGroupByNoSelectedRowSnippet]
 *
 * @include [CommonStatisticsDocs.AggregateColumnsSelectorWithoutDefaultSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * For more information: {@include [DocumentationUrls.GroupByReducing]}
 *
 * Don't confuse this with [values][Grouped.values] on a [GroupBy], which collects the values of all rows
 * of each group into lists.
 *
 * See also:
 * - [into][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [concat][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.groupByValues_properties]
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [GroupBy].
 * @param [G] The schema marker type of the groups.
 * @param [columns] The [ColumnsForAggregateSelector] selecting the columns to take the values from.
 * @return A new [DataFrame] with the grouping key columns and the values of the reduced rows
 *   in the selected columns.
 */
public fun <T, G> ReducedGroupBy<T, G>.values(columns: ColumnsForAggregateSelector<G, *>): DataFrame<G> =
    try {
        groupBy.aggregate { internal().columnValues(columns, reducer) }
    } catch (e: DuplicateColumnPathInsertException) {
        throw DuplicateColumnPathInsertException("${e.message!!} $GROUP_BY_VALUES_EXCEPTION_MESSAGE")
    }

// endregion

// region Pivot

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
 * Returns a [DataRow] with the [pivot] keys of this [ReducedPivot] as columns,
 * containing the values of all columns of the reduced row of the corresponding
 * group, except the pivoted columns.
 *
 * @include [PivotDocs.ReducedPivotFinalStepSnippet]
 *
 * @include [PivotDocs.ReducedPivotValuesStructureSnippet]
 *
 * For more information: {@include [DocumentationUrls.PivotReducing]}
 *
 * Don't confuse this with [values][Pivot.values] on a [Pivot], which takes the values of all rows
 * of each group.
 *
 * See also:
 * - [with][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotValues_properties]
 *
 * @param [T] The schema marker type of the pivoted [DataFrame].
 * @include [CommonStatisticsDocs.SeparateParam]
 * @return A new [DataRow] with the values of the reduced rows under their [pivot] keys.
 */
public fun <T> ReducedPivot<T>.values(separate: Boolean = false): DataRow<T> =
    pivot.delegate { reduce(reducer).values(separate = separate) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: ColumnReference<*>, separate: Boolean = false): DataRow<T> =
    values(separate) { columns.toColumnSet() }

/**
 * Returns a [DataRow] with the [pivot] keys of this [ReducedPivot] as columns,
 * containing the values of the reduced row of the corresponding group
 * in the [columns] with the given names.
 *
 * @include [PivotDocs.ReducedPivotFinalStepSnippet]
 *
 * @include [PivotDocs.ReducedPivotValuesStructureSnippet]
 *
 * For more information: {@include [DocumentationUrls.PivotReducing]}
 *
 * Don't confuse this with [values][Pivot.values] on a [Pivot], which takes the values of all rows
 * of each group.
 *
 * See also:
 * - [with][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotValuesSelectedColumns_strings]
 *
 * @param [T] The schema marker type of the pivoted [DataFrame].
 * @param [columns] The names of the columns to take the values from.
 * @include [CommonStatisticsDocs.SeparateParam]
 * @return A new [DataRow] with the values of the reduced rows in the given columns under their [pivot] keys.
 */
public fun <T> ReducedPivot<T>.values(vararg columns: String, separate: Boolean = false): DataRow<T> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataRow<T> =
    values(separate) { columns.toColumnSet() }

/**
 * Returns a [DataRow] with the [pivot] keys of this [ReducedPivot] as columns,
 * containing the values of the reduced row of the corresponding group
 * in the selected [columns].
 *
 * @include [PivotDocs.ReducedPivotFinalStepSnippet]
 *
 * @include [PivotDocs.ReducedPivotValuesStructureSnippet]
 *
 * @include [CommonStatisticsDocs.AggregateColumnsSelectorSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * For more information: {@include [DocumentationUrls.PivotReducing]}
 *
 * Don't confuse this with [values][Pivot.values] on a [Pivot], which takes the values of all rows
 * of each group.
 *
 * See also:
 * - [with][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotValuesSelectedColumns_properties]
 *
 * @param [T] The schema marker type of the pivoted [DataFrame].
 * @include [CommonStatisticsDocs.SeparateParam]
 * @param [columns] The [ColumnsForAggregateSelector] selecting the columns to take the values from.
 * @return A new [DataRow] with the values of the reduced rows in the selected columns under their [pivot] keys.
 */
public fun <T> ReducedPivot<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataRow<T> = pivot.delegate { reduce(reducer).values(separate = separate, columns = columns) }

// endregion

// region PivotGroupBy

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

public fun <T> ReducedPivotGroupBy<T>.values(separate: Boolean = false): DataFrame<T> =
    values(separate, pivot.remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivotGroupBy<T>.values(
    vararg columns: ColumnReference<*>,
    separate: Boolean = false,
): DataFrame<T> = values(separate) { columns.toColumnSet() }

public fun <T> ReducedPivotGroupBy<T>.values(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivotGroupBy<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataFrame<T> =
    values(separate) { columns.toColumnSet() }

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
