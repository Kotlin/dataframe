package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataCol

fun <T> DataCol<T>.asIterable() = values

fun <T> DataFrame<T>.asIterable() = rows()