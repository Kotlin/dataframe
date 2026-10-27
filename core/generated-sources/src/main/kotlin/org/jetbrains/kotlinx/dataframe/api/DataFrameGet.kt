package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.getColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region docs

// endregion

// region getColumnsWithPaths / getColumnPaths

/**
 * Returns the columns selected by [<code>selector</code>][selector], each paired with its [<code>ColumnPath</code>][ColumnPath], as a [<code>List</code>][List] of [<code>ColumnWithPath</code>][ColumnWithPath].
 *
 * The columns are selected with a [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 * They can be columns at any depth and are returned in the order they are selected.
 *
 * A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] is the chain of column names from the top level of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to the column,
 * through its parent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], like [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]`("name", "firstName")`.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * See also [<code>getColumnPath</code>][org.jetbrains.kotlinx.dataframe.api.getColumnPath] and [<code>getColumnPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnPaths], that return only the paths,
 * [<code>getColumnWithPath</code>][org.jetbrains.kotlinx.dataframe.api.getColumnWithPath] and [<code>getColumnsWithPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths], that return the columns together with their paths,
 * and [<code>getColumns</code>][org.jetbrains.kotlinx.dataframe.api.getColumns], that returns only the columns.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumnPaths { name.allCols() } // [pathOf("name", "firstName"), pathOf("name", "lastName")]
 * df.getColumnPath { name.lastName } // pathOf("name", "lastName")
 * df.getColumnsWithPaths { age and name.firstName } // [age] and [firstName] with their paths
 * df.getColumnWithPath { name.firstName }.path // pathOf("name", "firstName")
 * ```
 *
 * @param [selector] A [<code>ColumnsSelector</code>][ColumnsSelector] that selects the columns.
 * @return A [<code>List</code>][List] of [<code>ColumnWithPath</code>][ColumnWithPath], one for each selected column, in the order they are selected.
 * @throws [IllegalStateException] if [<code>selector</code>][selector] refers to a column that doesn't exist.
 */
public fun <T, C> DataFrame<T>.getColumnsWithPaths(selector: ColumnsSelector<T, C>): List<ColumnWithPath<C>> =
    getColumnsWithPaths(UnresolvedColumnsPolicy.Fail, selector)

/**
 * Returns the [<code>ColumnPath</code>][ColumnPath] of the single column selected by [<code>selector</code>][selector],
 * throwing if [<code>selector</code>][selector] doesn't resolve to exactly one column.
 *
 * The column is selected with a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector] lambda in the [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl].
 * It can be a nested column, and its type is inferred from the selector.
 *
 * A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] is the chain of column names from the top level of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to the column,
 * through its parent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], like [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]`("name", "firstName")`.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * See also [<code>getColumnPath</code>][org.jetbrains.kotlinx.dataframe.api.getColumnPath] and [<code>getColumnPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnPaths], that return only the paths,
 * [<code>getColumnWithPath</code>][org.jetbrains.kotlinx.dataframe.api.getColumnWithPath] and [<code>getColumnsWithPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths], that return the columns together with their paths,
 * and [<code>getColumns</code>][org.jetbrains.kotlinx.dataframe.api.getColumns], that returns only the columns.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumnPaths { name.allCols() } // [pathOf("name", "firstName"), pathOf("name", "lastName")]
 * df.getColumnPath { name.lastName } // pathOf("name", "lastName")
 * df.getColumnsWithPaths { age and name.firstName } // [age] and [firstName] with their paths
 * df.getColumnWithPath { name.firstName }.path // pathOf("name", "firstName")
 * ```
 *
 * @param [selector] A [<code>ColumnSelector</code>][ColumnSelector] that selects exactly one column.
 * @return The [<code>ColumnPath</code>][ColumnPath] of the selected column.
 * @throws [IllegalStateException] if the selector refers to a column that doesn't exist.
 * @throws [NoSuchElementException] if the selector resolves to no columns.
 * @throws [IllegalArgumentException] if the selector resolves to more than one column.
 */
public fun <T, C> DataFrame<T>.getColumnPath(selector: ColumnSelector<T, C>): ColumnPath =
    getColumnPaths(selector).single()

/**
 * Returns the [<code>ColumnPath</code>][ColumnPath]s of the columns selected by [<code>selector</code>][selector], in the order they are selected.
 *
 * The columns are selected with a [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 * They can be columns at any depth and are returned in the order they are selected.
 *
 * A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] is the chain of column names from the top level of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to the column,
 * through its parent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], like [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]`("name", "firstName")`.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * See also [<code>getColumnPath</code>][org.jetbrains.kotlinx.dataframe.api.getColumnPath] and [<code>getColumnPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnPaths], that return only the paths,
 * [<code>getColumnWithPath</code>][org.jetbrains.kotlinx.dataframe.api.getColumnWithPath] and [<code>getColumnsWithPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths], that return the columns together with their paths,
 * and [<code>getColumns</code>][org.jetbrains.kotlinx.dataframe.api.getColumns], that returns only the columns.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumnPaths { name.allCols() } // [pathOf("name", "firstName"), pathOf("name", "lastName")]
 * df.getColumnPath { name.lastName } // pathOf("name", "lastName")
 * df.getColumnsWithPaths { age and name.firstName } // [age] and [firstName] with their paths
 * df.getColumnWithPath { name.firstName }.path // pathOf("name", "firstName")
 * ```
 *
 * @param [selector] A [<code>ColumnsSelector</code>][ColumnsSelector] that selects the columns.
 * @return A [<code>List</code>][List] of [<code>ColumnPath</code>][ColumnPath], one for each selected column, in the order they are selected.
 * @throws [IllegalStateException] if [<code>selector</code>][selector] refers to a column that doesn't exist.
 */
