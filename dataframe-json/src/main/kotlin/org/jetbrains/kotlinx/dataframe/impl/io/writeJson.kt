@file:OptIn(ExperimentalSerializationApi::class)

package org.jetbrains.kotlinx.dataframe.impl.io

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.addAll
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.COLUMNS
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.DATA
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.IS_FORMATTED
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
import org.jetbrains.kotlinx.dataframe.io.CustomEncoder
import org.jetbrains.kotlinx.dataframe.io.VALUE_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.jupyter.KotlinNotebookPluginUtils
import org.jetbrains.kotlinx.dataframe.name
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.typeClass
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import java.io.IOException

// See docs/serialization_format.md for a description of
// serialization versions and format.
internal const val SERIALIZATION_VERSION = "2.2.0"

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
    const val IS_FORMATTED = "is_formatted"
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
    customEncoders: List<CustomEncoder> = emptyList(),
): JsonElement? {
    val values: List<Pair<String, JsonElement>> = frame.columns().map { col ->
        when (col) {
            is ColumnGroup<*> -> {
                val schema = col.schema()
                buildJsonObject {
                    put(DATA, encodeRowWithMetadata(col, index, rowLimit, customEncoders) ?: JsonPrimitive(null))
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
                    encodeFrameWithMetadata(col[index], null, customEncoders)
                } else {
                    encodeFrameWithMetadata(col[index].take(rowLimit), rowLimit, customEncoders)
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
                        put(NCOL, JsonPrimitive(col[index].columnsCount()))
                        put(NROW, JsonPrimitive(col[index].rowsCount()))
                    }
                }
            }

            else -> encodeValue(col, index, customEncoders)
        }.let { col.name to it }
    }
    if (values.isEmpty()) return null
    return JsonObject(values.toMap())
}

internal fun encodeValue(col: AnyCol, index: Int, customEncoders: List<CustomEncoder> = emptyList()): JsonElement {
    val matchingEncoder = customEncoders.firstOrNull { it.canEncode(col[index]) }

    return when {
        matchingEncoder != null -> matchingEncoder.encode(col[index])

        col.isList() -> col[index]?.let { list ->
            val values = list.map { convert(it) }
            JsonArray(values)
        } ?: JsonArray(emptyList())

        col.typeClass in valueTypes -> convert(col[index])

        else -> JsonPrimitive(col[index]?.toString())
    }
}

internal class DataframeConvertableEncoder(
    private val encoders: List<CustomEncoder>,
    private val rowLimit: Int? = null,
) : CustomEncoder {
    override fun canEncode(input: Any?): Boolean = isDataframeConvertable(input)

    override fun encode(input: Any?): JsonElement =
        input?.let {
            val data = encodeFrameWithMetadata(
                KotlinNotebookPluginUtils.convertToDataFrame(input),
                rowLimit,
                encoders,
            )
            val isFormatted = input is FormattedFrame<*>
            buildJsonObject {
                put(DATA, data)
                putJsonObject(METADATA) {
                    put(KIND, JsonPrimitive(CellKind.DataFrameConvertable.toString()))
                    put(IS_FORMATTED, JsonPrimitive(isFormatted))
                }
            }
        } ?: JsonPrimitive(null)
}

internal class BufferedImageEncoder(private val options: Base64ImageEncodingOptions) : CustomEncoder {
    override fun canEncode(input: Any?): Boolean = input is BufferedImage

    override fun encode(input: Any?): JsonElement =
        JsonPrimitive(
            input?.let { image -> encodeBufferedImageAsBase64(image as BufferedImage, options) } ?: "",
        )

    private fun encodeBufferedImageAsBase64(
        image: BufferedImage,
        imageEncodingOptions: Base64ImageEncodingOptions = Base64ImageEncodingOptions(),
    ): String =
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
        } catch (_: IOException) {
            ""
        }
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
    customEncoders: List<CustomEncoder> = emptyList(),
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
                        customEncoders,
                    )
                } else {
                    null
                }
            }
            ?: encodeRowWithMetadata(frame, rowIndex, rowLimit, customEncoders)
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
    customEncoders: List<CustomEncoder> = emptyList(),
    isFormatted: Boolean = false,
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
            put(IS_FORMATTED, JsonPrimitive(isFormatted))
        }
        put(
            KOTLIN_DATAFRAME,
            encodeFrameWithMetadata(
                frame = frame.take(rowLimit),
                rowLimit = nestedRowLimit,
                customEncoders = customEncoders,
            ),
        )
    }

@OptIn(ExperimentalSerializationApi::class)
internal fun encodeFrameNoDynamicNestedTables(df: AnyFrame, limit: Int, isFormatted: Boolean): JsonObject =
    buildJsonObject {
        put(NROW, df.rowsCount())
        put(NCOL, df.columnsCount())
        putJsonArray(COLUMNS) { addAll(df.columnNames()) }
        put(IS_FORMATTED, JsonPrimitive(isFormatted))
        put(
            KOTLIN_DATAFRAME,
            encodeFrame(df.take(limit)),
        )
    }

// region friend module error suppression

@Suppress("INVISIBLE_REFERENCE")
private object CellKind {
    val DataFrameConvertable = org.jetbrains.kotlinx.dataframe.columns.CellKind.DataFrameConvertable
}

@Suppress("INVISIBLE_REFERENCE")
private fun isDataframeConvertable(dataframeLike: Any?) =
    KotlinNotebookPluginUtils.isDataframeConvertable(dataframeLike = dataframeLike)

@Suppress("INVISIBLE_REFERENCE")
internal fun BufferedImage.resizeKeepingAspectRatio(
    maxSize: Int,
    resultImageType: Int = BufferedImage.TYPE_INT_ARGB,
    interpolation: Any = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
    renderingQuality: Any = RenderingHints.VALUE_RENDER_QUALITY,
    antialiasing: Any = RenderingHints.VALUE_ANTIALIAS_ON,
    observer: ImageObserver? = null,
) = org.jetbrains.kotlinx.dataframe.impl.io.resizeKeepingAspectRatio(
    image = this,
    maxSize = maxSize,
    resultImageType = resultImageType,
    interpolation = interpolation,
    renderingQuality = renderingQuality,
    antialiasing = antialiasing,
    observer = observer,
)

private const val DEFAULT_IMG_FORMAT: String = "png"

@Suppress("INVISIBLE_REFERENCE")
private fun BufferedImage.toByteArray(format: String = DEFAULT_IMG_FORMAT) =
    org.jetbrains.kotlinx.dataframe.impl.io.toByteArray(image = this, format = format)

// endregion
