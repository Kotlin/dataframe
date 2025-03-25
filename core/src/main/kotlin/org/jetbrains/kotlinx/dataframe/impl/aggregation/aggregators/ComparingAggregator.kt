package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.impl.bestByOrNull
import org.jetbrains.kotlinx.dataframe.impl.bestNotNullByOrNull
import org.jetbrains.kotlinx.dataframe.impl.indexOfBestNotNullBy
import kotlin.reflect.KType

internal class SelectingAggregationHandler<Type>(
    val isBetterThan: IsBetterThanSelector<Type>,
    val getReturnTypeOrNull: CalculateReturnTypeOrNull,
) : AggregatorAggregationHandler<Type, Type?> {

    @Suppress("UNCHECKED_CAST")
    override fun indexOfAggregationResultSingleSequence(values: Sequence<Type?>, valueType: ValueType): Int {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return if (valueType.isMarkedNullable) {
            values.indexOfBestNotNullBy {
                this.isBetterThan(it, valueType)
            }
        } else {
            (values as Sequence<Type>).indexOfBestNotNullBy {
                this.isBetterThan(it, valueType)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun aggregateSingleSequence(values: Sequence<Type?>, valueType: ValueType): Type? {
        val (values, valueType) = aggregator!!.preprocessAggregation(values, valueType)
        return if (valueType.isMarkedNullable) {
            values.bestNotNullByOrNull {
                this.isBetterThan(it, valueType)
            }
        } else {
            (values as Sequence<Type>).bestByOrNull {
                this.isBetterThan(it, valueType)
            }
        }
    }

    override fun aggregateSingleColumn(column: DataColumn<Type?>): Type? =
        aggregateSingleSequence(
            values = column.asSequence(),
            valueType = column.type().toValueType(),
        )

    override fun calculateReturnTypeOrNull(type: KType, emptyInput: Boolean): KType? =
        getReturnTypeOrNull(type, emptyInput)

    override var aggregator: Aggregator<@UnsafeVariance Type, @UnsafeVariance Type?>? = null
}
