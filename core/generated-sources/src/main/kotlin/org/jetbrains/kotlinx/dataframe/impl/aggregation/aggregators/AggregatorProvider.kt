package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import kotlin.reflect.KProperty

internal interface AggregatorProvider<C, R> {

    operator fun getValue(obj: Any?, property: KProperty<*>): Aggregator<C, R> = create(property.name)

    fun create(name: String): Aggregator<C, R>
}
