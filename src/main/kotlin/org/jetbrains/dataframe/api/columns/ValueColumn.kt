package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.ColumnKind

interface ValueColumn<T> : DataColumn<T> {

    fun distinct(): DataColumn<T>

    override fun kind() = ColumnKind.Value
}