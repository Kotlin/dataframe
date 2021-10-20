package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import kotlin.reflect.KProperty

public interface ColumnsContainer<out T> : SingleColumn<DataRow<T>> {

    public operator fun get(columnName: String): AnyCol
    public fun tryGetColumn(columnName: String): AnyCol?
    public fun tryGetColumn(path: ColumnPath): AnyCol?

    public operator fun <R> get(column: ColumnReference<R>): DataColumn<R>
    public operator fun <R> get(column: ColumnReference<DataRow<R>>): ColumnGroup<R>
    public operator fun <R> get(column: ColumnReference<DataFrame<R>>): FrameColumn<R>

    public operator fun <R> get(column: KProperty<R>): DataColumn<R> = get(column.name) as DataColumn<R>
    public operator fun <R> get(column: KProperty<DataRow<R>>): ColumnGroup<R> = get(column.name) as ColumnGroup<R>
    public operator fun <R> get(column: KProperty<DataFrame<R>>): FrameColumn<R> = get(column.name) as FrameColumn<R>

    public operator fun <C> get(columns: ColumnsSelector<T, C>): List<DataColumn<C>>
    public operator fun <C> get(columns: ColumnSelector<T, C>): DataColumn<C> = get(columns as ColumnsSelector<T, C>).single()

    public fun <R> getColumn(column: ColumnReference<R>): DataColumn<R> = get(column)
    public fun <R> getColumn(column: ColumnReference<DataRow<R>>): ColumnGroup<R> = get(column)
    public fun <R> getColumn(column: ColumnReference<DataFrame<R>>): FrameColumn<R> = get(column)
    public fun <R> getColumn(name: String): DataColumn<R> = get(name) as DataColumn<R>
    public fun <R> getColumn(path: ColumnPath): DataColumn<R> = get(path) as DataColumn<R>
    public fun <R> getColumn(index: Int): DataColumn<R> = col(index) as DataColumn<R>

    public fun hasColumn(columnName: String): Boolean = tryGetColumn(columnName) != null

    public operator fun get(columnPath: ColumnPath): AnyCol {
        require(columnPath.isNotEmpty()) { "ColumnPath is empty" }
        var res: AnyCol? = null
        columnPath.forEach {
            if (res == null) res = this[it]
            else res = res!!.asColumnGroup()[it]
        }
        return res!!
    }

    public operator fun get(index: Int): DataRow<T>
    public fun col(columnIndex: Int): AnyCol
    public fun columns(): List<AnyCol>
    public fun ncol(): Int
}
