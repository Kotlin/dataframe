package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.AGGREGATE_DSL_APPLY
import org.jetbrains.kotlinx.dataframe.aggregation.AGGREGATE_DSL_OPERATING_COLUMNS
import org.jetbrains.kotlinx.dataframe.aggregation.AGGREGATE_DSL_RECEIVER
import org.jetbrains.kotlinx.dataframe.aggregation.AGGREGATE_DSL_RESULT
import org.jetbrains.kotlinx.dataframe.aggregation.AGGREGATE_DSL_TYPE
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDslDocsSnippet
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDslDocsSnippet
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.impl.aggregateGroupBy

// region Pivot

/**
 * Aggregates this [Pivot] using the provided statistics
 * inside the [AggregateDsl].
 *
 * Returns a new [DataRow] with the original [pivot] keys as top-level columns on top level
 * and the correspndonding aggregated values in new nested columns.
 *
 * [AggregateDsl] allows to compute statistics on the columns within groups in [Pivot]
 * and store the results as a new column using [into][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into]. The given [expression][body] is applied to each group independently.
 *
 *
 * The resulting [DataRow] has the same structure as the original
 * [Pivot];
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
 *
 * Check out [`pivot` Grammar][PivotDocs.Grammar] for more information.
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 *
 * @param body The aggregation logic defined using [AggregateDsl].
 * @return A new [DataFrame] with the results of the aggregation applied to each group.
 */
public fun <T, R> Pivot<T>.aggregate(separate: Boolean = false, body: Selector<AggregateDsl<T>, R>): DataRow<T> =
    delegate {
        aggregate(separate, body)
    }

// endregion

/**
 * Aggregates this [GroupBy] using the provided statistics
 * inside the [AggregateGroupedDsl].
 *
 * Returns a new [DataFrame] with the original [groupBy] key columns
 * and the correspodning aggregated values in new columns.
 *
 * [AggregateGroupedDsl][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl] allows to compute statistics on the columns within groups in [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy]
 * and store the results as a new column using [into][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into]. The given [expression][body] is applied to each group independently.
 *
 *
 * The resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] has the same structure as the original
 * [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy];
 * instead of the groups, there are new columns of aggregated values created with [into][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into].
 *
 * You can use any of [DataFrame Aggregation Statistics][org.jetbrains.kotlinx.dataframe.aggregation.DataFrameAggregationStatistics]
 * or any custom aggregation function.
 *
 * Aggregated values can be either simple values, [data rows][org.jetbrains.kotlinx.dataframe.DataRow] or even
 * [data frames][org.jetbrains.kotlinx.dataframe.DataFrame]. Including them in the result using [into][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into] will lead
 * to creating [value column][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
 * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [frame column][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] while preserving the original structure at higher levels.
 *
 *
 *
 *
 *
 *
 * It allows [pivoting][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] inside [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] via corresponding methods:
 * * [pivot][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * * [pivotCounts][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts]
 * * [pivotMatches][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches]
 *
 * Pivoting inside [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] is useful for counting
 * cross-group statistics matrix-like statistics.
 *
 * Check out [`groupBy` Grammar][GroupByDocs.Grammar] for more information.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * @param body The aggregation logic defined using [AggregateGroupedDsl].
 * @return A new [DataFrame] with the results of the aggregation applied to each group.
 */
@Refine
@Interpretable("Aggregate")
public fun <T, R> Grouped<T>.aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T> =
    aggregateGroupBy((this as GroupBy<*, *>).toDataFrame(), { groups.cast() }, removeColumns = true, body).cast<T>()