public fun <T, C> DataFrame<T>.getColumnPaths(selector: ColumnsSelector<T, C>): List<ColumnPath> =
    getColumnPaths(UnresolvedColumnsPolicy.Fail, selector)

/**
 * Returns the single column selected by [<code>selector</code>][selector], paired with its [<code>ColumnPath</code>][ColumnPath] as a [<code>ColumnWithPath</code>][ColumnWithPath],
 * throwing if [<code>selector</code>][selector] doesn't resolve to exactly one column.
 *
 * The column is selected with a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector] lambda in the [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl].
 * It can be a nested column, and its type is inferred from the selector.
 *
 * A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] is the chain of column names from the top level of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to the column,
 * through its parent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], like [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]`("name", "firstName")`.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * See also [<code>getColumnPath</code>][org.jetbrains.kotlinx.dataframe.api.getColumnPath] and [<code>getColumnPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnPaths], that return only the paths,
 * [<code>getColumnWithPath</code>][org.jetbrains.kotlinx.dataframe.api.getColumnWithPath] and [<code>getColumnsWithPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths], that return the columns together with their paths,
 * and [<code>getColumns</code>][org.jetbrains.kotlinx.dataframe.api.getColumns], that returns only the columns.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumnPaths { name.allCols() } // [pathOf("name", "firstName"), pathOf("name", "lastName")]
 * df.getColumnPath { name.lastName } // pathOf("name", "lastName")
 * df.getColumnsWithPaths { age and name.firstName } // [age] and [firstName] with their paths
 * df.getColumnWithPath { name.firstName }.path // pathOf("name", "firstName")
 * ```
 *
 * @param [selector] A [<code>ColumnSelector</code>][ColumnSelector] that selects exactly one column.
 * @return A [<code>ColumnWithPath</code>][ColumnWithPath] with the selected column and its [<code>ColumnPath</code>][ColumnPath].
 * @throws [IllegalStateException] if the selector refers to a column that doesn't exist.
 * @throws [NoSuchElementException] if the selector resolves to no columns.
 * @throws [IllegalArgumentException] if the selector resolves to more than one column.
 */
public fun <T, C> DataFrame<T>.getColumnWithPath(selector: ColumnSelector<T, C>): ColumnWithPath<C> =
    getColumnsWithPaths(selector).single()

// endregion

// region getColumns / getColumnIndex

/**
 * Returns the columns selected by [<code>selector</code>][selector] as a [<code>List</code>][List] of [<code>DataColumn</code>][DataColumn], in the order they are selected.
 *
 * The columns are selected with a [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 * They can be columns at any depth and are returned in the order they are selected.
 *
 * It is the same as [<code>get</code>][ColumnsContainer.get] with a [<code>ColumnsSelector</code>][ColumnsSelector].
 *
 * For more information: [See `getColumns` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html#getcolumns)
 *
 * See also [<code>getColumn</code>][org.jetbrains.kotlinx.dataframe.api.getColumn], that returns a single column,
 * [<code>getColumnsWithPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths], that also returns the [<code>path</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] of each column,
 * and [<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select], that returns the selected columns as a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumns { age and name.firstName } // [age, firstName]
 * df.getColumns("city", "age") // [city, age]: in the order of the arguments
 * df.getColumnIndex(df.city) // 2
 * ```
 *
 * @param [selector] A [<code>ColumnsSelector</code>][ColumnsSelector] that selects the columns.
 * @return A [<code>List</code>][List] with the selected columns, in the order they are selected.
 * @throws [IllegalStateException] if [<code>selector</code>][selector] refers to a column that doesn't exist.
 */
public fun <T, C> DataFrame<T>.getColumns(selector: ColumnsSelector<T, C>): List<DataColumn<C>> = get(selector)

/**
 * Returns the top-level columns with the given names as a [<code>List</code>][List] of [<code>DataColumn</code>][DataColumn], in the order of [<code>columns</code>][columns].
 *
 * The name is looked up among the top-level columns only.
 * To get a nested column, use a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] or a column selector instead.
 *
 * For more information: [See `getColumns` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html#getcolumns)
 *
 * See also [<code>getColumn</code>][org.jetbrains.kotlinx.dataframe.api.getColumn], that returns a single column,
 * [<code>getColumnsWithPaths</code>][org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths], that also returns the [<code>path</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] of each column,
 * and [<code>select</code>][org.jetbrains.kotlinx.dataframe.api.select], that returns the selected columns as a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumns { age and name.firstName } // [age, firstName]
 * df.getColumns("city", "age") // [city, age]: in the order of the arguments
 * df.getColumnIndex(df.city) // 2
 * ```
 *
 * @param [columns] The names of the top-level columns to return.
 * @return A [<code>List</code>][List] with the columns, in the order of [<code>columns</code>][columns].
 * @throws [IllegalStateException] if there is no top-level column with one of the given names.
 */
public fun <T> DataFrame<T>.getColumns(vararg columns: String): List<DataColumn<*>> =
    getColumns {
        columns.toColumnSet()
    }

