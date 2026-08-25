package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

/**
 * Common interface for [<code>Aggregator</code>][Aggregator] handlers or "injector" objects that can build up an [<code>Aggregator</code>][Aggregator] instance.
 *
 * When an [<code>Aggregator</code>][Aggregator] is instantiated,
 * the [<code>init</code>][init] function of each [<code>AggregatorAggregationHandlers</code>][AggregatorAggregationHandler] is called,
 * which allows the handler to refer to [<code>Aggregator</code>][Aggregator] instance via [<code>aggregator</code>][aggregator].
 */
public interface AggregatorHandler<in Value : Any, out Return : Any?> {

    /**
     * Reference to the aggregator instance.
     *
     * Can only be used once [<code>init</code>][init] has run.
     */
    public var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>?

    public fun init(aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>) {
        this.aggregator = aggregator
    }
}
