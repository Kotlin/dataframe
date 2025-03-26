package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnTypeOrNull
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.IndexOfResult
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Reducer
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.toValueType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal class SelectingAggregationHandler<in Value : Return & Any, out Return : Any?>(
    val reducer: Reducer<Value, Return>,
    val indexOfResult: IndexOfResult<Value>,
    val getReturnTypeOrNull: CalculateReturnTypeOrNull,
) : AggregatorAggregationHandler<Value, Return> {

    @Suppress("UNCHECKED_CAST")
    override fun indexOfAggregationResultSingleSequence(values: Sequence<Value?>, valueType: ValueType): Int {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return indexOfResult(values, valueType)
    }

    @Suppress("UNCHECKED_CAST")
    override fun aggregateSingleSequence(values: Sequence<Value?>, valueType: ValueType): Return {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return reducer(
            // values =
            if (valueType.isMarkedNullable) {
                values.filterNotNull()
            } else {
                values as Sequence<Value>
            },
            // type =
            valueType.withNullability(false),
        )
    }

    override fun aggregateSingleColumn(column: DataColumn<Value?>): Return =
        aggregateSingleSequence(
            values = column.asSequence(),
            valueType = column.type().toValueType(),
        )

    override fun calculateReturnTypeOrNull(type: KType, emptyInput: Boolean): KType? =
        getReturnTypeOrNull(type, emptyInput)

    override var aggregator: Aggregator<@UnsafeVariance Value, @UnsafeVariance Return>? = null
}
