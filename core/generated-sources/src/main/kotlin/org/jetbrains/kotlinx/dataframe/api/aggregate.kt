package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDslDocs
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
 * and store the results as a new column using [into][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into]. The given [aggregating expression][body] is applied to each group independently.
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
 * #### Example
 * ```kotlin
 * df.pivot { city }.aggregate {
 *   // Сount rows within each pivot group and store the result
 *   // into a new "total" column (a new sub-column under each key column)
 *   count() into "total"
 *
 *   // Compute the maximum in "age" column within each group
 *   // and store it into a new "maxAge" column
 *   max { age } into "maxAge"
 * }
 * ```
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
 * and store the results as a new column using [into][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into]. The given [aggregating expression][body] is applied to each group independently.
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
 * cross-group matrix-like statistics.
 *
 *
 * Check out [`groupBy` Grammar][GroupByDocs.Grammar] for more information.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * #### Examples
 * ```kotlin
 * df.groupBy { city }.aggregate {
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
 * ```kotlin
 * df.groupBy { name.firstName }.aggregate {
 *     // Pivot the "city" column within each group,
 *     // creating a PivotGroupBy with "name" as grouping keys
 *     // and "city" as pivoted columns
 *     pivot { city }.aggregate {
 *         // Aggregate the mean of "age" column values for each
 *         // "firstName" × "city" combination group into the "meanAge" column
 *         mean { age } into "meanAge"
 *
 *         // Aggregate the size of each `PivotGroupBy` group
 *         // into the "count" column
 *         count() into "count"
 *     }
 *
 *     // Common `count` aggregation
 *     // into "total" column
 *     count() into "total"
 * }
 * ```
 *
 * @param body The aggregation logic defined using [AggregateGroupedDsl].
 * @return A new [DataFrame] with the results of the aggregation applied to each group.
 */
@Refine
@Interpretable("Aggregate")
public fun <T, R> Grouped<T>.aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T> =
    aggregateGroupBy((this as GroupBy<*, *>).toDataFrame(), { groups.cast() }, removeColumns = true, body).cast<T>()
