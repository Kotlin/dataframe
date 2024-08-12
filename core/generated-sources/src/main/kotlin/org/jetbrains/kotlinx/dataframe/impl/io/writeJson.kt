@file:OptIn(ExperimentalSerializationApi::class)

package org.jetbrains.kotlinx.dataframe.impl.io

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.columns.CellKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.COLUMNS
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.DATA
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.KIND
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.KOTLIN_DATAFRAME
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.METADATA
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.NCOL
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.NROW
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.TYPE
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.TYPES
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.VERSION
import org.jetbrains.kotlinx.dataframe.io.ARRAY_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.io.Base64ImageEncodingOptions
import org.jetbrains.kotlinx.dataframe.io.VALUE_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.jupyter.KotlinNotebookPluginUtils
import org.jetbrains.kotlinx.dataframe.jupyter.KotlinNotebookPluginUtils.isDataframeConvertable
import org.jetbrains.kotlinx.dataframe.name
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.typeClass
import java.awt.image.BufferedImage
import java.io.IOException

// See docs/serialization_format.md for a description of
// serialization versions and format.
internal const val SERIALIZATION_VERSION = "2.1.0"

internal object SerializationKeys {
    const val DATA = "data"
    const val METADATA = "metadata"
    const val KIND = "kind"
    const val NCOL = "ncol"
    const val NROW = "nrow"
    const val VERSION = "\$version"
    const val COLUMNS = "columns"
    const val KOTLIN_DATAFRAME = "kotlin_dataframe"
    const val TYPE = "type"
    const val TYPES = "types"
}

private val valueTypes =
    setOf(Boolean::class, Double::class, Int::class, Float::class, Long::class, Short::class, Byte::class)

@OptIn(ExperimentalSerializationApi::class)
private fun convert(value: Any?): JsonElement =
    when (value) {
        is JsonElement -> value
        is Number -> JsonPrimitive(value)
        is String -> JsonPrimitive(value)
        is Char -> JsonPrimitive(value.toString())
        is Boolean -> JsonPrimitive(value)
        null -> JsonPrimitive(null)
        else -> JsonPrimitive(value.toString())
    }

internal fun encodeRow(frame: ColumnsContainer<*>, index: Int): JsonObject {
    val values: Map<String, JsonElement> = frame.columns().associate { col ->
        col.name to when {
            col is ColumnGroup<*> -> encodeRow(col, index)

            col is FrameColumn<*> -> encodeFrame(col[index])

            col.isList() -> {
                col[index]?.let {
                    JsonArray((it as List<*>).map { value -> convert(value) })
                } ?: JsonPrimitive(null)
            }

            col.typeClass in valueTypes -> {
                val v = col[index]
                convert(v)
            }

            else -> JsonPrimitive(col[index]?.toString())
        }
    }

    if (values.isEmpty()) return buildJsonObject { }
    return JsonObject(values)
}

internal fun encodeRowWithMetadata(
    frame: ColumnsContainer<*>,
    index: Int,
    rowLimit: Int? = null,
    imageEncodingOptions: Base64ImageEncodingOptions? = null,
): JsonElement? {
    val values: List<Pair<String, JsonElement>> = frame.columns().map { col ->
        when (col) {
            is ColumnGroup<*> -> {
                val schema = col.schema()
                buildJsonObject {
                    put(DATA, encodeRowWithMetadata(col, index, rowLimit, imageEncodingOptions) ?: JsonPrimitive(null))
                    putJsonObject(METADATA) {
                        put(KIND, JsonPrimitive(ColumnKind.Group.toString()))
                        put(COLUMNS, Json.encodeToJsonElement(schema.columns.keys))
                        putJsonArray(TYPES) {
                            addAll(
                                schema.columns.values.map { columnSchema ->
                                    createJsonTypeDescriptor(columnSchema)
                                },
                            )
                        }
                    }
                }
            }

            is FrameColumn<*> -> {
                val data = if (rowLimit == null) {
                    encodeFrameWithMetadata(col[index], null, imageEncodingOptions)
                } else {
                    encodeFrameWithMetadata(col[index].take(rowLimit), rowLimit, imageEncodingOptions)
                }
                val schema = col.schema.value
                buildJsonObject {
                    put(DATA, data)
                    putJsonObject(METADATA) {
                        put(KIND, JsonPrimitive(ColumnKind.Frame.toString()))
                        put(COLUMNS, Json.encodeToJsonElement(schema.columns.keys))
                        putJsonArray(TYPES) {
                            addAll(
                                schema.columns.values.map { columnSchema ->
                                    createJsonTypeDescriptor(columnSchema)
                                },
                            )
                        }
                        put(NCOL, JsonPrimitive(col[index].ncol))
                        put(NROW, JsonPrimitive(col[index].nrow))
                    }
                }
            }

            else -> encodeValue(col, index, imageEncodingOptions)
        }.let { col.name to it }
    }
    if (values.isEmpty()) return null
    return JsonObject(values.toMap())
}

