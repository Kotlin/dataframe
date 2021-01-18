package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.SingleColumn
import org.jetbrains.dataframe.impl.DataFrameReceiver
import org.jetbrains.dataframe.impl.TreeNode
import kotlin.reflect.KProperty

fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, selector: ColumnsSelector<T, *>) = move(selector).to(newColumnIndex)
fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, cols: Iterable<Column>) = moveTo(newColumnIndex) { cols.toColumnSet() }
fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: String) = moveTo(newColumnIndex) { cols.toColumns() }
fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: KProperty<*>) = moveTo(newColumnIndex) { cols.toColumns() }
fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: Column) = moveTo(newColumnIndex) { cols.toColumns() }

fun <T> DataFrame<T>.moveToLeft(selector: ColumnsSelector<T, *>) = move(selector).toLeft()
fun <T> DataFrame<T>.moveToLeft(cols: Iterable<Column>) = moveToLeft { cols.toColumnSet() }
fun <T> DataFrame<T>.moveToLeft(vararg cols: String) = moveToLeft { cols.toColumns() }
fun <T> DataFrame<T>.moveToLeft(vararg cols: Column) = moveToLeft { cols.toColumns() }
fun <T> DataFrame<T>.moveToLeft(vararg cols: KProperty<*>) = moveToLeft { cols.toColumns() }

fun <T> DataFrame<T>.moveToRight(cols: ColumnsSelector<T, *>) = move(cols).toRight()
fun <T> DataFrame<T>.moveToRight(cols: Iterable<Column>) = moveToRight { cols.toColumnSet() }
fun <T> DataFrame<T>.moveToRight(vararg cols: String) = moveToRight { cols.toColumns() }
fun <T> DataFrame<T>.moveToRight(vararg cols: Column) = moveToRight { cols.toColumns() }
fun <T> DataFrame<T>.moveToRight(vararg cols: KProperty<*>) = moveToRight { cols.toColumns() }

fun <T, C> DataFrame<T>.move(selector: ColumnsSelector<T, C>): MoveColsClause<T, C> {

    val (df, removed) = doRemove(selector)
    return MoveColsClause(df, removed)
}

interface DataFrameForMove<T> : DataFrameBase<T> {

    fun path(vararg columns: String): List<String> = listOf(*columns)

    fun SingleColumn<*>.addPath(vararg columns: String): ColumnPath = this.resolveSingle (ColumnResolutionContext(this@DataFrameForMove, UnresolvedColumnsPolicy.Create))!!.path + listOf(*columns)

    operator fun SingleColumn<*>.plus(column: String) = addPath(column)
}

internal class MoveReceiver<T>(df: DataFrame<T>) : DataFrameReceiver<T>(df, false), DataFrameForMove<T>

fun <T, C> MoveColsClause<T, C>.intoGroup(groupPath: DataFrameForMove<T>.(DataFrameForMove<T>) -> List<String>): DataFrame<T> {
    val receiver = MoveReceiver(df)
    val path = groupPath(receiver, receiver)
    val columnsToInsert = removed.map { ColumnToInsert(path + it.name, it, it.data.column!!) }
    return df.doInsert(columnsToInsert)
}

fun <T, C> MoveColsClause<T, C>.intoGroups(groupName: DataFrameForMove<T>.(ColumnWithPath<C>) -> String): DataFrame<T> {
    val receiver = MoveReceiver(df)
    val columnsToInsert = removed.map {
        val col = it.column
        ColumnToInsert(listOf(groupName(receiver, col), it.name), it, col.data)
    }
    return df.doInsert(columnsToInsert)
}

fun <T, C> MoveColsClause<T, C>.toTop(groupNameExpression: DataFrameForMove<T>.(ColumnWithPath<C>) -> String = { it.name() }) = into { listOf(groupNameExpression(it)) }

fun <T, C> MoveColsClause<T, C>.intoIndexed(newPathExpression: DataFrameForMove<T>.(ColumnWithPath<C>, Int) -> ColumnPath): DataFrame<T> {
    var counter = 0
    return into { col ->
        newPathExpression(this, col, counter++)
    }
}

fun <T, C> MoveColsClause<T, C>.into(newPathExpression: DataFrameForMove<T>.(ColumnWithPath<C>) -> ColumnPath): DataFrame<T> {

    val receiver = MoveReceiver(df)
    val columnsToInsert = removed.map {
        val col = it.column
        ColumnToInsert(newPathExpression(receiver, col), it, col.data)
    }
    return df.doInsert(columnsToInsert)
}

fun <T, C> MoveColsClause<T, C>.into(name: String) = into { listOf(name) }
fun <T, C> MoveColsClause<T, C>.intoGroup(name: String) = intoGroup { listOf(name) }
fun <T, C> MoveColsClause<T, C>.intoGroup(groupDef: GroupedColumnDef) = intoGroup(groupDef.name())

fun <T, C> MoveColsClause<T, C>.into(path: List<String>) = intoGroup { path }
fun <T, C> MoveColsClause<T, C>.to(columnIndex: Int): DataFrame<T> {
    val newColumnList = df.columns().subList(0, columnIndex) + removed.map { it.data.column as ColumnData<C> } + df.columns().subList(columnIndex, df.ncol)
    return newColumnList.asDataFrame()
}

fun <T, C> MoveColsClause<T, C>.toLeft() = to(0)
fun <T, C> MoveColsClause<T, C>.toRight() = to(df.ncol)
class MoveColsClause<T, C> internal constructor(internal val df: DataFrame<T>, internal val removed: List<TreeNode<ColumnPosition>>) {

    internal val TreeNode<ColumnPosition>.column get() = column<C>()
}