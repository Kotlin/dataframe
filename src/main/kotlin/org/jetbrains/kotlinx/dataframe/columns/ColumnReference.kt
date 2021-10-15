package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.impl.columns.RenamedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.getColumn

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

internal fun <C> ColumnReference<C>.renamedReference(newName: String): ColumnReference<C> = RenamedColumnReference(this, newName)

internal fun ColumnReference<*>.shortPath() = ColumnPath(name)
