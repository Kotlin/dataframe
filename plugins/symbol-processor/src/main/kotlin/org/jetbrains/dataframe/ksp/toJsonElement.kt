package org.jetbrains.dataframe.ksp

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

/**
 * Serializes data schema into a human-readable JSON format.
 * Input of compiler plugin for "imported data schema" feature
 */
fun DataFrameSchema.toJsonString(
    json: Json = Json { prettyPrint = true },
    metadata: Map<String, JsonElement> = emptyMap(),
): String = json.encodeToString(JsonElement.serializer(), toJsonElement(metadata))

fun DataFrameSchema.toJsonElement(metadata: Map<String, JsonElement> = emptyMap()): JsonElement =
    buildJsonObject {
        metadata.forEach { (key, value) ->
            put(key, value)
        }

        put("schema", serializeSchema())
    }

fun DataFrameSchema.serializeSchema(): JsonElement = columns.serializeColumns()

private fun Map<String, ColumnSchema>.serializeColumns(): JsonObject =
    buildJsonObject {
        forEach { (columnName, columnSchema) ->
            val (element, suffix) = columnSchema.toJsonElement()
            val finalColumnName = suffix?.let { "$columnName$it" } ?: columnName
            put(finalColumnName, element)
        }
    }

fun ColumnSchema.toJsonElement(): Pair<JsonElement, String?> =
    when (this) {
        is ColumnSchema.Frame -> {
            schema.columns.serializeColumns() to ": FrameColumn"
        }

        is ColumnSchema.Group -> {
            schema.columns.serializeColumns() to ": ColumnGroup"
        }

        is ColumnSchema.Value -> {
            JsonPrimitive(type.toString()) to null
        }
    }
