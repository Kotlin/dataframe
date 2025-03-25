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
     * @include [TwoStepAggregatorForAny]
     */
    private fun <Type> twoStepPreservingType(
        isBetterThan: IsBetterThanSelector<Type>,
    ): AggregatorProvider<Aggregator<Type, Type?>> =
        Aggregator.Factory(
            aggregationHandler = SelectingAggregationHandler(isBetterThan, preserveReturnTypeNullIfEmpty),
            inputHandler = AnyInputHandler(),
            multipleColumnsHandler = TwoStepMultipleColumnsHandler(),
        )

    /**
     * Factory for a simple aggregator that changes the type of the input values.
     *
     * @include [TwoStepAggregatorForAny]
     */
    private fun <Value, Return> twoStepChangingType(
        getReturnTypeOrNull: CalculateReturnTypeOrNull,
        stepOneReducer: Reducer<Value, Return>,
        stepTwoReducer: Reducer<Return, Return>,
    ) = Aggregator.Factory(
        aggregationHandler = ReducingAggregationHandler(stepOneReducer, getReturnTypeOrNull),
        inputHandler = AnyInputHandler(),
        multipleColumnsHandler = TwoStepMultipleColumnsHandler(
            stepTwoAggregationHandler = ReducingAggregationHandler(stepTwoReducer, getReturnTypeOrNull),
        ),
    )

    /**
     * Factory for a flattening aggregator that preserves the type of the input values.
     *
     * @include [FlatteningAggregatorForAny]
     */
    private fun <Type> flatteningPreservingTypes(reducer: Reducer<Type, Type?>) =
        Aggregator.Factory(
            aggregationHandler = ReducingAggregationHandler(reducer, preserveReturnTypeNullIfEmpty),
            inputHandler = AnyInputHandler(),
            multipleColumnsHandler = FlatteningMultipleColumnsHandler(),
        )

    /**
     * Factory for a flattening aggregator that changes the type of the input values.
     *
     * @include [FlatteningAggregatorForAny]
     */
    private fun <Value, Return> flatteningChangingTypes(
        getReturnTypeOrNull: CalculateReturnTypeOrNull,
        reducer: Reducer<Value, Return>,
    ) = Aggregator.Factory(
        aggregationHandler = ReducingAggregationHandler(reducer, getReturnTypeOrNull),
        inputHandler = AnyInputHandler(),
        multipleColumnsHandler = FlatteningMultipleColumnsHandler(),
    )

    /**
     * Factory for a two-step aggregator that works only with numbers.
     *
     * @include [TwoStepAggregatorForNumbers]
     */
    private fun <Return : Number?> twoStepForNumbers(
        getReturnTypeOrNull: CalculateReturnTypeOrNull,
        reducer: Reducer<Number, Return>,
    ) = Aggregator.Factory(
        aggregationHandler = ReducingAggregationHandler(reducer, getReturnTypeOrNull),
        inputHandler = NumberInputHandler(),
        multipleColumnsHandler = TwoStepMultipleColumnsHandler(),
    )

    /** @include [AggregatorOptionSwitch1] */
    private fun <Param1, AggregatorType : Aggregator<*, *>> withOneOption(
        getAggregator: (Param1) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch1.Factory(getAggregator)

    /** @include [AggregatorOptionSwitch2] */
    private fun <Param1, Param2, AggregatorType : Aggregator<*, *>> withTwoOptions(
        getAggregator: (Param1, Param2) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch2.Factory(getAggregator)

//    private fun <Value, Return> AggregatorProvider<Aggregator<Value, Return>>.asByAggregator(
//        aggregatorBy: AggregateBy<Any?, Value, Return>,
//    ) where Value : Any, Value : Comparable<Value> = ComparingAggregator.Factory(this, aggregatorBy)

    // T: Comparable<T> -> T?
    fun <T : Comparable<T>?> min() = min.cast2<T, T?>()

    private val min by twoStepPreservingType<Comparable<Any?>> { other, type ->
        this < other
    }

    // T: Comparable<T> -> T?
    fun <T : Comparable<T>?> max() = max.cast2<T, T?>()

    private val max by twoStepPreservingType<Comparable<Any?>> { other, type ->
        this > other
    }

    // T: Number? -> Double
    val std by withTwoOptions { skipNA: Boolean, ddof: Int ->
        flatteningChangingTypes<Number, Double>(stdTypeConversion) { type ->
            asIterable().std(type, skipNA, ddof)
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
            asIterable().percentile(percentile, type)
        }
    }

    // T: Comparable<T>? -> T
    val median by flatteningPreservingTypes<Comparable<Any?>> { type ->
        asIterable().median(type)
    }

    // T: Number -> T
    val sum by twoStepForNumbers(sumTypeConversion) { type ->
        sum(type)
    }
}
