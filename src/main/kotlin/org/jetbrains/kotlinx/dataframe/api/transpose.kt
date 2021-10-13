package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.owner
import org.jetbrains.kotlinx.dataframe.toColumn
import org.jetbrains.kotlinx.dataframe.toColumnGuessType
import org.jetbrains.kotlinx.dataframe.values

public fun <T> DataRow<T>.transpose(): DataFrame<*> = dataFrameOf(owner.columnNames().toColumn(), values.toColumnGuessType())
