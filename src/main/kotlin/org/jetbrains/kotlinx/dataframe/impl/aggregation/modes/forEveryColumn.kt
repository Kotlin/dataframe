package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.api.GroupedPivot
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.getAggregateColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.getPath
import org.jetbrains.kotlinx.dataframe.type

internal fun <T, C, R> Aggregator<*, R>.aggregateFor(
    data: Aggregatable<T>,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = data.aggregateFor(columns, cast())

internal fun <T, C, R> Aggregatable<T>.aggregateFor(
    columns: ColumnsForAggregateSelector<T, C>,
    aggregator: Aggregator<C, R>
) = aggregateInternal {
    val cols = df.getAggregateColumns(columns)
    val isSingle = cols.size == 1
    cols.forEach { col ->
        val path = getPath(col, isSingle)
        val value = aggregator.aggregate(col.data)
        val inferType = !aggregator.preservesType
        yield(path, value, col.type, col.default, inferType)
    }
}

internal fun <T, C, R> Aggregator<*, R>.aggregateFor(
    data: GroupedPivot<T>,
    separate: Boolean,
    columns: ColumnsForAggregateSelector<T, C?>
): DataFrame<T> = data.separateAggregatedValues(separate).aggregateFor(columns, cast())

internal fun <T, C, R> Aggregator<*, R>.aggregateFor(
    data: DataFrame<T>,
    columns: ColumnsForAggregateSelector<T, C?>
): DataRow<T> = aggregateFor(data as Aggregatable<T>, columns)[0]
