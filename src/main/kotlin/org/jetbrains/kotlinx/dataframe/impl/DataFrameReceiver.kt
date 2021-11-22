package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupWithParent
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingDataColumn

internal open class DataFrameReceiver<T>(val source: DataFrame<T>, private val allowMissingColumns: Boolean) : DataFrameImpl<T>(source.columns()), SingleColumn<DataRow<T>> {

    private fun <R> DataColumn<R>?.check(): DataColumn<R>? =
        when (this) {
            null -> if (allowMissingColumns) MissingColumnGroup<Any>().asDataColumn().cast() else null
            is MissingDataColumn -> this
            is ColumnGroup<*> -> ColumnGroupWithParent(null, this).asDataColumn().cast()
            else -> this
        }

    override fun getColumnOrNull(name: String) = source.getColumnOrNull(name).check()
    override fun getColumnOrNull(index: Int) = source.getColumnOrNull(index).check()
    override fun <R> getColumnOrNull(column: ColumnReference<R>) = source.getColumnOrNull(column).check()
    override fun getColumnOrNull(path: ColumnPath) = source.getColumnOrNull(path).check()
    override fun <R> getColumnOrNull(column: ColumnSelector<T, R>) = source.getColumnOrNull(column).check()

    override fun <R> resolve(reference: ColumnReference<R>): ColumnWithPath<R>? {
        val context = ColumnResolutionContext(this, if (allowMissingColumns) UnresolvedColumnsPolicy.Skip else UnresolvedColumnsPolicy.Fail)
        return reference.resolveSingle(context)
    }

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataRow<T>>? = DataColumn.createColumnGroup("", source).addPath(emptyPath(), source)

    override fun columns() = source.columns().map { if (it.isColumnGroup()) ColumnGroupWithParent(null, it.asColumnGroup()) else it }
}
