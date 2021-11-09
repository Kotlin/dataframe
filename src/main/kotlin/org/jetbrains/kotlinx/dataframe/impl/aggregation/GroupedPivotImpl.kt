package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.api.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.api.GroupedPivot
import org.jetbrains.kotlinx.dataframe.api.PivotColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.firstOrNull
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateBodyInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.public
import org.jetbrains.kotlinx.dataframe.impl.api.aggregatePivot
import org.jetbrains.kotlinx.dataframe.impl.api.getPivotColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns

internal data class GroupedPivotImpl<T>(
    val df: GroupedDataFrame<*, T>,
    val columns: PivotColumnsSelector<T, *>,
    val inward: Boolean?,
    val separateStatistics: Boolean = false,
    val default: Any? = null
) : GroupedPivot<T>, AggregatableInternal<T> {
    override fun <R> aggregate(separate: Boolean, body: AggregateBody<T, R>): DataFrame<T> {
        return df.aggregate {
            aggregatePivot(this as GroupByReceiverImpl<T>, columns, separate, inward, default, body)
        }.cast()
    }

    override fun separateStatistics(flag: Boolean) = if (flag == separateStatistics) this else copy(separateStatistics = flag)

    override fun default(value: Any?) = copy(default = value)

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> = df.groups.firstOrNull()?.getPivotColumnPaths(columns).orEmpty().let { pivotPaths -> { all().except(pivotPaths.toColumnSet() and df.keys.columnNames().toColumns()) } }

    override fun <R> aggregateInternal(body: AggregateBodyInternal<T, R>) = aggregate(separateStatistics, body.public())
}
