package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn

public fun <T> AnyCol.castTo(): DataColumn<T> = this as DataColumn<T>