internal fun encodeValue(
    col: AnyCol,
    index: Int,
    imageEncodingOptions: Base64ImageEncodingOptions? = null,
): JsonElement =
    when {
        isDataframeConvertable(col[index]) -> if (col[index] == null) {
            JsonPrimitive(null)
        } else {
            val data = encodeFrameWithMetadata(
                KotlinNotebookPluginUtils.convertToDataFrame(col[index]!!),
                null,
                imageEncodingOptions,
            )
            buildJsonObject {
                put(DATA, data)
                putJsonObject(METADATA) {
                    put(KIND, JsonPrimitive(CellKind.DataFrameConvertable.toString()))
                }
            }
        }

        col.isList() -> col[index]?.let { list ->
            val values = (list as List<*>).map { convert(it) }
            JsonArray(values)
        } ?: JsonArray(emptyList())

        col.typeClass in valueTypes -> convert(col[index])

        col.typeClass == BufferedImage::class && imageEncodingOptions != null ->
            col[index]?.let { image ->
                JsonPrimitive(encodeBufferedImageAsBase64(image as BufferedImage, imageEncodingOptions))
            } ?: JsonPrimitive("")

        else -> JsonPrimitive(col[index]?.toString())
    }

private fun encodeBufferedImageAsBase64(
    image: BufferedImage,
    imageEncodingOptions: Base64ImageEncodingOptions = Base64ImageEncodingOptions(),
): String? =
    try {
        val preparedImage = if (imageEncodingOptions.isLimitSizeOn) {
            image.resizeKeepingAspectRatio(imageEncodingOptions.imageSizeLimit)
        } else {
            image
        }

        val bytes = if (imageEncodingOptions.isGzipOn) {
            preparedImage.toByteArray().encodeGzip()
        } else {
            preparedImage.toByteArray()
        }

        bytes.toBase64()
    } catch (e: IOException) {
        null
    }

private fun createJsonTypeDescriptor(columnSchema: ColumnSchema): JsonObject =
    JsonObject(
        mutableMapOf(KIND to JsonPrimitive(columnSchema.kind.toString())).also {
            if (columnSchema.kind == ColumnKind.Value) {
                it[TYPE] = JsonPrimitive(columnSchema.type.toString())
            }
        },
    )

internal fun encodeFrameWithMetadata(
    frame: AnyFrame,
    rowLimit: Int? = null,
    imageEncodingOptions: Base64ImageEncodingOptions? = null,
): JsonArray {
    val valueColumn = frame.extractValueColumn()
    val arrayColumn = frame.extractArrayColumn()

    val arraysAreFrames = arrayColumn?.kind() == ColumnKind.Frame

    val data = frame.indices().map { rowIndex ->
        valueColumn?.get(rowIndex)
            ?: arrayColumn?.get(rowIndex)?.let {
                if (arraysAreFrames) {
                    encodeFrameWithMetadata(
                        it as AnyFrame,
                        rowLimit,
                        imageEncodingOptions,
                    )
                } else {
                    null
                }
            }
            ?: encodeRowWithMetadata(frame, rowIndex, rowLimit, imageEncodingOptions)
    }

    return buildJsonArray { addAll(data.map { convert(it) }) }
}

