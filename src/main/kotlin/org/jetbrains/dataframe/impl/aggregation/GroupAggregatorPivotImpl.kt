package org.jetbrains.dataframe.impl.aggregation

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.aggregation.GroupByReceiver
import org.jetbrains.dataframe.aggregation.PivotAggregateBody
import org.jetbrains.dataframe.impl.aggregation.receivers.AggregateBodyInternal
import org.jetbrains.dataframe.impl.columns.toColumns
import org.jetbrains.dataframe.impl.emptyPath

internal data class GroupAggregatorPivotImpl<T>(
    internal val aggregator: GroupByReceiver<T>,
    internal val columns: ColumnsSelector<T, *>,
    internal val groupValues: Boolean = false,
    internal val default: Any? = null,
    internal val groupPath: ColumnPath = emptyPath()
) : GroupedPivot<T>, AggregatableInternal<T> {

    override fun separateAggregatedValues(flag: Boolean) = if (flag == groupValues) this else copy(groupValues = flag)

    override fun default(value: Any?) = copy(default = value)

    override fun withGrouping(groupPath: ColumnPath) = copy(groupPath = groupPath)

    override fun <R> aggregate(separate: Boolean, body: PivotAggregateBody<T, R>): DataFrame<T> {
        require(aggregator is GroupByReceiverImpl<T>)

        val childAggregator = aggregator.child()
        aggregatePivot(childAggregator, columns, separate, groupPath, default, body)
        return AggregatedPivot(aggregator.df, childAggregator)
    }

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> = { all().except(columns.toColumns()) }

    override fun <R> aggregateInternal(body: AggregateBodyInternal<T, R>) = aggregate(groupValues, body as PivotAggregateBody<T, R>)
}
