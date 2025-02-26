package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.commonType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Simple [Aggregator] implementation.
 *
 * Nulls are filtered from columns.
 *
 * When called on multiple columns,
 * the columns are flattened into a single list of values, filtering nulls as usual;
 * then the aggregation function is called with their common type.
 *
 * See [TwoStepAggregator] for different behavior for multiple columns.
 *
 * @param name The name of this aggregator.
 * @param aggregator Functional argument for the [aggregate] function.
 *   Note that it must be able to handle `null` values for the [Iterable] overload of [aggregate].
 * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
 */
internal class OneStepAggregator<Value, Return>(
    name: String,
    aggregator: (values: Iterable<Value>, type: KType) -> Return?,
    override val preservesType: Boolean,
) : AggregatorBase<Value, Return>(name, aggregator) {

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     * The columns are flattened into a single list of values, filtering nulls as usual;
     * then the aggregation function is with the common type of the columns.
     */
    override fun aggregate(columns: Iterable<DataColumn<Value?>>): Return? {
        val commonType = columns.map { it.type() }.commonType().withNullability(false)
        val allValues = columns.asSequence().flatMap { it.values() }.filterNotNull()
        return aggregate(allValues.asIterable(), commonType)
    }

    /**
     * Creates [OneStepAggregator].
     *
     * @param aggregator Functional argument for the [aggregate] function.
     * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
     */
    class Factory<Value, Return>(
        private val aggregator: (Iterable<Value>, KType) -> Return?,
        private val preservesType: Boolean,
    ) : AggregatorProvider<OneStepAggregator<Value, Return>> by AggregatorProvider({ name ->
            OneStepAggregator(name = name, aggregator = aggregator, preservesType = preservesType)
        })
}
