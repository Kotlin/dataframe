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
 * Aggregates this [<code>Pivot</code>][Pivot] using the provided statistics
 * inside the [<code>AggregateDsl</code>][AggregateDsl].
 *
 * Returns a new [<code>DataRow</code>][DataRow] with the original [<code>pivot</code>][pivot] keys as top-level columns on top level
 * and the corresponding aggregated values in new nested columns.
 *
 * [<code>AggregateDsl</code>][AggregateDsl] allows you to compute statistics on the columns within groups in [<code>Pivot</code>][Pivot]
 * and store the results as a new column using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into]. The given [<code>aggregating expression</code>][body] is applied to each group independently.
 *
 *
 * The resulting [<code>DataRow</code>][DataRow] has the same structure as the original
 * [<code>Pivot</code>][Pivot];
 * instead of the groups, there are new columns of aggregated values created with [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into].
 *
 * You can use any of [<code>DataFrame Aggregation Statistics</code>][org.jetbrains.kotlinx.dataframe.aggregation.DataFrameAggregationStatistics]
 * or any custom aggregation function.
 *
 * Aggregated values can be either simple values, [<code>data rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] or even
 * [<code>data frames</code>][org.jetbrains.kotlinx.dataframe.DataFrame]. Including them in the result using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into] will lead
 * to creating [<code>value column</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
 * in the resulting [<code>DataRow</code>][DataRow] while preserving the original structure at higher levels.
 *
 *
 *
 *
 *
 *
 * Check out [<code>`pivot` Grammar</code>][PivotDocs.Grammar] for more information.
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
 * @param body The aggregation logic defined using [<code>AggregateDsl</code>][AggregateDsl].
 * @return A new [<code>DataFrame</code>][DataFrame] with the results of the aggregation applied to each group.
 */
public fun <T, R> Pivot<T>.aggregate(separate: Boolean = false, body: Selector<AggregateDsl<T>, R>): DataRow<T> =
    delegate {
        aggregate(separate, body)
    }

// endregion

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] using the provided statistics
 * inside the [<code>AggregateGroupedDsl</code>][AggregateGroupedDsl].
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the original [<code>groupBy</code>][groupBy] key columns
 * and the corresponding aggregated values in new columns.
 *
 * [<code>AggregateGroupedDsl</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl] allows you to compute statistics on the columns within groups in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]
 * and store the results as a new column using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into]. The given [<code>aggregating expression</code>][body] is applied to each group independently.
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
 *
 *
 * Check out [<code>`groupBy` Grammar</code>][GroupByDocs.Grammar] for more information.
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
 * @param body The aggregation logic defined using [<code>AggregateGroupedDsl</code>][AggregateGroupedDsl].
 * @return A new [<code>DataFrame</code>][DataFrame] with the results of the aggregation applied to each group.
 */
@Refine
@Interpretable("Aggregate")
public fun <T, R> Grouped<T>.aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T> =
    aggregateGroupBy((this as GroupBy<*, *>).toDataFrame(), { groups.cast() }, removeColumns = true, body).cast<T>()
