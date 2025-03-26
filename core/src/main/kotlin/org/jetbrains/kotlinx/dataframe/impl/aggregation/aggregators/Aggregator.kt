package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.ReducingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.SelectingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.AnyInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.NumberInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.FlatteningMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.NoMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.TwoStepMultipleColumnsHandler
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Base interface for all aggregators.
 *
 * Aggregators are used to compute a single value from an [Iterable] of values, a single [DataColumn],
 * or multiple [DataColumns][DataColumn].
 *
 * [Aggregator] follows a dependency injection pattern:
 *
 * Using the constructor or [Aggregator.invoke] function, you can create an [Aggregator] instance with a choice of:
 * - [AggregatorAggregationHandler] - the base functionality of the aggregator,
 *   which computes the result from the input values.
 *
 *   Options: [ReducingAggregationHandler], [SelectingAggregationHandler]
 *
 * - [AggregatorInputHandler] - the input handler,
 *   which handles specific type checks, conversion, and preprocessing of the input values.
 *
 *   Options: [NumberInputHandler], [AnyInputHandler]
 *
 * - [AggregatorMultipleColumnsHandler] - the multiple columns handler, which specifies how to aggregate multiple columns.
 *
 *   Options: [FlatteningMultipleColumnsHandler], [TwoStepMultipleColumnsHandler], [NoMultipleColumnsHandler]
 *
 *
 * @param Value The type of the values to be aggregated.
 *   The input can always have nulls, they are filtered out.
 * @param Return The type of the resulting value. Can optionally be nullable.
 */
@PublishedApi
internal class Aggregator<in Value : Any, out Return : Any?>(
    val aggregationHandler: AggregatorAggregationHandler<Value, Return>,
    val inputHandler: AggregatorInputHandler<Value, Return>,
    val multipleColumnsHandler: AggregatorMultipleColumnsHandler<Value, Return>,
    val name: String,
) : AggregatorInputHandler<Value, Return> by inputHandler,
    AggregatorMultipleColumnsHandler<Value, Return> by multipleColumnsHandler,
    AggregatorAggregationHandler<Value, Return> by aggregationHandler {

    constructor(other: Aggregator<Value, Return>) : this(
        name = other.name,
        aggregationHandler = other,
        inputHandler = other,
        multipleColumnsHandler = other,
    )

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

    companion object {
        operator fun <Value : Any, Return : Any?> invoke(
            aggregationHandler: AggregatorAggregationHandler<Value, Return>,
            inputHandler: AggregatorInputHandler<Value, Return>,
            multipleColumnsHandler: AggregatorMultipleColumnsHandler<Value, Return>,
        ): AggregatorProvider<Aggregator<Value, Return>> =
            AggregatorProvider { name ->
                Aggregator(
                    aggregationHandler = aggregationHandler,
                    inputHandler = inputHandler,
                    multipleColumnsHandler = multipleColumnsHandler,
                    name = name,
                )
            }
    }
}

@PublishedApi
internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregate(
    values: Sequence<Value?>,
    valueType: ValueType,
) = aggregateSingleSequence(values, valueType)

@PublishedApi
internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregate(
    values: Sequence<Value?>,
    valueType: KType,
) = aggregate(values, valueType.toValueType())

internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.calculateValueType(
    values: Sequence<Value?>,
    valueTypes: Set<KType>? = null,
) = if (valueTypes != null && valueTypes.isNotEmpty()) {
    calculateValueType(valueTypes)
} else {
    calculateValueType(values)
}

internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregateCalculatingValueType(
    values: Sequence<Value?>,
    valueTypes: Set<KType>? = null,
) = aggregateSingleSequence(
    values = values,
    valueType = calculateValueType(values, valueTypes),
)

internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregate(column: DataColumn<Value?>) =
    aggregateSingleColumn(column)

internal fun <Value : Any, Return : Any?> Aggregator<Value, Return>.aggregate(columns: Sequence<DataColumn<Value?>>) =
    aggregateMultipleColumns(columns)

@PublishedApi
internal fun <Value : Return & Any, Return : Any?> Aggregator<Value, Return>.indexOfAggregationResult(
    values: Sequence<Value?>,
    valueType: ValueType,
): Int = indexOfAggregationResultSingleSequence(values, valueType)

@PublishedApi
internal fun <Value : Return & Any, Return : Any?> Aggregator<Value, Return>.indexOfAggregationResult(
    values: Sequence<Value?>,
    valueType: KType,
): Int = indexOfAggregationResultSingleSequence(values, valueType.toValueType())

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <Type : Any?> Aggregator<*, *>.cast(): Aggregator<Type & Any, Type> = this as Aggregator<Type & Any, Type>

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <Value : Any, Return : Any?> Aggregator<*, *>.cast2(): Aggregator<Value, Return> =
    this as Aggregator<Value, Return>

/** Type alias for [Aggregator.calculateReturnTypeMultipleColumnsOrNull] */
internal typealias CalculateReturnTypeOrNull = (type: KType, emptyInput: Boolean) -> KType?

/**
 * Type alias for the argument for [Aggregator.aggregateSingleSequence].
 * Nulls have already been filtered out when this argument is called.
 */
internal typealias Reducer<Value, Return> = Sequence<Value & Any>.(type: KType) -> Return

internal typealias IndexOfResult<Value> = Sequence<Value?>.(type: KType) -> Int

internal typealias IsBetterThanSelector<Value> = (Value & Any).(other: Value & Any, valueType: KType) -> Boolean

/** Common case for [CalculateReturnTypeOrNull], preserves return type, but makes it nullable for empty inputs. */
internal val preserveReturnTypeNullIfEmpty: CalculateReturnTypeOrNull = { type, emptyInput ->
    type.withNullability(emptyInput)
}
