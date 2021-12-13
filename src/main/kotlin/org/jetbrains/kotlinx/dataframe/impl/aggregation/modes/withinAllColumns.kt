package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.cast
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.pathOf

@PublishedApi
internal fun <T, C, R> Aggregator<*, R>.aggregateAll(
    data: DataFrame<T>,
    columns: ColumnsSelector<T, C>
): C? = data.aggregateAll(cast(), columns)

internal fun <T, R, C> Aggregator<*, R>.aggregateAll(
    data: Grouped<T>,
    name: String?,
    columns: ColumnsSelector<T, C>
): DataFrame<T> = data.aggregateAll(cast(), columns, name)

internal fun <T, R, C> Aggregator<*, R>.aggregateAll(
    data: PivotGroupBy<T>,
    columns: ColumnsSelector<T, C>
): DataFrame<T> = data.aggregateAll(cast(), columns)

internal fun <T, C, R> DataFrame<T>.aggregateAll(
    aggregator: Aggregator<C, R>,
    columns: ColumnsSelector<T, C>
): R? = aggregator.aggregate(get(columns))

internal fun <T, C, R> Grouped<T>.aggregateAll(
    aggregator: Aggregator<C, R>,
    columns: ColumnsSelector<T, C>,
    name: String?
): DataFrame<T> = aggregateInternal {
    val cols = df[columns]
    if (cols.size == 1) {
        yield(pathOf(name ?: cols[0].name()), aggregator.aggregate(cols[0]))
    } else {
        yield(pathOf(name ?: aggregator.name), aggregator.aggregate(cols))
    }
}

internal fun <T, C, R> PivotGroupBy<T>.aggregateAll(
    aggregator: Aggregator<C, R>,
    columns: ColumnsSelector<T, C>
): DataFrame<T> = aggregate {
    val cols = get(columns)
    if (cols.size == 1) {
        internal().yield(emptyPath(), aggregator.aggregate(cols[0]))
    } else {
        internal().yield(emptyPath(), aggregator.aggregate(cols))
    }
}
