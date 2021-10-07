package org.jetbrains.dataframe.impl.aggregation.aggregators

import org.jetbrains.dataframe.columns.DataColumn
import org.jetbrains.dataframe.createStarProjectedType
import org.jetbrains.dataframe.impl.commonNumberClass
import kotlin.reflect.KType

internal class NumbersAggregator<C : Number>(name: String, aggregate: (Iterable<C>, KType) -> C?) :
    AggregatorBase<C, C>(name, aggregate) {

    override fun aggregate(columns: Iterable<DataColumn<C?>>): C? {
        val columnValues = columns.mapNotNull { aggregate(it) }
        val classes = columnValues.map { it.javaClass.kotlin }
        return aggregate(columnValues, classes.commonNumberClass().createStarProjectedType(false))
    }

    class Factory(private val aggregate: Iterable<Number>.(KType) -> Number?) : AggregatorProvider<Number, Number> {
        override fun create(name: String) = NumbersAggregator(name, aggregate)
    }

    override val preservesType = false
}
