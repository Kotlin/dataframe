package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.indices

public fun <T> DataFrame<T>.reverse(): DataFrame<T> = get(indices.reversed())

public fun <T> DataColumn<T>.reverse(): DataColumn<T> = get(indices.reversed())

public fun <T> ColumnGroup<T>.reverse(): ColumnGroup<T> = get(indices.reversed())

public fun <T> FrameColumn<T>.reverse(): FrameColumn<T> = get(indices.reversed())

public fun <T> ValueColumn<T>.reverse(): ValueColumn<T> = get(indices.reversed())
