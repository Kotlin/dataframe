package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.columns.AnyCol
import org.jetbrains.kotlinx.dataframe.columns.DataColumn

public fun <T> AnyCol.castTo(): DataColumn<T> = this as DataColumn<T>
