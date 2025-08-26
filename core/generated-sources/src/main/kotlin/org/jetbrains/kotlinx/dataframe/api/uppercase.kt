package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.StringCol
import org.jetbrains.kotlinx.dataframe.util.MESSAGE_SHORTCUT
import org.jetbrains.kotlinx.dataframe.util.UPPERCASE_REPLACE

// region StringCol

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(UPPERCASE_REPLACE), DeprecationLevel.WARNING)
public fun StringCol.uppercase(): StringCol = map { it?.uppercase() }

// endregion
