package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.api.frameColumn
import kotlin.reflect.KProperty

/**
 * Combination of [column path][path] and [column type][T].
 *
 * Used to retrieve [DataColumn] from [DataFrame] or value from [DataRow].
 *
 * Can be created by [column], [columnGroup] or [frameColumn] delegates.
 *
 * @param T Expected [type][DataColumn.type] of values in the column
 */
public interface ColumnAccessor<out T> : ColumnReference<T> {

    public override operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnAccessor<T> = this

    public operator fun <C> get(column: ColumnReference<C>): ColumnAccessor<C>

    override fun rename(newName: String): ColumnAccessor<T>
}
