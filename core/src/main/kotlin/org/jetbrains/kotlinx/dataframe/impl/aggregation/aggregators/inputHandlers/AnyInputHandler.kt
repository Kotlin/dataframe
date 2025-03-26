package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.toValueType
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import kotlin.reflect.KType

internal class AnyInputHandler<in Value : Any, out Return : Any?> : AggregatorInputHandler<Value, Return> {

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
