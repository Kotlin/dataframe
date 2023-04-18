package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.columns.*
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupWithParent
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnGroupWithPathImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.missing.MissingDataColumn

private fun <T> DataFrame<T>.unbox(): DataFrame<T> = when (this) {
    is ColumnGroupWithParent -> source.unbox()
    is ColumnGroupWithPathImpl -> column.unbox()
    else -> this
}

internal abstract class DataFrameReceiverBase<T>(protected val df: DataFrame<T>) :
    DataFrameImpl<T>(df.columns(), df.nrow)

internal open class DataFrameReceiver<T>(
    source: DataFrame<T>,
    private val unresolvedColumnsPolicy: UnresolvedColumnsPolicy,
) : DataFrameReceiverBase<T>(source.unbox()), SingleColumn<DataRow<T>> {

    private fun <R> DataColumn<R>?.check(path: ColumnPath): DataColumn<R> =
        when (this) {
            null -> when (unresolvedColumnsPolicy) {
                UnresolvedColumnsPolicy.Create, UnresolvedColumnsPolicy.Skip -> MissingColumnGroup<Any>(
                    path,
                    this@DataFrameReceiver
                ).asDataColumn().cast()

                UnresolvedColumnsPolicy.Fail -> error("Column $path not found")
            }

            is MissingDataColumn -> this
            is ColumnGroup<*> -> ColumnGroupWithParent(null, this).asDataColumn().cast()
            else -> this
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

    override fun resolveSingleAfterTransform(
        context: ColumnResolutionContext,
        transformer: ColumnSetTransformer,
    ): ColumnWithPath<DataRow<T>>? =
        DataColumn.createColumnGroup(
            name = "",
            df = createColumnSet { df.columns().map { it.addPath() } }
                .let(transformer::transform).cast<T>()
                .resolve(context)
                .toDataFrame() as DataFrame<T>,
        ).addPath(emptyPath())

    override fun columns() =
        df.columns().map { if (it.isColumnGroup()) ColumnGroupWithParent(null, it.asColumnGroup()) else it }

    override fun columnNames() = df.columnNames()

    override fun columnTypes() = df.columnTypes()
}
