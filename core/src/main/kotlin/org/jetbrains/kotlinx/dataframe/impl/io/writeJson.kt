package org.jetbrains.kotlinx.dataframe.impl.io

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.KlaxonJson
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.take
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
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.VERSION
import org.jetbrains.kotlinx.dataframe.io.ARRAY_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.io.Base64ImageEncodingOptions
import org.jetbrains.kotlinx.dataframe.io.VALUE_COLUMN_NAME
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.typeClass
import java.awt.image.BufferedImage
import java.io.IOException

internal fun KlaxonJson.encodeRow(
    frame: ColumnsContainer<*>,
    index: Int,
): JsonObject? {
    val values =
        frame.columns().map { col ->
            when (col) {
                is ColumnGroup<*> -> encodeRow(col, index)
                is FrameColumn<*> -> encodeFrame(col[index])
                else -> encodeValue(col, index)
            }.let { col.name to it }
        }
    if (values.isEmpty()) return null
    return obj(values)
}

internal object SerializationKeys {
    const val DATA = "data"
    const val METADATA = "metadata"
    const val KIND = "kind"
    const val NCOL = "ncol"
    const val NROW = "nrow"
    const val VERSION = "\$version"
    const val COLUMNS = "columns"
    const val KOTLIN_DATAFRAME = "kotlin_dataframe"
}

internal const val SERIALIZATION_VERSION = "2.0.0"

internal fun KlaxonJson.encodeRowWithMetadata(
    frame: ColumnsContainer<*>,
    index: Int,
    rowLimit: Int? = null,
    imageEncodingOptions: Base64ImageEncodingOptions? = null,
): JsonObject? {
    val values =
        frame.columns().map { col ->
            when (col) {
                is ColumnGroup<*> ->
                    obj(
                        DATA to encodeRowWithMetadata(col, index, rowLimit, imageEncodingOptions),
                        METADATA to obj(KIND to ColumnKind.Group.toString()),
                    )

                is FrameColumn<*> -> {
                    val data =
                        if (rowLimit == null) {
                            encodeFrameWithMetadata(col[index], null, imageEncodingOptions)
                        } else {
                            encodeFrameWithMetadata(col[index].take(rowLimit), rowLimit, imageEncodingOptions)
                        }
                    obj(
                        DATA to data,
                        METADATA to
                            obj(
                                KIND to ColumnKind.Frame.toString(),
                                NCOL to col[index].ncol,
                                NROW to col[index].nrow,
                            ),
                    )
                }

                else -> encodeValue(col, index, imageEncodingOptions)
            }.let { col.name to it }
        }
    if (values.isEmpty()) return null
    return obj(values)
}

private val valueTypes =
    setOf(Boolean::class, Double::class, Int::class, Float::class, Long::class, Short::class, Byte::class)

internal fun KlaxonJson.encodeValue(
    col: AnyCol,
    index: Int,
    imageEncodingOptions: Base64ImageEncodingOptions? = null,
): Any? =
    when {
        col.isList() ->
            col[index]?.let { list ->
                val values =
                    (list as List<*>).map {
                        when (it) {
                            null, is Int, is Double, is Float, is Long, is Boolean, is Short, is Byte -> it

                            // Klaxon default serializers will try to use reflection and can sometimes fail.
                            // We can't have exceptions in Notebook DataFrame renderer
                            else -> it.toString()
                        }
                    }
                array(values)
            } ?: array()

        col.typeClass in valueTypes -> {
            val v = col[index]
            if ((v is Double && v.isNaN()) || (v is Float && v.isNaN())) {
                v.toString()
            } else {
                v
            }
        }

        col.typeClass == BufferedImage::class && imageEncodingOptions != null ->
            col[index]?.let { image ->
                encodeBufferedImageAsBase64(image as BufferedImage, imageEncodingOptions)
            } ?: ""

        else -> col[index]?.toString()
    }

