package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.pathOf

public interface SelectAggregatableColumnsReceiver<out T> : ColumnSelectionDsl<T> {

    public infix fun <C> ColumnSet<C>.default(defaultValue: C): ColumnSet<C> =
        org.jetbrains.kotlinx.dataframe.impl.aggregation.ConfiguredAggregateColumn.withDefault(this, defaultValue)

    public fun path(vararg names: String): ColumnPath = ColumnPath(names.asList())

    public infix fun <C> ColumnSet<C>.into(name: String): ColumnSet<C> = org.jetbrains.kotlinx.dataframe.impl.aggregation.ConfiguredAggregateColumn.withPath(this, pathOf(name))

    public infix fun <C> ColumnSet<C>.into(path: ColumnPath): ColumnSet<C> = org.jetbrains.kotlinx.dataframe.impl.aggregation.ConfiguredAggregateColumn.withPath(this, path)
}

public typealias AggregateColumnsSelector<T, C> = SelectAggregatableColumnsReceiver<T>.(
    SelectAggregatableColumnsReceiver<T>
) -> ColumnSet<C>
