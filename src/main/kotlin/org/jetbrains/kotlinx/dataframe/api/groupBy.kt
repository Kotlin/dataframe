package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.dataframe.ColumnToInsert
import org.jetbrains.dataframe.insertColumns
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.RowSelector
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.impl.GroupedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import org.jetbrains.kotlinx.dataframe.pathOf
import org.jetbrains.kotlinx.dataframe.typed
import kotlin.reflect.KProperty

public fun <T> DataColumn<T>.groupBy(vararg cols: AnyCol): GroupedDataFrame<Unit, Unit> = groupBy(cols.toList())
public fun <T> DataColumn<T>.groupBy(cols: Iterable<AnyCol>): GroupedDataFrame<Unit, Unit> =
    (cols + this).toDataFrame<Unit>().groupBy { cols(0 until ncol() - 1) }

public fun <T> DataFrame<T>.groupBy(cols: Iterable<Column>): GroupedDataFrame<T, T> = groupBy { cols.toColumnSet() }
public fun <T> DataFrame<T>.groupBy(vararg cols: KProperty<*>): GroupedDataFrame<T, T> = groupBy { cols.toColumns() }
public fun <T> DataFrame<T>.groupBy(vararg cols: String): GroupedDataFrame<T, T> = groupBy { cols.toColumns() }
public fun <T> DataFrame<T>.groupBy(vararg cols: Column): GroupedDataFrame<T, T> = groupBy { cols.toColumns() }
public fun <T> DataFrame<T>.groupBy(cols: ColumnsSelector<T, *>): GroupedDataFrame<T, T> {
    val nameGenerator = nameGenerator(GroupedDataFrame.columnForGroupedData.name())
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

    val groupedColumn = DataColumn.create(GroupedDataFrame.columnForGroupedData.name(), sorted, startIndices, false)

    val df = keyColumnsDf + groupedColumn
    return GroupedDataFrameImpl(df, groupedColumn, cols)
}

public inline fun <T, reified R> DataFrame<T>.groupByExpr(name: String = "key", noinline expression: RowSelector<T, R?>): GroupedDataFrame<T, T> =
    add(name, expression).groupBy(name)

internal val GroupedDataFrame.Companion.columnForGroupedData by column<AnyFrame>("group")
