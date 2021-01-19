package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.*
import org.jetbrains.dataframe.impl.DataFrameReceiver
import org.jetbrains.dataframe.impl.DataRowImpl
import org.jetbrains.dataframe.impl.EmptyDataFrame
import org.jetbrains.dataframe.impl.getOrPut
import org.jetbrains.dataframe.impl.topDfs
import kotlin.reflect.KProperty

internal open class SelectReceiverImpl<T>(df: DataFrameBase<T>, allowMissingColumns: Boolean) : DataFrameReceiver<T>(df, allowMissingColumns), SelectReceiver<T>

data class DataFrameSize(val ncol: Int, val nrow: Int) {
    override fun toString() = "$nrow x $ncol"
}

typealias DataFrameSelector<T, R> = DataFrame<T>.(DataFrame<T>) -> R

typealias ColumnsSelector<T, C> = SelectReceiver<T>.(SelectReceiver<T>) -> ColumnSet<C>

typealias ColumnSelector<T, C> = SelectReceiver<T>.(SelectReceiver<T>) -> ColumnDef<C>

fun <T, C> DataFrame<T>.createSelector(selector: ColumnsSelector<T, C>) = selector

internal fun  List<ColumnWithPath<*>>.allColumnsExcept(columns: Iterable<ColumnWithPath<*>>): List<ColumnWithPath<*>> {
    val fullTree = collectTree(null) { it }
    columns.forEach {
        var node = fullTree.getOrPut(it.path).asNullable()
        node?.dfs()?.forEach { it.data = null }
        while (node != null) {
            node.data = null
            node = node.parent
        }
    }
    val dfs = fullTree.topDfs { it.data != null }
    return dfs.map { it.data!!.addPath(it.pathFromRoot()) }
}

internal fun <T, C> DataFrame<T>.getColumns(skipMissingColumns: Boolean, selector: ColumnsSelector<T, C>): List<ColumnData<C>> = getColumnsWithPaths(if(skipMissingColumns) UnresolvedColumnsPolicy.Skip else UnresolvedColumnsPolicy.Fail, selector).map { it.data }

internal fun <T, C> DataFrame<T>.getColumns(selector: ColumnsSelector<T, C>) = getColumns(false, selector)

internal fun <T, C> DataFrame<T>.getColumnsWithPaths(unresolvedColumnsPolicy: UnresolvedColumnsPolicy, selector: ColumnsSelector<T, C>): List<ColumnWithPath<C>> = selector.toColumns().resolve(ColumnResolutionContext(this, unresolvedColumnsPolicy))

fun <T, C> DataFrame<T>.getColumnsWithPaths(selector: ColumnsSelector<T, C>): List<ColumnWithPath<C>> = getColumnsWithPaths(UnresolvedColumnsPolicy.Fail, selector)

internal fun <T, C> DataFrame<T>.getColumnPaths(selector: ColumnsSelector<T, C>): List<ColumnPath> = selector.toColumns().resolve(ColumnResolutionContext(this, UnresolvedColumnsPolicy.Fail)).map { it.path }

internal fun <T, C> DataFrame<T>.getGroupColumns(selector: ColumnsSelector<T, DataRow<C>>) = getColumnsWithPaths(selector).map { it.data.asGrouped() }

fun <T, C> DataFrame<T>.column(selector: ColumnSelector<T, C>) = getColumns(selector).single()

fun <T, C> DataFrame<T>.getColumnWithPath(selector: ColumnSelector<T, C>) = getColumnsWithPaths(selector).single()

@JvmName("getColumnForSpread")
internal fun <T, C> DataFrame<T>.getColumn(selector: SpreadColumnSelector<T, C>) = DataFrameForSpreadImpl(this).let { selector(it, it) }.let {
    this[it]
}

internal fun <T>  DataFrame<T>.getColumns(columnNames: Array<out String>) = columnNames.map { this[it] }

internal fun <T, C> DataFrame<T>.getColumns(columnNames: Array<out KProperty<C>>) = columnNames.map { this[it.name] as ColumnDef<C> }

internal fun <T>  DataFrame<T>.getColumns(columnNames: List<String>): List<DataCol> = columnNames.map { this[it] }

internal fun <T>  DataFrame<T>.new(columns: Iterable<DataCol>) = dataFrameOf(columns).typed<T>()

interface DataFrame<out T> : DataFrameBase<T> {

    companion object {
        fun <T> empty(nrow: Int = 0): DataFrame<T> = EmptyDataFrame(nrow)
    }

    fun nrow(): Int
    override fun ncol(): Int = columns().size

    fun rows() : Iterable<DataRow<T>>
    fun columnNames() = columns().map { it.name() }

    override fun columns(): List<DataCol>
    override fun column(columnIndex: Int) = columns()[columnIndex]

