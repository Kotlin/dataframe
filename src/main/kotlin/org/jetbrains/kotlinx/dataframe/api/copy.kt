package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame

public fun <T> DataFrame<T>.copy(): DataFrame<T> = columns().toDataFrame<T>()
