package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.GroupByAggregateBody
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.DataFrameSize
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.EmptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.toIndices

internal fun <T, C> DataFrame<T>.getColumns(
    skipMissingColumns: Boolean,
    selector: ColumnsSelector<T, C>
): List<DataColumn<C>> = getColumnsWithPaths(
    if (skipMissingColumns) UnresolvedColumnsPolicy.Skip else UnresolvedColumnsPolicy.Fail,
    selector
).map { it.data }

internal fun <T, C> DataFrame<T>.getColumnsWithPaths(
    unresolvedColumnsPolicy: UnresolvedColumnsPolicy,
    selector: ColumnsSelector<T, C>
): List<ColumnWithPath<C>> = selector.toColumns().resolve(this, unresolvedColumnsPolicy)

public fun <T, C> DataFrame<T>.getColumnsWithPaths(selector: ColumnsSelector<T, C>): List<ColumnWithPath<C>> =
    getColumnsWithPaths(UnresolvedColumnsPolicy.Fail, selector)

public fun <T, C> DataFrame<T>.getColumnPath(selector: ColumnSelector<T, C>): ColumnPath = getColumnPaths(selector).single()

public fun <T, C> DataFrame<T>.getColumnPaths(selector: ColumnsSelector<T, C>): List<ColumnPath> =
    selector.toColumns().resolve(this, UnresolvedColumnsPolicy.Fail).map { it.path }

public fun <T, C> DataFrame<T>.column(selector: ColumnSelector<T, C>): DataColumn<C> = get(selector)

public fun <T> DataFrame<T>.col(predicate: ColumnFilter<Any?>): AnyCol = column { single(predicate) }

public fun <T, C> DataFrame<T>.getColumnWithPath(selector: ColumnSelector<T, C>): ColumnWithPath<C> = getColumnsWithPaths(selector).single()

internal fun <T> DataFrame<T>.getColumns(columnNames: List<String>): List<AnyCol> = columnNames.map { this[it] }

public interface DataFrame<out T> : Aggregatable<T>, DataFrameBase<T> {

    public companion object {
        public fun empty(nrow: Int = 0): AnyFrame = EmptyDataFrame<Any?>(nrow)
    }

    public fun indices(): IntRange = 0 until nrow

    override fun ncol(): Int = columns().size

    public fun <C> values(byRow: Boolean = false, columns: ColumnsSelector<T, C>): Sequence<C>
    public fun values(byRow: Boolean = false): Sequence<Any?> = values(byRow) { all() }

    public fun <C> valuesNotNull(byRow: Boolean = false, columns: ColumnsSelector<T, C?>): Sequence<C> = values(byRow, columns).filterNotNull()
    public fun valuesNotNull(byRow: Boolean = false): Sequence<Any> = valuesNotNull(byRow) { all() }

    override fun columns(): List<AnyCol>
    public fun <C> columns(selector: ColumnsSelector<T, C>): List<DataColumn<C>> = get(selector)

    override fun col(columnIndex: Int): DataColumn<*> = columns()[columnIndex]

    public operator fun set(columnName: String, value: AnyCol)

    override operator fun get(index: Int): DataRow<T> = DataRowImpl(index, this)

    override operator fun get(columnName: String): DataColumn<*> =
        tryGetColumn(columnName) ?: throw Exception("Column not found: '$columnName'")

    override operator fun <R> get(column: ColumnReference<R>): DataColumn<R> = tryGetColumn(column)
        ?: error("Column not found: ${column.path().joinToString("/")}")

    override operator fun <R> get(column: ColumnReference<DataRow<R>>): ColumnGroup<R> =
        get<DataRow<R>>(column) as ColumnGroup<R>

    override operator fun <R> get(column: ColumnReference<DataFrame<R>>): FrameColumn<R> =
        get<DataFrame<R>>(column) as FrameColumn<R>

    override operator fun <C> get(selector: ColumnsSelector<T, C>): List<DataColumn<C>> = getColumns(false, selector)

    public operator fun get(indices: Iterable<Int>): DataFrame<T> = getRows(indices)
    public operator fun get(mask: BooleanArray): DataFrame<T> = getRows(mask)
    public operator fun get(range: IntRange): DataFrame<T> = getRows(range)
    public operator fun get(firstIndex: Int, vararg otherIndices: Int): DataFrame<T> = get(headPlusIterable(firstIndex, otherIndices.asIterable()))

    public operator fun plus(col: AnyCol): DataFrame<T> = (columns() + col).toDataFrame()
    public operator fun plus(cols: Iterable<AnyCol>): DataFrame<T> = (columns() + cols).toDataFrame()
    public operator fun plus(stub: AddRowNumberStub): DataFrame<T> = addRowNumber(stub.columnName)

    public fun getRows(indices: Iterable<Int>): DataFrame<T> = columns().map { col -> col.slice(indices) }.toDataFrame()
    public fun getRows(mask: BooleanArray): DataFrame<T> = getRows(mask.toIndices())
    public fun getRows(range: IntRange): DataFrame<T> = if (range == indices()) this else columns().map { col -> col.slice(range) }.toDataFrame()

    public fun getColumnIndex(name: String): Int
    public fun getColumnIndex(col: AnyCol): Int = getColumnIndex(col.name())

    public fun <R> tryGetColumn(column: ColumnReference<R>): DataColumn<R>? = column.resolveSingle(
        this,
        UnresolvedColumnsPolicy.Skip
    )?.data

    override fun tryGetColumn(columnName: String): AnyCol? =
        getColumnIndex(columnName).let { if (it != -1) col(it) else null }

    override fun tryGetColumn(path: ColumnPath): AnyCol? =
        if (path.size == 1) tryGetColumn(path[0])
        else path.dropLast(1).fold(this as AnyFrame?) { df, name -> df?.tryGetColumn(name) as? AnyFrame? }
            ?.tryGetColumn(path.last())

    public fun tryGetColumnGroup(name: String): ColumnGroup<*>? = tryGetColumn(name) as? ColumnGroup<*>

    public operator fun get(first: Column, vararg other: Column): DataFrame<T> = select(listOf(first) + other)
    public operator fun get(first: String, vararg other: String): DataFrame<T> = select(listOf(first) + other)

    public operator fun iterator(): Iterator<DataRow<T>> = rows().iterator()

    public fun <R> aggregate(body: GroupByAggregateBody<T, R>): DataRow<T>
}

public fun AnyFrame.columnNames(): List<String> = columns().map { it.name() }

public fun AnyFrame.size(): DataFrameSize = DataFrameSize(ncol(), nrow())

public fun AnyFrame.getFrame(path: ColumnPath): AnyFrame = if (path.isNotEmpty()) this[path].asFrame() else this

public fun <T> AnyFrame.typed(): DataFrame<T> = this as DataFrame<T>

public fun <T> AnyRow.typed(): DataRow<T> = this as DataRow<T>

public fun <T> DataFrameBase<*>.typed(): DataFrameBase<T> = this as DataFrameBase<T>

public fun <T> DataRow<T>.toDataFrame(): DataFrame<T> = owner[index..index]

internal val AnyFrame.ncol get() = ncol()
internal val AnyFrame.nrow get() = nrow()
internal val AnyFrame.indices get() = indices()
