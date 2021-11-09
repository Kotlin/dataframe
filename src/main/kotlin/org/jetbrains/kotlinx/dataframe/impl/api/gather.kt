package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Many
import org.jetbrains.kotlinx.dataframe.api.GatherClause
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toMany
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnMany
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

@PublishedApi
internal fun <T, C, K, R> gatherImpl(
    clause: GatherClause<T, C, K, R>,
    namesTo: String,
    valuesTo: String? = null,
    keyColumnType: KType,
    valueColumnType: KType
): DataFrame<T> {
    val removed = clause.df.removeImpl(clause.columns)

    val columnsToGather = removed.removedColumns.map { it.data.column as DataColumn<C> }

    val isGatherGroups = columnsToGather.any { it.isColumnGroup() }
    if (isGatherGroups && columnsToGather.any { !it.isColumnGroup() }) {
        throw UnsupportedOperationException("Cannot mix ColumnGroups with other types of columns in 'gather' operation")
    }

    val keys = columnsToGather.map { clause.nameTransform(it.name()) }

    val namesColumn = columnMany<K>(namesTo)
    val valuesColumn = columnMany<Any?>(valuesTo ?: "newValues")

    var df = removed.df

    var filter = clause.filter
    if (clause.dropNulls && columnsToGather.any { it.hasNulls() }) {
        if (filter == null) filter = { it != null }
        else {
            val oldFilter = filter
            filter = { it != null && oldFilter(it) }
        }
    }

    val valueTransform = clause.valueTransform

    if (filter == null) {
        // optimization when no filter is applied
        val wrappedKeys = keys.toMany()
        df = df.add { // add columns for names and values
            namesColumn from { wrappedKeys }
            valuesColumn from { row ->
                columnsToGather.map { col ->
                    val value = col[row]
                    if (valueTransform != null) {
                        when {
                            value is Many<*> -> (value as Many<C>).map(valueTransform)
                            else -> valueTransform(value)
                        }
                    } else value
                }.toMany()
            }
        }.explode(namesColumn, valuesColumn) // expand collected names and values
            .explode(valuesColumn) // expand values in Many
    } else {
        val nameAndValue = column<Many<Pair<K, Any?>>>("nameAndValue")
        df = df.add(nameAndValue) { row ->
            columnsToGather.mapIndexedNotNull { colIndex, col ->
                val value = col[row]
                when {
                    value is Many<*> -> {
                        val filtered = (value as Many<C>).filter(filter).toMany()
                        keys[colIndex] to (valueTransform?.let { filtered.map(it).toMany() } ?: filtered)
                    }
                    filter(value) -> keys[colIndex] to (valueTransform?.invoke(value) ?: value)
                    else -> null
                }
            }.toMany()
        }

        df = df.explode { nameAndValue }

        val nameAndValuePairs = nameAndValue.cast<Pair<K, C>>()

        df = df.split { nameAndValuePairs }
            .with { listOf(it.first, it.second) }
            .into(namesColumn, valuesColumn)
            .explode(valuesColumn)
    }

    df = df.convert(namesColumn.name()).to(keyColumnType)

    val valuesCol = df[valuesColumn.name()]

    if (valuesTo == null) {
        // values column needs to be removed
        if (valuesCol.isColumnGroup()) {
            df = df.ungroup(valuesColumn.name())
        } else df = df.remove(valuesColumn.name())
    } else {
        if (!valuesCol.isFrameColumn() && valueColumnType.jvmErasure != Any::class) {
            df = df.convert(valuesColumn.name()).to(valueColumnType)
        }
    }

    return df
}
