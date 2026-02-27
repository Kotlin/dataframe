package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.toPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupWithParent
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupWithPathImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingDataColumn
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.collections.map

private fun <T> DataFrame<T>.unbox(): DataFrame<T> =
    when (this) {
        is ColumnGroupWithParent -> source.unbox()
        is ColumnGroupWithPathImpl -> column.unbox()
        else -> this
    }

internal abstract class DataFrameReceiverBase<T>(protected val df: DataFrame<T>) :
    DataFrameImpl<T>(df.columns(), df.nrow)

internal open class DataFrameReceiver<T>(
    source: DataFrame<T>,
    private val unresolvedColumnsPolicy: UnresolvedColumnsPolicy,
) : DataFrameReceiverBase<T>(source.unbox()),
    SingleColumn<DataRow<T>> {

    private fun <R> DataColumn<R>?.check(path: ColumnPath): DataColumn<R> =
        when (this) {
            null -> when (unresolvedColumnsPolicy) {
                UnresolvedColumnsPolicy.Create, UnresolvedColumnsPolicy.Skip ->
                    MissingColumnGroup<Any>(
                        path = path,
                        host = this@DataFrameReceiver,
                    ).asDataColumn().cast()

                UnresolvedColumnsPolicy.Fail -> error(formatMissingColumnMessage(path))
            }

            is MissingDataColumn -> this

            is ColumnGroup<*> -> ColumnGroupWithParent(null, this).asDataColumn().cast()

            else -> this
        }

    // it's strange that we have to reverse-search why the column is missing
    // would be nice to "fail fast" exactly where resolve failed, knowing the current path and parent.
    // but it's very unclear what to do with resolveSingle.
    // at first glance: a lot of changes.
    private fun formatMissingColumnMessage(path: ColumnPath): String {
        val fullPath = path.joinToString()

        for (index in path.indices) {
            val currentPath = path.slice(0..index).toPath()
            val currentPathString = currentPath.joinToString()
            val column = df.getColumnOrNull(currentPath)
            if (column == null) {
                return if (index == 0) {
                    "Column '$currentPathString' not found among ${df.columnNames()}."
                } else {
                    val parentPath = currentPath.dropLast()
                    val parentPathString = parentPath.joinToString()
                    val parentColumn = df.getColumnOrNull(parentPath)
                    if (parentColumn != null && parentColumn.isColumnGroup()) {
                        "Column '$currentPathString' not found among columns of '$parentPathString': ${parentColumn.columnNames()}."
                    } else {
                        "Column '$currentPathString' not found among ${df.columnNames()}."
                    }
                }
            }

            if (index != path.lastIndex) {
                if (!column.isColumnGroup()) {
                    return "Column '$fullPath' cannot be resolved: '$currentPathString' is not a column group."
                }
            }
        }
        return "Column '$fullPath' not found among ${df.columnNames()}."
    }

    override fun getColumnOrNull(name: String) = df.getColumnOrNull(name).check(pathOf(name))

    override fun getColumnOrNull(index: Int) = df.getColumnOrNull(index).check(pathOf(""))

    override fun <R> getColumnOrNull(column: ColumnReference<R>): DataColumn<R>? {
        val context = ColumnResolutionContext(this, unresolvedColumnsPolicy)
        return column.resolveSingle(context).check(column.path())
    }

    override fun getColumnOrNull(path: ColumnPath) = super.getColumnOrNull(path).check(path)

    override fun <R> getColumnOrNull(column: ColumnSelector<T, R>) =
        getColumnsImpl(unresolvedColumnsPolicy, column).singleOrNull()

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataRow<T>>? =
        DataColumn.createColumnGroup("", df).addPath(emptyPath())

    override fun columns() =
        df.columns().map {
            if (it.isColumnGroup()) ColumnGroupWithParent(null, it) else it
        }

    override fun columnNames() = df.columnNames()

    override fun columnTypes() = df.columnTypes()
}
