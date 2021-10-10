package org.jetbrains.dataframe.impl

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.aggregation.GroupByAggregateBody
import org.jetbrains.dataframe.columns.FrameColumn
import org.jetbrains.dataframe.columns.values
import org.jetbrains.dataframe.impl.aggregation.AggregatableInternal
import org.jetbrains.dataframe.impl.aggregation.receivers.AggregateBodyInternal
import org.jetbrains.dataframe.impl.columns.toColumns
import org.jetbrains.dataframe.impl.groupBy.GroupedDataRowImpl

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
        df.update(groups) { transform(it, it) }.asGrouped { frameColumn<R>(groups.name()) }

    override fun asDataFrame(groupedColumnName: String?) = if (groupedColumnName == null || groupedColumnName == groups.name()) df else df.rename(groups).into(groupedColumnName)

    override fun toString() = df.toString()

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> = { all().except(keyColumnsInGroups.toColumns()) }

    override fun <R> aggregate(body: GroupByAggregateBody<G, R>) = aggregateGroupBy(asDataFrame(), { groups }, removeColumns = true, body).typed<G>()

    override fun <R> aggregateInternal(body: AggregateBodyInternal<G, R>) = aggregate(body as GroupByAggregateBody<G, R>)

    override fun filter(predicate: GroupedRowFilter<T, G>): GroupedDataFrame<T, G> {
        val indices = (0 until df.nrow()).filter {
            val row = GroupedDataRowImpl(df.get(it), groups)
            predicate(row, row)
        }
        return df[indices].asGrouped(groups)
    }
}
