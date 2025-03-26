package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregationHandlers

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorAggregationHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnTypeOrNull
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.IsBetterThanSelector
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.toValueType
import org.jetbrains.kotlinx.dataframe.impl.bestByOrNull
import org.jetbrains.kotlinx.dataframe.impl.bestNotNullByOrNull
import org.jetbrains.kotlinx.dataframe.impl.indexOfBestNotNullBy
import kotlin.reflect.KType

// todo remove?
internal class SelectingBestAggregationHandler<in Value : Return & Any, out Return : Any?>(
    val isBetterThan: IsBetterThanSelector<Value>,
    val getReturnTypeOrNull: CalculateReturnTypeOrNull,
) : AggregatorAggregationHandler<Value, Return> {

    @Suppress("UNCHECKED_CAST")
    override fun indexOfAggregationResultSingleSequence(values: Sequence<Value?>, valueType: ValueType): Int {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return if (valueType.isMarkedNullable) {
            values.indexOfBestNotNullBy {
                this.isBetterThan(it, valueType)
            }
        } else {
            (values as Sequence<Value>).indexOfBestNotNullBy {
                this.isBetterThan(it, valueType)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun aggregateSingleSequence(values: Sequence<Value?>, valueType: ValueType): Return {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return if (valueType.isMarkedNullable) {
            values.bestNotNullByOrNull {
                this.isBetterThan(it, valueType)
            }
        } else {
            (values as Sequence<Value>).bestByOrNull {
                this.isBetterThan(it, valueType)
            }
        } as Return
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
