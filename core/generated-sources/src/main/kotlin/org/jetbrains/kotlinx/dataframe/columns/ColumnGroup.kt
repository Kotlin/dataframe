package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import kotlin.reflect.KProperty

/**
 * Group of columns. Used to create column hierarchy in [<code>DataFrame</code>][DataFrame].
 *
 * ColumnGroup is a mix of [<code>DataFrame</code>][DataFrame] and [<code>DataColumn</code>][DataColumn] that supports all [<code>DataFrame</code>][DataFrame] operations but also has [<code>column name</code>][name] and [<code>column type</code>][type].
 * It derives not from [<code>DataColumn</code>][DataColumn], but from [<code>BaseColumn</code>][BaseColumn] to avoid API clashes between [<code>DataFrame</code>][DataFrame] and [<code>DataColumn</code>][DataColumn].
 *
 * ColumnGroup interface can be returned by:
 * - extension property generated for [<code>DataSchema</code>][DataSchema]
 * - [<code>ColumnAccessor</code>][ColumnAccessor] created by [<code>columnGroup</code>][columnGroup] delegate
 * - explicit cast using [<code>asColumnGroup</code>][asColumnGroup]
 *
 * Can be instantiated by [<code>DataColumn.createColumnGroup</code>][DataColumn.createColumnGroup].
 *
 * @param T Schema marker. See [<code>DataFrame</code>][DataFrame] for details.
 */
@HasSchema(schemaArg = 0)
public interface ColumnGroup<out T> :
    BaseColumn<DataRow<T>>,
    DataFrame<T> {

    /**
     * Gets the rows at given indices.
     *
     * NOTE: This doesn't work in the [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl], use [<code>ColumnsSelectionDsl.cols</code>][ColumnsSelectionDsl.cols] to select columns by index.
     */
    override fun get(indices: Iterable<Int>): ColumnGroup<T>

    override fun get(columnName: String): AnyCol

    override fun kind(): ColumnKind = ColumnKind.Group

    override fun distinct(): ColumnGroup<T>

    /**
     * Gets the rows at given indices.
     *
     * NOTE: This doesn't work in the [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl], use [<code>ColumnsSelectionDsl.cols</code>][ColumnsSelectionDsl.cols] to select columns by index.
     */
    override fun get(firstIndex: Int, vararg otherIndices: Int): ColumnGroup<T>

    /**
     * Gets the rows at given range of indices.
     *
     * NOTE: This doesn't work in the [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl], use [<code>ColumnsSelectionDsl.cols</code>][ColumnsSelectionDsl.cols] to select columns by range.
     */
    override fun get(range: IntRange): ColumnGroup<T>

    override fun rename(newName: String): ColumnGroup<T>

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnGroup<T> =
        super.getValue(thisRef, property) as ColumnGroup<T>
}
