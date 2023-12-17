package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asIterable
import org.jetbrains.kotlinx.dataframe.api.asSequence
import kotlin.reflect.KType

internal abstract class AggregatorBase<C, R>(
    override val name: String,
    protected val aggregator: (Iterable<C>, KType) -> R?
) : Aggregator<C, R> {

    override fun aggregate(column: DataColumn<C?>): R? = if (column.hasNulls()) {
        aggregate(column.asSequence().filterNotNull().asIterable(), column.type())
    } else aggregate(column.asIterable() as Iterable<C>, column.type())

    override fun aggregate(values: Iterable<C>, type: KType): R? = aggregator(values, type)
}
