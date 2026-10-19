package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf

/** Normalizes the final, generated Int count column of an existing valueCounts result. */
internal fun DataFrame<*>.normalizeValueCountsImpl(): DataFrame<*> {
    val columns = columns()
    val counts = columns.last().cast<Int>()
    val total = counts.values().sumOf { it.toLong() }
    val proportions = DataColumn.createValueColumn(counts.name(), counts.values().map { it.toDouble() / total })
    return dataFrameOf(columns.dropLast(1) + proportions)
}
