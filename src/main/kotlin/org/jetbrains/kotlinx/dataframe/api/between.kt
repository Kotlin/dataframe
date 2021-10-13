package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.between
import org.jetbrains.kotlinx.dataframe.DataColumn

public fun <T : Comparable<T>> DataColumn<T>.between(left: T, right: T, includeBoundaries: Boolean = true): DataColumn<Boolean> = map { it.between(left, right, includeBoundaries) }
