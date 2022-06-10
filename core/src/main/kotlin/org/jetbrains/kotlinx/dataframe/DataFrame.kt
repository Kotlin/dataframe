package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.DataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.DataFrameSize
import org.jetbrains.kotlinx.dataframe.impl.getColumnsImpl
import org.jetbrains.kotlinx.dataframe.impl.headPlusIterable
import org.jetbrains.kotlinx.dataframe.impl.schema.createEmptyDataFrameOf
import kotlin.reflect.KType

/**
 * Readonly interface for an ordered list of [columns][DataColumn].
 *
 * Columns in `DataFrame` have distinct non-empty [names][DataColumn.name] and equal [sizes][DataColumn.size].
 *
 * @param T Schema marker. It identifies column schema and is used to generate schema-specific extension properties for typed data access. It is covariant, so `DataFrame<A>` is assignable to variable of type `DataFrame<B>` if `A` is a subtype of `B`.
 */
public interface DataFrame<out T> : Aggregatable<T>, ColumnsContainer<T> {

    public companion object {
        public val Empty: AnyFrame = DataFrameImpl<Unit>(emptyList(), 0)
        public fun empty(nrow: Int = 0): AnyFrame = if (nrow == 0) Empty else DataFrameImpl<Unit>(emptyList(), nrow)

        public inline fun <reified T> emptyOf(): DataFrame<T> = createEmptyDataFrameOf(T::class).cast()
    }

    // region columns

    public fun columnNames(): List<String>

    public fun columnTypes(): List<KType>

    // endregion

    // region rows

    public fun rowsCount(): Int

    public operator fun iterator(): Iterator<DataRow<T>> = rows().iterator()

    // endregion

    public fun <R> aggregate(body: AggregateGroupedBody<T, R>): DataRow<T>

    // region get columns

    override operator fun <C> get(columns: ColumnsSelector<T, C>): List<DataColumn<C>> = getColumnsImpl(UnresolvedColumnsPolicy.Fail, columns)
    public operator fun get(first: Column, vararg other: Column): DataFrame<T> = select(listOf(first) + other)
    public operator fun get(first: String, vararg other: String): DataFrame<T> = select(listOf(first) + other)
    public operator fun get(columnRange: ClosedRange<String>): DataFrame<T> = select { columnRange.start..columnRange.endInclusive }

    // endregion

    // region get rows

    public operator fun get(index: Int): DataRow<T>
    public operator fun get(indices: Iterable<Int>): DataFrame<T> = getRows(indices)
    public operator fun get(range: IntRange): DataFrame<T> = getRows(range)
    public operator fun get(vararg ranges: IntRange): DataFrame<T> = getRows(ranges.asSequence().flatMap { it.asSequence() }.asIterable())
    public operator fun get(firstIndex: Int, vararg otherIndices: Int): DataFrame<T> = get(headPlusIterable(firstIndex, otherIndices.asIterable()))

    // endregion

    // region plus columns

    public operator fun plus(col: AnyBaseCol): DataFrame<T> = add(col)
    public operator fun plus(cols: Iterable<AnyBaseCol>): DataFrame<T> = (columns() + cols).toDataFrame().cast()

    // endregion
}

internal val ColumnsContainer<*>.ncol get() = columnsCount()
internal val AnyFrame.nrow get() = rowsCount()
internal val AnyFrame.indices get() = indices()
internal val AnyFrame.size: DataFrameSize get() = size()

public fun AnyFrame.size(): DataFrameSize = DataFrameSize(ncol, nrow)
