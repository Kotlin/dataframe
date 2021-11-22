package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.addParentPath
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.depth

public interface ColumnWithPath<out T> : DataColumn<T> {

    public val host: ColumnsContainer<*>
    public val data: DataColumn<T>
    public val path: ColumnPath
    public val parent: ColumnWithPath<*>?
    public fun depth(): Int = path.depth()
    public fun <C> getChild(accessor: ColumnReference<C>): ColumnWithPath<C>? = asColumnGroup().getColumnOrNull(accessor)?.addPath(path + accessor.path(), host)
    public fun getChild(name: String): ColumnWithPath<Any?>? = asColumnGroup().getColumnOrNull(name)?.addParentPath(path, host)
    public fun getChild(index: Int): ColumnWithPath<Any?>? = asColumnGroup().getColumnOrNull(index)?.addParentPath(path, host)
    public fun children(): List<ColumnWithPath<Any?>> = if (isColumnGroup()) data.asColumnGroup().columns().map { it.addParentPath(path, host) } else emptyList()

    override fun path(): ColumnPath = path

    override fun rename(newName: String): ColumnWithPath<T>
}

public val <T> ColumnWithPath<T>.depth: Int get() = path.depth()
