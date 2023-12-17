package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.indices

// region DataColumn

public fun <T> DataColumn<T>.shuffle(): DataColumn<T> = get(indices.shuffled())

// endregion

// region DataFrame

public fun <T> DataFrame<T>.shuffle(): DataFrame<T> = getRows(indices.shuffled())

// endregion
