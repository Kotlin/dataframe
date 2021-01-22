package org.jetbrains.dataframe

fun AnyRow.isEmpty() = owner.columns().all { it[index] == null }
fun AnyFrame.isEmpty() = ncol() == 0
