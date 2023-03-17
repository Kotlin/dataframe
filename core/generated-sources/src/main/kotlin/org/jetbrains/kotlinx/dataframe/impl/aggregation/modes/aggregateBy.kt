package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.namedValues

internal fun <T> Grouped<T>.aggregateBy(
    body: DataFrameExpression<T, DataRow<T>?>
): DataFrame<T> {
    require(this is GroupBy<*, T>)
    val keyColumns = keys.columnNames().toSet()
    return aggregateInternal {
        val row = body(df, df)
        row?.namedValues()?.forEach {
            if (!keyColumns.contains(it.name)) yield(it)
        }
    }.cast()
}
