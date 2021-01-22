package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.MapColumn
import org.jetbrains.dataframe.api.columns.ValueColumn

internal class ValueWithParentImplColumn<T>(parent: MapColumn<*>, source: ValueColumn<T>) : DataColumnWithParentImpl<T>(parent, source), ValueColumn<T> by source {

    override fun kind() = super<ValueColumn>.kind()

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<T>> {
        return super<DataColumnWithParentImpl>.resolve(context)
    }

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? {
        return super<DataColumnWithParentImpl>.resolveSingle(context)
    }
}