/**
 * Returns the zero-based index of the top-level column with the same name as [<code>col</code>][col],
 * or `-1` if there is no such column; it doesn't throw.
 *
 * Only the [<code>name</code>][DataColumn.name] of [<code>col</code>][col] is compared, not its values,
 * so a nested column has an index only if a top-level column has the same name.
 *
 * For more information: [See `getColumn` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html)
 *
 * See also [<code>getColumnIndex</code>][ColumnsContainer.getColumnIndex], that takes the column name,
 * and [<code>getColumn</code>][getColumn], that returns the column at an index.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumns { age and name.firstName } // [age, firstName]
 * df.getColumns("city", "age") // [city, age]: in the order of the arguments
 * df.getColumnIndex(df.city) // 2
 * ```
 *
 * @param [col] The column whose name is looked up among the top-level columns.
 * @return The index of the top-level column named like [<code>col</code>][col], or `-1` if there is none.
 */
public fun <T> DataFrame<T>.getColumnIndex(col: DataColumn<*>): Int = getColumnIndex(col.name())

// endregion

// region getRows / getOrNull

/**
 * Returns a new [<code>DataFrame</code>][DataFrame] with the rows at the zero-based indices in [<code>range</code>][range],
 * throwing if any of them is out of bounds.
 *
 * It is the same as [<code>get</code>][DataFrame.get] with an [<code>IntRange</code>][IntRange].
 *
 * For more information: [See "Get rows" on the documentation website.](https://kotlin.github.io/dataframe/getrow.html)
 *
 * See also [<code>getOrNull</code>][org.jetbrains.kotlinx.dataframe.api.getOrNull], that returns a single row or `null`,
 * [<code>get</code>][org.jetbrains.kotlinx.dataframe.DataFrame.get], that returns a single row or several rows by their indices,
 * and [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.filter], that returns the rows matching a condition.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getRows(0..2) // a DataFrame with the first three rows
 * df.getRows(listOf(4, 1)) // a DataFrame with the rows 4 and 1, in this order
 * ```
 *
 * @param [range] The range of row indices to return.
 * @return A new [<code>DataFrame</code>][DataFrame] with the rows in [<code>range</code>][range].
 * @throws [IndexOutOfBoundsException] if [<code>range</code>][range] contains an index out of the bounds of this [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataFrame<T>.getRows(range: IntRange): DataFrame<T> = get(range)

/**
 * Returns a new [<code>DataFrame</code>][DataFrame] with the rows at the zero-based [<code>indices</code>][indices], in the order of [<code>indices</code>][indices],
 * throwing if any of them is out of bounds.
 *
 * The same index can occur several times; then the row is repeated.
 * It is the same as [<code>get</code>][DataFrame.get] with an [<code>Iterable</code>][Iterable] of indices.
 *
 * For more information: [See "Get rows" on the documentation website.](https://kotlin.github.io/dataframe/getrow.html)
 *
 * See also [<code>getOrNull</code>][org.jetbrains.kotlinx.dataframe.api.getOrNull], that returns a single row or `null`,
 * [<code>get</code>][org.jetbrains.kotlinx.dataframe.DataFrame.get], that returns a single row or several rows by their indices,
 * and [<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.filter], that returns the rows matching a condition.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getRows(0..2) // a DataFrame with the first three rows
 * df.getRows(listOf(4, 1)) // a DataFrame with the rows 4 and 1, in this order
 * ```
 *
 * @param [indices] The row indices to return.
 * @return A new [<code>DataFrame</code>][DataFrame] with the rows at [<code>indices</code>][indices], in the order of [<code>indices</code>][indices].
 * @throws [IndexOutOfBoundsException] if [<code>indices</code>][indices] contains an index out of the bounds of this [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataFrame<T>.getRows(indices: Iterable<Int>): DataFrame<T> = get(indices)

/**
 * Returns the [<code>row</code>][DataRow] at the zero-based [<code>index</code>][index], or `null` if [<code>index</code>][index] is negative
 * or not less than the number of rows.
 *
 * It is the safe counterpart of [<code>get</code>][DataFrame.get] with an [<code>Int</code>][Int], which throws instead.
 *
 * For more information: [See "Get rows" on the documentation website.](https://kotlin.github.io/dataframe/getrow.html)
 *
 * See also [<code>get</code>][DataFrame.get], that throws instead of returning `null`,
 * [<code>getRows</code>][getRows], that returns several rows,
 * and [<code>firstOrNull</code>][firstOrNull], [<code>lastOrNull</code>][lastOrNull], that return the first or the last row, or `null` if this [<code>DataFrame</code>][DataFrame] is empty.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getOrNull(1) // the row with index 1
 * df.getOrNull(10) // null: df has only 10 rows
 * df.getOrNull(-1) // null
 * ```
 *
 * @param [index] The index of the row.
 * @return The [<code>DataRow</code>][DataRow] at [<code>index</code>][index], or `null` if [<code>index</code>][index] is out of the bounds of this [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataFrame<T>.getOrNull(index: Int): DataRow<T>? = if (index < 0 || index >= nrow) null else get(index)

// endregion

// region getFrameColumn

/**
 * Returns the column at [<code>columnPath</code>][columnPath] as a [<code>FrameColumn</code>][FrameColumn], throwing if there is no such column.
 *
 * The [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] is a chain of column names that leads through [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * to a column at any depth, like [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]`("name", "firstName")`.
 *
 * The `getFrameColumn` overloads find the column by a top-level column name (`String`) or by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath].
 * They throw if the column doesn't exist, and they don't check the kind of the column:
 * a column that isn't a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] leads to a [<code>ClassCastException</code>][ClassCastException].
 *
 * For more information: [See `getColumn` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html)
 *
 * See also [<code>getColumnOrNull</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.getColumnOrNull], that returns `null` instead of throwing,
 * [<code>getColumn</code>][org.jetbrains.kotlinx.dataframe.api.getColumn], that returns a column of any kind,
 * and [<code>getColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroup], that returns the column as a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * ### Examples
 *
 * ```kotlin
 * val grouped = df.groupBy { city }.toDataFrame() // has the FrameColumn "group"
 * grouped.getFrameColumn("group") // FrameColumn<*>
 * grouped.getFrameColumn(pathOf("group")) // FrameColumn<*>
 * ```
 *
 * @param [columnPath] The [<code>ColumnPath</code>][ColumnPath] of the column.
 * @return The column at [<code>columnPath</code>][columnPath] as a [<code>FrameColumn</code>][FrameColumn]`<*>`.
 * @throws [IllegalArgumentException] if there is no column at [<code>columnPath</code>][columnPath].
 * @throws [ClassCastException] if the column at [<code>columnPath</code>][columnPath] is not a [<code>FrameColumn</code>][FrameColumn].
 */
