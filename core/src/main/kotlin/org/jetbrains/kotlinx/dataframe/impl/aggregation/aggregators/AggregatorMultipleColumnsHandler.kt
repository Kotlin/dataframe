package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KType

internal interface AggregatorMultipleColumnsHandler<in Value, out Return> : AggregatorRefHolder<Value, Return> {

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     */
    fun aggregateMultipleColumns(columns: Sequence<DataColumn<Value?>>): Return

    /**
     * Function that can give the return type of [aggregateSingleSequence] with columns as [kotlin.reflect.KType],
     * given the multiple types of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     * @return The return type of [aggregateSingleSequence] as [kotlin.reflect.KType].
     */
    fun calculateReturnTypeMultipleColumnsOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType?
}
