package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal interface FlatteningAggregator<in Value, out Return> : Aggregator<Value, Return> {
    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     * The columns are flattened into a single list of values, filtering nulls as usual;
     * then the aggregation function is with the common type of the columns.
     */
    override fun aggregateMultipleColumns(columns: Sequence<DataColumn<Value?>>): Return {
        val commonType = calculateValueType(columns.map { it.type() }.toSet())
        val allValues = columns.flatMap { it.values() }.filterNotNull()
        return aggregateSingleSequence(allValues, commonType.withNullability(false))
    }

    /**
     * Function that can give the return type of [aggregateSingleIterable] with columns as [KType],
     * given the multiple types of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     * @return The return type of [aggregateSingleIterable] as [KType].
     */
    override fun calculateReturnTypeMultipleColumnsOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? =
        calculateReturnTypeOrNull(calculateValueType(colTypes).withNullability(false), colsEmpty)
}
