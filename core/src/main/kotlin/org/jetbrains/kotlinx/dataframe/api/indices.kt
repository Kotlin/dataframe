package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows
import org.jetbrains.kotlinx.dataframe.indices

// region DataFrame

/**
 * {@comment
 *    Website link and "see also" list shared by both `indices` overloads.
 *    Include it into KDoc with `@include [CommonIndicesDocs]`.
 * }
 *
 * For more information: {@include [DocumentationUrls.Indices]}
 *
 * See also:
 *  - [rowsCount][DataFrame.rowsCount] — returns the number of rows in this [DataFrame].
 *  - [getRows][DataFrame.getRows] — returns the rows at the given indices, as a new [DataFrame].
 *  - [filter][DataFrame.filter] — returns the rows that satisfy a condition, instead of their indices.
 */
@ExcludeFromSources
private typealias CommonIndicesDocs = Nothing

/**
 * Returns the indices of all rows in this [DataFrame],
 * from `0` to the number of rows minus one,
 * or an empty range if this [DataFrame] has no rows.
 *
 * {@include [CommonIndicesDocs]}
 *
 * @return An [IntRange] with the index of every row in this [DataFrame].
 */
public fun DataFrame<*>.indices(): IntRange = 0 until rowsCount()

/**
 * Returns the indices of the rows in this [DataFrame] that satisfy the given [filter].
 *
 * They are listed in the same order as the rows in this [DataFrame].
 *
 * {@include [SelectingRows.RowFilterSnippet] {@set [SelectingRows.FILTER_PARAM] [filter]}}
 *
 * {@include [CommonIndicesDocs]}
 *
 * ### Example
 * ```kotlin
 * // The indices of the rows where the "city" column is "Moscow"
 * df.indices { city == "Moscow" }
 * ```
 *
 * @param [filter] A [RowFilter] that returns `true` for the rows whose indices should be returned.
 * @return A [List] with the index of every row that satisfies the [filter],
 * empty if no row satisfies it.
 */
public inline fun <T> DataFrame<T>.indices(filter: RowFilter<T>): List<Int> =
    indices().filter {
        val row = get(it)
        filter(row, row)
    }

// endregion
