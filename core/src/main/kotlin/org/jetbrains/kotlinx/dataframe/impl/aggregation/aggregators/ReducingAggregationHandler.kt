package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asSequence
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
internal class ReducingAggregationHandler<in Value, out Return>(
    val reducer: Reducer<Value, Return>,
    val getReturnTypeOrNull: CalculateReturnTypeOrNull,
) : AggregatorAggregationHandler<Value, Return> {

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [valueType] into account,
     * filtering nulls (only if [type.isMarkedNullable][KType.isMarkedNullable]),
     * and computes a single resulting value.
     *
     * When using [AggregatorAggregationHandler], this can be supplied by the [AggregatorAggregationHandler.aggregateSingle] argument.
     *
     * When the exact [valueType] is unknown, use [Aggregator.aggregateCalculatingValueType].
     */
    @Suppress("UNCHECKED_CAST")
    override fun aggregateSingleSequence(values: Sequence<Value?>, valueType: ValueType): Return {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return reducer(
            // values =
            if (valueType.isMarkedNullable) {
                values.filterNotNull()
            } else {
                values as Sequence<Value & Any>
            },
            // type =
            valueType.withNullability(false),
        )
    }

    /**
     * Aggregates the data in the given column and computes a single resulting value.
     *
     * Nulls are filtered out by default, then [aggregateSingleColumn] (with [Iterable] and [KType]) is called.
     */
    @Suppress("UNCHECKED_CAST")
    override fun aggregateSingleColumn(column: DataColumn<Value?>): Return =
        aggregateSingleSequence(
            values = column.asSequence(),
            valueType = column.type().toValueType(),
        )

    override fun indexOfAggregationResultSingleSequence(values: Sequence<Value?>, valueType: ValueType): Int = -1

    override fun calculateReturnTypeOrNull(type: KType, emptyInput: Boolean): KType? =
        getReturnTypeOrNull(type, emptyInput)
}
