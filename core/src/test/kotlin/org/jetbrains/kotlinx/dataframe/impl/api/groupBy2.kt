package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.impl.GroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator

/** Step 2: read grouping keys from the underlying columns instead of delegating through column paths. */
internal fun <T> DataFrame<T>.groupByImpl2(moveToTop: Boolean, columns: ColumnsSelector<T, *>): GroupBy<T, T> {
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

    val keyDataColumns = keyColumns.map { it.data }
    val groupMap = LinkedHashMap<List<Any?>, Int>()
    val groups = ArrayList<MutableList<Int>>()
    for (index in 0 until rowsCount()) {
        val key = ArrayList<Any?>(keyDataColumns.size)
        for (column in keyDataColumns) key.add(column[index])
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
