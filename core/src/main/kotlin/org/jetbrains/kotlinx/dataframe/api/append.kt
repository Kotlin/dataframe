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
 * Returns a [DataFrame] containing the existing rows followed by new rows constructed from [values].
 *
 * The [values] form a flat sequence. Each consecutive group containing one value for every column forms one new row;
 * values within a group follow the dataframe's column order. The number of [values] must be a multiple
 * of the number of columns.
 *
 * Every appended value must be accepted by the corresponding column. A [ValueColumn] accepts `null` or a value of
 * its declared type. A [ColumnGroup] accepts `null`; a [DataRow], whose values are matched to the group's columns by
 * name (a group column whose name is absent from the row receives `null`); or a [List] whose values follow the
 * group's column order. A [FrameColumn] accepts `null` or a [DataFrame].
 *
 * @include [AppendingNullsToHierarchicalColumns]
 *
 * If [values] is empty, this [DataFrame] is returned as is.
 *
 * @include [AppendImmutabilityAndPerformanceNote]
 *
 * For more information: {@include [DocumentationUrls.Append]}
 *
 * See also:
 * - [appendNulls] — appends rows filled with `null` values.
 * - [concat][DataFrame.concat] — vertically combines this [DataFrame] with other dataframes or rows.
 * - [duplicate][DataFrame.duplicate] — repeats existing rows.
 * - [add][DataFrame.add] — adds columns rather than rows.
 *
 * ### Examples
 * In the examples below, `df` has `"name"` and `"age"` columns and contains one row: `"Alice", 20`.
 *
 * Append one row:
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.Append.appendOneRow]
 *
 * Append several rows:
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.Append.appendSeveralRows]
 *
 * @param [values] A flat sequence of values for the appended rows. Each consecutive group containing one value for
 * every column, in the dataframe's column order, forms one row.
 * @return A new [DataFrame] containing the existing and appended rows, or this [DataFrame] if [values] is empty.
 * @throws [IllegalArgumentException] if [values] is not empty and this [DataFrame] has no columns, the number of
 * [values] is not a multiple of the number of columns, or a value is not accepted by the corresponding column.
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
 * Returns a [DataFrame] containing the existing rows followed by [numberOfRows] new rows filled with `null` values.
 *
 * @include [AppendingNullsToHierarchicalColumns]
 *
 * If [numberOfRows] is `0`, this [DataFrame] is returned as is.
 *
 * @include [AppendImmutabilityAndPerformanceNote]
 *
 * For more information: {@include [DocumentationUrls.AppendNulls]}
 *
 * See also:
 * - [append][DataFrame.append] — appends rows containing specified values.
 * - [concat][DataFrame.concat] — vertically combines this [DataFrame] with other dataframes or rows.
 * - [fillNulls][DataFrame.fillNulls] — replaces `null` values in existing rows.
 * - [duplicate][DataFrame.duplicate] — repeats existing rows.
 *
 * ### Examples
 * In the examples below, `df` has `"name"` and `"age"` columns and contains one row: `"Alice", 20`.
 *
 * Append one row using the default value of [numberOfRows]:
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.Append.appendOneNullRow]
 *
 * Append a specified number of rows:
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.Append.appendSeveralNullRows]
 *
 * @param [numberOfRows] The number of rows to append. Must not be negative. The default is `1`.
 * @return A new [DataFrame] containing the existing rows followed by [numberOfRows] new rows filled with `null`,
 * or this [DataFrame] if [numberOfRows] is `0`.
 * @throws [IllegalArgumentException] if [numberOfRows] is negative.
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

/**
 * This operation does not modify the original [DataFrame].
 *
 * Adding rows creates a new [DataFrame] and rebuilds its columns using the existing and appended values.
 * Repeatedly appending rows one at a time in a loop is a performance antipattern.
 * Prefer building a dataframe at once or appending/concatenating rows in batches.
 */
@ExcludeFromSources
internal typealias AppendImmutabilityAndPerformanceNote = Nothing

/**
 * For a [ColumnGroup], an appended `null` is passed to its nested columns. In a [FrameColumn], an appended `null`
 * is represented by an empty [DataFrame] created using the schema available from that [FrameColumn].
 */
@ExcludeFromSources
private typealias AppendingNullsToHierarchicalColumns = Nothing
