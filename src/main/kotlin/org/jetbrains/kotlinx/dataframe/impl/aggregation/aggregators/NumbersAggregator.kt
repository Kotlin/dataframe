package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.commonNumberClass
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
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
