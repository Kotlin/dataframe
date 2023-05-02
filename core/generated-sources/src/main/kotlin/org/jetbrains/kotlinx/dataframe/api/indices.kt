package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.indices

// region DataFrame

public fun AnyFrame.indices(): IntRange = 0 until rowsCount()

public fun <T> DataFrame<T>.indices(filter: RowFilter<T>): List<Int> = indices.filter {
    val row = get(it)
    filter(row, row)
}

// endregion
