package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl
import org.jetbrains.kotlinx.dataframe.api.GroupedPivot
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateBodyInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.public
import org.jetbrains.kotlinx.dataframe.impl.api.AggregatedPivot
import org.jetbrains.kotlinx.dataframe.impl.api.aggregatePivot
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns

internal data class GroupedPivotInsideAggregateImpl<T>(
    val aggregator: AggregateGroupedDsl<T>,
    val columns: ColumnsSelector<T, *>,
    val inward: Boolean?,
    val groupValues: Boolean = false,
    val default: Any? = null
) : GroupedPivot<T>, AggregatableInternal<T> {

    override fun separateStatistics(flag: Boolean) = if (flag == groupValues) this else copy(groupValues = flag)

    override fun default(value: Any?) = copy(default = value)

    override fun <R> aggregate(separate: Boolean, body: AggregateBody<T, R>): DataFrame<T> {
        require(aggregator is GroupByReceiverImpl<T>)

        val childAggregator = aggregator.child()
        aggregatePivot(childAggregator, columns, separate, inward, default, body)
        return AggregatedPivot(aggregator.df, childAggregator)
    }

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> = { all().except(columns.toColumns()) }

    override fun <R> aggregateInternal(body: AggregateBodyInternal<T, R>) = aggregate(groupValues, body.public())
}
