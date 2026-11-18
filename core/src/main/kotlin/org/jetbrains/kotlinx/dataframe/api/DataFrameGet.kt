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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.getColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.AS_COLUMN_GROUP_IMPORT
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.GET_COLUMN_GROUP_OR_NULL
import org.jetbrains.kotlinx.dataframe.util.GET_COLUMN_GROUP_OR_NULL_REPLACE
import kotlin.reflect.KProperty

// region docs

/**
 * The name is looked up among the top-level columns only.
 * To get a nested column, use a [ColumnPath] or a [column selector][ColumnSelector] instead.
 */
@ExcludeFromSources
private typealias ByTopLevelNameSnippet = Nothing

/**
 * The [ColumnPath] is a chain of column names that leads through [column groups][ColumnGroup]
 * to a column at any depth, like [pathOf]`("name", "firstName")`.
 */
@ExcludeFromSources
private typealias ByPathSnippet = Nothing

/** The index is the zero-based position of the column among the top-level columns. */
@ExcludeFromSources
private typealias ByIndexSnippet = Nothing

/**
 * The column is selected with a [ColumnSelector] lambda in the {@include [SelectingColumns.CSDslSingleLink]}.
 * It can be a nested column, and its type is inferred from the selector.
 */
@ExcludeFromSources
private typealias BySelectorSnippet = Nothing

/**
 * The columns are selected with a [ColumnsSelector] lambda in the {@include [SelectingColumns.CSDslLink]}.
 * They can be columns at any depth and are returned in the order they are selected.
 */
@ExcludeFromSources
private typealias ByColumnsSelectorSnippet = Nothing

/**
 * @throws [IllegalStateException] if the selector refers to a column that doesn't exist.
 * @throws [NoSuchElementException] if the selector resolves to no columns.
 * @throws [IllegalArgumentException] if the selector resolves to more than one column.
 */
@ExcludeFromSources
private typealias SingleColumnSelectorThrowsSnippet = Nothing

/**
 * The `getColumn` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [ColumnPath], by a zero-based column index (`Int`),
 * or by a [column selector][ColumnSelector].
 * Only the selector overload returns a typed [DataColumn]; the others return [DataColumn]`<*>`.
 * All of them throw if the column doesn't exist.
 *
 * For more information: {@include [DocumentationUrls.GetColumn]}
 *
 * See also:
 * - [getColumnOrNull][ColumnsContainer.getColumnOrNull], that returns `null` instead of throwing;
 * - [getColumnGroup] and [getFrameColumn], that return the column as a [ColumnGroup] or a [FrameColumn];
 * - [getColumns], that returns several columns.
 *
 * ### Examples
 *
 * $[GetColumnDocs.SAMPLE]
 */
@ExcludeFromSources
internal interface GetColumnDocs {
    typealias SAMPLE = Nothing
}

/**
 * The `getColumnGroup` overloads differ in how they find the column:
 * by a top-level column name (`String`), by a [ColumnPath], by a zero-based column index (`Int`),
 * or by a [column selector][ColumnSelector].
 * Only the selector overload returns a typed [ColumnGroup]; the others return [ColumnGroup]`<*>`.
 * They throw if the column doesn't exist, and they don't check the kind of the column:
 * a column that isn't a [ColumnGroup] leads to a [ClassCastException].
 *
 * For more information: {@include [DocumentationUrls.GetColumn.GetColumnGroup]}
 *
 * See also:
 * - [getColumnOrNull][ColumnsContainer.getColumnOrNull], that returns `null` instead of throwing;
 * - [getColumn], that returns a column of any kind;
 * - [getFrameColumn], that returns the column as a [FrameColumn].
 *
 * ### Examples
 *
 * $[GetColumnGroupDocs.SAMPLE]
 */
@ExcludeFromSources
internal interface GetColumnGroupDocs {
    typealias SAMPLE = Nothing
}

/**
 * The `getFrameColumn` overloads find the column by a top-level column name (`String`), a [ColumnPath],
 * a zero-based column index (`Int`), or a [column selector][ColumnSelector].
 * Only the selector overload returns a typed [FrameColumn]; the others return [FrameColumn]`<*>`.
 * They throw if the column doesn't exist, and they don't check the kind of the column:
 * a column that isn't a [FrameColumn] leads to a [ClassCastException].
 *
 * For more information: {@include [DocumentationUrls.GetColumn]}
 *
 * See also:
 * - [getColumnOrNull][ColumnsContainer.getColumnOrNull], that returns `null` instead of throwing;
 * - [getColumn], that returns a column of any kind;
 * - [getColumnGroup], that returns the column as a [ColumnGroup].
 *
 * ### Examples
 *
 * $[GetFrameColumnDocs.SAMPLE]
 */
