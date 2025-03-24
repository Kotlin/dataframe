package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal class FlatteningMultipleColumnsHandler<in Value, out Return> :
    AggregatorMultipleColumnsHandler<Value, Return> {

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     * The columns are flattened into a single list of values, filtering nulls as usual;
     * then the aggregation function is with the common type of the columns.
     */
    override fun aggregateMultipleColumns(columns: Sequence<DataColumn<Value?>>): Return {
        val commonType = aggregator!!.calculateValueType(columns.map { it.type() }.toSet())
            .run { copy(kType = kType.withNullability(false)) }
        val allValues = columns.flatMap { it.values() }.filterNotNull()
        return aggregator!!.aggregateSingleSequence(allValues, commonType)
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
    override fun calculateReturnTypeMultipleColumnsOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? =
        aggregator!!.calculateReturnTypeOrNull(
            type = aggregator!!.calculateValueType(colTypes).kType.withNullability(false),
            emptyInput = colsEmpty,
        )

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
