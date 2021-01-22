package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.DataColumn

internal interface DataColumnWithParent<C> : ColumnWithParent<C>, DataColumn<C> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {
        return super<ColumnWithParent>.resolveSingle(context)
    }
}