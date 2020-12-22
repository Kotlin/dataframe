package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.ColumnPath
import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.ColumnWithPath

internal class ColumnWithPathImpl<T> internal constructor(override val data: ColumnData<T>, override val path: ColumnPath) : ColumnWithPath<T> {

    override fun resolveSingle(context: ColumnResolutionContext) = this
}