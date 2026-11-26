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
 * @include [AggregateDslDocs]
 * {@set [AggregateDslDocs.AGGREGATE_DSL_TYPE] [AggregateGroupedDsl]}
 * {@set [AggregateDslDocs.RECEIVER] [GroupBy]}
 * {@set [AggregateDslDocs.APPLY_NOTE] The given aggregating expression is applied to each group independently.}
 * {@set [AggregateDslDocs.RESULT_TYPE] [DataFrame]}
 * {@set [AggregateDslDocs.OPERATING_COLUMNS] columns within groups in [GroupBy]}
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
