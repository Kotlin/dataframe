package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

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
internal interface Aggregator<in Value, out Return> {

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
     *
     * When the exact [type] is unknown, use [aggregateCalculatingType].
     */
    fun aggregate(values: Iterable<Value>, type: KType): Return?

    /**
     * Aggregates the data in the given column and computes a single resulting value.
     * Nulls are filtered out by default, then [aggregate] (with [Iterable] and [KType]) is called.
     *
     * See [AggregatorBase.aggregate].
     */
    fun aggregate(column: DataColumn<Value?>): Return?

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     */
    fun aggregate(columns: Iterable<DataColumn<Value?>>): Return?

    /**
     * Special case of [aggregate] with [Iterable] that calculates the common type of the values at runtime.
     * Without [valueTypes], this is a heavy operation and should be avoided when possible.
     *
     * @param values The values to be aggregated.
     * @param valueTypes The types of the values.
     *   If provided, this can be used to avoid calculating the types of [values] at runtime with reflection.
     *   It should contain all types of [values].
     *   If `null`, the types of [values] will be calculated at runtime (heavy!).
     */
    fun aggregateCalculatingType(values: Iterable<Value>, valueTypes: Set<KType>? = null): Return?

    /**
     * Function that can give the return type of [aggregate] as [KType], given the type of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param type The type of the input values.
     * @param emptyInput If `true`, the input values are considered empty. This often affects the return type.
     * @return The return type of [aggregate] as [KType].
     */
    fun calculateReturnTypeOrNull(type: KType, emptyInput: Boolean): KType?
}

@PublishedApi
internal fun <Type> Aggregator<*, *>.cast(): Aggregator<Type, Type> = this as Aggregator<Type, Type>

@PublishedApi
internal fun <Value, Return> Aggregator<*, *>.cast2(): Aggregator<Value, Return> = this as Aggregator<Value, Return>

/** Type alias for [Aggregator.calculateReturnTypeOrNull] */
internal typealias CalculateReturnTypeOrNull = (type: KType, emptyInput: Boolean) -> KType?

/** Type alias for [Aggregator.aggregate]. */
internal typealias Aggregate<Value, Return> = Iterable<Value>.(type: KType) -> Return?

/** Common case for [CalculateReturnTypeOrNull], preserves return type, but makes it nullable for empty inputs. */
internal val preserveReturnTypeNullIfEmpty: CalculateReturnTypeOrNull = { type, emptyInput ->
    type.withNullability(emptyInput)
}
