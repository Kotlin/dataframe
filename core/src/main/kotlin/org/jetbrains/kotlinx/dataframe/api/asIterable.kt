package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn

// region DataColumn

public fun <T> DataColumn<T>.asIterable(): Iterable<T> = values()

// endregion
