package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.castFrameColumn
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columnName
import kotlin.reflect.KProperty

internal class ColumnGroupWithParent<T>(override val parent: ColumnGroupReference?, override val source: ColumnGroup<T>) : ColumnGroupImpl<T>(source.name(), source), ColumnWithParent<DataRow<T>> {

    override fun path() = super<ColumnWithParent>.path()

    override fun getValue(thisRef: Any?, property: KProperty<*>): DataColumnGroup<T> = source.getValue(thisRef, property) as DataColumnGroup<T>

    override fun equals(other: Any?) = source.checkEquals(other)

    override fun hashCode() = source.hashCode()

    override fun resolveSingle(context: ColumnResolutionContext) = super<ColumnWithParent>.resolveSingle(context)

    override fun resolve(context: ColumnResolutionContext) = super<ColumnWithParent>.resolve(context)

    // region get column

    override fun columns() = super.columns().map { it.addParent(this) }

    override fun getColumnOrNull(index: Int) = super.getColumnOrNull(index)?.addParent(this)
    override fun getColumnOrNull(name: String) = super.getColumnOrNull(name)?.addParent(this)
    override fun <R> getColumnOrNull(column: ColumnReference<R>) = super.getColumnOrNull(column)?.addParent(this)
    override fun getColumnOrNull(path: ColumnPath) = path.fold(this as AnyCol?) { col, name -> col?.asColumnGroup()?.getColumnOrNull(name) }
    override fun <R> getColumnOrNull(column: ColumnSelector<T, R>) = getColumnOrNull(getColumnWithPath(column).path)?.cast<R>()

    override operator fun get(columnName: String): AnyCol = getColumn(columnName)
    override operator fun get(columnPath: ColumnPath): AnyCol = getColumn(columnPath)

    override operator fun <R> get(column: KProperty<R>): DataColumn<R> = get(column.columnName).cast()
    override operator fun <R> get(column: KProperty<DataRow<R>>): ColumnGroup<R> = get(column.columnName).asColumnGroup().cast()
    override operator fun <R> get(column: KProperty<DataFrame<R>>): FrameColumn<R> = get(column.columnName).asFrameColumn().castFrameColumn()

    override operator fun <C> get(columns: ColumnsSelector<T, C>): List<DataColumn<C>> = super.get(columns).map { it.addParent(this) }
    override operator fun <C> get(column: ColumnSelector<T, C>): DataColumn<C> = get(column as ColumnsSelector<T, C>).single()

    // endregion

    override fun get(firstIndex: Int, vararg otherIndices: Int) = ColumnGroupWithParent(parent, super.get(firstIndex, *otherIndices))
}