public fun <T> ColumnsContainer<T>.getFrameColumn(columnPath: ColumnPath): FrameColumn<*> =
    get(columnPath).asAnyFrameColumn()

/**
 * Returns the top-level column named [<code>columnName</code>][columnName] as a [<code>FrameColumn</code>][FrameColumn], throwing if there is no such column.
 *
 * The name is looked up among the top-level columns only.
 * To get a nested column, use a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] or a column selector instead.
 *
 * The `getFrameColumn` overloads find the column by a top-level column name (`String`) or by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath].
 * They throw if the column doesn't exist, and they don't check the kind of the column:
 * a column that isn't a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] leads to a [<code>ClassCastException</code>][ClassCastException].
 *
 * For more information: [See `getColumn` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html)
 *
 * See also [<code>getColumnOrNull</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.getColumnOrNull], that returns `null` instead of throwing,
 * [<code>getColumn</code>][org.jetbrains.kotlinx.dataframe.api.getColumn], that returns a column of any kind,
 * and [<code>getColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroup], that returns the column as a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * ### Examples
 *
 * ```kotlin
 * val grouped = df.groupBy { city }.toDataFrame() // has the FrameColumn "group"
 * grouped.getFrameColumn("group") // FrameColumn<*>
 * grouped.getFrameColumn(pathOf("group")) // FrameColumn<*>
 * ```
 *
 * @param [columnName] The name of the top-level column.
 * @return The column named [<code>columnName</code>][columnName] as a [<code>FrameColumn</code>][FrameColumn]`<*>`.
 * @throws [IllegalArgumentException] if there is no top-level column named [<code>columnName</code>][columnName].
 * @throws [ClassCastException] if the column named [<code>columnName</code>][columnName] is not a [<code>FrameColumn</code>][FrameColumn].
 */
public fun <T> ColumnsContainer<T>.getFrameColumn(columnName: String): FrameColumn<*> =
    get(columnName).asAnyFrameColumn()

// endregion

/**
 * Returns the column at [<code>columnPath</code>][columnPath] as a [<code>ColumnGroup</code>][ColumnGroup], throwing if there is no such column.
 *
 * The [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] is a chain of column names that leads through [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * to a column at any depth, like [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]`("name", "firstName")`.
 *
 * The `getColumnGroup` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], by a zero-based column index (`Int`),
 * or by a [<code>column selector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector].
 * Only the selector overload returns a typed [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]; the others return [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]`<*>`.
 * They throw if the column doesn't exist, and they don't check the kind of the column:
 * a column that isn't a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] leads to a [<code>ClassCastException</code>][ClassCastException].
 *
 * For more information: [See `getColumnGroup` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html#getcolumngroup)
 *
 * See also [<code>getColumnGroupOrNull</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroupOrNull], that returns `null` instead of throwing if there is no such column,
 * [<code>getColumn</code>][org.jetbrains.kotlinx.dataframe.api.getColumn], that returns a column of any kind,
 * and [<code>getFrameColumn</code>][org.jetbrains.kotlinx.dataframe.api.getFrameColumn], that returns the column as a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumnGroup("name") // ColumnGroup<*>
 * df.getColumnGroup(0) // ColumnGroup<*>: "name"
 * df.getColumnGroup(pathOf("name")) // ColumnGroup<*>
 * df.getColumnGroup { name } // ColumnGroup<Name>
 * df.getColumnGroupOrNull("address") // null: there is no such column
 * ```
 *
 * @param [columnPath] The [<code>ColumnPath</code>][ColumnPath] of the column.
 * @return The column at [<code>columnPath</code>][columnPath] as a [<code>ColumnGroup</code>][ColumnGroup]`<*>`.
 * @throws [IllegalArgumentException] if there is no column at [<code>columnPath</code>][columnPath].
 * @throws [ClassCastException] if the column at [<code>columnPath</code>][columnPath] is not a [<code>ColumnGroup</code>][ColumnGroup].
 */
public fun <T> ColumnsContainer<T>.getColumnGroup(columnPath: ColumnPath): ColumnGroup<*> =
    get(columnPath).asColumnGroup()

