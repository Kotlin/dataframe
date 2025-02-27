package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.median
import org.jetbrains.kotlinx.dataframe.math.percentile
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.sum
import kotlin.reflect.KType

@PublishedApi
internal object Aggregators {

    /**
     * Factory for a simple aggregator that preserves the type of the input values.
     *
     * A slightly more advanced [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] implementation.
     *
     * Nulls are filtered from columns.
     *
     * When called on multiple columns, this [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] works in two steps:
     * First, it aggregates within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]/[Iterable] ([stepOneAggregator]) with their (given) type,
     * and then in between different columns ([stepTwoAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.stepTwoAggregator]) using the results of the first and the newly
     * calculated common type of those results.
     *
     * ```
     * Iterable<Column<Value?>>
     *     -> Iterable<Iterable<Value>> // nulls filtered out
     *     -> stepOneAggregator(Iterable<Value>, colType) // called on each iterable
     *     -> Iterable<Return> // nulls filtered out
     *     -> stepTwoAggregator(Iterable<Return>, common valueType)
     *     -> Return?
     * ```
     *
     * It can also be used as a "simple" aggregator by providing the same function for both steps,
     * requires [preservesType][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.preservesType] be set to `true`.
     *
     * See [FlatteningAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator] for different behavior for multiple columns.
     *
     * @param name The name of this aggregator.
     * @param stepOneAggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.aggregate] function, used within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] or [Iterable].
     * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
     *   It is run on the results of [stepOneAggregator].
     * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
     */
    private fun <Type> twoStepPreservingType(aggregator: Iterable<Type>.(type: KType) -> Type?) =
        TwoStepAggregator.Factory(
            stepOneAggregator = aggregator,
            stepTwoAggregator = aggregator,
            preservesType = true,
        )

    /**
     * Factory for a simple aggregator that changes the type of the input values.
     *
     * A slightly more advanced [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] implementation.
     *
     * Nulls are filtered from columns.
     *
     * When called on multiple columns, this [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] works in two steps:
     * First, it aggregates within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]/[Iterable] ([stepOneAggregator]) with their (given) type,
     * and then in between different columns ([stepTwoAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.stepTwoAggregator]) using the results of the first and the newly
     * calculated common type of those results.
     *
     * ```
     * Iterable<Column<Value?>>
     *     -> Iterable<Iterable<Value>> // nulls filtered out
     *     -> stepOneAggregator(Iterable<Value>, colType) // called on each iterable
     *     -> Iterable<Return> // nulls filtered out
     *     -> stepTwoAggregator(Iterable<Return>, common valueType)
     *     -> Return?
     * ```
     *
     * It can also be used as a "simple" aggregator by providing the same function for both steps,
     * requires [preservesType][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.preservesType] be set to `true`.
     *
     * See [FlatteningAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator] for different behavior for multiple columns.
     *
     * @param name The name of this aggregator.
     * @param stepOneAggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.aggregate] function, used within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] or [Iterable].
     * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
     *   It is run on the results of [stepOneAggregator].
     * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
     */
    private fun <Value, Return> twoStepChangingType(
        stepOneAggregator: Iterable<Value>.(type: KType) -> Return,
        stepTwoAggregator: Iterable<Return>.(type: KType) -> Return,
    ) = TwoStepAggregator.Factory(
        stepOneAggregator = stepOneAggregator,
        stepTwoAggregator = stepTwoAggregator,
        preservesType = false,
    )

    /**
     * Factory for a flattening aggregator that preserves the type of the input values.
     *
     * Simple [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] implementation with flattening behavior for multiple columns.
     *
     * Nulls are filtered from columns.
     *
     * When called on multiple columns,
     * the columns are flattened into a single list of values, filtering nulls as usual;
     * then the aggregation function is called with their common type.
     *
     * ```
     * Iterable<Column<Value?>>
     *     -> Iterable<Value> // flattened without nulls
     *     -> aggregator(Iterable<Value>, common colType)
     *     -> Return?
     * ```
     *
     * This is essential for aggregators that depend on the distribution of all values across the dataframe.
     *
     * See [TwoStepAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator] for different behavior for multiple columns.
     *
     * @param name The name of this aggregator.
     * @param aggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.aggregate] function.
     *   Note that it must be able to handle `null` values for the [Iterable] overload of [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.aggregate].
     * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
     */
    private fun <Type> flatteningPreservingTypes(aggregate: Iterable<Type?>.(type: KType) -> Type?) =
        FlatteningAggregator.Factory(
            aggregator = aggregate,
            preservesType = true,
        )