internal fun AnyFrame.extractValueColumn(): DataColumn<*>? {
    val allColumns = columns()

    return allColumns.filter { it.name.startsWith(VALUE_COLUMN_NAME) }
        .takeIf { isPossibleToFindUnnamedColumns }
        ?.maxByOrNull { it.name }
        ?.let { valueCol ->
            // check that value in this column is not null only when other values are null
            if (valueCol.kind() != ColumnKind.Value) {
                null
            } else {
                // check that value in this column is not null only when other values are null
                val isValidValueColumn = rows().all { row ->
                    if (valueCol[row] != null) {
                        allColumns.all { col ->
                            if (col.name != valueCol.name) {
                                col[row] == null
                            } else {
                                true
                            }
                        }
                    } else {
                        true
                    }
                }
                if (isValidValueColumn) {
                    valueCol
                } else {
                    null
                }
            }
        }
}

// If there is only 1 column, then `isValidValueColumn` always true.
// But at the same time, we shouldn't treat dataFrameOf("value")(1,2,3) like an unnamed column
// because it was created by the user.
internal val AnyFrame.isPossibleToFindUnnamedColumns: Boolean
    get() = columns().size != 1

internal fun AnyFrame.extractArrayColumn(): DataColumn<*>? {
    val allColumns = columns()

    return columns().filter { it.name.startsWith(ARRAY_COLUMN_NAME) }
        .takeIf { isPossibleToFindUnnamedColumns }
        ?.maxByOrNull { it.name }
        ?.let { arrayCol ->
            if (arrayCol.kind() == ColumnKind.Group) {
                null
            } else {
                // check that value in this column is not null only when other values are null
                val isValidArrayColumn = rows().all { row ->
                    if (arrayCol[row] != null) {
                        allColumns.all { col ->
                            if (col.name != arrayCol.name) {
                                col[row] == null
                            } else {
                                true
                            }
                        }
                    } else {
                        true
                    }
                }
                if (isValidArrayColumn) {
                    arrayCol
                } else {
                    null
                }
            }
        }
}

internal fun encodeFrame(frame: AnyFrame): JsonArray {
    val valueColumn = frame.extractValueColumn()
    val arrayColumn = frame.extractArrayColumn()

    val arraysAreFrames = arrayColumn?.kind() == ColumnKind.Frame

    val data = frame.indices().map { rowIndex ->
        when {
            valueColumn != null -> valueColumn[rowIndex]

            arrayColumn != null -> arrayColumn[rowIndex]?.let {
                if (arraysAreFrames) {
                    encodeFrame(it as AnyFrame)
                } else {
                    null
                }
            }

            else -> encodeRow(frame, rowIndex)
        }
    }

    return buildJsonArray { addAll(data.map { convert(it) }) }
}

internal fun encodeDataFrameWithMetadata(
    frame: AnyFrame,
    rowLimit: Int,
    nestedRowLimit: Int? = null,
    imageEncodingOptions: Base64ImageEncodingOptions? = null,
): JsonObject =
    buildJsonObject {
        put(VERSION, JsonPrimitive(SERIALIZATION_VERSION))
        putJsonObject(METADATA) {
            putJsonArray(COLUMNS) { addAll(frame.columnNames().map { JsonPrimitive(it) }) }
            putJsonArray(TYPES) {
                addAll(
                    frame.schema().columns.values.map { colSchema ->
                        createJsonTypeDescriptor(colSchema)
                    },
                )
            }
            put(NROW, JsonPrimitive(frame.rowsCount()))
            put(NCOL, JsonPrimitive(frame.columnsCount()))
        }
        put(
            KOTLIN_DATAFRAME,
            encodeFrameWithMetadata(
                frame = frame.take(rowLimit),
                rowLimit = nestedRowLimit,
                imageEncodingOptions = imageEncodingOptions,
            ),
        )
    }
