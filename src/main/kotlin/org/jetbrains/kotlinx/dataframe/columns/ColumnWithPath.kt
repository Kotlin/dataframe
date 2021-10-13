package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnPath
import org.jetbrains.kotlinx.dataframe.DataFrameBase
import org.jetbrains.kotlinx.dataframe.impl.columns.addParentPath
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.asGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.depth
import org.jetbrains.kotlinx.dataframe.isGroup

public interface ColumnWithPath<out T> : DataColumn<T> {

    public val df: DataFrameBase<*>
    public val data: DataColumn<T>
    public val path: ColumnPath
    public val parent: ColumnWithPath<*>?
    public fun depth(): Int = path.depth()
    public fun <C> getChild(accessor: ColumnReference<C>): ColumnWithPath<C>? = asGroup()?.tryGetColumn(accessor)?.addPath(path + accessor.path(), df)
    public fun getChild(name: String): ColumnWithPath<Any?>? = asGroup()?.tryGetColumn(name)?.addParentPath(path, df)
    public fun getChild(index: Int): ColumnWithPath<Any?>? = asGroup()?.tryGetColumn(index)?.addParentPath(path, df)
    public fun children(): List<ColumnWithPath<Any?>> = if (isGroup()) data.asGroup().columns().map { it.addParentPath(path, df) } else emptyList()

    override fun rename(newName: String): ColumnWithPath<T>
}

public val <T> ColumnWithPath<T>.depth: Int get() = path.depth()