@ExcludeFromSources
internal interface GetFrameColumnDocs {
    typealias SAMPLE = Nothing
}

/**
 * For more information: {@include [DocumentationUrls.GetColumn.GetColumns]}
 *
 * See also:
 * - [getColumn], that returns a single column;
 * - [getColumnsWithPaths], that also returns the [path][ColumnPath] of each column;
 * - [select], that returns the selected columns as a new [DataFrame].
 *
 * ### Examples
 *
 * $[GetColumnsDocs.SAMPLE]
 */
@ExcludeFromSources
internal interface GetColumnsDocs {
    typealias SAMPLE = Nothing
}

/**
 * A [ColumnPath] is the chain of column names from the top level of this [DataFrame] to the column,
 * through its parent [column groups][ColumnGroup], like [pathOf]`("name", "firstName")`.
 *
 * For more information: {@include [DocumentationUrls.ColumnSelectors]}
 *
 * See also $[GetColumnPathsDocs.SEE_ALSO]
 *
 * ### Examples
 *
 * $[GetColumnPathsDocs.SAMPLE]
 */
@ExcludeFromSources
internal interface GetColumnPathsDocs {
    typealias SAMPLE = Nothing
    typealias SEE_ALSO = Nothing
}

/**
 * For more information: {@include [DocumentationUrls.GetRow]}
 *
 * See also:
 * - [getOrNull], that returns a single row or `null`;
 * - [get][DataFrame.get], that returns a single row or several rows by their indices;
 * - [filter], that returns the rows matching a condition.
 *
 * ### Examples
 *
 * $[GetRowsDocs.SAMPLE]
 */
@ExcludeFromSources
internal interface GetRowsDocs {
    typealias SAMPLE = Nothing
}

/**
 * The iteration is lazy: no rows are copied,
 * and each [DataRow] is created only when the iterator reaches it.
 * Each call to [iterator][Iterable.iterator] starts a new iteration.
 *
 * For more information: {@include [DocumentationUrls.Rows]}
 *
 * See also $[RowsDocs.SEE_ALSO]
 *
 * ### Examples
 *
 * $[RowsDocs.SAMPLE]
 */
@ExcludeFromSources
internal interface RowsDocs {
    typealias SAMPLE = Nothing
    typealias SEE_ALSO = Nothing
}

// endregion

// region getColumnsWithPaths / getColumnPaths

/**
 * Returns the columns selected by [selector], each paired with its [ColumnPath], as a [List] of [ColumnWithPath].
 *
 * @include [ByColumnsSelectorSnippet]
 *
 * {@set [GetColumnPathsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnsWithPathsBySelector]}
 * {@set [GetColumnPathsDocs.SEE_ALSO] [getColumnPath] and [getColumnPaths], which return only the paths; [getColumnWithPath], which returns one column with its path; and [getColumns], which returns only the columns.}
 * @include [GetColumnPathsDocs]
 *
 * @param [selector] A [ColumnsSelector] that selects the columns.
 * @return A [List] of [ColumnWithPath], one for each selected column, in the order they are selected.
 * @throws [IllegalStateException] if [selector] refers to a column that doesn't exist.
 */
public fun <T, C> DataFrame<T>.getColumnsWithPaths(selector: ColumnsSelector<T, C>): List<ColumnWithPath<C>> =
    getColumnsWithPaths(UnresolvedColumnsPolicy.Fail, selector)

/**
 * Returns the [ColumnPath] of the single column selected by [selector],
 * throwing if [selector] doesn't resolve to exactly one column.
 *
 * @include [BySelectorSnippet]
 *
 * {@set [GetColumnPathsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnPathBySelector]}
 * {@set [GetColumnPathsDocs.SEE_ALSO] [getColumnPaths], which returns paths for multiple columns; [getColumnWithPath] and [getColumnsWithPaths], which return columns together with their paths; and [getColumns], which returns only the columns.}
 * @include [GetColumnPathsDocs]
 *
 * @param [selector] A [ColumnSelector] that selects exactly one column.
 * @return The [ColumnPath] of the selected column.
 * @include [SingleColumnSelectorThrowsSnippet]
 */
