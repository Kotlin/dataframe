package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData

fun <T> ColumnData<T>.asSequence() = asIterable().asSequence()

fun <T> DataFrame<T>.asSequence() = asIterable().asSequence()