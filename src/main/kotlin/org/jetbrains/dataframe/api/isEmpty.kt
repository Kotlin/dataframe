package org.jetbrains.dataframe

fun DataRow<*>.isEmpty() = owner.columns().all { it[index] == null }
fun DataFrame<*>.isEmpty() = ncol() == 0
