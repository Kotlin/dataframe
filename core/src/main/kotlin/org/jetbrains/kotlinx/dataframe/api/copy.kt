package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.util.MESSAGE_SHORTCUT_1_0

// region DataFrame

@Deprecated(MESSAGE_SHORTCUT_1_0, ReplaceWith("columns().toDataFrame().cast()"), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.copy(): DataFrame<T> = columns().toDataFrame().cast()

// endregion
