package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.commonType
import kotlin.reflect.KType
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
 *     -> Return
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
 */
internal class FlatteningAggregator<in Value, out Return>(
    name: String,
    getReturnTypeOrNull: CalculateReturnTypeOrNull,
    aggregator: Aggregate<Value, Return>,
) : AggregatorBase<Value, Return>(name, getReturnTypeOrNull, aggregator) {

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     * The columns are flattened into a single list of values, filtering nulls as usual;
     * then the aggregation function is with the common type of the columns.
     */
    override fun aggregate(columns: Iterable<DataColumn<Value?>>): Return {
        val commonType = columns.map { it.type() }.commonType().withNullability(false)
        val allValues = columns.asSequence().flatMap { it.values() }.filterNotNull()
        return aggregate(allValues.asIterable(), commonType)
    }

    /**
     * Function that can give the return type of [aggregate] with columns as [KType],
     * given the multiple types of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     * @return The return type of [aggregate] as [KType].
     */
    override fun calculateReturnTypeOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? {
        val commonType = colTypes.commonType().withNullability(false)
        return calculateReturnTypeOrNull(commonType, colsEmpty)
    }

    /**
     * Creates [FlatteningAggregator].
     *
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull] function.
     * @param aggregator Functional argument for the [aggregate] function.
     */
    class Factory<in Value, out Return>(
        private val getReturnTypeOrNull: CalculateReturnTypeOrNull,
        private val aggregator: Aggregate<Value, Return>,
    ) : AggregatorProvider<FlatteningAggregator<Value, Return>> by AggregatorProvider({ name ->
            FlatteningAggregator(
                name = name,
                getReturnTypeOrNull = getReturnTypeOrNull,
                aggregator = aggregator,
            )
        })
}
