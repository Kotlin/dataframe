package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowCellFilter
import org.jetbrains.kotlinx.dataframe.api.UpdateClause
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.typed
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

@PublishedApi
internal fun <T, C> UpdateClause<T, C>.updateImpl(expression: (DataRow<T>, DataColumn<C>, C) -> C): DataFrame<T> = df.replace(selector).with { it.updateImpl(df, filter, expression) }

internal fun <T, C> DataColumn<C>.updateImpl(
    df: DataFrame<T>,
    filter: RowCellFilter<T, C>?,
    expression: (DataRow<T>, DataColumn<C>, C) -> C
): DataColumn<C> {
    val collector = createDataCollector<C>(size, type)
    val src = this
    if (filter == null) {
        df.forEach { row ->
            collector.add(expression(row, src, src[row.index]))
        }
    } else {
        df.forEach { row ->
            val currentValue = row[src]
            val newValue =
                if (filter.invoke(row, currentValue)) expression(row, src, currentValue) else currentValue
            collector.add(newValue)
        }
    }
    return collector.toColumn(src.name).typed()
}

/**
 * Replaces all values in column asserting that new values are compatible with current column kind
 */
internal fun <T> DataColumn<T>.updateWith(values: List<T>): DataColumn<T> = when (this) {
    is FrameColumn<*> -> {
        var nulls = false
        values.forEach {
            if (it == null) nulls = true
            else require(it is AnyFrame) { "Can not add value '$it' to FrameColumn" }
        }
        val groups = (values as List<AnyFrame?>)
        DataColumn.createFrameColumn(name, groups, nulls) as DataColumn<T>
    }
    is ColumnGroup<*> -> {
        this.columns().mapIndexed { colIndex, col ->
            val newValues = values.map {
                when (it) {
                    null -> null
                    is List<*> -> it[colIndex]
                    is AnyRow -> it.tryGet(col.name)
                    else -> require(false) { "Can not add value '$it' to MapColumn" }
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
