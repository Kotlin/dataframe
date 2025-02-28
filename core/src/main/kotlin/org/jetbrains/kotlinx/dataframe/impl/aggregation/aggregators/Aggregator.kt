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
 *
 * @param Value The type of the values to be aggregated.
 *   This can be nullable for [Iterables][Iterable] or not, depending on the use case.
 *   For columns, [Value] will always be considered nullable; nulls are filtered out from columns anyway.
 * @param Return The type of the resulting value. It doesn't matter if this is nullable or not, as the aggregator
 *   will always return a [Return]`?`.
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

    /**
     * Special case of [aggregate] with [Iterable] that calculates the common type of the values at runtime.
     * This is a heavy operation and should be avoided when possible.
     * If provided, [valueTypes] can be used to avoid calculating the types of [values] at runtime.
     */
    fun aggregateCalculatingType(values: Iterable<Value>, valueTypes: Set<KType>? = null): Return?
}

@PublishedApi
internal fun <Type> Aggregator<*, *>.cast(): Aggregator<Type, Type> = this as Aggregator<Type, Type>

@PublishedApi
internal fun <Value, Return> Aggregator<*, *>.cast2(): Aggregator<Value, Return> = this as Aggregator<Value, Return>
