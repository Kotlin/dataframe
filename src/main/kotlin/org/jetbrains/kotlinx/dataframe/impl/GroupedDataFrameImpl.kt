package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.dataframe.*
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.GroupKey
import org.jetbrains.kotlinx.dataframe.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.GroupedRowFilter
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.GroupByAggregateBody
import org.jetbrains.kotlinx.dataframe.api.AggregatedPivot
import org.jetbrains.kotlinx.dataframe.api.NamedValue
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.doRemove
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.union
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.asGroupedDataFrame
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.name
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.frameColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.AggregatableInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupByReceiverImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.AggregateBodyInternal
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.groupBy.GroupedDataRowImpl
import org.jetbrains.kotlinx.dataframe.pathOf
import org.jetbrains.kotlinx.dataframe.typed
import org.jetbrains.kotlinx.dataframe.values

internal class GroupedDataFrameImpl<T, G>(
    val df: DataFrame<T>,
    override val groups: FrameColumn<G>,
    private val keyColumnsInGroups: ColumnsSelector<G, *>
) :
    GroupedDataFrame<T, G>,
    AggregatableInternal<G> {

    override val keys by lazy { df - groups }

    override operator fun get(key: GroupKey): DataFrame<T> {
        require(key.size < df.ncol()) { "Invalid size of the key" }

        val keySize = key.size
        val filtered = df.filter { it.values.subList(0, keySize) == key }
        return filtered.frameColumn(groups.name()).values.union().typed<T>()
    }

    override fun <R> mapGroups(transform: Selector<DataFrame<G>?, DataFrame<R>?>) =
        df.update(groups) { transform(it, it) }.asGroupedDataFrame { frameColumn<R>(groups.name()) }

    override fun toDataFrame(groupedColumnName: String?) = if (groupedColumnName == null || groupedColumnName == groups.name()) df else df.rename(groups).into(groupedColumnName)

    override fun toString() = df.toString()

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> = { all().except(keyColumnsInGroups.toColumns()) }

    override fun <R> aggregate(body: GroupByAggregateBody<G, R>) = aggregateGroupBy(toDataFrame(), { groups }, removeColumns = true, body).typed<G>()

    override fun <R> aggregateInternal(body: AggregateBodyInternal<G, R>) = aggregate(body as GroupByAggregateBody<G, R>)

    override fun filter(predicate: GroupedRowFilter<T, G>): GroupedDataFrame<T, G> {
        val indices = (0 until df.nrow()).filter {
            val row = GroupedDataRowImpl(df.get(it), groups)
            predicate(row, row)
        }
        return df[indices].asGroupedDataFrame(groups)
    }
}

internal fun <T, G, R> aggregateGroupBy(
    df: DataFrame<T>,
    selector: ColumnSelector<T, DataFrame<G>?>,
    removeColumns: Boolean,
    body: GroupByAggregateBody<G, R>
): DataFrame<T> {
    val defaultAggregateName = "aggregated"

    val column = df.column(selector)

    val (df2, removedNodes) = df.doRemove(selector)

    val groupedFrame = column.values.map {
        if (it == null) null
        else {
            val builder = GroupByReceiverImpl(it)
            val result = body(builder, builder)
            if (result != Unit && result !is NamedValue && result !is AggregatedPivot<*>) builder.yield(
                NamedValue.create(
                    pathOf(defaultAggregateName), result, null, null, true
                )
            )
            builder.compute()
        }
    }.union()

    val removedNode = removedNodes.single()
    val insertPath = removedNode.pathFromRoot().dropLast(1)

    if (!removeColumns) removedNode.data.wasRemoved = false

    val columnsToInsert = groupedFrame.columns().map {
        ColumnToInsert(insertPath + it.name, it, removedNode)
    }
    val src = if (removeColumns) df2 else df
    return src.insert(columnsToInsert)
}