public fun <T, C> DataFrame<T>.getColumnPath(selector: ColumnSelector<T, C>): ColumnPath =
    getColumnPaths(selector).single()

/**
 * Returns the [ColumnPath]s of the columns selected by [selector], in the order they are selected.
 *
 * @include [ByColumnsSelectorSnippet]
 *
 * {@set [GetColumnPathsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnPathsBySelector]}
 * {@set [GetColumnPathsDocs.SEE_ALSO] [getColumnPath], which returns a single path; [getColumnWithPath] and [getColumnsWithPaths], which return columns together with their paths; and [getColumns], which returns only the columns.}
 * @include [GetColumnPathsDocs]
 *
 * @param [selector] A [ColumnsSelector] that selects the columns.
 * @return A [List] of [ColumnPath], one for each selected column, in the order they are selected.
 * @throws [IllegalStateException] if [selector] refers to a column that doesn't exist.
 */
public fun <T, C> DataFrame<T>.getColumnPaths(selector: ColumnsSelector<T, C>): List<ColumnPath> =
    getColumnPaths(UnresolvedColumnsPolicy.Fail, selector)

/**
 * Returns the single column selected by [selector], paired with its [ColumnPath] as a [ColumnWithPath],
 * throwing if [selector] doesn't resolve to exactly one column.
 *
 * @include [BySelectorSnippet]
 *
 * {@set [GetColumnPathsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnWithPathBySelector]}
 * {@set [GetColumnPathsDocs.SEE_ALSO] [getColumnPath] and [getColumnPaths], which return only the paths; [getColumnsWithPaths], which returns several columns together with their paths; and [getColumns], which returns only the columns.}
 * @include [GetColumnPathsDocs]
 *
 * @param [selector] A [ColumnSelector] that selects exactly one column.
 * @return A [ColumnWithPath] with the selected column and its [ColumnPath].
 * @include [SingleColumnSelectorThrowsSnippet]
 */
public fun <T, C> DataFrame<T>.getColumnWithPath(selector: ColumnSelector<T, C>): ColumnWithPath<C> =
    getColumnsWithPaths(selector).single()

// endregion

// region getColumns / getColumnIndex

/**
 * Returns the columns selected by [selector] as a [List] of [DataColumn], in the order they are selected.
 *
 * @include [ByColumnsSelectorSnippet]
 *
 * It is the same as [get][ColumnsContainer.get] with a [ColumnsSelector].
 *
 * {@set [GetColumnsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnsByNames_properties]}
 * @include [GetColumnsDocs]
 *
 * @param [selector] A [ColumnsSelector] that selects the columns.
 * @return A [List] with the selected columns, in the order they are selected.
 * @throws [IllegalStateException] if [selector] refers to a column that doesn't exist.
 */
public fun <T, C> DataFrame<T>.getColumns(selector: ColumnsSelector<T, C>): List<DataColumn<C>> = get(selector)

/**
 * Returns the top-level columns with the given names as a [List] of [DataColumn], in the order of [columns].
 *
 * @include [ByTopLevelNameSnippet]
 *
 * {@set [GetColumnsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnsByNames_strings]}
 * @include [GetColumnsDocs]
 *
 * @param [columns] The names of the top-level columns to return.
 * @return A [List] with the columns, in the order of [columns].
 * @throws [IllegalStateException] if there is no top-level column with one of the given names.
 */
public fun <T> DataFrame<T>.getColumns(vararg columns: String): List<DataColumn<*>> =
    getColumns {
        columns.toColumnSet()
    }

/**
 * Returns the zero-based index of the top-level column with the same name as [col],
 * or `-1` if there is no such column; it doesn't throw.
 *
 * For more information: {@include [DocumentationUrls.GetColumn]}
 *
 * See also [getColumnIndex][ColumnsContainer.getColumnIndex], that takes the column name,
 * and [getColumn], that returns the column at an index.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnIndexByColumn]
 *
 * @param [col] The column whose name is looked up among the top-level columns.
 * @return The index of the top-level column named like [col], or `-1` if there is none.
 */
public fun <T> DataFrame<T>.getColumnIndex(col: DataColumn<*>): Int = getColumnIndex(col.name())

