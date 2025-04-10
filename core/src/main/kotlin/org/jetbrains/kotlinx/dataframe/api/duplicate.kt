package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.api.duplicateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.duplicateRowsImpl

public fun <T> DataFrame<T>.duplicate(n: Int): FrameColumn<T> = List(n) { this }.toFrameColumn()

public fun <T> DataFrame<T>.duplicateRows(n: Int): DataFrame<T> = duplicateRowsImpl(n)

public inline fun <T> DataFrame<T>.duplicateRows(n: Int, filter: RowFilter<T>): DataFrame<T> =
    duplicateRowsImpl(n, rows().filter { filter(it, it) }.map { it.index() })

public fun <T> DataRow<T>.duplicate(n: Int): DataFrame<T> = duplicateImpl(n)
