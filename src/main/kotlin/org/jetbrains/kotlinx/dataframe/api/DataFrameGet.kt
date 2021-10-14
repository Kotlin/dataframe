package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.asFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.impl.toIndices
import org.jetbrains.kotlinx.dataframe.resolve

public fun <T, C> DataFrame<T>.getColumnsWithPaths(selector: ColumnsSelector<T, C>): List<ColumnWithPath<C>> =
    getColumnsWithPaths(UnresolvedColumnsPolicy.Fail, selector)

public fun <T, C> DataFrame<T>.getColumnPath(selector: ColumnSelector<T, C>): ColumnPath = getColumnPaths(selector).single()
public fun <T, C> DataFrame<T>.getColumnPaths(selector: ColumnsSelector<T, C>): List<ColumnPath> =
    selector.toColumns().resolve(this, UnresolvedColumnsPolicy.Fail).map { it.path }

public fun <T, C> DataFrame<T>.column(selector: ColumnSelector<T, C>): DataColumn<C> = get(selector)
public fun <T> DataFrame<T>.col(predicate: ColumnFilter<Any?>): AnyCol = column { single(predicate) }
public fun <T, C> DataFrame<T>.getColumnWithPath(selector: ColumnSelector<T, C>): ColumnWithPath<C> = getColumnsWithPaths(selector).single()
public fun AnyFrame.getFrame(path: ColumnPath): AnyFrame = if (path.isNotEmpty()) this[path].asFrame() else this
public fun <T, C> DataFrame<T>.columns(selector: ColumnsSelector<T, C>): List<DataColumn<C>> = get(selector)
public fun <T> DataFrame<T>.getColumnIndex(col: AnyCol): Int = getColumnIndex(col.name())
public fun <T> DataFrame<T>.getRows(range: IntRange): DataFrame<T> = if (range == indices()) this else columns().map { col -> col.slice(range) }.toDataFrame()
public fun <T> DataFrame<T>.getRows(mask: BooleanArray): DataFrame<T> = getRows(mask.toIndices())
public fun <T> DataFrame<T>.getRows(indices: Iterable<Int>): DataFrame<T> = columns().map { col -> col.slice(indices) }.toDataFrame()
