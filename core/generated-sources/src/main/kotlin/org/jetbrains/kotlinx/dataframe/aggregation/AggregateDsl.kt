package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.maxFor
import org.jetbrains.kotlinx.dataframe.api.maxOf
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.meanFor
import org.jetbrains.kotlinx.dataframe.api.meanOf
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianFor
import org.jetbrains.kotlinx.dataframe.api.medianOf
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.minFor
import org.jetbrains.kotlinx.dataframe.api.minOf
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileFor
import org.jetbrains.kotlinx.dataframe.api.percentileOf
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.stdFor
import org.jetbrains.kotlinx.dataframe.api.stdOf
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.sumFor
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.aggregation.ValueWithDefault
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.internal
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

/**
 * ### [<code>DataFrame</code>][DataFrame] aggregation statistics
 *
 * Predefined shortcuts for the most common statistical aggregation operations on [<code>DataFrame</code>][DataFrame].
 *
 * * [<code>count</code>][DataFrame.count] — calculate the number of rows
 *   (optionally counting only rows that satisfy the given predicate);
 * * [<code>max</code>][DataFrame.max] / [<code>maxOf</code>][DataFrame.maxOf] / [<code>maxFor</code>][DataFrame.maxFor] —
 *   calculate the maximum of all values on the selected columns / by a row expression /
 *   for each of the selected columns;
 * * [<code>min</code>][DataFrame.min] / [<code>minOf</code>][DataFrame.minOf] / [<code>minFor</code>][DataFrame.minFor] —
 *   calculate the minimum of all values on the selected columns / by a row expression /
 *   for each of the selected columns;
 * * [<code>sum</code>][DataFrame.sum] / [<code>sumOf</code>][DataFrame.sumOf] / [<code>sumFor</code>][DataFrame.sumFor] —
 *   calculate the sum of all values on the selected columns / by a row expression /
 *   for each of the selected columns;
 * * [<code>mean</code>][DataFrame.mean] / [<code>meanOf</code>][DataFrame.meanOf] / [<code>meanFor</code>][DataFrame.meanFor] —
 *   calculate the mean (average) of all values on the selected columns / by a row expression /
 *   for each of the selected columns;
 * * [<code>std</code>][DataFrame.std] / [<code>stdOf</code>][DataFrame.stdOf] / [<code>stdFor</code>][DataFrame.stdFor] —
 *   calculate the standard deviation of all values on the selected columns / by a row expression /
 *   for each of the selected columns;
 * * [<code>median</code>][DataFrame.median] / [<code>medianOf</code>][DataFrame.medianOf] / [<code>medianFor</code>][DataFrame.medianFor] —
 *   calculate the median of all values on the selected columns / by a row expression /
 *   for each of the selected columns;
 * * [<code>percentile</code>][DataFrame.percentile] / [<code>percentileOf</code>][DataFrame.percentileOf] / [<code>percentileFor</code>][DataFrame.percentileFor] —
 *   calculate a specified percentile of all values on the selected columns / by a row expression /
 *   for each of the selected columns.
 *
 * For more information: [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 */
internal typealias DataFrameAggregationStatistics = Nothing

/**
 *  allows you to compute statistics on the
 * and store the results as a new column using [<code>into</code>][AggregateDsl.into].
 *
 *
 * The resulting  has the same structure as the original
 * ;
 * instead of the groups, there are new columns of aggregated values created with [<code>into</code>][AggregateDsl.into].
 *
 * You can use any of [<code>DataFrame Aggregation Statistics</code>][DataFrameAggregationStatistics]
 * or any custom aggregation function.
 *
 * Aggregated values can be either simple values, [<code>data rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] or even
 * [<code>data frames</code>][org.jetbrains.kotlinx.dataframe.DataFrame]. Including them in the result using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into] will lead
 * to creating [<code>value column</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
 * in the resulting  while preserving the original structure at higher levels.
 */
internal interface AggregateDslDocs

/**
 * A specialized [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl] that allows you to aggregate
 * [<code>DataFrame</code>][DataFrame] or `DataFrame`-like structures ([<code>GroupBy</code>][GroupBy], [<code>Pivot</code>][Pivot] or [<code>PivotGroupBy</code>][PivotGroupBy]).
 *
 * [<code>AggregateDsl</code>][AggregateDsl] allows you to compute statistics on the columns of the [<code>DataFrame</code>][DataFrame] or columns within groups in [<code>GroupBy</code>][GroupBy], [<code>Pivot</code>][Pivot], or [<code>PivotGroupBy</code>][PivotGroupBy]
 * and store the results as a new column using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into]. The given aggregating expression is applied to each group independently.
 *
 *
 * The resulting [<code>DataFrame</code>][DataFrame] or [<code>DataRow</code>][DataRow] has the same structure as the original
 * [<code>DataFrame</code>][DataFrame], [<code>GroupBy</code>][GroupBy], [<code>Pivot</code>][Pivot] or [<code>PivotGroupBy</code>][PivotGroupBy];
 * instead of the groups, there are new columns of aggregated values created with [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into].
 *
 * You can use any of [<code>DataFrame Aggregation Statistics</code>][org.jetbrains.kotlinx.dataframe.aggregation.DataFrameAggregationStatistics]
 * or any custom aggregation function.
 *
 * Aggregated values can be either simple values, [<code>data rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] or even
 * [<code>data frames</code>][org.jetbrains.kotlinx.dataframe.DataFrame]. Including them in the result using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into] will lead
 * to creating [<code>value column</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
 * in the resulting [<code>DataFrame</code>][DataFrame] or [<code>DataRow</code>][DataRow] while preserving the original structure at higher levels.
 *
 *
 *
 *
 *
 */
@HasSchema(schemaArg = 0)
public abstract class AggregateDsl<out T> :
    DataFrame<T>,
    ColumnSelectionDsl<T> {

    /**
     * Adds the result of the aggregation operation to the resulting
     * [<code>DataFrame</code>][DataFrame] or [<code>DataRow</code>][DataRow] as a new column.
     *
     * Aggregated values can be either simple values, [<code>data rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] or even
     * [<code>data frames</code>][org.jetbrains.kotlinx.dataframe.DataFrame]. Including them in the result using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into] will lead
     * to creating [<code>value column</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
     * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
     * in the resulting [<code>DataFrame</code>][DataFrame] or [<code>DataRow</code>][DataRow] while preserving the original structure at higher levels.
     *
     * @param [name] The name of the new column.
     */
    @Interpretable("AggregateDslInto")
    public inline infix fun <reified R> R.into(name: String): NamedValue =
        internal().yield(pathOf(name), this, typeOf<R>())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> R.into(column: ColumnAccessor<R>): NamedValue =
        internal().yield(pathOf(column.name()), this, typeOf<R>())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> R.into(column: KProperty<R>): NamedValue =
        internal().yield(pathOf(column.columnName), this, typeOf<R>())

    /**
     * Sets the default value for the aggregation operation.
     * All `null` values will be replaced with this value.
     *
     * Can be used before or after [<code>into</code>][into].
     *
     * @receiver The aggregation operation result.
     * @param [defaultValue] The default value for the result.
     */
    public infix fun <R> R.default(defaultValue: R): Any =
        when (this) {
            is NamedValue -> this.also { it.default = defaultValue }
            else -> ValueWithDefault(this, defaultValue)
        }
}
