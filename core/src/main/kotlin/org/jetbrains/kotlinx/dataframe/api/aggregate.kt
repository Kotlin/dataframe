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
 * {@include [AggregateDslDocsSnippet]}
 * {@set [AGGREGATE_DSL_TYPE] [AggregateDsl]}
 * {@set [AGGREGATE_DSL_RECEIVER] [Pivot]}
 * {@set [AGGREGATE_DSL_APPLY] The given [expression][body] is applied to each group independently.}
 * {@set [AGGREGATE_DSL_RESULT] [DataRow]}
 * {@set [AGGREGATE_DSL_OPERATING_COLUMNS] columns within groups in [Pivot]}
 *
 * Check out [`pivot` Grammar][PivotDocs.Grammar] for more information.
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
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
 * {@include [AggregateGroupedDslDocsSnippet]}
 *
 * Check out [`groupBy` Grammar][GroupByDocs.Grammar] for more information.
 *
 * For more information: {@include [DocumentationUrls.GroupBy]}
 *
 * @param body The aggregation logic defined using [AggregateGroupedDsl].
 * @return A new [DataFrame] with the results of the aggregation applied to each group.
 */
@Refine
@Interpretable("Aggregate")
public fun <T, R> Grouped<T>.aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T> =
    aggregateGroupBy((this as GroupBy<*, *>).toDataFrame(), { groups.cast() }, removeColumns = true, body).cast<T>()
