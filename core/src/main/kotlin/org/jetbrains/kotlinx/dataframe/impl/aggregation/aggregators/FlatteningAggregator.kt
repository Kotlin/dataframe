package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.commonType
import kotlin.reflect.full.withNullability

/**
 * Simple [Aggregator] implementation with flattening behavior for multiple columns.
 *
 * Nulls are filtered from columns.
 *
 * When called on multiple columns,
 * the columns are flattened into a single list of values, filtering nulls as usual;
 * then the aggregation function is called with their common type.
 *
 * ```
 * Iterable<Column<Value?>>
 *     -> Iterable<Value> // flattened without nulls
 *     -> aggregator(Iterable<Value>, common colType)
 *     -> Return?
 * ```
 *
 * This is essential for aggregators that depend on the distribution of all values across the dataframe, like
 * the median, percentile, and standard deviation.
 *
 * See [TwoStepAggregator] for different behavior for multiple columns.
 *
 * @param name The name of this aggregator.
 * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull] function.
 * @param aggregator Functional argument for the [aggregate] function.
 *   Note that it must be able to handle `null` values for the [Iterable] overload of [aggregate].
 * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
 */
internal class FlatteningAggregator<in Value, out Return>(
    name: String,
    getReturnTypeOrNull: CalculateReturnTypeOrNull,
    aggregator: Aggregate<Value, Return>,
    override val preservesType: Boolean,
) : AggregatorBase<Value, Return>(name, getReturnTypeOrNull, aggregator) {

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
     * Creates [FlatteningAggregator].
     *
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull] function.
     * @param aggregator Functional argument for the [aggregate] function.
     * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
     */
    class Factory<in Value, out Return>(
        private val getReturnTypeOrNull: CalculateReturnTypeOrNull,
        private val aggregator: Aggregate<Value, Return>,
        private val preservesType: Boolean,
    ) : AggregatorProvider<FlatteningAggregator<Value, Return>> by AggregatorProvider({ name ->
            FlatteningAggregator(
                name = name,
                getReturnTypeOrNull = getReturnTypeOrNull,
                aggregator = aggregator,
                preservesType = preservesType,
            )
        })
}
