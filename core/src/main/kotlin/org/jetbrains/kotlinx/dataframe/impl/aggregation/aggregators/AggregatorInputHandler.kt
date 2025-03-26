package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import kotlin.reflect.KType

internal interface AggregatorInputHandler<in Value : Any, out Return : Any?> : AggregatorHandler<Value, Return> {

    fun calculateValueType(valueTypes: Set<KType>): ValueType

    // heavy!
    fun calculateValueType(values: Sequence<Value?>): ValueType

    fun preprocessAggregation(
        values: Sequence<Value?>,
        valueType: ValueType,
    ): Pair<Sequence<@UnsafeVariance Value?>, KType>
}
