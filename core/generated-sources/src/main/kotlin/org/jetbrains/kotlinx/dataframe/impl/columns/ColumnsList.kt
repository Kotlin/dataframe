package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver

public interface ColumnsList<C> : ColumnSet<C> {
    public val columns: List<ColumnsResolver<C>>
}

internal class ColumnListImpl<C>(override val columns: List<ColumnsResolver<C>>) :
    ColumnSet<C>,
    ColumnsList<C> {
    constructor(vararg columns: ColumnsResolver<C>) : this(columns.toList())

    override fun resolve(context: ColumnResolutionContext) = columns.flatMap { it.resolve(context) }
}
