package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow

// region DataFrame

public fun DataFrame<*>.isEmpty(): Boolean = ncol == 0 || nrow == 0

public fun DataFrame<*>.isNotEmpty(): Boolean = !isEmpty()

// endregion
