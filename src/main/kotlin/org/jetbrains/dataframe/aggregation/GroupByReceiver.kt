package org.jetbrains.dataframe.aggregation

import org.jetbrains.dataframe.Column
import org.jetbrains.dataframe.ColumnsSelector
import org.jetbrains.dataframe.GroupedPivot
import org.jetbrains.dataframe.impl.aggregation.GroupAggregatorPivotImpl
import org.jetbrains.dataframe.impl.columns.toColumns

public abstract class GroupByReceiver<out T> : AggregateReceiver<T>() {

    public fun pivot(columns: ColumnsSelector<T, *>): GroupedPivot<T> = GroupAggregatorPivotImpl(this, columns)
    public fun pivot(vararg columns: Column): GroupedPivot<T> = pivot { columns.toColumns() }
    public fun pivot(vararg columns: String): GroupedPivot<T> = pivot { columns.toColumns() }
}

public typealias GroupByAggregateBody<G, R> = GroupByReceiver<G>.(GroupByReceiver<G>) -> R
