package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

/**
 * Wrapper around an [aggregator factory][AggregatorProvider] for aggregators that require a single parameter.
 *
 * Aggregators are cached by their parameter value.
 * @see AggregatorOptionSwitch2
 */
@PublishedApi
internal class AggregatorOptionSwitch1<in Param1, in Value : Any, out Return : Any?>(
    val name: String,
    val getAggregator: (param1: Param1) -> AggregatorProvider<Value, Return>,
) {

    private val cache: MutableMap<Param1, Aggregator<Value, Return>> = mutableMapOf()

    operator fun invoke(param1: Param1): Aggregator<Value, Return> =
        cache.getOrPut(param1) {
            getAggregator(param1).create(name)
        }

    @Suppress("FunctionName")
    companion object {

        /**
         * Creates [AggregatorOptionSwitch1].
         *
         * Used like:
         * ```kt
         * val myAggregator by AggregatorOptionSwitch1.Factory { param1: Param1 ->
         *   MyAggregator.Factory(param1)
         * }
         */
        fun <Param1, Value : Any, Return : Any?> Factory(
            getAggregator: (param1: Param1) -> AggregatorProvider<Value, Return>,
        ) = Provider { name -> AggregatorOptionSwitch1(name, getAggregator) }
    }
}

/**
 * Wrapper around an [aggregator factory][AggregatorProvider] for aggregators that require two parameters.
 *
 * Aggregators are cached by their parameter values.
 * @see AggregatorOptionSwitch1
 */
@PublishedApi
internal class AggregatorOptionSwitch2<in Param1, in Param2, in Value : Any, out Return : Any?>(
    val name: String,
    val getAggregator: (param1: Param1, param2: Param2) -> AggregatorProvider<Value, Return>,
) {

    private val cache: MutableMap<Pair<Param1, Param2>, Aggregator<Value, Return>> = mutableMapOf()

    operator fun invoke(param1: Param1, param2: Param2): Aggregator<Value, Return> =
        cache.getOrPut(param1 to param2) {
            getAggregator(param1, param2).create(name)
        }

    @Suppress("FunctionName")
    companion object {

        /**
         * Creates [AggregatorOptionSwitch2].
         *
         * Used like:
         * ```kt
         * val myAggregator by AggregatorOptionSwitch2.Factory { param1: Param1, param2: Param2 ->
         *   MyAggregator.Factory(param1, param2)
         * }
         */
        fun <Param1, Param2, Value : Any, Return : Any?> Factory(
            getAggregator: (param1: Param1, param2: Param2) -> AggregatorProvider<Value, Return>,
        ) = Provider { name -> AggregatorOptionSwitch2(name, getAggregator) }
    }
}
