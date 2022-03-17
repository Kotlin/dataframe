package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.values

// region DataColumn

public fun <T> DataColumn<T>.forEach(action: (T) -> Unit): Unit = values.forEach(action)

public fun <T> DataColumn<T>.forEachIndexed(action: (Int, T) -> Unit): Unit = values.forEachIndexed(action)

// endregion

// region DataFrame

public fun <T> DataFrame<T>.forEachRow(action: RowExpression<T, Unit>): Unit = rows().forEach { action(it, it) }

public fun <T> DataFrame<T>.forEachColumn(action: (AnyCol) -> Unit): Unit = columns().forEach(action)

public fun <T> DataFrame<T>.forEachColumnIndexed(action: (Int, AnyCol) -> Unit): Unit =
    columns().forEachIndexed(action)

// endregion

// region GroupBy

public fun <T, G> GroupBy<T, G>.forEach(body: (GroupBy.Entry<T, G>) -> Unit): Unit = forEach { key, group ->
    body(
        GroupBy.Entry(key, group)
    )
}
public fun <T, G> GroupBy<T, G>.forEach(body: (key: DataRow<T>, group: DataFrame<G>?) -> Unit): Unit =
    keys.forEachRow { row ->
        val group = groups[row.index()]
        body(row, group)
    }

// endregion
