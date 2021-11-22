package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.castFrameColumn
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import kotlin.reflect.KProperty

internal class ColumnGroupWithParent<T>(override val parent: ColumnGroupReference?, override val source: ColumnGroup<T>) : ColumnGroupImpl<T>(source.df, source.name()), ColumnWithParent<DataRow<T>> {

    override fun path() = super<ColumnWithParent>.path()

    override fun getValue(thisRef: Any?, property: KProperty<*>): DataColumnGroup<T> = source.getValue(thisRef, property) as DataColumnGroup<T>

    private fun <T> BaseColumn<T>.addParent(parent: ColumnGroup<*>) = (this as DataColumnInternal<T>).addParent(parent)

    override fun equals(other: Any?) = source.checkEquals(other)

    override fun hashCode() = source.hashCode()

    override fun resolveSingle(context: ColumnResolutionContext) = super<ColumnWithParent>.resolveSingle(context)

    override fun resolve(context: ColumnResolutionContext) = super<ColumnWithParent>.resolve(context)

    override fun <R> resolve(reference: ColumnReference<R>): ColumnWithPath<R>? = reference.resolveSingle(this)

    // region get column

    override fun columns() = df.columns().map { it.addParent(this) }

    override fun getColumnOrNull(index: Int) = df.getColumnOrNull(index)?.addParent(this)
    override fun getColumnOrNull(name: String) = df.getColumnOrNull(name)?.addParent(this)
    override fun <R> getColumnOrNull(column: ColumnReference<R>) = df.getColumnOrNull(column)?.addParent(this)
    override fun getColumnOrNull(path: ColumnPath) = path.fold(this as AnyCol?) { col, name -> col?.asColumnGroup()?.getColumnOrNull(name) }
    override fun <R> getColumnOrNull(column: ColumnSelector<T, R>) = getColumnOrNull(df.getColumnWithPath(column).path)?.cast<R>()

    override operator fun get(columnName: String): AnyCol = getColumn(columnName)
    override operator fun get(columnPath: ColumnPath): AnyCol = getColumn(columnPath)

    override operator fun <R> get(column: KProperty<R>): DataColumn<R> = get(column.name).cast()
    override operator fun <R> get(column: KProperty<DataRow<R>>): ColumnGroup<R> = get(column.name).asColumnGroup().cast()
    override operator fun <R> get(column: KProperty<DataFrame<R>>): FrameColumn<R> = get(column.name).asFrameColumn().castFrameColumn()

    override operator fun <C> get(columns: ColumnsSelector<T, C>): List<DataColumn<C>> = df.get(columns).map { it.addParent(this) }
    override operator fun <C> get(column: ColumnSelector<T, C>): DataColumn<C> = get(column as ColumnsSelector<T, C>).single()

    // endregion

    override fun get(firstIndex: Int, vararg otherIndices: Int): ColumnGroup<T> = ColumnGroupWithParent(parent, super.get(firstIndex, *otherIndices))
}
