package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.AGGREGATE_DSL_OPERATING_COLUMNS
import org.jetbrains.kotlinx.dataframe.aggregation.AGGREGATE_DSL_RECEIVER
import org.jetbrains.kotlinx.dataframe.aggregation.AGGREGATE_DSL_RESULT
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.impl.aggregateGroupBy
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDslDocsSnippet

// region Pivot

public fun <T, R> Pivot<T>.aggregate(separate: Boolean = false, body: Selector<AggregateDsl<T>, R>): DataRow<T> =
    delegate {
        aggregate(separate, body)
    }

// endregion

/**
 * Aggregates this [GroupBy] using the provided statistics
 * inside the [AggregateDsl].
 *
 * {@include [AggregateDslDocsSnippet]}
 * {@set [AGGREGATE_DSL_RECEIVER] [GroupBy]}
 * {@set [AGGREGATE_DSL_RESULT] [DataFrame]}
 * {@set [AGGREGATE_DSL_OPERATING_COLUMNS] columns within groups of this [GroupBy]}

 *
 * @param T The schema type of the DataFrame.
 * @param R The return type of the aggregation logic.
 * @param body The aggregation logic defined as an [AggregateGroupedBody].
 *             This logic specifies how each group should be aggregated.
 * @return A new DataFrame with the results of the aggregation applied to each group.
 */
@Refine
@Interpretable("Aggregate")
public fun <T, R> Grouped<T>.aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T> =
    aggregateGroupBy((this as GroupBy<*, *>).toDataFrame(), { groups.cast() }, removeColumns = true, body).cast<T>()
