package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.StringCol
import org.jetbrains.kotlinx.dataframe.util.LOWERCASE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.MESSAGE_SHORTCUT

// region StringCol

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(LOWERCASE_REPLACE), DeprecationLevel.WARNING)
public fun DataColumn<String?>.lowercase(): DataColumn<String?> = map { it?.lowercase() }

// endregion
