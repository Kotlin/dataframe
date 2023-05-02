package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.values

// region DataColumn

public fun <T> DataColumn<T>.any(predicate: Predicate<T>): Boolean = values.any(predicate)

// endregion

// region DataFrame

public fun <T> DataFrame<T>.any(predicate: RowFilter<T>): Boolean = rows().any { predicate(it, it) }

// endregion
