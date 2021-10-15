package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.emptyDataFrame

public operator fun AnyColumn.plus(other: AnyColumn): AnyFrame = dataFrameOf(listOf(this, other))

public fun <T> FrameColumn<T>.union(): DataFrame<Any?> = if (size > 0) values.concat() else emptyDataFrame(0)
