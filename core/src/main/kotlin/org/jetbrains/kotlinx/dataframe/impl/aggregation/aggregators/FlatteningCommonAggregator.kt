package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

/**
 * Simple [Aggregator] implementation with flattening behavior for multiple columns.
 *
 * Nulls are filtered out.
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
 * See [TwoStepCommonAggregator] for different behavior for multiple columns.
 *
 * @param name The name of this aggregator.
 * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeMultipleColumnsOrNull] function.
 * @param aggregateSingle Functional argument for the [aggregateSingleIterable] function.
 *   Note that it must be able to handle `null` values for the [Iterable] overload of [aggregateSingleIterable].
 */
internal class FlatteningCommonAggregator<in Value, out Return>(
    name: String,
    getReturnTypeOrNull: CalculateReturnTypeOrNull,
    aggregateSingle: Aggregate<Value, Return>,
) : AggregatorBase<Value, Return>(name, getReturnTypeOrNull, aggregateSingle),
    FlatteningAggregator<Value, Return>,
    CommonAggregator<Value, Return> {

    /**
     * Creates [FlatteningCommonAggregator].
     *
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeMultipleColumnsOrNull] function.
     * @param aggregator Functional argument for the [aggregateSingleIterable] function.
     */
    class Factory<in Value, out Return>(
        private val getReturnTypeOrNull: CalculateReturnTypeOrNull,
        private val aggregator: Aggregate<Value, Return>,
    ) : AggregatorProvider<FlatteningCommonAggregator<Value, Return>> by AggregatorProvider({ name ->
            FlatteningCommonAggregator(
                name = name,
                getReturnTypeOrNull = getReturnTypeOrNull,
                aggregateSingle = aggregator,
            )
        })
}
