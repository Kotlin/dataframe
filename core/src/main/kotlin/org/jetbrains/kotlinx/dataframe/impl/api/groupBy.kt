package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.GroupedDataRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.GroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

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
    val keyDataColumns = keyColumns.map { it.data }
    val nRows = rowsCount()
    // Reuse row membership to redistribute every value column in a sequential pass.
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
    val groupedColumns: List<ColumnGroupByResult> = columns().map { column ->
        distributeColumnValuesIntoGroups(column, nRows, nGroups, groupSizes, rowToGroup, groups)
    }
    val groupDataFrames: List<DataFrame<T>> = List(nGroups) { groupIndex ->
        groupedColumns.map { it[groupIndex] }.toDataFrame().cast<T>()
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

internal typealias ColumnGroupByResult = Array<AnyCol>

private fun distributeColumnValuesIntoGroups(
    column: AnyCol,
    nRows: Int,
    nGroups: Int,
    groupSizes: IntArray,
    rowToGroup: IntArray,
    groups: List<List<Int>>,
): ColumnGroupByResult =
    when (column) {
        is ValueColumn<*> -> distributeColumnValuesIntoGroups(column, nRows, nGroups, groupSizes, rowToGroup)

        is ColumnGroup<*> -> {
            val childResults: List<ColumnGroupByResult> = column.columns().map { child ->
                distributeColumnValuesIntoGroups(child, nRows, nGroups, groupSizes, rowToGroup, groups)
            }
            Array(nGroups) { groupIndex ->
                DataColumn.createColumnGroup(
                    name = column.name(),
                    df = childResults.map { it[groupIndex] }.toDataFrame(),
                ) as AnyCol
            }
        }

        else -> Array(nGroups) { groupIndex -> column[groups[groupIndex]] }
    }

private fun distributeColumnValuesIntoGroups(
    column: ValueColumn<*>,
    nRows: Int,
    nGroups: Int,
    groupSizes: IntArray,
    rowToGroup: IntArray,
): ColumnGroupByResult {
    @Suppress("UNCHECKED_CAST")
    val values = column.values() as List<Any?>
    val type = column.type()
    val canBecomeFrameColumn = type.isSubtypeOf(typeOf<AnyFrame?>())
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
        if (!groupNullable[groupIndex] && canBecomeFrameColumn) {
            DataColumn.createFrameColumn(column.name(), groupValues[groupIndex] as List<AnyFrame>)
        } else {
            DataColumn.createValueColumn(
                name = column.name(),
                values = groupValues[groupIndex],
                type = if (groupNullable[groupIndex] == type.isMarkedNullable) {
                    type
                } else {
                    type.withNullability(groupNullable[groupIndex])
                },
            )
        }
    }
}
