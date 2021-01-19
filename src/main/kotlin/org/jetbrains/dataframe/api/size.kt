package org.jetbrains.dataframe

fun <T, G> GroupedDataFrame<T, G>.size(columnName: String = "size") = aggregate {
    nrow() into columnName
}