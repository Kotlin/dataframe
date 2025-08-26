package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.values

// region DataColumn

/**
 * Returns `true` if at least one element in this [DataColumn] satisfies the given [predicate].
 *
 * This is a convenience alias that delegates to [Iterable.any] on the column's [values].
 *
 * @param predicate A lambda function that takes a value from the column
 * and returns `true` if it matches the condition.
 * @return `true` if at least one element matches the [predicate], `false` otherwise.
 * @see [DataColumn.all]
 * @see [DataColumn.filter]
 * @see [DataColumn.count]
 */
public fun <T> DataColumn<T>.any(predicate: Predicate<T>): Boolean = values.any(predicate)

// endregion

// region DataFrame

/**
 * Returns `true` if at least one row in this [DataFrame] satisfies the given [predicate].
 *
 * The [predicate] is a [RowFilter][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [extension properties][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs] for convenient and type-safe access.
 *
 * ### Example
 * ```kotlin
 * // Check if there is at least one row where "age" is greater than 18
 * val hasAdults = df.any { age > 18 }
 * ```
 *
 * @param predicate A [RowFilter] lambda that takes a [DataRow] (as both `this` and `it`)
 * and returns `true` if the row should be considered a match.
 * @return `true` if at least one row satisfies the [predicate], `false` otherwise.
 */
public inline fun <T> DataFrame<T>.any(predicate: RowFilter<T>): Boolean = rows().any { predicate(it, it) }

// endregion
