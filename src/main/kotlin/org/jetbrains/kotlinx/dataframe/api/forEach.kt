package org.jetbrains.kotlinx.dataframe.api

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

public fun <T> DataFrame<T>.forEach(action: RowExpression<T, Unit>): Unit = rows().forEach { action(it, it) }

// endregion

// region GroupBy

public fun <T, G> GroupBy<T, G>.forEach(body: (GroupBy.Entry<T, G>) -> Unit): Unit = forEach { key, group ->
    body(
        GroupBy.Entry(key, group)
    )
}
public fun <T, G> GroupBy<T, G>.forEach(body: (key: DataRow<T>, group: DataFrame<G>?) -> Unit): Unit =
    keys.forEach { row ->
        val group = groups[row.index()]
        body(row, group)
    }

// endregion
