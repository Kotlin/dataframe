package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.api.skipNaN_default
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.ReducingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.SelectingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.AnyInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.NumberInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.FlatteningMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.TwoStepMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.math.indexOfMax
import org.jetbrains.kotlinx.dataframe.math.indexOfMedian
import org.jetbrains.kotlinx.dataframe.math.indexOfMin
import org.jetbrains.kotlinx.dataframe.math.maxOrNull
import org.jetbrains.kotlinx.dataframe.math.maxTypeConversion
import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.meanTypeConversion
import org.jetbrains.kotlinx.dataframe.math.medianConversion
import org.jetbrains.kotlinx.dataframe.math.medianOrNull
import org.jetbrains.kotlinx.dataframe.math.minOrNull
import org.jetbrains.kotlinx.dataframe.math.minTypeConversion
import org.jetbrains.kotlinx.dataframe.math.percentile
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.stdTypeConversion
import org.jetbrains.kotlinx.dataframe.math.sum
import org.jetbrains.kotlinx.dataframe.math.sumTypeConversion

@PublishedApi
internal object Aggregators {

    // TODO these might need some small refactoring

    private fun <Value : Return & Any, Return : Any?> twoStepSelectingForAny(
        getReturnType: CalculateReturnType,
        indexOfResult: IndexOfResult<Value>,
        stepOneSelector: Selector<Value, Return>,
    ) = Aggregator(
        aggregationHandler = SelectingAggregationHandler(stepOneSelector, indexOfResult, getReturnType),
        inputHandler = AnyInputHandler(),
        multipleColumnsHandler = TwoStepMultipleColumnsHandler(),
    )

    private fun <Value : Return & Any, Return : Any?> flattenSelectingForAny(
        getReturnType: CalculateReturnType,
        indexOfResult: IndexOfResult<Value>,
        selector: Selector<Value, Return>,
    ) = Aggregator(
        aggregationHandler = SelectingAggregationHandler(selector, indexOfResult, getReturnType),
        inputHandler = AnyInputHandler(),
        multipleColumnsHandler = FlatteningMultipleColumnsHandler(),
    )

    private fun <Value : Any, Return : Any?> twoStepReducingForAny(
        getReturnType: CalculateReturnType,
        stepOneReducer: Reducer<Value, Return>,
        stepTwoReducer: Reducer<Return, Return>? = null,
    ) = Aggregator(
        aggregationHandler = ReducingAggregationHandler(stepOneReducer, getReturnType),
        inputHandler = AnyInputHandler(),
        multipleColumnsHandler = TwoStepMultipleColumnsHandler(
            stepTwoAggregationHandler = stepTwoReducer?.let {
                ReducingAggregationHandler<Return & Any, Return>(stepTwoReducer, getReturnType)
            },
        ),
    )

    private fun <Type : Any> flattenReducingForAny(reducer: Reducer<Type, Type?>) =
        Aggregator(
            aggregationHandler = ReducingAggregationHandler(reducer, preserveReturnTypeNullIfEmpty),
            inputHandler = AnyInputHandler(),
            multipleColumnsHandler = FlatteningMultipleColumnsHandler(),
        )

    private fun <Value : Any, Return : Any?> flattenReducingForAny(
        getReturnType: CalculateReturnType,
        reducer: Reducer<Value, Return>,
    ) = Aggregator(
        aggregationHandler = ReducingAggregationHandler(reducer, getReturnType),
        inputHandler = AnyInputHandler(),
        multipleColumnsHandler = FlatteningMultipleColumnsHandler(),
    )

    private fun <Return : Number?> flattenReducingForNumbers(
        getReturnType: CalculateReturnType,
        reducer: Reducer<Number, Return>,
    ) = Aggregator(
        aggregationHandler = ReducingAggregationHandler(reducer, getReturnType),
        inputHandler = NumberInputHandler(),
        multipleColumnsHandler = FlatteningMultipleColumnsHandler(),
    )

    private fun <Return : Number?> twoStepReducingForNumbers(
        getReturnType: CalculateReturnType,
        reducer: Reducer<Number, Return>,
    ) = Aggregator(
        aggregationHandler = ReducingAggregationHandler(reducer, getReturnType),
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
        twoStepSelectingForAny<Comparable<Any>, Comparable<Any>?>(
            getReturnType = minTypeConversion,
            stepOneSelector = { type -> minOrNull(type, skipNaN) },
            indexOfResult = { type -> indexOfMin(type, skipNaN) },
        )
    }

    // T: Comparable<T> -> T?
    // T : Comparable<T & Any>? -> T?
    fun <T : Comparable<T & Any>?> max(skipNaN: Boolean): Aggregator<T & Any, T?> = max.invoke(skipNaN).cast2()

    private val max by withOneOption { skipNaN: Boolean ->
        twoStepSelectingForAny<Comparable<Any>, Comparable<Any>?>(
            getReturnType = maxTypeConversion,
            stepOneSelector = { type -> maxOrNull(type, skipNaN) },
            indexOfResult = { type -> indexOfMax(type, skipNaN) },
        )
    }

    // T: Number? -> Double
    val std by withTwoOptions { skipNaN: Boolean, ddof: Int ->
        flattenReducingForNumbers(stdTypeConversion) { type ->
            std(type, skipNaN, ddof)
        }
    }

    // step one: T: Number? -> Double
    // step two: Double -> Double
    val mean by withOneOption { skipNaN: Boolean ->
        twoStepReducingForNumbers(meanTypeConversion) { type ->
            mean(type, skipNaN)
        }
    }

    // T: Comparable<T>? -> T
    val percentile by withOneOption { percentile: Double ->
        flattenReducingForAny<Comparable<Any?>> { type ->
            asIterable().percentile(percentile, type)
        }
    }

    fun <T> median(): Aggregator<T & Any, T?>
        where T : Comparable<T & Any>? =
        median.invoke(skipNaN_default).cast2()

    fun <T> median(skipNaN: Boolean): Aggregator<T & Any, Double>
        where T : Comparable<T & Any>?, T : Number? =
        median.invoke(skipNaN).cast2()

    // T: Comparable<T>? -> T
    @Suppress("UNCHECKED_CAST")
    private val median by withOneOption { skipNaN: Boolean ->
        flattenSelectingForAny<Comparable<Any>, Comparable<Any>?>(
            getReturnType = medianConversion,
            selector = { type -> medianOrNull(type, skipNaN) as Comparable<Any>? },
            indexOfResult = { type -> indexOfMedian(type, skipNaN) },
        )
    }

    // T: Number -> T
    val sum by withOneOption { skipNaN: Boolean ->
        twoStepReducingForNumbers(sumTypeConversion) { type ->
            sum(type, skipNaN)
        }
    }
}
