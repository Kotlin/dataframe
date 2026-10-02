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
 * @param [n\] The number of copies in the result, the original included. Must be greater than 0.
 * @throws [IllegalArgumentException] if [n\] is 0 or negative.
 */
@ExcludeFromSources
private interface DuplicateCountDocs

/**
 * In the example, `df` has the columns `name` and `age` and three rows:
 * `Alice, 15`, `Bob, 20` and `Charlie, 30`.
 */
@ExcludeFromSources
private interface DuplicateExampleDataDocs

/**
 * Returns a [FrameColumn] that holds this [DataFrame] [n] times.
 *
 * Each of the [n] cells of the column is this [DataFrame].
 * The column has an empty name.
 *
 * For more information: {@include [DocumentationUrls.DuplicateOnDataFrame]}
 *
 * See also:
 * - [duplicateRows][DataFrame.duplicateRows] — repeats every row of this [DataFrame] [n] times.
 * - [concat][DataColumn.concat] — joins the copies into one [DataFrame]:
 *   `df.duplicate(2).concat()` has all rows of `df`, followed by all rows of `df` again.
 *
 * ### Example
 * @include [DuplicateExampleDataDocs]
 * {@comment Backed by `KDoc example - DataFrame duplicate…` in DuplicateTests. }
 * ```kotlin
 * // A column with 3 cells, each cell holds `df`
 * df.duplicate(3)
 * ```
 *
 * @include [DuplicateCountDocs]
 * @return A [FrameColumn] with an empty name and [n] cells, each holding this [DataFrame].
 */
public fun <T> DataFrame<T>.duplicate(n: Int): FrameColumn<T> = duplicateImpl(n)

/**
 * Returns a [DataFrame] where every row is repeated [n] times.
 *
 * The copies of a row come right after it, so the order of the rows stays the same:
 * with [n] = 2, rows `A, B` become `A, A, B, B`.
 * Values in column groups and frame columns are repeated together with their rows.
 * The column names and types stay the same.
 *
 * Don't confuse this with [distinct][DataFrame.distinct], which removes repeated rows.
 *
 * For more information: {@include [DocumentationUrls.DuplicateRows]}
 *
 * See also:
 * - [duplicateRows][DataFrame.duplicateRows]`(n) { filter: RowFilter<T> }` — repeats only the rows
 *   that match a condition.
 * - [duplicate][DataFrame.duplicate] — puts copies of the whole [DataFrame] into a [FrameColumn].
 * - [duplicate][DataRow.duplicate] — makes a [DataFrame] out of copies of a single [DataRow].
 *
 * ### Example
 * @include [DuplicateExampleDataDocs]
 * {@comment Backed by `KDoc example - duplicateRows…` in DuplicateTests. }
 * ```kotlin
 * // Alice, Alice, Alice, Bob, Bob, Bob, Charlie, Charlie, Charlie
 * df.duplicateRows(3)
 * ```
 *
 * @include [DuplicateCountDocs]
 * @return A [DataFrame] with every row repeated [n] times.
 */
public fun <T> DataFrame<T>.duplicateRows(n: Int): DataFrame<T> = duplicateRowsImpl(n)

/**
 * Returns a [DataFrame] where each row that matches the [filter] is repeated [n] times.
 *
 * Rows that do not match the [filter] appear once, as before.
 * The copies of a row come right after it, so the order of the rows stays the same.
 * Values in column groups and frame columns are repeated together with their rows.
 * The column names and types stay the same.
 *
 * The [filter] is a [RowFilter] — a lambda that receives each [DataRow] as both `this` and `it`
 * and returns `true` for the rows to repeat.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [extension properties][AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * For more information: {@include [DocumentationUrls.DuplicateRows]} {@include [DocumentationUrls.DataRow.RowFilter]}
 *
 * See also:
 * - [duplicateRows][DataFrame.duplicateRows]`(n: Int)` — repeats every row.
 * - [filter][DataFrame.filter] — keeps only the rows that match a condition.
 *
 * ### Example
 * @include [DuplicateExampleDataDocs]
 * {@comment Backed by `KDoc example - duplicateRows with a condition…` in DuplicateTests. }
 * ```kotlin
 * // Alice, Bob, Bob, Bob, Charlie, Charlie, Charlie
 * df.duplicateRows(3) { age > 18 }
 * ```
 *
 * @param [filter] The condition that selects the rows to repeat.
 * @include [DuplicateCountDocs]
 * @return A [DataFrame] where the rows that match the [filter] are repeated [n] times
 * and the other rows appear once.
 */
public inline fun <T> DataFrame<T>.duplicateRows(n: Int, filter: RowFilter<T>): DataFrame<T> =
    duplicateRowsImpl(n, rows().filter { filter(it, it) }.map { it.index() })

/**
 * Returns a [DataFrame] with [n] rows, each of them a copy of this [DataRow].
 *
 * The result has the same columns as the [DataFrame] this row comes from,
 * column groups and frame columns included.
 * The type of each column follows the value in this row:
 * a nullable column whose value in this row is not `null` becomes non-nullable in the result.
 *
 * For more information: {@include [DocumentationUrls.DuplicateOnDataRow]}
 *
 * See also:
 * - [duplicateRows][DataFrame.duplicateRows] — repeats rows inside a [DataFrame].
 * - [duplicate][DataFrame.duplicate] — puts copies of a whole [DataFrame] into a [FrameColumn].
 *
 * ### Example
 * @include [DuplicateExampleDataDocs]
 * {@comment Backed by `KDoc example - DataRow duplicate…` in DuplicateTests. }
 * ```kotlin
 * // Bob, Bob, Bob
 * df[1].duplicate(3)
 * ```
 *
 * @include [DuplicateCountDocs]
 * @return A [DataFrame] with [n] copies of this [DataRow].
 */
public fun <T> DataRow<T>.duplicate(n: Int): DataFrame<T> = duplicateImpl(n)
