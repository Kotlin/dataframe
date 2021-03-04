package org.jetbrains.dataframe.columns

import org.jetbrains.dataframe.*

interface MapColumn<T> : DataColumn<DataRow<T>>, NestedColumn<T>,
    ColumnGroup<T> {

    fun distinctColumn() = DataColumn.Companion.create(name(), (this as DataFrame<T>).distinct())

    override fun get(index: Int): DataRow<T> {
        return super<ColumnGroup>.get(index)
    }

    override fun get(columnName: String): AnyCol {
        return super<ColumnGroup>.get(columnName)
    }

    override fun kind() = ColumnKind.Map
}