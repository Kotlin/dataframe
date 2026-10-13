package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls

// region DataFrame

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the last [<code>numRows</code>][numRows] rows, five by default.
 *
 * If [<code>numRows</code>][numRows] is greater than or equal to the number of rows, the whole [<code>DataFrame</code>][DataFrame] is returned
 * as it is, column types included.
 * Otherwise, each column keeps its runtime type except for nullability: a nullable column
 * whose last [<code>numRows</code>][numRows] values contain no `null` becomes non-nullable in the result.
 *
 * When [<code>numRows</code>][numRows] is `0`, the result has no rows but keeps all columns,
 * including the nested columns of column groups and frame columns.
 *
 * This is equivalent to calling [<code>takeLast</code>][DataFrame.takeLast] with the same [<code>numRows</code>][numRows].
 *
 * For more information: [See `tail` on the documentation website.](https://kotlin.github.io/dataframe/tail.html)
 *
 * See also:
 * - [<code>head</code>][DataFrame.head] — returns the first [<code>numRows</code>][numRows] rows instead.
 * - [<code>takeLast</code>][DataFrame.takeLast] — takes the last `n` rows, with one row by default.
 * - [<code>dropLast</code>][DataFrame.dropLast] — drops the last `n` rows and keeps the rest.
 *
 * ### Example
 * ```kotlin
 * df.tail(2)
 * ```
 *
 * @param [numRows] The number of rows to take from the end. Must not be negative. Defaults to `5`.
 * @return A [<code>DataFrame</code>][DataFrame] containing the last [<code>numRows</code>][numRows] rows,
 * or the whole [<code>DataFrame</code>][DataFrame] if [<code>numRows</code>][numRows] is greater than or equal to the number of rows.
 * @throws [IllegalArgumentException] if [<code>numRows</code>][numRows] is negative.
 */
public fun <T> DataFrame<T>.tail(numRows: Int = 5): DataFrame<T> = takeLast(numRows)

// endregion
