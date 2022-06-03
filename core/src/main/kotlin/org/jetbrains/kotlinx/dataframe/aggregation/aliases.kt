package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet

public typealias AggregateBody<T, R> = Selector<AggregateDsl<T>, R>

public typealias AggregateGroupedBody<G, R> = Selector<AggregateGroupedDsl<G>, R>

public typealias ColumnsForAggregateSelector<T, C> = Selector<ColumnsForAggregateSelectionDsl<T>, ColumnSet<C>>
