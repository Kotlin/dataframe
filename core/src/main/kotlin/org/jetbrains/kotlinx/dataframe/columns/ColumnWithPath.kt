package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.addParentPath
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.depth
import kotlin.reflect.KProperty

public interface ColumnWithPath<out T> : DataColumn<T> {

    public val data: DataColumn<T>
    public val path: ColumnPath
    public val name: String get() = name()
    public val parentName: String? get() = path.parentName
    public fun depth(): Int = path.depth()
    public fun <C> getChild(accessor: ColumnReference<C>): ColumnWithPath<C>? = asColumnGroup().getColumnOrNull(accessor)?.addPath(path + accessor.path())
    public fun getChild(name: String): ColumnWithPath<Any?>? = asColumnGroup().getColumnOrNull(name)?.addParentPath(path)
    public fun getChild(index: Int): ColumnWithPath<Any?>? = asColumnGroup().getColumnOrNull(index)?.addParentPath(path)
    public fun <C> getChild(accessor: KProperty<C>): ColumnWithPath<C>? = asColumnGroup().getColumnOrNull(accessor)?.addParentPath(path)

    public fun children(): List<ColumnWithPath<Any?>> = if (isColumnGroup()) data.asColumnGroup().columns().map { it.addParentPath(path) } else emptyList()

    override fun path(): ColumnPath = path

    override fun rename(newName: String): ColumnWithPath<T>
}

public val <T> ColumnWithPath<T>.depth: Int get() = path.depth()