    operator fun set(columnName: String, value: DataCol)

    override operator fun get(index: Int): DataRow<T> = DataRowImpl(index, this)
    override operator fun get(columnName: String) = tryGetColumn(columnName) ?: throw Exception("Column not found: '$columnName'")
    override operator fun <R> get(column: ColumnDef<R>): ColumnData<R> = tryGetColumn(column)!!
    override operator fun <R> get(column: ColumnDef<DataRow<R>>): GroupedColumn<R> = get<DataRow<R>>(column) as GroupedColumn<R>
    override operator fun <R> get(column: ColumnDef<DataFrame<R>>): TableColumn<R> = get<DataFrame<R>>(column) as TableColumn<R>

    operator fun get(indices: Iterable<Int>) = getRows(indices)
    operator fun get(mask: BooleanArray) = getRows(mask)
    operator fun get(range: IntRange) = getRows(range)

    operator fun plus(col: DataCol) = dataFrameOf(columns() + col).typed<T>()
    operator fun plus(col: Iterable<DataCol>) = new(columns() + col)
    operator fun plus(stub: AddRowNumberStub) = addRowNumber(stub.columnName)

    fun getRows(indices: Iterable<Int>) = columns().map { col -> col.slice(indices) }.asDataFrame<T>()
    fun getRows(mask: BooleanArray) = columns().map { col -> col.slice(mask) }.asDataFrame<T>()
    fun getRows(range: IntRange) = columns().map { col -> col.slice(range) }.asDataFrame<T>()

    fun getColumnIndex(name: String): Int
    fun getColumnIndex(col: DataCol) = getColumnIndex(col.name())

    fun <R> tryGetColumn(column: ColumnDef<R>): ColumnData<R>? = tryGetColumn(column.name()) as? ColumnData<R>

    override fun tryGetColumn(name: String): DataCol? = getColumnIndex(name).let { if (it != -1) column(it) else null }

    fun tryGetColumnGroup(name: String) = tryGetColumn(name) as? GroupedColumn<*>
    fun getColumnGroup(name: String) = tryGetColumnGroup(name)!!

    operator fun get(col1: Column, col2: Column, vararg other: Column) = select(listOf(col1, col2) + other)
    operator fun get(col1: String, col2: String, vararg other: String) = select(getColumns(listOf(col1, col2) + other))

    fun addRow(vararg values: Any?): DataFrame<T>

    fun all(predicate: RowFilter<T>): Boolean = rows().all { predicate(it, it) }
    fun any(predicate: RowFilter<T>): Boolean = rows().any { predicate(it, it) }

    fun first() = rows().first()
    fun firstOrNull() = rows().firstOrNull()
    fun last() = rows().last() // TODO: optimize (don't iterate through the whole data frame)
    fun lastOrNull() = rows().lastOrNull()
    fun take(numRows: Int) = getRows(0 until numRows)
    fun drop(numRows: Int) = getRows(numRows until nrow())
    fun takeLast(numRows: Int) = getRows(nrow() - numRows until nrow())
    fun skipLast(numRows: Int) = getRows(0 until nrow() - numRows)
    fun head(numRows: Int = 5) = take(numRows)
    fun tail(numRows: Int = 5) = takeLast(numRows)
    fun shuffled() = getRows((0 until nrow()).shuffled())
    fun <K, V> associate(transform: RowSelector<T, Pair<K, V>>) = rows().associate { transform(it, it) }
    fun <V> associateBy(transform: RowSelector<T, V>) = rows().associateBy { transform(it, it) }
    fun <R> distinctBy(selector: RowSelector<T, R>) = rows().distinctBy { selector(it, it) }.map { it.index }.let { getRows(it) }
    fun distinct() = distinctBy { it.values }
    fun single() = rows().single()
    fun single(predicate: RowSelector<T, Boolean>) = rows().single { predicate(it, it) }

    fun <R> map(selector: RowSelector<T, R>) = rows().map { selector(it, it) }

    fun <R> mapIndexed(action: (Int, DataRow<T>) -> R) = rows().mapIndexed(action)

    fun size() = DataFrameSize(ncol(), nrow())
}

fun <T> DataFrame<*>.typed(): DataFrame<T> = this as DataFrame<T>

fun <T> DataFrameBase<*>.typed(): DataFrameBase<T> = this as DataFrameBase<T>

fun <T> DataRow<T>.toDataFrame(): DataFrame<T> = owner[index..index]

fun <T, C> DataFrame<T>.forEachIn(selector: ColumnsSelector<T, C>, action: (DataRow<T>, ColumnData<C>) -> Unit) = getColumnsWithPaths(selector).let { cols ->
    rows().forEach { row ->
        cols.forEach { col ->
            action(row, col.data)
        }
    }
}


