package org.jetbrains.dataframe.columns

import org.jetbrains.dataframe.ColumnResolutionContext

interface ColumnSet<out C> {

    fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>
}