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
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.columnValues
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.remainingColumnsSelector
import org.jetbrains.kotlinx.dataframe.impl.valuesImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region docs

// endregion

// region DataFrame

/**
 * Extracts the values of the selected columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] into a lazy [<code>Sequence</code>][Sequence].
 *
 * By default, the values come column by column: all values of the first selected column,
 * then all values of the second one, and so on. With the row-by-row traversal enabled, the
 * values come row by row instead: for each row, the values of the selected columns in the
 * order the columns were selected.
 *
 * The [<code>Sequence</code>][Sequence] is lazy — the values are pulled from this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] one by one, as the
 * sequence is consumed.
 *
 * Don't confuse this `values` with the aggregation [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.values], which collects
 * the values of the selected columns for every group of a [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] or
 * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] result.
 *
 * See also:
 * - [<code>valuesNotNull</code>][org.jetbrains.kotlinx.dataframe.DataFrame.valuesNotNull] — the same, but `null` values are skipped.
 * - [<code>asSequence</code>][org.jetbrains.kotlinx.dataframe.DataFrame.asSequence] — a [<code>Sequence</code>][Sequence] over the [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples
 *
 * ```kotlin
 * // all "age" values, then all "weight" values
 * val values: Sequence<Int?> = df.values { age and weight }
 * ```
 *
 * ```kotlin
 * // for each row, "age" and "weight" — row by row
 * val values: Sequence<Int?> = df.values(byRow = true) { age and weight }
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [C] The common type of the selected columns.
 * @param [byRow] When `true`, the values come row by row: for each row, the values of
 *   all selected columns in the order the columns were selected. When `false` (default),
 *   the values come column by column: all values of the first selected column, then all
 *   values of the second one, and so on.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns to extract
 *   the values from.
 * @return A lazy [<code>Sequence</code>][Sequence] of the values of the selected columns.
 */
public fun <T, C> DataFrame<T>.values(byRow: Boolean = false, columns: ColumnsSelector<T, C>): Sequence<C> =
    valuesImpl(byRow, columns)

/**
 * Extracts the values of the selected columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] into a lazy [<code>Sequence</code>][Sequence].
 *
 * By default, the values come column by column: all values of the first selected column,
 * then all values of the second one, and so on. With the row-by-row traversal enabled, the
 * values come row by row instead: for each row, the values of the selected columns in the
 * order the columns were selected.
 *
 * The [<code>Sequence</code>][Sequence] is lazy — the values are pulled from this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] one by one, as the
 * sequence is consumed.
 *
 * Don't confuse this `values` with the aggregation [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.values], which collects
 * the values of the selected columns for every group of a [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] or
 * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] result.
 *
 * See also:
 * - [<code>valuesNotNull</code>][org.jetbrains.kotlinx.dataframe.DataFrame.valuesNotNull] — the same, but `null` values are skipped.
 * - [<code>asSequence</code>][org.jetbrains.kotlinx.dataframe.DataFrame.asSequence] — a [<code>Sequence</code>][Sequence] over the [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 *
 * This overload extracts the values from all columns of this [<code>DataFrame</code>][DataFrame].
 *
 * ### Examples
 *
 * ```kotlin
 * val allValues: Sequence<Any?> = df.values()
 * ```
 *
 * ```kotlin
 * val allValues: Sequence<Any?> = df.values(byRows = true)
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [byRows] When `true`, the values come row by row: for each row, the values of all
 *   columns in the order the columns appear in this [<code>DataFrame</code>][DataFrame]. When `false` (default), the
 *   values come column by column: all values of the first column, then all values of the
 *   second one, and so on.
 * @return A lazy [<code>Sequence</code>][Sequence] of the values of all columns of this [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataFrame<T>.values(byRows: Boolean = false): Sequence<Any?> = values(byRows) { all() }

/**
 * Extracts the values of the selected columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] into a lazy [<code>Sequence</code>][Sequence],
 * skipping `null` values.
 *
 * The order of the values is the same as in [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values]: column by column
 * by default, row by row with `byRow = true`.
 *
 * See also:
 * - [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] — the same, but `null` values are included.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `valuesNotNull` overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples
 *
 * ```kotlin
 * val values: Sequence<Int> = df.valuesNotNull { age and weight }
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [C] The common type of the values of the selected columns.
 * @param [byRow] When `true`, the values come row by row: for each row, the values of
 *   all selected columns in the order the columns were selected. When `false` (default),
 *   the values come column by column: all values of the first selected column, then all
 *   values of the second one, and so on.
 * @param [columns] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns to extract
 *   the values from.
 * @return A lazy [<code>Sequence</code>][Sequence] of the non-`null` values of the selected columns.
 */
