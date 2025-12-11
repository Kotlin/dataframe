package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.SelectingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.columns.WrappedStatistic
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

    /**
     * Aggregates the data in the given column and computes a single resulting value.
     * Calls [aggregateSequence].
     */
    public fun aggregateSingleColumn(column: DataColumn<Value?>): Return {
        println("NOT ValueColumnImpl")
        return aggregateSequence(
            values = column.asSequence(),
            valueType = column.type().toValueType(),
        )
    }

    /**
     * optimized override of [aggregateSingleColumn],
     * preferred when column's runtime type is ValueColumnInternal so that
     * it is possible to exploit cached statistics which are proper of ValueColumnInternal
     */
    public fun aggregateSingleColumn(
        column: DataColumn<Value?>,
        wrappedStatistic: WrappedStatistic,
        skipNaN: Boolean,
    ): Return {
        when {
            skipNaN && wrappedStatistic.wasComputedSkippingNaN -> {
                println("valuecol, NOT COMPUTED")
                return wrappedStatistic.statisticComputedSkippingNaN as Return
            }

            (!skipNaN) && wrappedStatistic.wasComputedNotSkippingNaN -> {
                println("valuecol, NOT COMPUTED")
                return wrappedStatistic.statisticComputedNotSkippingNaN as Return
            }

            else -> {
                val statistic = aggregateSequence(
                    values = column.asSequence(),
                    valueType = column.type().toValueType(),
                )
                if (skipNaN) {
                    wrappedStatistic.wasComputedSkippingNaN = true
                    wrappedStatistic.statisticComputedSkippingNaN = statistic
                } else {
                    wrappedStatistic.wasComputedNotSkippingNaN = true
                    wrappedStatistic.statisticComputedNotSkippingNaN = statistic
                }
                println("valuecol, COMPUTED")
                return aggregateSingleColumn(column, wrappedStatistic, skipNaN)
            }
        }
    }

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
