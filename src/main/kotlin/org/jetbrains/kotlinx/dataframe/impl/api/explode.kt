package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyBaseColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.appendNulls
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.type

internal fun <T> DataFrame<T>.explodeImpl(dropEmpty: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val columns = getColumnsWithPaths(columns)

    val rowExpandSizes = indices.map { row ->
        columns.maxOf {
            val n = when (val value = it.data[row]) {
                is AnyFrame -> value.nrow()
                is List<*> -> value.size
                else -> 1
            }
            if (!dropEmpty && n == 0) 1
            else n
        }
    }

    val outputRowsCount = rowExpandSizes.sum()

    fun splitIntoRows(df: AnyFrame, data: Set<ColumnPath>): AnyFrame {
        val newColumns: List<AnyBaseColumn> = df.columns().map { col ->

            val isTargetColumn = data.contains(listOf(col.name))
            if (col is ColumnGroup<*>) { // go to nested columns recursively
                val group = col.asColumnGroup()
                val newData = data.mapNotNull {
                    if (it.isNotEmpty() && it[0] == col.name) it.drop(1) else null
                }.toSet()
                val newDf = splitIntoRows(group.df, newData)
                DataColumn.createColumnGroup(col.name, newDf)
            } else if (isTargetColumn) { // values in current column will be splitted
                when (col) {
                    is FrameColumn<*> -> {
                        val newDf = col.values.mapIndexed { row, frame ->
                            val expectedSize = rowExpandSizes[row]
                            when {
                                frame != null -> {
                                    assert(frame.nrow <= expectedSize)
                                    frame.appendNulls(expectedSize - frame.nrow)
                                }
                                expectedSize > 0 -> DataFrame.empty(expectedSize)
                                else -> null
                            }
                        }.concat()

                        DataColumn.createColumnGroup(col.name, newDf)
                    }
                    is ValueColumn<*> -> {
                        val collector = createDataCollector(outputRowsCount)
                        col.asSequence().forEachIndexed { rowIndex, value ->
                            val list = valueToList(value, splitStrings = false)
                            val expectedSize = rowExpandSizes[rowIndex]
                            list.forEach { collector.add(it) }
                            repeat(expectedSize - list.size) {
                                collector.add(null)
                            }
                        }
                        collector.toColumn(col.name)
                    }
                    else -> error("")
                }
            } else { // values in current column will be duplicated
                val collector = createDataCollector<Any?>(outputRowsCount, col.type)
                for (row in 0 until col.size) {
                    val expandSize = rowExpandSizes[row]
                    if (expandSize > 0) {
                        val value = col[row]
                        repeat(expandSize) {
                            collector.add(value)
                        }
                    }
                }
                if (col.isFrameColumn()) DataColumn.createFrameColumn(
                    col.name,
                    collector.values as List<AnyFrame>,
                    collector.hasNulls,
                    col.asFrameColumn().schema // keep original schema
                )
                else collector.toColumn(col.name)
            }
        }
        return newColumns.toDataFrame()
    }

    return splitIntoRows(this, columns.map { it.path }.toSet()).cast()
}
