package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
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
 * then all values of the second one, and so on. With `byRow = true`, the values come row by
 * row instead: for each row, the values of the selected columns in the order the columns
 * were selected.
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
 * then all values of the second one, and so on. With `byRow = true`, the values come row by
 * row instead: for each row, the values of the selected columns in the order the columns
 * were selected.
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * val result = df.groupBy("city").values("name", "age")
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [columns] The names of the columns to collect the values from.
 * @param [dropNA] When `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values are removed from the collected
 *   values. `false` by default. Each column is filtered independently of the others.
 * @param [distinct] When `true`, only distinct values are collected: the first occurrence
 *   of each value, in the order the values appear. `false` by default.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group: the group keys, and, per selected
 *   column, a [<code>List</code>][List] with all values from the group.
 */
public fun <T> Grouped<T>.values(
    vararg columns: String,
    dropNA: Boolean = false,
    distinct: Boolean = false,
): DataFrame<T> = values(dropNA, distinct) { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * // for every city, the "name" and "age" values of its people, as lists
 * val result = df.groupBy { city }.values { name and age }
 * ```
 *
 * ```kotlin
 * // `null` values are skipped in the "weight" lists, while "name" lists stay whole
 * val result = df.groupBy { city }.values(dropNA = true) { name and weight }
 * ```
 *
 * ```kotlin
 * // only distinct "name" values per city
 * val result = df.groupBy { city }.values(distinct = true) { name }
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns to
 *   collect the values from.
 * @param [dropNA] When `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values are removed from the collected
 *   values. `false` by default. Each column is filtered independently of the others.
 * @param [distinct] When `true`, only distinct values are collected: the first occurrence
 *   of each value, in the order the values appear. `false` by default.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group: the group keys, and, per selected
 *   column, a [<code>List</code>][List] with all values from the group.
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * This overload collects the values from all columns except the group keys.
 *
 * ### Examples
 *
 * ```kotlin
 * // all columns except the keys, as lists per city
 * val result = df.groupBy { city }.values()
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [dropNA] When `true`, [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values are removed from the collected
 *   values. `false` by default. Each column is filtered independently of the others.
 * @param [distinct] When `true`, only distinct values are collected: the first occurrence
 *   of each value, in the order the values appear. `false` by default.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group: the group keys, and, per selected
 *   column, a [<code>List</code>][List] with all values from the group.
 */
public fun <T> Grouped<T>.values(dropNA: Boolean = false, distinct: Boolean = false): DataFrame<T> =
    values(dropNA, distinct, remainingColumnsSelector())

// endregion

// region ReducedGroupBy

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * This overload collects the values from all columns except the group keys.
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
 * @param [T] The schema marker type of the [<code>DataFrame</code>][DataFrame] the groups were created from.
 * @param [G] The schema marker type of the group data.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group: the group keys, and, per selected
 *   column, the value from the row that the reduction (for example, `minBy`) picked
 *   in that group.
 */
public fun <T, G> ReducedGroupBy<T, G>.values(): DataFrame<G> = values(groupBy.remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: ColumnReference<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * val youngest = df.groupBy("city").minBy("age").values("name", "weight")
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][DataFrame] the groups were created from.
 * @param [G] The schema marker type of the group data.
 * @param [columns] The names of the columns to collect the values from.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group: the group keys, and, per selected
 *   column, the value from the row that the reduction (for example, `minBy`) picked
 *   in that group.
 */
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: String): DataFrame<G> = values { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.values(vararg columns: KProperty<*>): DataFrame<G> =
    values { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * // the "name" and "weight" of the youngest person in each city
 * val youngest = df.groupBy { city }.minBy { age }.values { name and weight }
 * // the same, for all columns except the keys
 * val youngestAll = df.groupBy { city }.minBy { age }.values()
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][DataFrame] the groups were created from.
 * @param [G] The schema marker type of the group data.
 * @param [columns] The [<code>ColumnsForAggregateSelector</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector] used to select the columns to
 *   collect the values from.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with one row per group: the group keys, and, per selected
 *   column, the value from the row that the reduction (for example, `minBy`) picked
 *   in that group.
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * // the "age" values of each city, as a single row
 * val result = df.pivot { city }.values { age }
 * // the same, for all columns except the pivot column
 * val resultAll = df.pivot { city }.values()
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
 * @return A single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with one (nested) column per unique combination of the pivot
 *   column values; every cell holds the collected values of the rows with that pivot
 *   value: the single value if there is exactly one, a [<code>List</code>][List] if there are several, and an empty
 *   cell if there is nothing to collect. An empty cell is `null`, or an empty [<code>List</code>][List] when
 *   the other cells of that column hold lists; it appears when the cell has no rows, or
 *   when `dropNA` removed all the values..
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * val result = df.pivot("city").values("age")
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
 * @return A single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with one (nested) column per unique combination of the pivot
 *   column values; every cell holds the collected values of the rows with that pivot
 *   value: the single value if there is exactly one, a [<code>List</code>][List] if there are several, and an empty
 *   cell if there is nothing to collect. An empty cell is `null`, or an empty [<code>List</code>][List] when
 *   the other cells of that column hold lists; it appears when the cell has no rows, or
 *   when `dropNA` removed all the values..
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * This overload collects the values from all columns except the pivot columns.
 *
 * ### Examples
 *
 * ```kotlin
 * // the "age" values of each city, as a single row
 * val result = df.pivot { city }.values { age }
 * // the same, for all columns except the pivot column
 * val resultAll = df.pivot { city }.values()
 * ```
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
 * @return A single [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] with one (nested) column per unique combination of the pivot
 *   column values; every cell holds the collected values of the rows with that pivot
 *   value: the single value if there is exactly one, a [<code>List</code>][List] if there are several, and an empty
 *   cell if there is nothing to collect. An empty cell is `null`, or an empty [<code>List</code>][List] when
 *   the other cells of that column hold lists; it appears when the cell has no rows, or
 *   when `dropNA` removed all the values..
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * This overload collects the values from all columns except the pivot columns.
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
public fun <T> ReducedPivot<T>.values(separate: Boolean = false): DataRow<T> =
    pivot.delegate { reduce(reducer).values(separate = separate) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: ColumnReference<*>, separate: Boolean = false): DataRow<T> =
    values(separate) { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * val youngest = df.pivot("city").minBy("age").values("name", "weight")
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
public fun <T> ReducedPivot<T>.values(vararg columns: String, separate: Boolean = false): DataRow<T> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivot<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataRow<T> =
    values(separate) { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * // the "name" and "weight" of the youngest person in each city
 * val youngest = df.pivot { city }.minBy { age }.values { name and weight }
 * // the same, for all columns except the pivot column
 * val youngestAll = df.pivot { city }.minBy { age }.values()
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
): DataRow<T> = pivot.delegate { reduce(reducer).values(separate = separate, columns = columns) }

// endregion

// region PivotGroupBy

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 *   cell if there is nothing to collect. An empty cell is `null`, or an empty [<code>List</code>][List] when
 *   the other cells of that column hold lists; it appears when the cell has no rows, or
 *   when `dropNA` removed all the values..
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 *   cell if there is nothing to collect. An empty cell is `null`, or an empty [<code>List</code>][List] when
 *   the other cells of that column hold lists; it appears when the cell has no rows, or
 *   when `dropNA` removed all the values..
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
 * // with dropNA, a cell whose values are all `null` becomes empty as well
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
 *   cell if there is nothing to collect. An empty cell is `null`, or an empty [<code>List</code>][List] when
 *   the other cells of that column hold lists; it appears when the cell has no rows, or
 *   when `dropNA` removed all the values..
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
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
public fun <T> ReducedPivotGroupBy<T>.values(separate: Boolean = false): DataFrame<T> =
    values(separate, pivot.remainingColumnsSelector())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivotGroupBy<T>.values(
    vararg columns: ColumnReference<*>,
    separate: Boolean = false,
): DataFrame<T> = values(separate) { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
public fun <T> ReducedPivotGroupBy<T>.values(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    values(separate) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ReducedPivotGroupBy<T>.values(vararg columns: KProperty<*>, separate: Boolean = false): DataFrame<T> =
    values(separate) { columns.toColumnSet() }

/**
 * Collects the values of the selected columns for every group.
 *
 * Each selected column is collected independently of the others.
 *
 * When the groups have lost track of the key columns — this can happen after
 * [<code>updateGroups</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.updateGroups], [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.filter], or [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.add]
 * changed the columns inside the groups — the collected columns can collide with the key
 * columns, and a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown with a hint about the
 * cause. Try renaming the columns inside the groups or aggregate them manually instead.
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
