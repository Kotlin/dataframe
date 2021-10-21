package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateBodyInternal

internal interface AggregatableInternal<out T> : Aggregatable<T> {

    fun <R> aggregateInternal(body: AggregateBodyInternal<T, R>): DataFrame<T>

    fun remainingColumnsSelector(): ColumnsSelector<*, *>
}

internal fun <T> Aggregatable<T>.remainingColumnsSelector() = internal().remainingColumnsSelector()

@PublishedApi
internal fun <T> Aggregatable<T>.internal(): AggregatableInternal<T> = this as AggregatableInternal<T>

@PublishedApi
internal fun <T, R> Aggregatable<T>.aggregateInternal(body: AggregateBodyInternal<T, R>): DataFrame<T> = internal().aggregateInternal(body)
