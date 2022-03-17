package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.StringCol

// region StringCol

public fun StringCol.lowercase(): StringCol = map { it?.lowercase() }

// endregion
