package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnKind
import org.jetbrains.kotlinx.dataframe.DataColumn

public interface ValueColumn<T> : DataColumn<T> {

    override fun distinct(): ValueColumn<T>

    override fun kind(): ColumnKind = ColumnKind.Value

    public operator fun get(range: IntRange): DataColumn<T> = slice(range)
}
