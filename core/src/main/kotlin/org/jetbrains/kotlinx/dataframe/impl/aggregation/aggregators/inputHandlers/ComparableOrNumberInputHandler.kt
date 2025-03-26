package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import kotlin.reflect.KType

// TODO? for input consisting of V : Comparable<V> or (mixed) Numbers
internal class ComparableOrNumberInputHandler<in Value : Comparable<Value>, out Return : Any?> :
    AggregatorInputHandler<Value, Return> {
    override fun calculateValueType(valueTypes: Set<KType>): ValueType {
        TODO("Not yet implemented")
    }

    override fun calculateValueType(values: Sequence<Value?>): ValueType {
        TODO("Not yet implemented")
    }

    override fun preprocessAggregation(
        values: Sequence<Value?>,
        valueType: ValueType,
    ): Pair<Sequence<@UnsafeVariance Value?>, KType> {
        TODO("Not yet implemented")
    }

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
