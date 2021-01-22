package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.ColumnKind
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.NestedColumn

interface TableCol<out T> : DataCol<DataFrame<T>>, NestedColumn<T> {

    override fun kind() = ColumnKind.Table
}