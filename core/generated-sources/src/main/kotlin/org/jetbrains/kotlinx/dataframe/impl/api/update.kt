package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnExpression
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.api.AddDataRow
import org.jetbrains.kotlinx.dataframe.api.Update
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.impl.columns.AddDataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

@PublishedApi
internal fun <T, C> Update<T, C>.updateImpl(expression: (AddDataRow<T>, DataColumn<C>, C) -> C?): DataFrame<T> =
    if (df.isEmpty()) df
    else df.replace(columns).with { it.updateImpl(df, filter, expression) }

internal fun <T, C> Update<T, C>.updateWithValuePerColumnImpl(selector: ColumnExpression<C, C>) =
    if (df.isEmpty()) df
    else {
        df.replace(columns).with {
            val value = selector(it, it)
            val convertedValue = value?.convertTo(it.type()) as C
            it.updateImpl(df, filter) { _, _, _ -> convertedValue }
        }
    }

/**
 * Implementation for Update As Frame:
 * Replaces selected column groups with the result of the expression only where the filter is true.
 */
internal fun <T, C, R> Update<T, DataRow<C>>.asFrameImpl(expression: DataFrameExpression<C, DataFrame<R>>): DataFrame<T> =
    if (df.isEmpty()) df
    else df.replace(columns).with {
        // First, we create an updated column group with the result of the expression
        val srcColumnGroup = it.asColumnGroup()
        val updatedColumnGroup = srcColumnGroup
            .asDataFrame()
            .let { expression(it, it) }
            .asColumnGroup(srcColumnGroup.name())

        if (filter == null) {
            // If there is no filter, we simply return the updated column group
            updatedColumnGroup
        } else {
            // If there is a filter, then we replace the rows of the source column group with the updated column group
            // only if they satisfy the filter
            srcColumnGroup.replaceRowsIf(from = updatedColumnGroup) {
                val srcRow = df[it.index]
                val srcValue = srcRow[srcColumnGroup]

                filter.invoke(srcRow, srcValue)
            }
        }
    }

private fun <C, R> ColumnGroup<C>.replaceRowsIf(
    from: ColumnGroup<R>,
    condition: (DataRow<C>) -> Boolean = { true },
): ColumnGroup<C> = values()
    .map { if (condition(it)) from[it.index] else it }
    .toColumn(name)
    .asColumnGroup()
    .cast()

internal fun <T, C> DataColumn<C>.updateImpl(
    df: DataFrame<T>,
    filter: RowValueFilter<T, C>?,
    expression: (AddDataRow<T>, DataColumn<C>, C) -> C?,
): DataColumn<C> {
    val collector = createDataCollector<C>(size, type)
    val src = this
    if (filter == null) {
        df.indices().forEach { rowIndex ->
            val row = AddDataRowImpl(rowIndex, df, collector.values)
            collector.add(expression(row, src, src[rowIndex]))
        }
    } else {
        df.indices().forEach { rowIndex ->
            val row = AddDataRowImpl(rowIndex, df, collector.values)
            val currentValue = row[src]
            val newValue =
                if (filter.invoke(row, currentValue)) expression(row, src, currentValue) else currentValue
            collector.add(newValue)
        }
    }
    return collector.toColumn(src.name).cast()
}

/**
 * Replaces all values in column asserting that new values are compatible with current column kind
 */
internal fun <T> DataColumn<T>.updateWith(values: List<T>): DataColumn<T> = when (this) {
    is FrameColumn<*> -> {
        values.forEach {
            require(it is AnyFrame) { "Can not add value '$it' to FrameColumn" }
        }
        val groups = (values as List<AnyFrame>)
        DataColumn.createFrameColumn(name, groups) as DataColumn<T>
    }

    is ColumnGroup<*> -> {
        this.columns().mapIndexed { colIndex, col ->
            val newValues = values.map {
                when (it) {
                    null -> null
                    is List<*> -> it[colIndex]
                    is AnyRow -> it.getOrNull(col.name)
                    else -> require(false) { "Can not add value '$it' to ColumnGroup" }
                }
            }
            col.updateWith(newValues)
        }.toDataFrame().let { DataColumn.createColumnGroup(name, it) } as DataColumn<T>
    }

    else -> {
        var nulls = false
        val kclass = type.jvmErasure
        values.forEach {
            when (it) {
                null -> nulls = true
                else -> {
                    require(it.javaClass.kotlin.isSubclassOf(kclass)) { "Can not add value '$it' to column '$name' of type $type" }
                }
            }
        }
        DataColumn.createValueColumn(name, values, type.withNullability(nulls))
    }
}