/**
 * Utility property to access scope with only dataframe column properties for code completion,
 * filtering out DataFrame API.
 *
 * It's a quick way to check that code generation in notebooks or compiler plugin
 * worked as expected or find columns you're interested in.
 *
 * In notebooks:
 * ```
 * val df = DataFrame.read("file.csv")
 * ==== next code cell
 * df. // column properties are mixed together with methods, not easy to find unless you already know names
 * df.properties(). // easy to overview available columns
 * ```
 * In compiler plugin:
 * ```
 * val df = @Import DataFrame.read("file.csv")
 * df.properties().
 * ```
 */
public fun <T> DataFrame<T>.properties(): ColumnsScope<T> = this

// region getColumn

/**
 * Returns the top-level column named [<code>name</code>][name] as a [<code>DataColumn</code>][DataColumn], throwing if there is no such column.
 *
 * The name is looked up among the top-level columns only.
 * To get a nested column, use a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] or a column selector instead.
 *
 * The `getColumn` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], by a zero-based column index (`Int`),
 * or by a [<code>column selector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector].
 * Only the selector overload returns a typed [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]; the others return [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<*>`.
 * All of them throw if the column doesn't exist.
 *
 * For more information: [See `getColumn` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html)
 *
 * See also [<code>getColumnOrNull</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.getColumnOrNull], that returns `null` instead of throwing,
 * [<code>getColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroup] and [<code>getFrameColumn</code>][org.jetbrains.kotlinx.dataframe.api.getFrameColumn], that return the column as a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn],
 * and [<code>getColumns</code>][org.jetbrains.kotlinx.dataframe.api.getColumns], that returns several columns.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumn("age") // by the name of a top-level column
 * df.getColumn(pathOf("name", "firstName")) // by a path, also to a nested column
 * df.getColumn(1) // by the index of a top-level column: "age"
 * df.getColumn { name.firstName } // by a column selector, typed: DataColumn<String>
 * ```
 *
 * @param [name] The name of the top-level column.
 * @return The column named [<code>name</code>][name] as a [<code>DataColumn</code>][DataColumn]`<*>`.
 * @throws [IllegalArgumentException] if there is no top-level column named [<code>name</code>][name].
 */
public fun <T> ColumnsContainer<T>.getColumn(name: String): DataColumn<*> =
    getColumnOrNull(name) ?: throw IllegalArgumentException("Column not found: '$name'")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.getColumn(column: ColumnReference<DataFrame<R>>): FrameColumn<R> =
    getColumnOrNull(column)?.asFrameColumn() ?: throw IllegalArgumentException("FrameColumn not found: '$column'")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.getColumn(column: ColumnReference<DataRow<R>>): ColumnGroup<R> =
    getColumnOrNull(column)?.asColumnGroup() ?: throw IllegalArgumentException("ColumnGroup not found: '$column'")

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.getColumn(column: ColumnReference<R>): DataColumn<R> =
    getColumnOrNull(column) ?: throw IllegalArgumentException("Column not found: '$column'")

/**
 * Returns the column at [<code>path</code>][path] as a [<code>DataColumn</code>][DataColumn], throwing if there is no such column.
 *
 * The [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] is a chain of column names that leads through [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * to a column at any depth, like [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]`("name", "firstName")`.
 *
 * The `getColumn` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], by a zero-based column index (`Int`),
 * or by a [<code>column selector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector].
 * Only the selector overload returns a typed [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]; the others return [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<*>`.
 * All of them throw if the column doesn't exist.
 *
 * For more information: [See `getColumn` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html)
 *
 * See also [<code>getColumnOrNull</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.getColumnOrNull], that returns `null` instead of throwing,
 * [<code>getColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroup] and [<code>getFrameColumn</code>][org.jetbrains.kotlinx.dataframe.api.getFrameColumn], that return the column as a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn],
 * and [<code>getColumns</code>][org.jetbrains.kotlinx.dataframe.api.getColumns], that returns several columns.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumn("age") // by the name of a top-level column
 * df.getColumn(pathOf("name", "firstName")) // by a path, also to a nested column
 * df.getColumn(1) // by the index of a top-level column: "age"
 * df.getColumn { name.firstName } // by a column selector, typed: DataColumn<String>
 * ```
 *
 * @param [path] The [<code>ColumnPath</code>][ColumnPath] of the column.
 * @return The column at [<code>path</code>][path] as a [<code>DataColumn</code>][DataColumn]`<*>`.
 * @throws [IllegalArgumentException] if there is no column at [<code>path</code>][path].
 */
public fun <T> ColumnsContainer<T>.getColumn(path: ColumnPath): DataColumn<*> =
    getColumnOrNull(path) ?: throw IllegalArgumentException("Column not found: '$path'")

