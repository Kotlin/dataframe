package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.isEmpty
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregateCalculatingValueType
import org.jetbrains.kotlinx.dataframe.impl.anyNull
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

/**
 *
 * @param stepTwoAggregationHandler The [aggregation handler][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorAggregationHandler] for the second step.
 *   If not supplied, the handler of the first step is reused.
 * @param stepTwoInputHandler The [input handler][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorInputHandler] for the second step.
 *   If not supplied, the handler of the first step is reused.
 */
internal class TwoStepMultipleColumnsHandler<in Value : Any, out Return : Any?>(
    stepTwoAggregationHandler: AggregatorAggregationHandler<Return & Any, Return>? = null,
    stepTwoInputHandler: AggregatorInputHandler<Return & Any, Return>? = null,
) : AggregatorMultipleColumnsHandler<Value, Return> {

    @Suppress("UNCHECKED_CAST")
    val stepTwo by lazy {
        Aggregator.Companion.invoke(
            aggregationHandler = stepTwoAggregationHandler
                ?: aggregator as AggregatorAggregationHandler<Return & Any, Return>,
            inputHandler = stepTwoInputHandler ?: aggregator as AggregatorInputHandler<Return & Any, Return>,
            multipleColumnsHandler = NoMultipleColumnsHandler(),
        ).create(aggregator!!.name)
    }

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     *
     * This function calls [stepOneAggregator] on each column and then [stepTwoAggregator] on the results.
     *
     * Post-step-one types are calculated by [calculateReturnTypeMultipleColumnsOrNull].
     */
    override fun aggregateMultipleColumns(columns: Sequence<DataColumn<Value?>>): Return {
        val (values, types) = columns.mapNotNull { col ->
            // uses stepOneAggregator
            val value = aggregator!!.aggregateSingleColumn(col) ?: return@mapNotNull null
            val type = aggregator!!.calculateReturnTypeOrNull(
                type = col.type(),
                emptyInput = col.isEmpty,
            ) ?: value::class.starProjectedType // heavy fallback type calculation

            value to type
        }.unzip()

        return stepTwo.aggregateCalculatingValueType(values.asSequence(), types.toSet())
    }

    /**
     * Function that can give the return type of [aggregateSingleSequence] with columns as [KType],
     * given the multiple types of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     * @return The return type of [aggregateSingleSequence] as [KType].
     */
    @Suppress("UNCHECKED_CAST")
    override fun calculateReturnTypeMultipleColumnsOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? {
        val typesAfterStepOne = colTypes.map { type ->
            aggregator!!.calculateReturnTypeOrNull(type = type, emptyInput = colsEmpty)
        }
        if (typesAfterStepOne.anyNull()) return null

        val stepTwoValueType = stepTwo
            .calculateValueType(typesAfterStepOne.toSet() as Set<KType>)

        return stepTwo.calculateReturnTypeOrNull(
            type = stepTwoValueType.kType.withNullability(false),
            emptyInput = colsEmpty,
        )
    }

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
