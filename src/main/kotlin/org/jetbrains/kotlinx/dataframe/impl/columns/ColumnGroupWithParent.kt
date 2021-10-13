package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.MapColumnReference
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import kotlin.reflect.KProperty

internal class ColumnGroupWithParent<T>(override val parent: MapColumnReference?, override val source: ColumnGroup<T>) : ColumnWithParent<DataRow<T>>, DataColumnGroup<T> by (source as DataColumnGroup<T>) {

    override fun path() = super<ColumnWithParent>.path()

    override fun getValue(thisRef: Any?, property: KProperty<*>): DataColumnGroup<T> = source.getValue(thisRef, property) as DataColumnGroup<T>

    private fun <T> BaseColumn<T>.addParent(parent: ColumnGroup<*>) = (this as DataColumnInternal<T>).addParent(parent)

    override fun get(columnName: String): AnyCol {
        val col = df[columnName]
        return col.addParent(this)
    }
    override fun <R> get(column: ColumnReference<R>) = df[column].addParent(this)
    override fun <R> get(column: ColumnReference<DataRow<R>>) = df[column].addParent(this) as ColumnGroup<R>
    override fun columns() = df.columns().map { it.addParent(this) }
    override fun col(columnIndex: Int) = df.col(columnIndex).addParent(this)

    override fun equals(other: Any?) = source.checkEquals(other)

    override fun hashCode() = source.hashCode()

    override fun resolveSingle(context: ColumnResolutionContext) = super<ColumnWithParent>.resolveSingle(context)

    override fun resolve(context: ColumnResolutionContext) = super<ColumnWithParent>.resolve(context)
}
