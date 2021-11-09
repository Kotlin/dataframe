package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.values

// region copy

public fun <T> DataFrame<T>.copy(): DataFrame<T> = columns().toDataFrame().cast()

// endregion

// region transpose

public fun <T> DataRow<T>.transpose(): DataFrame<*> = dataFrameOf(owner.columnNames().toValueColumn(), values.toColumn(inferType = true))

// endregion

// region print

public fun <T> DataFrame<T>.print(): Unit = println(this)
public fun <T> DataRow<T>.print(): Unit = println(this)
public fun <T> DataColumn<T>.print(): Unit = println(this)

// endregion
