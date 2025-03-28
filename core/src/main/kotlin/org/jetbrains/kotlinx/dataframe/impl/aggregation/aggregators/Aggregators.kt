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
import org.jetbrains.kotlinx.dataframe.math.maxTypeConversion
import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.meanTypeConversion
import org.jetbrains.kotlinx.dataframe.math.median
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
        stepOneReducer: Reducer<Value, Return>,
    ) = Aggregator(
        aggregationHandler = SelectingAggregationHandler(stepOneReducer, indexOfResult, getReturnType),
        inputHandler = AnyInputHandler(),
        multipleColumnsHandler = TwoStepMultipleColumnsHandler(),
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
            stepOneReducer = { type -> minOrNull(type, skipNaN) },
            indexOfResult = { type -> indexOfMin(type, skipNaN) },
        )
    }

    // T: Comparable<T> -> T?
    // T : Comparable<T & Any>? -> T?
    fun <T : Comparable<T & Any>?> max(skipNaN: Boolean): Aggregator<T & Any, T?> = max.invoke(skipNaN).cast2()

    private val max by withOneOption { skipNaN: Boolean ->
        twoStepSelectingForAny<Comparable<Any>, Comparable<Any>?>(
            getReturnType = maxTypeConversion,
            stepOneReducer = { type -> maxOrNull(type, skipNaN) },
            indexOfResult = { type -> indexOfMax(type, skipNaN) },
        )
    }

    // T: Number? -> Double
    val std by withTwoOptions { skipNA: Boolean, ddof: Int ->
        flattenReducingForAny<Number, Double>(stdTypeConversion) { type ->
            asIterable().std(type, skipNA, ddof)
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

    // T: Comparable<T>? -> T
    val median by flattenReducingForAny<Comparable<Any?>> { type ->
        asIterable().median(type)
    }

    // T: Number -> T
    val sum by withOneOption { skipNaN: Boolean ->
        twoStepReducingForNumbers(sumTypeConversion) { type ->
            sum(type, skipNaN)
        }
    }
}
