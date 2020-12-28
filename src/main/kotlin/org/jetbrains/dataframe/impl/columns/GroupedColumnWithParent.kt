package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.GroupedColumn

internal class GroupedColumnWithParent<T>(override val parent: GroupedColumnDef?, val source: GroupedColumn<T>) : ColumnDataWithParent<DataRow<T>>, GroupedColumn<T> by source {

    internal fun <T> ColumnData<T>.addParent(parent: GroupedColumn<*>) = (this as ColumnDataInternal<T>).addParent(parent)

    override fun get(columnName: String) = df[columnName].addParent(this)
    override fun <R> get(column: ColumnDef<R>) = df[column].addParent(this)
    override fun <R> get(column: ColumnDef<DataRow<R>>) = df[column].addParent(this) as GroupedColumn<R>
    override fun columns() = df.columns().map { it.addParent(this) }
    override fun getColumn(columnIndex: Int) = df.getColumn(columnIndex).addParent(this)

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataRow<T>>? {
        return super<ColumnDataWithParent>.resolveSingle(context)
    }

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<DataRow<T>>> {
        return super<ColumnDataWithParent>.resolve(context)
    }
}