package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

internal interface FrameColumnInternal<out T> : FrameColumn<T> {
    val schema: Lazy<DataFrameSchema>
}

internal fun <T> FrameColumn<T>.internal() = this as FrameColumnInternal<T>
