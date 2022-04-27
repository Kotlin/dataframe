package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath

internal class RenamedColumnReference<C>(val source: ColumnReference<C>, val name: String) : ColumnReference<C> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {
        return source.resolveSingle(context)?.let { it.data.rename(name).addPath(it.path, context.df) }
    }

    override fun name() = name

    override fun rename(newName: String) = RenamedColumnReference(source, newName)

    override fun getValue(row: AnyRow) = source.getValue(row)

    override fun getValueOrNull(row: AnyRow) = source.getValueOrNull(row)
}
