package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region DataFrame

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the first [<code>numRows</code>][numRows] rows, five by default.
 *
 * If [<code>numRows</code>][numRows] is greater than or equal to the number of rows, the whole [<code>DataFrame</code>][DataFrame] is returned
 * as it is, column types included.
 * Otherwise, each column keeps its runtime type except for nullability: a nullable column
 * whose first [<code>numRows</code>][numRows] values contain no `null` becomes non-nullable in the result.
 *
 * When [<code>numRows</code>][numRows] is `0`, the result has no rows but keeps all columns,
 * including the nested columns of column groups and frame columns.
 *
 * This is equivalent to calling [<code>take</code>][DataFrame.take] with the same [<code>numRows</code>][numRows].
 *
 * For more information: [See `head` on the documentation website.](https://kotlin.github.io/dataframe/head.html)
 *
 * See also:
 * - [<code>tail</code>][DataFrame.tail] — returns the last [<code>numRows</code>][numRows] rows instead.
 * - [<code>take</code>][DataFrame.take] — takes the first `n` rows, without a default.
 * - [<code>drop</code>][DataFrame.drop]`(n: Int)` — drops the first `n` rows and keeps the rest.
 *
 * ### Example
 * ```kotlin
 * df.head(3)
 * ```
 *
 * @param [numRows] The number of rows to take. Must not be negative. Defaults to `5`.
 * @return A [<code>DataFrame</code>][DataFrame] containing the first [<code>numRows</code>][numRows] rows,
 * or the whole [<code>DataFrame</code>][DataFrame] if [<code>numRows</code>][numRows] is greater than or equal to the number of rows.
 * @throws [IllegalArgumentException] if [<code>numRows</code>][numRows] is negative.
 */
public fun <T> DataFrame<T>.head(numRows: Int = 5): DataFrame<T> = take(numRows)

// endregion
