package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.castFrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import kotlin.reflect.KProperty

public interface ColumnsContainer<out T> {

    public fun columns(): List<AnyCol>
    public fun ncol(): Int

    public fun getColumn(index: Int): AnyCol
    public fun getColumnOrNull(name: String): AnyCol?

    public operator fun get(columnName: String): AnyCol = getColumn(columnName)
    public operator fun get(columnPath: ColumnPath): AnyCol = getColumn(columnPath)

    public operator fun <R> get(column: ColumnReference<R>): DataColumn<R> = getColumn(column)
    public operator fun <R> get(column: ColumnReference<DataRow<R>>): ColumnGroup<R> = getColumn(column)
    public operator fun <R> get(column: ColumnReference<DataFrame<R>>): FrameColumn<R> = getColumn(column)

    public operator fun <R> get(column: KProperty<R>): DataColumn<R> = get(column.name).cast()
    public operator fun <R> get(column: KProperty<DataRow<R>>): ColumnGroup<R> = get(column.name).asColumnGroup().cast()
    public operator fun <R> get(column: KProperty<DataFrame<R>>): FrameColumn<R> = get(column.name).asFrameColumn().castFrameColumn()

    public operator fun <C> get(columns: ColumnsSelector<T, C>): List<DataColumn<C>>
    public operator fun <C> get(column: ColumnSelector<T, C>): DataColumn<C> = get(column as ColumnsSelector<T, C>).single()

    public fun <R> resolve(reference: ColumnReference<R>): ColumnWithPath<R>?

    public fun asColumnGroup(): ColumnGroup<*>
}

public fun <T> ColumnsContainer<T>.getColumn(name: String): AnyCol = getColumnOrNull(name) ?: throw IllegalArgumentException("Column not found: '$name'")

public fun <T, R> ColumnsContainer<T>.getColumn(column: ColumnReference<DataFrame<R>>): FrameColumn<R> = getColumnOrNull(column)?.asFrameColumn() ?: throw IllegalArgumentException("FrameColumn not found: '$column'")

public fun <T, R> ColumnsContainer<T>.getColumn(column: ColumnReference<DataRow<R>>): ColumnGroup<R> = getColumnOrNull(column)?.asColumnGroup() ?: throw IllegalArgumentException("ColumnGroup not found: '$column'")

public fun <T, R> ColumnsContainer<T>.getColumn(column: ColumnReference<R>): DataColumn<R> = getColumnOrNull(column) ?: throw IllegalArgumentException("Column not found: '$column'")

public fun <T> ColumnsContainer<T>.getColumn(path: ColumnPath): AnyCol = getColumnOrNull(path) ?: throw IllegalArgumentException("Column not found: '$path'")

public fun <T> ColumnsContainer<T>.getColumnGroup(index: Int): ColumnGroup<*> = getColumn(index).asColumnGroup()

public fun <T> ColumnsContainer<T>.getColumnGroup(name: String): ColumnGroup<*> = getColumn(name).asColumnGroup()

public fun <T> ColumnsContainer<T>.getColumnGroupOrNull(name: String): ColumnGroup<*>? = getColumnOrNull(name)?.asColumnGroup()

public fun <T, R> ColumnsContainer<T>.getColumnOrNull(column: ColumnReference<R>): DataColumn<R>? = resolve(column)?.data

public fun <T> ColumnsContainer<T>.getColumnOrNull(path: ColumnPath): AnyCol? =
    when (path.size) {
        0 -> asColumnGroup().asDataColumn()
        1 -> getColumnOrNull(path[0])
        else -> path.dropLast(1).fold(this as AnyFrame?) { df, name -> df?.getColumnOrNull(name) as? AnyFrame? }
            ?.getColumnOrNull(path.last())
    }
