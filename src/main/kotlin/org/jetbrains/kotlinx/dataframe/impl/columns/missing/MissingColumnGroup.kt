package org.jetbrains.kotlinx.dataframe.impl.columns.missing

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.impl.columns.DataColumnGroup

internal class MissingColumnGroup<T> : MissingDataColumn<DataRow<T>>(), DataColumnGroup<T> {

    override val df: DataFrame<T>
        get() = throw UnsupportedOperationException()

    override fun getColumnOrNull(name: String): AnyCol? {
        return null
    }

    override fun getColumn(columnIndex: Int) = MissingValueColumn<Any?>()

    override fun ncol(): Int = 0

    override fun get(index: Int) = throw UnsupportedOperationException()

    override fun get(columnName: String) = throw UnsupportedOperationException()

    override fun nrow(): Int = 0

    override fun columns(): List<AnyCol> = emptyList()

    override fun columnNames(): List<String> = emptyList()

    override fun getColumnIndex(name: String) = -1

    override fun kind() = super.kind()

    override fun get(firstIndex: Int, vararg otherIndices: Int) = throw UnsupportedOperationException()

    override fun distinct() = throw UnsupportedOperationException()

    override fun <C> values(byRow: Boolean, columns: ColumnsSelector<T, C>) = throw UnsupportedOperationException()

    override fun <R> aggregate(body: AggregateGroupedBody<T, R>) = throw UnsupportedOperationException()

    override fun rows(): Iterable<DataRow<T>> = throw UnsupportedOperationException()

    override fun rowsReversed(): Iterable<DataRow<T>> = throw UnsupportedOperationException()
}
