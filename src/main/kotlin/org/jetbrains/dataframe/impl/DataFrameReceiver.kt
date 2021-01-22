package org.jetbrains.dataframe.impl

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.MissingGroupCol
import org.jetbrains.dataframe.MissingTableCol
import org.jetbrains.dataframe.MissingValueCol
import org.jetbrains.dataframe.api.columns.DataCol
import org.jetbrains.dataframe.api.columns.GroupedCol
import org.jetbrains.dataframe.api.columns.TableCol
import org.jetbrains.dataframe.impl.columns.GroupedWithParentCol
import java.lang.Exception

internal fun <T> prepareForReceiver(df: DataFrameBase<T>) = DataFrameImpl<T>(df.columns().map { if(it.isGrouped()) GroupedWithParentCol(null, it.asGrouped()) else it })

internal abstract class DataFrameReceiverBase<T>(protected val source: DataFrameBase<T>): DataFrameBase<T> by source

internal abstract class DataFrameReceiver<T>(source: DataFrameBase<T>, private val allowMissingColumns: Boolean): DataFrameReceiverBase<T>(prepareForReceiver(source)) {

    override fun column(columnIndex: Int): AnyCol {
        if(allowMissingColumns && columnIndex < 0 || columnIndex >= ncol()) return MissingValueCol<Any?>()
        return super.column(columnIndex)
    }

    override operator fun get(columnName: String) = getColumnChecked(columnName) ?: MissingValueCol<Any?>()

    fun <R> getColumnChecked(columnName: String): DataCol<R>? {
        val col = source.tryGetColumn(columnName)
        if(col == null) {
            if(allowMissingColumns) return null
            throw Exception("Column not found: '$columnName'")
        }
        return col as DataCol<R>
    }

    override operator fun <R> get(column: ColumnReference<R>): DataCol<R> = getColumnChecked(column.name()) ?: MissingValueCol()
    override operator fun <R> get(column: ColumnReference<DataRow<R>>): GroupedCol<R> = (getColumnChecked(column.name()) ?: MissingGroupCol<R>()) as GroupedCol<R>
    override operator fun <R> get(column: ColumnReference<DataFrame<R>>): TableCol<R> = (getColumnChecked(column.name()) ?: MissingTableCol<R>()) as TableCol<R>
}