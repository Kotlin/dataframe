package org.jetbrains.kotlinx.dataframe.io

import com.beust.klaxon.*
import org.jetbrains.kotlinx.dataframe.AnyColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Many
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toAnyFrame
import org.jetbrains.kotlinx.dataframe.api.toMany
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.splitByIndices
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.type
import java.io.File
import java.io.FileWriter
import java.net.URL
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType

public fun DataFrame.Companion.readJson(file: File): AnyFrame = readJson(file.toURI().toURL())

public fun DataFrame.Companion.readJson(path: String): AnyFrame {
    val url = when {
        isURL(path) -> URL(path).toURI()
        else -> File(path).toURI()
    }
    return readJson(url.toURL())
}

@Suppress("UNCHECKED_CAST")
public fun DataFrame.Companion.readJson(url: URL): AnyFrame =
    catchHttpResponse(url) { readJson(Parser.default().parse(it)) }

public fun DataFrame.Companion.readJsonStr(text: String): DataFrame<Any?> = readJson(Parser.default().parse(StringBuilder(text)))

private fun readJson(parsed: Any?) = when (parsed) {
    is JsonArray<*> -> fromJsonList(parsed.value)
    else -> fromJsonList(listOf(parsed))
}

private val arrayColumnName = "array"

internal val valueColumnName = "value"

internal fun fromJsonList(records: List<*>): AnyFrame {
    fun AnyFrame.isSingleUnnamedColumn() = ncol == 1 && col(0).name.let { it == org.jetbrains.kotlinx.dataframe.io.valueColumnName || it == org.jetbrains.kotlinx.dataframe.io.arrayColumnName }

    var hasPrimitive = false
    var hasArray = false
    // list element type can be JsonObject, JsonArray or primitive
    val nameGenerator = ColumnNameGenerator()
    records.forEach {
        when (it) {
            is JsonObject -> it.entries.forEach {
                nameGenerator.addIfAbsent(it.key)
            }
            is JsonArray<*> -> hasArray = true
            null -> {}
            else -> hasPrimitive = true
        }
    }

    val valueColumn = if (hasPrimitive) {
        nameGenerator.addUnique(valueColumnName)
    } else valueColumnName

    val arrayColumn = if (hasArray) {
        nameGenerator.addUnique(arrayColumnName)
    } else arrayColumnName

    val columns: List<AnyColumn> = nameGenerator.names.map { colName ->
        when {
            colName == valueColumn -> {
                val collector = createDataCollector(records.size)
                records.forEach {
                    when (it) {
                        is JsonObject -> collector.add(null)
                        is JsonArray<*> -> collector.add(null)
                        else -> collector.add(it)
                    }
                }
                collector.toColumn(colName)
            }
            colName == arrayColumn -> {
                val values = mutableListOf<Any?>()
                val startIndices = ArrayList<Int>()
                records.forEach {
                    startIndices.add(values.size)
                    if (it is JsonArray<*>) values.addAll(it.value)
                }
                val parsed = fromJsonList(values)
                when {
                    parsed.isSingleUnnamedColumn() -> {
                        val col = parsed.col(0)
                        val elementType = col.type
                        val values = col.values.asList().splitByIndices(startIndices.asSequence()).toMany()
                        DataColumn.createValueColumn(colName, values, Many::class.createType(listOf(KTypeProjection.invariant(elementType))))
                    }
                    else -> DataColumn.createFrameColumn(colName, parsed, startIndices, false)
                }
            }
            else -> {
                val values = ArrayList<Any?>(records.size)

                records.forEach {
                    when (it) {
                        is JsonObject -> values.add(it[colName])
                        else -> values.add(null)
                    }
                }

                val parsed = fromJsonList(values)
                when {
                    parsed.ncol == 0 -> DataColumn.createValueColumn(colName, arrayOfNulls<Any?>(values.size).toList(), getType<Any?>())
                    parsed.isSingleUnnamedColumn() -> parsed.col(0).rename(colName)
                    else -> DataColumn.createColumnGroup(colName, parsed)
                }
            }
        }
    }
    if (columns.isEmpty()) return DataFrame.empty(records.size)
    return columns.toAnyFrame()
}

internal fun KlaxonJson.encodeRow(frame: ColumnsContainer<*>, index: Int): JsonObject? {
    val values = frame.columns().mapNotNull { col ->
        when (col) {
            is ColumnGroup<*> -> encodeRow(col, index)
            is FrameColumn<*> -> col[index]?.let { encodeFrame(it) }
            else -> col[index]?.toString()
        }?.let { col.name to it }
    }
    if (values.isEmpty()) return null
    return obj(values)
}

internal fun KlaxonJson.encodeFrame(frame: AnyFrame): JsonArray<*> {
    val allColumns = frame.columns()

    val valueColumn = allColumns.filter { it.name.startsWith(valueColumnName) }
        .maxByOrNull { it.name }?.let { valueCol ->
            if (valueCol.kind() != ColumnKind.Value) null
            else {
                // check that value in this column is not null only when other values are null
                val isValidValueColumn = frame.rows().all { row ->
                    if (valueCol[row] != null) {
                        allColumns.all { col ->
                            if (col.name != valueCol.name) col[row] == null
                            else true
                        }
                    } else true
                }
                if (isValidValueColumn) valueCol
                else null
            }
        }

    val arrayColumn = frame.columns().filter { it.name.startsWith(arrayColumnName) }
        .maxByOrNull { it.name }?.let { arrayCol ->
            if (arrayCol.kind() == ColumnKind.Group) null
            else {
                // check that value in this column is not null only when other values are null
                val isValidArrayColumn = frame.rows().all { row ->
                    if (arrayCol[row] != null) {
                        allColumns.all { col ->
                            if (col.name != arrayCol.name) col[row] == null
                            else true
                        }
                    } else true
                }
                if (isValidArrayColumn) arrayCol
                else null
            }
        }

    val arraysAreFrames = arrayColumn?.kind() == ColumnKind.Frame

    val data = frame.indices().map { rowIndex ->
        valueColumn?.get(rowIndex) ?: arrayColumn?.get(rowIndex)?.let { if (arraysAreFrames) encodeFrame(it as AnyFrame) else null } ?: encodeRow(frame, rowIndex)
    }
    return array(data)
}

public fun AnyFrame.writeJsonStr(prettyPrint: Boolean = false, canonical: Boolean = false): String {
    return json {
        encodeFrame(this@writeJsonStr)
    }.toJsonString(prettyPrint, canonical)
}

public fun AnyFrame.writeJson(file: File, prettyPrint: Boolean = false, canonical: Boolean = false) {
    FileWriter(file).write(writeJsonStr(prettyPrint, canonical))
}

public fun AnyFrame.writeJson(path: String, prettyPrint: Boolean = false, canonical: Boolean = false): Unit = writeJson(File(path), prettyPrint, canonical)
