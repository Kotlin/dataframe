package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameBase
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowSelector
import org.jetbrains.kotlinx.dataframe.aggregation.GroupByAggregateBody
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.impl.aggregation.AggregatableInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupByReceiverImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateBodyInternal
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.typed

internal open class DataFrameImpl<T>(var columns: List<AnyCol>) : DataFrame<T>, AggregatableInternal<T> {

    private val nrow: Int = columns.firstOrNull()?.size ?: 0

    override fun nrow() = nrow

    private val columnsMap: MutableMap<String, Int>

    init {

        val invalidSizeColumns = columns.filter { it.size != nrow() }
        require(invalidSizeColumns.isEmpty()) { "Unequal column sizes:\n${columns.joinToString("\n") { it.name + " (" + it.size + ")" }}" }

        columnsMap = mutableMapOf()
        columns.forEachIndexed { i, col ->
            val name = col.name
            if (columnsMap.containsKey(name)) {
                if (name != "") {
                    val names = columns.groupBy { it.name }.filter { it.key != "" && it.value.size > 1 }.map { it.key }
                    throw IllegalArgumentException("Duplicate column names: $names. All columns: ${columnNames()}")
                }
            } else columnsMap[name] = i
        }
    }

    override fun getColumnIndex(columnName: String) = columnsMap[columnName] ?: -1

    override fun equals(other: Any?): Boolean {
        val df = other as? AnyFrame ?: return false
        return columns == df.columns()
    }

    override fun hashCode() = columns.hashCode()

    override fun toString() = renderToString()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataRow<T>>? {
        return DataColumn.create("", this).addPath(emptyPath(), this)
    }

    override fun set(columnName: String, value: AnyCol) {
        require(value.size == nrow()) { "Invalid column size for column '$columnName'. Expected: ${nrow()}, actual: ${value.size}" }

        val renamed = value.rename(columnName)
        val index = getColumnIndex(columnName)
        val newCols = if (index == -1) columns + renamed else columns.mapIndexed { i, col -> if (i == index) renamed else col }
        columnsMap[columnName] = if (index == -1) ncol() else index
        columns = newCols
    }

    override fun columns() = columns

    override fun <R> aggregateInternal(body: AggregateBodyInternal<T, R>) = aggregate(body as GroupByAggregateBody<T, R>).df()

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> = { all() }

    override fun <C> values(byRow: Boolean, columns: ColumnsSelector<T, C>): Sequence<C> {
        val cols = get(columns)
        return if (byRow) sequence {
            indices().forEach { row ->
                cols.forEach {
                    yield(it[row])
                }
            }
        }
        else sequence {
            cols.forEach { col ->
                col.values().forEach {
                    yield(it)
                }
            }
        }
    }

    override fun <R> aggregate(body: GroupByAggregateBody<T, R>): DataRow<T> {
        val receiver = GroupByReceiverImpl(this)
        body(receiver, receiver)
        val row = receiver.compute() ?: DataFrame.empty(1)[0]
        return row.typed()
    }

    override fun rows(): Iterable<DataRow<T>> = object : Iterable<DataRow<T>> {
        override fun iterator() =

            object : Iterator<DataRow<T>> {
                var nextRow = 0

                override fun hasNext(): Boolean = nextRow < nrow

                override fun next(): DataRow<T> {
                    require(nextRow < nrow)
                    return get(nextRow++)
                }
            }
    }

    override fun rowsReversed(): Iterable<DataRow<T>> = object : Iterable<DataRow<T>> {
        override fun iterator() =

            object : Iterator<DataRow<T>> {
                var nextRow = nrow - 1

                override fun hasNext(): Boolean = nextRow >= 0

                override fun next(): DataRow<T> {
                    require(nextRow >= 0)
                    return get(nextRow--)
                }
            }
    }
}

@PublishedApi
internal fun <T, R> DataFrameBase<T>.mapRows(selector: RowSelector<T, R>): Sequence<R> = rows().asSequence().map { selector(it, it) }
