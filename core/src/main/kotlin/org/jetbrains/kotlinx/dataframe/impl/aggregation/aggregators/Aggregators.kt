package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

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
     * @include [TwoStepAggregator]
     */
    private fun <Type> twoStepPreservingType(aggregator: Aggregate<Type, Type?>) =
        TwoStepAggregator.Factory(
            getReturnTypeOrNull = preserveReturnTypeNullIfEmpty,
            stepOneAggregator = aggregator,
            stepTwoAggregator = aggregator,
        )

    /**
     * Factory for a simple aggregator that changes the type of the input values.
     *
     * @include [TwoStepAggregator]
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
     * @include [FlatteningAggregator]
     */
    private fun <Type> flatteningPreservingTypes(aggregate: Aggregate<Type, Type?>) =
        FlatteningAggregator.Factory(
            getReturnTypeOrNull = preserveReturnTypeNullIfEmpty,
            aggregator = aggregate,
        )

    /**
     * Factory for a flattening aggregator that changes the type of the input values.
     *
     * @include [FlatteningAggregator]
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
     * @include [TwoStepNumbersAggregator]
     */
    private fun <Return : Number?> twoStepForNumbers(
        getReturnTypeOrNull: CalculateReturnTypeOrNull,
        aggregate: Aggregate<Number, Return>,
    ) = TwoStepNumbersAggregator.Factory(
        getReturnTypeOrNull = getReturnTypeOrNull,
        aggregate = aggregate,
    )

    /** @include [AggregatorOptionSwitch1] */
    private fun <Param1, AggregatorType : Aggregator<*, *>> withOneOption(
        getAggregator: (Param1) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch1.Factory(getAggregator)

    /** @include [AggregatorOptionSwitch2] */
    private fun <Param1, Param2, AggregatorType : Aggregator<*, *>> withTwoOptions(
        getAggregator: (Param1, Param2) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch2.Factory(getAggregator)

    private fun <Value, Return> AggregatorProvider<Aggregator<Value, Return>>.asByAggregator(
        aggregatorBy: AggregateBy<Any?, Value, Return>,
    ) where Value : Any, Value : Comparable<Value> = ByAggregator.Factory(this, aggregatorBy)

    // T: Comparable<T> -> T?
    fun <T : Comparable<T>?> min() = min.cast2<T, T?>()

    private val min by twoStepPreservingType<Comparable<Any?>?> { type ->
        minOrNull(type)
    }.asByAggregator { sourceType, valueType, selector ->
        minByOrNull(selector)
    }

    // T: Comparable<T> -> T?
    fun <T : Comparable<T>?> max() = max.cast2<T, T?>()

    private val max by twoStepPreservingType<Comparable<Any?>> {
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
