package org.jetbrains.dataframe

fun <T> DataFrame<T>.count(predicate: RowFilter<T>) = rows.count { predicate(it, it) }