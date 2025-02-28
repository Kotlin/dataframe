package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.math.meanOrNull
import org.jetbrains.kotlinx.dataframe.math.median
import org.jetbrains.kotlinx.dataframe.math.percentile
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.sum
import java.math.BigDecimal
import kotlin.reflect.KType

@PublishedApi
internal object Aggregators {

    /**
     * Factory for a simple aggregator that preserves the type of the input values.
     *
     * @include [TwoStepAggregator]
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
     * @include [TwoStepAggregator]
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
     * @include [FlatteningAggregator]
     */
    private fun <Type> flatteningPreservingTypes(aggregate: Iterable<Type?>.(type: KType) -> Type?) =
        FlatteningAggregator.Factory(
            aggregator = aggregate,
            preservesType = true,
        )

    /**
     * Factory for a flattening aggregator that changes the type of the input values.
     *
     * @include [FlatteningAggregator]
     */
    private fun <Value, Return> flatteningChangingTypes(aggregate: Iterable<Value?>.(type: KType) -> Return?) =
        FlatteningAggregator.Factory(
            aggregator = aggregate,
            preservesType = false,
        )

    /**
     * Factory for a two-step aggregator that works only with numbers.
     *
     * @include [TwoStepNumbersAggregator]
     */
    private fun <Return : Number> twoStepForNumbers(aggregate: Iterable<Number>.(numberType: KType) -> Return?) =
        TwoStepNumbersAggregator.Factory(aggregate)

    /** @include [AggregatorOptionSwitch1] */
    private fun <Param1, AggregatorType : Aggregator<*, *>> withOneOption(
        getAggregator: (Param1) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch1.Factory(getAggregator)

    /** @include [AggregatorOptionSwitch2] */
    private fun <Param1, Param2, AggregatorType : Aggregator<*, *>> withTwoOptions(
        getAggregator: (Param1, Param2) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch2.Factory(getAggregator)

    val min by twoStepPreservingType<Comparable<Any?>> { minOrNull() }

    val max by twoStepPreservingType<Comparable<Any?>> { maxOrNull() }

    val std by withTwoOptions { skipNA: Boolean, ddof: Int ->
        flatteningChangingTypes<Number, Double> { std(it, skipNA, ddof) }
    }

    @Suppress("ClassName")
    object mean {
        val toNumber = withOneOption { skipNA: Boolean ->
            twoStepForNumbers { meanOrNull(it, skipNA) }
        }.create(mean::class.simpleName!!)

        val toDouble = withOneOption { skipNA: Boolean ->
            twoStepForNumbers { meanOrNull(it, skipNA) as Double? }
        }.create(mean::class.simpleName!!)

        val toBigDecimal =
            twoStepForNumbers {
                meanOrNull(it) as BigDecimal?
            }.create(mean::class.simpleName!!)
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
