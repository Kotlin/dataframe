package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.PivotColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.firstOrNull
import org.jetbrains.kotlinx.dataframe.impl.GroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.api.aggregatePivot
import org.jetbrains.kotlinx.dataframe.impl.api.getPivotColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet

internal data class PivotGroupByImpl<T>(
    val df: GroupBy<*, T>,
    val columns: PivotColumnsSelector<T, *>,
    val inward: Boolean?,
    val default: Any? = null
) : PivotGroupBy<T>, AggregatableInternal<T> {
    override fun <R> aggregate(separate: Boolean, body: AggregateBody<T, R>): DataFrame<T> {
        return df.aggregate {
            aggregatePivot(this as GroupByReceiverImpl<T>, columns, separate, inward, default, body)
        }.cast()
    }

    override fun default(value: Any?) = copy(default = value)

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> = df.groups.firstOrNull()?.getPivotColumnPaths(columns).orEmpty().let { pivotPaths -> { all().except(pivotPaths.toColumnSet() and (df as GroupByImpl).keyColumnsInGroups.toColumnSet()) } }
}
