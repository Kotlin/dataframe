package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.IndexOfResult
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Reducer
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregateCalculatingValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.calculateValueType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Implementation of [AggregatorAggregationHandler] which functions like a selector ánd reducer:
 * it takes a sequence of values and returns a single value, which is likely part of the input, but not necessarily.
 *
 * In practice, this means the handler implements both [indexOfAggregationResultSingleSequence]
 * (meaning it can give an index of the result in the input), and [aggregateSequence] with a return type that is
 * potentially different from the input.
 * The return value of [aggregateSequence] and the value at the index retrieved from [indexOfAggregationResultSingleSequence]
 * may differ.
 *
 * @param reducer This function actually does the selection/reduction.
 *   Before it is called, nulls are filtered out. The type of the values is passed as [KType] to the selector.
 * @param indexOfResult This function must be supplied to give the index of the result in the input values.
 * @param getReturnType This function must be supplied to give the return type of [reducer] given some input type and
 *   whether the input is empty.
 *   When selecting, the return type is always `typeOf<Value>()` or `typeOf<Value?>()`, when reducing it can be anything.
 * @see [ReducingAggregationHandler]
 */
internal class HybridAggregationHandler<in Value : Any, out Return : Any?>(
    val reducer: Reducer<Value, Return>,
    val indexOfResult: IndexOfResult<Value>,
    val getReturnType: CalculateReturnType,
) : AggregatorAggregationHandler<Value, Return> {

    /**
     * Function that can give the index of the aggregation result in the input [values].
     * Calls the supplied [indexOfResult] after preprocessing the input.
     */
    @Suppress("UNCHECKED_CAST")
    override fun indexOfAggregationResultSingleSequence(values: Sequence<Value?>, valueType: ValueType): Int {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return indexOfResult(values, valueType)
    }

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

    /**
     * Give the return type of [reducer] given some input type and whether the input is empty.
     * Calls the supplied [getReturnType].
     */
    override fun calculateReturnType(valueType: KType, emptyInput: Boolean): KType =
        getReturnType(valueType.withNullability(false), emptyInput)

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
