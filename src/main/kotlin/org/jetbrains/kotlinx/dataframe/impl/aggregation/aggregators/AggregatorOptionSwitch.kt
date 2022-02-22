package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import kotlin.reflect.KProperty

@PublishedApi
internal class AggregatorOptionSwitch<P, C, R>(val name: String, val getAggregator: (P) -> AggregatorProvider<C, R>) {

    private val cache = mutableMapOf<P, Aggregator<C, R>>()

    operator fun invoke(option: P) = cache.getOrPut(option) { getAggregator(option).create(name) }

    class Factory<P, C, R>(val getAggregator: (P) -> AggregatorProvider<C, R>) {
        operator fun getValue(obj: Any?, property: KProperty<*>) = AggregatorOptionSwitch(property.name, getAggregator)
    }
}

@PublishedApi
internal class AggregatorOptionSwitch2<P1, P2, C, R>(
    val name: String,
    val getAggregator: (P1, P2) -> AggregatorProvider<C, R>
) {

    private val cache = mutableMapOf<Pair<P1, P2>, Aggregator<C, R>>()

    operator fun invoke(option1: P1, option2: P2) = cache.getOrPut(option1 to option2) { getAggregator(option1, option2).create(name) }

    class Factory<P1, P2, C, R>(val getAggregator: (P1, P2) -> AggregatorProvider<C, R>) {
        operator fun getValue(obj: Any?, property: KProperty<*>) = AggregatorOptionSwitch2(property.name, getAggregator)
    }
}
