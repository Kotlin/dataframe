package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.StringCol

// region StringCol

public fun StringCol.length(): DataColumn<Int> = map { it?.length ?: 0 }

// endregion
