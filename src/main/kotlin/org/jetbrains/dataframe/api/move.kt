package org.jetbrains.dataframe

import org.jetbrains.dataframe.impl.TreeNode
import kotlin.reflect.KProperty

fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, cols: ColumnsSelector<T, *>) = moveTo(newColumnIndex, getColumns(cols) as Iterable<Column>)
fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: String) = moveTo(newColumnIndex, getColumns(cols))
fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: KProperty<*>) = moveTo(newColumnIndex, getColumns(cols))
fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: Column) = moveTo(newColumnIndex, cols.asIterable())
fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, cols: Iterable<Column>): DataFrame<T> {
    val columnsToMove = cols.map { this[it] }
    val otherColumns = columns - columnsToMove
    val newColumnList = otherColumns.subList(0, newColumnIndex) + columnsToMove + otherColumns.subList(newColumnIndex, otherColumns.size)
    return dataFrameOf(newColumnList).typed()
}

fun <T> DataFrame<T>.moveToLeft(cols: ColumnsSelector<T, *>) = moveToLeft(getColumns(cols))
fun <T> DataFrame<T>.moveToLeft(cols: Iterable<Column>) = moveTo(0, cols)
fun <T> DataFrame<T>.moveToLeft(vararg cols: String) = moveToLeft(getColumns(cols))
fun <T> DataFrame<T>.moveToLeft(vararg cols: Column) = moveToLeft(cols.asIterable())
fun <T> DataFrame<T>.moveToLeft(vararg cols: KProperty<*>) = moveToLeft(getColumns(cols))
fun <T> DataFrame<T>.moveToRight(cols: Iterable<Column>) = moveTo(ncol - cols.count(), cols)
fun <T> DataFrame<T>.moveToRight(cols: ColumnsSelector<T, *>) = moveToRight(getColumns(cols))
fun <T> DataFrame<T>.moveToRight(vararg cols: String) = moveToRight(getColumns(cols))
fun <T> DataFrame<T>.moveToRight(vararg cols: Column) = moveToRight(cols.asIterable())
fun <T> DataFrame<T>.moveToRight(vararg cols: KProperty<*>) = moveToRight(getColumns(cols))
fun <T, C> DataFrame<T>.move(selector: ColumnsSelector<T, C>): MoveColsClause<T, C> {

    val (df, removed) = doRemove(getColumns(selector))
    return MoveColsClause(df, removed)
}

interface DataFrameForMove<T> : DataFrameBase<T> {

    fun path(vararg columns: String): List<String> = listOf(*columns)

    fun SingleColumn<*>.addPath(vararg columns: String): List<String> = (this as Column).getPath() + listOf(*columns)

    operator fun SingleColumn<*>.plus(column: String) = addPath(column)
}

class MoveReceiver<T>(df: DataFrame<T>) : DataFrameForMove<T>, DataFrameBase<T> by df

fun <T, C> MoveColsClause<T, C>.intoGroup(groupPath: DataFrameForMove<T>.(DataFrameForMove<T>) -> List<String>): DataFrame<T> {
    val receiver = MoveReceiver(df)
    val path = groupPath(receiver, receiver)
    val columnsToInsert = removedColumns().map { ColumnToInsert(path + it.name, it, it.data.column!!) }
    return df.doInsert(columnsToInsert)
}

fun <T, C> MoveColsClause<T, C>.intoGroups(groupName: DataFrameForMove<T>.(ColumnWithPath<C>) -> String): DataFrame<T> {
    val receiver = MoveReceiver(df)
    val columnsToInsert = removedColumns().map {
        val col = it.column
        ColumnToInsert(listOf(groupName(receiver, col), it.name), it, col)
    }
    return df.doInsert(columnsToInsert)
}

fun <T, C> MoveColsClause<T, C>.toTop(groupNameExpression: DataFrameForMove<T>.(ColumnWithPath<C>) -> String = { it.name }) = into { listOf(groupNameExpression(it)) }
fun <T, C> MoveColsClause<T, C>.into(groupNameExpression: DataFrameForMove<T>.(ColumnWithPath<C>) -> List<String>): DataFrame<T> {

    val receiver = MoveReceiver(df)
    val columnsToInsert = removedColumns().map {
        val col = it.column
        ColumnToInsert(groupNameExpression(receiver, col), it, col)
    }
    return df.doInsert(columnsToInsert)
}

fun <T, C> MoveColsClause<T, C>.into(name: String) = intoGroup { listOf(name) }
fun <T, C> MoveColsClause<T, C>.into(path: List<String>) = intoGroup { path }
fun <T, C> MoveColsClause<T, C>.to(columnIndex: Int): DataFrame<T> {
    val newColumnList = df.columns.subList(0, columnIndex) + removedColumns().map { it.data.column as ColumnData<C> } + df.columns.subList(columnIndex, df.ncol)
    return newColumnList.asDataFrame()
}

fun <T, C> MoveColsClause<T, C>.toLeft() = to(0)
fun <T, C> MoveColsClause<T, C>.toRight() = to(df.ncol)
class MoveColsClause<T, C> internal constructor(internal val df: DataFrame<T>, internal val removed: TreeNode<ColumnPosition>) {

    internal fun removedColumns() = removed.allRemovedColumns()

    internal val TreeNode<ColumnPosition>.column get() = column<C>()
}