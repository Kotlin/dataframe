package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

/**
 * Wrapper around an [aggregator factory][AggregatorProvider] for aggregators that require a single parameter.
 *
 * Aggregators are cached by their parameter value.
 * @see AggregatorOptionSwitch2
 */
@PublishedApi
internal class AggregatorOptionSwitch1<in Param1, out AggregatorType : Aggregator<*, *>>(
    val name: String,
    val getAggregator: (param1: Param1) -> AggregatorProvider<AggregatorType>,
) {

    private val cache: MutableMap<Param1, AggregatorType> = mutableMapOf()

    operator fun invoke(param1: Param1): AggregatorType =
        cache.getOrPut(param1) {
            getAggregator(param1).create(name)
        }

    /**
     * Creates [AggregatorOptionSwitch1].
     *
     * Used like:
     * ```kt
     * val myAggregator by AggregatorOptionSwitch1.Factory { param1: Param1 ->
     *   MyAggregator.Factory(param1)
     * }
     */
    class Factory<in Param1, out AggregatorType : Aggregator<*, *>>(
        val getAggregator: (Param1) -> AggregatorProvider<AggregatorType>,
    ) : Provider<AggregatorOptionSwitch1<Param1, AggregatorType>> by Provider({ name ->
            AggregatorOptionSwitch1(name, getAggregator)
        })
}

/**
 * Wrapper around an [aggregator factory][AggregatorProvider] for aggregators that require two parameters.
 *
 * Aggregators are cached by their parameter values.
 * @see AggregatorOptionSwitch1
 */
@PublishedApi
internal class AggregatorOptionSwitch2<in Param1, in Param2, out AggregatorType : Aggregator<*, *>>(
    val name: String,
    val getAggregator: (param1: Param1, param2: Param2) -> AggregatorProvider<AggregatorType>,
) {

    private val cache: MutableMap<Pair<Param1, Param2>, AggregatorType> = mutableMapOf()

    operator fun invoke(param1: Param1, param2: Param2): AggregatorType =
        cache.getOrPut(param1 to param2) {
            getAggregator(param1, param2).create(name)
        }

    /**
     * Creates [AggregatorOptionSwitch2].
     *
     * Used like:
     * ```kt
     * val myAggregator by AggregatorOptionSwitch2.Factory { param1: Param1, param2: Param2 ->
     *   MyAggregator.Factory(param1, param2)
     * }
     * ```
     */
    class Factory<in Param1, in Param2, out AggregatorType : Aggregator<*, *>>(
        val getAggregator: (Param1, Param2) -> AggregatorProvider<AggregatorType>,
    ) : Provider<AggregatorOptionSwitch2<Param1, Param2, AggregatorType>> by Provider({ name ->
            AggregatorOptionSwitch2(name, getAggregator)
        })
}
