package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.StringCol
import org.jetbrains.kotlinx.dataframe.util.LENGTH_REPLACE
import org.jetbrains.kotlinx.dataframe.util.MESSAGE_SHORTCUT

// region StringCol

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(LENGTH_REPLACE), DeprecationLevel.WARNING)
public fun StringCol.length(): DataColumn<Int> = map { it?.length ?: 0 }

// endregion
