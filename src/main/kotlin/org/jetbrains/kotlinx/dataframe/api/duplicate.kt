package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.DataRowBase
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.name
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.owner
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.full.withNullability

public fun <T> DataRowBase<T>.duplicate(n: Int): DataFrame<T> = (this as DataRow<T>).owner.columns().mapIndexed { colIndex, col ->
    when (col) {
        is ColumnGroup<*> -> DataColumn.create(col.name, col[index].duplicate(n))
        else -> {
            val value = col[index]
            if (value is AnyFrame) {
                DataColumn.create(col.name, MutableList(n) { value })
            } else DataColumn.create(col.name, MutableList(n) { value }, col.type.withNullability(value == null))
        }
    }
}.toDataFrame()
