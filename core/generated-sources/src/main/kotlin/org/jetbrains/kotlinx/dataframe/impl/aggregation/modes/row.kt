package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.getColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregateCalculatingValueType

/**
 * Generic function to apply an [<code>Aggregator</code>][Aggregator] ([<code>this</code>][this]) to aggregate values of a row.
 *
 * [<code>Aggregator.aggregateCalculatingValueType</code>][Aggregator.aggregateCalculatingValueType] is used to deal with mixed types.
 *
 * @param row a row to aggregate
 * @param columns selector of which columns inside the [<code>row</code>][row] to aggregate
 */
@PublishedApi
internal fun <T, V : Any?, R : Any?> Aggregator<V & Any, R>.aggregateOfRow(
    row: DataRow<T>,
    columns: ColumnsSelector<T, V>,
): R {
    val filteredColumns = row.df().getColumns(columns).asSequence()
    return aggregateCalculatingValueType(
        values = filteredColumns.map { row[it] },
        valueTypes = filteredColumns.map { it.type() }.toSet(),
    )
}
