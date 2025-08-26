package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.util.TAIL
import org.jetbrains.kotlinx.dataframe.util.TAIL_REPLACE

// region DataFrame

@Deprecated(TAIL, ReplaceWith(TAIL_REPLACE), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.tail(numRows: Int = 5): DataFrame<T> = takeLast(numRows)

// endregion
