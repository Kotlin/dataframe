package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException
import org.jetbrains.kotlinx.dataframe.impl.aggregation.AggregatableInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupByReceiverImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.resolveSingle
import org.jetbrains.kotlinx.dataframe.io.renderToString

private const val unnamedColumnPrefix = "untitled"

internal open class DataFrameImpl<T>(cols: List<AnyCol>, val nrow: Int) : DataFrame<T>, AggregatableInternal<T> {

    private val columnsMap: Map<String, Int>

    protected val columns: List<AnyCol>

    init {
        columnsMap = mutableMapOf()

        // check that column sizes are equal
        if (cols.any { it.size != nrow }) {
            throw UnequalColumnSizesException(nrow, cols.map { it.name to it.size })
        }

        // check that column names are unique
        var hasUnnamedColumns = false
        cols.forEachIndexed { i, col ->
            val name = col.name
            if (name.isEmpty()) hasUnnamedColumns = true
            else {
                if (columnsMap.containsKey(name)) {
                    throw DuplicateColumnNamesException(cols.map { it.name })
                }
                columnsMap[name] = i
            }
        }

        // generate unique names for unnamed columns
        if (hasUnnamedColumns) {
            val nameGenerator = ColumnNameGenerator(cols.map { it.name })
            columns = cols.mapIndexed { i, col ->
                val name = col.name
                if (name.isEmpty()) {
                    val uniqueName = nameGenerator.addUnique(unnamedColumnPrefix)
                    val renamed = col.rename(uniqueName)
                    columnsMap[uniqueName] = i
                    renamed
                } else col
            }
        } else columns = cols
    }

    override fun rowsCount() = nrow

    override fun getColumnIndex(name: String) = columnsMap[name] ?: -1

    override fun equals(other: Any?): Boolean {
        val df = other as? AnyFrame ?: return false
        return columns == df.columns()
    }

    override fun hashCode() = columns.hashCode()

    override fun toString() = renderToString()

    override fun columns() = columns

    override fun columnNames() = columns.map { it.name() }

    override fun columnTypes() = columns.map { it.type() }

    override fun columnsCount(): Int = columns.size

    override operator fun get(index: Int): DataRow<T> {
        if (index < 0 || index >= nrow) {
            throw IndexOutOfBoundsException("index: $index, rowsCount: $nrow")
        }
        return DataRowImpl(index, this)
    }

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> = { all() }

    override fun <R> aggregate(body: AggregateGroupedBody<T, R>): DataRow<T> {
        val receiver = GroupByReceiverImpl(this, false)
        body(receiver, receiver)
        val row = receiver.compute() ?: DataFrame.empty(1)[0]
        return row.cast()
    }

    override fun getColumnOrNull(name: String): AnyCol? =
        getColumnIndex(name).let { if (it != -1) getColumn(it) else null }

    override fun getColumnOrNull(index: Int) = if (index >= 0 && index < columns.size) columns[index] else null

    override fun <R> getColumnOrNull(column: ColumnSelector<T, R>): DataColumn<R>? = getColumnsImpl(
        UnresolvedColumnsPolicy.Skip, column
    ).singleOrNull()

    override fun <R> getColumnOrNull(column: ColumnReference<R>): DataColumn<R>? = column.resolveSingle(this, UnresolvedColumnsPolicy.Skip)?.data

    override fun getColumnOrNull(path: ColumnPath): AnyCol? =
        when (path.size) {
            0 -> asColumnGroup().asDataColumn()
            1 -> getColumnOrNull(path[0])
            else -> path.dropLast(1).fold(this as AnyFrame?) { df, name -> df?.getColumnOrNull(name) as? AnyFrame? }
                ?.getColumnOrNull(path.last())
        }

    override fun containsColumn(name: String): Boolean = columnsMap.containsKey(name)

    override fun containsColumn(path: ColumnPath): Boolean = getColumnOrNull(path) != null
}

internal fun <T, C> DataFrame<T>.valuesImpl(byRow: Boolean, columns: ColumnsSelector<T, C>): Sequence<C> {
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
