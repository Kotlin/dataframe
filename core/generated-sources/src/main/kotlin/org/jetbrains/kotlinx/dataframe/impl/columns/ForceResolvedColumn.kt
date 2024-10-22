package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.DataColumn

internal interface ForceResolvedColumn<T> : DataColumn<T> {
    val source: DataColumn<T>
}
