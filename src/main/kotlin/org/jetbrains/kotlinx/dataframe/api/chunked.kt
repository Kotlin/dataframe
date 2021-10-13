package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn

public fun <T> DataFrame<T>.chunked(size: Int): FrameColumn<T> {
    val startIndices = (0 until nrow() step size)
    return org.jetbrains.kotlinx.dataframe.columns.DataColumn.create("", this, startIndices, false)
}
