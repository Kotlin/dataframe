package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Gather
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.column

internal fun <T, C, K, R> Gather<T, C, K, R>.gatherImpl(
    namesTo: String? = null,
    valuesTo: String? = null,
): DataFrame<T> {
    require(namesTo != null || valuesTo != null)

    val removed = df.removeImpl(columns = columns)

    val columnsToGather = removed.removedColumns.map { it.data.column as DataColumn<C> }

    val keys = columnsToGather.map { keyTransform(it.name()) }

    val keysColumn = namesTo?.let { column<List<K>>(it) }
    val valuesColumn = valuesTo?.let { column<List<Any?>>(it) }

    var df = removed.df

    val filter = filter
    val valueTransform = valueTransform

    // optimization when no filter is applied
    if (filter == null) {
        // add key and value columns
        df = df.add { // add columns for names and values
            if (keysColumn != null) {
                keysColumn from { keys }
            }
            if (valuesColumn != null) {
                valuesColumn from { row ->
                    columnsToGather.map { col ->
                        val value = col[row]
                        if (valueTransform != null) {
                            when {
                                explode && value is List<*> -> (value as List<C>).map(valueTransform)
                                else -> valueTransform(value)
                            }
                        } else value
                    }
                }
            }
        }

        // explode keys and values
        when {
            keysColumn != null && valuesColumn != null -> df = df.explode(keysColumn, valuesColumn)
            else -> df = df.explode(keysColumn ?: valuesColumn!!)
        }

        // explode values in lists
        if (explode && valuesColumn != null) {
            df = df.explode(valuesColumn)
        }
    } else {
        val nameAndValue = column<List<Pair<K, Any?>>>("nameAndValue")
        df = df.add(nameAndValue) { row ->
            columnsToGather.mapIndexedNotNull { colIndex, col ->
                val value = col[row]
                when {
                    explode && value is List<*> -> {
                        val filtered = (value as List<C>).filter(filter)
                        val transformed = valueTransform?.let { filtered.map(it) } ?: filtered
                        keys[colIndex] to transformed
                    }
                    filter(value) -> {
                        val transformed = valueTransform?.invoke(value) ?: value
                        keys[colIndex] to transformed
                    }
                    else -> null
                }
            }
        }

        df = df.explode { nameAndValue }

        if (df.isEmpty()) return df

        val nameAndValuePairs = nameAndValue.cast<Pair<*, *>>()

        when {
            keysColumn != null && valuesColumn != null -> {
                df = df.split { nameAndValuePairs }
                    .into(keysColumn.name(), valuesColumn.name())
                    .explode(valuesColumn)
            }
            keysColumn != null -> {
                df = df.replace { nameAndValuePairs }.with { it.map { it.first } named keysColumn.name() }
            }
            valuesColumn != null -> {
                df = df.replace { nameAndValuePairs }.with { it.map { it.second } named valuesColumn.name() }
            }
        }
    }

    if (keysColumn != null && keyType != null) {
        df = df.convert(keysColumn.name()).to(keyType)
    }

    return df
}
