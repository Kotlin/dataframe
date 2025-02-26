package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.median
import org.jetbrains.kotlinx.dataframe.math.percentile
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.sum
import kotlin.reflect.KType

@PublishedApi
internal object Aggregators {

    /** todo
     * Factory for a simple aggregator that preserves the type of the input values.
     *
     * Null values in columns are filtered out.
     *
     * For aggregating across multiple columns, the [aggregator] is applied to each column individually first,
     * and then the results are aggregated again using the same [aggregator].
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
     * Because the type changes after the first round of aggregations
     * (within a [DataColumn]/[Iterable], [stepOneAggregator]),
     * you must define separately how to aggregate the values of those results ([stepTwoAggregator]).
     *
     * Nulls are filtered from columns.
     */
    private fun <Value, Return> twoStepChangingType(
        stepOneAggregator: Iterable<Value>.(type: KType) -> Return,
        stepTwoAggregator: Iterable<Return>.(type: KType) -> Return,
    ) = TwoStepAggregator.Factory(
        stepOneAggregator = stepOneAggregator,
        stepTwoAggregator = stepTwoAggregator,
        preservesType = false,
    )

    private fun <Type> oneStepPreservingTypes(aggregate: Iterable<Type?>.(type: KType) -> Type?) =
        OneStepAggregator.Factory(
            aggregator = aggregate,
            preservesType = true,
        )

    private fun <Value, Return> oneStepChangingTypes(aggregate: Iterable<Value?>.(type: KType) -> Return?) =
        OneStepAggregator.Factory(
            aggregator = aggregate,
            preservesType = false,
        )

    private fun extendsNumbers(aggregate: Iterable<Number>.(numberType: KType) -> Number?) =
        NumbersAggregator.Factory(aggregate)

    private fun <Param1, AggregatorType : Aggregator<*, *>> withOption1(
        getAggregator: (Param1) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch1.Factory(getAggregator)

    private fun <Param1, Param2, AggregatorType : Aggregator<*, *>> withOption2(
        getAggregator: (Param1, Param2) -> AggregatorProvider<AggregatorType>,
    ) = AggregatorOptionSwitch2.Factory(getAggregator)

    val min by twoStepPreservingType<Comparable<Any?>> { minOrNull() }

    val max by twoStepPreservingType<Comparable<Any?>> { maxOrNull() }

    val std by withOption2 { skipNA: Boolean, ddof: Int ->
        oneStepChangingTypes<Number, Double> { std(it, skipNA, ddof) }
    }

    val mean by withOption1 { skipNA: Boolean ->
        twoStepChangingType({ mean(it, skipNA) }) { mean(skipNA) }
    }

    val percentile by withOption1 { percentile: Double ->
        oneStepChangingTypes<Comparable<Any?>, Comparable<Any?>> { type ->
            percentile(percentile, type)
        }
    }

    val median by oneStepPreservingTypes<Comparable<Any?>> {
        median(it)
    }

    val sum by extendsNumbers { sum(it) }
}
