package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.DataColumn

public fun <T> DataColumn<T>.asIterable(): Iterable<T> = values()

public fun <T> DataFrame<T>.asIterable(): Iterable<DataRow<T>> = rows()
