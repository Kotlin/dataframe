package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDslDocs
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
import org.jetbrains.kotlinx.dataframe.impl.aggregateGroupBy

// region Pivot

/**
 * Aggregates this [<code>Pivot</code>][Pivot] using the provided statistics
 * inside the [<code>AggregateDsl</code>][AggregateDsl].
 *
 * Every value computed inside the [<code>aggregating expression</code>][body]
 * and stored with [<code>into</code>][AggregateDsl.into] becomes a new column
 * nested under the corresponding [<code>pivot</code>][pivot] key.
 * If nothing is stored with [<code>into</code>][AggregateDsl.into]
 * and the last expression of [<code>body</code>][body] is not [<code>Unit</code>][Unit],
 * its value is stored directly under the [<code>pivot</code>][pivot] key.
 * If something is stored with [<code>into</code>][AggregateDsl.into],
 * a trailing value that is not stored with [<code>into</code>][AggregateDsl.into] is ignored —
 * unlike in [<code>GroupBy.aggregate</code>][Grouped.aggregate],
 * which stores it in a new column named `"aggregated"`.
 *
 * Returns a new [<code>DataRow</code>][DataRow] with the original [<code>pivot</code>][pivot] keys as top-level columns
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
 * to creating a [<code>value column</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
 * a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
 * in the resulting [<code>DataRow</code>][DataRow] while preserving the original structure at higher levels.
 *
 *
 *
 *
 *
 *
 * Check out [<code>`pivot` Grammar</code>][PivotDocs.Grammar] for more information.
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html) [See "`Pivot` Aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation)
 *
 * See also:
 * - [<code>GroupBy.aggregate</code>][Grouped.aggregate] — the [<code>GroupBy</code>][GroupBy] counterpart
 *   that produces a [<code>DataFrame</code>][DataFrame] with one row per group.
 * - [<code>Pivot aggregations</code>][PivotDocs.Aggregation] — all other ways to aggregate a [<code>Pivot</code>][Pivot],
 *   including the predefined [<code>statistics</code>][PivotDocs.AggregationStatistics].
 *
 * ### Examples
 *
 * ```kotlin
 * df.pivot { isHappy }.aggregate {
 *     // Count rows within each pivot group and store the result
 *     // into a new "total" column (a new sub-column under each "isHappy" key column)
 *     count() into "total"
 *
 *     // Count rows with "age" of at least 18 within each pivot group
 *     // and store the result into a new "adults" column
 *     count { age >= 18 } into "adults"
 *
 *     // Compute the median of the "age" column values within each pivot group
 *     // and store it into a new "median age" column
 *     median { age } into "median age"
 *
 *     // Compute the minimum of the "age" column values within each pivot group
 *     // and store it into a new "min age" column
 *     min { age } into "min age"
 *
 *     // Store the "name" column group of the oldest person within each pivot group
 *     // as a new "oldest" column group
 *     maxBy { age }.name into "oldest"
 * }
 * ```
 * ```kotlin
 * // The last expression is not stored with `into`,
 * // so its value is stored directly under the pivot keys
 * df.pivot { isHappy }.aggregate { minBy { age }.name }
 * ```
 *
 * @param [T] The schema marker type of the pivoted [<code>DataFrame</code>][DataFrame].
 * @param [R] The result type of the [<code>aggregating expression</code>][body]; usually `Unit`.
 * @param [separate] If `false` (the default), the resulting columns are indexed
 *   first by the pivot key(s) and then by the names of the aggregated columns.
 *   If `true`, this order is reversed: the results are grouped by aggregated column first.
 * @param [body] The aggregation logic defined using [<code>AggregateDsl</code>][AggregateDsl].
 * @return A new [<code>DataRow</code>][DataRow] with the results of the aggregation applied to each group.
 */
public fun <T, R> Pivot<T>.aggregate(separate: Boolean = false, body: Selector<AggregateDsl<T>, R>): DataRow<*> =
    delegate {
        aggregate(separate, body)
    }

