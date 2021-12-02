package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyBaseColumn
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.appendNulls
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.getColumn
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
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.pathOf
import org.jetbrains.kotlinx.dataframe.type

internal fun AnyCol.explodeImpl(): AnyCol = dataFrameOf(this).explodeImpl(true) { all() }.getColumn(0)

internal fun <T> DataFrame<T>.explodeImpl(dropEmpty: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val columns = getColumnsWithPaths(columns)

    val rowExpandSizes = indices.map { row ->
        columns.maxOf {
            val n = when (val value = it.data[row]) {
                is AnyFrame -> value.nrow
                is List<*> -> value.size
                else -> 1
            }
            if (!dropEmpty && n == 0) 1
            else n
        }
    }

    val outputRowsCount = rowExpandSizes.sum()

    fun splitIntoRows(df: AnyFrame, data: Map<ColumnPath, AnyCol>): AnyFrame {
        val newColumns: List<AnyBaseColumn> = df.columns().map { srcCol ->

            val dstCol = data[pathOf(srcCol.name)]
            if (srcCol is ColumnGroup<*>) { // go to nested columns recursively
                val group = srcCol.asColumnGroup()
                val newData = data.mapNotNull {
                    if (it.key.isNotEmpty() && it.key[0] == srcCol.name) it.key.drop(1) to it.value else null
                }.toMap()
                val newDf = splitIntoRows(group.df, newData)
                DataColumn.createColumnGroup(srcCol.name, newDf)
            } else if (dstCol != null) { // values in current column will be splitted
                when (dstCol) {
                    is FrameColumn<*> -> {
                        val newDf = dstCol.values.mapIndexed { row, frame ->
                            val expectedSize = rowExpandSizes[row]
                            assert(frame.nrow <= expectedSize)
                            frame.appendNulls(expectedSize - frame.nrow)
                        }.concat()

                        DataColumn.createColumnGroup(dstCol.name, newDf)
                    }
                    is ValueColumn<*> -> {
                        val collector = createDataCollector(outputRowsCount)
                        dstCol.asSequence().forEachIndexed { rowIndex, value ->
                            val list = valueToList(value, splitStrings = false)
                            val expectedSize = rowExpandSizes[rowIndex]
                            list.forEach { collector.add(it) }
                            repeat(expectedSize - list.size) {
                                collector.add(null)
                            }
                        }
                        collector.toColumn(dstCol.name)
                    }
                    else -> error("")
                }
            } else { // values in current column will be duplicated
                val collector = createDataCollector<Any?>(outputRowsCount, srcCol.type)
                for (row in 0 until srcCol.size) {
                    val expandSize = rowExpandSizes[row]
                    if (expandSize > 0) {
                        val value = srcCol[row]
                        repeat(expandSize) {
                            collector.add(value)
                        }
                    }
                }
                if (srcCol.isFrameColumn()) DataColumn.createFrameColumn(
                    srcCol.name,
                    collector.values as List<AnyFrame>,
                    srcCol.asFrameColumn().schema // keep original schema
                )
                else collector.toColumn(srcCol.name)
            }
        }
        return newColumns.toDataFrame()
    }

    return splitIntoRows(this, columns.associate { it.path to it.data }).cast()
}
