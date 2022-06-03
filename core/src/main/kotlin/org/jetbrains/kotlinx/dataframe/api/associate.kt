package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression

// region DataFrame

public fun <T, V> DataFrame<T>.associateBy(transform: RowExpression<T, V>): Map<V, DataRow<T>> =
    rows().associateBy { transform(it, it) }

public fun <T, K, V> DataFrame<T>.associate(transform: RowExpression<T, Pair<K, V>>): Map<K, V> =
    rows().associate { transform(it, it) }

// endregion
