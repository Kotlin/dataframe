package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.exceptions.DataFrameError

/**
 * Public API to be re-used in compiler plugin implementation
 */
public fun <T> Pair<List<String>, List<T>>.withValuesImpl(): List<Pair<String, List<T>>> {
    val (header, values) = this
    val ncol = header.size

    if (!(header.isNotEmpty() && values.size.rem(ncol) == 0)) {
        throw WrongNumberOfValuesException(values.size, ncol)
    }

    val nrow = values.size / ncol

    return (0 until ncol).map { col ->
        val colValues = (0 until nrow).map { row ->
            values[row * ncol + col]
        }
        header[col] to colValues
    }
}

internal class WrongNumberOfValuesException(size: Int, ncol: Int) :
    IllegalArgumentException(),
    DataFrameError {
    override val message = "Number of values $size is not divisible by number of columns $ncol"
}
