package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.api.columns.FrameColumn

fun <T> DataFrame<T>.chunked(size: Int): FrameColumn<T> {
    val startIndices = (0 until nrow() step size)
    return DataColumn.create("", this, startIndices)
}