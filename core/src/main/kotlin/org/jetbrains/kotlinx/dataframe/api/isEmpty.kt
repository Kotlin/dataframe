package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow

// region DataFrame

public fun AnyFrame.isEmpty(): Boolean = ncol == 0 || nrow == 0
public fun AnyFrame.isNotEmpty(): Boolean = !isEmpty()

// endregion
