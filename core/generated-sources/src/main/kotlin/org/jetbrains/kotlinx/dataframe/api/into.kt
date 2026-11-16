package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.withExpr
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region GroupBy

/**
 * Converts this [<code>GroupBy</code>][GroupBy] into a [<code>DataFrame</code>][DataFrame].
 *
 * Does the same as [<code>GroupBy.toDataFrame</code>][GroupBy.toDataFrame].
 *
 * Each row of the resulting [<code>DataFrame</code>][DataFrame] represents a unique keys–group pair:
 * a row from [<code>keys</code>][keys] and its corresponding group of rows (as [<code>DataFrame</code>][DataFrame]).
 *
 * Resulting [<code>DataFrame</code>][DataFrame] contains a [<code>FrameColumn</code>][FrameColumn] with [<code>groups</code>][groups].
 *
 * Check out [<code>groupBy Grammar</code>][GroupByDocs.Grammar].
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * @param column The name of the column in which to store grouped data.
 * @return A new [<code>DataFrame</code>][DataFrame] that includes the grouping key columns together
 * with a [<code>FrameColumn</code>][FrameColumn] containing the corresponding groups.
 */
@Refine
@Interpretable("GroupByInto")
public fun <T, G> GroupBy<T, G>.into(column: String): DataFrame<T> = toDataFrame(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> GroupBy<T, *>.into(column: ColumnAccessor<DataFrame<*>>): DataFrame<T> = toDataFrame(column.name())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> GroupBy<T, *>.into(column: KProperty<DataFrame<*>>): DataFrame<T> = toDataFrame(column.columnName)

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] into a [<code>DataFrame</code>][DataFrame]
 * by computing the values of [<code>expression</code>][expression] on all rows for each group.
 *
 * Each row of the resulting [<code>DataFrame</code>][DataFrame] represents a unique keys–value pair:
 * a row from [<code>keys</code>][keys] and list of values, computed on all rows of the corresponding group using
 * provided [<code>expression</code>][expression].
 *
 * Check out [<code>groupBy Grammar</code>][GroupByDocs.Grammar].
 *
 * See [<code>Row Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow]
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * #### Example
 * ```kotlin
 * // Compute list of prices within each city
 * // by multiplying "amount" and "cost" values.
 * df.groupBy { city }.into("prices") {
 *    amount * cost
 * }
 * ```
 *
 * @param [columnName] The name of the column in which to store aggregated data.
 * @param [expression] The expression to compute on each group row.
 * @return A new [<code>DataFrame</code>][DataFrame] that includes the grouping key columns together
 * with a list of values computed on all rows of corresponding groups.
 */
public inline fun <T, G, reified V> GroupBy<T, G>.into(
    columnName: String? = null,
    noinline expression: RowExpression<G, V>,
): DataFrame<G> = into(pathOf(columnName ?: groups.name()), expression, typeOf<V>())

// @Hide
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified V> GroupBy<T, G>.into(
    column: ColumnAccessor<V>,
    noinline expression: RowExpression<G, V>,
): DataFrame<G> {
    val type = typeOf<V>()
    val path = column.path()
    return aggregate {
        internal().withExpr(type, path, expression)
    }
}

@PublishedApi
internal fun <T, G, V> GroupBy<T, G>.into(
    path: ColumnPath,
    expression: RowExpression<G, V>,
    type: KType,
): DataFrame<G> =
    aggregate {
        internal().withExpr(type, path, expression)
    }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified V> GroupBy<T, G>.into(
    column: KProperty<V>,
    noinline expression: RowExpression<G, V>,
): DataFrame<G> = into(column.columnName, expression)

// endregion

// region ReducedGroupBy

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with one row per group of this [<code>ReducedGroupBy</code>][ReducedGroupBy]:
 * the grouping key columns and a new column with the value of [<code>expression</code>][expression]
 * computed on the reduced row of the group.
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
 * [<code>expression</code>][expression] is a [<code>RowExpression</code>][RowExpression] computed only on the single reduced row of each group;
 * this row is both the receiver (`this`) and the argument (`it`) of the expression.
 * See [<code>Row Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow]
 *
 * For more information: [See "`GroupBy` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#reducing)
 *
 * Don't confuse this with [<code>into</code>][GroupBy.into] on a [<code>GroupBy</code>][GroupBy], which computes the expression on all rows
 * of each group and collects the results into lists.
 *
 * See also:
 * - [<code>into</code>][ReducedGroupBy.into]`(columnName)` — puts the whole reduced rows into a new column group.
 * - [<code>values</code>][ReducedGroupBy.values] — takes the values of the selected columns of the reduced rows.
 * - [<code>concat</code>][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * ```kotlin
 * df.groupBy { isHappy }.minBy { age }.into("youngest") { name }
 * ```
 *
 * ```kotlin
 * df.groupBy("isHappy").minBy("age").into("youngest") { getColumnGroup("name") }
 * ```
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [<code>GroupBy</code>][GroupBy].
 * @param [G] The schema marker type of the groups.
 * @param [V] The type of the values computed by [<code>expression</code>][expression].
 * @param [columnName] The name of the new column. If `null` (the default),
 *   the name of the [<code>groups</code>][GroupBy.groups] column of the underlying [<code>GroupBy</code>][GroupBy] is used.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] computing the value of the new column from the reduced row.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns and the column with the computed values.
 */
public inline fun <T, G, reified V> ReducedGroupBy<T, G>.into(
    columnName: String? = null,
    noinline expression: RowExpression<G, V>,
): DataFrame<G> {
    val type = typeOf<V>()
    val name = columnName ?: groupBy.groups.name()
    return groupBy.aggregate {
        val row = reducer(it, it)
        if (row != null) {
            internal().yield(pathOf(name), expression(row, row), type)
        }
    }
}

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified V> ReducedGroupBy<T, G>.into(
    column: ColumnAccessor<V>,
    noinline expression: RowExpression<G, V>,
): DataFrame<G> = into(column.name(), expression)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, G, reified V> ReducedGroupBy<T, G>.into(
    column: KProperty<V>,
    noinline expression: RowExpression<G, V>,
): DataFrame<G> = into(column.columnName, expression)

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with one row per group of this [<code>ReducedGroupBy</code>][ReducedGroupBy]:
 * the grouping key columns and a new
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] named [<code>columnName</code>][columnName]
 * containing the reduced row of the group.
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
 * Don't confuse this with [<code>into</code>][GroupBy.into] on a [<code>GroupBy</code>][GroupBy], which puts the whole groups
 * into a [<code>FrameColumn</code>][FrameColumn].
 *
 * See also:
 * - [<code>into</code>][ReducedGroupBy.into]` { expression }` — puts a value computed from each reduced row into a new column.
 * - [<code>values</code>][ReducedGroupBy.values] — takes the values of the selected columns of the reduced rows.
 * - [<code>concat</code>][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * ```kotlin
 * // For each `isHappy` value, the whole row of the youngest person in the "youngest" column group
 * df.groupBy { isHappy }.minBy { age }.into("youngest")
 * ```
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [<code>GroupBy</code>][GroupBy].
 * @param [G] The schema marker type of the groups.
 * @param [columnName] The name of the new column group.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns and the column group with the reduced rows.
 */
@Refine
@Interpretable("GroupByReduceInto")
public fun <T, G> ReducedGroupBy<T, G>.into(columnName: String): DataFrame<G> = into(columnName) { this }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.into(column: ColumnAccessor<DataRow<*>>): DataFrame<G> = into(column) { this }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.into(column: KProperty<DataRow<*>>): DataFrame<G> = into(column) { this }

// endregion
