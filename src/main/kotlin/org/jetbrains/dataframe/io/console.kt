package org.jetbrains.dataframe.io

import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.GroupedDataFrame

fun <T> DataFrame<T>.print() = println(this)
fun <T, G> GroupedDataFrame<T, G>.print() = println(this)