package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.asDataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.forEachIndexed
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.full.withNullability

internal fun <T> DataFrame<T>.duplicateRowsImpl(n: Int): DataFrame<T> = columns().map { it.duplicateValuesImpl(n) }.toDataFrame().cast()

internal fun <T> DataColumn<T>.duplicateValuesImpl(n: Int): DataColumn<T> {
    require(n > 0)
    return when (this) {
        is ColumnGroup<*> -> DataColumn.createColumnGroup(
            name,
            asDataFrame().duplicateRowsImpl(n)
        ).asDataColumn()
        else -> {
            val list = List(size() * n) { get(it / n) }
            if (isFrameColumn()) DataColumn.createFrameColumn(name, list as List<AnyFrame>)
            else DataColumn.createValueColumn(name, list, type, defaultValue = defaultValue())
        }
    }.cast()
}

internal fun <T> DataColumn<T>.duplicateValuesImpl(n: Int, indicesSorted: Iterable<Int>): DataColumn<T> = when (this) {
    is ColumnGroup<*> -> DataColumn.createColumnGroup(
        name,
        asDataFrame().duplicateRowsImpl(n, indicesSorted)
    ).asDataColumn()
    else -> {
        val list = mutableListOf<Any?>()
        val iterator = indicesSorted.iterator()
        var next = if (iterator.hasNext()) iterator.next() else -1
        forEachIndexed { i, value ->
            if (i == next) {
                repeat(n) { list.add(value) }
                next = if (iterator.hasNext()) iterator.next() else -1
            } else list.add(value)
        }
        if (isFrameColumn()) DataColumn.createFrameColumn(name, list as List<AnyFrame>)
        else DataColumn.createValueColumn(name, list, type, defaultValue = defaultValue())
    }
}.cast()

internal fun <T> DataFrame<T>.duplicateRowsImpl(n: Int, indicesSorted: Iterable<Int>): DataFrame<T> = columns().map { it.duplicateValuesImpl(n, indicesSorted) }.toDataFrame().cast()

internal fun <T> DataRow<T>.duplicateImpl(n: Int): DataFrame<T> = owner.columns().map { col ->
    when (col) {
        is ColumnGroup<*> -> DataColumn.createColumnGroup(col.name, col[index].duplicateImpl(n))
        else -> {
            val value = col[index]
            if (value is AnyFrame) {
                DataColumn.createFrameColumn(col.name, List(n) { value })
            } else DataColumn.createValueColumn(
                col.name,
                List(n) { value },
                col.type.withNullability(value == null)
            )
        }
    }
}.toDataFrame().cast()
