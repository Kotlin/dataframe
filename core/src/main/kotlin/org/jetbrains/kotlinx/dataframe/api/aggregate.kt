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
 * @include [AggregateDslDocs]
 * {@set [AggregateDslDocs.AGGREGATE_DSL_TYPE] [AggregateDsl]}
 * {@set [AggregateDslDocs.RECEIVER] [Pivot]}
 * {@set [AggregateDslDocs.APPLY_NOTE] The given [aggregating expression][body] is applied to each group independently.}
 * {@set [AggregateDslDocs.RESULT_TYPE] [DataRow]}
 * {@set [AggregateDslDocs.OPERATING_COLUMNS] columns within groups in [Pivot]}
 *
 * Check out [`pivot` Grammar][PivotDocs.Grammar] for more information.
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
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
 * @include [AggregateGroupedDslDocsSnippet]
 * {@set [AggregateDslDocs.APPLY_NOTE] The given [aggregating expression][body] is applied to each group independently.}
 *
 * Check out [`groupBy` Grammar][GroupByDocs.Grammar] for more information.
 *
 * For more information: {@include [DocumentationUrls.GroupBy]}
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
