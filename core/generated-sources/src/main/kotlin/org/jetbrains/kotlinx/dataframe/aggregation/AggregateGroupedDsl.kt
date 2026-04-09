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
 * A specialized [AggregateDsl]
 * used in [GroupBy.aggregate][Grouped.aggregate] method; allows
 * [pivoting][DataFrame.pivot] inside its body.
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
 */
@HasSchema(schemaArg = 0)
public abstract class AggregateGroupedDsl<out T> : AggregateDsl<T>()
