package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregate
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import kotlin.reflect.typeOf

@PublishedApi
internal inline fun <C, reified V : Any?, R : Any?> Aggregator<V & Any, R>.aggregateOf(
    values: Sequence<C>,
    crossinline transform: (C) -> V,
): R = aggregate(values = values.mapNotNull { transform(it) }, valueType = typeOf<V>())

@PublishedApi
internal inline fun <C, reified V : Any?, R : Any?> Aggregator<V & Any, R>.aggregateOf(
    column: DataColumn<C>,
    crossinline transform: (C) -> V,
): R = aggregateOf(column.asSequence(), transform)

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <T, reified V : Any?, R : Any?> Aggregator<*, R>.aggregateOf(
    frame: DataFrame<T>,
    crossinline expression: RowExpression<T, V>,
): R = (this as Aggregator<V & Any, R>).aggregateOf<DataRow<T>, V, R>(frame.rows().asSequence()) { expression(it, it) }

@PublishedApi
internal fun <T, C, R : Any?> Aggregator<*, R>.aggregateOfDelegated(
    frame: Grouped<T>,
    name: String?,
    body: AggregateBody<T, C>,
): DataFrame<T> =
    frame.aggregateValue(name ?: this.name) {
        body(this, this)
    }

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <T, reified C : Any, reified R : Any?> Aggregator<*, R>.aggregateOf(
    data: Grouped<T>,
    resultName: String? = null,
    crossinline expression: RowExpression<T, C?>,
): DataFrame<T> = data.aggregateOf(resultName, expression, this as Aggregator<C, R>)

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <T, reified C : Any, reified R : Any?> Aggregator<*, R>.aggregateOf(
    data: PivotGroupBy<T>,
    crossinline expression: RowExpression<T, C?>,
): DataFrame<T> = data.aggregateOf<T, C, R>(expression, this as Aggregator<C, R>)

@PublishedApi
internal inline fun <T, reified C : Any, reified R : Any?> Grouped<T>.aggregateOf(
    resultName: String?,
    crossinline expression: RowExpression<T, C?>,
    aggregator: Aggregator<C, R>,
): DataFrame<T> {
    val path = pathOf(resultName ?: aggregator.name)
    val expressionResultType = typeOf<C>()
    return aggregateInternal {
        val value = aggregator.aggregateOf(df, expression)
        val returnType = aggregator.calculateReturnTypeOrNull(
            type = expressionResultType,
            emptyInput = df.isEmpty(),
        )
        yield(
            path = path,
            value = value,
            type = returnType,
            default = null,
            guessType = returnType == null,
        )
    }
}

@PublishedApi
internal inline fun <T, reified C : Any, R : Any?> PivotGroupBy<T>.aggregateOf(
    crossinline expression: RowExpression<T, C?>,
    aggregator: Aggregator<C, R>,
): DataFrame<T> =
    aggregate {
        internal().yield(emptyPath(), aggregator.aggregateOf(this, expression))
    }
