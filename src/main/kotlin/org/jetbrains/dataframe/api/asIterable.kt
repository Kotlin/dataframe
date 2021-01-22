package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData

fun <T> ColumnData<T>.asIterable() = values

fun <T> DataFrame<T>.asIterable() = rows()