/**
 * Returns the top-level column at [<code>index</code>][index] as a [<code>DataColumn</code>][DataColumn], throwing if [<code>index</code>][index] is out of bounds.
 *
 * The index is the zero-based position of the column among the top-level columns.
 *
 * The `getColumn` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], by a zero-based column index (`Int`),
 * or by a [<code>column selector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector].
 * Only the selector overload returns a typed [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]; the others return [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<*>`.
 * All of them throw if the column doesn't exist.
 *
 * For more information: [See `getColumn` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html)
 *
 * See also [<code>getColumnOrNull</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.getColumnOrNull], that returns `null` instead of throwing,
 * [<code>getColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroup] and [<code>getFrameColumn</code>][org.jetbrains.kotlinx.dataframe.api.getFrameColumn], that return the column as a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn],
 * and [<code>getColumns</code>][org.jetbrains.kotlinx.dataframe.api.getColumns], that returns several columns.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumn("age") // by the name of a top-level column
 * df.getColumn(pathOf("name", "firstName")) // by a path, also to a nested column
 * df.getColumn(1) // by the index of a top-level column: "age"
 * df.getColumn { name.firstName } // by a column selector, typed: DataColumn<String>
 * ```
 *
 * @param [index] The zero-based index of the top-level column.
 * @return The column at [<code>index</code>][index] as a [<code>DataColumn</code>][DataColumn]`<*>`.
 * @throws [IllegalArgumentException] if [<code>index</code>][index] is negative or not less than the number of top-level columns.
 */
public fun <T> ColumnsContainer<T>.getColumn(index: Int): DataColumn<*> =
    getColumnOrNull(index)
        ?: throw IllegalArgumentException("Column index is out of bounds: $index. Columns count = $ncol")

/**
 * Returns the single column selected by [<code>selector</code>][selector] as a typed [<code>DataColumn</code>][DataColumn],
 * throwing if [<code>selector</code>][selector] doesn't resolve to exactly one column.
 *
 * The column is selected with a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector] lambda in the [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl].
 * It can be a nested column, and its type is inferred from the selector.
 *
 * It is the same as [<code>get</code>][ColumnsContainer.get] with a [<code>ColumnSelector</code>][ColumnSelector].
 *
 * The `getColumn` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], by a zero-based column index (`Int`),
 * or by a [<code>column selector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector].
 * Only the selector overload returns a typed [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]; the others return [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<*>`.
 * All of them throw if the column doesn't exist.
 *
 * For more information: [See `getColumn` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html)
 *
 * See also [<code>getColumnOrNull</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.getColumnOrNull], that returns `null` instead of throwing,
 * [<code>getColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroup] and [<code>getFrameColumn</code>][org.jetbrains.kotlinx.dataframe.api.getFrameColumn], that return the column as a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn],
 * and [<code>getColumns</code>][org.jetbrains.kotlinx.dataframe.api.getColumns], that returns several columns.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumn("age") // by the name of a top-level column
 * df.getColumn(pathOf("name", "firstName")) // by a path, also to a nested column
 * df.getColumn(1) // by the index of a top-level column: "age"
 * df.getColumn { name.firstName } // by a column selector, typed: DataColumn<String>
 * ```
 *
 * @param [selector] A [<code>ColumnSelector</code>][ColumnSelector] that selects exactly one column.
 * @return The selected column as a [<code>DataColumn</code>][DataColumn]`<C>`.
 * @throws [IllegalStateException] if the selector refers to a column that doesn't exist.
 * @throws [NoSuchElementException] if the selector resolves to no columns.
 * @throws [IllegalArgumentException] if the selector resolves to more than one column.
 */
public fun <T, C> ColumnsContainer<T>.getColumn(selector: ColumnSelector<T, C>): DataColumn<C> = get(selector)

// endregion

// region getColumnGroup

/**
 * Returns the top-level column at [<code>index</code>][index] as a [<code>ColumnGroup</code>][ColumnGroup], throwing if [<code>index</code>][index] is out of bounds.
 *
 * The index is the zero-based position of the column among the top-level columns.
 *
 * The `getColumnGroup` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], by a zero-based column index (`Int`),
 * or by a [<code>column selector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector].
 * Only the selector overload returns a typed [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]; the others return [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]`<*>`.
 * They throw if the column doesn't exist, and they don't check the kind of the column:
 * a column that isn't a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] leads to a [<code>ClassCastException</code>][ClassCastException].
 *
 * For more information: [See `getColumnGroup` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html#getcolumngroup)
 *
 * See also [<code>getColumnGroupOrNull</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroupOrNull], that returns `null` instead of throwing if there is no such column,
 * [<code>getColumn</code>][org.jetbrains.kotlinx.dataframe.api.getColumn], that returns a column of any kind,
 * and [<code>getFrameColumn</code>][org.jetbrains.kotlinx.dataframe.api.getFrameColumn], that returns the column as a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumnGroup("name") // ColumnGroup<*>
 * df.getColumnGroup(0) // ColumnGroup<*>: "name"
 * df.getColumnGroup(pathOf("name")) // ColumnGroup<*>
 * df.getColumnGroup { name } // ColumnGroup<Name>
 * df.getColumnGroupOrNull("address") // null: there is no such column
 * ```
 *
 * @param [index] The zero-based index of the top-level column.
 * @return The column at [<code>index</code>][index] as a [<code>ColumnGroup</code>][ColumnGroup]`<*>`.
 * @throws [IllegalArgumentException] if [<code>index</code>][index] is negative or not less than the number of top-level columns.
 * @throws [ClassCastException] if the column at [<code>index</code>][index] is not a [<code>ColumnGroup</code>][ColumnGroup].
 */
public fun <T> ColumnsContainer<T>.getColumnGroup(index: Int): ColumnGroup<*> = getColumn(index).asColumnGroup()

