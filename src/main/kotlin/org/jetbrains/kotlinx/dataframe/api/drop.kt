package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.DataColumn

public fun <T> DataFrame<T>.drop(predicate: RowFilter<T>): DataFrame<T> = filter { !predicate(it, it) }

public fun <T> DataColumn<T>.drop(predicate: Predicate<T>): DataColumn<T> = filter { !predicate(it) }
