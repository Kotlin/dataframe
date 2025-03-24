package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KType

/**
 * A slightly more advanced [Aggregator] implementation.
 *
 * Nulls are filtered out.
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
 *     -> Return
 * ```
 *
 * It can also be used as a "simple" aggregator by providing the same function for both steps.
 *
 * See [FlatteningCommonAggregator] for different behavior for multiple columns.
 *
 * @param name The name of this aggregator.
 * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeMultipleColumnsOrNull] function.
 * @param stepOneAggregator Functional argument for the [aggregateSingleIterable] function, used within a [DataColumn] or [Iterable].
 * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
 *   It is run on the results of [stepOneAggregator].
 */
internal class TwoStepCommonAggregator<in Value, out Return>(
    name: String,
    getReturnTypeOrNull: CalculateReturnTypeOrNull,
    stepOneAggregator: Aggregate<Value, Return>,
    stepTwoAggregator: Aggregate<Return & Any, Return>,
) : AggregatorBase<Value, Return>(name, getReturnTypeOrNull, stepOneAggregator),
    TwoStepAggregator<Value, Return>,
    CommonAggregator<Value, Return> {

    override val stepTwo: Aggregator<Return & Any, Return> = object :
        AggregatorBase<Return & Any, Return>(name, getReturnTypeOrNull, stepTwoAggregator),
        CommonAggregator<Return & Any, Return> {
        override fun aggregateMultipleColumns(columns: Sequence<DataColumn<@UnsafeVariance Return?>>): Return =
            error("")

        override fun calculateReturnTypeMultipleColumnsOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? =
            error("")
    }

    /**
     * Creates [TwoStepCommonAggregator].
     *
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeMultipleColumnsOrNull] function.
     * @param stepOneAggregator Functional argument for the [aggregateSingleIterable] function, used within a [DataColumn] or [Iterable].
     * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
     *   It is run on the results of [stepOneAggregator].
     */
    class Factory<in Value, out Return>(
        private val getReturnTypeOrNull: CalculateReturnTypeOrNull,
        private val stepOneAggregator: Aggregate<Value, Return>,
        private val stepTwoAggregator: Aggregate<Return & Any, Return>,
    ) : AggregatorProvider<TwoStepCommonAggregator<Value, Return>> by AggregatorProvider({ name ->
            TwoStepCommonAggregator(
                name = name,
                getReturnTypeOrNull = getReturnTypeOrNull,
                stepOneAggregator = stepOneAggregator,
                stepTwoAggregator = stepTwoAggregator,
            )
        })
}