/**
 * Returns the top-level column named [<code>name</code>][name] as a [<code>ColumnGroup</code>][ColumnGroup], throwing if there is no such column.
 *
 * The name is looked up among the top-level columns only.
 * To get a nested column, use a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] or a column selector instead.
 *
 * The `getColumnGroup` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], by a zero-based column index (`Int`),
 * or by a [<code>column selector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector].
 * Only the selector overload returns a typed [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]; the others return [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]`<*>`.
 * They throw if the column doesn't exist, and they don't check the kind of the column:
 * a column that isn't a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] leads to a [<code>ClassCastException</code>][ClassCastException].
 *
 * For more information: [See `getColumnGroup` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html#getcolumngroup)
 *
 * See also [<code>getColumnGroupOrNull</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroupOrNull], that returns `null` instead of throwing if there is no such column,
 * [<code>getColumn</code>][org.jetbrains.kotlinx.dataframe.api.getColumn], that returns a column of any kind,
 * and [<code>getFrameColumn</code>][org.jetbrains.kotlinx.dataframe.api.getFrameColumn], that returns the column as a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumnGroup("name") // ColumnGroup<*>
 * df.getColumnGroup(0) // ColumnGroup<*>: "name"
 * df.getColumnGroup(pathOf("name")) // ColumnGroup<*>
 * df.getColumnGroup { name } // ColumnGroup<Name>
 * df.getColumnGroupOrNull("address") // null: there is no such column
 * ```
 *
 * @param [name] The name of the top-level column.
 * @return The column named [<code>name</code>][name] as a [<code>ColumnGroup</code>][ColumnGroup]`<*>`.
 * @throws [IllegalArgumentException] if there is no top-level column named [<code>name</code>][name].
 * @throws [ClassCastException] if the column named [<code>name</code>][name] is not a [<code>ColumnGroup</code>][ColumnGroup].
 */
public fun <T> ColumnsContainer<T>.getColumnGroup(name: String): ColumnGroup<*> = getColumn(name).asColumnGroup()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnsContainer<T>.getColumnGroup(column: KProperty<*>): ColumnGroup<*> =
    getColumnGroup(column.columnName)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> ColumnsContainer<T>.getColumnGroup(column: ColumnReference<DataRow<C>>): ColumnGroup<C> =
    getColumn(column)

/**
 * Returns the single [<code>column group</code>][ColumnGroup] selected by [<code>column</code>][column] as a typed [<code>ColumnGroup</code>][ColumnGroup],
 * throwing if [<code>column</code>][column] doesn't resolve to exactly one column.
 *
 * The column is selected with a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector] lambda in the [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl].
 * It can be a nested column, and its type is inferred from the selector.
 *
 * The `getColumnGroup` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], by a zero-based column index (`Int`),
 * or by a [<code>column selector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector].
 * Only the selector overload returns a typed [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]; the others return [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]`<*>`.
 * They throw if the column doesn't exist, and they don't check the kind of the column:
 * a column that isn't a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] leads to a [<code>ClassCastException</code>][ClassCastException].
 *
 * For more information: [See `getColumnGroup` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html#getcolumngroup)
 *
 * See also [<code>getColumnGroupOrNull</code>][org.jetbrains.kotlinx.dataframe.api.getColumnGroupOrNull], that returns `null` instead of throwing if there is no such column,
 * [<code>getColumn</code>][org.jetbrains.kotlinx.dataframe.api.getColumn], that returns a column of any kind,
 * and [<code>getFrameColumn</code>][org.jetbrains.kotlinx.dataframe.api.getFrameColumn], that returns the column as a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumnGroup("name") // ColumnGroup<*>
 * df.getColumnGroup(0) // ColumnGroup<*>: "name"
 * df.getColumnGroup(pathOf("name")) // ColumnGroup<*>
 * df.getColumnGroup { name } // ColumnGroup<Name>
 * df.getColumnGroupOrNull("address") // null: there is no such column
 * ```
 *
 * @param [column] A [<code>ColumnSelector</code>][ColumnSelector] that selects exactly one column group.
 * @return The selected column as a [<code>ColumnGroup</code>][ColumnGroup]`<C>`.
 * @throws [IllegalStateException] if the selector refers to a column that doesn't exist.
 * @throws [NoSuchElementException] if the selector resolves to no columns.
 * @throws [IllegalArgumentException] if the selector resolves to more than one column.
 * @throws [ClassCastException] if the selected column is not a [<code>ColumnGroup</code>][ColumnGroup].
 */
public fun <T, C> ColumnsContainer<T>.getColumnGroup(column: ColumnSelector<T, DataRow<C>>): ColumnGroup<C> =
    get(column).asColumnGroup()

// endregion

// region getColumnGroupOrNull

/**
 * Returns the top-level column named [<code>name</code>][name] as a [<code>ColumnGroup</code>][ColumnGroup], or `null` if there is no such column.
 *
 * The name is looked up among the top-level columns only.
 * To get a nested column, use a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] or a column selector instead.
 *
 * Only a missing column gives `null`:
 * an existing column that isn't a [<code>ColumnGroup</code>][ColumnGroup] still leads to a [<code>ClassCastException</code>][ClassCastException].
 *
 * For more information: [See `getColumnGroup` on the documentation website.](https://kotlin.github.io/dataframe/getcolumn.html#getcolumngroup)
 *
 * See also [<code>getColumnGroup</code>][getColumnGroup], that throws instead of returning `null`,
 * and [<code>getColumnOrNull</code>][ColumnsContainer.getColumnOrNull], that returns a column of any kind or `null`.
 *
 * ### Examples
 *
 * ```kotlin
 * df.getColumnGroup("name") // ColumnGroup<*>
 * df.getColumnGroup(0) // ColumnGroup<*>: "name"
 * df.getColumnGroup(pathOf("name")) // ColumnGroup<*>
 * df.getColumnGroup { name } // ColumnGroup<Name>
 * df.getColumnGroupOrNull("address") // null: there is no such column
 * ```
 *
 * @param [name] The name of the top-level column.
 * @return The column named [<code>name</code>][name] as a [<code>ColumnGroup</code>][ColumnGroup]`<*>`, or `null` if there is no such column.
 * @throws [ClassCastException] if the column named [<code>name</code>][name] exists but is not a [<code>ColumnGroup</code>][ColumnGroup].
 */
