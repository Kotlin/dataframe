package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet

internal class ColumnsList<C>(val columns: List<ColumnsResolver<C>>) : ColumnSet<C> {
    constructor(vararg columns: ColumnsResolver<C>) : this(columns.toList())

    override fun resolve(context: ColumnResolutionContext) =
        columns.flatMap { it.resolve(context) }
}
