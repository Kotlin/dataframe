package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupedDataRow
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.typed
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.GroupedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import org.jetbrains.kotlinx.dataframe.pathOf

internal class GroupedDataRowImpl<T, G>(private val row: DataRow<T>, private val frameCol: FrameColumn<G>) : GroupedDataRow<T, G>, DataRow<T> by row {

    override fun group() = groupOrNull()!!

    override fun groupOrNull() = frameCol[row.index()]
}

internal fun <T> DataFrame<T>.groupByImpl(columns: ColumnsSelector<T, *>): GroupedDataFrame<T, T> {
    val nameGenerator = nameGenerator(GroupedDataFrame.groupedColumnAccessor.name())
    val keyColumns = get(columns).map {
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

    val keyColumnsDf = org.jetbrains.kotlinx.dataframe.impl.api.insertImpl(keyColumnsToInsert).typed<T>()

    val permutation = groups.flatMap { it.second }
    val sorted = getRows(permutation)

    var lastIndex = 0
    val startIndices = groups.asSequence().map {
        val start = lastIndex
        lastIndex += it.second.size
        start
    }

    val groupedColumn = DataColumn.createFrameColumn(GroupedDataFrame.groupedColumnAccessor.name(), sorted, startIndices.asIterable(), false)

    val df = keyColumnsDf + groupedColumn
    return GroupedDataFrameImpl(df, groupedColumn, columns)
}