// endregion

// region getRows / getOrNull

/**
 * Returns a new [DataFrame] with the rows at the zero-based indices in [range],
 * throwing if any of them is out of bounds.
 * In case the [range] is exactly the same as the range of indices in this [DataFrame],
 * the function returns this [DataFrame] itself.
 *
 * It is the same as [get][DataFrame.get] with an [IntRange].
 *
 * {@set [GetRowsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetRowSamples.getRowsByRange]}
 * @include [GetRowsDocs]
 *
 * @param [range] The range of row indices to return.
 * @return A new [DataFrame] with the rows in [range].
 * @throws [IndexOutOfBoundsException] if [range] contains an index out of the bounds of this [DataFrame].
 */
public fun <T> DataFrame<T>.getRows(range: IntRange): DataFrame<T> = get(range)

/**
 * Returns a new [DataFrame] with the rows at the zero-based [indices], in the order of [indices],
 * throwing if any of them is out of bounds.
 *
 * The same index can occur several times; then the row is repeated.
 * It is the same as [get][DataFrame.get] with an [Iterable] of indices.
 *
 * {@set [GetRowsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetRowSamples.getRowsByIndices]}
 * @include [GetRowsDocs]
 *
 * @param [indices] The row indices to return.
 * @return A new [DataFrame] with the rows at [indices], in the order of [indices].
 * @throws [IndexOutOfBoundsException] if [indices] contains an index out of the bounds of this [DataFrame].
 */
public fun <T> DataFrame<T>.getRows(indices: Iterable<Int>): DataFrame<T> = get(indices)

/**
 * Returns the [row][DataRow] at the zero-based [index], or `null` if [index] is out of bounds.
 *
 * It is the safe counterpart of [get][DataFrame.get] with an [Int], which throws instead.
 *
 * For more information: {@include [DocumentationUrls.GetRow]}
 *
 * See also:
 *  - [get][DataFrame.get], [getRows] that return several rows and throw instead of returning `null`;
 *  - [firstOrNull], [lastOrNull], that return the first or the last row, or `null` if this [DataFrame] is empty.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetRowSamples.getOrNull]
 *
 * @param [index] The index of the row.
 * @return The [DataRow] at [index], or `null` if [index] is out of the bounds of this [DataFrame].
 */
public fun <T> DataFrame<T>.getOrNull(index: Int): DataRow<T>? = if (index !in 0..<nrow) null else get(index)

// endregion

// region getFrameColumn

/**
 * Returns the column at [columnPath] as a [FrameColumn], throwing if there is no such column.
 *
 * @include [ByPathSnippet]
 *
 * {@set [GetFrameColumnDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getFrameColumnByPath]}
 * @include [GetFrameColumnDocs]
 *
 * @param [columnPath] The [ColumnPath] of the column.
 * @return The column at [columnPath] as a [FrameColumn]`<*>`.
 * @throws [IllegalArgumentException] if there is no column at [columnPath].
 * @throws [ClassCastException] if the column at [columnPath] is not a [FrameColumn].
 */
public fun <T> ColumnsContainer<T>.getFrameColumn(columnPath: ColumnPath): FrameColumn<*> =
    get(columnPath).asAnyFrameColumn()

/**
 * Returns the top-level column at [index] as a [FrameColumn], throwing if [index] is out of bounds.
 *
 * @include [ByIndexSnippet]
 *
 * {@set [GetFrameColumnDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getFrameColumnByIndex]}
 * @include [GetFrameColumnDocs]
 *
 * @param [index] The zero-based index of the top-level column.
 * @return The column at [index] as a [FrameColumn]`<*>`.
 * @throws [IllegalArgumentException] if [index] is negative or not less than the number of top-level columns.
 * @throws [ClassCastException] if the column at [index] is not a [FrameColumn].
 */
public fun <T> ColumnsContainer<T>.getFrameColumn(index: Int): FrameColumn<*> = getColumn(index).asAnyFrameColumn()

/**
 * Returns the top-level column named [columnName] as a [FrameColumn], throwing if there is no such column.
 *
 * @include [ByTopLevelNameSnippet]
 *
 * {@set [GetFrameColumnDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getFrameColumnByName]}
 * @include [GetFrameColumnDocs]
 *
 * @param [columnName] The name of the top-level column.
 * @return The column named [columnName] as a [FrameColumn]`<*>`.
 * @throws [IllegalArgumentException] if there is no top-level column named [columnName].
 * @throws [ClassCastException] if the column named [columnName] is not a [FrameColumn].
 */
