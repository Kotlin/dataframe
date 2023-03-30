package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.aggregation.NamedValue
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.GroupedRowFilter
import org.jetbrains.kotlinx.dataframe.api.asGroupBy
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.AggregatableInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupByReceiverImpl
import org.jetbrains.kotlinx.dataframe.impl.api.AggregatedPivot
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert
import org.jetbrains.kotlinx.dataframe.impl.api.GroupedDataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.values

internal class GroupByImpl<T, G>(
    val df: DataFrame<T>,
    override val groups: FrameColumn<G>,
    internal val keyColumnsInGroups: ColumnsSelector<G, *>
) :
    GroupBy<T, G>,
    AggregatableInternal<G> {

    override val keys by lazy { df - groups }

    override fun <R> updateGroups(transform: Selector<DataFrame<G>, DataFrame<R>>) =
        df.convert(groups) { transform(it, it) }.asGroupBy(groups.name()) as GroupBy<T, R>

    override fun toDataFrame(groupedColumnName: String?) = if (groupedColumnName == null || groupedColumnName == groups.name()) df else df.rename(groups).into(groupedColumnName)

    override fun toString() = df.toString()

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> =
        keyColumnsInGroups.toColumnSet().let { groupCols -> { all().except(groupCols) } }

    override fun <R> aggregate(body: AggregateGroupedBody<G, R>) = aggregateGroupBy(toDataFrame(), { groups }, removeColumns = true, body).cast<G>()

    override fun filter(predicate: GroupedRowFilter<T, G>): GroupBy<T, G> {
        val indices = (0 until df.nrow).filter {
            val row = GroupedDataRowImpl(df.get(it), groups)
            predicate(row, row)
        }
        return df[indices].asGroupBy(groups)
    }
}

internal fun <T, G, R> aggregateGroupBy(
    df: DataFrame<T>,
    selector: ColumnSelector<T, DataFrame<G>?>,
    removeColumns: Boolean,
    body: AggregateGroupedBody<G, R>
): DataFrame<T> {
    val defaultAggregateName = "aggregated"

    val column = df.getColumn(selector)

    val removed = df.removeImpl(columns = selector)

    val hasKeyColumns = removed.df.ncol > 0

    val groupedFrame = column.values.map {
        if (it == null) null
        else {
            val builder = GroupByReceiverImpl(it, hasKeyColumns)
            val result = body(builder, builder)
            if (result != Unit && result !is NamedValue && result !is AggregatedPivot<*>) builder.yield(
                NamedValue.create(
                    pathOf(defaultAggregateName), result, null, null, true
                )
            )
            builder.compute()
        }
    }.concat()

    val removedNode = removed.removedColumns.single()
    val insertPath = removedNode.pathFromRoot().dropLast(1)

    if (!removeColumns) removedNode.data.wasRemoved = false

    val columnsToInsert = groupedFrame.getColumnsWithPaths { allDfs() }.map {
        ColumnToInsert(insertPath + it.path, it, removedNode)
    }
    val src = if (removeColumns) removed.df else df
    return src.insertImpl(columnsToInsert)
}
