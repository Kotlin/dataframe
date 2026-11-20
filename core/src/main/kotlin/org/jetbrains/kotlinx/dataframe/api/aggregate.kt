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
import org.jetbrains.kotlinx.dataframe.documentation.CommonStatisticsDocs
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.impl.aggregateGroupBy

// region Pivot

/**
 * Aggregates this [Pivot] using the provided statistics
 * inside the [AggregateDsl].
 *
 * Every value computed inside the [aggregating expression][body]
 * and stored with [into][AggregateDsl.into] becomes a new column
 * nested under the corresponding [pivot] key.
 * If the last expression of [body] is not [Unit]
 * and is not stored with [into][AggregateDsl.into],
 * its value is stored directly under the [pivot] key.
 *
 * Returns a new [DataRow] with the original [pivot] keys as top-level columns
 * and the corresponding aggregated values in new nested columns.
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
 * For more information: {@include [DocumentationUrls.Pivot]} {@include [DocumentationUrls.PivotAggregation]}
 *
 * See also:
 * - [GroupBy.aggregate][Grouped.aggregate] — the [GroupBy] counterpart
 *   that produces a [DataFrame] with one row per group.
 * - [Pivot aggregations][PivotDocs.Aggregation] — all other ways to aggregate a [Pivot],
 *   including the predefined [statistics][PivotDocs.AggregationStatistics].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotAggregateMultiple_properties]
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotAggregate_properties]
 *
 * @param [T] The schema marker type of the pivoted [DataFrame].
 * @param [R] The result type of the [aggregating expression][body]; usually `Unit`.
 * @include [CommonStatisticsDocs.SeparateParam]
 * @param [body] The aggregation logic defined using [AggregateDsl].
 * @return A new [DataRow] with the results of the aggregation applied to each group.
 */
public fun <T, R> Pivot<T>.aggregate(separate: Boolean = false, body: Selector<AggregateDsl<T>, R>): DataRow<*> =
    delegate {
        aggregate(separate, body)
    }

// endregion

/**
 * Aggregates this [GroupBy] using the provided statistics
 * inside the [AggregateGroupedDsl].
 *
 * Every value computed inside the [aggregating expression][body]
 * and stored with [into][AggregateDsl.into] becomes a new column in the result.
 * If the last expression of [body] is not [Unit]
 * and is not stored with [into][AggregateDsl.into],
 * its value is stored in a new column named `"aggregated"`.
 *
 * Returns a new [DataFrame] with one row per group: the original [groupBy] key columns
 * and the corresponding aggregated values in new columns.
 *
 * @include [AggregateGroupedDslDocsSnippet]
 * {@set [AggregateDslDocs.APPLY_NOTE] The given [aggregating expression][body] is applied to each group independently.}
 *
 * Check out [`groupBy` Grammar][GroupByDocs.Grammar] for more information.
 *
 * For more information: {@include [DocumentationUrls.GroupBy]} {@include [DocumentationUrls.GroupByAggregation]}
 *
 * See also:
 * - [aggregate][Pivot.aggregate] — the [Pivot] counterpart that produces a single [DataRow].
 * - [GroupBy aggregation][GroupByDocs.Aggregation] — all other ways to aggregate a [GroupBy],
 *   including the predefined [statistics][GroupByDocs.AggregationStatistics].
 * - [GroupBy reducing][GroupByDocs.Reducing] — a specific case of aggregation
 *   where each group is collapsed into a single row.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.aggregateOnGroupBy_properties]
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.aggregateOnGroupByWithoutInto_properties]
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotInAggregate_properties]
 *
 * @param [T] The schema marker type of the [groups][GroupBy.groups] in the underlying [GroupBy].
 * @param [R] The result type of the [aggregating expression][body]; usually `Unit`.
 * @param [body] The aggregation logic defined using [AggregateGroupedDsl].
 * @return A new [DataFrame] with one row per group with the results of the aggregation applied to it.
 */
@Refine
@Interpretable("Aggregate")
public fun <T, R> Grouped<T>.aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T> =
    aggregateGroupBy((this as GroupBy<*, *>).toDataFrame(), { groups.cast() }, removeColumns = true, body).cast<T>()
