package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.GroupedCol
import org.jetbrains.dataframe.api.columns.ValueCol

internal class ValueWithParentImplCol<T>(parent: GroupedCol<*>, source: ValueCol<T>) : DataColWithParentImpl<T>(parent, source), ValueCol<T> by source {

    override fun kind() = super<ValueCol>.kind()

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<T>> {
        return super<DataColWithParentImpl>.resolve(context)
    }

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? {
        return super<DataColWithParentImpl>.resolveSingle(context)
    }
}