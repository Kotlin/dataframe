package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.pathOf
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
import org.jetbrains.kotlinx.dataframe.nrow

internal open class DataFrameReceiver<T>(
    val source: DataFrame<T>,
    private val unresolvedColumnsPolicy: UnresolvedColumnsPolicy
) : DataFrameImpl<T>(source.columns(), source.nrow), SingleColumn<DataRow<T>> {

    private fun <R> DataColumn<R>?.check(path: ColumnPath): DataColumn<R>? =
        when (this) {
            null -> when (unresolvedColumnsPolicy) {
                UnresolvedColumnsPolicy.Create, UnresolvedColumnsPolicy.Skip -> MissingColumnGroup<Any>(path, this@DataFrameReceiver).asDataColumn().cast()
                UnresolvedColumnsPolicy.Fail -> error("Column $path not found")
            }
            is MissingDataColumn -> this
            is ColumnGroup<*> -> ColumnGroupWithParent(null, this).asDataColumn().cast()
            else -> this
        }

    override fun getColumnOrNull(name: String) = source.getColumnOrNull(name).check(pathOf(name))
    override fun getColumnOrNull(index: Int) = source.getColumnOrNull(index).check(pathOf(""))

    override fun <R> getColumnOrNull(column: ColumnReference<R>): DataColumn<R>? {
        val context = ColumnResolutionContext(this, unresolvedColumnsPolicy)
        return column.resolveSingle(context).check(column.path())
    }

    override fun getColumnOrNull(path: ColumnPath) = super.getColumnOrNull(path).check(path)
    override fun <R> getColumnOrNull(column: ColumnSelector<T, R>) = getColumnsImpl(unresolvedColumnsPolicy, column).singleOrNull()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataRow<T>>? = DataColumn.createColumnGroup("", source).addPath(emptyPath(), source)

    override fun columns() = source.columns().map { if (it.isColumnGroup()) ColumnGroupWithParent(null, it.asColumnGroup()) else it }

    override fun columnNames() = source.columnNames()

    override fun columnTypes() = source.columnTypes()
}
