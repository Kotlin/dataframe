package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.RenamedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.getColumn
import kotlin.reflect.KProperty

/**
 * Entity that can retrieve [<code>DataColumn</code>][DataColumn] from [<code>DataFrame</code>][DataFrame] or value from [<code>DataRow</code>][DataRow].
 *
 * Base interface for [<code>DataColumn</code>][DataColumn] and [<code>ColumnAccessor</code>][ColumnAccessor].
 * @param C Expected [<code>type</code>][DataColumn.type] of values in the column
 */
public interface ColumnReference<out C> : SingleColumn<C> {

    public operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnReference<C> =
        renamedReference(property.columnName)

    public fun name(): String

    public fun rename(newName: String): ColumnReference<C>

    public fun path(): ColumnPath = ColumnPath(name)

    public fun getValue(row: AnyRow): C = resolveFor(row.df())!![row.index()]

    public fun getValueOrNull(row: AnyRow): C? = resolveFor(row.df())?.get(row.index())

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? =
        context.df
            .getColumn<C>(path(), context.unresolvedColumnsPolicy)
            ?.addPath(path())
}

internal fun <C> ColumnReference<C>.renamedReference(newName: String): ColumnReference<C> =
    RenamedColumnReference(this, newName)

internal fun ColumnReference<*>.shortPath() = ColumnPath(name)

internal fun <C> ColumnReference<C>.resolveFor(df: AnyFrame): ColumnWithPath<C>? =
    resolveSingle(ColumnResolutionContext(df, UnresolvedColumnsPolicy.Skip))
