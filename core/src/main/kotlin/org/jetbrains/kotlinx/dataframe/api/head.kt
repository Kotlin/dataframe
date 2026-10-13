package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region DataFrame

/**
 * Returns a [DataFrame] containing the first [numRows] rows, five by default.
 *
 * If [numRows] is greater than or equal to the number of rows, the whole [DataFrame] is returned
 * as it is, column types included.
 * Otherwise, each column keeps its runtime type except for nullability: a nullable column
 * whose first [numRows] values contain no `null` becomes non-nullable in the result.
 *
 * When [numRows] is `0`, the result has no rows but keeps all columns,
 * including the nested columns of column groups and frame columns.
 *
 * This is equivalent to calling [take][DataFrame.take] with the same [numRows].
 *
 * For more information: {@include [DocumentationUrls.Head]}
 *
 * See also:
 * - [tail][DataFrame.tail] — returns the last [numRows] rows instead.
 * - [take][DataFrame.take] — takes the first `n` rows, without a default.
 * - [drop][DataFrame.drop]`(n: Int)` — drops the first `n` rows and keeps the rest.
 *
 * ### Example
 * ```kotlin
 * df.head(3)
 * ```
 *
 * @param [numRows] The number of rows to take. Must not be negative. Defaults to `5`.
 * @return A [DataFrame] containing the first [numRows] rows,
 * or the whole [DataFrame] if [numRows] is greater than or equal to the number of rows.
 * @throws [IllegalArgumentException] if [numRows] is negative.
 */
public fun <T> DataFrame<T>.head(numRows: Int = 5): DataFrame<T> = take(numRows)

// endregion