// endregion

/**
 * Aggregates this [<code>GroupBy</code>][GroupBy] using the provided statistics
 * inside the [<code>AggregateGroupedDsl</code>][AggregateGroupedDsl].
 *
 * Every value computed inside the [<code>aggregating expression</code>][body]
 * and stored with [<code>into</code>][AggregateDsl.into] becomes a new column in the result.
 * If the last expression of [<code>body</code>][body] is not [<code>Unit</code>][Unit]
 * and is not stored with [<code>into</code>][AggregateDsl.into],
 * its value is stored in a new column named `"aggregated"`.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with one row per group: the original [<code>groupBy</code>][groupBy] key columns
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
 * to creating a [<code>value column</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
 * a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
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
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html) [See "`GroupBy` Aggregation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation)
 *
 * See also:
 * - [<code>aggregate</code>][Pivot.aggregate] — the [<code>Pivot</code>][Pivot] counterpart that produces a single [<code>DataRow</code>][DataRow].
 * - [<code>GroupBy aggregation</code>][GroupByDocs.Aggregation] — all other ways to aggregate a [<code>GroupBy</code>][GroupBy],
 *   including the predefined [<code>statistics</code>][GroupByDocs.AggregationStatistics].
 * - [<code>GroupBy reducing</code>][GroupByDocs.Reducing] — a specific case of aggregation
 *   where each group is collapsed into a single row.
 *
 * ### Examples
 *
 * ```kotlin
 * df.groupBy { city }.aggregate {
 *     // Count rows within each group and store the result
 *     // into a new "total" column
 *     count() into "total"
 *
 *     // Count rows with "age" column value greater than 18 within each group
 *     // and store the result into a new "adults" column
 *     count { age > 18 } into "adults"
 *
 *     // Compute the median of the "age" column values within each group
 *     // and store it into a new "median age" column
 *     median { age } into "median age"
 *
 *     // Compute the minimum of the "age" column values within each group
 *     // and store it into a new "min age" column
 *     min { age } into "min age"
 *
 *     // Store the "name" column group values of the oldest person in each group
 *     // as a new "oldest" column group
 *     maxBy { age }.name into "oldest"
 * }
 * ```
 * ```kotlin
 * // The last expression is not stored with `into`,
 * // so its value is stored in a new column named "aggregated"
 * df.groupBy { city }.aggregate { maxBy { age }.name }
 * ```
 * ```kotlin
 * df.groupBy { name.firstName }.aggregate {
 *     // Pivot the "isHappy" column within each group,
 *     // creating a PivotGroupBy with "name.firstName" as grouping keys
 *     // and "isHappy" as pivoted columns
 *     pivot { isHappy }.aggregate(separate = true) {
 *         // Compute the mean of the "age" column values for each
 *         // "firstName" × "isHappy" combination group into the "mean age" column
 *         mean { age } into "mean age"
 *
 *         // Count rows within each "firstName" × "isHappy" combination group
 *         // into the "count" column
 *         count() into "count"
 *     }
 *
 *     // Common `count` aggregation into the "total" column
 *     count() into "total"
 * }
 * ```
 *
 * @param [T] The schema marker type of the [<code>groups</code>][GroupBy.groups] in the underlying [<code>GroupBy</code>][GroupBy].
 * @param [R] The result type of the [<code>aggregating expression</code>][body]; usually `Unit`.
 * @param [body] The aggregation logic defined using [<code>AggregateGroupedDsl</code>][AggregateGroupedDsl].
 * @return A new [<code>DataFrame</code>][DataFrame] with one row per group with the results of the aggregation applied to it.
 */
@Refine
@Interpretable("Aggregate")
public fun <T, R> Grouped<T>.aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T> =
    aggregateGroupBy((this as GroupBy<*, *>).toDataFrame(), {
        groups.cast()
    }, removeColumns = true, body).castUnchecked<T>()
