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
 * {@include [AggregateDslDocsSnippet]}
 * {@set [AGGREGATE_DSL_TYPE] [AggregateGroupedDsl]}
 * {@set [AGGREGATE_DSL_RECEIVER] [GroupBy]}
 * {@set [AGGREGATE_DSL_APPLY] The given [expression][body] is applied to each group independently.}
 * {@set [AGGREGATE_DSL_RESULT] [DataFrame]}
 * {@set [AGGREGATE_DSL_OPERATING_COLUMNS] columns within groups in [GroupBy]}
 *
 * It allows [pivoting][DataFrame.pivot] inside [aggregate][Grouped.aggregate] via corresponding methods:
 * * [pivot][AggregateGroupedDsl.pivot]
 * * [pivotCounts][AggregateGroupedDsl.pivotCounts]
 * * [pivotMatches][AggregateGroupedDsl.pivotMatches]
 *
 * Pivoting inside [aggregate][Grouped.aggregate] is useful for counting
 * cross-group matrix-like statistics.
 */
@ExcludeFromSources
internal typealias AggregateGroupedDslDocsSnippet = Nothing

/**
 * A specialized [AggregateDsl]
 * used in [GroupBy.aggregate][Grouped.aggregate] method; allows
 * [pivoting][DataFrame.pivot] inside its body.
 *
 * {@include [AggregateGroupedDslDocsSnippet]}
 */
@HasSchema(schemaArg = 0)
public abstract class AggregateGroupedDsl<out T> : AggregateDsl<T>()
