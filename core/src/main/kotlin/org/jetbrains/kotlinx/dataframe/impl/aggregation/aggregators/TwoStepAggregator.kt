package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.isEmpty
import org.jetbrains.kotlinx.dataframe.impl.commonType
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

/**
 * A slightly more advanced [Aggregator] implementation.
 *
 * Nulls are filtered from columns.
 *
 * When called on multiple columns, this [Aggregator] works in two steps:
 * First, it aggregates within a [DataColumn]/[Iterable] ([stepOneAggregator]) with their (given) type,
 * and then in between different columns ([stepTwoAggregator]) using the results of the first and the newly
 * calculated common type of those results.
 *
 * ```
 * Iterable<Column<Value?>>
 *     -> Iterable<Iterable<Value>> // nulls filtered out
 *     -> stepOneAggregator(Iterable<Value>, colType) // called on each iterable
 *     -> Iterable<Return> // nulls filtered out
 *     -> stepTwoAggregator(Iterable<Return>, common valueType)
 *     -> Return?
 * ```
 *
 * It can also be used as a "simple" aggregator by providing the same function for both steps.
 *
 * See [FlatteningAggregator] for different behavior for multiple columns.
 *
 * @param name The name of this aggregator.
 * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull] function.
 * @param stepOneAggregator Functional argument for the [aggregate] function, used within a [DataColumn] or [Iterable].
 * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
 *   It is run on the results of [stepOneAggregator].
 */
internal class TwoStepAggregator<in Value, out Return>(
    name: String,
    getReturnTypeOrNull: CalculateReturnTypeOrNull,
    stepOneAggregator: Aggregate<Value, Return>,
    private val stepTwoAggregator: Aggregate<Return, Return>,
) : AggregatorBase<Value, Return>(name, getReturnTypeOrNull, stepOneAggregator) {

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     *
     * This function calls [stepOneAggregator] on each column and then [stepTwoAggregator] on the results.
     *
     * Post-step-one types are calculated by [calculateReturnTypeOrNull].
     */
    override fun aggregate(columns: Iterable<DataColumn<Value?>>): Return? {
        val (values, types) = columns.mapNotNull { col ->
            // uses stepOneAggregator
            val value = aggregate(col) ?: return@mapNotNull null
            val type = calculateReturnTypeOrNull(
                type = col.type().withNullability(false),
                emptyInput = col.isEmpty,
            ) ?: value::class.starProjectedType // heavy fallback type calculation

            value to type
        }.unzip()
        val commonType = types.commonType()
        return stepTwoAggregator(values, commonType)
    }

    /**
     * Function that can give the return type of [aggregate] with columns as [KType],
     * given the multiple types of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     * @return The return type of [aggregate] as [KType].
     */
    override fun calculateReturnTypeOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? {
        val typesAfterStepOne = colTypes.map { type ->
            calculateReturnTypeOrNull(type = type.withNullability(false), emptyInput = colsEmpty)
        }
        if (typesAfterStepOne.any { it == null }) return null
        return typesAfterStepOne.commonType()
    }

    /**
     * Creates [TwoStepAggregator].
     *
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull] function.
     * @param stepOneAggregator Functional argument for the [aggregate] function, used within a [DataColumn] or [Iterable].
     * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
     *   It is run on the results of [stepOneAggregator].
     */
    class Factory<in Value, out Return>(
        private val getReturnTypeOrNull: CalculateReturnTypeOrNull,
        private val stepOneAggregator: Aggregate<Value, Return>,
        private val stepTwoAggregator: Aggregate<Return, Return>,
    ) : AggregatorProvider<TwoStepAggregator<Value, Return>> by AggregatorProvider({ name ->
            TwoStepAggregator(
                name = name,
                getReturnTypeOrNull = getReturnTypeOrNull,
                stepOneAggregator = stepOneAggregator,
                stepTwoAggregator = stepTwoAggregator,
            )
        })
}
