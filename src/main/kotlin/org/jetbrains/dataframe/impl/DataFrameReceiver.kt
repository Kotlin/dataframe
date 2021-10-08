package org.jetbrains.dataframe.impl

import org.jetbrains.dataframe.ColumnPath
import org.jetbrains.dataframe.ColumnResolutionContext
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.DataRow
import org.jetbrains.dataframe.UnresolvedColumnsPolicy
import org.jetbrains.dataframe.columns.*
import org.jetbrains.dataframe.impl.columns.ColumnGroupWithParent
import org.jetbrains.dataframe.impl.columns.asGroup
import org.jetbrains.dataframe.impl.columns.missing.MissingColumnGroup
import org.jetbrains.dataframe.impl.columns.missing.MissingFrameColumn
import org.jetbrains.dataframe.impl.columns.missing.MissingValueColumn
import org.jetbrains.dataframe.isGroup
import org.jetbrains.dataframe.pathOf

internal fun <T> prepareForReceiver(df: DataFrame<T>) = DataFrameImpl<T>(df.columns().map { if (it.isGroup()) ColumnGroupWithParent(null, it.asGroup()) else it })

internal abstract class DataFrameReceiverBase<T>(protected val source: DataFrame<T>) : DataFrame<T> by source

internal abstract class DataFrameReceiver<T>(source: DataFrame<T>, private val allowMissingColumns: Boolean) : DataFrameReceiverBase<T>(prepareForReceiver(source)) {

    override fun col(columnIndex: Int): AnyCol {
        if (allowMissingColumns && columnIndex < 0 || columnIndex >= ncol()) return MissingValueColumn<Any?>()
        return super.col(columnIndex)
    }

    override operator fun get(columnName: String) = getColumnChecked(pathOf(columnName)) ?: MissingValueColumn<Any?>()

    fun <R> getColumnChecked(path: ColumnPath): DataColumn<R>? {
        val col = source.tryGetColumn(path)
        if (col == null) {
            if (allowMissingColumns) return null
            throw Exception("Column not found: '$path'")
        }
        return col as DataColumn<R>
    }

    fun <R> getColumnChecked(reference: ColumnReference<R>): DataColumn<R>? {
        val context = ColumnResolutionContext(this, if (allowMissingColumns) UnresolvedColumnsPolicy.Skip else UnresolvedColumnsPolicy.Fail)
        return reference.resolveSingle(context)?.data
    }

    override operator fun <R> get(column: ColumnReference<R>): DataColumn<R> = getColumnChecked(column) ?: MissingValueColumn()
    override operator fun <R> get(column: ColumnReference<DataRow<R>>): ColumnGroup<R> = (getColumnChecked(column) ?: MissingColumnGroup<R>()) as ColumnGroup<R>
    override operator fun <R> get(column: ColumnReference<DataFrame<R>>): FrameColumn<R> = (getColumnChecked(column) ?: MissingFrameColumn<R>()) as FrameColumn<R>
}
