package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.values

// region DataColumn

public inline fun <T> DataColumn<T>.forEach(action: (T) -> Unit): Unit = values().forEach(action)

public inline fun <T> DataColumn<T>.forEachIndexed(action: (Int, T) -> Unit): Unit = values().forEachIndexed(action)

// endregion

// region DataFrame

public inline fun <T> DataFrame<T>.forEach(action: RowExpression<T, Unit>): Unit = rows().forEach { action(it, it) }

// endregion

// region GroupBy

public inline fun <T, G> GroupBy<T, G>.forEach(body: (GroupBy.Entry<T, G>) -> Unit): Unit =
    keys.forEach { key ->
        val group = groups[key.index()]
        body(GroupBy.Entry(key, group))
    }

// endregion
