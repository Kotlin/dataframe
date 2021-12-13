package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KType

@PublishedApi
internal interface Aggregator<C, R> {

    val name: String

    fun aggregate(column: DataColumn<C?>): R?

    val preservesType: Boolean

    fun aggregate(columns: Iterable<DataColumn<C?>>): R?

    fun aggregate(values: Iterable<C>, type: KType): R?
}

@PublishedApi
internal fun <T> Aggregator<*, *>.cast(): Aggregator<T, T> = this as Aggregator<T, T>

@PublishedApi
internal fun <T, P> Aggregator<*, *>.cast2(): Aggregator<T, P> = this as Aggregator<T, P>
