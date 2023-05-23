package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet

internal class DistinctColumnSet<T>(val src: ColumnsResolver<T>) : ColumnSet<T> {
    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<T>> =
        src.resolve(context).distinctBy { it.path }
}
