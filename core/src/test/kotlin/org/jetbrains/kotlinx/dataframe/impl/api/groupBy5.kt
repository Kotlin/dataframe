package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.GroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import java.util.stream.Collectors
import kotlin.reflect.full.withNullability

/** Step 5: process independent source columns in parallel on top of the column-first implementation. */
internal fun <T> DataFrame<T>.groupByImpl5(moveToTop: Boolean, columns: ColumnsSelector<T, *>): GroupBy<T, T> {
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
    val nRows = rowsCount()
    val rowToGroup = IntArray(nRows)
    val groupMap = LinkedHashMap<Any?, Int>()
    val groups = ArrayList<MutableList<Int>>()
    if (keyDataColumns.size == 1) {
        val column = keyDataColumns[0]
        for (index in 0 until nRows) {
            val groupIndex = groupMap.getOrPut(column[index]) {
                groups.add(ArrayList())
                groups.lastIndex
            }
            groups[groupIndex].add(index)
            rowToGroup[index] = groupIndex
        }
    } else {
        for (index in 0 until nRows) {
            val key = ArrayList<Any?>(keyDataColumns.size)
            for (column in keyDataColumns) key.add(column[index])
            val groupIndex = groupMap.getOrPut(key) {
                groups.add(ArrayList())
                groups.lastIndex
            }
            groups[groupIndex].add(index)
            rowToGroup[index] = groupIndex
        }
    }

    val nGroups = groups.size

    val keyIndices = List(nGroups) { groups[it][0] }

    val keyColumnsToInsert = keyColumns.map {
        val column = it[keyIndices]
        val path = if (moveToTop) pathOf(it.name()) else it.path()
        ColumnToInsert(path, column, null)
    }

    val keyColumnsDf = dataFrameOf(keyColumnsToInsert).cast<T>()

    val groupSizes = IntArray(nGroups) { groups[it].size }
    val columnGroupResults: List<Array<AnyCol>> = columns().parallelStream().map { column ->
        if (column is ValueColumn<*>) {
            processValueColumnForGroups(column, nRows, nGroups, groupSizes, rowToGroup)
        } else {
            Array(nGroups) { groupIndex -> column[groups[groupIndex]] }
        }
    }.collect(Collectors.toList())
    val groupDataFrames = List(nGroups) { groupIndex ->
        columnGroupResults.map { it[groupIndex] }.toDataFrame().cast<T>()
    }

    val groupedColumnName = keyColumnsDf.nameGenerator().addUnique(GroupBy.groupedColumnAccessor.name())
    val groupedColumn = DataColumn.createFrameColumn(
        name = groupedColumnName,
        groups = groupDataFrames,
        schema = lazy { schema() },
    )

    val df = keyColumnsDf + groupedColumn
    return GroupByImpl(df, groupedColumn, columns)
}

private fun processValueColumnForGroups(
    column: ValueColumn<*>,
    nRows: Int,
    nGroups: Int,
    groupSizes: IntArray,
    rowToGroup: IntArray,
): Array<AnyCol> {
    @Suppress("UNCHECKED_CAST")
    val values = column.values() as List<Any?>
    val type = column.type()
    val groupValues = Array<MutableList<Any?>>(nGroups) { ArrayList(groupSizes[it]) }
    val groupNullable = BooleanArray(nGroups)

    for (index in 0 until nRows) {
        val groupIndex = rowToGroup[index]
        val value = values[index]
        if (value == null) groupNullable[groupIndex] = true
        groupValues[groupIndex].add(value)
    }

    return Array(nGroups) { groupIndex ->
        @Suppress("UNCHECKED_CAST")
        DataColumn.createValueColumn(
            name = column.name(),
            values = groupValues[groupIndex] as List<Nothing>,
            type = if (groupNullable[groupIndex] == type.isMarkedNullable) {
                type
            } else {
                type.withNullability(groupNullable[groupIndex])
            },
        ) as AnyCol
    }
}
