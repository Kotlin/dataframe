package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.cast
import org.jetbrains.kotlinx.dataframe.impl.aggregation.getAggregateColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.getPath
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateInternalDsl
import org.jetbrains.kotlinx.dataframe.type

internal fun <T, C, R> Aggregator<*, R>.aggregateFor(
    data: Grouped<T>,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = data.aggregateInternal {
    aggregateFor(columns, cast())
}

internal fun <T, C, R> Aggregator<*, R>.aggregateFor(
    data: PivotGroupBy<T>,
    separate: Boolean,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = data.aggregate(separate) {
    internal().aggregateFor(columns, cast())
}

internal fun <T, C, R> Aggregator<*, R>.aggregateFor(
    data: DataFrame<T>,
    columns: ColumnsForAggregateSelector<T, C?>
): DataRow<T> = data.aggregate {
    internal().aggregateFor(columns, cast())
}

internal fun <T, C, R> AggregateInternalDsl<T>.aggregateFor(
    columns: ColumnsForAggregateSelector<T, C>,
    aggregator: Aggregator<C, R>
) {
    val cols = df.getAggregateColumns(columns)
    val isSingle = cols.size == 1
    cols.forEach { col ->
        val path = getPath(col, isSingle)
        val value = aggregator.aggregate(col.data)
        val inferType = !aggregator.preservesType
        yield(path, value, col.type, col.default, inferType)
    }
}
