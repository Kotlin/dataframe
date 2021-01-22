package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.DataCol

internal interface DataColWithParent<C> : ColumnWithParent<C>, DataCol<C> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {
        return super<ColumnWithParent>.resolveSingle(context)
    }
}