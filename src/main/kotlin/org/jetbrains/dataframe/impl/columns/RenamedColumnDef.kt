package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnWithPath

internal class RenamedColumnDef<C>(val source: ColumnDef<C>, override val name: String) : ColumnDef<C> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {

        return source.resolveSingle(context)?.let { it.data.doRename(name).addPath(it.path) }
    }
}