package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.updateWith
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow

// region DataFrame

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the existing rows followed by new rows constructed from [<code>values</code>][values].
 *
 * The [<code>values</code>][values] form a flat sequence. Each consecutive group containing one value for every column forms one new row;
 * values within a group follow the dataframe's column order. The number of [<code>values</code>][values] must be a multiple
 * of the number of columns.
 *
 * Every appended value must be accepted by the corresponding column. A [<code>ValueColumn</code>][ValueColumn] accepts `null` or a value of
 * its declared type. A [<code>ColumnGroup</code>][ColumnGroup] accepts `null`; a [<code>DataRow</code>][DataRow], whose values are matched to the group's columns by
 * name (a group column whose name is absent from the row receives `null`); or a [<code>List</code>][List] whose values follow the
 * group's column order. A [<code>FrameColumn</code>][FrameColumn] accepts `null` or a [<code>DataFrame</code>][DataFrame].
 *
 * For a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], an appended `null` is passed to its nested columns. In a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], an appended `null`
 * is represented by an empty [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] created using the schema available from that [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * If [<code>values</code>][values] is empty, this [<code>DataFrame</code>][DataFrame] is returned as is.
 *
 * This operation does not modify the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Adding rows creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and rebuilds its columns using the existing and appended values.
 * Repeatedly appending rows one at a time in a loop is a performance antipattern.
 * Prefer building a dataframe at once or appending/concatenating rows in batches.
 *
 * For more information: [See `append` on the documentation website.](https://kotlin.github.io/dataframe/append.html)
 *
 * See also:
 * - [<code>appendNulls</code>][appendNulls] — appends rows filled with `null` values.
 * - [<code>concat</code>][DataFrame.concat] — vertically combines this [<code>DataFrame</code>][DataFrame] with other dataframes or rows.
 * - [<code>duplicate</code>][DataFrame.duplicate] — repeats existing rows.
 * - [<code>add</code>][DataFrame.add] — adds columns rather than rows.
 *
 * ### Examples
 * In the examples below, `df` has `"name"` and `"age"` columns and contains one row: `"Alice", 20`.
 *
 * Append one row:
 *
 * ```kotlin
 * df.append("Bob", 30)
 * ```
 *
 * Append several rows:
 *
 * ```kotlin
 * df.append(
 *     "Bob", // name in the first new row
 *     30, // age in the first new row
 *     "Charlie", // name in the second new row
 *     25, // age in the second new row
 * )
 * ```
 *
 * @param [values] A flat sequence of values for the appended rows. Each consecutive group containing one value for
 * every column, in the dataframe's column order, forms one row.
 * @return A new [<code>DataFrame</code>][DataFrame] containing the existing and appended rows, or this [<code>DataFrame</code>][DataFrame] if [<code>values</code>][values] is empty.
 * @throws [IllegalArgumentException] if [<code>values</code>][values] is not empty and this [<code>DataFrame</code>][DataFrame] has no columns, the number of
 * [<code>values</code>][values] is not a multiple of the number of columns, or a value is not accepted by the corresponding column.
 */
public fun <T> DataFrame<T>.append(vararg values: Any?): DataFrame<T> {
    if (values.isEmpty()) return this

    val ncol = ncol
    require(ncol > 0) {
        "Cannot append values to a DataFrame with no columns"
    }
    require(values.size % ncol == 0) {
        "Invalid number of arguments. Multiple of $ncol is expected, but actual was: ${values.size}"
    }
    val newRows = values.size / ncol
    return columns().mapIndexed { colIndex, col ->
        val newValues = (0 until newRows).map { values[colIndex + it * ncol] }
        col.updateWith(col.values + newValues)
    }.toDataFrame().cast()
}

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the existing rows followed by [<code>numberOfRows</code>][numberOfRows] new rows filled with `null` values.
 *
 * For a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], an appended `null` is passed to its nested columns. In a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], an appended `null`
 * is represented by an empty [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] created using the schema available from that [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * If [<code>numberOfRows</code>][numberOfRows] is `0`, this [<code>DataFrame</code>][DataFrame] is returned as is.
 *
 * This operation does not modify the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Adding rows creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and rebuilds its columns using the existing and appended values.
 * Repeatedly appending rows one at a time in a loop is a performance antipattern.
 * Prefer building a dataframe at once or appending/concatenating rows in batches.
 *
 * For more information: [See `appendNulls` on the documentation website.](https://kotlin.github.io/dataframe/appendnulls.html)
 *
 * See also:
 * - [<code>append</code>][DataFrame.append] — appends rows containing specified values.
 * - [<code>concat</code>][DataFrame.concat] — vertically combines this [<code>DataFrame</code>][DataFrame] with other dataframes or rows.
 * - [<code>fillNulls</code>][DataFrame.fillNulls] — replaces `null` values in existing rows.
 * - [<code>duplicate</code>][DataFrame.duplicate] — repeats existing rows.
 *
 * ### Examples
 * In the examples below, `df` has `"name"` and `"age"` columns and contains one row: `"Alice", 20`.
 *
 * Append one row using the default value of [<code>numberOfRows</code>][numberOfRows]:
 *
 * ```kotlin
 * df.appendNulls()
 * ```
 *
 * Append a specified number of rows:
 *
 * ```kotlin
 * df.appendNulls(numberOfRows = 3)
 * ```
 *
 * @param [numberOfRows] The number of rows to append. Must not be negative. The default is `1`.
 * @return A new [<code>DataFrame</code>][DataFrame] containing the existing rows followed by [<code>numberOfRows</code>][numberOfRows] new rows filled with `null`,
 * or this [<code>DataFrame</code>][DataFrame] if [<code>numberOfRows</code>][numberOfRows] is `0`.
 * @throws [IllegalArgumentException] if [<code>numberOfRows</code>][numberOfRows] is negative.
 */
public fun <T> DataFrame<T>.appendNulls(numberOfRows: Int = 1): DataFrame<T> {
    require(numberOfRows >= 0)
    if (numberOfRows == 0) return this
    if (ncol == 0) return DataFrame.empty(nrow + numberOfRows).cast()
    return columns().map { col ->
        col.updateWith(col.values + arrayOfNulls(numberOfRows))
    }.toDataFrame().cast()
}

// endregion
