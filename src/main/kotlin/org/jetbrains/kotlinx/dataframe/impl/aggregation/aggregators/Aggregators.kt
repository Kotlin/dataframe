package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators.std
import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.median
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.sum
import kotlin.reflect.KType

@PublishedApi
internal object Aggregators {

    private fun <C> preservesType(aggregate: Iterable<C>.(KType) -> C?) =
        TwoStepAggregator.Factory(aggregate, aggregate, true)

    private fun <C : Any, R> mergedValues(aggregate: Iterable<C?>.(KType) -> R?) =
        MergedValuesAggregator.Factory(aggregate, true)

    private fun <C, R> changesType(aggregate1: Iterable<C>.(KType) -> R, aggregate2: Iterable<R>.(KType) -> R) =
        TwoStepAggregator.Factory(aggregate1, aggregate2, false)

    private fun extendsNumbers(aggregate: Iterable<Number>.(KType) -> Number?) =
        NumbersAggregator.Factory(aggregate)

    private fun <P, C, R> withOption(getAggregator: (P) -> AggregatorProvider<C, R>) =
        AggregatorOptionSwitch.Factory(getAggregator)

    private fun <P1, P2, C, R> withOption2(getAggregator: (P1, P2) -> AggregatorProvider<C, R>) =
        AggregatorOptionSwitch2.Factory(getAggregator)

    val min by preservesType<Comparable<Any?>> { minOrNull() }
    val max by preservesType<Comparable<Any?>> { maxOrNull() }
    val std by withOption2<Boolean, Int, Number, Double> { skipNA, ddof ->
        mergedValues { std(it, skipNA, ddof) }
    }
    val mean by withOption<Boolean, Number, Double> { skipNA ->
        changesType({ mean(it, skipNA) }) { mean(skipNA) }
    }
    val median by mergedValues<Comparable<Any?>, Comparable<Any?>> { median(it) }
    val sum by extendsNumbers { sum(it) }
}
