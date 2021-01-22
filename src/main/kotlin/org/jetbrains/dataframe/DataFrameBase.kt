package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataCol
import org.jetbrains.dataframe.api.columns.GroupedCol
import org.jetbrains.dataframe.api.columns.SingleColumn
import org.jetbrains.dataframe.api.columns.TableCol
import kotlin.reflect.KProperty

interface DataFrameBase<out T>: SingleColumn<DataRow<T>> {

    operator fun get(columnName: String): AnyCol
    fun tryGetColumn(columnName: String): AnyCol?
    fun <R> getColumn(columnName: String) = get(columnName) as DataCol<R>

    fun getGroup(columnName: String) = get(columnName).asGrouped()
    fun getGroup(columnPath: ColumnPath): GroupedCol<*> = get(columnPath).asGrouped()

    fun getTable(columnName: String) = get(columnName).asTable()
    fun getTable(columnPath: ColumnPath): TableCol<*> = get(columnPath).asTable()

    operator fun <R> get(column: ColumnReference<R>): DataCol<R>
    operator fun <R> get(column: ColumnReference<DataRow<R>>): GroupedCol<R>
    operator fun <R> get(column: ColumnReference<DataFrame<R>>): TableCol<R>

    operator fun get(columnPath: ColumnPath): AnyCol {

        var res: AnyCol? = null
        columnPath.forEach {
            if(res == null) res = this[it]
            else res = res!!.asGrouped()[it]
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