private fun encodeBufferedImageAsBase64(
    image: BufferedImage,
    imageEncodingOptions: Base64ImageEncodingOptions = Base64ImageEncodingOptions(),
): String? =
    try {
        val preparedImage =
            if (imageEncodingOptions.isLimitSizeOn) {
                image.resizeKeepingAspectRatio(imageEncodingOptions.imageSizeLimit)
            } else {
                image
            }

        val bytes =
            if (imageEncodingOptions.isGzipOn) {
                preparedImage.toByteArray().encodeGzip()
            } else {
                preparedImage.toByteArray()
            }

        bytes.toBase64()
    } catch (e: IOException) {
        null
    }

internal fun KlaxonJson.encodeFrameWithMetadata(
    frame: AnyFrame,
    rowLimit: Int? = null,
    imageEncodingOptions: Base64ImageEncodingOptions? = null,
): JsonArray<*> {
    val valueColumn = frame.extractValueColumn()
    val arrayColumn = frame.extractArrayColumn()

    val arraysAreFrames = arrayColumn?.kind() == ColumnKind.Frame

    val data =
        frame.indices().map { rowIndex ->
            valueColumn
                ?.get(rowIndex)
                ?: arrayColumn
                    ?.get(rowIndex)
                    ?.let {
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

    return array(data)
}

internal fun AnyFrame.extractValueColumn(): DataColumn<*>? {
    val allColumns = columns()

    return allColumns
        .filter { it.name.startsWith(VALUE_COLUMN_NAME) }
        .takeIf { isPossibleToFindUnnamedColumns }
        ?.maxByOrNull { it.name }
        ?.let { valueCol ->
            // check that value in this column is not null only when other values are null
            if (valueCol.kind() != ColumnKind.Value) {
                null
            } else {
                // check that value in this column is not null only when other values are null
                val isValidValueColumn =
                    rows().all { row ->
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

// if there is only 1 column, then `isValidValueColumn` always true.
// But at the same time, we shouldn't treat dataFrameOf("value")(1,2,3) like unnamed column
// because it was created by user.
internal val AnyFrame.isPossibleToFindUnnamedColumns: Boolean
    get() = columns().size != 1

internal fun AnyFrame.extractArrayColumn(): DataColumn<*>? {
    val allColumns = columns()

    return columns()
        .filter { it.name.startsWith(ARRAY_COLUMN_NAME) }
        .takeIf { isPossibleToFindUnnamedColumns }
        ?.maxByOrNull { it.name }
        ?.let { arrayCol ->
            if (arrayCol.kind() == ColumnKind.Group) {
                null
            } else {
                // check that value in this column is not null only when other values are null
                val isValidArrayColumn =
                    rows().all { row ->
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

internal fun KlaxonJson.encodeFrame(frame: AnyFrame): JsonArray<*> {
    val valueColumn = frame.extractValueColumn()
    val arrayColumn = frame.extractArrayColumn()

    val arraysAreFrames = arrayColumn?.kind() == ColumnKind.Frame

    val data =
        frame.indices().map { rowIndex ->
            valueColumn
                ?.get(rowIndex)
                ?: arrayColumn
                    ?.get(rowIndex)
                    ?.let {
                        if (arraysAreFrames) encodeFrame(it as AnyFrame) else null
                    }
                ?: encodeRow(frame, rowIndex)
        }

    return array(data)
}

internal fun KlaxonJson.encodeDataFrameWithMetadata(
    frame: AnyFrame,
    rowLimit: Int,
    nestedRowLimit: Int? = null,
    imageEncodingOptions: Base64ImageEncodingOptions? = null,
): JsonObject =
    obj(
        VERSION to SERIALIZATION_VERSION,
        METADATA to
            obj(
                COLUMNS to frame.columnNames(),
                NROW to frame.rowsCount(),
                NCOL to frame.columnsCount(),
            ),
        KOTLIN_DATAFRAME to
            encodeFrameWithMetadata(
                frame.take(rowLimit),
                rowLimit = nestedRowLimit,
                imageEncodingOptions,
            ),
    )
