package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath

internal class DistinctColumnSet<T>(val src: ColumnSet<T>) : ColumnSet<T> {
    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<T>> =
        src.resolve(context).distinctBy { it.path }
}
