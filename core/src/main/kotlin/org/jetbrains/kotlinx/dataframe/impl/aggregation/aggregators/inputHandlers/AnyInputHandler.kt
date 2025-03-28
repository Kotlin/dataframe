package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregate
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.toValueType
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import kotlin.reflect.KType

/**
 * Input handler for aggregators that can handle any type of input.
 *
 * When calculating the value type, it will try to find the common type in terms of inheritance.
 */
internal class AnyInputHandler<in Value : Any, out Return : Any?> : AggregatorInputHandler<Value, Return> {

    /** No preprocessing is done on the input values. */
    override fun preprocessAggregation(
        values: Sequence<Value?>,
        valueType: ValueType,
    ): Pair<Sequence<@UnsafeVariance Value?>, KType> = Pair(values, valueType.kType)

    /**
     * If the specific [ValueType] of the input is not known, but you still want to call [aggregate],
     * this function can be called to calculate it in terms of inheritance by combining the set of known [valueTypes].
     */
    override fun calculateValueType(valueTypes: Set<KType>): ValueType = valueTypes.commonType(false).toValueType()

    /**
     * WARNING: HEAVY!
     *
     * If the specific [ValueType] of the input is not known, but you still want to call [aggregate],
     * this function can be called to calculate it in terms of inheritance by getting the types of [values] at runtime.
     */
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
