package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows
import org.jetbrains.kotlinx.dataframe.indices

// region DataFrame

/**
 * Returns the indices of all rows in this [<code>DataFrame</code>][DataFrame],
 * from `0` to the number of rows minus one,
 * or an empty range if this [<code>DataFrame</code>][DataFrame] has no rows.
 *
 *
 *
 * For more information: [See `indices` on the documentation website.](https://kotlin.github.io/dataframe/indexing.html#row-indices)
 *
 * See also:
 *  - [<code>rowsCount</code>][org.jetbrains.kotlinx.dataframe.DataFrame.rowsCount] — returns the number of rows in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *  - [<code>getRows</code>][org.jetbrains.kotlinx.dataframe.DataFrame.getRows] — returns the rows at the given indices, as a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *  - [<code>filter</code>][org.jetbrains.kotlinx.dataframe.DataFrame.filter] — returns the rows that satisfy a condition, instead of their indices.
 *
 * @return An [<code>IntRange</code>][IntRange] with the index of every row in this [<code>DataFrame</code>][DataFrame].
 */
public fun DataFrame<*>.indices(): IntRange = 0 until rowsCount()

/**
 * Returns the indices of the rows in this [<code>DataFrame</code>][DataFrame] that satisfy the given [<code>filter</code>][filter].
 *
 * They are listed in the same order as the rows in this [<code>DataFrame</code>][DataFrame].
 *
 *
 *
 * The [<code>filter</code>][filter] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * as both `this` and `it` and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 *
 *
 * For more information: [See `indices` on the documentation website.](https://kotlin.github.io/dataframe/indexing.html#row-indices)
 *
 * See also:
 *  - [<code>rowsCount</code>][org.jetbrains.kotlinx.dataframe.DataFrame.rowsCount] — returns the number of rows in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *  - [<code>getRows</code>][org.jetbrains.kotlinx.dataframe.DataFrame.getRows] — returns the rows at the given indices, as a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *  - [<code>filter</code>][org.jetbrains.kotlinx.dataframe.DataFrame.filter] — returns the rows that satisfy a condition, instead of their indices.
 *
 * ### Example
 * ```kotlin
 * // The indices of the rows where the "city" column is "Moscow"
 * df.indices { city == "Moscow" }
 * ```
 *
 * @param [filter] A [<code>RowFilter</code>][RowFilter] that returns `true` for the rows whose indices should be returned.
 * @return A [<code>List</code>][List] with the index of every row that satisfies the [<code>filter</code>][filter],
 * empty if no row satisfies it.
 */
public inline fun <T> DataFrame<T>.indices(filter: RowFilter<T>): List<Int> =
    indices().filter {
        val row = get(it)
        filter(row, row)
    }

// endregion
