package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

// region DataFrame

/**
 * Builds a [<code>Map</code>][Map] where each key is produced by applying [<code>transform</code>][transform] to a row,
 * and the value is the corresponding [<code>DataRow</code>][DataRow].
 *
 * The [<code>transform</code>][transform] is a [<code>RowExpression</code>][RowExpression] — a lambda that receives each [<code>DataRow</code>][DataRow]
 * both as `this` and `it` and is expected to return a key, allowing you to compute keys directly from row values.
 * You can also use [<code>extension properties</code>][AccessApis.ExtensionPropertiesApi] for concise and type-safe access.
 *
 * If several rows produce the same key, the map keeps the last row for that key,
 * consistent with Kotlin's [<code>kotlin.collections.associateBy</code>][kotlin.collections.associateBy] behavior.
 *
 * The keys are in the same order as the rows. A key that occurs in several rows appears
 * at the position of its first row, with the row of its last one.
 *
 *
 * For more information: [See `associateBy` on the documentation website.](https://kotlin.github.io/dataframe/associateby.html)
 *
 * See also:
 * - [<code>associate</code>][associate] — builds a map from key-value [<code>Pair</code>][Pair]s produced by transforming each row,
 *   so the values are computed instead of being the rows themselves.
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
 * @throws IllegalArgumentException if [<code>transform</code>][transform] reads a column this [<code>DataFrame</code>][DataFrame] does not have.
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
 * If several rows produce the same key, the map keeps the last value for that key,
 * consistent with Kotlin's [<code>kotlin.collections.associate</code>][kotlin.collections.associate] behavior.
 *
 * The keys are in the same order as the rows. A key that occurs in several rows appears
 * at the position of its first row, with the value of its last one.
 *
 *
 * For more information: [See `associate` on the documentation website.](https://kotlin.github.io/dataframe/associate.html)
 *
 * See also:
 * - [<code>associateBy</code>][associateBy] — builds a map with the rows themselves as values,
 *   so only a key is computed for each row.
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
 * @throws IllegalArgumentException if [<code>transform</code>][transform] reads a column this [<code>DataFrame</code>][DataFrame] does not have.
 */
public inline fun <T, K, V> DataFrame<T>.associate(transform: RowExpression<T, Pair<K, V>>): Map<K, V> =
    rows().associate { transform(it, it) }

// endregion
