package org.jetbrains.dataframe

import org.jetbrains.dataframe.columns.ColumnGroup
import org.jetbrains.dataframe.columns.DataColumn
import org.jetbrains.dataframe.columns.FrameColumn
import org.jetbrains.dataframe.columns.MapColumn
import org.jetbrains.dataframe.columns.ValueColumn

fun <T> DataFrame<T>.distinct() = distinctBy { it.values }

inline fun <T, reified C: DataColumn<T>> C.distinct(): C = when(this) {
    is ValueColumn<*> -> this.distinct() as C
    is MapColumn<*> -> this.distinctColumn() as C
    is FrameColumn<*> -> this.distinct() as C
    else -> throw RuntimeException("")
}

fun <T> ColumnGroup<T>.distinct() = (this as MapColumn<T>).distinctColumn()