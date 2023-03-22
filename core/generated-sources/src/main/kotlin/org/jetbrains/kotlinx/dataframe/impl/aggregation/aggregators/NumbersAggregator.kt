package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.commonNumberClass
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import kotlin.reflect.KProperty
import kotlin.reflect.KType

internal class NumbersAggregator<C : Number>(name: String, aggregate: (Iterable<C>, KType) -> C?) :
    AggregatorBase<C, C>(name, aggregate) {

    override fun aggregate(columns: Iterable<DataColumn<C?>>): C? {
        return aggregateMixed(columns.mapNotNull { aggregate(it) })
    }

    class Factory(private val aggregate: Iterable<Number>.(KType) -> Number?) : AggregatorProvider<Number, Number> {
        override fun create(name: String) = NumbersAggregator(name, aggregate)

        override operator fun getValue(obj: Any?, property: KProperty<*>): NumbersAggregator<Number> = create(property.name)
    }

    fun aggregateMixed(values: Iterable<C>): C? {
        val classes = values.map { it.javaClass.kotlin }
        return aggregate(values, classes.commonNumberClass().createStarProjectedType(false))
    }

    override val preservesType = false
}
