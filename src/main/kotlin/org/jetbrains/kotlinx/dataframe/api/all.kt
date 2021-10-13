package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.values

public fun <T> DataColumn<T>.all(predicate: Predicate<T>): Boolean = values.all(predicate)
