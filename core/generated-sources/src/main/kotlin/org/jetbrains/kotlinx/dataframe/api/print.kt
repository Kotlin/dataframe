package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.io.StringBorderStyle
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.util.PRINT

// region DataColumn

/**
 * Prints this [<code>DataColumn</code>][DataColumn] to standard output as a one-column table with its type.
 *
 * This is a convenience function for quick interactive and debug output:
 * it only prints to standard output and does not transform any data.
 */
public fun <T> DataColumn<T>.print(): Unit = println(this)

// endregion

// region DataRow

/**
 * Prints this [<code>DataRow</code>][DataRow] to standard output in the `{ name:value, ... }` format.
 * Null values and empty collections are skipped.
 *
 * This is a convenience function for quick interactive and debug output:
 * it only prints to standard output and does not transform any data.
 */
public fun <T> DataRow<T>.print(): Unit = println(this)

// endregion

// region DataFrame

@Deprecated(message = PRINT, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.print(
    rowsLimit: Int = 20,
    valueLimit: Int = 40,
    borders: Boolean = false,
    alignLeft: Boolean = false,
    columnTypes: Boolean = false,
    title: Boolean = false,
): Unit = print(rowsLimit, valueLimit, borders, alignLeft, columnTypes, title, true)

@Deprecated(message = PRINT, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.print(
    rowsLimit: Int = 20,
    valueLimit: Int = 40,
    borders: Boolean = false,
    alignLeft: Boolean = false,
    columnTypes: Boolean = true,
    title: Boolean = false,
    rowIndex: Boolean = true,
): Unit = print(rowsLimit, valueLimit, borders, alignLeft, columnTypes, title, rowIndex)

/**
 * Prints this [<code>DataFrame</code>][DataFrame] to standard output as a text table rendered by [<code>renderToString</code>][renderToString]
 * with the given options.
 *
 * See also [<code>renderToString</code>][renderToString] — renders the same table to a [<code>String</code>][String] instead of printing it.
 *
 * This is a convenience function for quick interactive and debug output:
 * it only prints to standard output and does not transform any data.
 *
 * @param [rowsLimit] The maximum number of rows to render (default: `20`). When some rows are skipped,
 * a `...` line and the total column and row counts are rendered below the table. `null` renders all rows.
 * @param [valueLimit] The maximum length of a rendered cell value (default: `40`). Longer values are cut
 * to `valueLimit` characters ending in `...`, or replaced with just `...` when `valueLimit` is less than 4.
 * `null` does not limit the length.
 * @param [borders] Whether to draw borders around the table (default: `false`).
 * @param [alignLeft] Whether to align the cell content to the left instead of the right (default: `false`).
 * @param [columnTypes] Whether to render the column types under the header (default: `true`).
 * @param [title] Whether to add a `DataFrame [rows x columns]` line above the table (default: `false`).
 * @param [rowIndex] Whether to include the row index as the first column (default: `true`).
 * @param [borderStyle] The style of the border characters drawn when [borders] is `true`
 * (default: [<code>StringBorderStyle.DottedLineWithRounderCorners</code>][org.jetbrains.kotlinx.dataframe.io.StringBorderStyle.DottedLineWithRounderCorners]).
 */
public fun <T> DataFrame<T>.print(
    rowsLimit: Int? = 20,
    valueLimit: Int? = 40,
    borders: Boolean = false,
    alignLeft: Boolean = false,
    columnTypes: Boolean = true,
    title: Boolean = false,
    rowIndex: Boolean = true,
    borderStyle: StringBorderStyle = StringBorderStyle.DottedLineWithRounderCorners,
): Unit = println(renderToString(rowsLimit, valueLimit, borders, alignLeft, columnTypes, title, rowIndex, borderStyle))

// endregion

// region GroupBy

/**
 * Prints this [<code>GroupBy</code>][GroupBy] to standard output: its key columns and its [<code>groups</code>][GroupBy.groups] column
 * are rendered as a text table, one row per group. The other columns of the source [<code>DataFrame</code>][DataFrame] are not shown.
 *
 * This is a convenience function for quick interactive and debug output:
 * it only prints to standard output and does not transform any data.
 */
public fun <T, G> GroupBy<T, G>.print(): Unit = println(this)

// endregion

// region DataFrameSchema

/**
 * Prints this [<code>DataFrameSchema</code>][DataFrameSchema] to standard output as a list of its columns with their types.
 *
 * This is a convenience function for quick interactive and debug output:
 * it only prints to standard output and does not transform any data.
 */
public fun DataFrameSchema.print(): Unit = println(this)

// endregion

/**
 * Prints the Kotlin code wrapped in this [<code>CodeString</code>][CodeString] to standard output.
 *
 * This is a convenience function for quick interactive and debug output:
 * it only prints to standard output and does not transform any data.
 */
public fun CodeString.print(): Unit = println(this)
