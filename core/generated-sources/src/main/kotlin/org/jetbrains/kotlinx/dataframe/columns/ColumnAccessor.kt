package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.api.frameColumn
import kotlin.reflect.KProperty

/**
 * Combination of [<code>column path</code>][path] and [<code>column type</code>][T].
 *
 * Used to retrieve [<code>DataColumn</code>][DataColumn] from [<code>DataFrame</code>][DataFrame] or value from [<code>DataRow</code>][DataRow].
 *
 * Can be created by [<code>column</code>][column], [<code>columnGroup</code>][columnGroup] or [<code>frameColumn</code>][frameColumn] delegates.
 *
 * @param T Expected [<code>type</code>][DataColumn.type] of values in the column
 */
public interface ColumnAccessor<out T> : ColumnReference<T> {

    public override operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnAccessor<T> = this

    public operator fun <C> get(column: ColumnReference<C>): ColumnAccessor<C>

    override fun rename(newName: String): ColumnAccessor<T>
}
