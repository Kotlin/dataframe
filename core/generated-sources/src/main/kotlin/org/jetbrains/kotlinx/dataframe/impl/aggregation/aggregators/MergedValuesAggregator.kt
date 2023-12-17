package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.commonType
import kotlin.reflect.KProperty
import kotlin.reflect.KType

internal class MergedValuesAggregator<C : Any, R>(
    name: String,
    val aggregateWithType: (Iterable<C?>, KType) -> R?,
    override val preservesType: Boolean
) : AggregatorBase<C, R>(name, aggregateWithType) {

    override fun aggregate(columns: Iterable<DataColumn<C?>>): R? {
        val commonType = columns.map { it.type() }.commonType()
        val allValues = columns.flatMap { it.values() }
        return aggregateWithType(allValues, commonType)
    }

    fun aggregateMixed(values: Iterable<C?>): R? {
        var hasNulls = false
        val classes = values.mapNotNull {
            if (it == null) {
                hasNulls = true
                null
            } else {
                it.javaClass.kotlin
            }
        }
        return aggregateWithType(values, classes.commonType(hasNulls))
    }

    class Factory<C : Any, R>(
        private val aggregateWithType: (Iterable<C?>, KType) -> R?,
        private val preservesType: Boolean
    ) : AggregatorProvider<C, R> {
        override fun create(name: String) = MergedValuesAggregator(name, aggregateWithType, preservesType)

        override operator fun getValue(obj: Any?, property: KProperty<*>): MergedValuesAggregator<C, R> = create(property.name)
    }
}
