package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnPath
import org.jetbrains.kotlinx.dataframe.SelectReceiver
import org.jetbrains.kotlinx.dataframe.columns.Columns
import org.jetbrains.kotlinx.dataframe.pathOf

public interface SelectAggregatableColumnsReceiver<out T> : SelectReceiver<T> {

    public infix fun <C> Columns<C>.default(defaultValue: C): Columns<C> =
        org.jetbrains.kotlinx.dataframe.impl.aggregation.ConfiguredAggregateColumn.withDefault(this, defaultValue)

    public fun path(vararg names: String): ColumnPath = ColumnPath(names.asList())

    public infix fun <C> Columns<C>.into(name: String): Columns<C> = org.jetbrains.kotlinx.dataframe.impl.aggregation.ConfiguredAggregateColumn.withPath(this, pathOf(name))

    public infix fun <C> Columns<C>.into(path: ColumnPath): Columns<C> = org.jetbrains.kotlinx.dataframe.impl.aggregation.ConfiguredAggregateColumn.withPath(this, path)
}

public typealias AggregateColumnsSelector<T, C> = SelectAggregatableColumnsReceiver<T>.(
    SelectAggregatableColumnsReceiver<T>
) -> Columns<C>
