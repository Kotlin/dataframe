package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.columns.ColumnSet

internal class ColumnsList<C>(val columns: List<ColumnSet<C>>) : ColumnSet<C> {
    constructor(vararg columns: ColumnSet<C>) : this(columns.toList())

    override fun resolve(context: ColumnResolutionContext) = columns.flatMap { it.resolve(context) }
}