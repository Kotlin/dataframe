package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.ReducingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.SelectingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.AnyInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.NumberInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.FlatteningMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.TwoStepMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.math.indexOfMax
import org.jetbrains.kotlinx.dataframe.math.indexOfMin
import org.jetbrains.kotlinx.dataframe.math.maxOrNull
import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.meanTypeConversion
import org.jetbrains.kotlinx.dataframe.math.median
import org.jetbrains.kotlinx.dataframe.math.minOrNull
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
    private fun <Value : Return & Any, Return : Any?> twoStepSelecting(
        reducer: Reducer<Value, Return>,
        indexOfResult: IndexOfResult<Value>,
    ) = Aggregator(
        aggregationHandler = SelectingAggregationHandler(reducer, indexOfResult, preserveReturnTypeNullIfEmpty),
        inputHandler = AnyInputHandler(),
        multipleColumnsHandler = TwoStepMultipleColumnsHandler(),
    )

    /**
     * Factory for a simple aggregator that changes the type of the input values.
     *
     * @include [TwoStepAggregatorForAny]
     */
    private fun <Value : Any, Return : Any?> twoStepReducing(
        getReturnTypeOrNull: CalculateReturnTypeOrNull,
        stepOneReducer: Reducer<Value, Return>,
        stepTwoReducer: Reducer<Return, Return>,
    ) = Aggregator(
        aggregationHandler = ReducingAggregationHandler(stepOneReducer, getReturnTypeOrNull),
        inputHandler = AnyInputHandler(),
        multipleColumnsHandler = TwoStepMultipleColumnsHandler(
            ReducingAggregationHandler<Return & Any, Return>(stepTwoReducer, getReturnTypeOrNull),
        ),
    )

    /**
     * Factory for a flattening aggregator that preserves the type of the input values.
     *
     * @include [FlatteningAggregatorForAny]
     */
    private fun <Type : Any> flatteningPreservingTypes(reducer: Reducer<Type, Type?>) =
        Aggregator(
            aggregationHandler = ReducingAggregationHandler(reducer, preserveReturnTypeNullIfEmpty),
            inputHandler = AnyInputHandler(),
            multipleColumnsHandler = FlatteningMultipleColumnsHandler(),
        )

    /**
     * Factory for a flattening aggregator that changes the type of the input values.
     *
     * @include [FlatteningAggregatorForAny]
     */
    private fun <Value : Any, Return : Any?> flatteningChangingTypes(
        getReturnTypeOrNull: CalculateReturnTypeOrNull,
        reducer: Reducer<Value, Return>,
    ) = Aggregator(
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
    ) = Aggregator(
        aggregationHandler = ReducingAggregationHandler(reducer, getReturnTypeOrNull),
        inputHandler = NumberInputHandler(),
        multipleColumnsHandler = TwoStepMultipleColumnsHandler(),
    )

    /** @include [AggregatorOptionSwitch1] */
    private fun <Param1, Value : Any, Return : Any?> withOneOption(
        getAggregator: (Param1) -> AggregatorProvider<Value, Return>,
    ) = AggregatorOptionSwitch1.Factory(getAggregator)

    /** @include [AggregatorOptionSwitch2] */
    private fun <Param1, Param2, Value : Any, Return : Any?> withTwoOptions(
        getAggregator: (Param1, Param2) -> AggregatorProvider<Value, Return>,
    ) = AggregatorOptionSwitch2.Factory(getAggregator)

    // T: Comparable<T> -> T?
    // T : Comparable<T & Any>? -> T?
    fun <T : Comparable<T & Any>?> min(skipNaN: Boolean): Aggregator<T & Any, T?> = min.invoke(skipNaN).cast2()

    private val min by withOneOption { skipNaN: Boolean ->
        twoStepSelecting<Comparable<Any>, Comparable<Any>?>(
            reducer = { type -> minOrNull(type, skipNaN) },
            indexOfResult = { type -> indexOfMin(type, skipNaN) },
        )
    }

    // T: Comparable<T> -> T?
    // T : Comparable<T & Any>? -> T?
    fun <T : Comparable<T & Any>?> max(skipNaN: Boolean): Aggregator<T & Any, T?> = max.invoke(skipNaN).cast2()

    private val max by withOneOption { skipNaN: Boolean ->
        twoStepSelecting<Comparable<Any>, Comparable<Any>?>(
            reducer = { type -> maxOrNull(type, skipNaN) },
            indexOfResult = { type -> indexOfMax(type, skipNaN) },
        )
    }

    // T: Number? -> Double
    val std by withTwoOptions { skipNA: Boolean, ddof: Int ->
        flatteningChangingTypes<Number, Double>(stdTypeConversion) { type ->
            asIterable().std(type, skipNA, ddof)
        }
    }

    // step one: T: Number? -> Double
    // step two: Double -> Double
    val mean by withOneOption { skipNaN: Boolean ->
        twoStepForNumbers(meanTypeConversion) { type ->
            mean(type, skipNaN)
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
    val sum by withOneOption { skipNaN: Boolean ->
        twoStepForNumbers(sumTypeConversion) { type ->
            sum(type, skipNaN)
        }
    }
}
