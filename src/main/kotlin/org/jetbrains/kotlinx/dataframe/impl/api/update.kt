package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.UpdateClause
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.name
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

@PublishedApi
internal fun <T, C> updateImpl(clause: UpdateClause<T, C>, expression: (DataRow<T>, DataColumn<C>) -> Any?): DataFrame<T> {
    val removeResult = clause.df.removeImpl(clause.selector)

    val nrow = clause.df.nrow()
    val toInsert = removeResult.removedColumns.map {
        val srcColumn = it.data.column as DataColumn<C>
        val collector = when {
            clause.toNull -> createDataCollector(nrow, srcColumn.type)
            clause.typeSuggestions != null -> createDataCollector(nrow, clause.typeSuggestions)
            clause.targetType != null -> createDataCollector(nrow, clause.targetType)
            else -> createDataCollector(nrow, srcColumn.type())
        }
        if (clause.filter == null) {
            clause.df.forEach { row ->
                collector.add(expression(row, srcColumn))
            }
        } else {
            clause.df.forEach { row ->
                val currentValue = srcColumn[row.index]
                val newValue = if (clause.filter.invoke(row, currentValue)) expression(row, srcColumn) else currentValue
                collector.add(newValue)
            }
        }

        val newColumn = collector.toColumn(srcColumn.name())

        ColumnToInsert(it.pathFromRoot(), newColumn, it)
    }
    return removeResult.df.insertImpl(toInsert)
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
        DataColumn.create(name, groups, nulls) as DataColumn<T>
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
        }.toDataFrame<Unit>().let { DataColumn.create(name, it) } as DataColumn<T>
    }
    else -> {
        var nulls = false
        val kclass = type.jvmErasure
        values.forEach {
            when (it) {
                null -> nulls = true
                else -> {
                    require(it.javaClass.kotlin.isSubclassOf(kclass)) { "Can not append value '$it' to column '$name' of type $type" }
                }
            }
        }
        DataColumn.create(name, values, type.withNullability(nulls))
    }
}
