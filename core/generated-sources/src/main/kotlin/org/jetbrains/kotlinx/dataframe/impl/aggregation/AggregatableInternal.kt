package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateBodyInternal

internal interface AggregatableInternal<out T> : Aggregatable<T> {

    fun remainingColumnsSelector(): ColumnsSelector<*, *>
}

internal fun <T> Aggregatable<T>.remainingColumnsSelector() = internal().remainingColumnsSelector()

@PublishedApi
internal fun <T> Aggregatable<T>.internal(): AggregatableInternal<T> = this as AggregatableInternal<T>

@PublishedApi
internal fun <T, R> Grouped<T>.aggregateInternal(body: AggregateBodyInternal<T, R>): DataFrame<T> = aggregate(body as AggregateGroupedBody<T, R>)
