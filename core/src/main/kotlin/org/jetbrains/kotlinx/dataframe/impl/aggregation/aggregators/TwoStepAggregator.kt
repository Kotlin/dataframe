package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.classes
import org.jetbrains.kotlinx.dataframe.impl.commonType
import kotlin.reflect.KType

/**
 * A slightly more advanced [Aggregator] implementation.
 *
 * Nulls are filtered from columns.
 *
 * When called on multiple columns, this [Aggregator] works in two steps:
 * First, it aggregates within a [DataColumn]/[Iterable] ([stepOneAggregator]) with the given type,
 * and then in between different columns ([stepTwoAggregator]) using the results of the first and the newly
 * calculated common type of those results.
 *
 * It can also be used as a "simple" aggregator by providing the same function for both steps,
 * requires [preservesType] be set to `true`.
 *
 * See [OneStepAggregator] for different behavior for multiple columns.
 *
 * @param name The name of this aggregator.
 * @param stepOneAggregator Functional argument for the [aggregate] function, used within a [DataColumn] or [Iterable].
 * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
 *   It is run on the results of [stepOneAggregator].
 * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
 */
internal class TwoStepAggregator<Value, Return>(
    name: String,
    stepOneAggregator: (values: Iterable<Value>, type: KType) -> Return?,
    private val stepTwoAggregator: (values: Iterable<Return>, type: KType) -> Return?,
    override val preservesType: Boolean,
) : AggregatorBase<Value, Return>(name, stepOneAggregator) {

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     *
     * This function calls [stepOneAggregator] on each column and then [stepTwoAggregator] on the results.
     */
    override fun aggregate(columns: Iterable<DataColumn<Value?>>): Return? {
        val columnValues = columns.mapNotNull {
            // uses stepOneAggregator
            aggregate(it)
        }
        val commonType = columnValues.classes().commonType(false)
        return stepTwoAggregator(columnValues, commonType)
    }

    /**
     * Creates [TwoStepAggregator].
     *
     * @param stepOneAggregator Functional argument for the [aggregate] function, used within a [DataColumn] or [Iterable].
     * @param stepTwoAggregator Functional argument for the aggregation function used between different columns.
     *   It is run on the results of [stepOneAggregator].
     * @param preservesType If `true`, [Value][Value]`  ==  `[Return][Return].
     */
    class Factory<Value, Return>(
        private val stepOneAggregator: (Iterable<Value>, KType) -> Return?,
        private val stepTwoAggregator: (Iterable<Return>, KType) -> Return?,
        private val preservesType: Boolean,
    ) : AggregatorProvider<TwoStepAggregator<Value, Return>> by AggregatorProvider({ name ->
            TwoStepAggregator(
                name = name,
                stepOneAggregator = stepOneAggregator,
                stepTwoAggregator = stepTwoAggregator,
                preservesType = preservesType,
            )
        })
}
