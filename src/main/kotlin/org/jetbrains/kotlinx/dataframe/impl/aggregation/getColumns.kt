package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.api.NamedValue
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.isComparable
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType

internal inline fun <T> Aggregatable<T>.remainingColumns(crossinline predicate: (AnyCol) -> Boolean): ColumnsSelector<T, Any?> =
    remainingColumnsSelector().filter { predicate(it.data) }

internal fun <T> Aggregatable<T>.comparableColumns() = remainingColumns { it.isComparable() } as ColumnsSelector<T, Comparable<Any?>>

internal fun <T> Aggregatable<T>.numberColumns() = remainingColumns { it.isNumber() } as ColumnsSelector<T, Number?>

internal fun NamedValue.toColumnWithPath() = path to guessColumnType(
    path.last(),
    listOf(value),
    type,
    guessType,
    default
)
