package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

@ExcludeFromSources
internal interface AssociateDocs {

    /**
     * If several rows produce the same key, the map keeps the last {@get [ITEM]} for that key,
     * consistent with Kotlin's {@get [STDLIB]} behavior.
     *
     * The keys are in the same order as the rows. A key that occurs in several rows appears
     * at the position of its first row, with the {@get [ITEM]} of its last one.
     *
     * @comment The rule is the same for both functions, only the wording of the kept item and the
     *   matching stdlib function differ. Both statements are expected values in `AssociateTests`.
     */
    @ExcludeFromSources
    typealias DuplicateKeysAndOrderSnippet = Nothing

    /** {@comment What a duplicate key keeps — a value for `associate`, a row for `associateBy`.} */
    @ExcludeFromSources
    typealias ITEM = Nothing

    /** {@comment The stdlib function whose behavior this one matches.} */
    @ExcludeFromSources
    typealias STDLIB = Nothing
}

// region DataFrame

/**
 * Builds a [Map] where each key is produced by applying [transform] to a row,
 * and the value is the corresponding [DataRow].
 *
 * The [transform] is a [RowExpression] — a lambda that receives each [DataRow]
 * both as `this` and `it` and is expected to return a key, allowing you to compute keys directly from row values.
 * You can also use [extension properties][AccessApis.ExtensionPropertiesApi] for concise and type-safe access.
 *
 * @include [AssociateDocs.DuplicateKeysAndOrderSnippet]
 *
 * For more information: {@include [DocumentationUrls.AssociateBy]}
 *
 * See also:
 * - [associate] — builds a map from key-value [Pair]s produced by transforming each row,
 *   so the values are computed instead of being the rows themselves.
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
 * @throws IllegalArgumentException if [transform] reads a column this [DataFrame] does not have.
 * @set [AssociateDocs.ITEM] row
 * @set [AssociateDocs.STDLIB] [kotlin.collections.associateBy]
 */
public inline fun <T, V> DataFrame<T>.associateBy(transform: RowExpression<T, V>): Map<V, DataRow<T>> =
    rows().associateBy { transform(it, it) }

/**
 * Builds a [Map] from key-value [Pair]s produced by applying [transform] to each row.
 *
 * The [transform] is a [RowExpression] — a lambda that receives each [DataRow]
 * both as `this` and `it` and is expected to return a pair, allowing you to generate [Pair]s of keys and values from row contents.
 * You can also use [extension properties][AccessApis.ExtensionPropertiesApi] for concise and type-safe access.
 *
 * @include [AssociateDocs.DuplicateKeysAndOrderSnippet]
 *
 * For more information: {@include [DocumentationUrls.Associate]}
 *
 * See also:
 * - [associateBy] — builds a map with the rows themselves as values,
 *   so only a key is computed for each row.
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
 * @throws IllegalArgumentException if [transform] reads a column this [DataFrame] does not have.
 * @set [AssociateDocs.ITEM] value
 * @set [AssociateDocs.STDLIB] [kotlin.collections.associate]
 */
public inline fun <T, K, V> DataFrame<T>.associate(transform: RowExpression<T, Pair<K, V>>): Map<K, V> =
    rows().associate { transform(it, it) }

// endregion
