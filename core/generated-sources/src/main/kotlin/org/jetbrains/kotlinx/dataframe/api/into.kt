package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.withExpr
import org.jetbrains.kotlinx.dataframe.impl.columnName
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region GroupBy

public fun <T, G> GroupBy<T, G>.into(column: String): DataFrame<T> = toDataFrame(column)
public fun <T> GroupBy<T, *>.into(column: ColumnAccessor<AnyFrame>): DataFrame<T> = toDataFrame(column.name())
public fun <T> GroupBy<T, *>.into(column: KProperty<AnyFrame>): DataFrame<T> = toDataFrame(column.columnName)

public inline fun <T, G, reified V> GroupBy<T, G>.into(
    columnName: String? = null,
    noinline expression: RowExpression<G, V>
): DataFrame<G> = into(pathOf(columnName ?: groups.name()).cast(), expression)
public inline fun <T, G, reified V> GroupBy<T, G>.into(
    column: ColumnAccessor<V>,
    noinline expression: RowExpression<G, V>
): DataFrame<G> {
    val type = typeOf<V>()
    val path = column.path()
    return aggregate {
        internal().withExpr(type, path, expression)
    }
}
public inline fun <T, G, reified V> GroupBy<T, G>.into(column: KProperty<V>, noinline expression: RowExpression<G, V>): DataFrame<G> = into(column.columnName, expression)

// endregion

// region ReducedGroupBy

public inline fun <T, G, reified V> ReducedGroupBy<T, G>.into(
    columnName: String? = null,
    noinline expression: RowExpression<G, V>
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
public inline fun <T, G, reified V> ReducedGroupBy<T, G>.into(
    column: ColumnAccessor<V>,
    noinline expression: RowExpression<G, V>
): DataFrame<G> = into(column.name(), expression)
public inline fun <T, G, reified V> ReducedGroupBy<T, G>.into(
    column: KProperty<V>,
    noinline expression: RowExpression<G, V>
): DataFrame<G> = into(column.columnName, expression)

public fun <T, G> ReducedGroupBy<T, G>.into(columnName: String): DataFrame<G> = into(columnName) { this }
public fun <T, G> ReducedGroupBy<T, G>.into(column: ColumnAccessor<AnyRow>): DataFrame<G> = into(column) { this }
public fun <T, G> ReducedGroupBy<T, G>.into(column: KProperty<AnyRow>): DataFrame<G> = into(column) { this }

public fun <T, G> ReducedGroupBy<T, G>.concat(): DataFrame<G> = groupBy.groups.values().map { reducer(it, it) }.concat()

// endregion