public fun <T> ColumnsContainer<T>.getFrameColumn(columnName: String): FrameColumn<*> =
    get(columnName).asAnyFrameColumn()

/**
 * Returns the single column selected by [selector] as a typed [FrameColumn],
 * throwing if [selector] doesn't resolve to exactly one column.
 *
 * @include [BySelectorSnippet]
 *
 * It is the same as [get][ColumnsContainer.get] with a [ColumnSelector].
 *
 * {@set [GetFrameColumnDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getFrameColumnBySelector_properties]}
 * @include [GetFrameColumnDocs]
 *
 * @param [selector] A [ColumnSelector] that selects exactly one column.
 * @return The selected column as a [FrameColumn]`<C>`.
 * @include [SingleColumnSelectorThrowsSnippet]
 * @throws [ClassCastException] if the selected column is not a [FrameColumn].
 */
public fun <T, C> ColumnsContainer<T>.getFrameColumn(selector: ColumnSelector<T, DataFrame<C>>): FrameColumn<C> =
    get(selector).asFrameColumn()

// endregion

// region getColumn

/**
 * Returns the top-level column named [name] as a [DataColumn], throwing if there is no such column.
 *
 * @include [ByTopLevelNameSnippet]
 *
 * {@set [GetColumnDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnByName_strings]}
 * @include [GetColumnDocs]
 *
 * @param [name] The name of the top-level column.
 * @return The column named [name] as a [DataColumn]`<*>`.
 * @throws [IllegalArgumentException] if there is no top-level column named [name].
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
 * Returns the column at [path] as a [DataColumn], throwing if there is no such column.
 *
 * @include [ByPathSnippet]
 *
 * {@set [GetColumnDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnByPath]}
 * @include [GetColumnDocs]
 *
 * @param [path] The [ColumnPath] of the column.
 * @return The column at [path] as a [DataColumn]`<*>`.
 * @throws [IllegalArgumentException] if there is no column at [path].
 */
public fun <T> ColumnsContainer<T>.getColumn(path: ColumnPath): DataColumn<*> =
    getColumnOrNull(path) ?: throw IllegalArgumentException("Column not found: '$path'")

/**
 * Returns the top-level column at [index] as a [DataColumn], throwing if [index] is out of bounds.
 *
 * @include [ByIndexSnippet]
 *
 * {@set [GetColumnDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnByIndex]}
 * @include [GetColumnDocs]
 *
 * @param [index] The zero-based index of the top-level column.
 * @return The column at [index] as a [DataColumn]`<*>`.
 * @throws [IllegalArgumentException] if [index] is negative or not less than the number of top-level columns.
 */
public fun <T> ColumnsContainer<T>.getColumn(index: Int): DataColumn<*> =
    getColumnOrNull(index)
        ?: throw IllegalArgumentException("Column index is out of bounds: $index. Columns count = $ncol")

/**
 * Returns the single column selected by [selector] as a typed [DataColumn],
 * throwing if [selector] doesn't resolve to exactly one column.
 *
 * @include [BySelectorSnippet]
 *
 * It is the same as [get][ColumnsContainer.get] with a [ColumnSelector].
 *
 * {@set [GetColumnDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnByName_properties]}
 * @include [GetColumnDocs]
 *
 * @param [selector] A [ColumnSelector] that selects exactly one column.
 * @return The selected column as a [DataColumn]`<C>`.
 * @include [SingleColumnSelectorThrowsSnippet]
 */
public fun <T, C> ColumnsContainer<T>.getColumn(selector: ColumnSelector<T, C>): DataColumn<C> = get(selector)

// endregion

// region getColumnGroup

/**
 * Returns the column at [columnPath] as a [ColumnGroup], throwing if there is no such column.
 *
 * @include [ByPathSnippet]
 *
 * {@set [GetColumnGroupDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnGroupByPath]}
 * @include [GetColumnGroupDocs]
 *
 * @param [columnPath] The [ColumnPath] of the column.
 * @return The column at [columnPath] as a [ColumnGroup]`<*>`.
 * @throws [IllegalArgumentException] if there is no column at [columnPath].
 * @throws [ClassCastException] if the column at [columnPath] is not a [ColumnGroup].
 */
