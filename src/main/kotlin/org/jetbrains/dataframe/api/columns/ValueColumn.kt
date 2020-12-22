package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.ColumnKind

interface ValueColumn<T> : ColumnData<T> {

    fun distinct(): ColumnData<T>

    override fun kind() = ColumnKind.Data
}