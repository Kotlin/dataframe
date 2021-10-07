package org.jetbrains.dataframe.impl.aggregation.aggregators

import org.jetbrains.dataframe.columns.DataColumn
import org.jetbrains.dataframe.commonType
import kotlin.reflect.KType

internal class TwoStepAggregator<C, R>(
    name: String,
    aggregateWithType: (Iterable<C>, KType) -> R?,
    private val aggregateValues: (Iterable<R>, KType) -> R?,
    override val preservesType: Boolean
) : AggregatorBase<C, R>(name, aggregateWithType) {

    override fun aggregate(columns: Iterable<DataColumn<C?>>): R? {
        val columnValues = columns.mapNotNull { aggregate(it) }
        val commonType = columnValues.map { it.javaClass.kotlin }.commonType(false)
        return aggregateValues(columnValues, commonType)
    }

    class Factory<C, R>(
        private val aggregateWithType: (Iterable<C>, KType) -> R?,
        private val aggregateValues: (Iterable<R>, KType) -> R?,
        private val preservesType: Boolean
    ) : AggregatorProvider<C, R> {
        override fun create(name: String) = TwoStepAggregator(name, aggregateWithType, aggregateValues, preservesType)
    }
}
