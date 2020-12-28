package org.jetbrains.dataframe.impl

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.MissingGroupColumn
import org.jetbrains.dataframe.MissingTableColumn
import org.jetbrains.dataframe.MissingValueColumn
import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.api.columns.TableColumn
import org.jetbrains.dataframe.impl.columns.GroupedColumnWithParent
import java.lang.Exception

internal fun <T> prepareForReceiver(df: DataFrameBase<T>) = DataFrameImpl<T>(df.columns().map { if(it.isGrouped()) GroupedColumnWithParent(null, it.asGrouped()) else it })

internal abstract class DataFrameReceiverBase<T>(protected val source: DataFrameBase<T>): DataFrameBase<T> by source

internal abstract class DataFrameReceiver<T>(source: DataFrameBase<T>, private val allowMissingColumns: Boolean): DataFrameReceiverBase<T>(prepareForReceiver(source)) {

    override fun getColumn(columnIndex: Int): DataCol {
        if(allowMissingColumns && columnIndex < 0 || columnIndex >= ncol) return MissingValueColumn<Any?>()
        return super.getColumn(columnIndex)
    }

    override operator fun get(columnName: String) = getColumnChecked(columnName) ?: MissingValueColumn<Any?>()

    fun <R> getColumnChecked(columnName: String): ColumnData<R>? {
        val col = source.tryGetColumn(columnName)
        if(col == null) {
            if(allowMissingColumns) return null
            throw Exception("Column not found: '$columnName'")
        }
        return col as ColumnData<R>
    }

    override operator fun <R> get(column: ColumnDef<R>): ColumnData<R> = getColumnChecked(column.name) ?: MissingValueColumn()
    override operator fun <R> get(column: ColumnDef<DataRow<R>>): GroupedColumn<R> = (getColumnChecked(column.name) ?: MissingGroupColumn<R>()) as GroupedColumn<R>
    override operator fun <R> get(column: ColumnDef<DataFrame<R>>): TableColumn<R> = (getColumnChecked(column.name) ?: MissingTableColumn<R>()) as TableColumn<R>
}