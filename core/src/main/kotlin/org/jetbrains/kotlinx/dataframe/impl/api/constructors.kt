package org.jetbrains.kotlinx.dataframe.impl.api

internal fun <T> withValuesImpl(header: List<String>, values: List<T>): List<Pair<String, List<T>>> {
    val ncol = header.size

    require(header.isNotEmpty() && values.size.rem(ncol) == 0) {
        "Number of values ${values.size} is not divisible by number of columns $ncol"
    }

    val nrow = values.size / ncol

    return (0 until ncol).map { col ->
        val colValues = (0 until nrow).map { row ->
            values[row * ncol + col]
        }
        header[col] to colValues
    }
}
