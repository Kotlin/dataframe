package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal class ByAggregator<Source, in Value, out Return>(
    aggregator: Aggregator<Value, Return>,
    val aggregatorBy: AggregateBy<Source, Value, Return>,
) : Aggregator<Value, Return> by aggregator {

    fun <Source> aggregateBy(
        values: Iterable<Source>,
        sourceType: KType,
        valueType: KType,
        selector: (Source) -> Value?,
    ): Return = TODO()
//        aggregatorBy(
//            if (valueType.isMarkedNullable) {
//                values.asSequence().filterNot { selector(it) == null }.asIterable()
//            } else {
//                values
//            },
//            sourceType,
//            valueType.withNullability(false),
//            selector as (Source) -> Value,
//        )

    class Factory<Source, in Value, out Return>(
        private val aggregatorProvider: AggregatorProvider<Aggregator<Value, Return>>,
        private val aggregatorBy: AggregateBy<Source, Value, Return>,
    ) : Provider<ByAggregator<Source, Value, Return>> by
        Provider({ name -> ByAggregator(aggregatorProvider.create(name), aggregatorBy) })
}
