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
 *   The input can always have nulls, they are filtered out.
 * @param Return The type of the resulting value. Can optionally be nullable.
 */
@PublishedApi
internal interface Aggregator<in Value, out Return> {

    /** The name of this aggregator. */
    val name: String

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [valueType] into account,
     * filtering nulls (only if [type.isMarkedNullable][KType.isMarkedNullable]),
     * and computes a single resulting value.
     *
     * When using [AggregatorBase], this can be supplied by the [AggregatorBase.aggregateSingle] argument.
     *
     * When the exact [valueType] is unknown, use [aggregateCalculatingValueType].
     */
    fun aggregateSingleSequence(values: Sequence<Value?>, valueType: KType): Return

    fun calculateValueType(valueTypes: Set<KType>): KType

    fun calculateValueType(values: Sequence<Value?>): KType

    /**
     * Aggregates the data in the given column and computes a single resulting value.
     * Calls [aggregateSingleColumn] (with [Iterable] and [KType]).
     *
     * See [AggregatorBase.aggregateSingleIterable].
     */
    fun aggregateSingleColumn(column: DataColumn<Value?>): Return

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     */
    fun aggregateMultipleColumns(columns: Sequence<DataColumn<Value?>>): Return

    /**
     * Function that can give the return type of [aggregateSingleIterable] as [KType], given the type of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param type The type of the input values.
     * @param emptyInput If `true`, the input values are considered empty. This often affects the return type.
     * @return The return type of [aggregateSingleIterable] as [KType].
     */
    fun calculateReturnTypeOrNull(type: KType, emptyInput: Boolean): KType?

    /**
     * Function that can give the return type of [aggregateSingleIterable] with columns as [KType],
     * given the multiple types of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     * @return The return type of [aggregateSingleIterable] as [KType].
     */
    fun calculateReturnTypeMultipleColumnsOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType?

    val ref: Aggregator<Value, Return> get() = this
}

internal fun <Value, Return> Aggregator<Value, Return>.aggregate(values: Sequence<Value?>, valueType: KType) =
    aggregateSingleSequence(values, valueType)

internal fun <Value, Return> Aggregator<Value, Return>.calculateValueType(
    values: Sequence<Value?>,
    valueTypes: Set<KType>? = null,
) = if (valueTypes != null && valueTypes.isNotEmpty()) {
    calculateValueType(valueTypes)
} else {
    calculateValueType(values)
}

internal fun <Value, Return> Aggregator<Value, Return>.aggregateCalculatingValueType(
    values: Sequence<Value?>,
    valueTypes: Set<KType>? = null,
) = aggregateSingleSequence(
    values = values,
    valueType = calculateValueType(values, valueTypes),
)

internal fun <Value, Return> Aggregator<Value, Return>.aggregate(column: DataColumn<Value?>) =
    aggregateSingleColumn(column)

internal fun <Value, Return> Aggregator<Value, Return>.aggregate(columns: Sequence<DataColumn<Value?>>) =
    aggregateMultipleColumns(columns)

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <Type> Aggregator<*, *>.cast(): Aggregator<Type, Type> = this as Aggregator<Type, Type>

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <Value, Return> Aggregator<*, *>.cast2(): Aggregator<Value, Return> = this as Aggregator<Value, Return>

/** Type alias for [Aggregator.calculateReturnTypeMultipleColumnsOrNull] */
internal typealias CalculateReturnTypeOrNull = (type: KType, emptyInput: Boolean) -> KType?

/**
 * Type alias for the argument for [Aggregator.aggregateSingleSequence].
 * Nulls have already been filtered out when this argument is called.
 */
internal typealias Aggregate<Value, Return> = Sequence<Value & Any>.(type: KType) -> Return

internal typealias AggregateBy<Source, Value, Return> =
    Sequence<Source>.(sourceType: KType, valueType: KType, selector: (Source) -> Value) -> Return

/** Common case for [CalculateReturnTypeOrNull], preserves return type, but makes it nullable for empty inputs. */
internal val preserveReturnTypeNullIfEmpty: CalculateReturnTypeOrNull = { type, emptyInput ->
    type.withNullability(emptyInput)
}
