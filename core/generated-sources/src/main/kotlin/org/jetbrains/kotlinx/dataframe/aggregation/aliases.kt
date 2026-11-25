package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver

/**
 * The type of the [<code>aggregating expression</code>][Pivot.aggregate] used in [<code>Pivot</code>][Pivot] aggregations.
 *
 * A lambda that receives [<code>AggregateDsl</code>][AggregateDsl] both as the receiver (`this`) and as the argument (`it`),
 * computes the aggregation results for one group at a time,
 * and stores each of them with [<code>into</code>][AggregateDsl.into].
 */
public typealias AggregateBody<T, R> = Selector<AggregateDsl<T>, R>

/**
 * The type of the [<code>aggregating expression</code>][Grouped.aggregate] used in [<code>GroupBy</code>][GroupBy] aggregations.
 *
 * A lambda that receives [<code>AggregateGroupedDsl</code>][AggregateGroupedDsl] both as the receiver (`this`) and as the argument (`it`),
 * computes the aggregation results for one group at a time,
 * and stores each of them with [<code>into</code>][AggregateDsl.into].
 * Unlike [<code>AggregateBody</code>][AggregateBody], it also allows [<code>pivoting</code>][AggregateGroupedDsl.pivot] inside the aggregation.
 */
public typealias AggregateGroupedBody<G, R> = Selector<AggregateGroupedDsl<G>, R>

/**
 * A specialized [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used for selecting columns
 * to aggregate.
 *
 * Provides [<code>ColumnsForAggregateSelectionDsl</code>][ColumnsForAggregateSelectionDsl] both as the receiver (`this`) and the lambda parameter (`it`),
 * and expects a [<code>ColumnsResolver</code>][ColumnsResolver] as the return value.
 *
 * Enables storing the aggregation results under a new name or path
 * using [<code>into</code>][ColumnsForAggregateSelectionDsl.into]
 * and specifying the value used instead of `null` results in pivot aggregations
 * using [<code>default</code>][ColumnsForAggregateSelectionDsl.default].
 *
 * See [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 */
public typealias ColumnsForAggregateSelector<T, C> = Selector<ColumnsForAggregateSelectionDsl<T>, ColumnsResolver<C>>
