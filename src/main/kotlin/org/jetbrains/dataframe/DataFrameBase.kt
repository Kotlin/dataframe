package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.api.columns.MapColumn
import org.jetbrains.dataframe.api.columns.SingleColumn
import org.jetbrains.dataframe.api.columns.TableColumn
import kotlin.reflect.KProperty

interface DataFrameBase<out T>: SingleColumn<DataRow<T>> {

    operator fun get(columnName: String): AnyCol
    fun tryGetColumn(columnName: String): AnyCol?
    fun <R> getColumn(columnName: String) = get(columnName) as DataColumn<R>

    fun getGroup(columnName: String) = get(columnName).asGroup()
    fun getGroup(columnPath: ColumnPath): MapColumn<*> = get(columnPath).asGroup()

    fun getTable(columnName: String) = get(columnName).asTable()
    fun getTable(columnPath: ColumnPath): TableColumn<*> = get(columnPath).asTable()

    operator fun <R> get(column: ColumnReference<R>): DataColumn<R>
    operator fun <R> get(column: ColumnReference<DataRow<R>>): MapColumn<R>
    operator fun <R> get(column: ColumnReference<DataFrame<R>>): TableColumn<R>

    operator fun get(columnPath: ColumnPath): AnyCol {

        var res: AnyCol? = null
        columnPath.forEach {
            if(res == null) res = this[it]
            else res = res!!.asGroup()[it]
        }
        return res!!
    }

    operator fun get(index: Int): DataRow<T>
    fun column(columnIndex: Int): AnyCol
    fun columns(): List<AnyCol>
    fun ncol(): Int
}

operator fun <T, R> DataFrameBase<T>.get(column: KProperty<DataFrame<R>>) = get(column.toColumnDef())

@JvmName("getT")
operator fun <T, R> DataFrameBase<T>.get(column: KProperty<DataRow<R>>) = get(column.toColumnDef())

operator fun <T, R> DataFrameBase<T>.get(column: KProperty<R>) = get(column.toColumnDef())