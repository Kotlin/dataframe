package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.asDataFrame

public fun <T> DataFrame<T>.copy(): DataFrame<T> = columns().asDataFrame<T>()
