package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import kotlin.reflect.KType

/**
 * The input handler of the aggregator,
 * which handles type checks, conversions, and preprocessing of a single sequence of input values.
 * It can also calculate a specific [<code>value type</code>][ValueType] from the input values or input types
 * if the (specific) type is not known.
 */
public interface AggregatorInputHandler<in Value : Any, out Return : Any?> : AggregatorHandler<Value, Return> {

    /**
     * If the specific [<code>ValueType</code>][ValueType] of the input is not known, but you still want to call [<code>aggregate</code>][aggregate],
     * this function can be called to calculate it by combining the set of known [<code>valueTypes</code>][valueTypes].
     */
    public fun calculateValueType(valueTypes: Set<KType>): ValueType

    /**
     * WARNING: HEAVY!
     *
     * If the specific [<code>ValueType</code>][ValueType] of the input is not known, but you still want to call [<code>aggregate</code>][aggregate],
     * this function can be called to calculate it by getting the types of [<code>values</code>][values] at runtime.
     * This is heavy because it uses reflection on each value.
     */
    public fun calculateValueType(values: Sequence<Value?>): ValueType

    /**
     * Preprocesses the input values before aggregation.
     * It's expected that this function converts [<code>values</code>][values] to the right [<code>valueType.kType</code>][ValueType.kType]
     * if [<code>valueType.needsFullConversion</code>][ValueType.needsFullConversion].
     *
     * @return A pair of the preprocessed values and the (potentially new) type of the values.
     */
    public fun preprocessAggregation(
        values: Sequence<Value?>,
        valueType: ValueType,
    ): Pair<Sequence<@UnsafeVariance Value?>, KType>
}
