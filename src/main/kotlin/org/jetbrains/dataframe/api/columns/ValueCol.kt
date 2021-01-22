package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.ColumnKind

interface ValueCol<T> : DataCol<T> {

    fun distinct(): DataCol<T>

    override fun kind() = ColumnKind.Data
}