public fun <T> ColumnsContainer<T>.getColumnGroupOrNull(name: String): ColumnGroup<*>? =
    getColumnOrNull(name)?.asColumnGroup()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnsContainer<T>.getColumnGroupOrNull(column: KProperty<*>): ColumnGroup<*>? =
    getColumnGroupOrNull(column.columnName)

// endregion

// region containsColumn

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <C> ColumnsContainer<*>.containsColumn(column: ColumnReference<C>): Boolean = getColumnOrNull(column) != null

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun ColumnsContainer<*>.containsColumn(column: KProperty<*>): Boolean = containsColumn(column.columnName)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public operator fun ColumnsContainer<*>.contains(column: ColumnReference<*>): Boolean = containsColumn(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public operator fun ColumnsContainer<*>.contains(column: KProperty<*>): Boolean = containsColumn(column)

// region rows

/**
 * Returns a lazy [<code>Iterable</code>][Iterable] over the [<code>rows</code>][DataRow] of this [<code>DataFrame</code>][DataFrame], from the first row to the last one.
 *
 * The iteration is lazy: no rows are copied,
 * and each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] is created only when the iterator reaches it.
 * Each call to [<code>iterator</code>][Iterable.iterator] starts a new iteration.
 *
 * For more information: [See `rows` on the documentation website.](https://kotlin.github.io/dataframe/rows.html)
 *
 * See also [<code>rows</code>][org.jetbrains.kotlinx.dataframe.api.rows] and [<code>rowsReversed</code>][org.jetbrains.kotlinx.dataframe.api.rowsReversed], that iterate in the forward and the reverse order,
 * [<code>forEach</code>][org.jetbrains.kotlinx.dataframe.api.forEach], that runs an action for each row,
 * and [<code>asSequence</code>][org.jetbrains.kotlinx.dataframe.api.asSequence], that returns the rows as a [<code>Sequence</code>][Sequence].
 *
 * ### Examples
 *
 * ```kotlin
 * df.rows().map { it.age } // [15, 45, 20, ..., 18]: from the first row to the last one
 * df.rowsReversed().map { it.age } // [18, 15, 30, ..., 15]: from the last row to the first one
 * ```
 *
 * @return An [<code>Iterable</code>][Iterable] over the rows of this [<code>DataFrame</code>][DataFrame] in their order.
 */
@RequiredByIntellijPlugin
public fun <T> DataFrame<T>.rows(): Iterable<DataRow<T>> =
    object : Iterable<DataRow<T>> {
        override fun iterator() =
            object : Iterator<DataRow<T>> {
                var nextRow = 0

                override fun hasNext(): Boolean = nextRow < nrow

                override fun next(): DataRow<T> {
                    require(nextRow < nrow)
                    return get(nextRow++)
                }
            }
    }

/**
 * Returns a lazy [<code>Iterable</code>][Iterable] over the [<code>rows</code>][DataRow] of this [<code>DataFrame</code>][DataFrame] in the reverse order,
 * from the last row to the first one.
 *
 * Unlike [<code>reverse</code>][reverse], it doesn't create a new [<code>DataFrame</code>][DataFrame].
 *
 * The iteration is lazy: no rows are copied,
 * and each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] is created only when the iterator reaches it.
 * Each call to [<code>iterator</code>][Iterable.iterator] starts a new iteration.
 *
 * For more information: [See `rows` on the documentation website.](https://kotlin.github.io/dataframe/rows.html)
 *
 * See also [<code>rows</code>][org.jetbrains.kotlinx.dataframe.api.rows] and [<code>rowsReversed</code>][org.jetbrains.kotlinx.dataframe.api.rowsReversed], that iterate in the forward and the reverse order,
 * [<code>forEach</code>][org.jetbrains.kotlinx.dataframe.api.forEach], that runs an action for each row,
 * and [<code>asSequence</code>][org.jetbrains.kotlinx.dataframe.api.asSequence], that returns the rows as a [<code>Sequence</code>][Sequence].
 *
 * ### Examples
 *
 * ```kotlin
 * df.rows().map { it.age } // [15, 45, 20, ..., 18]: from the first row to the last one
 * df.rowsReversed().map { it.age } // [18, 15, 30, ..., 15]: from the last row to the first one
 * ```
 *
 * @return An [<code>Iterable</code>][Iterable] over the rows of this [<code>DataFrame</code>][DataFrame] in the reverse order.
 */
public fun <T> DataFrame<T>.rowsReversed(): Iterable<DataRow<T>> =
    object : Iterable<DataRow<T>> {
        override fun iterator() =
            object : Iterator<DataRow<T>> {
                var nextRow = nrow - 1

                override fun hasNext(): Boolean = nextRow >= 0

                override fun next(): DataRow<T> {
                    require(nextRow >= 0)
                    return get(nextRow--)
                }
            }
    }

// endregion
