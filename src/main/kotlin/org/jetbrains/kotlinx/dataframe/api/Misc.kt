package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.toColumn
import org.jetbrains.kotlinx.dataframe.toColumnGuessType
import org.jetbrains.kotlinx.dataframe.values

// region copy

public fun <T> DataFrame<T>.copy(): DataFrame<T> = columns().toDataFrame<T>()

// endregion

// region castTo

public fun <T> AnyCol.castTo(): DataColumn<T> = this as DataColumn<T>

// endregion

// region transpose

public fun <T> DataRow<T>.transpose(): DataFrame<*> = dataFrameOf(owner.columnNames().toColumn(), values.toColumnGuessType())

// endregion

// region print

public fun <T> DataFrame<T>.print(): Unit = println(this)
public fun <T> DataRow<T>.print(): Unit = println(this)
public fun <T> DataColumn<T>.print(): Unit = println(this)

// endregion
