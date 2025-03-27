package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
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

/**
 * Aggregates [values] by first applying [transform] to each element of the sequence and then
 * applying the [Aggregator] ([this]) to the resulting sequence.
 *
 * @param V is used to infer whether there are nulls in the values fed to the aggregator!
 */
@PublishedApi
internal inline fun <C, reified V : Any?, R : Any?> Aggregator<V & Any, R>.aggregateOf(
    values: Sequence<C>,
    crossinline transform: (C) -> V,
): R = aggregate(values = values.map { transform(it) }, valueType = typeOf<V>())

/**
 * Aggregates [column] by first applying [transform] to each element of the column and then
 * applying the [Aggregator] ([this]) to the resulting sequence.
 *
 * @param V is used to infer whether there are nulls in the values fed to the aggregator!
 */
@PublishedApi
internal inline fun <C, reified V : Any?, R : Any?> Aggregator<V & Any, R>.aggregateOf(
    column: DataColumn<C>,
    crossinline transform: (C) -> V,
): R = aggregateOf(column.asSequence(), transform)

/**
 * Aggregates [frame] by first applying [expression] to each row of the frame and then
 * applying the [Aggregator] ([this]) to the resulting sequence.
 *
 * @param V is used to infer whether there are nulls in the values fed to the aggregator!
 */
@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <T, reified V : Any?, R : Any?> Aggregator<*, R>.aggregateOf(
    frame: DataFrame<T>,
    crossinline expression: RowExpression<T, V>,
): R = (this as Aggregator<V & Any, R>).aggregateOf(frame.rows().asSequence()) { expression(it, it) }

@PublishedApi
internal fun <T, C, R : Any?> Aggregator<*, R>.aggregateOfDelegated(
    frame: Grouped<T>,
    name: String?,
    body: AggregateBody<T, C>,
): DataFrame<T> =
    frame.aggregateValue(name ?: this.name) {
        body(this, this)
    }

/**
 * Aggregates [data] by first applying [expression] to each row of the frame and then
 *
 * @param C is used to infer whether there are nulls in the values fed to the aggregator!
 */
@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <T, reified C : Any?, reified R : Any?> Aggregator<*, R>.aggregateOf(
    data: Grouped<T>,
    resultName: String? = null,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = data.aggregateOf(resultName, expression, this as Aggregator<C, R>)

/**
 * @param C is used to infer whether there are nulls in the values fed to the aggregator!
 */
@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <T, reified C : Any?, reified R : Any?> Aggregator<*, R>.aggregateOf(
    data: PivotGroupBy<T>,
    crossinline expression: RowExpression<T, C>,
): DataFrame<T> = data.aggregateOf(expression, this as Aggregator<C, R>)

/**
 * @param C is used to infer whether there are nulls in the values fed to the aggregator!
 */
@PublishedApi
internal inline fun <T, reified C : Any?, reified R : Any?> Grouped<T>.aggregateOf(
    resultName: String?,
    crossinline expression: RowExpression<T, C>,
    aggregator: Aggregator<C & Any, R>,
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

/**
 * @param C is used to infer whether there are nulls in the values fed to the aggregator!
 */
@PublishedApi
internal inline fun <T, reified C : Any?, R : Any?> PivotGroupBy<T>.aggregateOf(
    crossinline expression: RowExpression<T, C>,
    aggregator: Aggregator<C & Any, R>,
): DataFrame<T> =
    aggregate {
        internal().yield(emptyPath(), aggregator.aggregateOf(this, expression))
    }