public fun <T> ColumnsContainer<T>.getColumnGroup(columnPath: ColumnPath): ColumnGroup<*> =
    get(columnPath).asColumnGroup()

/**
 * Returns the top-level column at [index] as a [ColumnGroup], throwing if [index] is out of bounds.
 *
 * @include [ByIndexSnippet]
 *
 * {@set [GetColumnGroupDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnGroupByIndex]}
 * @include [GetColumnGroupDocs]
 *
 * @param [index] The zero-based index of the top-level column.
 * @return The column at [index] as a [ColumnGroup]`<*>`.
 * @throws [IllegalArgumentException] if [index] is negative or not less than the number of top-level columns.
 * @throws [ClassCastException] if the column at [index] is not a [ColumnGroup].
 */
public fun <T> ColumnsContainer<T>.getColumnGroup(index: Int): ColumnGroup<*> = getColumn(index).asColumnGroup()

/**
 * Returns the top-level column named [name] as a [ColumnGroup], throwing if there is no such column.
 *
 * @include [ByTopLevelNameSnippet]
 *
 * {@set [GetColumnGroupDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnGroupByName_strings]}
 * @include [GetColumnGroupDocs]
 *
 * @param [name] The name of the top-level column.
 * @return The column named [name] as a [ColumnGroup]`<*>`.
 * @throws [IllegalArgumentException] if there is no top-level column named [name].
 * @throws [ClassCastException] if the column named [name] is not a [ColumnGroup].
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
 * Returns the single [column group][ColumnGroup] selected by [column] as a typed [ColumnGroup],
 * throwing if [column] doesn't resolve to exactly one column.
 *
 * @include [BySelectorSnippet]
 *
 * {@set [GetColumnGroupDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.GetColumnSamples.getColumnGroupByName_properties]}
 * @include [GetColumnGroupDocs]
 *
 * @param [column] A [ColumnSelector] that selects exactly one column group.
 * @return The selected column as a [ColumnGroup]`<C>`.
 * @include [SingleColumnSelectorThrowsSnippet]
 * @throws [ClassCastException] if the selected column is not a [ColumnGroup].
 */
public fun <T, C> ColumnsContainer<T>.getColumnGroup(column: ColumnSelector<T, DataRow<C>>): ColumnGroup<C> =
    get(column).asColumnGroup()

// endregion

// region getColumnGroupOrNull

@Deprecated(
    message = GET_COLUMN_GROUP_OR_NULL,
    replaceWith = ReplaceWith(GET_COLUMN_GROUP_OR_NULL_REPLACE, AS_COLUMN_GROUP_IMPORT),
    level = DeprecationLevel.WARNING,
)
public fun <T> ColumnsContainer<T>.getColumnGroupOrNull(name: String): ColumnGroup<*>? =
    getColumnOrNull(name)?.asColumnGroup()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnsContainer<T>.getColumnGroupOrNull(column: KProperty<*>): ColumnGroup<*>? =
    getColumnOrNull(column.columnName)?.asColumnGroup()

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

// endregion

// region rows

/**
 * Returns an [Iterable] over the [rows][DataRow] of this [DataFrame], from the first row to the last one.
 *
 * {@set [RowsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.RowsSamples.rows]}
 * {@set [RowsDocs.SEE_ALSO] [rowsReversed], which iterates rows in reverse order; [forEach], which runs an action for each row; and [asSequence], which returns the rows as a [Sequence].}
 * @include [RowsDocs]
 *
 * @return An [Iterable] over the rows of this [DataFrame] in their order.
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
 * Returns an [Iterable] over the [rows][DataRow] of this [DataFrame] in the reverse order,
 * from the last row to the first one.
 *
 * Unlike [reverse], it doesn't create a new [DataFrame].
 *
 * {@set [RowsDocs.SAMPLE] @sample [org.jetbrains.kotlinx.dataframe.samples.api.RowsSamples.rowsReversed]}
 * {@set [RowsDocs.SEE_ALSO] [rows], which iterates rows in forward order; [forEach], which runs an action for each row; and [asSequence], which returns the rows as a [Sequence].}
 * @include [RowsDocs]
 *
 * @return An [Iterable] over the rows of this [DataFrame] in the reverse order.
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

// region properties

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

// endregion
