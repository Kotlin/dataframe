package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
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
 * Returns a [<code>DataFrame</code>][DataFrame] with one row per group of this [<code>ReducedGroupBy</code>][ReducedGroupBy]:
 * the grouping key columns and the values of all other columns of the reduced row.
 *
 *
 *
 * This is the final step of [<code>GroupBy reducing</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Reducing].
 * A [<code>ReducedGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy] is created by a reducing method, like [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.first], [<code>last</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.last],
 * [<code>minBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.minBy], [<code>maxBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.maxBy], [<code>medianBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.medianBy], or
 * [<code>percentileBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.percentileBy], which selects at most one row in each group.
 *
 * Check out [<code>`GroupBy grammar`</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar].
 *
 *
 *
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.first]), the result still contains a row for this group.
 * This row is treated as missing every value, so its value columns contain `null` and become nullable.
 *
 * Missing values in a [<code>List</code>][List] column are represented by empty lists, and missing values in a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] by empty
 * [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; these columns do not become nullable. A missing [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] remains a column group, and its
 * nested columns are filled according to these same rules recursively.
 *
 * For more information: [See "`GroupBy` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#reducing)
 *
 * Don't confuse this with [<code>values</code>][Grouped.values] on a [<code>GroupBy</code>][GroupBy], which collects the values of all rows
 * of each group into lists.
 *
 * See also:
 * - [<code>into</code>][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [<code>concat</code>][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * ```kotlin
 * // For each `isHappy` value, the values of the youngest person
 * df.groupBy { isHappy }.minBy { age }.values()
 * ```
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [<code>GroupBy</code>][GroupBy].
 * @param [G] The schema marker type of the groups.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns and the values of the reduced rows.
 */
public fun <T, G> ReducedGroupBy<T, G>.values(): DataFrame<G> = values(groupBy.remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: ColumnReference<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with one row per group of this [<code>ReducedGroupBy</code>][ReducedGroupBy]:
 * the grouping key columns and the values of the reduced row
 * in the [<code>columns</code>][columns] with the given names.
 *
 *
 *
 * This is the final step of [<code>GroupBy reducing</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Reducing].
 * A [<code>ReducedGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy] is created by a reducing method, like [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.first], [<code>last</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.last],
 * [<code>minBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.minBy], [<code>maxBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.maxBy], [<code>medianBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.medianBy], or
 * [<code>percentileBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.percentileBy], which selects at most one row in each group.
 *
 * Check out [<code>`GroupBy grammar`</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar].
 *
 *
 *
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.first]), the result still contains a row for this group.
 * This row is treated as missing every value, so its value columns contain `null` and become nullable.
 *
 * Missing values in a [<code>List</code>][List] column are represented by empty lists, and missing values in a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] by empty
 * [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; these columns do not become nullable. A missing [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] remains a column group, and its
 * nested columns are filled according to these same rules recursively.
 *
 * For more information: [See "`GroupBy` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#reducing)
 *
 * Don't confuse this with [<code>values</code>][Grouped.values] on a [<code>GroupBy</code>][GroupBy], which collects the values of all rows
 * of each group into lists.
 *
 * See also:
 * - [<code>into</code>][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [<code>concat</code>][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * ```kotlin
 * df.groupBy("isHappy").minBy("age").values("name", "age", "city")
 * ```
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [<code>GroupBy</code>][GroupBy].
 * @param [G] The schema marker type of the groups.
 * @param [columns] The names of the columns to take the values from.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns and the values of the reduced rows
 *   in the given columns.
 */
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: String): DataFrame<G> = values { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: KProperty<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with one row per group of this [<code>ReducedGroupBy</code>][ReducedGroupBy]:
 * the grouping key columns and the values of the reduced row in the selected [<code>columns</code>][columns].
 *
 *
 *
 * This is the final step of [<code>GroupBy reducing</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Reducing].
 * A [<code>ReducedGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy] is created by a reducing method, like [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.first], [<code>last</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.last],
 * [<code>minBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.minBy], [<code>maxBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.maxBy], [<code>medianBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.medianBy], or
 * [<code>percentileBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.percentileBy], which selects at most one row in each group.
 *
 * Check out [<code>`GroupBy grammar`</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar].
 *
 *
 *
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.first]), the result still contains a row for this group.
 * This row is treated as missing every value, so its value columns contain `null` and become nullable.
 *
 * Missing values in a [<code>List</code>][List] column are represented by empty lists, and missing values in a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] by empty
 * [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; these columns do not become nullable. A missing [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] remains a column group, and its
 * nested columns are filled according to these same rules recursively.
 *
 *
 *
 * Select the columns using the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl].
 *
 *
 *
 * The [<code>Columns For Aggregate Selection DSL</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] allows selecting columns
 * to aggregate, just like the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * It also allows configuring how the aggregation result of each selected column is stored:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] sets the name
 *   (or the [<code>path</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], see [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]) of the resulting column.
 *   If several columns are selected, their results are put into a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   with this name, keeping the names of the columns.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See "`GroupBy` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#reducing)
 *
 * Don't confuse this with [<code>values</code>][Grouped.values] on a [<code>GroupBy</code>][GroupBy], which collects the values of all rows
 * of each group into lists.
 *
 * See also:
 * - [<code>into</code>][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [<code>concat</code>][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * ```kotlin
 * df.groupBy { isHappy }.minBy { age }.values { name and age and city }
 * ```
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [<code>GroupBy</code>][GroupBy].
 * @param [G] The schema marker type of the groups.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][ColumnsForAggregateSelector] selecting the columns to take the values from.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns and the values of the reduced rows
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
): DataRow<*> = delegate { values(dropNA, distinct, separate, columns) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.values(
    vararg columns: ColumnReference<*>,
    dropNA: Boolean = false,
    distinct: Boolean = false,
    separate: Boolean = false,
): DataRow<*> = values(dropNA, distinct, separate) { columns.toColumnSet() }

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
 * Returns a [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys of this [<code>ReducedPivot</code>][ReducedPivot] as columns,
 * containing the values of all columns of the reduced row of the corresponding
 * group, except the pivoted columns.
 *
 *
 *
 * This is the final step of [<code>Pivot reducing</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Reducing].
 * A [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] is created by a reducing method, like [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.first], [<code>last</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.last],
 * [<code>minBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.minBy], [<code>maxBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.maxBy], [<code>medianBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.medianBy], or
 * [<code>percentileBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentileBy], which selects at most one row in each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] group.
 *
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.first]), the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key of this group is still present
 * in the resulting [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow], but all the values under it are `null`.
 *
 * Check out [<code>`Pivot grammar`</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 *
 *
 * If a single column is taken, its value is stored directly under the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key.
 * If several columns are taken and `separate` is `false` (the default), the value of each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key
 * is a row with the values of these columns; if `separate` is `true`, the results are grouped
 * by the names of these columns first, and by the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys second.
 *
 * For more information: [See "`Pivot` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
 *
 * Don't confuse this with [<code>values</code>][Pivot.values] on a [<code>Pivot</code>][Pivot], which takes the values of all rows
 * of each group.
 *
 * See also:
 * - [<code>with</code>][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * ```kotlin
 * df.pivot { isHappy }.first().values()
 * ```
 *
 * @param [T] The schema marker type of the pivoted [<code>DataFrame</code>][DataFrame].
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @return A new [<code>DataRow</code>][DataRow] with the values of the reduced rows under their [<code>pivot</code>][pivot] keys.
 */
public fun <T> ReducedPivot<T>.values(separate: Boolean = false): DataRow<*> =
    pivot.delegate { reduce(reducer).values(separate = separate) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: ColumnReference<*>, separate: Boolean = false): DataRow<*> =
    values(separate) { columns.toColumnSet() }

/**
 * Returns a [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys of this [<code>ReducedPivot</code>][ReducedPivot] as columns,
 * containing the values of the reduced row of the corresponding group
 * in the [<code>columns</code>][columns] with the given names.
 *
 *
 *
 * This is the final step of [<code>Pivot reducing</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Reducing].
 * A [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] is created by a reducing method, like [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.first], [<code>last</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.last],
 * [<code>minBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.minBy], [<code>maxBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.maxBy], [<code>medianBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.medianBy], or
 * [<code>percentileBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentileBy], which selects at most one row in each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] group.
 *
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.first]), the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key of this group is still present
 * in the resulting [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow], but all the values under it are `null`.
 *
 * Check out [<code>`Pivot grammar`</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 *
 *
 * If a single column is taken, its value is stored directly under the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key.
 * If several columns are taken and `separate` is `false` (the default), the value of each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key
 * is a row with the values of these columns; if `separate` is `true`, the results are grouped
 * by the names of these columns first, and by the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys second.
 *
 * For more information: [See "`Pivot` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
 *
 * Don't confuse this with [<code>values</code>][Pivot.values] on a [<code>Pivot</code>][Pivot], which takes the values of all rows
 * of each group.
 *
 * See also:
 * - [<code>with</code>][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * ```kotlin
 * // For each `isHappy` value, the `name` and `age` of the first person
 * df.pivot("isHappy").first().values("name", "age")
 * ```
 *
 * @param [T] The schema marker type of the pivoted [<code>DataFrame</code>][DataFrame].
 * @param [columns] The names of the columns to take the values from.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @return A new [<code>DataRow</code>][DataRow] with the values of the reduced rows in the given columns under their [<code>pivot</code>][pivot] keys.
 */
public fun <T> ReducedPivot<T>.values(vararg columns: String, separate: Boolean = false): DataRow<*> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataRow<*> =
    values(separate) { columns.toColumnSet() }

/**
 * Returns a [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys of this [<code>ReducedPivot</code>][ReducedPivot] as columns,
 * containing the values of the reduced row of the corresponding group
 * in the selected [<code>columns</code>][columns].
 *
 *
 *
 * This is the final step of [<code>Pivot reducing</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Reducing].
 * A [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] is created by a reducing method, like [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.first], [<code>last</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.last],
 * [<code>minBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.minBy], [<code>maxBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.maxBy], [<code>medianBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.medianBy], or
 * [<code>percentileBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentileBy], which selects at most one row in each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] group.
 *
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.first]), the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key of this group is still present
 * in the resulting [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow], but all the values under it are `null`.
 *
 * Check out [<code>`Pivot grammar`</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 *
 *
 * If a single column is taken, its value is stored directly under the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key.
 * If several columns are taken and `separate` is `false` (the default), the value of each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key
 * is a row with the values of these columns; if `separate` is `true`, the results are grouped
 * by the names of these columns first, and by the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys second.
 *
 *
 *
 * Select the columns using the [<code>ColumnsForAggregateSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl].
 *
 *
 *
 *
 *
 * The [<code>Columns For Aggregate Selection DSL</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] allows selecting columns
 * to aggregate, just like the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * It also allows configuring how the aggregation result of each selected column is stored:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] sets the name
 *   (or the [<code>path</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], see [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]) of the resulting column.
 *   If several columns are selected, their results are put into a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   with this name, keeping the names of the columns.
 * - [<code>default</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] sets the value used in [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] aggregations
 *   instead of `null` results, including the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] cells without any rows.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See "`Pivot` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
 *
 * Don't confuse this with [<code>values</code>][Pivot.values] on a [<code>Pivot</code>][Pivot], which takes the values of all rows
 * of each group.
 *
 * See also:
 * - [<code>with</code>][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * ```kotlin
 * // For each `isHappy` value, the `name` and `age` of the first person
 * df.pivot { isHappy }.first().values { name and age }
 * ```
 *
 * @param [T] The schema marker type of the pivoted [<code>DataFrame</code>][DataFrame].
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][ColumnsForAggregateSelector] selecting the columns to take the values from.
 * @return A new [<code>DataRow</code>][DataRow] with the values of the reduced rows in the selected columns under their [<code>pivot</code>][pivot] keys.
 */
public fun <T> ReducedPivot<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataRow<*> = pivot.delegate { reduce(reducer).values(separate = separate, columns = columns) }

// endregion

// region PivotGroupBy

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
