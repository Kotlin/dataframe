package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorMultipleColumnsHandler
import kotlin.reflect.KType

/**
 * Implementation of [AggregatorMultipleColumnsHandler] that simply flattens all input columns.
 * This is useful for aggregators that depend on the distribution of values across multiple columns.
 *
 * @see [TwoStepMultipleColumnsHandler]
 */
internal class FlatteningMultipleColumnsHandler<in Value : Any, out Return : Any?> :
    AggregatorMultipleColumnsHandler<Value, Return> {

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     * The columns are flattened into a single list of values;
     * then the aggregation function is called with the common type of the columns.
     */
    override fun aggregateMultipleColumns(columns: Sequence<DataColumn<Value?>>): Return {
        val allValues = columns.flatMap { it.asSequence() }
        val commonType = aggregator!!.calculateValueType(columns.map { it.type() }.toSet())
        return aggregator!!.aggregateSequence(allValues, commonType)
    }

    /**
     * Function that can give the return type of [aggregateMultipleColumns], given types of the columns.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     */
    override fun calculateReturnTypeMultipleColumns(colTypes: Set<KType>, colsEmpty: Boolean): KType =
        aggregator!!.calculateReturnType(
            valueType = aggregator!!.calculateValueType(colTypes).kType,
            emptyInput = colsEmpty,
        )

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
