package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.io.escapeHTML
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.type
import java.net.URL
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

internal fun String.truncate(limit: Int): RenderedContent = if (limit in 1 until length) {
    if (limit < 4) RenderedContent.truncatedText("...", this)
    else RenderedContent.truncatedText(substring(0, Math.max(limit - 3, 1)) + "...", this)
} else {
    RenderedContent.text(this)
}

internal fun renderSchema(df: AnyFrame): String =
    df.columns().map { "${it.name()}:${renderType(it)}" }.joinToString()

internal fun renderSchema(schema: DataFrameSchema): String =
    schema.columns.map { "${it.key}:${renderType(it.value)}" }.joinToString()

internal fun renderType(column: ColumnSchema) =
    when (column) {
        is ColumnSchema.Value -> {
            renderType(column.type)
        }
        is ColumnSchema.Frame -> {
            "[${renderSchema(column.schema)}]"
        }
        is ColumnSchema.Group -> {
            "{${renderSchema(column.schema)}}"
        }
        else -> throw NotImplementedError()
    }

internal val classesToBeRenderedShort = setOf(URL::class, LocalDateTime::class, LocalTime::class)

internal fun renderType(type: KType): String {
    return when (type.classifier) {
        List::class -> {
            val argument = type.arguments[0].type?.let { renderType(it) } ?: "*"
            "List<$argument>"
        }
        else -> {
            val result = type.toString()
            if (classesToBeRenderedShort.contains(type.classifier) || result.startsWith("kotlin.") || result.startsWith("org.jetbrains.kotlinx.dataframe.")) {
                (type.jvmErasure.simpleName?.let { if (type.isMarkedNullable) "$it?" else it }) ?: result
            } else result
        }
    }
}

internal fun renderType(column: AnyCol) =
    when (column.kind()) {
        ColumnKind.Value -> renderType(column.type)
        ColumnKind.Frame -> {
            val table = column.asFrameColumn()
            "[${renderSchema(table.schema.value)}]"
        }
        ColumnKind.Group -> {
            val group = column.asColumnGroup()
            "{${renderSchema(group.df)}}"
        }
    }

internal fun AnyCol.renderShort() = when (kind()) {
    ColumnKind.Value -> "ValueColumn<${renderType(type)}>: $size entries".escapeHTML()
    ColumnKind.Frame -> "FrameColumn: $size entries"
    ColumnKind.Group -> "ColumnGroup ${asColumnGroup().df.size}}"
}
