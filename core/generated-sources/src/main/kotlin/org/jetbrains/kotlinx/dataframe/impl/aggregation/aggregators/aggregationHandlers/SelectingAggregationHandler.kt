package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.IndexOfResult
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Selector
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregateCalculatingValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.calculateValueType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Implementation of [<code>AggregatorAggregationHandler</code>][AggregatorAggregationHandler] which functions like a selector:
 * it takes a sequence of values and returns a single value, which must exist in the input.
 *
 * This is reflected in the type constraints.
 *
 * @param selector This function actually does the selection.
 *   Before it is called, nulls are filtered out. The type of the values is passed as [<code>KType</code>][KType] to the selector.
 * @param indexOfResult This function must be supplied to give the index of the result in the input values.
 * @param getReturnType This function must be supplied to give the return type of [<code>selector</code>][selector] given some input type and
 *   whether the input is empty.
 *   In practice the return type is always either `typeOf<Value>()` or `typeOf<Value?>()`.
 * @see [ReducingAggregationHandler]
 */
internal class SelectingAggregationHandler<in Value : Return & Any, out Return : Any?>(
    val selector: Selector<Value, Return>,
    val indexOfResult: IndexOfResult<Value>,
    val getReturnType: CalculateReturnType,
) : AggregatorAggregationHandler<Value, Return> {

    /**
     * Function that can give the index of the aggregation result in the input [<code>values</code>][values].
     * Calls the supplied [<code>indexOfResult</code>][indexOfResult] after preprocessing the input.
     */
    @Suppress("UNCHECKED_CAST")
    override fun indexOfAggregationResultSingleSequence(values: Sequence<Value?>, valueType: ValueType): Int {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return indexOfResult(values, valueType)
    }

    /**
     * Base function of [<code>Aggregator</code>][Aggregator].
     *
     * Aggregates the given values, taking [<code>valueType</code>][valueType] into account,
     * filtering nulls (only if [<code>valueType.type.isMarkedNullable</code>][KType.isMarkedNullable]),
     * and computes a single resulting value.
     *
     * When the exact [<code>valueType</code>][valueType] is unknown, use [<code>calculateValueType</code>][calculateValueType] or [<code>aggregateCalculatingValueType</code>][aggregateCalculatingValueType].
     *
     * Calls the supplied [<code>selector</code>][selector].
     */
    @Suppress("UNCHECKED_CAST")
    override fun aggregateSequence(values: Sequence<Value?>, valueType: ValueType): Return {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return selector(
            // values =
            if (valueType.isMarkedNullable) {
                values.filterNotNull()
            } else {
                values as Sequence<Value>
            },
            // type =
            valueType.withNullability(false),
        )
    }

    /**
     * Give the return type of [<code>selector</code>][selector] given some input type and whether the input is empty.
     * Calls the supplied [<code>getReturnType</code>][getReturnType].
     *
     * In practice the return type is always either `typeOf<Value & Any>()` or `typeOf<Value?>()`.
     */
    override fun calculateReturnType(valueType: KType, emptyInput: Boolean): KType =
        getReturnType(valueType.withNullability(false), emptyInput).also {
            require(it == valueType.withNullability(false) || it == valueType.withNullability(true)) {
                "The return type of the selector must be either ${valueType.withNullability(false)} or ${
                    valueType.withNullability(true)
                } but was $it."
            }
        }

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
