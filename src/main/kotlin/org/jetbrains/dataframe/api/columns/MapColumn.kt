package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.*

interface MapColumn<T> : DataColumn<DataRow<T>>, NestedColumn<T>,
    ColumnGroup<T> {

    fun distinctColumn() = DataColumn.createGroup(name(), (this as DataFrame<T>).distinct())

    override fun get(index: Int): DataRow<T> {
        return super<ColumnGroup>.get(index)
    }

    override fun get(columnName: String): AnyCol {
        return super<ColumnGroup>.get(columnName)
    }

    override fun kind() = ColumnKind.Map
}