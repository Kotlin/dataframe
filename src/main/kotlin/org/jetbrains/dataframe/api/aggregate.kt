package org.jetbrains.dataframe

import org.jetbrains.dataframe.aggregation.GroupByAggregateBody
import org.jetbrains.dataframe.columns.name
import org.jetbrains.dataframe.columns.values
import org.jetbrains.dataframe.impl.aggregation.GroupByReceiverImpl

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
            if (result != Unit && result !is NamedValue && result !is AggregatedPivot<*>) builder.yield(NamedValue.create(pathOf(defaultAggregateName), result, null, null, true))
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