    /**
     * Factory for a flattening aggregator that changes the type of the input values.
     *
     * Simple [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] implementation with flattening behavior for multiple columns.
     *
     * Nulls are filtered from columns.
     *
     * When called on multiple columns,
     * the columns are flattened into a single list of values, filtering nulls as usual;
     * then the aggregation function is called with their common type.
     *
     * ```
     * Iterable<Column<Value?>>
     *     -> Iterable<Value> // flattened without nulls
     *     -> aggregator(Iterable<Value>, common colType)
     *     -> Return?
     * ```
     *
     * This is essential for aggregators that depend on the distribution of all values across the dataframe.
     *
     * See [TwoStepAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator] for different behavior for multiple columns.
     *
     * @param name The name of this aggregator.
     * @param aggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.aggregate] function.
     *   Note that it must be able to handle `null` values for the [Iterable] overload of [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.aggregate].
     * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
     */
    private fun <Value, Return> flatteningChangingTypes(aggregate: Iterable<Value?>.(type: KType) -> Return?) =
        FlatteningAggregator.Factory(
            aggregator = aggregate,
            preservesType = false,
        )

    /**
     * Factory for a two-step aggregator that works only with numbers.
     *
     * [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] made specifically for number calculations.
     *
     * Nulls are filtered from columns.
     *
     * When called on multiple columns (with potentially different [Number] types),
     * this [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] works in two steps:
     *
     * First, it aggregates within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]/[Iterable] with their (given) [Number] type,
     * and then between different columns
     * using the results of the first and the newly calculated [unified number][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers] type of those results.
     *
     * ```
     * Iterable<Column<Number?>>
     *     -> Iterable<Iterable<Number>> // nulls filtered out
     *     -> aggregator(Iterable<Number>, colType) // called on each iterable
     *     -> Iterable<Return> // nulls filtered out
     *     -> aggregator(Iterable<Return>, unified number type of common valueType)
     *     -> Return?
     * ```
     *
     * @param name The name of this aggregator.
     * @param aggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepNumbersAggregator.aggregate] function, used within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] or [Iterable].
     *   While it takes a [Number] argument, you can assume that all values are of the same specific type, however,
     *   this type can be different for different calls to [aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorBase.aggregator].
     */
    private fun <Return : Number> twoStepForNumbers(aggregate: Iterable<Number>.(numberType: KType) -> Return?) =
        TwoStepNumbersAggregator.Factory(aggregate)

    /** Wrapper around an [aggregator factory][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorProvider] for aggregators that require a single parameter.
     *
     * Aggregators are cached by their parameter value.
     * @see AggregatorOptionSwitch2 */
    private fun <Param1, AggregatorType : Aggregator<*, *>> withOneOption(
        getAggregator: (Param1) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch1.Factory(getAggregator)

    /** Wrapper around an [aggregator factory][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorProvider] for aggregators that require two parameters.
     *
     * Aggregators are cached by their parameter values.
     * @see AggregatorOptionSwitch1 */
    private fun <Param1, Param2, AggregatorType : Aggregator<*, *>> withTwoOptions(
        getAggregator: (Param1, Param2) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch2.Factory(getAggregator)

    val min by twoStepPreservingType<Comparable<Any?>> { minOrNull() }

    val max by twoStepPreservingType<Comparable<Any?>> { maxOrNull() }

    val std by withTwoOptions { skipNA: Boolean, ddof: Int ->
        flatteningChangingTypes<Number, Double> { std(it, skipNA, ddof) }
    }

    val mean by withOneOption { skipNA: Boolean ->
        twoStepChangingType({ mean(it, skipNA) }) { mean(skipNA) }
    }

    val percentile by withOneOption { percentile: Double ->
        flatteningChangingTypes<Comparable<Any?>, Comparable<Any?>> { type ->
            percentile(percentile, type)
        }
    }

    val median by flatteningPreservingTypes<Comparable<Any?>> {
        median(it)
    }

    val sum by twoStepForNumbers { sum(it) }
}
