package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupWithParent
import org.jetbrains.kotlinx.dataframe.impl.columns.asGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingValueColumn
import org.jetbrains.kotlinx.dataframe.isGroup
import org.jetbrains.kotlinx.dataframe.pathOf

// TODO: don't copy columns, just wrap original DataFrame
internal fun <T> prepareForReceiver(df: DataFrame<T>) = DataFrameImpl<T>(df.columns().map { if (it.isGroup()) ColumnGroupWithParent(null, it.asGroup()) else it })

// Needed just to pass converted constructor argument into 'implement by'
internal open class DataFrameReceiverBase<T>(protected val source: DataFrame<T>) : DataFrame<T> by source

internal abstract class DataFrameReceiver<T>(source: DataFrame<T>, private val allowMissingColumns: Boolean) : DataFrameReceiverBase<T>(
    prepareForReceiver(source)
) {

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
