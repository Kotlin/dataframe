package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.CSDslLink

public typealias AggregateBody<T, R> = Selector<AggregateDsl<T>, R>

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
