package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.aggregation.NamedValue
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.GroupedRowFilter
import org.jetbrains.kotlinx.dataframe.api.asFrameColumn
import org.jetbrains.kotlinx.dataframe.api.asGroupBy
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.AggregatableInternal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.GroupByReceiverImpl
import org.jetbrains.kotlinx.dataframe.impl.api.AggregatedPivot
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert
import org.jetbrains.kotlinx.dataframe.impl.api.GroupedDataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.schema.createEmptyDataFrame
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.values

/**
 * @property df DataFrame containing [groups] column and key columns. Represents GroupBy.
 */
internal class GroupByImpl<T, G>(
    val df: DataFrame<T>,
    override val groups: FrameColumn<G>,
    internal val keyColumnsInGroups: ColumnsSelector<G, *>,
) : GroupBy<T, G>,
    AggregatableInternal<G> {

    override val keys by lazy { df.remove(groups) }

    override fun <R> updateGroups(transform: Selector<DataFrame<G>, DataFrame<R>>) =
        df.convert(groups) { transform(it, it) }.asGroupBy(groups.name()) as GroupBy<T, R>

    override fun toString() = df.toString()

    override fun remainingColumnsSelector(): ColumnsSelector<*, *> =
        keyColumnsInGroups.toColumnSet().let { groupCols -> { all().except(groupCols) } }

    override fun filter(predicate: GroupedRowFilter<T, G>): GroupBy<T, G> {
        val indices = (0 until df.nrow).filter {
            val row = GroupedDataRowImpl(df.get(it), groups)
            predicate(row, row)
        }
        return df[indices].asGroupBy(groups)
    }

    override fun toDataFrame(groupedColumnName: String?): DataFrame<T> =
        if (groupedColumnName == null || groupedColumnName == groups.name()) {
            df
        } else {
            df.rename(groups).into(groupedColumnName)
        }
}

internal fun <T, G, R> aggregateGroupBy(
    df: DataFrame<T>,
    selector: ColumnSelector<T, DataFrame<G>?>,
    body: AggregateGroupedBody<G, R>,
): DataFrame<T> {
    val defaultAggregateName = "aggregated"
    val groupedDfIsEmpty = df.size().nrow == 0
    val column = df.getColumn(selector)
    val removed = df.removeImpl(columns = selector)
    val hasKeyColumns = removed.df.ncol > 0

    val groups =
        if (groupedDfIsEmpty) {
            // if the grouped dataframe is empty, make sure the provided AggregateGroupedBody is called at least once
            // to create aggregated columns. We empty them below.
            listOf(
                column.asFrameColumn().schema.value
                    .createEmptyDataFrame()
                    .cast(),
            )
        } else {
            column.values
        }

    val groupedFrame = groups.map {
        if (it == null) {
            null
        } else {
            val builder = GroupByReceiverImpl(it, hasKeyColumns)
            val result = body(builder, builder)
            if (result != Unit && result !is NamedValue && result !is AggregatedPivot<*>) {
                builder.yield(
                    NamedValue.create(
                        path = pathOf(defaultAggregateName),
                        value = result,
                        type = null,
                        defaultValue = null,
                        guessType = true,
                    ),
                )
            }
            builder.compute()
        }
    }.concat()
        .let {
            // empty the aggregated columns that were created by calling the provided AggregateGroupedBody once
            // if the grouped dataframe is empty
            if (groupedDfIsEmpty) it.take(0) else it
        }

    val removedNode = removed.removedColumns.single()
    val insertPath = removedNode.pathFromRoot().dropLast(1)

    val columnsToInsert = groupedFrame.getColumnsWithPaths {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }.map {
        ColumnToInsert(insertPath + it.path, it, removedNode)
    }

    return removed.df.insertImpl(columnsToInsert)
}
