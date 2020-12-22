package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.api.columns.SingleColumn
import org.jetbrains.dataframe.api.columns.TableColumn
import kotlin.reflect.KProperty

interface DataFrameBase<out T>: SingleColumn<DataFrameRow<T>> {

    operator fun get(columnName: String): DataCol
    fun tryGetColumn(columnName: String): DataCol?

    fun getGroup(columnName: String) = get(columnName).asGrouped()
    fun getGroup(columnPath: ColumnPath): GroupedColumn<*> = get(columnPath).asGrouped()

    fun getTable(columnName: String) = get(columnName).asTable()
    fun getTable(columnPath: ColumnPath): TableColumn<*> = get(columnPath).asTable()

    operator fun <R> get(column: ColumnDef<R>): ColumnData<R>
    operator fun <R> get(column: ColumnDef<DataFrameRow<R>>): GroupedColumn<R>
    operator fun <R> get(column: ColumnDef<DataFrame<R>>): TableColumn<R>

    operator fun get(index: Int): DataFrameRow<T>
    fun getColumn(columnIndex: Int): DataCol
    fun columns(): List<DataCol>
    val ncol: Int
}

operator fun <T, R> DataFrameBase<T>.get(column: KProperty<DataFrame<R>>) = get(column.toColumnDef())

@JvmName("getT")
operator fun <T, R> DataFrameBase<T>.get(column: KProperty<DataFrameRow<R>>) = get(column.toColumnDef())

operator fun <T, R> DataFrameBase<T>.get(column: KProperty<R>) = get(column.toColumnDef())