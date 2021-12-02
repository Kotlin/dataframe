package org.jetbrains.kotlinx.dataframe.impl.columns.missing

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.columns.DataColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import kotlin.reflect.KType

internal class MissingColumnGroup<T>(val path: ColumnPath, val host: ColumnsContainer<*>) : MissingDataColumn<DataRow<T>>(), DataColumnGroup<T> {

    override val df: DataFrame<T>
        get() = this

    override fun getColumnOrNull(name: String) = MissingColumnGroup<Any?>(path + name, host)

    override fun getColumnOrNull(index: Int) = MissingColumnGroup<Any?>(path + "", host)

    override fun columnsCount(): Int = 0

    override fun get(index: Int) = throw UnsupportedOperationException()

    override fun get(columnName: String) = throw UnsupportedOperationException()

    override fun rowsCount(): Int = 0

    override fun columns(): List<AnyCol> = emptyList()

    override fun columnNames(): List<String> = emptyList()

    override fun columnTypes(): List<KType> = emptyList()

    override fun getColumnIndex(name: String) = -1

    override fun kind() = super.kind()

    override fun get(firstIndex: Int, vararg otherIndices: Int) = throw UnsupportedOperationException()

    override fun distinct() = throw UnsupportedOperationException()

    override fun <C> values(byRow: Boolean, columns: ColumnsSelector<T, C>) = throw UnsupportedOperationException()

    override fun <R> aggregate(body: AggregateGroupedBody<T, R>) = throw UnsupportedOperationException()

    override fun rows(): Iterable<DataRow<T>> = throw UnsupportedOperationException()

    override fun rowsReversed(): Iterable<DataRow<T>> = throw UnsupportedOperationException()

    override fun <R> getColumnOrNull(column: ColumnReference<R>) = MissingColumnGroup<Any>(path + column.name(), host).asDataColumn().cast<R>()

    override fun getColumnOrNull(path: ColumnPath) = MissingColumnGroup<Any?>(this.path + path, host)

    override fun <R> getColumnOrNull(column: ColumnSelector<T, R>) = MissingColumnGroup<Any>(path + "", host).asDataColumn().cast<R>()

    override fun name(): String = path.name

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataRow<T>>? = when (context.unresolvedColumnsPolicy) {
        UnresolvedColumnsPolicy.Skip -> null
        UnresolvedColumnsPolicy.Create -> this.addPath(path, host)
        UnresolvedColumnsPolicy.Fail -> error("Failed to resolve column $path")
    }
}
