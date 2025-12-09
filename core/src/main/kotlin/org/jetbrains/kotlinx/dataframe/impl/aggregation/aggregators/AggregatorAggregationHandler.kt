package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.SelectingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.columns.ValueColumnImpl
import kotlin.reflect.KType

/**
 * The base functionality of the aggregator,
 * which defines how the aggregation of a single [Sequence] or [column][DataColumn] is done.
 * It also provides information on which return type will be given, as [KType], given a [value type][ValueType].
 * It can also provide the index of the result in the input values if it is a selecting aggregator.
 */
public interface AggregatorAggregationHandler<in Value : Any, out Return : Any?> : AggregatorHandler<Value, Return> {

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [valueType] into account,
     * filtering nulls (only if [valueType.type.isMarkedNullable][KType.isMarkedNullable]),
     * and computes a single resulting value.
     *
     * When the exact [valueType] is unknown, use [calculateValueType] or [aggregateCalculatingValueType].
     */
    public fun aggregateSequence(values: Sequence<Value?>, valueType: ValueType): Return
//
//    public fun aggregateSingleColumn(column: DataColumn<Value?>): Return {
//        // It is possible to exploit cached statistic which is proper of ValueColumnImpl
//        if (column is ValueColumnImpl<Value?>) {
//            if (column.max.wasComputed) {
//                return column.max.cachedStatistic as Return
//            } else {
//                val max = aggregateSequence(
//                    values = column.asSequence(),
//                    valueType = column.type().toValueType(),
//                )
//                column.max.cachedStatistic = max
//                column.max.wasComputed = true
//                aggregateSingleColumn(column)
//            }
//        }
//        // Otherwise
//        return aggregateSequence(
//            values = column.asSequence(),
//            valueType = column.type().toValueType(),
//        )
//    }
    /**
     * Aggregates the data in the given column and computes a single resulting value.
     * Calls [aggregateSequence].
     */
    public fun aggregateSingleColumn(column: DataColumn<Value?>): Return =
        aggregateSequence(
            values = column.asSequence(),
            valueType = column.type().toValueType(),
        )

    /**
     * Function that can give the return type of [aggregateSequence] as [KType], given the type of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param valueType The type of the input values.
     * @param emptyInput If `true`, the input values are considered empty. This often affects the return type.
     * @return The return type of [aggregateSequence] as [KType].
     */
    public fun calculateReturnType(valueType: KType, emptyInput: Boolean): KType

    /**
     * Function that can give the index of the aggregation result in the input [values], if it applies.
     * This is used for [AggregatorAggregationHandlers][AggregatorAggregationHandler] where
     * [Value][Value]`  ==  `[Return][Return], and where the result exists in the input.
     *
     * Like for [SelectingAggregationHandler].
     *
     * Defaults to `-1`.
     */
    public fun indexOfAggregationResultSingleSequence(values: Sequence<Value?>, valueType: ValueType): Int
}
