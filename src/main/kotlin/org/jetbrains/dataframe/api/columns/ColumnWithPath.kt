package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.asGrouped
import kotlin.reflect.KType

interface ColumnWithPath<out T> : ColumnDef<T> {

    val data: ColumnData<T>
    val path: ColumnPath
    val name: String get() = data.name()
    val type: KType get() = data.type
    val hasNulls: Boolean get() = data.hasNulls
    fun isGrouped() = data.isGrouped()
    fun asGrouped() = data.asGrouped().addPath(path)
    fun depth() = path.depth()
    fun children() = if (isGrouped()) data.asGrouped().columns().map { it.addPath(path + it.name()) } else emptyList()
    override fun name() = name
}