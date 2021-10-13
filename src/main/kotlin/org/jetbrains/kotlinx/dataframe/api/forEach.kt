package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.RowSelector
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.getColumnsWithPaths

public fun <T, G> GroupedDataFrame<T, G>.forEach(body: (GroupedDataFrame.Entry<T, G>) -> Unit): Unit = forEach { key, group ->
    body(
        GroupedDataFrame.Entry(key, group)
    )
}
public fun <T, G> GroupedDataFrame<T, G>.forEach(body: (key: DataRow<T>, group: DataFrame<G>?) -> Unit): Unit =
    keys.forEachIndexed { index, row ->
        val group = groups[index]
        body(row, group)
    }

public fun <T> DataFrame<T>.forEach(action: RowSelector<T, Unit>): Unit = rows().forEach { action(it, it) }
public fun <T> DataColumn<T>.forEach(action: (T) -> Unit): Unit = values.forEach(action)

public fun <T> DataFrame<T>.forEachIndexed(action: (Int, DataRow<T>) -> Unit): Unit = rows().forEachIndexed(action)
public fun <T> DataColumn<T>.forEachIndexed(action: (Int, T) -> Unit): Unit = values.forEachIndexed(action)

public fun <T, C> DataFrame<T>.forEachIn(selector: ColumnsSelector<T, C>, action: (DataRow<T>, DataColumn<C>) -> Unit): Unit =
    getColumnsWithPaths(selector).let { cols ->
        rows().forEach { row ->
            cols.forEach { col ->
                action(row, col.data)
            }
        }
    }
