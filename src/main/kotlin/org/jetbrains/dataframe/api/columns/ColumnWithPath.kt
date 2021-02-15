package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.asGroup
import kotlin.reflect.KType

interface ColumnWithPath<out T> : ColumnReference<T> {

    val df: DataFrameBase<*>
    val data: DataColumn<T>
    val path: ColumnPath
    val depth: Int get() = path.size - 1
    val name: String get() = data.name
    val type: KType get() = data.type
    val hasNulls: Boolean get() = data.hasNulls
    val parent: ColumnWithPath<*>?
    fun isGroup() = data.isGroup()
    fun depth() = path.depth()
    fun <C> getChild(accessor: ColumnReference<C>): ColumnWithPath<C>? = asGroup()?.tryGetColumn(accessor)?.addPath(path + accessor.columnPath(), df)
    fun getChild(name: String) = asGroup()?.tryGetColumn(name)?.addParentPath(path, df)
    fun getChild(index: Int) = asGroup()?.tryGetColumn(index)?.addParentPath(path, df)
    fun children() = if (isGroup()) data.asGroup().columns().map { it.addParentPath(path, df) } else emptyList()
    override fun name() = name

    override fun resolveSingle(context: ColumnResolutionContext) = this
}

internal fun ColumnWithPath<*>.asGroup() = if(data.isGroup()) data.asGroup() else null

val ColumnWithPath<*>?.df get() = this!!.df
val ColumnWithPath<*>?.data get() = this!!.data
val ColumnWithPath<*>?.path get() = this!!.path
val ColumnWithPath<*>?.depth get() = this!!.depth
val ColumnWithPath<*>?.name get() = this!!.name
val ColumnWithPath<*>?.type get() = this!!.type
val ColumnWithPath<*>?.hasNulls get() = this!!.hasNulls
val ColumnWithPath<*>?.parent get() = this!!.parent