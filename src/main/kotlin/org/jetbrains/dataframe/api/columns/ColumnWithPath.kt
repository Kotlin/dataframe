package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.asGroup
import kotlin.reflect.KType

interface ColumnWithPath<out T> : ColumnReference<T> {

    val data: DataColumn<T>
    val path: ColumnPath
    val name: String get() = data.name()
    val type: KType get() = data.type
    val hasNulls: Boolean get() = data.hasNulls
    fun isGrouped() = data.isGroup()
    fun asGrouped() = data.asGroup().addPath(path)
    fun depth() = path.depth()
    fun children() = if (isGrouped()) data.asGroup().columns().map { it.addPath(path + it.name()) } else emptyList()
    override fun name() = name
}