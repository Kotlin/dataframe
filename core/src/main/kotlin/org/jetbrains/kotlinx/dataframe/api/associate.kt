package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs

// region DataFrame

/**
 * Builds a [Map] where each key is produced by applying [transform] to a row,
 * and the value is the corresponding [DataRow].
 *
 * The [transform] is a [RowExpression] — a lambda that receives each [DataRow]
 * both as `this` and `it` and is expected to return a key, allowing you to compute keys directly from row values.
 * You can also use [extension properties][ExtensionPropertiesAPIDocs] for concise and type-safe access.
 *
 * If multiple rows produce the same key, the last row for that key is stored,
 * consistent with Kotlin's [kotlin.collections.associateBy] behavior.
 *
 * See also:
 * - [toMap] — converts a [DataFrame] into a [Map] by using column names as keys
 *   and their values as the corresponding map values.
 *
 * ### Example
 * ```kotlin
 * // Associate each row by the "id" column
 * val map = df.associateBy { id }
 * ```
 *
 * @param transform A [RowExpression] that returns a key for each row.
 * @return A [Map] of keys to corresponding rows.
 */
public inline fun <T, V> DataFrame<T>.associateBy(transform: RowExpression<T, V>): Map<V, DataRow<T>> =
    rows().associateBy { transform(it, it) }

/**
 * Builds a [Map] from key-value [Pair]s produced by applying [transform] to each row.
 *
 * The [transform] is a [RowExpression] — a lambda that receives each [DataRow]
 * both as `this` and `it` and is expected to return a pair, allowing you to generate [Pair]s of keys and values from row contents.
 * You can also use [extension properties][ExtensionPropertiesAPIDocs] for concise and type-safe access.
 *
 * If multiple rows produce the same key, the last value for that key is stored,
 * consistent with Kotlin's [kotlin.collections.associate] behavior.
 *
 * See also:
 * - [toMap] — converts a [DataFrame] into a [Map] by using column names as keys
 *   and their values as the corresponding map values.
 *
 * ### Example
 * ```kotlin
 * // Associate rows into a map where key = id, value = name
 * val map = df.associate { id to name }
 * ```
 *
 * @param transform A [RowExpression] that returns a [Pair] of key and value for each row.
 * @return A [Map] of keys to values.
 */
public inline fun <T, K, V> DataFrame<T>.associate(transform: RowExpression<T, Pair<K, V>>): Map<K, V> =
    rows().associate { transform(it, it) }

// endregion
