package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.*

interface GroupedCol<T> : DataCol<DataRow<T>>, NestedColumn<T>, GroupedColumnBase<T> {

    override fun get(index: Int): DataRow<T> {
        return super<GroupedColumnBase>.get(index)
    }

    override fun get(columnName: String): AnyCol {
        return super<GroupedColumnBase>.get(columnName)
    }

    override fun kind() = ColumnKind.Group
}