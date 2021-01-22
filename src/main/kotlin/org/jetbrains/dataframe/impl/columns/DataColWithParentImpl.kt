package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.DataCol
import org.jetbrains.dataframe.api.columns.GroupedCol
import org.jetbrains.dataframe.checkEquals
import org.jetbrains.dataframe.getHashCode

internal abstract class DataColWithParentImpl<T>(override val parent: GroupedCol<*>, val source: DataCol<T>) : DataColWithParent<T>, DataCol<T> by source {

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? {
        return super<DataColWithParent>.resolveSingle(context)
    }

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<T>> {
        return super<DataColWithParent>.resolve(context)
    }
}