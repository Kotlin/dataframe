package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region DataColumn

/**
 * Returns `true` if at least one element in this [<code>DataColumn</code>][DataColumn] satisfies the given [<code>predicate</code>][predicate].
 *
 * This is a convenience alias that delegates to [<code>Iterable.any</code>][Iterable.any] on the column's [<code>values</code>][values].
 *
 * For more information: [See `any` on the documentation website.](https://kotlin.github.io/dataframe/any.html)
 *
 * @param predicate A lambda function that takes a value from the column
 * and returns `true` if it matches the condition.
 * @return `true` if at least one element matches the [<code>predicate</code>][predicate], `false` otherwise.
 * @see [DataColumn.all]
 * @see [DataColumn.filter]
 * @see [DataColumn.count]
 */
public fun <T> DataColumn<T>.any(predicate: Predicate<T>): Boolean = values.any(predicate)

// endregion

// region DataFrame

/**
 * Returns `true` if at least one row in this [<code>DataFrame</code>][DataFrame] satisfies the given [<code>predicate</code>][predicate].
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 * [See `any` on the documentation website.](https://kotlin.github.io/dataframe/any.html)
 *
 * ### Example
 * ```kotlin
 * // Check if there is at least one row where "age" is greater than 18
 * val hasAdults = df.any { age > 18 }
 * ```
 *
 * @param predicate A [<code>RowFilter</code>][RowFilter] lambda that takes a [<code>DataRow</code>][DataRow] (as both `this` and `it`)
 * and returns `true` if the row should be considered a match.
 * @return `true` if at least one row satisfies the [<code>predicate</code>][predicate], `false` otherwise.
 * @see [DataFrame.all]
 */
public inline fun <T> DataFrame<T>.any(predicate: RowFilter<T>): Boolean = rows().any { predicate(it, it) }

// endregion
