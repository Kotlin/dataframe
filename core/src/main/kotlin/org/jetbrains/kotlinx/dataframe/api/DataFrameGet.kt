package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.getColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KProperty

public fun <T, C> DataFrame<T>.getColumnsWithPaths(selector: ColumnsSelector<T, C>): List<ColumnWithPath<C>> =
    getColumnsWithPaths(UnresolvedColumnsPolicy.Fail, selector)

public fun <T, C> DataFrame<T>.getColumnPath(selector: ColumnSelector<T, C>): ColumnPath =
    getColumnPaths(selector).single()

public fun <T, C> DataFrame<T>.getColumnPaths(selector: ColumnsSelector<T, C>): List<ColumnPath> =
    getColumnPaths(UnresolvedColumnsPolicy.Fail, selector)

public fun <T, C> DataFrame<T>.getColumnWithPath(selector: ColumnSelector<T, C>): ColumnWithPath<C> =
    getColumnsWithPaths(selector).single()

public fun <T, C> DataFrame<T>.getColumns(selector: ColumnsSelector<T, C>): List<DataColumn<C>> = get(selector)

public fun <T> DataFrame<T>.getColumns(vararg columns: String): List<AnyCol> = getColumns { columns.toColumnSet() }

public fun <T> DataFrame<T>.getColumnIndex(col: AnyCol): Int = getColumnIndex(col.name())

public fun <T> DataFrame<T>.getRows(range: IntRange): DataFrame<T> =
    if (range == indices()) this else columns().map { col -> col[range] }.toDataFrame().cast()

public fun <T> DataFrame<T>.getRows(indices: Iterable<Int>): DataFrame<T> =
    columns().map { col -> col[indices] }.toDataFrame().cast()

public fun <T> DataFrame<T>.getOrNull(index: Int): DataRow<T>? = if (index < 0 || index >= nrow) null else get(index)

public fun <T> ColumnsContainer<T>.getFrameColumn(columnPath: ColumnPath): FrameColumn<*> =
    get(columnPath).asAnyFrameColumn()

public fun <T> ColumnsContainer<T>.getFrameColumn(columnName: String): FrameColumn<*> =
    get(columnName).asAnyFrameColumn()

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

public fun <T> ColumnsContainer<T>.getColumn(name: String): AnyCol =
    getColumnOrNull(name) ?: throw IllegalArgumentException("Column not found: '$name'")

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.getColumn(column: ColumnReference<DataFrame<R>>): FrameColumn<R> =
    getColumnOrNull(column)?.asFrameColumn() ?: throw IllegalArgumentException("FrameColumn not found: '$column'")

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.getColumn(column: ColumnReference<DataRow<R>>): ColumnGroup<R> =
    getColumnOrNull(column)?.asColumnGroup() ?: throw IllegalArgumentException("ColumnGroup not found: '$column'")

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.getColumn(column: ColumnReference<R>): DataColumn<R> =
    getColumnOrNull(column) ?: throw IllegalArgumentException("Column not found: '$column'")

public fun <T> ColumnsContainer<T>.getColumn(path: ColumnPath): AnyCol =
    getColumnOrNull(path) ?: throw IllegalArgumentException("Column not found: '$path'")

public fun <T> ColumnsContainer<T>.getColumn(index: Int): AnyCol =
    getColumnOrNull(index)
        ?: throw IllegalArgumentException("Column index is out of bounds: $index. Columns count = $ncol")

public fun <T, C> ColumnsContainer<T>.getColumn(selector: ColumnSelector<T, C>): DataColumn<C> = get(selector)

// endregion

// region getColumnGroup

public fun <T> ColumnsContainer<T>.getColumnGroup(index: Int): ColumnGroup<*> = getColumn(index).asColumnGroup()

public fun <T> ColumnsContainer<T>.getColumnGroup(name: String): ColumnGroup<*> = getColumn(name).asColumnGroup()

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T> ColumnsContainer<T>.getColumnGroup(column: KProperty<*>): ColumnGroup<*> =
    getColumnGroup(column.columnName)

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> ColumnsContainer<T>.getColumnGroup(column: ColumnReference<DataRow<C>>): ColumnGroup<C> =
    getColumn(column)

public fun <T, C> ColumnsContainer<T>.getColumnGroup(column: ColumnSelector<T, DataRow<C>>): ColumnGroup<C> =
    get(column).asColumnGroup()

// endregion

// region getColumnGroupOrNull

public fun <T> ColumnsContainer<T>.getColumnGroupOrNull(name: String): ColumnGroup<*>? =
    getColumnOrNull(name)?.asColumnGroup()

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T> ColumnsContainer<T>.getColumnGroupOrNull(column: KProperty<*>): ColumnGroup<*>? =
    getColumnGroupOrNull(column.columnName)

// endregion

// region containsColumn

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <C> ColumnsContainer<*>.containsColumn(column: ColumnReference<C>): Boolean = getColumnOrNull(column) != null

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun ColumnsContainer<*>.containsColumn(column: KProperty<*>): Boolean = containsColumn(column.columnName)

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public operator fun ColumnsContainer<*>.contains(column: AnyColumnReference): Boolean = containsColumn(column)

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public operator fun ColumnsContainer<*>.contains(column: KProperty<*>): Boolean = containsColumn(column)

// region rows

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
