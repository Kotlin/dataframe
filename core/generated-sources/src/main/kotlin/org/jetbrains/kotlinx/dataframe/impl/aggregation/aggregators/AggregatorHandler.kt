package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

/**
 * Common interface for [Aggregator] handlers or "injector" objects that can build up an [Aggregator] instance.
 *
 * When an [Aggregator] is instantiated,
 * the [init] function of each [AggregatorAggregationHandlers][AggregatorAggregationHandler] is called,
 * which allows the handler to refer to [Aggregator] instance via [aggregator].
 */
internal interface AggregatorHandler<in Value : Any, out Return : Any?> {

    /**
     * Reference to the aggregator instance.
     *
     * Can only be used once [init] has run.
     */
    var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>?

    fun init(aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>) {
        this.aggregator = aggregator
    }
}
