package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.GroupedColumn

internal interface ColumnDataInternal<T> : ColumnData<T> {

    fun rename(newName: String): ColumnData<T>
    fun addParent(parent: GroupedColumn<*>): ColumnData<T>
}