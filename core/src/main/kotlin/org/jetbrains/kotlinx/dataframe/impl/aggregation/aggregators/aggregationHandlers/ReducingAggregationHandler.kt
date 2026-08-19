package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Reducer
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregateCalculatingValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.calculateValueType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Implementation of [AggregatorAggregationHandler] which functions like a reducer:
 * it takes a sequence of values and returns a single value, which can be a completely different type.
 *
 * @param reducer This function actually does the reduction.
 *   Before it is called, nulls are filtered out. The type of the values is passed as [KType] to the reducer.
 * @param getReturnType This function must be supplied to give the return type of [reducer] given some input type and
 *   whether the input is empty.
 * @see [SelectingAggregationHandler]
 */
internal class ReducingAggregationHandler<in Value : Any, out Return : Any?>(
    val reducer: Reducer<Value, Return>,
    val getReturnType: CalculateReturnType,
) : AggregatorAggregationHandler<Value, Return> {

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [valueType] into account,
     * filtering nulls (only if [valueType.type.isMarkedNullable][KType.isMarkedNullable]),
     * and computes a single resulting value.
     *
     * When the exact [valueType] is unknown, use [calculateValueType] or [aggregateCalculatingValueType].
     *
     * Calls the supplied [reducer].
     */
    @Suppress("UNCHECKED_CAST")
    override fun aggregateSequence(values: Sequence<Value?>, valueType: ValueType): Return {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return reducer(
            // values =
            if (valueType.isMarkedNullable) {
                values.filterNotNull()
            } else {
                values as Sequence<Value>
            },
            // type =
            valueType.withNullability(false),
        )
    }

    /** This function always returns `-1` because the result of a reducer is not in the input values. */
    override fun indexOfAggregationResultSingleSequence(values: Sequence<Value?>, valueType: ValueType): Int = -1

    /**
     * Give the return type of [reducer] given some input type and whether the input is empty.
     * Calls the supplied [getReturnType].
     */
    override fun calculateReturnType(valueType: KType, emptyInput: Boolean): KType =
        getReturnType(valueType.withNullability(false), emptyInput)

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
