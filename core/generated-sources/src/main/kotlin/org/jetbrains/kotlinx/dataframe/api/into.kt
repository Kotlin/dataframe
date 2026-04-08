package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
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
 * Do the same as [GroupBy.toDataFrame].
 *
 * Each row of the resulting [DataFrame] represents a unique keys–group pair:
 * a row from [keys] and its corresponding group of rows (as [DataFrame]).
 *
 * Resulting [DataFrame] contains a [FrameColumn] with [groups].
 *
 * Check out [groupBy Grammar][GroupByDocs.Grammar].
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
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
public fun <T> GroupBy<T, *>.into(column: ColumnAccessor<AnyFrame>): DataFrame<T> = toDataFrame(column.name())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> GroupBy<T, *>.into(column: KProperty<AnyFrame>): DataFrame<T> = toDataFrame(column.columnName)

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
 * See [Row Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow]
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * @param [columnName] The name of the column in which to store aggregated data.
 * @param [expression] The expression to compute on each group row.
 * @return A new [DataFrame] that includes the grouping key columns together
 * with a valuess computed on all rows of correspodning groups.
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

@Refine
@Interpretable("GroupByReduceInto")
public fun <T, G> ReducedGroupBy<T, G>.into(columnName: String): DataFrame<G> = into(columnName) { this }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.into(column: ColumnAccessor<AnyRow>): DataFrame<G> = into(column) { this }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> ReducedGroupBy<T, G>.into(column: KProperty<AnyRow>): DataFrame<G> = into(column) { this }

// endregion
