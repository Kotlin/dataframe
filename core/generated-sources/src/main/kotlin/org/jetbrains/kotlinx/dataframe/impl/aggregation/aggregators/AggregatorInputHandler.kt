package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import kotlin.reflect.KType

/**
 * The input handler of the aggregator,
 * which handles type checks, conversions, and preprocessing of a single sequence of input values.
 * It can also calculate a specific [value type][ValueType] from the input values or input types
 * if the (specific) type is not known.
 */
internal interface AggregatorInputHandler<in Value : Any, out Return : Any?> : AggregatorHandler<Value, Return> {

    /**
     * If the specific [ValueType] of the input is not known, but you still want to call [aggregate],
     * this function can be called to calculate it by combining the set of known [valueTypes].
     */
    fun calculateValueType(valueTypes: Set<KType>): ValueType

    /**
     * WARNING: HEAVY!
     *
     * If the specific [ValueType] of the input is not known, but you still want to call [aggregate],
     * this function can be called to calculate it by getting the types of [values] at runtime.
     */
    fun calculateValueType(values: Sequence<Value?>): ValueType

    /**
     * Preprocesses the input values before aggregation.
     * It's expected that this function converts [values] to the right [valueType.kType][ValueType.kType]
     * if [valueType.needsFullConversion][ValueType.needsFullConversion].
     *
     * @return A pair of the preprocessed values and the (potentially new) type of the values.
     */
    fun preprocessAggregation(
        values: Sequence<Value?>,
        valueType: ValueType,
    ): Pair<Sequence<@UnsafeVariance Value?>, KType>
}
