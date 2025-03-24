package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.isEmpty
import org.jetbrains.kotlinx.dataframe.impl.anyNull
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

internal interface TwoStepAggregator<in Value, out Return> : Aggregator<Value, Return> {

    val stepTwo: Aggregator<Return & Any, Return>

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
            val value = aggregateSingleColumn(col) ?: return@mapNotNull null
            val type = calculateReturnTypeOrNull(
                type = col.type(),
                emptyInput = col.isEmpty,
            ) ?: value::class.starProjectedType // heavy fallback type calculation

            value to type
        }.unzip()

        return stepTwo.aggregateCalculatingValueType(values.asSequence(), types.toSet())
    }

    /**
     * Function that can give the return type of [aggregateSingleIterable] with columns as [KType],
     * given the multiple types of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     * @return The return type of [aggregateSingleIterable] as [KType].
     */
    @Suppress("UNCHECKED_CAST")
    override fun calculateReturnTypeMultipleColumnsOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? {
        val typesAfterStepOne = colTypes.map { type ->
            calculateReturnTypeOrNull(type = type, emptyInput = colsEmpty)
        }
        if (typesAfterStepOne.anyNull()) return null

        val stepTwoValueType = stepTwo
            .calculateValueType(typesAfterStepOne.toSet() as Set<KType>)
            .withNullability(false)

        return stepTwo.calculateReturnTypeOrNull(
            type = stepTwoValueType,
            emptyInput = colsEmpty,
        )
    }
}
