package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.tryGetColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.addParentPath
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.depth

public interface ColumnWithPath<out T> : DataColumn<T> {

    public val df: ColumnsContainer<*>
    public val data: DataColumn<T>
    public val path: ColumnPath
    public val parent: ColumnWithPath<*>?
    public fun depth(): Int = path.depth()
    public fun <C> getChild(accessor: ColumnReference<C>): ColumnWithPath<C>? = asColumnGroup()?.tryGetColumn(accessor)?.addPath(path + accessor.path(), df)
    public fun getChild(name: String): ColumnWithPath<Any?>? = asColumnGroup()?.tryGetColumn(name)?.addParentPath(path, df)
    public fun getChild(index: Int): ColumnWithPath<Any?>? = asColumnGroup()?.tryGetColumn(index)?.addParentPath(path, df)
    public fun children(): List<ColumnWithPath<Any?>> = if (isColumnGroup()) data.asColumnGroup().columns().map { it.addParentPath(path, df) } else emptyList()

    override fun rename(newName: String): ColumnWithPath<T>
}

public val <T> ColumnWithPath<T>.depth: Int get() = path.depth()
