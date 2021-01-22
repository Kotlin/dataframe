package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataCol

fun <T> DataCol<T>.asSequence() = asIterable().asSequence()

fun <T> DataFrame<T>.asSequence() = asIterable().asSequence()