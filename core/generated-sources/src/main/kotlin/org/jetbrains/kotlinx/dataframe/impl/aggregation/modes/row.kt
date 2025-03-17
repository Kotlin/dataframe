package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.api.getColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator

/**
 * Generic function to apply an [Aggregator] ([this]) to aggregate values of a row.
 *
 * [Aggregator.aggregateCalculatingType] is used to deal with mixed types.
 *
 * @param row a row to aggregate
 * @param columns selector of which columns inside the [row] to aggregate
 */
@PublishedApi
internal fun <V, R> Aggregator<V, R>.aggregateOfRow(row: AnyRow, columns: ColumnsSelector<*, V?>): R {
    val filteredColumns = row.df().getColumns(columns)
    return aggregateCalculatingType(
        values = filteredColumns.mapNotNull { row[it] },
        valueTypes = filteredColumns.map { it.type() }.toSet(),
    )
}
