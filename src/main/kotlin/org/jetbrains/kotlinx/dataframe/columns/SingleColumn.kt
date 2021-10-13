package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnResolutionContext

public interface SingleColumn<out C> : Columns<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> = resolveSingle(context)?.let { listOf(it) } ?: emptyList()

    public fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>?
}
