package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.concatImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateWith
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow

// region concat

@JvmName("concatRows")
public fun <T> Iterable<DataRow<T>?>.concat(): DataFrame<T> = concatImpl(map { it?.toDataFrame() ?: emptyDataFrame(1) }).cast()

public fun <T> Iterable<DataFrame<T>>.concat(): DataFrame<T> {
    return concatImpl(asList()).cast()
}

public fun <T> DataColumn<DataFrame<T>>.concat(): DataFrame<T> = values.concat().cast()

public fun <T> DataColumn<Collection<T>>.concat(): List<T> = values.flatten()

public fun <T> DataFrame<T>.concat(vararg other: DataFrame<T>): DataFrame<T> = concatImpl(listOf(this) + other.toList()).cast<T>()

// endregion

// region append

public fun <T> DataFrame<T>.append(vararg values: Any?): DataFrame<T> {
    val ncol = ncol
    assert(values.size % ncol == 0) { "Invalid number of arguments. Multiple of $ncol is expected, but actual was: ${values.size}" }
    val newRows = values.size / ncol
    return columns().mapIndexed { colIndex, col ->
        val newValues = (0 until newRows).map { values[colIndex + it * ncol] }
        col.updateWith(col.values + newValues)
    }.toDataFrame().cast()
}

public fun <T> DataFrame<T>.appendNulls(numberOfRows: Int = 1): DataFrame<T> {
    require(numberOfRows >= 0)
    if (numberOfRows == 0) return this
    if (ncol == 0) return DataFrame.empty(nrow + numberOfRows).cast()
    return columns().map { col ->
        col.updateWith(col.values + arrayOfNulls(numberOfRows))
    }.toDataFrame().cast()
}

// endregion
