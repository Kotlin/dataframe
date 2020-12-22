package org.jetbrains.dataframe.io

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.impl.createDataCollector
import java.io.File
import java.lang.StringBuilder
import java.net.URL
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType

fun readJson(fileOrUrl: String) = DataFrame.fromJson(fileOrUrl)

fun readJson(file: File) = DataFrame.fromJson(file)

fun parseJson(text: String) = DataFrame.fromJsonStr(text)

fun DataFrame.Companion.fromJson(file: File) = fromJson(file.toURI().toURL())

fun DataFrame.Companion.fromJson(fileOrUrl: String): DataFrame<*> {
    val url = when {
        isURL(fileOrUrl) -> URL(fileOrUrl).toURI()
        else -> File(fileOrUrl).toURI()
    }
    return fromJson(url.toURL())
}

@Suppress("UNCHECKED_CAST")
fun DataFrame.Companion.fromJson(url: URL): DataFrame<*> =
        fromJson(Parser().parse(url.openStream()))

fun DataFrame.Companion.fromJsonStr(text: String) = fromJson(Parser().parse(StringBuilder(text)))

private fun fromJson(parsed: Any?) = when (parsed) {
    is JsonArray<*> -> fromList(parsed.value)
    else -> fromList(listOf(parsed))
}

private val arrayColumnName = "array"

private val valueColumnName = "value"

internal fun fromList(records: List<*>): DataFrame<*> {

    fun DataFrame<*>.isSingleUnnamedColumn() = ncol == 1 && (columns[0].name == valueColumnName || columns[0].name == arrayColumnName)

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

    val valueColumn = if(hasPrimitive){
        nameGenerator.addUnique(valueColumnName)
    }else valueColumnName

    val arrayColumn = if(hasArray){
        nameGenerator.addUnique(arrayColumnName)
    }else arrayColumnName

    return nameGenerator.names.map { colName ->
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
                val parsed = fromList(values)
                when {
                    parsed.isSingleUnnamedColumn() -> {
                        val col = parsed.columns[0]
                        val elementType = col.type
                        val values = col.values.asList().splitByIndices(startIndices)
                        column(colName, values, List::class.createType(listOf(KTypeProjection.invariant(elementType))))
                    }
                    else -> ColumnData.createTable(colName, parsed, startIndices)
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

                val parsed = fromList(values)
                when {
                    parsed.isSingleUnnamedColumn() -> parsed.columns[0].doRename(colName)
                    else -> ColumnData.createGroup(colName, parsed)
                }
            }
        }
    }.asDataFrame<Unit>()
}

