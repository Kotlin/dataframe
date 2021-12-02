package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.DataFrameSize
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.EmptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.resolveSingle
import org.jetbrains.kotlinx.dataframe.impl.getColumnsImpl
import org.jetbrains.kotlinx.dataframe.impl.headPlusIterable
import kotlin.reflect.KType

public interface DataFrame<out T> : Aggregatable<T>, ColumnsContainer<T> {

    public companion object {
        public val Empty: AnyFrame = EmptyDataFrame<Unit>(0)
        public fun empty(nrow: Int = 0): AnyFrame = if (nrow == 0) Empty else EmptyDataFrame<Unit>(nrow)
    }

    // region columns

    override fun columns(): List<AnyCol>

    public fun columnNames(): List<String>

    public fun columnTypes(): List<KType>

    override fun columnsCount(): Int

    // endregion

    // region rows

    public fun rowsCount(): Int
    public fun rows(): Iterable<DataRow<T>>
    public fun rowsReversed(): Iterable<DataRow<T>>
    public fun indices(): IntRange = 0 until nrow

    // endregion

    // region values

    public fun <C> values(byRow: Boolean = false, columns: ColumnsSelector<T, C>): Sequence<C>
    public fun values(byRows: Boolean = false): Sequence<Any?> = values(byRows) { all() }

    public fun <C> valuesNotNull(byRow: Boolean = false, columns: ColumnsSelector<T, C?>): Sequence<C> = values(byRow, columns).filterNotNull()
    public fun valuesNotNull(byRow: Boolean = false): Sequence<Any> = valuesNotNull(byRow) { all() }

    // endregion

    // region get columns

    override operator fun <C> get(columns: ColumnsSelector<T, C>): List<DataColumn<C>> = getColumnsImpl(UnresolvedColumnsPolicy.Fail, columns)
    public operator fun get(first: Column, vararg other: Column): DataFrame<T> = select(listOf(first) + other)
    public operator fun get(first: String, vararg other: String): DataFrame<T> = select(listOf(first) + other)
    public operator fun get(columnRange: ClosedRange<String>): DataFrame<T> = select { columnRange.start..columnRange.endInclusive }

    public fun getColumnIndex(name: String): Int

    // endregion

    // region get rows

    public operator fun get(index: Int): DataRow<T> = DataRowImpl(index, this)
    public operator fun get(indices: Iterable<Int>): DataFrame<T> = getRows(indices)
    public operator fun get(range: IntRange): DataFrame<T> = getRows(range)
    public operator fun get(vararg ranges: IntRange): DataFrame<T> = getRows(ranges.asSequence().flatMap { it.asSequence() }.asIterable())
    public operator fun get(firstIndex: Int, vararg otherIndices: Int): DataFrame<T> = get(headPlusIterable(firstIndex, otherIndices.asIterable()))

    // endregion

    // region plus columns

    public operator fun plus(col: AnyCol): DataFrame<T> = add(col)
    public operator fun plus(cols: Iterable<AnyCol>): DataFrame<T> = (columns() + cols).toDataFrame().cast()

    // endregion

    // region rows iterator

    public operator fun iterator(): Iterator<DataRow<T>> = rows().iterator()

    // endregion

    // region aggregate

    public fun <R> aggregate(body: AggregateGroupedBody<T, R>): DataRow<T>

    // endregion

    override fun <R> resolve(reference: ColumnReference<R>): ColumnWithPath<R>? = reference.resolveSingle(this, UnresolvedColumnsPolicy.Skip)

    override fun asColumnGroup(name: String): ColumnGroup<*> = DataColumn.createColumnGroup(name, this)
}

internal val ColumnsContainer<*>.ncol get() = columnsCount()
internal val AnyFrame.nrow get() = rowsCount()
internal val AnyFrame.indices get() = indices()
internal val AnyFrame.size: DataFrameSize get() = size()

public fun AnyFrame.size(): DataFrameSize = DataFrameSize(ncol, nrow)