public fun <T, C> DataFrame<T>.valuesNotNull(byRow: Boolean = false, columns: ColumnsSelector<T, C?>): Sequence<C> =
    values(byRow, columns).filterNotNull()

/**
 * Extracts the values of the selected columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] into a lazy [<code>Sequence</code>][Sequence],
 * skipping `null` values.
 *
 * The order of the values is the same as in [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values]: column by column
 * by default, row by row with `byRow = true`.
 *
 * See also:
 * - [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] — the same, but `null` values are included.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `valuesNotNull` overload
 *
 * This overload extracts the values from all columns of this [<code>DataFrame</code>][DataFrame].
 *
 * ### Examples
 *
 * ```kotlin
 * val values: Sequence<Any> = df.valuesNotNull()
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [byRow] When `true`, the values come row by row: for each row, the values of
 *   all selected columns in the order the columns were selected. When `false` (default),
 *   the values come column by column: all values of the first selected column, then all
 *   values of the second one, and so on.
 * @return A lazy [<code>Sequence</code>][Sequence] of the non-`null` values of all columns of this [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataFrame<T>.valuesNotNull(byRow: Boolean = false): Sequence<Any> = valuesNotNull(byRow) { all() }

// endregion

// region GroupBy

/**
 * Message appended to a [<code>DuplicateColumnPathInsertException</code>][DuplicateColumnPathInsertException] rethrown by the aggregation
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
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by collecting the values of the columns with the given names
 * from all rows of each group into a [<code>List</code>][List].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the grouping key columns
 * and one result column per selected column. Each result column is named after
 * its source column and contains the [<code>List</code>][List] of that column's values
 * collected from all rows of the group.
 *
 * Values from a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * are collected into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] instead of a [<code>List</code>][List],
 * making the corresponding result column a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
 *
 * Check out [<code>`groupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar].
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html) [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * Don't confuse this with [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy.values] on a [<code>ReducedGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy],
 * which takes the values of the single reduced row of each group instead of all its rows.
 *
 * See also:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.into] — collects the values computed for each row of a group into a new column,
 *   or stores the groups themselves in a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 * - [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>GroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Aggregation] — all other ways to aggregate a [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy].
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Examples
 *
 * ```kotlin
 * // Collect the "name" and "age" values of all rows of each group into lists
 * df.groupBy("isHappy").values("name", "age")
 * ```
 *
 * @param [columns] The names of the columns to collect the values from.
 * @param [dropNA] If `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`, [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN])
 *   are removed from the collected values. `false` by default.
 *   Each column is filtered independently of the others.
 * @param [distinct] If `true`, duplicate values are removed from the collected values,
 *   keeping the first occurrence of each. `false` by default.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns
 *   and the collected values of the columns with the given names.
 */
public fun <T> Grouped<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
): DataFrame<T> = values(dropNA, distinct) { columns.toColumnSet() }

