package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.Companion.PRIMITIVES_ONLY
import kotlin.reflect.KType

private val logger = KotlinLogging.logger { }

/**
 * [Aggregator] made specifically for number calculations.
 * Mixed number types are [unified][UnifyingNumbers] to [primitives][PRIMITIVES_ONLY].
 *
 * Nulls are filtered out.
 *
 * When called on multiple columns (with potentially mixed [Number] types),
 * this [Aggregator] works in two steps:
 *
 * First, it aggregates within a [DataColumn]/[Iterable] with their (given) [Number] type
 * (potentially unifying the types), and then between different columns
 * using the results of the first and the newly calculated [unified number][UnifyingNumbers] type of those results.
 *
 * ```
 * Iterable<Column<Number?>>
 *     -> Iterable<Iterable<Number>> // nulls filtered out
 *     -> aggregator(Iterable<specific Number>, unified number type of common colType) // called on each iterable
 *     -> Iterable<Return> // nulls filtered out
 *     -> aggregator(Iterable<specific Return>, unified number type of common valueType)
 *     -> Return
 * ```
 *
 * @param name The name of this aggregator.
 * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeMultipleColumnsOrNull] function.
 * @param aggregator Functional argument for the [aggregateSingleIterable] function, used within a [DataColumn] or [Iterable].
 *   While it takes a [Number] argument, you can assume that all values are of the same specific type, however,
 *   this type can be different for different calls to [aggregator].
 */
internal class TwoStepNumbersAggregator<out Return : Number?>(
    name: String,
    val getReturnTypeOrNull: CalculateReturnTypeOrNull,
    val aggregator: Aggregate<Number, Return>,
) : AggregatorBase<Number, Return>(name, getReturnTypeOrNull, aggregator),
    TwoStepAggregator<Number, Return>,
    NumbersAggregator<Return> {

    override fun aggregateSingleSequence(values: Sequence<Number?>, valueType: KType): Return =
        aggregateSingleIterableOfNumbers(values, valueType) { values, valueType ->
            super.aggregateSingleSequence(values, valueType)
        }

    override val stepTwo: AggregatorBase<Return & Any, Return> = StepTwo()

    inner class StepTwo :
        AggregatorBase<Number, Return>(name, getReturnTypeOrNull, aggregator),
        FlatteningAggregator<Number, Return>,
        NumbersAggregator<Return> {
        override fun aggregateSingleSequence(values: Sequence<Number?>, valueType: KType): Return =
            aggregateSingleIterableOfNumbers(values, valueType) { values, valueType ->
                super.aggregateSingleSequence(values, valueType)
            }
    }

    /**
     * Creates [TwoStepNumbersAggregator].
     *
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeMultipleColumnsOrNull] function.
     * @param aggregator Functional argument for the [aggregate] function, used within a [DataColumn] or [Iterable].
     */
    class Factory<out Return : Number?>(
        private val getReturnTypeOrNull: CalculateReturnTypeOrNull,
        private val aggregate: Aggregate<Number, Return>,
    ) : AggregatorProvider<TwoStepNumbersAggregator<Return>> by AggregatorProvider({ name ->
            TwoStepNumbersAggregator(
                name = name,
                getReturnTypeOrNull = getReturnTypeOrNull,
                aggregator = aggregate,
            )
        })
}
