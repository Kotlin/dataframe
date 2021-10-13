package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.PivotAggregateBody
import org.jetbrains.kotlinx.dataframe.api.GroupedPivot
import org.jetbrains.kotlinx.dataframe.api.aggregatePivot
import org.jetbrains.kotlinx.dataframe.columnNames
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateBodyInternal
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.typed

internal data class GroupedPivotImpl<T>(
    internal val df: GroupedDataFrame<*, T>,
    internal val columns: ColumnsSelector<T, *>,
    internal val groupValues: Boolean = false,
    internal val default: Any? = null,
    internal val groupPath: ColumnPath = emptyPath()
) : GroupedPivot<T>, AggregatableInternal<T> {
    override fun <R> aggregate(separate: Boolean, body: PivotAggregateBody<T, R>): DataFrame<T> {
        return df.aggregate {
            aggregatePivot(this as GroupByReceiverImpl<T>, columns, separate, groupPath, default, body)
        }.typed()
    }

    override fun separateAggregatedValues(flag: Boolean) = if (flag == groupValues) this else copy(groupValues = flag)

    override fun withGrouping(groupPath: ColumnPath) = copy(groupPath = groupPath)

    override fun default(value: Any?) = copy(default = value)

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> = { all().except(columns.toColumns() and df.keys.columnNames().toColumns()) }

    override fun <R> aggregateInternal(body: AggregateBodyInternal<T, R>) = aggregate(groupValues, body as PivotAggregateBody<T, R>)
}
