package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.isEmpty
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregateCalculatingValueType
import kotlin.reflect.KType

/**
 * Implementation of [<code>AggregatorMultipleColumnsHandler</code>][AggregatorMultipleColumnsHandler] that aggregates the data in two steps:
 * - first, it aggregates the data for each of the given columns
 * - then, it aggregates the results of the first step into a single value
 *
 * For the first step, this [<code>aggregator</code>][aggregator] will be used as usual.
 *
 * For the second step, a [<code>new</code>][stepTwo] [<code>Aggregator</code>][Aggregator] will be constructed using the supplied handlers:
 * [<code>stepTwoAggregationHandler</code>][stepTwoAggregationHandler] and [<code>stepTwoInputHandler</code>][stepTwoInputHandler].
 * For both arguments, it holds that if they are not supplied or `null`,
 * the handlers of this [<code>aggregator</code>][aggregator] will be cast and reused.
 * In all cases [<code>NoMultipleColumnsHandler</code>][NoMultipleColumnsHandler] will be used as [<code>AggregatorMultipleColumnsHandler</code>][AggregatorMultipleColumnsHandler].
 *
 * This is useful for aggregators that do not depend on the distribution of values across multiple columns.
 * It may be more memory efficient than [<code>FlatteningMultipleColumnsHandler</code>][FlatteningMultipleColumnsHandler] and could be parallelized in the future.
 *
 * @param stepTwoAggregationHandler The [<code>aggregation handler</code>][AggregatorAggregationHandler] for the second step.
 *   If not supplied, the handler of the first step is reused.
 * @param stepTwoInputHandler The [<code>input handler</code>][AggregatorInputHandler] for the second step.
 *   If not supplied, the handler of the first step is reused.
 * @see [FlatteningMultipleColumnsHandler]
 */
internal class TwoStepMultipleColumnsHandler<in Value : Any, Return : Any?>(
    stepTwoAggregationHandler: AggregatorAggregationHandler<Return & Any, Return>? = null,
    stepTwoInputHandler: AggregatorInputHandler<Return & Any, Return>? = null,
) : AggregatorMultipleColumnsHandler<Value, Return> {

    /**
     * The second step [<code>Aggregator</code>][Aggregator] which can take multiple outputs of
     * this [<code>aggregator</code>][aggregator] and aggregate it to a single value.
     */
    @Suppress("UNCHECKED_CAST")
    val stepTwo by lazy {
        Aggregator(
            aggregationHandler = stepTwoAggregationHandler
                ?: aggregator as AggregatorAggregationHandler<Return & Any, Return>,
            inputHandler = stepTwoInputHandler ?: aggregator as AggregatorInputHandler<Return & Any, Return>,
            multipleColumnsHandler = NoMultipleColumnsHandler(),
            statisticsParameters = emptyMap(),
        ).create(aggregator!!.name)
    }

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     *
     * This function calls [<code>aggregator</code>][aggregator] [<code>.aggregateSingleColumn()</code>][Aggregator.aggregateSingleColumn]
     * on each column and then [<code>stepTwo</code>][stepTwo] [<code>.aggregateSequence()</code>][Aggregator.aggregateSequence] on the results.
     */
    override fun aggregateMultipleColumns(columns: Sequence<DataColumn<Value?>>): Return {
        val (values, types) = columns.map { col ->
            val value = aggregator!!.aggregateSingleColumn(col)
            val type = aggregator!!.calculateReturnType(
                valueType = col.type(),
                emptyInput = col.isEmpty,
            )
            value to type
        }.unzip()

        return stepTwo.aggregateCalculatingValueType(values.asSequence(), types.toSet())
    }

    /**
     * Function that can give the return type of [<code>aggregateMultipleColumns</code>][aggregateMultipleColumns], given types of the columns.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     */
    @Suppress("UNCHECKED_CAST")
    override fun calculateReturnTypeMultipleColumns(colTypes: Set<KType>, colsEmpty: Boolean): KType {
        val typesAfterStepOne = colTypes.map { type ->
            aggregator!!.calculateReturnType(valueType = type, emptyInput = colsEmpty)
        }
        val stepTwoValueType = stepTwo.calculateValueType(typesAfterStepOne.toSet())
        return stepTwo.calculateReturnType(
            valueType = stepTwoValueType.kType,
            emptyInput = colsEmpty,
        )
    }

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
