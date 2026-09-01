package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.GroupedDataRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.GroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator

internal class GroupedDataRowImpl<T, G>(private val row: DataRow<T>, private val frameCol: FrameColumn<G>) :
    GroupedDataRow<T, G>,
    DataRow<T> by row {

    override fun group() = frameCol[row.index()]
}

@PublishedApi
internal fun <T> DataFrame<T>.groupByImpl(moveToTop: Boolean, columns: ColumnsSelector<T, *>): GroupBy<T, T> {
    val nameGenerator = nameGenerator(GroupBy.groupedColumnAccessor.name())
    var keyColumns = getColumnsWithPaths(columns)
    if (!moveToTop) {
        keyColumns = keyColumns.map {
            val currentName = it.name()
            val uniqueName = nameGenerator.addUnique(currentName)
            if (uniqueName != currentName) {
                it.rename(uniqueName)
            } else {
                it
            }
        }
    }
    /*
     * Step 1 benchmark (see core/GROUP_BY_PERFORMANCE.md): latency ranges from a 1.17x speedup to a 19%
     * regression, while allocations drop by 1.04-1.45x. Unlike the baseline, it also passes the 1M-row
     * constrained-heap case with -Xmx192m.
     */
    val groupMap = LinkedHashMap<List<Any?>, Int>()
    val groups = ArrayList<MutableList<Int>>()
    for (index in 0 until rowsCount()) {
        val key = ArrayList<Any?>(keyColumns.size)
        for (column in keyColumns) key.add(column[index])
        val groupIndex = groupMap.getOrPut(key) {
            groups.add(ArrayList())
            groups.lastIndex
        }
        groups[groupIndex].add(index)
    }

    val keyIndices = List(groups.size) { groups[it][0] }

    val keyColumnsToInsert = keyColumns.map {
        val column = it[keyIndices]
        val path = if (moveToTop) pathOf(it.name()) else it.path()
        ColumnToInsert(path, column, null)
    }

    val keyColumnsDf = dataFrameOf(keyColumnsToInsert).cast<T>()

    val permutation = groups.flatten()
    val sorted = getRows(permutation)

    var lastIndex = 0
    val startIndices = groups.asSequence().map {
        val start = lastIndex
        lastIndex += it.size
        start
    }

    val groupedColumnName = keyColumnsDf.nameGenerator().addUnique(GroupBy.groupedColumnAccessor.name())
    val groupedColumn = sorted.chunkedImpl(startIndices.asIterable(), groupedColumnName)

    val df = keyColumnsDf + groupedColumn
    return GroupByImpl(df, groupedColumn, columns)
}
