package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.api.skipNaNDefault
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.HybridAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.ReducingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers.SelectingAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.AnyInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers.NumberInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.FlatteningMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.TwoStepMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.math.indexOfMax
import org.jetbrains.kotlinx.dataframe.math.indexOfMedian
import org.jetbrains.kotlinx.dataframe.math.indexOfMin
import org.jetbrains.kotlinx.dataframe.math.indexOfPercentile
import org.jetbrains.kotlinx.dataframe.math.maxOrNull
import org.jetbrains.kotlinx.dataframe.math.maxTypeConversion
import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.meanTypeConversion
import org.jetbrains.kotlinx.dataframe.math.medianConversion
import org.jetbrains.kotlinx.dataframe.math.medianOrNull
import org.jetbrains.kotlinx.dataframe.math.minOrNull
import org.jetbrains.kotlinx.dataframe.math.minTypeConversion
import org.jetbrains.kotlinx.dataframe.math.percentileConversion
import org.jetbrains.kotlinx.dataframe.math.percentileOrNull
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.stdTypeConversion
import org.jetbrains.kotlinx.dataframe.math.sum
import org.jetbrains.kotlinx.dataframe.math.sumTypeConversion

public object Aggregators {

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

    private fun <Value : Any, Return : Any?> flattenHybridForAny(
        getReturnType: CalculateReturnType,
        indexOfResult: IndexOfResult<Value>,
        reducer: Reducer<Value, Return>,
    ) = Aggregator(
        aggregationHandler = HybridAggregationHandler(reducer, indexOfResult, getReturnType),
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
    public fun <T : Comparable<T & Any>?> min(skipNaN: Boolean): Aggregator<T & Any, T?> = min.invoke(skipNaN).cast2()

    public val min: AggregatorOptionSwitch1<Boolean, Comparable<Any>, Comparable<Any>?>
        by withOneOption { skipNaN: Boolean ->
            twoStepSelectingForAny<Comparable<Any>, Comparable<Any>?>(
                getReturnType = minTypeConversion,
                stepOneSelector = { type -> minOrNull(type, skipNaN) },
                indexOfResult = { type -> indexOfMin(type, skipNaN) },
            )
        }

    // T: Comparable<T> -> T?
    // T : Comparable<T & Any>? -> T?

    // idea: if the following function get the ValueColumnImpl. I know if there is any stored value
    // -> I return it.
    // else I do the procedure max.invoke... and then cache the value inside
    public fun <T : Comparable<T & Any>?> max(skipNaN: Boolean): Aggregator<T & Any, T?> = max.invoke(skipNaN).cast2()

    public val max: AggregatorOptionSwitch1<Boolean, Comparable<Any>, Comparable<Any>?>
        by withOneOption { skipNaN: Boolean ->
            twoStepSelectingForAny<Comparable<Any>, Comparable<Any>?>(
                getReturnType = maxTypeConversion,
                // idea, need to change the following line
                // if(thereIsCachedValueInsideValueColumnImpl) that's what I want
                // else ...
                // maxOrNull is called on a sequence... -> previous idea can't be applied this way LOOK UP
                stepOneSelector = { type -> maxOrNull(type, skipNaN) },
                indexOfResult = { type -> indexOfMax(type, skipNaN) },
            )
        }

    // T: Number? -> Double
    public val std: AggregatorOptionSwitch2<Boolean, Int, Number, Double> by withTwoOptions {
        skipNaN: Boolean,
        ddof: Int,
        ->
        flattenReducingForNumbers(stdTypeConversion) { type ->
            std(type, skipNaN, ddof)
        }
    }

    // step one: T: Number? -> Double
    // step two: Double -> Double
    public val mean: AggregatorOptionSwitch1<Boolean, Number, Double> by withOneOption { skipNaN: Boolean ->
        twoStepReducingForNumbers(meanTypeConversion) { type ->
            mean(type, skipNaN)
        }
    }

    // T: primitive Number? -> Double?
    // T: Comparable<T & Any>? -> T?
    public fun <T> percentileCommon(
        percentile: Double,
        skipNaN: Boolean,
    ): Aggregator<T & Any, T?>
        where T : Comparable<T & Any>? =
        this.percentile.invoke(percentile, skipNaN).cast2()

    // T: Comparable<T & Any>? -> T?
    public fun <T> percentileComparables(
        percentile: Double,
    ): Aggregator<T & Any, T?>
        where T : Comparable<T & Any>? =
        percentileCommon<T>(percentile, skipNaNDefault).cast2()

    // T: primitive Number? -> Double?
    public fun <T> percentileNumbers(
        percentile: Double,
        skipNaN: Boolean,
    ): Aggregator<T & Any, Double?>
        where T : Comparable<T & Any>?, T : Number? =
        percentileCommon<T>(percentile, skipNaN).cast2()

    @Suppress("UNCHECKED_CAST")
    public val percentile: AggregatorOptionSwitch2<Double, Boolean, Comparable<Any>, Comparable<Any>?>
        by withTwoOptions {
            percentile: Double,
            skipNaN: Boolean,
            ->
            flattenHybridForAny<Comparable<Any>, Comparable<Any>?>(
                getReturnType = percentileConversion,
                reducer = { type -> percentileOrNull(percentile, type, skipNaN) as Comparable<Any>? },
                indexOfResult = { type -> indexOfPercentile(percentile, type, skipNaN) },
            )
        }

    // T: primitive Number? -> Double?
    // T: Comparable<T & Any>? -> T?
    public fun <T> medianCommon(skipNaN: Boolean): Aggregator<T & Any, T?>
        where T : Comparable<T & Any>? =
        median.invoke(skipNaN).cast2()

    // T: Comparable<T & Any>? -> T?
    public fun <T> medianComparables(): Aggregator<T & Any, T?>
        where T : Comparable<T & Any>? =
        medianCommon<T>(skipNaNDefault).cast2()

    // T: primitive Number? -> Double?
    public fun <T> medianNumbers(
        skipNaN: Boolean,
    ): Aggregator<T & Any, Double?>
        where T : Comparable<T & Any>?, T : Number? =
        medianCommon<T>(skipNaN).cast2()

    @Suppress("UNCHECKED_CAST")
    public val median: AggregatorOptionSwitch1<Boolean, Comparable<Any>, Comparable<Any>?>
        by withOneOption { skipNaN: Boolean ->
            flattenHybridForAny<Comparable<Any>, Comparable<Any>?>(
                getReturnType = medianConversion,
                reducer = { type -> medianOrNull(type, skipNaN) as Comparable<Any>? },
                indexOfResult = { type -> indexOfMedian(type, skipNaN) },
            )
        }

    // T: Number -> T
    // Byte -> Int
    // Short -> Int
    // Nothing -> Double
    public val sum: AggregatorOptionSwitch1<Boolean, Number, Number> by withOneOption { skipNaN: Boolean ->
        twoStepReducingForNumbers(sumTypeConversion) { type ->
            sum(type, skipNaN)
        }
    }
}
