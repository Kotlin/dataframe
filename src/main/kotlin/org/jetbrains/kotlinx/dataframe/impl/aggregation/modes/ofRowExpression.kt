package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.pathOf

@PublishedApi
internal inline fun <C, reified V, R> Aggregator<V, R>.aggregateOf(
    values: Iterable<C>,
    noinline transform: (C) -> V
): R? = aggregate(values.asSequence().map(transform).asIterable(), getType<V>())

@PublishedApi
internal inline fun <C, reified V, R> Aggregator<V, R>.aggregateOf(
    column: DataColumn<C>,
    noinline transform: (C) -> V
): R? = aggregateOf(column.values(), transform)

@PublishedApi
internal inline fun <T, reified C, R> Aggregator<*, R>.aggregateOf(
    frame: DataFrame<T>,
    crossinline expression: RowExpression<T, C>
): R? = (this as Aggregator<C, R>).aggregateOf(frame.rows()) { expression(it, it) } // TODO: inline

@PublishedApi
internal fun <T, C, R> Aggregator<*, R>.aggregateOfDelegated(
    frame: Grouped<T>,
    name: String?,
    body: AggregateBody<T, C>
): DataFrame<T> = frame.aggregateValue(name ?: this.name) {
    body(this, this)
}

@PublishedApi
internal inline fun <T, reified C, R> Aggregator<*, R>.of(
    data: DataFrame<T>,
    crossinline expression: RowExpression<T, C>
): R? = aggregateOf(data as DataFrame<T>, expression)

@PublishedApi
internal inline fun <C, reified V, R> Aggregator<V, R>.of(
    data: DataColumn<C>,
    crossinline expression: (C) -> V
): R? = aggregateOf(data.values()) { expression(it) } // TODO: inline

@PublishedApi
internal inline fun <T, reified C, reified R> Aggregator<*, R>.aggregateOf(
    data: Grouped<T>,
    resultName: String? = null,
    crossinline expression: RowExpression<T, C>
): DataFrame<T> = data.aggregateOf(resultName, expression, this as Aggregator<C, R>)

@PublishedApi
internal inline fun <T, reified C, reified R> Aggregator<*, R>.aggregateOf(
    data: PivotGroupBy<T>,
    crossinline expression: RowExpression<T, C>
): DataFrame<T> = data.aggregateOf(expression, this as Aggregator<C, R>)

@PublishedApi
internal inline fun <T, reified C, reified R> Grouped<T>.aggregateOf(
    resultName: String?,
    crossinline expression: RowExpression<T, C>,
    aggregator: Aggregator<C, R>
): DataFrame<T> {
    val path = pathOf(resultName ?: aggregator.name)
    val type = getType<R>()
    return aggregateInternal {
        val value = aggregator.aggregateOf(df, expression)
        yield(path, value, type, null, false)
    }
}

@PublishedApi
internal inline fun <T, reified C, R> PivotGroupBy<T>.aggregateOf(
    crossinline expression: RowExpression<T, C>,
    aggregator: Aggregator<C, R>
): DataFrame<T> = aggregateInternal {
    yield(emptyPath(), aggregator.aggregateOf(df, expression))
}
