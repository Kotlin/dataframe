package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame

// region DataFrame

public fun <T> DataFrame<T>.head(numRows: Int = 5): DataFrame<T> = take(numRows)

// endregion
