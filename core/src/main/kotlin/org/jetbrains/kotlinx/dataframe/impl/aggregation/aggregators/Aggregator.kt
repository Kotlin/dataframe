package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.ReducingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.SelectingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.AnyInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.NumberInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.FlatteningMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.NoMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.TwoStepMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.columns.ParameterValue
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * This class is the main entry-point for creating an aggregator.
 *
 * Aggregators are used to compute a single value from a [Sequence] of values,
 * a single [DataColumn], or multiple [DataColumns][DataColumn].
 *
 * [Aggregator] follows a dependency injection pattern:
 *
 * Using the constructor or [Aggregator.invoke] function, you can create an [Aggregator] instance with a choice of:
 * - [AggregatorInputHandler] - {@include [AggregatorInputHandler]}
 *
 *   Options: [NumberInputHandler], [AnyInputHandler]
 *
 * - [AggregatorAggregationHandler] - {@include [AggregatorAggregationHandler]}
 *
 *   Options: [ReducingAggregationHandler], [SelectingAggregationHandler]
 *
 * - [AggregatorMultipleColumnsHandler] - {@include [AggregatorMultipleColumnsHandler]}
 *
 *   Options: [FlatteningMultipleColumnsHandler], [TwoStepMultipleColumnsHandler], [NoMultipleColumnsHandler]
 *
 * @param Value The non-null type of the values to be aggregated.
 *   The input can always have nulls, they are filtered out.
 * @param Return The type of the resulting value. Can optionally be nullable.
 * @see [invoke]
 */
public class Aggregator<in Value : Any, out Return : Any?>(
    public val aggregationHandler: AggregatorAggregationHandler<Value, Return>,
    public val inputHandler: AggregatorInputHandler<Value, Return>,
    public val multipleColumnsHandler: AggregatorMultipleColumnsHandler<Value, Return>,
    public val name: String,
    public val statisticsParameters: Map<String, ParameterValue?>,
) : AggregatorInputHandler<Value, Return> by inputHandler,
    AggregatorMultipleColumnsHandler<Value, Return> by multipleColumnsHandler,
    AggregatorAggregationHandler<Value, Return> by aggregationHandler {

    // Set the aggregator reference in all handlers to this instance
    init {
        aggregationHandler.init(this)
        inputHandler.init(this)
        multipleColumnsHandler.init(this)
        init(this)
    }

    override fun init(aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>) {
        this.aggregator = aggregator
    }

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null

    override fun toString(): String =
        "Aggregator(name='$name', aggregationHandler=$aggregationHandler, inputHandler=$inputHandler, multipleColumnsHandler=$multipleColumnsHandler)"

    internal companion object {

        /**
         * Factory function for creating an [Aggregator] instance given a name.
         *
         * @see AggregatorProvider
         * @see Aggregator
         */
        internal operator fun <Value : Any, Return : Any?> invoke(
            aggregationHandler: AggregatorAggregationHandler<Value, Return>,
            inputHandler: AggregatorInputHandler<Value, Return>,
            multipleColumnsHandler: AggregatorMultipleColumnsHandler<Value, Return>,
            statisticsParameters: Map<String, ParameterValue?>,
        ): AggregatorProvider<Value, Return> =
            AggregatorProvider { name ->
                Aggregator(
                    aggregationHandler = aggregationHandler,
                    inputHandler = inputHandler,
                    multipleColumnsHandler = multipleColumnsHandler,
                    name = name,
                    statisticsParameters = statisticsParameters,
                )
            }

        // fictitious, I want the program to compile
        internal operator fun <Value : Any, Return : Any?> invoke(
            aggregationHandler: AggregatorAggregationHandler<Value, Return>,
            inputHandler: AggregatorInputHandler<Value, Return>,
            multipleColumnsHandler: AggregatorMultipleColumnsHandler<Value, Return>,
        ): AggregatorProvider<Value, Return> =
            AggregatorProvider { name ->
                Aggregator(
                    aggregationHandler = aggregationHandler,
                    inputHandler = inputHandler,
                    multipleColumnsHandler = multipleColumnsHandler,
                    name = name,
                    emptyMap(),
                )
            }
    }
}

/**
 * Performs aggregation on the given [values], taking [valueType] into account.
 * If [valueType] is unknown, see [calculateValueType] or [aggregateCalculatingValueType].
 */
@PublishedApi
internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregate(
    values: Sequence<Value?>,
    valueType: ValueType,
): Return = aggregateSequence(values, valueType)

/**
 * Performs aggregation on the given [values], taking [valueType] into account.
 * If [valueType] is unknown, see [calculateValueType] or [aggregateCalculatingValueType].
 */
