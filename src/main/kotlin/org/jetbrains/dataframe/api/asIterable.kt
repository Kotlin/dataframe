package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn

fun <T> DataColumn<T>.asIterable() = values

fun <T> DataFrame<T>.asIterable() = rows()