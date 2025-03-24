package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import kotlin.reflect.KType

internal class ByAggregator<Source, in Value, out Return>(
    aggregator: Aggregator<Value, Return>,
    private val aggregatorBy: AggregateBy<Source, Value, Return>,
) : Aggregator<Value, Return>(aggregator) {

    // Set the aggregator reference in all handlers to this instance
    init {
        aggregationHandler.aggregator = this
        inputHandler.aggregator = this
        multipleColumnsHandler.aggregator = this
    }

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = this

    fun <Source> aggregateBy(
        values: Sequence<Source>,
        sourceType: KType,
        valueType: KType,
        selector: (Source) -> Value?,
    ): Return = TODO()
//        aggregatorBy(
//            if (valueType.isMarkedNullable) {
//                values.filterNot { selector(it) == null }
//            } else {
//                values
//            },
//            sourceType,
//            valueType.withNullability(false),
//            selector as (Source) -> Value,
//        )

    @Suppress("FunctionName")
    companion object {
        fun <Source, Value, Return> Factory(
            aggregatorProvider: AggregatorProvider<Aggregator<Value, Return>>,
            aggregatorBy: AggregateBy<Source, Value, Return>,
        ) = Provider { name -> ByAggregator(aggregatorProvider.create(name), aggregatorBy) }
    }
}
