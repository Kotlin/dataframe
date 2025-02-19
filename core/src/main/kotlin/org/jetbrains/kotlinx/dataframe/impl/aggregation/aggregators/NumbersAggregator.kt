package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.commonNumberType
import org.jetbrains.kotlinx.dataframe.impl.convertToCommonNumberType
import org.jetbrains.kotlinx.dataframe.impl.types
import kotlin.reflect.KProperty
import kotlin.reflect.KType

internal class NumbersAggregator(name: String, aggregate: (Iterable<Number>, KType) -> Number?) :
    AggregatorBase<Number, Number>(name, aggregate) {

    override fun aggregate(columns: Iterable<DataColumn<Number?>>): Number? =
        aggregateMixed(
            values = columns.mapNotNull { aggregate(it) },
            types = columns.map { it.type() }.toSet(),
        )

    class Factory(private val aggregate: Iterable<Number>.(KType) -> Number?) : AggregatorProvider<Number, Number> {
        override fun create(name: String) = NumbersAggregator(name, aggregate)

        override operator fun getValue(obj: Any?, property: KProperty<*>): NumbersAggregator = create(property.name)
    }

    /**
     * Can aggregate numbers with different types by first converting them to a compatible type.
     */
    @Suppress("UNCHECKED_CAST")
    fun aggregateMixed(values: Iterable<Number>, types: Set<KType>): Number? {
        val commonType = types.commonNumberType()
        return aggregate(
            values = values.convertToCommonNumberType(commonType),
            type = commonType,
        )
    }

    override val preservesType = false
}
