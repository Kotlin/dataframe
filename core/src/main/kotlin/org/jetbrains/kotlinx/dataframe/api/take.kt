package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.nrow

// region DataColumn

public fun <T> DataColumn<T>.take(n: Int): DataColumn<T> = when {
    n == 0 -> get(emptyList())
    n >= size -> this
    else -> get(0 until n)
}

public fun <T> DataColumn<T>.takeLast(n: Int): DataColumn<T> = drop(size - n)

// endregion

// region DataFrame

/**
 * Returns a DataFrame containing first [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.take(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(0 until n.coerceAtMost(nrow))
}

/**
 * Returns a DataFrame containing last [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.takeLast(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return drop((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a DataFrame containing first rows that satisfy the given [predicate].
 */
public fun <T> DataFrame<T>.takeWhile(predicate: RowFilter<T>): DataFrame<T> = firstOrNull { !predicate(it, it) }?.let { take(it.index) } ?: this

// endregion
