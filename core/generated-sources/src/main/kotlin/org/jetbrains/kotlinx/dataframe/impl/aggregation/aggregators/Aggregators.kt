package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.meanTypeConversion
import org.jetbrains.kotlinx.dataframe.math.median
import org.jetbrains.kotlinx.dataframe.math.percentile
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.stdTypeConversion
import org.jetbrains.kotlinx.dataframe.math.sum
import org.jetbrains.kotlinx.dataframe.math.sumTypeConversion

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
     * It can also be used as a "simple" aggregator by providing the same function for both steps.
     *
     * See [FlatteningAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator] for different behavior for multiple columns.
     *
     * @param name The name of this aggregator.
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.calculateReturnTypeOrNull] function.
     * @param stepOneAggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.aggregate] function, used within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] or [Iterable].
     * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
     *   It is run on the results of [stepOneAggregator].
     */
    private fun <Type> twoStepPreservingType(aggregator: Aggregate<Type, Type>) =
        TwoStepAggregator.Factory(
            getReturnTypeOrNull = preserveReturnTypeNullIfEmpty,
            stepOneAggregator = aggregator,
            stepTwoAggregator = aggregator,
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
     * It can also be used as a "simple" aggregator by providing the same function for both steps.
     *
     * See [FlatteningAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator] for different behavior for multiple columns.
     *
     * @param name The name of this aggregator.
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.calculateReturnTypeOrNull] function.
     * @param stepOneAggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator.aggregate] function, used within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] or [Iterable].
     * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
     *   It is run on the results of [stepOneAggregator].
     */
    private fun <Value, Return> twoStepChangingType(
        getReturnTypeOrNull: CalculateReturnTypeOrNull,
        stepOneAggregator: Aggregate<Value, Return>,
        stepTwoAggregator: Aggregate<Return, Return>,
    ) = TwoStepAggregator.Factory(
        getReturnTypeOrNull = getReturnTypeOrNull,
        stepOneAggregator = stepOneAggregator,
        stepTwoAggregator = stepTwoAggregator,
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
     * This is essential for aggregators that depend on the distribution of all values across the dataframe, like
     * the median, percentile, and standard deviation.
     *
     * See [TwoStepAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator] for different behavior for multiple columns.
     *
     * @param name The name of this aggregator.
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.calculateReturnTypeOrNull] function.
     * @param aggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.aggregate] function.
     *   Note that it must be able to handle `null` values for the [Iterable] overload of [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.aggregate].
     */
    private fun <Type> flatteningPreservingTypes(aggregate: Aggregate<Type, Type>) =
        FlatteningAggregator.Factory(
            getReturnTypeOrNull = preserveReturnTypeNullIfEmpty,
            aggregator = aggregate,
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
     * This is essential for aggregators that depend on the distribution of all values across the dataframe, like
     * the median, percentile, and standard deviation.
     *
     * See [TwoStepAggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepAggregator] for different behavior for multiple columns.
     *
     * @param name The name of this aggregator.
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.calculateReturnTypeOrNull] function.
     * @param aggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.aggregate] function.
     *   Note that it must be able to handle `null` values for the [Iterable] overload of [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.FlatteningAggregator.aggregate].
     */
    private fun <Value, Return> flatteningChangingTypes(
        getReturnTypeOrNull: CalculateReturnTypeOrNull,
        aggregate: Aggregate<Value, Return>,
    ) = FlatteningAggregator.Factory(
        getReturnTypeOrNull = getReturnTypeOrNull,
        aggregator = aggregate,
    )

    /**
     * Factory for a two-step aggregator that works only with numbers.
     *
     * [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] made specifically for number calculations.
     * Mixed number types are [unified][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers] to [primitives][org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.Companion.PRIMITIVES_ONLY].
     *
     * Nulls are filtered from columns.
     *
     * When called on multiple columns (with potentially mixed [Number] types),
     * this [Aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator] works in two steps:
     *
     * First, it aggregates within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]/[Iterable] with their (given) [Number] type
     * (potentially unifying the types), and then between different columns
     * using the results of the first and the newly calculated [unified number][org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers] type of those results.
     *
     * ```
     * Iterable<Column<Number?>>
     *     -> Iterable<Iterable<Number>> // nulls filtered out
     *     -> aggregator(Iterable<specific Number>, unified number type of common colType) // called on each iterable
     *     -> Iterable<Return> // nulls filtered out
     *     -> aggregator(Iterable<specific Return>, unified number type of common valueType)
     *     -> Return?
     * ```
     *
     * @param name The name of this aggregator.
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepNumbersAggregator.calculateReturnTypeOrNull] function.
     * @param aggregator Functional argument for the [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.TwoStepNumbersAggregator.aggregate] function, used within a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] or [Iterable].
     *   While it takes a [Number] argument, you can assume that all values are of the same specific type, however,
     *   this type can be different for different calls to [aggregator][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorBase.aggregator].
     */
    private fun <Return : Number> twoStepForNumbers(
        getReturnTypeOrNull: CalculateReturnTypeOrNull,
        aggregate: Aggregate<Number, Return>,
    ) = TwoStepNumbersAggregator.Factory(
        getReturnTypeOrNull = getReturnTypeOrNull,
        aggregate = aggregate,
    )

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

    // T: Comparable<T> -> T?
    val min by twoStepPreservingType<Comparable<Any?>> {
        minOrNull()
    }

    // T: Comparable<T> -> T?
    val max by twoStepPreservingType<Comparable<Any?>> {
        maxOrNull()
    }

    // T: Number? -> Double
    val std by withTwoOptions { skipNA: Boolean, ddof: Int ->
        flatteningChangingTypes<Number, Double>(stdTypeConversion) { type ->
            std(type, skipNA, ddof)
        }
    }

    // step one: T: Number? -> Double
    // step two: Double -> Double
    val mean by withOneOption { skipNA: Boolean ->
        twoStepForNumbers(meanTypeConversion) { type ->
            mean(type, skipNA)
        }
    }

    // T: Comparable<T>? -> T
    val percentile by withOneOption { percentile: Double ->
        flatteningPreservingTypes<Comparable<Any?>> { type ->
            percentile(percentile, type)
        }
    }

    // T: Comparable<T>? -> T
    val median by flatteningPreservingTypes<Comparable<Any?>> { type ->
        median(type)
    }

    // T: Number -> T
    val sum by twoStepForNumbers(sumTypeConversion) { type ->
        sum(type)
    }
}
