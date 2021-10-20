package org.jetbrains.kotlinx.dataframe.impl.aggregation.modes

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.Grouped
import org.jetbrains.kotlinx.dataframe.api.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.api.typed
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregateInternal
import org.jetbrains.kotlinx.dataframe.impl.namedValues

internal fun <T> Grouped<T>.aggregateBy(
    body: DataFrameSelector<T, DataRow<T>?>
): DataFrame<T> {
    require(this is GroupedDataFrame<*, T>)
    val keyColumns = keys.columnNames().toSet()
    return aggregateInternal {
        val row = body(df, df)
        row?.namedValues()?.forEach {
            if (!keyColumns.contains(it.name)) yield(it)
        }
    }.typed()
}
