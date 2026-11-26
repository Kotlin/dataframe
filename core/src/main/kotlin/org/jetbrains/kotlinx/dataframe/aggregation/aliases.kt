package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.CSDslLink

/**
 * The type of the [aggregating expression][Pivot.aggregate] used in [Pivot] aggregations.
 *
 * A lambda that receives [AggregateDsl] both as the receiver (`this`) and as the argument (`it`),
 * computes the aggregation results for one group at a time,
 * and stores each of them with [into][AggregateDsl.into].
 */
public typealias AggregateBody<T, R> = Selector<AggregateDsl<T>, R>

/**
 * The type of the [aggregating expression][Grouped.aggregate] used in [GroupBy] aggregations.
 *
 * A lambda that receives [AggregateGroupedDsl] both as the receiver (`this`) and as the argument (`it`),
 * computes the aggregation results for one group at a time,
 * and stores each of them with [into][AggregateDsl.into].
 * Unlike [AggregateBody], it also allows [pivoting][AggregateGroupedDsl.pivot] inside the aggregation.
 */
public typealias AggregateGroupedBody<G, R> = Selector<AggregateGroupedDsl<G>, R>

/**
 * A specialized [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used for selecting columns
 * to aggregate.
 *
 * Provides [ColumnsForAggregateSelectionDsl] both as the receiver (`this`) and the lambda parameter (`it`),
 * and expects a [ColumnsResolver] as the return value.
 *
 * Enables storing the aggregation results under a new name or path
 * using [into][ColumnsForAggregateSelectionDsl.into]
 * and specifying the value used instead of `null` results in pivot aggregations
 * using [default][ColumnsForAggregateSelectionDsl.default].
 *
 * See {@include [CSDslLink]}.
 */
public typealias ColumnsForAggregateSelector<T, C> = Selector<ColumnsForAggregateSelectionDsl<T>, ColumnsResolver<C>>
