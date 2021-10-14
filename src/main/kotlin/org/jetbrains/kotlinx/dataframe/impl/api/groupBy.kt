package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.dataframe.ColumnToInsert
import org.jetbrains.dataframe.insertColumns
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.impl.GroupedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import org.jetbrains.kotlinx.dataframe.pathOf
import org.jetbrains.kotlinx.dataframe.typed

internal fun <T> DataFrame<T>.groupByImpl(cols: ColumnsSelector<T, *>): GroupedDataFrame<T, T> {
    val nameGenerator = nameGenerator(GroupedDataFrame.groupedColumnAccessor.name())
    val keyColumns = get(cols).map {
        val currentName = it.name()
        val uniqueName = nameGenerator.addUnique(currentName)
        if (uniqueName != currentName) it.rename(uniqueName)
        else it
    }
    val groups = (0 until nrow())
        .map { index -> keyColumns.map { it[index] } to index }
        .groupBy({ it.first }) { it.second }.toList()

    val keyIndices = groups.map { it.second[0] }

    val keyColumnsToInsert = keyColumns.map {
        val column = it.slice(keyIndices)
        val path = pathOf(it.name())
        ColumnToInsert(path, column, null)
    }

    val keyColumnsDf = insertColumns(keyColumnsToInsert).typed<T>()

    val permutation = groups.flatMap { it.second }
    val sorted = getRows(permutation)

    var lastIndex = 0
    val startIndices = groups.asSequence().map {
        val start = lastIndex
        lastIndex += it.second.size
        start
    }

    val groupedColumn = DataColumn.create(GroupedDataFrame.groupedColumnAccessor.name(), sorted, startIndices, false)

    val df = keyColumnsDf + groupedColumn
    return GroupedDataFrameImpl(df, groupedColumn, cols)
}
