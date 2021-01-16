package org.jetbrains.dataframe

fun <T> DataFrame<T>.copy() = columns.asDataFrame<T>()