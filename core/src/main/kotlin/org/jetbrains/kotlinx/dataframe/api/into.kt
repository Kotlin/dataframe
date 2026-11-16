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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.RowExpressionsLink
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.withExpr
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region GroupBy

/**
 * Converts this [GroupBy] into a [DataFrame].
 *
 * Does the same as [GroupBy.toDataFrame].
 *
 * Each row of the resulting [DataFrame] represents a unique keys–group pair:
 * a row from [keys] and its corresponding group of rows (as [DataFrame]).
 *
 * Resulting [DataFrame] contains a [FrameColumn] with [groups].
 *
 * Check out [groupBy Grammar][GroupByDocs.Grammar].
 *
 * For more information: {@include [DocumentationUrls.GroupBy]}
 *
 * @param column The name of the column in which to store grouped data.
 * @return A new [DataFrame] that includes the grouping key columns together
 * with a [FrameColumn] containing the corresponding groups.
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
 * Aggregates this [GroupBy] into a [DataFrame]
 * by computing the values of [expression] on all rows for each group.
 *
 * Each row of the resulting [DataFrame] represents a unique keys–value pair:
 * a row from [keys] and list of values, computed on all rows of the corresponding group using
 * provided [expression].
 *
 * Check out [groupBy Grammar][GroupByDocs.Grammar].
 *
 * See {@include [RowExpressionsLink]}
 *
 * For more information: {@include [DocumentationUrls.GroupBy]}
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
 * @return A new [DataFrame] that includes the grouping key columns together
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
 * Returns a [DataFrame] with one row per group of this [ReducedGroupBy]:
 * the grouping key columns and a new column with the value of [expression]
 * computed on the reduced row of the group.
 *
 * @include [GroupByDocs.ReducedGroupByFinalStepSnippet]
 *
 * @include [GroupByDocs.ReducedGroupByNoSelectedRowSnippet]
 *
 * [expression] is a [RowExpression] computed only on the single reduced row of each group;
 * this row is both the receiver (`this`) and the argument (`it`) of the expression.
 * See {@include [RowExpressionsLink]}
 *
 * For more information: {@include [DocumentationUrls.GroupByReducing]}
 *
 * Don't confuse this with [into][GroupBy.into] on a [GroupBy], which computes the expression on all rows
 * of each group and collects the results into lists.
 *
 * See also:
 * - [into][ReducedGroupBy.into]`(columnName)` — puts the whole reduced rows into a new column group.
 * - [values][ReducedGroupBy.values] — takes the values of the selected columns of the reduced rows.
 * - [concat][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.groupByInto_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.groupByInto_strings]
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [GroupBy].
 * @param [G] The schema marker type of the groups.
 * @param [V] The type of the values computed by [expression].
 * @param [columnName] The name of the new column. If `null` (the default),
 *   the name of the [groups][GroupBy.groups] column of the underlying [GroupBy] is used.
 * @param [expression] The [RowExpression] computing the value of the new column from the reduced row.
 * @return A new [DataFrame] with the grouping key columns and the column with the computed values.
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
 * Returns a [DataFrame] with one row per group of this [ReducedGroupBy]:
 * the grouping key columns and a new
 * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] named [columnName]
 * containing the reduced row of the group.
 *
 * @include [GroupByDocs.ReducedGroupByFinalStepSnippet]
 *
 * @include [GroupByDocs.ReducedGroupByNoSelectedRowSnippet]
 *
 * For more information: {@include [DocumentationUrls.GroupByReducing]}
 *
 * Don't confuse this with [into][GroupBy.into] on a [GroupBy], which puts the whole groups
 * into a [FrameColumn].
 *
 * See also:
 * - [into][ReducedGroupBy.into]` { expression }` — puts a value computed from each reduced row into a new column.
 * - [values][ReducedGroupBy.values] — takes the values of the selected columns of the reduced rows.
 * - [concat][ReducedGroupBy.concat] — returns the reduced rows themselves.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.groupByIntoColumnGroup]
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [GroupBy].
 * @param [G] The schema marker type of the groups.
 * @param [columnName] The name of the new column group.
 * @return A new [DataFrame] with the grouping key columns and the column group with the reduced rows.
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
