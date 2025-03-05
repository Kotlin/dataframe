package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.median
import org.jetbrains.kotlinx.dataframe.math.percentile
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.sum
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

@PublishedApi
internal object Aggregators {

    /**
     * Factory for a simple aggregator that preserves the type of the input values.
     *
     * @include [TwoStepAggregator]
     */
    private fun <Type> twoStepPreservingType(aggregator: Iterable<Type>.(type: KType) -> Type?) =
        TwoStepAggregator.Factory(
            getReturnTypeOrNull = preserveReturnTypeNullIfEmpty,
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
        getReturnTypeOrNull: (type: KType, emptyInput: Boolean) -> KType?,
        stepOneAggregator: Iterable<Value>.(type: KType) -> Return,
        stepTwoAggregator: Iterable<Return>.(type: KType) -> Return,
    ) = TwoStepAggregator.Factory(
        getReturnTypeOrNull = getReturnTypeOrNull,
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
            getReturnTypeOrNull = preserveReturnTypeNullIfEmpty,
            aggregator = aggregate,
            preservesType = true,
        )

    /**
     * Factory for a flattening aggregator that changes the type of the input values.
     *
     * @include [FlatteningAggregator]
     */
    private fun <Value, Return> flatteningChangingTypes(
        getReturnTypeOrNull: (type: KType, emptyInput: Boolean) -> KType?,
        aggregate: Iterable<Value?>.(type: KType) -> Return?,
    ) = FlatteningAggregator.Factory(
        getReturnTypeOrNull = getReturnTypeOrNull,
        aggregator = aggregate,
        preservesType = false,
    )

    /**
     * Factory for a two-step aggregator that works only with numbers.
     *
     * @include [TwoStepNumbersAggregator]
     */
    private fun <Return : Number> twoStepForNumbers(
        getReturnTypeOrNull: (type: KType, emptyInput: Boolean) -> KType?,
        aggregate: Iterable<Number>.(numberType: KType) -> Return?,
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
        flatteningChangingTypes<Number, Double>(
            getReturnTypeOrNull = { _, emptyInput -> typeOf<Double>().withNullability(emptyInput) },
        ) { type ->
            std(type, skipNA, ddof)
        }
    }

    // step one: T: Number? -> Double
    // step two: Double -> Double
    val mean by withOneOption { skipNA: Boolean ->
        twoStepChangingType(
            getReturnTypeOrNull = { _, _ -> typeOf<Double>() },
            stepOneAggregator = { type -> mean(type, skipNA) },
            stepTwoAggregator = { mean(skipNA) },
        )
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
    val sum by twoStepForNumbers(
        getReturnTypeOrNull = { type, _ -> type.withNullability(false) },
    ) { type ->
        sum(type)
    }
}
