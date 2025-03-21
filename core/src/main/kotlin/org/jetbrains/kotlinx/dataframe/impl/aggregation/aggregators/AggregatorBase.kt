package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asIterable
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Abstract base class for [aggregators][Aggregator].
 *
 * Aggregators are used to compute a single value from an [Iterable] of values, a single [DataColumn],
 * or multiple [DataColumns][DataColumn].
 *
 * @param name The name of this aggregator.
 */
internal abstract class AggregatorBase<in Value, out Return>(
    override val name: String,
    private val getReturnTypeOrNull: CalculateReturnTypeOrNull,
    private val aggregateSingle: Aggregate<Value, Return>,
) : Aggregator<Value, Return> {

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [valueType] into account,
     * filtering nulls (only if [type.isMarkedNullable][KType.isMarkedNullable]),
     * and computes a single resulting value.
     *
     * When using [AggregatorBase], this can be supplied by the [AggregatorBase.aggregateSingle] argument.
     *
     * When the exact [valueType] is unknown, use [Aggregator.aggregateCalculatingValueType].
     */
    @Suppress("UNCHECKED_CAST")
    override fun aggregateSingleIterable(values: Iterable<Value?>, valueType: KType): Return =
        aggregateSingle(
            // values =
            if (valueType.isMarkedNullable) {
                values.asSequence().filterNotNull().asIterable()
            } else {
                values as Iterable<Value & Any>
            },
            // type =
            valueType.withNullability(false),
        )

    /**
     * Aggregates the data in the given column and computes a single resulting value.
     *
     * Nulls are filtered out by default, then [aggregateSingleColumn] (with [Iterable] and [KType]) is called.
     */
    @Suppress("UNCHECKED_CAST")
    final override fun aggregateSingleColumn(column: DataColumn<Value?>): Return =
        aggregateSingleIterable(
            values = column.asIterable(),
            valueType = column.type(),
        )

    final override fun calculateReturnTypeOrNull(type: KType, emptyInput: Boolean): KType? =
        getReturnTypeOrNull(type, emptyInput)
}