/**
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by collecting the values of the selected columns
 * from all rows of each group into a [<code>List</code>][List].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the grouping key columns
 * and one result column per selected column. Each result column is named after
 * its source column and contains the [<code>List</code>][List] of that column's values
 * collected from all rows of the group.
 *
 * Values from a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * are collected into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] instead of a [<code>List</code>][List],
 * making the corresponding result column a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
 *
 * Check out [<code>`groupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar].
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html) [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * Don't confuse this with [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy.values] on a [<code>ReducedGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy],
 * which takes the values of the single reduced row of each group instead of all its rows.
 *
 * See also:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.into] — collects the values computed for each row of a group into a new column,
 *   or stores the groups themselves in a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 * - [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>GroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Aggregation] — all other ways to aggregate a [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy].
 *
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
 * ### Examples
 *
 * ```kotlin
 * // Collect the "name" and "age" values of all rows of each group into lists
 * df.groupBy { isHappy }.values { name and age }
 * ```
 * ```kotlin
 * // Collect the "age" column values of all rows of each group into a list,
 * // and store it in a new "ages" column
 * df.groupBy { isHappy }.values { age into "ages" }
 * ```
 *
 * @param [dropNA] If `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`, [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN])
 *   are removed from the collected values. `false` by default.
 *   Each column is filtered independently of the others.
 * @param [distinct] If `true`, duplicate values are removed from the collected values,
 *   keeping the first occurrence of each. `false` by default.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][ColumnsForAggregateSelector] used to select the columns to collect the values from.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns and the collected values of the selected columns.
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
 *
 *
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by collecting the values of all remaining columns (all group columns except the grouping keys)
 * from all rows of each group into a [<code>List</code>][List].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group, containing the grouping key columns
 * and one result column per selected column. Each result column is named after
 * its source column and contains the [<code>List</code>][List] of that column's values
 * collected from all rows of the group.
 *
 * Values from a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * are collected into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] instead of a [<code>List</code>][List],
 * making the corresponding result column a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
 *
 * Check out [<code>`groupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar].
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html) [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * Don't confuse this with [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy.values] on a [<code>ReducedGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy],
 * which takes the values of the single reduced row of each group instead of all its rows.
 *
 * See also:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.into] — collects the values computed for each row of a group into a new column,
 *   or stores the groups themselves in a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 * - [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] — the general way to aggregate groups.
 * - [<code>GroupBy aggregation</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Aggregation] — all other ways to aggregate a [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // Collect the values of all columns except the grouping keys
 * // from all rows of each group into lists
 * df.groupBy { isHappy }.values()
 * ```
 *
 * @param [dropNA] If `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`, [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN])
 *   are removed from the collected values. `false` by default.
 *   Each column is filtered independently of the others.
 * @param [distinct] If `true`, duplicate values are removed from the collected values,
 *   keeping the first occurrence of each. `false` by default.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns
 *   and the collected values of all remaining columns.
 */
public fun <T> Grouped<T>.values(dropNA: Boolean = false, distinct: Boolean = false): DataFrame<T> =
    values(dropNA, distinct, remainingColumnsSelector())

// endregion

