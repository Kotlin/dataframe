package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region DataFrame

/**
 * Returns a [DataFrame] containing the last [numRows] rows, five by default.
 *
 * If [numRows] is greater than or equal to the number of rows, the whole [DataFrame] is returned
 * as it is, column types included.
 * Otherwise, each column keeps its runtime type except for nullability: a nullable column
 * whose last [numRows] values contain no `null` becomes non-nullable in the result.
 *
 * When [numRows] is `0`, the result has no rows but keeps all columns,
 * including the nested columns of column groups and frame columns.
 *
 * This is equivalent to calling [takeLast][DataFrame.takeLast] with the same [numRows].
 *
 * For more information: {@include [DocumentationUrls.Tail]}
 *
 * See also:
 * - [head][DataFrame.head] — returns the first [numRows] rows instead.
 * - [takeLast][DataFrame.takeLast] — takes the last `n` rows, with one row by default.
 * - [dropLast][DataFrame.dropLast] — drops the last `n` rows and keeps the rest.
 *
 * ### Example
 * ```kotlin
 * df.tail(2)
 * ```
 *
 * @param [numRows] The number of rows to take from the end. Must not be negative. Defaults to `5`.
 * @return A [DataFrame] containing the last [numRows] rows,
 * or the whole [DataFrame] if [numRows] is greater than or equal to the number of rows.
 * @throws [IllegalArgumentException] if [numRows] is negative.
 */
public fun <T> DataFrame<T>.tail(numRows: Int = 5): DataFrame<T> = takeLast(numRows)

// endregion
