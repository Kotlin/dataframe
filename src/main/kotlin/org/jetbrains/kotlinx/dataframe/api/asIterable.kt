package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow

public fun <T> DataColumn<T>.asIterable(): Iterable<T> = values()

public fun <T> DataFrame<T>.asIterable(): Iterable<DataRow<T>> = rows()
