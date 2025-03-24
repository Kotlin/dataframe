package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KType

internal class NoMultipleColumnsHandler<in Value, out Return> : AggregatorMultipleColumnsHandler<Value, Return> {

    override fun aggregateMultipleColumns(columns: Sequence<DataColumn<@UnsafeVariance Value?>>): Return = error("")

    override fun calculateReturnTypeMultipleColumnsOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? = error("")

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
