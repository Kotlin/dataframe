package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.resolve
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.impl.toIndices

public fun <T, C> DataFrame<T>.getColumnsWithPaths(selector: ColumnsSelector<T, C>): List<ColumnWithPath<C>> =
    getColumnsWithPaths(UnresolvedColumnsPolicy.Fail, selector)

public fun <T, C> DataFrame<T>.getColumnPath(selector: ColumnSelector<T, C>): ColumnPath = getColumnPaths(selector).single()
public fun <T, C> DataFrame<T>.getColumnPaths(selector: ColumnsSelector<T, C>): List<ColumnPath> =
    selector.toColumns().resolve(this, UnresolvedColumnsPolicy.Fail).map { it.path }

public fun <T, C> DataFrame<T>.column(selector: ColumnSelector<T, C>): DataColumn<C> = get(selector)
public fun <T> DataFrame<T>.col(predicate: (AnyCol) -> Boolean): AnyCol = columns().single(predicate)
public fun <T, C> DataFrame<T>.getColumnWithPath(selector: ColumnSelector<T, C>): ColumnWithPath<C> = getColumnsWithPaths(selector).single()
public fun <T, C> DataFrame<T>.columns(selector: ColumnsSelector<T, C>): List<DataColumn<C>> = get(selector)
public fun <T> DataFrame<T>.getColumnIndex(col: AnyCol): Int = getColumnIndex(col.name())
public fun <T> DataFrame<T>.getRows(range: IntRange): DataFrame<T> = if (range == indices()) this else columns().map { col -> col.slice(range) }.toDataFrame()
public fun <T> DataFrame<T>.getRows(mask: BooleanArray): DataFrame<T> = getRows(mask.toIndices())
public fun <T> DataFrame<T>.getRows(indices: Iterable<Int>): DataFrame<T> = columns().map { col -> col.slice(indices) }.toDataFrame()
public fun <T> DataFrame<T>.getOrNull(index: Int): DataRow<T>? = if (index < 0 || index >= nrow()) null else get(index)

public fun <T> ColumnsContainer<T>.frameColumn(columnPath: ColumnPath): FrameColumn<*> = get(columnPath).asFrameColumn()
public fun <T> ColumnsContainer<T>.frameColumn(columnName: String): FrameColumn<*> = get(columnName).asFrameColumn()
public fun <T> ColumnsContainer<T>.tryGetColumn(columnIndex: Int): AnyCol? = if (columnIndex in 0 until ncol()) col(columnIndex) else null
public fun <T> ColumnsContainer<T>.getColumnGroup(columnPath: ColumnPath): ColumnGroup<*> = get(columnPath).asColumnGroup()
public fun <T> ColumnsContainer<T>.getColumnGroup(columnName: String): ColumnGroup<*> = get(columnName).asColumnGroup()
