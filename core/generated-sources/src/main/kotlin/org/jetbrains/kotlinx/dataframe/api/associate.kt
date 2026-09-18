package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region DataFrame

/**
 * Builds a [<code>Map</code>][Map] where each key is produced by applying [<code>transform</code>][transform] to a row,
 * and the value is the corresponding [<code>DataRow</code>][DataRow].
 *
 * The [<code>transform</code>][transform] is a [<code>RowExpression</code>][RowExpression] — a lambda that receives each [<code>DataRow</code>][DataRow]
 * both as `this` and `it` and is expected to return a key, allowing you to compute keys directly from row values.
 * You can also use [<code>extension properties</code>][AccessApis.ExtensionPropertiesApi] for concise and type-safe access.
 *
 * If multiple rows produce the same key, the last row for that key is stored,
 * consistent with Kotlin's [<code>kotlin.collections.associateBy</code>][kotlin.collections.associateBy] behavior.
 *
 * For more information: [See `associateBy` on the documentation website.](https://kotlin.github.io/dataframe/associateby.html)
 *
 * See also:
 * - [<code>toMap</code>][toMap] — converts a [<code>DataFrame</code>][DataFrame] into a [<code>Map</code>][Map] by using column names as keys
 *   and their values as the corresponding map values.
 *
 * ### Example
 * ```kotlin
 * // Associate each row by the "id" column
 * val map = df.associateBy { id }
 * ```
 *
 * @param transform A [<code>RowExpression</code>][RowExpression] that returns a key for each row.
 * @return A [<code>Map</code>][Map] of keys to corresponding rows.
 */
public inline fun <T, V> DataFrame<T>.associateBy(transform: RowExpression<T, V>): Map<V, DataRow<T>> =
    rows().associateBy { transform(it, it) }

/**
 * Builds a [<code>Map</code>][Map] from key-value [<code>Pair</code>][Pair]s produced by applying [<code>transform</code>][transform] to each row.
 *
 * The [<code>transform</code>][transform] is a [<code>RowExpression</code>][RowExpression] — a lambda that receives each [<code>DataRow</code>][DataRow]
 * both as `this` and `it` and is expected to return a pair, allowing you to generate [<code>Pair</code>][Pair]s of keys and values from row contents.
 * You can also use [<code>extension properties</code>][AccessApis.ExtensionPropertiesApi] for concise and type-safe access.
 *
 * If multiple rows produce the same key, the last value for that key is stored,
 * consistent with Kotlin's [<code>kotlin.collections.associate</code>][kotlin.collections.associate] behavior.
 *
 * For more information: [See `associate` on the documentation website.](https://kotlin.github.io/dataframe/associate.html)
 *
 * See also:
 * - [<code>toMap</code>][toMap] — converts a [<code>DataFrame</code>][DataFrame] into a [<code>Map</code>][Map] by using column names as keys
 *   and their values as the corresponding map values.
 *
 * ### Example
 * ```kotlin
 * // Associate rows into a map where key = id, value = name
 * val map = df.associate { id to name }
 * ```
 *
 * @param transform A [<code>RowExpression</code>][RowExpression] that returns a [<code>Pair</code>][Pair] of key and value for each row.
 * @return A [<code>Map</code>][Map] of keys to values.
 */
public inline fun <T, K, V> DataFrame<T>.associate(transform: RowExpression<T, Pair<K, V>>): Map<K, V> =
    rows().associate { transform(it, it) }

// endregion
