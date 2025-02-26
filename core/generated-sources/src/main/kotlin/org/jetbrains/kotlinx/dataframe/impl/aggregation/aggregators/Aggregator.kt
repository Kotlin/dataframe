package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KType

/**
 * Base interface for all aggregators.
 *
 * Aggregators are used to compute a single value from an [Iterable] of values, a single [DataColumn],
 * or multiple [DataColumns][DataColumn].
 *
 * The [AggregatorBase] class is a base implementation of this interface.
 */
@PublishedApi
internal interface Aggregator<Value, Return> {

    /** The name of this aggregator. */
    val name: String

    /** If `true`, [Value][Value]`  ==  ` [Return][Return]. */
    val preservesType: Boolean

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [type] into account, and computes a single resulting value.
     *
     * When using [AggregatorBase], this can be supplied by the [AggregatorBase.aggregator] argument.
     */
    fun aggregate(values: Iterable<Value>, type: KType): Return?

    /**
     * Aggregates the data in the given column and computes a single resulting value.
     * Nulls are filtered out by default, then the aggregation function (with [Iterable] and [KType]) is called.
     *
     * See [AggregatorBase.aggregate].
     */
    fun aggregate(column: DataColumn<Value?>): Return?

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     *
     * Must be overridden when using [AggregatorBase].
     */
    fun aggregate(columns: Iterable<DataColumn<Value?>>): Return?
}

@PublishedApi
internal fun <Type> Aggregator<*, *>.cast(): Aggregator<Type, Type> = this as Aggregator<Type, Type>

@PublishedApi
internal fun <Value, Return> Aggregator<*, *>.cast2(): Aggregator<Value, Return> = this as Aggregator<Value, Return>
