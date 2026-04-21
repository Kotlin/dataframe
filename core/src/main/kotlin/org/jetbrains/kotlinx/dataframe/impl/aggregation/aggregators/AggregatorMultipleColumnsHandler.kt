package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import kotlin.reflect.KType

/**
 * The multiple columns handler,
 * which specifies how to aggregate multiple columns into a single value by using the supplied
 * [AggregatorAggregationHandler].
 * It can also calculate the return type of the aggregation given all input column types.
 */
public interface AggregatorMultipleColumnsHandler<in Value : Any, out Return : Any?> :
    AggregatorHandler<Value, Return> {

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     * Calls [Aggregator.aggregateSequence] or [Aggregator.aggregateSingleColumn].
     */
    public fun aggregateMultipleColumns(columns: Sequence<DataColumn<Value?>>): Return

    /**
     * Function that can give the return type of [aggregateMultipleColumns], given types of the columns.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     */
    public fun calculateReturnTypeMultipleColumns(colTypes: Set<KType>, colsEmpty: Boolean): KType
}
