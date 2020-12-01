package org.jetbrains.dataframe

fun <T, G> GroupedDataFrame<T, G>.countInto(columnName: String) = aggregate {
    nrow into columnName
}

fun <T> DataFrame<T>.count(predicate: RowFilter<T>) = rows.count { predicate(it, it) }