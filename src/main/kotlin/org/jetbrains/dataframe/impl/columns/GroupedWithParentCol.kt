package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.DataCol
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.GroupedCol

internal class GroupedWithParentCol<T>(override val parent: GroupedColumnDef?, val source: GroupedCol<T>) : DataColWithParent<DataRow<T>>, GroupedCol<T> by source {

    internal fun <T> DataCol<T>.addParent(parent: GroupedCol<*>) = (this as DataColInternal<T>).addParent(parent)

    override fun get(columnName: String) = df[columnName].addParent(this)
    override fun <R> get(column: ColumnDef<R>) = df[column].addParent(this)
    override fun <R> get(column: ColumnDef<DataRow<R>>) = df[column].addParent(this) as GroupedCol<R>
    override fun columns() = df.columns().map { it.addParent(this) }
    override fun column(columnIndex: Int) = df.column(columnIndex).addParent(this)

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataRow<T>>? {
        return super<DataColWithParent>.resolveSingle(context)
    }

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<DataRow<T>>> {
        return super<DataColWithParent>.resolve(context)
    }
}