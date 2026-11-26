package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.getOrNull
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.indexOfAggregationResult
import kotlin.reflect.typeOf

@PublishedApi
internal object NoElementAtIndex

/**
 * Selects the best matching value in the [sequence][values]
 * using the provided [Aggregator] `by` the provided [selector].
 *
 * @param V is used to infer whether there are nulls in the values fed to the aggregator!
 */
@Suppress("UNCHECKED_CAST", "ktlint:standard:comment-wrapping")
@PublishedApi
internal inline fun <T, reified V : R, R : Any?> Aggregator<V & Any, R>.aggregateBy(
    values: Sequence<T>,
    crossinline selector: (T) -> V,
): Result<T> {
    val res /* : T | NoElementAtIndex */ = values.elementAtOrElse(
        indexOfAggregationResult(
            values = values.map { selector(it) },
            valueType = typeOf<V>(),
        ),
    ) { NoElementAtIndex }

    return when (res) {
        is NoElementAtIndex ->
            Result.failure(NoSuchElementException("No values to aggregate"))

        else -> Result.success(res as T)
    }
}

/**
 * Selects the best matching value in the [iterable][values]
 * using the provided [Aggregator] `by` the provided [selector].
 *
 * Faster implementation than for sequences.
 *
 * @param V is used to infer whether there are nulls in the values fed to the aggregator!
 */
@Suppress("UNCHECKED_CAST", "ktlint:standard:comment-wrapping")
@PublishedApi
internal inline fun <T, reified V : R, R : Any?> Aggregator<V & Any, R>.aggregateBy(
    values: Iterable<T>,
    crossinline selector: (T) -> V,
): Result<T> {
    val res /* : T | NoElementAtIndex */ = values.elementAtOrElse(
        indexOfAggregationResult(
            values = values.asSequence().map { selector(it) },
            valueType = typeOf<V>(),
        ),
    ) { NoElementAtIndex }

    return when (res) {
        is NoElementAtIndex ->
            Result.failure(NoSuchElementException("No values to aggregate"))

        else -> Result.success(res as T)
    }
}

/**
 * Selects the best matching value in the [column] using the provided [Aggregator] `by` the provided [selector].
 *
 * @param V is used to infer whether there are nulls in the values fed to the aggregator!
 */
@PublishedApi
internal inline fun <T, reified V : R, R : Any?> Aggregator<V & Any, R>.aggregateBy(
    column: DataColumn<T>,
    crossinline selector: (T) -> V,
): Result<T> = aggregateBy(column.values(), selector)

/**
 * Selects the best matching value in the [dataframe][data]
 * using the provided [Aggregator] `by` the provided [rowExpression].
 *
 * @param V is used to infer whether there are nulls in the values fed to the aggregator!
 */
@PublishedApi
internal inline fun <T, reified V : R, R : Any?> Aggregator<V & Any, R>.aggregateByOrNull(
    data: DataFrame<T>,
    crossinline rowExpression: RowExpression<T, V>,
): DataRow<T>? =
    data.getOrNull(
        indexOfAggregationResult(
            values = data.asSequence().map { rowExpression(it, it) },
            valueType = typeOf<V>(),
        ),
    )

/**
 * Selects the best matching value in the [dataframe][data]
 * using the provided [Aggregator] `by` the provided [column].
 *
 * @param V is used to infer whether there are nulls in the values fed to the aggregator!
 */
@PublishedApi
internal inline fun <T, reified V : R, R> Aggregator<V & Any, R>.aggregateByOrNull(
    data: DataFrame<T>,
    column: ColumnReference<V>,
): DataRow<T>? =
    data.getOrNull(
        indexOfAggregationResult(
            values = data.asSequence().map { column.getValue(it) },
            valueType = typeOf<V>(),
        ),
    )
