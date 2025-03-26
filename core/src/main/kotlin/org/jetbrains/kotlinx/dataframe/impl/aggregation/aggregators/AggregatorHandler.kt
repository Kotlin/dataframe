package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

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
