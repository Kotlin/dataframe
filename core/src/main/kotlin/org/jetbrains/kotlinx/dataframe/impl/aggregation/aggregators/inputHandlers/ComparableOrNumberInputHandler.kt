package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

// TODO? for input consisting of V : Comparable<V> or (mixed) Numbers
// TODO test! :)
internal class ComparableOrNumberInputHandler<in Value : Comparable<Value>, out Return : Any?> :
    AggregatorInputHandler<Value, Return> {

    private val numberInputHandler = NumberInputHandler<Return>()
    private val anyInputHandler = AnyInputHandler<Value, Return>()

    @Suppress("UNCHECKED_CAST")
    override fun init(aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>) {
        super.init(aggregator)
        numberInputHandler.init(aggregator as Aggregator<Number, Return>)
        anyInputHandler.init(aggregator)
    }

    override fun calculateValueType(valueTypes: Set<KType>): ValueType =
        if (valueTypes.all { it.isSubtypeOf(typeOf<Number?>()) }) {
            numberInputHandler.calculateValueType(valueTypes)
        } else {
            anyInputHandler.calculateValueType(valueTypes)
        }

    @Suppress("UNCHECKED_CAST")
    override fun calculateValueType(values: Sequence<Value?>): ValueType {
        val anyType = anyInputHandler.calculateValueType(values)
        return if (anyType.kType.isSubtypeOf(typeOf<Number?>())) {
            numberInputHandler.calculateValueType(values as Sequence<Number?>)
        } else {
            anyType
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun preprocessAggregation(
        values: Sequence<Value?>,
        valueType: ValueType,
    ): Pair<Sequence<@UnsafeVariance Value?>, KType> =
        if (valueType.kType.isSubtypeOf(typeOf<Number?>())) {
            numberInputHandler.preprocessAggregation(values as Sequence<Number?>, valueType)
        } else {
            anyInputHandler.preprocessAggregation(values, valueType)
        } as Pair<Sequence<Value?>, KType>

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
