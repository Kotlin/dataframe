package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.util.COPY_REPLACE
import org.jetbrains.kotlinx.dataframe.util.MESSAGE_SHORTCUT

// region DataFrame

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(COPY_REPLACE), DeprecationLevel.WARNING)
public fun <T> DataFrame<T>.copy(): DataFrame<T> = columns().toDataFrame().cast()

// endregion
