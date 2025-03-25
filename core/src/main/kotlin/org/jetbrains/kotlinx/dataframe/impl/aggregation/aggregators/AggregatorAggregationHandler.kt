package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KType

@PublishedApi
internal interface AggregatorAggregationHandler<in Value, out Return> : AggregatorRefHolder<Value, Return> {

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [valueType] into account,
     * filtering nulls (only if [type.isMarkedNullable][kotlin.reflect.KType.isMarkedNullable]),
     * and computes a single resulting value.
     *
     * When using [AggregatorAggregationHandler], this can be supplied by the [AggregatorAggregationHandler.aggregateSingle] argument.
     *
     * When the exact [valueType] is unknown, use [aggregateCalculatingValueType].
     */
    fun aggregateSingleSequence(values: Sequence<Value?>, valueType: ValueType): Return

    /**
     * Aggregates the data in the given column and computes a single resulting value.
     * Calls [aggregateSingleColumn] (with [Iterable] and [kotlin.reflect.KType]).
     *
     * See [AggregatorAggregationHandler.aggregateSingleSequence].
     */
    fun aggregateSingleColumn(column: DataColumn<Value?>): Return

    /**
     * Function that can give the return type of [aggregateSingleSequence] as [kotlin.reflect.KType], given the type of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param type The type of the input values.
     * @param emptyInput If `true`, the input values are considered empty. This often affects the return type.
     * @return The return type of [aggregateSingleSequence] as [kotlin.reflect.KType].
     */
    fun calculateReturnTypeOrNull(type: KType, emptyInput: Boolean): KType?

    /**
     * Function that can give the index of the aggregation result in the input [values], if it applies.
     * This is used for [AggregatorAggregationHandlers][AggregatorAggregationHandler] where
     * [Value][Value]`  ==  `[Return][Return], and where the result exists in the input.
     *
     * Like for [SelectingAggregationHandler].
     *
     * Defaults to `-1`.
     */
    fun indexOfAggregationResultSingleSequence(values: Sequence<Value?>, valueType: ValueType): Int
}
