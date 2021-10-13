package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.EmptyDataFrame
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow

public fun <T> DataFrame<T>.appendNulls(numberOfRows: Int = 1): DataFrame<T> {
    require(numberOfRows >= 0)
    if (numberOfRows == 0) return this
    if (ncol() == 0) return EmptyDataFrame(nrow + numberOfRows)
    return columns().map { col ->
        col.replaceAll(col.values + arrayOfNulls(numberOfRows))
    }.toDataFrame()
}

public fun <T> DataFrame<T>.append(vararg values: Any?): DataFrame<T> {
    val ncol = ncol
    assert(values.size % ncol == 0) { "Invalid number of arguments. Multiple of ${ncol()} is expected, but actual was: ${values.size}" }
    val newRows = values.size / ncol
    return columns().mapIndexed { colIndex, col ->
        val newValues = (0 until newRows).map { values[colIndex + it * ncol] }
        col.replaceAll(col.values + newValues)
    }.toDataFrame()
}
