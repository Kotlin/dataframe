package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.convertToUnifiedNumberType
import org.jetbrains.kotlinx.dataframe.impl.unifiedNumberType
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 *
 */
internal class NumbersAggregator(name: String, aggregator: (values: Iterable<Number>, numberType: KType) -> Number?) :
    AggregatorBase<Number, Number>(name, aggregator) {

    override fun aggregate(column: DataColumn<Number?>): Number? = super.aggregate(column)

    override fun aggregate(values: Iterable<Number>, type: KType): Number? = super.aggregate(values, type)

    override fun aggregate(columns: Iterable<DataColumn<Number?>>): Number? =
        aggregateUnifyingNumbers(
            values = columns.mapNotNull { aggregate(it) },
            numberTypes = columns.map { it.type() }.toSet(),
        )

    class Factory(private val aggregate: Iterable<Number>.(numberType: KType) -> Number?) :
        AggregatorProvider<NumbersAggregator> by AggregatorProvider({ name ->
            NumbersAggregator(name = name, aggregator = aggregate)
        })

    /**
     * Can aggregate numbers with different types by first converting them to a compatible type.
     */
    @Suppress("UNCHECKED_CAST")
    fun aggregateUnifyingNumbers(values: Iterable<Number>, numberTypes: Set<KType>): Number? {
        if (numberTypes.size < 2) {
            return aggregate(values, numberTypes.singleOrNull() ?: typeOf<Number>())
        }

        val commonType = numberTypes.unifiedNumberType()
        return aggregate(
            values = values.convertToUnifiedNumberType(commonType),
            type = commonType,
        )
    }

    override val preservesType = false
}
