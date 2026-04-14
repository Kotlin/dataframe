package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDslDocs
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.DataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.DataFrameSize
import org.jetbrains.kotlinx.dataframe.impl.getColumnsImpl
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.impl.headPlusIterable
import org.jetbrains.kotlinx.dataframe.impl.schema.createEmptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.schema.createEmptyDataFrameOf
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KType

/**
 * Readonly interface for an ordered list of [columns][DataColumn].
 *
 * Columns in `DataFrame` have distinct non-empty [names][DataColumn.name] and equal [sizes][DataColumn.size].
 *
 * @param T Schema marker. It identifies column schema and is used to generate schema-specific extension properties for typed data access. It is covariant, so `DataFrame<A>` is assignable to variable of type `DataFrame<B>` if `A` is a subtype of `B`.
 */
@HasSchema(schemaArg = 0)
public interface DataFrame<out T> :
    Aggregatable<T>,
    ColumnsContainer<T> {

    public companion object {
        public val Empty: AnyFrame = DataFrameImpl<Unit>(emptyList(), 0)

        public fun empty(nrow: Int = 0): AnyFrame = if (nrow == 0) Empty else DataFrameImpl<Unit>(emptyList(), nrow)

        /**
         * Creates a DataFrame with empty columns (rows = 0).
         * Can be used as a "null object" in aggregation operations, operations that work on columns (select, reorder, ...)
         *
         */
        public inline fun <reified T> emptyOf(): DataFrame<T> = createEmptyDataFrameOf(T::class).cast()

        /**
         * Creates a DataFrame with empty columns (rows = 0).
         * Can be used as a "null object" in aggregation operations, operations that work on columns (select, reorder, ...)
         */
        public fun empty(schema: DataFrameSchema): AnyFrame = schema.createEmptyDataFrame()
    }

    // region columns

    public fun columnNames(): List<String>

    public fun columnTypes(): List<KType>

    // endregion

    // region rows

    /**
     * Returns the total number of rows of this [DataFrame].
     *
     * @return The number of rows in the [DataFrame].
     */
    @RequiredByIntellijPlugin
    public fun rowsCount(): Int

    public operator fun iterator(): Iterator<DataRow<T>> = rows().iterator()

    // endregion

    /**
     * Aggregates this [DataFrame] using the provided statistics
     * inside the [AggregateDsl].
     *
     * Returns a new [DataRow] with the aggregated values.
     *
     * [AggregateDsl] allows to compute statistics on the columns of this [DataFrame]
     * and store the results as a new column using [into][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into].
     *
     *
     * The resulting [DataRow] has the same structure as the original
     * [DataFrame];
     * instead of the groups, there are new columns of aggregated values created with [into][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into].
     *
     * You can use any of [DataFrame Aggregation Statistics][org.jetbrains.kotlinx.dataframe.aggregation.DataFrameAggregationStatistics]
     * or any custom aggregation function.
     *
     * Aggregated values can be either simple values, [data rows][org.jetbrains.kotlinx.dataframe.DataRow] or even
     * [data frames][org.jetbrains.kotlinx.dataframe.DataFrame]. Including them in the result using [into][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into] will lead
     * to creating [value column][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
     * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [frame column][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
     * in the resulting [DataRow] while preserving the original structure at higher levels.
     *
     *
     *
     *
     *
     * #### Example
     * ```kotlin
     * df.aggregate {
     *   // Сount rows within each group and store the result
     *   // into a new "total" column
     *   count() into "total"
     *
     *   // Compute the maximum in "age" column within each group
     *   // and store it into a new "maxAge" column
     *   max { age } into "maxAge"
     * }
     * ```
     *
     * @param body The aggregation logic defined using [AggregateDsl].
     * @return A new [DataRow] with the results of the aggregation.
     */
    @Refine
    @Interpretable("AggregateRow")
    public fun <R> aggregate(body: AggregateGroupedBody<T, R>): DataRow<T>

    // region get columns

    /**
     * Returns a list of columns selected by [columns], a [ColumnsSelectionDsl].
     *
     * NOTE: This doesn't work in [ColumnsSelectionDsl], use [ColumnsSelectionDsl.cols] to select columns by predicate.
     */
    override fun <C> get(columns: ColumnsSelector<T, C>): List<DataColumn<C>> =
        getColumnsImpl(UnresolvedColumnsPolicy.Fail, columns)

    // endregion

    // region get rows

    @RequiredByIntellijPlugin
    public operator fun get(index: Int): DataRow<T>

    public operator fun get(indices: Iterable<Int>): DataFrame<T> =
        columns().map { col -> col[indices] }.toDataFrame().cast()

    public operator fun get(range: IntRange): DataFrame<T> =
        if (range == indices()) this else columns().map { col -> col[range] }.toDataFrame().cast()

    public operator fun get(first: IntRange, vararg ranges: IntRange): DataFrame<T> =
        getRows(headPlusArray(first, ranges).asSequence().flatMap { it.asSequence() }.asIterable())

    public operator fun get(firstIndex: Int, vararg otherIndices: Int): DataFrame<T> =
        get(headPlusIterable(firstIndex, otherIndices.asIterable()))

    // endregion

    // region plus columns

    public operator fun plus(col: AnyBaseCol): DataFrame<T> = add(col)

    public operator fun plus(cols: Iterable<AnyBaseCol>): DataFrame<T> = (columns() + cols).toDataFrame().cast()

    // endregion
}

// region get columns

/**
 * Returns a list of columns selected by [columns], a [ColumnsSelectionDsl].
 */
public operator fun <T, C> DataFrame<T>.get(columns: ColumnsSelector<T, C>): List<DataColumn<C>> = this.get(columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public operator fun <T> DataFrame<T>.get(first: AnyColumnReference, vararg other: AnyColumnReference): DataFrame<T> =
    select { (listOf(first) + other).toColumnSet() }

public operator fun <T> DataFrame<T>.get(first: String, vararg other: String): DataFrame<T> =
    select { (listOf(first) + other).toColumnSet() }

public operator fun <T> DataFrame<T>.get(columnRange: ClosedRange<String>): DataFrame<T> =
    select { columnRange.start..columnRange.endInclusive }

// endregion

internal val ColumnsContainer<*>.ncol get() = columnsCount()
internal val AnyFrame.nrow get() = rowsCount()
internal val AnyFrame.indices get() = indices()
internal val AnyFrame.size: DataFrameSize get() = size()

public fun AnyFrame.size(): DataFrameSize = DataFrameSize(ncol, nrow)
