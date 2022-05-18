package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import kotlin.reflect.KProperty

public fun <T> DataFrame<T>.insert(column: AnyCol): InsertClause<T> = InsertClause(this, column)

public inline fun <T, reified R> DataFrame<T>.insert(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<T, R>
): InsertClause<T> = insert(mapToColumn(name, infer, expression))

public inline fun <T, reified R> DataFrame<T>.insert(
    column: ColumnAccessor<R>,
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<T, R>
): InsertClause<T> = insert(column.name(), infer, expression)

public inline fun <T, reified R> DataFrame<T>.insert(
    column: KProperty<R>,
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<T, R>
): InsertClause<T> = insert(column.columnName, infer, expression)

public data class InsertClause<T>(val df: DataFrame<T>, val column: AnyCol)

public fun <T> InsertClause<T>.under(column: ColumnSelector<T, *>): DataFrame<T> = under(df.getColumnPath(column))
public fun <T> InsertClause<T>.under(columnPath: ColumnPath): DataFrame<T> = df.insertImpl(columnPath + column.name, column)
public fun <T> InsertClause<T>.under(column: ColumnAccessor<*>): DataFrame<T> = under(column.path())
public fun <T> InsertClause<T>.under(column: KProperty<*>): DataFrame<T> = under(column.columnName)
public fun <T> InsertClause<T>.under(column: String): DataFrame<T> = under(pathOf(column))

public fun <T> InsertClause<T>.after(column: ColumnSelector<T, *>): DataFrame<T> = after(df.getColumnPath(column))
public fun <T> InsertClause<T>.after(column: String): DataFrame<T> = df.add(this.column).move(this.column).after(column)
public fun <T> InsertClause<T>.after(column: ColumnAccessor<*>): DataFrame<T> = after(column.path())
public fun <T> InsertClause<T>.after(column: KProperty<*>): DataFrame<T> = after(column.columnName)
public fun <T> InsertClause<T>.after(columnPath: ColumnPath): DataFrame<T> {
    val dstPath = ColumnPath(columnPath.removeAt(columnPath.size - 1) + column.name())
    return df.insertImpl(dstPath, column).move { dstPath }.after { columnPath }
}

public fun <T> InsertClause<T>.at(position: Int): DataFrame<T> = df.add(column).move(column).to(position)
