package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.asGroup
import kotlin.reflect.KType

interface ColumnWithPath<out T> : ColumnReference<T> {

    val data: DataColumn<T>
    val path: ColumnPath
    val name: String get() = data.name
    val type: KType get() = data.type
    val hasNulls: Boolean get() = data.hasNulls
    fun isGroup() = data.isGroup()
    fun depth() = path.depth()
    fun <C> getChild(accessor: ColumnReference<C>): ColumnWithPath<C>? = asGroup()?.tryGetColumn(accessor)?.addPath(path + accessor.columnPath())
    fun getChild(name: String) = asGroup()?.tryGetColumn(name)?.addParentPath(path)
    fun getChild(index: Int) = asGroup()?.tryGetColumn(index)?.addParentPath(path)
    fun children() = if (isGroup()) data.asGroup().columns().map { it.addParentPath(path) } else emptyList()
    override fun name() = name
}

internal fun ColumnWithPath<*>.asGroup() = if(data.isGroup()) data.asGroup() else null