@PublishedApi
internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregate(
    values: Sequence<Value?>,
    valueType: KType,
): Return = aggregate(values, valueType.toValueType(needsFullConversion = false))

/**
 * If the specific [ValueType] of the input is not known, but you still want to call [aggregate],
 * this function can be called to calculate it by combining the set of known [valueTypes] or
 * by gathering the types from [values].
 *
 * This is a helper function that calls the correct
 * [AggregatorInputHandler.calculateValueType] based on the given input.
 *
 * Giving [valueTypes] is preferred because of efficiency, as it allows for avoiding runtime type checks.
 */
internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.calculateValueType(
    values: Sequence<Value?>,
    valueTypes: Set<KType>? = null,
) = if (valueTypes != null && valueTypes.isNotEmpty()) {
    calculateValueType(valueTypes)
} else {
    calculateValueType(values)
}

/**
 * If the specific [ValueType] of the input is not known, but you still want to call [aggregate],
 * this function can be called to calculate it by combining the set of known [valueTypes] or
 * by gathering the types from [values] and then aggregating them.
 *
 * Giving [valueTypes] is preferred because of efficiency, as it allows for avoiding runtime type checks.
 */
internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregateCalculatingValueType(
    values: Sequence<Value?>,
    valueTypes: Set<KType>? = null,
) = aggregateSequence(
    values = values,
    valueType = calculateValueType(values, valueTypes),
)

/**
 * Aggregates the data in the given column and computes a single resulting value.
 */
internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregate(column: DataColumn<Value?>) =
    aggregateSingleColumn(column)

/**
 * Aggregates the data in the given columns and computes a single resulting value.
 */
internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregate(columns: Sequence<DataColumn<Value?>>) =
    aggregateMultipleColumns(columns)

/**
 * Gives the index of the aggregation result in the input [values], if it applies.
 * This is used for aggregators with an [AggregatorAggregationHandler] where
 * [Value][Value]`  ==  `[Return][Return], and where the result exists in the input.
 *
 * Like for [SelectingAggregationHandler].
 *
 * Defaults to `-1`.
 *
 * If [valueType] is unknown, see [calculateValueType]
 */
@PublishedApi
internal fun <Value : Return & Any, Return : Any?> Aggregator<Value, Return>.indexOfAggregationResult(
    values: Sequence<Value?>,
    valueType: ValueType,
): Int = indexOfAggregationResultSingleSequence(values, valueType)

/**
 * Gives the index of the aggregation result in the input [values], if it applies.
 * This is used for aggregators with an [AggregatorAggregationHandler] where
 * [Value][Value]`  ==  `[Return][Return], and where the result exists in the input.
 *
 * Like for [SelectingAggregationHandler].
 *
 * Defaults to `-1`.
 *
 * If [valueType] is unknown, see [calculateValueType]
 */
@PublishedApi
internal fun <Value : Return & Any, Return : Any?> Aggregator<Value, Return>.indexOfAggregationResult(
    values: Sequence<Value?>,
    valueType: KType,
): Int = indexOfAggregationResultSingleSequence(values, valueType.toValueType(needsFullConversion = false))

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <Type : Any?> Aggregator<*, *>.cast(): Aggregator<Type & Any, Type> = this as Aggregator<Type & Any, Type>

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <Value : Any, Return : Any?> Aggregator<*, *>.cast2(): Aggregator<Value, Return> =
    this as Aggregator<Value, Return>

/**
 * Type alias for a function that gives the return type of a [Reducer] or [Selector]
 * given some input type and whether the input is empty.
 */
internal typealias CalculateReturnType = (type: KType, emptyInput: Boolean) -> KType

/**
 * Type alias for a reducer function where the type of the values is provided as [KType].
 * Nulls have already been filtered out when this function is called.
 */
internal typealias Reducer<Value, Return> = Sequence<Value & Any>.(valueType: KType) -> Return

/**
 * Type alias for a selector function where the type of the values is provided as [KType].
 *
 * It is expected that [Value][Value]`  :  `[Return][Return]`  &  `[Any][Any], and [Return][Return]`  :  `[Any?][Any].
 *
 * Nulls have already been filtered out when this function is called.
 */
internal typealias Selector<Value, Return> = Sequence<Value & Any>.(type: KType) -> Return

/**
 * Type alias for a function that returns the index of the result of [Selector] in this sequence.
 * If the result is not in the sequence, it returns -1.
 * The type of the values is provided as [KType] and the sequence can contain nulls.
 */
internal typealias IndexOfResult<Value> = Sequence<Value?>.(type: KType) -> Int

/** Common case for [CalculateReturnType], preserves return type, but makes it nullable for empty inputs. */
internal val preserveReturnTypeNullIfEmpty: CalculateReturnType = { type, emptyInput ->
    type.withNullability(emptyInput)
}
