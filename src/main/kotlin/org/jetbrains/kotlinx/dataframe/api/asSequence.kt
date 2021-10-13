package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow

public fun <T> DataColumn<T>.asSequence(): Sequence<T> = asIterable().asSequence()

public fun <T> DataFrame<T>.asSequence(): Sequence<DataRow<T>> = asIterable().asSequence()
