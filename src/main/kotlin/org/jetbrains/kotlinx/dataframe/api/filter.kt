package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.VectorizedRowFilter
import org.jetbrains.kotlinx.dataframe.isMatching

public fun <T> DataFrame<T>.filter(predicate: RowFilter<T>): DataFrame<T> =
    (0 until nrow()).filter {
        val row = get(it)
        predicate(row, row)
    }.let { get(it) }

public fun <T> DataColumn<T>.filter(predicate: Predicate<T>): DataColumn<T> = slice(isMatching(predicate))

internal fun <T> DataFrame<T>.filterFast(predicate: VectorizedRowFilter<T>) = this[predicate(this)]
