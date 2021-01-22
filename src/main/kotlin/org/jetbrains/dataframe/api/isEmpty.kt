package org.jetbrains.dataframe

fun DataRow<*>.isEmpty() = owner.columns().all { it[index] == null }
fun AnyFrame.isEmpty() = ncol() == 0
