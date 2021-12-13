package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.api.afterOrBefore
import org.jetbrains.kotlinx.dataframe.impl.api.moveImpl
import org.jetbrains.kotlinx.dataframe.impl.api.moveTo
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.ncol
import kotlin.reflect.KProperty

public fun <T, C> DataFrame<T>.move(columns: ColumnsSelector<T, C>): MoveClause<T, C> = MoveClause(this, columns)
public fun <T> DataFrame<T>.move(vararg cols: String): MoveClause<T, Any?> = move { cols.toColumns() }
public fun <T, C> DataFrame<T>.move(vararg cols: ColumnReference<C>): MoveClause<T, C> = move { cols.toColumns() }
public fun <T, C> DataFrame<T>.move(vararg cols: KProperty<C>): MoveClause<T, C> = move { cols.toColumns() }

public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).to(newColumnIndex)
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg columns: String): DataFrame<T> = moveTo(newColumnIndex) { columns.toColumns() }
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg columns: Column): DataFrame<T> = moveTo(newColumnIndex) { columns.toColumns() }
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg columns: KProperty<*>): DataFrame<T> = moveTo(newColumnIndex) { columns.toColumns() }

public fun <T> DataFrame<T>.moveToLeft(columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).toLeft()
public fun <T> DataFrame<T>.moveToLeft(vararg columns: String): DataFrame<T> = moveToLeft { columns.toColumns() }
public fun <T> DataFrame<T>.moveToLeft(vararg columns: Column): DataFrame<T> = moveToLeft { columns.toColumns() }
public fun <T> DataFrame<T>.moveToLeft(vararg columns: KProperty<*>): DataFrame<T> = moveToLeft { columns.toColumns() }

public fun <T> DataFrame<T>.moveToRight(columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).toRight()
public fun <T> DataFrame<T>.moveToRight(vararg columns: String): DataFrame<T> = moveToRight { columns.toColumns() }
public fun <T> DataFrame<T>.moveToRight(vararg columns: Column): DataFrame<T> = moveToRight { columns.toColumns() }
public fun <T> DataFrame<T>.moveToRight(vararg columns: KProperty<*>): DataFrame<T> = moveToRight { columns.toColumns() }

public fun <T, C> MoveClause<T, C>.into(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> Column): DataFrame<T> = moveImpl(
    under = false,
    column
)

public fun <T, C> MoveClause<T, C>.into(column: String): DataFrame<T> = pathOf(column).let { path -> into { path } }

public fun <T, C> MoveClause<T, C>.intoIndexed(
    newPathExpression: ColumnsSelectionDsl<T>.(ColumnWithPath<C>, Int) -> Column
): DataFrame<T> {
    var counter = 0
    return into { col ->
        newPathExpression(this, col, counter++)
    }
}

public fun <T, C> MoveClause<T, C>.under(column: String): DataFrame<T> = pathOf(column).let { path -> under { path } }
public fun <T, C> MoveClause<T, C>.under(column: ColumnGroupAccessor): DataFrame<T> = column.path().let { path -> under { path } }
public fun <T, C> MoveClause<T, C>.under(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> Column): DataFrame<T> = moveImpl(
    under = true,
    column
)

public fun <T, C> MoveClause<T, C>.to(columnIndex: Int): DataFrame<T> = moveTo(columnIndex)

public fun <T, C> MoveClause<T, C>.toTop(
    newColumnName: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> String = { it.name() }
): DataFrame<T> =
    into { newColumnName(it).toColumnAccessor() }

public fun <T, C> MoveClause<T, C>.after(column: ColumnSelector<T, *>): DataFrame<T> = afterOrBefore(column, true)
public fun <T, C> MoveClause<T, C>.after(column: String): DataFrame<T> = after { column.toColumnAccessor() }
public fun <T, C> MoveClause<T, C>.after(column: Column): DataFrame<T> = after { column }
public fun <T, C> MoveClause<T, C>.after(column: KProperty<*>): DataFrame<T> = after { column.toColumnAccessor() }

// TODO: implement 'before'
/*
fun <T, C> MoveColsClause<T, C>.before(columnPath: ColumnPath) = before { columnPath.toColumnDef() }
fun <T, C> MoveColsClause<T, C>.before(column: Column) = before { column }
fun <T, C> MoveColsClause<T, C>.before(column: KProperty<*>) = before { column.toColumnDef() }
fun <T, C> MoveColsClause<T, C>.before(column: String) = before { column.toColumnDef() }
fun <T, C> MoveColsClause<T, C>.before(column: ColumnSelector<T, *>) = afterOrBefore(column, false)
*/

public fun <T, C> MoveClause<T, C>.toLeft(): DataFrame<T> = to(0)
public fun <T, C> MoveClause<T, C>.toRight(): DataFrame<T> = to(df.ncol)

public class MoveClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>)
