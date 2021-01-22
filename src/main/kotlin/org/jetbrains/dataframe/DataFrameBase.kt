package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.api.columns.SingleColumn
import org.jetbrains.dataframe.api.columns.TableColumn
import kotlin.reflect.KProperty

interface DataFrameBase<out T>: SingleColumn<DataRow<T>> {

    operator fun get(columnName: String): DataCol
    fun tryGetColumn(columnName: String): DataCol?
    fun <R> getColumn(columnName: String) = get(columnName) as ColumnData<R>

    fun getGroup(columnName: String) = get(columnName).asGrouped()
    fun getGroup(columnPath: ColumnPath): GroupedColumn<*> = get(columnPath).asGrouped()

    fun getTable(columnName: String) = get(columnName).asTable()
    fun getTable(columnPath: ColumnPath): TableColumn<*> = get(columnPath).asTable()

    operator fun <R> get(column: ColumnDef<R>): ColumnData<R>
    operator fun <R> get(column: ColumnDef<DataRow<R>>): GroupedColumn<R>
    operator fun <R> get(column: ColumnDef<DataFrame<R>>): TableColumn<R>

    operator fun get(columnPath: ColumnPath): DataCol {

        var res: DataCol? = null
        columnPath.forEach {
            if(res == null) res = this[it]
            else res = res!!.asGrouped()[it]
        }
        return res!!
    }

    operator fun get(index: Int): DataRow<T>
    fun column(columnIndex: Int): DataCol
    fun columns(): List<DataCol>
    fun ncol(): Int
}

operator fun <T, R> DataFrameBase<T>.get(column: KProperty<DataFrame<R>>) = get(column.toColumnDef())

@JvmName("getT")
operator fun <T, R> DataFrameBase<T>.get(column: KProperty<DataRow<R>>) = get(column.toColumnDef())

operator fun <T, R> DataFrameBase<T>.get(column: KProperty<R>) = get(column.toColumnDef())