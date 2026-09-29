package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.duplicateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.duplicateRowsImpl

/**
 * Returns a [<code>FrameColumn</code>][FrameColumn] that holds this [<code>DataFrame</code>][DataFrame] [<code>n</code>][n] times.
 *
 * Each of the [<code>n</code>][n] cells of the column is this [<code>DataFrame</code>][DataFrame].
 * The column has an empty name.
 *
 * For more information: [See `duplicate` on a DataFrame on the documentation website.](https://kotlin.github.io/dataframe/duplicate.html#duplicate-on-a-dataframe)
 *
 * See also:
 * - [<code>duplicateRows</code>][DataFrame.duplicateRows] — repeats every row of this [<code>DataFrame</code>][DataFrame] [<code>n</code>][n] times.
 * - [<code>concat</code>][DataColumn.concat] — joins the copies into one [<code>DataFrame</code>][DataFrame]:
 *   `df.duplicate(2).concat()` has all rows of `df`, followed by all rows of `df` again.
 *
 * ### Example
 * In the example, `df` has the columns `name` and `age` and three rows:
 * `Alice, 15`, `Bob, 20` and `Charlie, 30`.
 *
 * ```kotlin
 * // A column with 3 cells, each cell holds `df`
 * df.duplicate(3)
 * ```
 *
 * @param [n] The number of copies in the result, the original included. Must be greater than 0.
 * @throws [IllegalArgumentException] if [n] is 0 or negative.
 * @return A [<code>FrameColumn</code>][FrameColumn] with an empty name and [<code>n</code>][n] cells, each holding this [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataFrame<T>.duplicate(n: Int): FrameColumn<T> = duplicateImpl(n)

/**
 * Returns a [<code>DataFrame</code>][DataFrame] where every row is repeated [<code>n</code>][n] times.
 *
 * The copies of a row come right after it, so the order of the rows stays the same:
 * with [<code>n</code>][n] = 2, rows `A, B` become `A, A, B, B`.
 * Values in column groups and frame columns are repeated together with their rows.
 * The column names and types stay the same.
 *
 * Don't confuse this with [<code>distinct</code>][DataFrame.distinct], which removes repeated rows.
 *
 * For more information: [See `duplicateRows` on the documentation website.](https://kotlin.github.io/dataframe/duplicate.html#duplicaterows)
 *
 * See also:
 * - [<code>duplicateRows</code>][DataFrame.duplicateRows]`(n) { filter: RowFilter<T> }` — repeats only the rows
 *   that match a condition.
 * - [<code>duplicate</code>][DataFrame.duplicate] — puts copies of the whole [<code>DataFrame</code>][DataFrame] into a [<code>FrameColumn</code>][FrameColumn].
 * - [<code>duplicate</code>][DataRow.duplicate] — makes a [<code>DataFrame</code>][DataFrame] out of copies of a single [<code>DataRow</code>][DataRow].
 *
 * ### Example
 * In the example, `df` has the columns `name` and `age` and three rows:
 * `Alice, 15`, `Bob, 20` and `Charlie, 30`.
 *
 * ```kotlin
 * // Alice, Alice, Alice, Bob, Bob, Bob, Charlie, Charlie, Charlie
 * df.duplicateRows(3)
 * ```
 *
 * @param [n] The number of copies in the result, the original included. Must be greater than 0.
 * @throws [IllegalArgumentException] if [n] is 0 or negative.
 * @return A [<code>DataFrame</code>][DataFrame] with every row repeated [<code>n</code>][n] times.
 */
public fun <T> DataFrame<T>.duplicateRows(n: Int): DataFrame<T> = duplicateRowsImpl(n)

/**
 * Returns a [<code>DataFrame</code>][DataFrame] where each row that matches the [<code>filter</code>][filter] is repeated [<code>n</code>][n] times.
 *
 * Rows that do not match the [<code>filter</code>][filter] appear once, as before.
 * The copies of a row come right after it, so the order of the rows stays the same.
 * Values in column groups and frame columns are repeated together with their rows.
 * The column names and types stay the same.
 *
 * The [<code>filter</code>][filter] is a [<code>RowFilter</code>][RowFilter] — a lambda that receives each [<code>DataRow</code>][DataRow] as both `this` and `it`
 * and returns `true` for the rows to repeat.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * For more information: [See `duplicateRows` on the documentation website.](https://kotlin.github.io/dataframe/duplicate.html#duplicaterows) [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 * See also:
 * - [<code>duplicateRows</code>][DataFrame.duplicateRows]`(n: Int)` — repeats every row.
 * - [<code>filter</code>][DataFrame.filter] — keeps only the rows that match a condition.
 *
 * ### Example
 * In the example, `df` has the columns `name` and `age` and three rows:
 * `Alice, 15`, `Bob, 20` and `Charlie, 30`.
 *
 * ```kotlin
 * // Alice, Bob, Bob, Bob, Charlie, Charlie, Charlie
 * df.duplicateRows(3) { age > 18 }
 * ```
 *
 * @param [filter] The condition that selects the rows to repeat.
 * @param [n] The number of copies in the result, the original included. Must be greater than 0.
 * @throws [IllegalArgumentException] if [n] is 0 or negative.
 * @return A [<code>DataFrame</code>][DataFrame] where the rows that match the [<code>filter</code>][filter] are repeated [<code>n</code>][n] times
 * and the other rows appear once.
 */
public inline fun <T> DataFrame<T>.duplicateRows(n: Int, filter: RowFilter<T>): DataFrame<T> =
    duplicateRowsImpl(n, rows().filter { filter(it, it) }.map { it.index() })

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with [<code>n</code>][n] rows, each of them a copy of this [<code>DataRow</code>][DataRow].
 *
 * The result has the same columns as the [<code>DataFrame</code>][DataFrame] this row comes from,
 * column groups and frame columns included.
 * The type of each column follows the value in this row:
 * a nullable column whose value in this row is not `null` becomes non-nullable in the result.
 *
 * For more information: [See `duplicate` on a DataRow on the documentation website.](https://kotlin.github.io/dataframe/duplicate.html#duplicate-on-a-datarow)
 *
 * See also:
 * - [<code>duplicateRows</code>][DataFrame.duplicateRows] — repeats rows inside a [<code>DataFrame</code>][DataFrame].
 * - [<code>duplicate</code>][DataFrame.duplicate] — puts copies of a whole [<code>DataFrame</code>][DataFrame] into a [<code>FrameColumn</code>][FrameColumn].
 *
 * ### Example
 * In the example, `df` has the columns `name` and `age` and three rows:
 * `Alice, 15`, `Bob, 20` and `Charlie, 30`.
 *
 * ```kotlin
 * // Bob, Bob, Bob
 * df[1].duplicate(3)
 * ```
 *
 * @param [n] The number of copies in the result, the original included. Must be greater than 0.
 * @throws [IllegalArgumentException] if [n] is 0 or negative.
 * @return A [<code>DataFrame</code>][DataFrame] with [<code>n</code>][n] copies of this [<code>DataRow</code>][DataRow].
 */
public fun <T> DataRow<T>.duplicate(n: Int): DataFrame<T> = duplicateImpl(n)
