package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.asGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asTable
import kotlin.reflect.KProperty

public interface DataFrameBase<out T> : SingleColumn<DataRow<T>> {

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
        var res: AnyCol? = null
        columnPath.forEach {
            if (res == null) res = this[it]
            else res = res!!.asGroup()[it]
        }
        return res!!
    }

    public operator fun get(index: Int): DataRow<T>
    public fun col(columnIndex: Int): AnyCol
    public fun columns(): List<AnyCol>
    public fun ncol(): Int
    public fun nrow(): Int
    public fun rows(): Iterable<DataRow<T>>
    public fun rowsReversed(): Iterable<DataRow<T>>
}

public fun <T> DataFrameBase<T>.frameColumn(columnPath: ColumnPath): FrameColumn<*> = get(columnPath).asTable()

public fun <T> DataFrameBase<T>.frameColumn(columnName: String): FrameColumn<*> = get(columnName).asTable()

public fun <T> DataFrameBase<T>.tryGetColumn(columnIndex: Int): AnyCol? = if (columnIndex in 0 until ncol()) col(columnIndex) else null

public fun <T> DataFrameBase<T>.getOrNull(index: Int): DataRow<T>? = if (index < 0 || index >= nrow()) null else get(index)

public fun <T> DataFrameBase<T>.getColumnGroup(columnPath: ColumnPath): ColumnGroup<*> = get(columnPath).asGroup()

public fun <T> DataFrameBase<T>.getColumnGroup(columnName: String): ColumnGroup<*> = get(columnName).asGroup()
