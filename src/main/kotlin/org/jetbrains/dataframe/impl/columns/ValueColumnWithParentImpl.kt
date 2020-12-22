package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.api.columns.ValueColumn
import org.jetbrains.dataframe.checkEquals
import org.jetbrains.dataframe.getHashCode

internal class ValueColumnWithParentImpl<T>(parent: GroupedColumn<*>, source: ValueColumn<T>) : ColumnDataWithParentImpl<T>(parent, source), ValueColumn<T> by source {

    override fun kind() = super<ValueColumn>.kind()

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<T>> {
        return super<ColumnDataWithParentImpl>.resolve(context)
    }

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? {
        return super<ColumnDataWithParentImpl>.resolveSingle(context)
    }
}