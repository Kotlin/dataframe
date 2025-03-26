package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorMultipleColumnsHandler
import kotlin.reflect.KType

internal class NoMultipleColumnsHandler<in Value : Any, out Return : Any?> :
    AggregatorMultipleColumnsHandler<Value, Return> {

    override fun aggregateMultipleColumns(columns: Sequence<DataColumn<@UnsafeVariance Value?>>): Return = error("")

    override fun calculateReturnTypeMultipleColumnsOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? = error("")

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
