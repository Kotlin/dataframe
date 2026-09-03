package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.pivotCounts
import org.jetbrains.kotlinx.dataframe.api.pivotMatches
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

/**
 * A specialized [<code>AggregateDsl</code>][AggregateDsl]
 * used in [<code>GroupBy.aggregate</code>][Grouped.aggregate] method; allows
 * [<code>pivoting</code>][DataFrame.pivot] inside its body.
 *
 * [<code>AggregateGroupedDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl] allows you to compute statistics on the columns within groups in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]
 * and store the results as a new column using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into]. The given aggregating expression is applied to each group independently.
 *
 *
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] has the same structure as the original
 * [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy];
 * instead of the groups, there are new columns of aggregated values created with [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into].
 *
 * You can use any of [<code>DataFrame Aggregation Statistics</code>][org.jetbrains.kotlinx.dataframe.aggregation.DataFrameAggregationStatistics]
 * or any custom aggregation function.
 *
 * Aggregated values can be either simple values, [<code>data rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] or even
 * [<code>data frames</code>][org.jetbrains.kotlinx.dataframe.DataFrame]. Including them in the result using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into] will lead
 * to creating [<code>value column</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] while preserving the original structure at higher levels.
 *
 *
 *
 *
 *
 *
 * It allows [<code>pivoting</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] inside [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] via corresponding methods:
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * * [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts]
 * * [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches]
 *
 * Pivoting inside [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] is useful for counting
 * cross-group matrix-like statistics.
 */
@HasSchema(schemaArg = 0)
public abstract class AggregateGroupedDsl<out T> : AggregateDsl<T>()
