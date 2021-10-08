package org.jetbrains.dataframe.columns

import org.jetbrains.dataframe.AnyRow
import org.jetbrains.dataframe.ColumnPath
import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.impl.columns.RenamedColumnReference
import org.jetbrains.dataframe.impl.columns.addPath
import org.jetbrains.dataframe.impl.columns.getColumn

/**
 * Column with type and name/path
 */
public interface ColumnReference<out C> : SingleColumn<C> {

    public fun name(): String

    public fun rename(newName: String): ColumnReference<C>

    public fun path(): ColumnPath = ColumnPath(name)

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {
        return context.df.getColumn<C>(path(), context.unresolvedColumnsPolicy)?.addPath(path(), context.df)
    }
}

public operator fun <C> ColumnReference<C>.invoke(row: AnyRow): C = row[this]

internal val ColumnReference<*>.name get() = name()

internal fun <C> ColumnReference<C>.renamedReference(newName: String): ColumnReference<C> = RenamedColumnReference(this, newName)

internal fun ColumnReference<*>.shortPath() = ColumnPath(name)
