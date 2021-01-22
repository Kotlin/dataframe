package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.api.columns.DataCol
import org.jetbrains.dataframe.api.columns.GroupedCol
import kotlin.reflect.KType

internal interface DataColInternal<T> : DataCol<T> {

    fun changeType(type: KType): DataCol<T>
    fun rename(newName: String): DataCol<T>
    fun addParent(parent: GroupedCol<*>): DataCol<T>
}