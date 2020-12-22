package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.checkEquals
import org.jetbrains.dataframe.getHashCode

internal abstract class ColumnDataWithParentImpl<T>(override val parent: GroupedColumn<*>, val source: ColumnData<T>) : ColumnDataWithParent<T>, ColumnData<T> by source {

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? {
        return super<ColumnDataWithParent>.resolveSingle(context)
    }

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<T>> {
        return super<ColumnDataWithParent>.resolve(context)
    }
}