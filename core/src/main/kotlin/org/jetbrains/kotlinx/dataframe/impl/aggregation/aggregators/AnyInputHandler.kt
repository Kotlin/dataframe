package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import kotlin.reflect.KType

internal class AnyInputHandler<in Value, out Return> : AggregatorInputHandler<Value, Return> {

    override fun preprocessAggregation(
        values: Sequence<Value?>,
        valueType: ValueType,
    ): Pair<Sequence<@UnsafeVariance Value?>, KType> = Pair(values, valueType.kType)

    override fun calculateValueType(valueTypes: Set<KType>): ValueType = valueTypes.commonType(false).toValueType()

    // heavy
    override fun calculateValueType(values: Sequence<Value?>): ValueType {
        var hasNulls = false
        val classes = values.mapNotNull {
            if (it == null) {
                hasNulls = true
                null
            } else {
                it.javaClass.kotlin
            }
        }.toSet()
        return if (classes.isEmpty()) {
            nothingType(hasNulls)
        } else {
            classes.commonType(hasNulls)
        }.toValueType()
    }

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
