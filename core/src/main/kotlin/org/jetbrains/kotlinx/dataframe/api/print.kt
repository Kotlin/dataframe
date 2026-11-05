package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.io.RenderToStringParamsSnippet
import org.jetbrains.kotlinx.dataframe.io.StringBorderStyle
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.util.PRINT

/**
 * This is a convenience function for quick interactive and debug output:
 * it only prints to standard output and does not transform any data.
 */
@ExcludeFromSources
internal typealias PrintConvenienceSnippet = Nothing

// region DataColumn

/**
 * Prints this [DataColumn] to standard output as a one-column table with its type.
 *
 * @include [PrintConvenienceSnippet]
 */
public fun <T> DataColumn<T>.print(): Unit = println(this)

// endregion

// region DataRow

/**
 * Prints this [DataRow] to standard output in the `{ name:value, ... }` format.
 * Null values and empty collections are skipped.
 *
 * @include [PrintConvenienceSnippet]
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
 * Prints this [DataFrame] to standard output as a text table rendered by [renderToString]
 * with the given options.
 *
 * See also [renderToString] — renders the same table to a [String] instead of printing it.
 *
 * @include [PrintConvenienceSnippet]
 *
 * @include [RenderToStringParamsSnippet]
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
 * Prints this [GroupBy] to standard output: its key columns and its [groups][GroupBy.groups] column
 * are rendered as a text table, one row per group. The other columns of the source [DataFrame] are not shown.
 *
 * @include [PrintConvenienceSnippet]
 */
public fun <T, G> GroupBy<T, G>.print(): Unit = println(this)

// endregion

// region DataFrameSchema

/**
 * Prints this [DataFrameSchema] to standard output as a list of its columns with their types.
 *
 * @include [PrintConvenienceSnippet]
 */
public fun DataFrameSchema.print(): Unit = println(this)

// endregion

/**
 * Prints the Kotlin code wrapped in this [CodeString] to standard output.
 *
 * @include [PrintConvenienceSnippet]
 */
public fun CodeString.print(): Unit = println(this)
