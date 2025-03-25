package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.CandidateForRemoval
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.indexOfAggregationResult
import org.jetbrains.kotlinx.dataframe.impl.namedValues
import kotlin.reflect.typeOf

@CandidateForRemoval
internal fun <T> Grouped<T>.aggregateByOrNull(body: DataFrameExpression<T, DataRow<T>?>): DataFrame<T> {
    require(this is GroupBy<*, T>)
    val keyColumns = keys.columnNames().toSet()
    return aggregateInternal {
        val row = body(df, df)
        row?.namedValues()?.forEach {
            if (!keyColumns.contains(it.name)) yield(it)
        }
    }.cast()
}

/**
 * Aggregates the values of the column using the provided [Aggregator] `by` the provided [selector].
 * H
 */
@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <C, reified V : Comparable<V>> Aggregator<V, V?>.aggregateByOrNull(
    values: Sequence<C>,
    noinline selector: (C) -> V?,
): C? =
    values.elementAtOrNull(
        indexOfAggregationResult(
            values = values.map(selector),
            valueType = typeOf<V>(),
        ),
    )

@PublishedApi
internal inline fun <C, reified V : Comparable<V>?> Aggregator<V, V?>.aggregateByOrNull(
    column: DataColumn<C>,
    noinline selector: (C) -> V?,
): C? = aggregateByOrNull(column.asSequence(), selector)
