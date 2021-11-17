package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import kotlin.reflect.KProperty

public interface ColumnsContainer<out T> {

    public fun tryGetColumn(columnName: String): AnyCol?
    public fun tryGetColumn(path: ColumnPath): AnyCol?

    public operator fun get(columnName: String): AnyCol
    public operator fun get(columnPath: ColumnPath): AnyCol = tryGetColumn(columnPath)!!

    public operator fun <R> get(column: ColumnReference<R>): DataColumn<R>
    public operator fun <R> get(column: ColumnReference<DataRow<R>>): ColumnGroup<R>
    public operator fun <R> get(column: ColumnReference<DataFrame<R>>): FrameColumn<R>

    public operator fun <R> get(column: KProperty<R>): DataColumn<R> = get(column.name) as DataColumn<R>
    public operator fun <R> get(column: KProperty<DataRow<R>>): ColumnGroup<R> = get(column.name) as ColumnGroup<R>
    public operator fun <R> get(column: KProperty<DataFrame<R>>): FrameColumn<R> = get(column.name) as FrameColumn<R>

    public operator fun <C> get(columns: ColumnsSelector<T, C>): List<DataColumn<C>>
    public operator fun <C> get(column: ColumnSelector<T, C>): DataColumn<C> = get(column as ColumnsSelector<T, C>).single()

    public fun <R> getColumn(column: ColumnReference<R>): DataColumn<R> = get(column)
    public fun <R> getColumn(column: ColumnReference<DataRow<R>>): ColumnGroup<R> = get(column)
    public fun <R> getColumn(column: ColumnReference<DataFrame<R>>): FrameColumn<R> = get(column)
    public fun getColumn(name: String): AnyCol = get(name)
    public fun getColumn(path: ColumnPath): AnyCol = get(path)
    public fun getColumn(index: Int): AnyCol
    public fun getColumnGroup(index: Int): ColumnGroup<*> = getColumn(index).asColumnGroup()
    public fun getColumnGroup(name: String): ColumnGroup<*> = getColumn(name).asColumnGroup()

    public operator fun get(index: Int): DataRow<T>
    public fun columns(): List<AnyCol>
    public fun ncol(): Int
}
