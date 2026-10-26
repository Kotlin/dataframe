package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.StringCol
import org.jetbrains.kotlinx.dataframe.util.MESSAGE_SHORTCUT
import org.jetbrains.kotlinx.dataframe.util.UPPERCASE_REPLACE

// region StringCol

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(UPPERCASE_REPLACE), DeprecationLevel.WARNING)
public fun DataColumn<String?>.uppercase(): DataColumn<String?> = map { it?.uppercase() }

// endregion
