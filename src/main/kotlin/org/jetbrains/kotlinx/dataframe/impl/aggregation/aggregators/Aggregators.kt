package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.median
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.sum
import kotlin.reflect.KType

@PublishedApi
internal object Aggregators {

    private fun <C> preservesType(aggregate: Iterable<C>.(KType) -> C?) =
        TwoStepAggregator.Factory(aggregate, aggregate, true)

    private fun <C, R> changesType(aggregate1: Iterable<C>.(KType) -> R, aggregate2: Iterable<R>.(KType) -> R) =
        TwoStepAggregator.Factory(aggregate1, aggregate2, false)

    private fun extendsNumbers(aggregate: Iterable<Number>.(KType) -> Number?) =
        NumbersAggregator.Factory(aggregate)

    private fun <P, C, R> withOption(getAggregator: (P) -> AggregatorProvider<C, R>) =
        AggregatorOptionSwitch.Factory(getAggregator)

    val min by preservesType<Comparable<Any?>> { minOrNull() }
    val max by preservesType<Comparable<Any?>> { maxOrNull() }
    val std by changesType<Number?, Double>({ std(it) }) { std() }
    val mean by withOption<Boolean, Number, Double> { skipNa ->
        changesType({ mean(it, skipNa) }) { mean(skipNa) }
    }
    val median by preservesType<Comparable<Any?>> { median(it) }
    val sum by extendsNumbers { sum(it) }
}