// region ReducedGroupBy

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.first]), the result still contains a row for this group.
 * This row is treated as missing every value, so its value columns contain `null` and become nullable.
 *
 * Missing values in a [<code>List</code>][List] column are represented by empty lists, and missing values in a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] by empty
 * [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; these columns do not become nullable. A missing [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] remains a column group, and its
 * nested columns are filled according to these same rules recursively.
 *
 * This overload collects the values from all columns except the group keys.
 *
 * For more information: [See "`GroupBy` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#reducing)
 *
 * See also:
 * - [<code>into</code>][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [<code>concat</code>][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * ```kotlin
 * // the "name" and "weight" of the youngest person in each city
 * val youngest = df.groupBy { city }.minBy { age }.values { name and weight }
 * // the same, for all columns except the keys
 * val youngestAll = df.groupBy { city }.minBy { age }.values()
 * ```
 *
 * ```kotlin
 * // For each `isHappy` value, the values of the youngest person
 * df.groupBy { isHappy }.minBy { age }.values()
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][DataFrame] the groups were created from.
 * @param [G] The schema marker type of the group data.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group: the group keys, and, per selected
 *   column, the value from the row that the reduction (for example, `minBy`) picked in
 *   that group, or `null` if the reduction picked no row.
 */
public fun <T, G> ReducedGroupBy<T, G>.values(): DataFrame<G> = values(groupBy.remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: ColumnReference<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.first]), the result still contains a row for this group.
 * This row is treated as missing every value, so its value columns contain `null` and become nullable.
 *
 * Missing values in a [<code>List</code>][List] column are represented by empty lists, and missing values in a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] by empty
 * [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; these columns do not become nullable. A missing [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] remains a column group, and its
 * nested columns are filled according to these same rules recursively.
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * For more information: [See "`GroupBy` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#reducing)
 *
 * See also:
 * - [<code>into</code>][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [<code>concat</code>][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * ```kotlin
 * val youngest = df.groupBy("city").minBy("age").values("name", "weight")
 * ```
 *
 * ```kotlin
 * df.groupBy("isHappy").minBy("age").values("name", "age", "city")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][DataFrame] the groups were created from.
 * @param [G] The schema marker type of the group data.
 * @param [columns] The names of the columns to collect the values from.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group: the group keys, and, per selected
 *   column, the value from the row that the reduction (for example, `minBy`) picked in
 *   that group, or `null` if the reduction picked no row.
 */
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: String): DataFrame<G> = values { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: KProperty<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.first]), the result still contains a row for this group.
 * This row is treated as missing every value, so its value columns contain `null` and become nullable.
 *
 * Missing values in a [<code>List</code>][List] column are represented by empty lists, and missing values in a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] by empty
 * [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; these columns do not become nullable. A missing [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] remains a column group, and its
 * nested columns are filled according to these same rules recursively.
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * For more information: [See "`GroupBy` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#reducing)
 *
 * See also:
 * - [<code>into</code>][ReducedGroupBy.into] — puts the reduced rows, or a value computed from each of them, into a new column.
 * - [<code>concat</code>][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * ```kotlin
 * // the "name" and "weight" of the youngest person in each city
 * val youngest = df.groupBy { city }.minBy { age }.values { name and weight }
 * // the same, for all columns except the keys
 * val youngestAll = df.groupBy { city }.minBy { age }.values()
 * ```
 *
 * ```kotlin
 * df.groupBy { isHappy }.minBy { age }.values { name and age and city }
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][DataFrame] the groups were created from.
 * @param [G] The schema marker type of the group data.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns to
 *   collect the values from.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group: the group keys, and, per selected
 *   column, the value from the row that the reduction (for example, `minBy`) picked in
 *   that group, or `null` if the reduction picked no row.
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
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by collecting the values of the selected columns
 * from all rows of each pivot group into a [<code>List</code>][List].
 *
 * Returns a new [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as top-level columns
 * (or as [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * for multiple or nested pivot keys).
 * The value of each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key is aggregated from the corresponding group:
 *
 *
 *
 * For each pivot cell: an empty group yields `null`, a group of exactly one row yields
 * that single value, and a group of several rows yields a `List` of all its values.
 *
 * If a single column is selected, its values are stored directly under the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys.
 * If several columns are selected, each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key contains one subcolumn
 * per selected column, named after it
 * (or, if [separate] is `true`, the results are grouped
 * by the selected columns first, and by the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys second).
 *
 * Values from a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * follow the same rule: a single row gives a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow],
 * and several rows are collected into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Check out [<code>`pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html) [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * Don't confuse this with [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.values] on a [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot],
 * which takes the values of the single reduced row of each group instead of all its rows.
 *
 * See also:
 * - [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.aggregate] — the general way to aggregate pivot groups.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 *
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
 * ### Examples
 *
 * ```kotlin
 * // For each "isHappy" value, collect the "name" and "age" values of all rows of the group
 * // into lists and store them in the nested columns with the same names as the original ones
 * df.pivot { isHappy }.values { name and age }
 * ```
 *
 * @param [T] The schema marker type of the pivoted [<code>DataFrame</code>][DataFrame].
 * @param [dropNA] If `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`, [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN])
 *   are removed from the collected values. `false` by default.
 *   Each column is filtered independently of the others.
 * @param [distinct] If `true`, duplicate values are removed from the collected values,
 *   keeping the first occurrence of each. `false` by default.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][ColumnsForAggregateSelector] used to select the columns to collect the values from.
 * @return A new [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys and the collected values of the selected columns.
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
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by collecting the values of the columns with the given names
 * from all rows of each pivot group into a [<code>List</code>][List].
 *
 * Returns a new [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as top-level columns
 * (or as [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * for multiple or nested pivot keys).
 * The value of each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key is aggregated from the corresponding group:
 *
 *
 *
 * For each pivot cell: an empty group yields `null`, a group of exactly one row yields
 * that single value, and a group of several rows yields a `List` of all its values.
 *
 * If a single column is selected, its values are stored directly under the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys.
 * If several columns are selected, each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key contains one subcolumn
 * per selected column, named after it
 * (or, if [separate] is `true`, the results are grouped
 * by the selected columns first, and by the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys second).
 *
 * Values from a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * follow the same rule: a single row gives a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow],
 * and several rows are collected into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Check out [<code>`pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html) [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * Don't confuse this with [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.values] on a [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot],
 * which takes the values of the single reduced row of each group instead of all its rows.
 *
 * See also:
 * - [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.aggregate] — the general way to aggregate pivot groups.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Examples
 *
 * ```kotlin
 * // For each "isHappy" value, collect the "name" and "age" values of all rows of the group
 * // into lists and store them in the nested columns with the same names as the original ones
 * df.pivot("isHappy").values("name", "age")
 * ```
 *
 * @param [T] The schema marker type of the pivoted [<code>DataFrame</code>][DataFrame].
 * @param [columns] The names of the columns to collect the values from.
 * @param [dropNA] If `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`, [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN])
 *   are removed from the collected values. `false` by default.
 *   Each column is filtered independently of the others.
 * @param [distinct] If `true`, duplicate values are removed from the collected values,
 *   keeping the first occurrence of each. `false` by default.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @return A new [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys
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
 *
 *
 * Aggregates this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] by collecting the values of all remaining columns (all columns except the pivoted columns)
 * from all rows of each pivot group into a [<code>List</code>][List].
 *
 * Returns a new [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys as top-level columns
 * (or as [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * for multiple or nested pivot keys).
 * The value of each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key is aggregated from the corresponding group:
 *
 *
 *
 * For each pivot cell: an empty group yields `null`, a group of exactly one row yields
 * that single value, and a group of several rows yields a `List` of all its values.
 *
 * If a single column is selected, its values are stored directly under the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys.
 * If several columns are selected, each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key contains one subcolumn
 * per selected column, named after it
 * (or, if [separate] is `true`, the results are grouped
 * by the selected columns first, and by the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] keys second).
 *
 * Values from a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * follow the same rule: a single row gives a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow],
 * and several rows are collected into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Check out [<code>`pivot` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html) [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * Don't confuse this with [<code>values</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot.values] on a [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot],
 * which takes the values of the single reduced row of each group instead of all its rows.
 *
 * See also:
 * - [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.aggregate] — the general way to aggregate pivot groups.
 * - [<code>Pivot aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot].
 *
 *
 * ### Examples
 *
 * ```kotlin
 * // For each "isHappy" value, collect the values of all columns except the pivoted one
 * // from all rows of the group into lists
 * // and store them in the nested columns with the same names as the original ones
 * df.pivot { isHappy }.values()
 * ```
 *
 * @param [T] The schema marker type of the pivoted [<code>DataFrame</code>][DataFrame].
 * @param [dropNA] If `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`, [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN])
 *   are removed from the collected values. `false` by default.
 *   Each column is filtered independently of the others.
 * @param [distinct] If `true`, duplicate values are removed from the collected values,
 *   keeping the first occurrence of each. `false` by default.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @return A new [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys and the collected values of all remaining columns.
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
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
 * This overload collects the values from all columns except the pivot columns.
 *
 * For more information: [See "`Pivot` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
 *
 * See also:
 * - [<code>with</code>][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * ```kotlin
 * // the "name" and "weight" of the youngest person in each city
 * val youngest = df.pivot { city }.minBy { age }.values { name and weight }
 * // the same, for all columns except the pivot column
 * val youngestAll = df.pivot { city }.minBy { age }.values()
 * ```
 *
 * ```kotlin
 * df.pivot { isHappy }.first().values()
 * ```
 *
 * @param [T] The schema marker type of the original [<code>DataFrame</code>][DataFrame].
 * @param [separate] When several columns are selected: when `true`, each selected column
 *   becomes its own column group with one nested column per pivot value; when `false`
 *   (default), each pivot value becomes its own column group with one nested column per
 *   selected column. When a single column is selected, its values go directly into the
 *   pivot cells, and this parameter has no effect.
 * @return A single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with one (nested) column per unique combination of the pivot
 *   column values; every cell holds, per selected column, the value from the row that the
 *   reduction (for example, `minBy`) picked for that pivot value, or `null` if the
 *   reduction picked no row.
 */
public fun <T> ReducedPivot<T>.values(separate: Boolean = false): DataRow<*> =
    pivot.delegate { reduce(reducer).values(separate = separate) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: ColumnReference<*>, separate: Boolean = false): DataRow<*> =
    values(separate) { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
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
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * For more information: [See "`Pivot` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
 *
 * See also:
 * - [<code>with</code>][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * ```kotlin
 * val youngest = df.pivot("city").minBy("age").values("name", "weight")
 * ```
 *
 * ```kotlin
 * // For each `isHappy` value, the `name` and `age` of the first person
 * df.pivot("isHappy").first().values("name", "age")
 * ```
 *
 * @param [T] The schema marker type of the original [<code>DataFrame</code>][DataFrame].
 * @param [columns] The names of the columns to collect the values from.
 * @param [separate] When several columns are selected: when `true`, each selected column
 *   becomes its own column group with one nested column per pivot value; when `false`
 *   (default), each pivot value becomes its own column group with one nested column per
 *   selected column. When a single column is selected, its values go directly into the
 *   pivot cells, and this parameter has no effect.
 * @return A single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with one (nested) column per unique combination of the pivot
 *   column values; every cell holds, per selected column, the value from the row that the
 *   reduction (for example, `minBy`) picked for that pivot value, or `null` if the
 *   reduction picked no row.
 */
public fun <T> ReducedPivot<T>.values(vararg columns: String, separate: Boolean = false): DataRow<*> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataRow<*> =
    values(separate) { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
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
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * For more information: [See "`Pivot` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
 *
 * See also:
 * - [<code>with</code>][ReducedPivot.with] — computes a value from each reduced row.
 *
 * ### Examples
 *
 * ```kotlin
 * // the "name" and "weight" of the youngest person in each city
 * val youngest = df.pivot { city }.minBy { age }.values { name and weight }
 * // the same, for all columns except the pivot column
 * val youngestAll = df.pivot { city }.minBy { age }.values()
 * ```
 *
 * ```kotlin
 * // For each `isHappy` value, the `name` and `age` of the first person
 * df.pivot { isHappy }.first().values { name and age }
 * ```
 *
 * @param [T] The schema marker type of the original [<code>DataFrame</code>][DataFrame].
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns to
 *   collect the values from.
 * @param [separate] When several columns are selected: when `true`, each selected column
 *   becomes its own column group with one nested column per pivot value; when `false`
 *   (default), each pivot value becomes its own column group with one nested column per
 *   selected column. When a single column is selected, its values go directly into the
 *   pivot cells, and this parameter has no effect.
 * @return A single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with one (nested) column per unique combination of the pivot
 *   column values; every cell holds, per selected column, the value from the row that the
 *   reduction (for example, `minBy`) picked for that pivot value, or `null` if the
 *   reduction picked no row.
 */
public fun <T> ReducedPivot<T>.values(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, *>,
): DataRow<*> = pivot.delegate { reduce(reducer).values(separate = separate, columns = columns) }

// endregion

// region PivotGroupBy

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 *
 * This overload collects the values from all columns except the pivot columns and
 * the [<code>groupBy</code>][DataFrame.groupBy] keys.
 *
 * @param [T] The schema marker type of the original [<code>DataFrame</code>][DataFrame].
 * @param [dropNA] When `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values are removed from the collected
 *   values. `false` by default. Each column is filtered independently of the others.
 * @param [distinct] When `true`, only distinct values are collected: the first occurrence
 *   of each value, in the order the values appear. `false` by default.
 * @param [separate] When several columns are selected: when `true`, each selected column
 *   becomes its own column group with one nested column per pivot value; when `false`
 *   (default), each pivot value becomes its own column group with one nested column per
 *   selected column. When a single column is selected, its values go directly into the
 *   pivot cells, and this parameter has no effect.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per combination of the [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]
 *   key values and one (nested) column per unique combination of the pivot column values;
 *   every cell holds the collected values of the corresponding rows: the single value if there is exactly one, a [<code>List</code>][List] if there are several, and an empty
 *   cell if there is nothing to collect. When the other cells of that column hold
 *   lists, a cell holds a [<code>List</code>][List] too: a single value as a one-element [<code>List</code>][List], a cell
 *   with no rows as an empty [<code>List</code>][List]; a cell with no rows is `null` otherwise, and a
 *   cell emptied by `dropNA` is always `null`. A selected column group is collected
 *   as a nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the collected rows of the columns inside it, instead
 *   of a [<code>List</code>][List].
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Examples
 *
 * ```kotlin
 * val result = df.pivot("city").groupBy("name").values("age")
 * ```
 *
 * @param [T] The schema marker type of the original [<code>DataFrame</code>][DataFrame].
 * @param [columns] The names of the columns to collect the values from.
 * @param [dropNA] When `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values are removed from the collected
 *   values. `false` by default. Each column is filtered independently of the others.
 * @param [distinct] When `true`, only distinct values are collected: the first occurrence
 *   of each value, in the order the values appear. `false` by default.
 * @param [separate] When several columns are selected: when `true`, each selected column
 *   becomes its own column group with one nested column per pivot value; when `false`
 *   (default), each pivot value becomes its own column group with one nested column per
 *   selected column. When a single column is selected, its values go directly into the
 *   pivot cells, and this parameter has no effect.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per combination of the [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]
 *   key values and one (nested) column per unique combination of the pivot column values;
 *   every cell holds the collected values of the corresponding rows: the single value if there is exactly one, a [<code>List</code>][List] if there are several, and an empty
 *   cell if there is nothing to collect. When the other cells of that column hold
 *   lists, a cell holds a [<code>List</code>][List] too: a single value as a one-element [<code>List</code>][List], a cell
 *   with no rows as an empty [<code>List</code>][List]; a cell with no rows is `null` otherwise, and a
 *   cell emptied by `dropNA` is always `null`. A selected column group is collected
 *   as a nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the collected rows of the columns inside it, instead
 *   of a [<code>List</code>][List].
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples
 *
 * ```kotlin
 * // for every person, the "age" values per city: a single value when there is one,
 * // a list when there are several, and an empty cell when there is nothing to collect
 * val result = df.pivot { city }.groupBy { name }.values { age }
 * // with dropNA, `null` weights are skipped in every cell: a cell whose values
 * // are all `null` collects nothing
 * val withoutNA = df.pivot { city }.groupBy { name }.values(dropNA = true) { weight }
 * ```
 *
 * @param [T] The schema marker type of the original [<code>DataFrame</code>][DataFrame].
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns to
 *   collect the values from.
 * @param [dropNA] When `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values are removed from the collected
 *   values. `false` by default. Each column is filtered independently of the others.
 * @param [distinct] When `true`, only distinct values are collected: the first occurrence
 *   of each value, in the order the values appear. `false` by default.
 * @param [separate] When several columns are selected: when `true`, each selected column
 *   becomes its own column group with one nested column per pivot value; when `false`
 *   (default), each pivot value becomes its own column group with one nested column per
 *   selected column. When a single column is selected, its values go directly into the
 *   pivot cells, and this parameter has no effect.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per combination of the [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]
 *   key values and one (nested) column per unique combination of the pivot column values;
 *   every cell holds the collected values of the corresponding rows: the single value if there is exactly one, a [<code>List</code>][List] if there are several, and an empty
 *   cell if there is nothing to collect. When the other cells of that column hold
 *   lists, a cell holds a [<code>List</code>][List] too: a single value as a one-element [<code>List</code>][List], a cell
 *   with no rows as an empty [<code>List</code>][List]; a cell with no rows is `null` otherwise, and a
 *   cell emptied by `dropNA` is always `null`. A selected column group is collected
 *   as a nested [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the collected rows of the columns inside it, instead
 *   of a [<code>List</code>][List].
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 *
 * This overload collects the values from all columns except the pivot columns and
 * the [<code>groupBy</code>][DataFrame.groupBy] keys.
 *
 * @param [T] The schema marker type of the original [<code>DataFrame</code>][DataFrame].
 * @param [separate] When several columns are selected: when `true`, each selected column
 *   becomes its own column group with one nested column per pivot value; when `false`
 *   (default), each pivot value becomes its own column group with one nested column per
 *   selected column. When a single column is selected, its values go directly into the
 *   pivot cells, and this parameter has no effect.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per combination of the [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]
 *   key values and one (nested) column per unique combination of the pivot column values;
 *   every cell holds, per selected column, the value from the row that the reduction
 *   (for example, `minBy`) picked in that cell, or `null` if the reduction picked no row.
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Examples
 *
 * ```kotlin
 * val result = df.pivot("city").groupBy("name").minBy("age").values("weight")
 * ```
 *
 * @param [T] The schema marker type of the original [<code>DataFrame</code>][DataFrame].
 * @param [columns] The names of the columns to collect the values from.
 * @param [separate] When several columns are selected: when `true`, each selected column
 *   becomes its own column group with one nested column per pivot value; when `false`
 *   (default), each pivot value becomes its own column group with one nested column per
 *   selected column. When a single column is selected, its values go directly into the
 *   pivot cells, and this parameter has no effect.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per combination of the [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]
 *   key values and one (nested) column per unique combination of the pivot column values;
 *   every cell holds, per selected column, the value from the row that the reduction
 *   (for example, `minBy`) picked in that cell, or `null` if the reduction picked no row.
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected into its own column of the result.
 *
 * Don't confuse this `values` with [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataFrame.values] on a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], which
 * extracts the values of the selected columns into a lazy [<code>Sequence</code>][Sequence], without any grouping.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `values` on the documentation website.](https://kotlin.github.io/dataframe/values.html)
 *
 * ### This `values` overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples
 *
 * ```kotlin
 * // the "weight" of the youngest person for every name and city
 * val result = df.pivot { city }.groupBy { name }.minBy { age }.values { weight }
 * ```
 *
 * @param [T] The schema marker type of the original [<code>DataFrame</code>][DataFrame].
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns to
 *   collect the values from.
 * @param [separate] When several columns are selected: when `true`, each selected column
 *   becomes its own column group with one nested column per pivot value; when `false`
 *   (default), each pivot value becomes its own column group with one nested column per
 *   selected column. When a single column is selected, its values go directly into the
 *   pivot cells, and this parameter has no effect.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per combination of the [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]
 *   key values and one (nested) column per unique combination of the pivot column values;
 *   every cell holds, per selected column, the value from the row that the reduction
 *   (for example, `minBy`) picked in that cell, or `null` if the reduction picked no row.
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
