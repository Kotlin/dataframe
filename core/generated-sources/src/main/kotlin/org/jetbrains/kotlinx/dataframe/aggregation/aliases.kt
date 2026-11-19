package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver

public typealias AggregateBody<T, R> = Selector<AggregateDsl<T>, R>

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
