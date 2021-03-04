package org.jetbrains.dataframe

import org.jetbrains.dataframe.columns.DataColumn

fun <T> DataColumn<T>.asSequence() = asIterable().asSequence()

fun <T> DataFrame<T>.asSequence() = asIterable().asSequence()