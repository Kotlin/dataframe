package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.columnsCount
import org.jetbrains.kotlinx.dataframe.api.getColumns
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.impl.asArrayAsListOrNull
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.scale
import org.jetbrains.kotlinx.dataframe.impl.truncate
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.util.PRINT
import java.math.BigDecimal

public fun AnyFrame.renderToString(
    rowsLimit: Int? = 20,
    valueLimit: Int? = 40,
    borders: Boolean = false,
    alignLeft: Boolean = false,
    columnTypes: Boolean = true,
    title: Boolean = false,
    rowIndex: Boolean = true,
): String {
    val sb = StringBuilder()
    val table = prepareTable(rowsLimit, valueLimit, columnTypes, rowIndex)
    val columnLengths = table.values.mapIndexed { col, vals ->
        (vals + table.header[col] + (table.types?.get(col) ?: "")).maxOf { it.length } + 2
    }

    // title
    if (title) {
        sb.appendLine("DataFrame [${size()}]")
        sb.appendLine()
    }

    // top border
    if (borders) {
        sb.append(Borders.TOP_LEFT)
        repeat(columnLengths.sum() + columnLengths.size - 1) { sb.append('-') }
        sb.append(Borders.TOP_RIGHT)
        sb.appendLine()
        sb.append(Borders.VERTICAL)
    }

    // header
    for (col in table.header.indices) {
        val len = columnLengths[col]
        val str = table.header[col]
        val padded = if (alignLeft) str.padEnd(len) else str.padStart(len)
        sb.append(padded)
        if (borders) sb.append(Borders.VERTICAL)
    }
    sb.appendLine()

    table.types?.let { types ->
        if (borders) sb.append(Borders.VERTICAL)
        for (col in table.header.indices) {
            val len = columnLengths[col]
            val str = types[col]
            val padded = if (alignLeft) str.padEnd(len) else str.padStart(len)
            sb.append(padded)
            if (borders) sb.append(Borders.VERTICAL)
        }
        sb.appendLine()
    }

    // header splitter
    if (borders) {
        sb.append(Borders.VERTICAL)
        for (colLength in columnLengths) {
            repeat(colLength) { sb.append(Borders.HORIZONTAL) }
            sb.append(Borders.HEADER_SPLIT)
        }
        sb.appendLine()
    }

    // data
    for (row in 0 until table.rowsCount) {
        if (borders) sb.append(Borders.VERTICAL)
        for (col in table.values.indices) {
            val len = columnLengths[col]
            val str = table.values[col][row]
            val padded = if (alignLeft) str.padEnd(len) else str.padStart(len)
            sb.append(padded)
            if (borders) sb.append(Borders.VERTICAL)
        }
        sb.appendLine()
    }

    // footer
    if (table.totalRows > table.rowsCount) {
        sb.appendLine("...")
        sb.appendLine("${size.ncol} columns x ${size.nrow} rows")
    } else if (borders) {
        sb.append(Borders.BOTTOM_LEFT)
        repeat(columnLengths.sum() + columnLengths.size - 1) { sb.append(Borders.HORIZONTAL) }
        sb.append(Borders.BOTTOM_RIGHT)
        sb.appendLine()
    }
    return sb.toString()
}

@Deprecated(message = PRINT, level = DeprecationLevel.HIDDEN)
public fun AnyFrame.renderToString(
    rowsLimit: Int = 20,
    valueLimit: Int = 40,
    borders: Boolean = false,
    alignLeft: Boolean = false,
    columnTypes: Boolean = true,
    title: Boolean = false,
    rowIndex: Boolean = true,
): String = renderToString(rowsLimit, valueLimit, borders, alignLeft, columnTypes, title, rowIndex)

private object Borders {
    const val TOP_LEFT = "\u230C"
    const val TOP_RIGHT = "\u230D"
    const val BOTTOM_LEFT = "\u230E"
    const val BOTTOM_RIGHT = "\u230F"
    const val HORIZONTAL = "-"
    const val VERTICAL = "|"
    const val HEADER_SPLIT = "|"
}

internal class PreparedTable(
    val header: List<String>,
    val types: List<String>?,
    val values: List<List<String>>,
    val rowsCount: Int,
    val totalRows: Int,
)

internal fun AnyFrame.prepareTable(
    rowsLimit: Int?,
    valueLimit: Int?,
    columnTypes: Boolean,
    rowIndex: Boolean,
    escapeValue: (String) -> String = { it },
): PreparedTable {
    val rowsCount = rowsLimit?.coerceAtMost(nrow) ?: rowsCount()
    val cols = if (rowIndex) listOf((0..<rowsCount).toColumn()) + columns() else columns()
    val header = cols.map { col -> col.name() }
    val types = if (columnTypes) {
        cols.mapIndexed { colIndex, col ->
            if (!rowIndex || colIndex > 0) {
                renderType(col)
            } else {
                ""
            }
        }
    } else {
        null
    }
    val values = cols.map { col ->
        val top = col.take(rowsCount)
        val precision = if (top.isNumber()) {
            top.asNumbers().scale()
        } else if (top.isColumnGroup()) {
            top.getColumns { colsAtAnyDepth().filter { it.isNumber() } }.maxOfOrNull {
                it.asNumbers().scale()
            } ?: 0
        } else {
            0
        }
        val decimalFormat =
            if (precision >= 0) RendererDecimalFormat.fromPrecision(precision) else RendererDecimalFormat.of("%e")
        top.values().map {
            escapeValue(renderValueForStdout(it, valueLimit ?: -1, decimalFormat = decimalFormat).truncatedContent)
        }
    }
    return PreparedTable(header, types, values, rowsCount, nrow)
}

internal const val VALUE_TO_STRING_LIMIT_DEFAULT = 1000
internal const val VALUE_TO_STRING_LIMIT_FOR_ROW_AS_TABLE = 50

internal fun AnyRow.getVisibleValues(): List<Pair<String, Any?>> {
    fun Any?.skip(): Boolean =
        when (this) {
            null -> true
            is List<*> -> this.isEmpty()
            is AnyRow -> values().all { it.skip() }
            else -> false
        }
    return owner.columns().map { it.name() to it[index] }.filter { !it.second.skip() }
}

internal fun AnyRow.renderToString(decimalFormat: RendererDecimalFormat = RendererDecimalFormat.DEFAULT): String {
    val values = getVisibleValues()
    if (values.isEmpty()) return "{ }"
    return values.joinToString(
        prefix = "{ ",
        postfix = " }",
    ) { "${it.first}:${renderValueForStdout(it.second, decimalFormat = decimalFormat).truncatedContent}" }
}

internal fun AnyRow.renderToStringTable(forHtml: Boolean = false): String {
    if (columnsCount() == 0) return ""
    val pairs = owner.columns().map { it.name() to renderValueForRowTable(it[index], forHtml) }
    val width = pairs.maxOf { it.first.length + it.second.textLength } + 4
    return pairs.joinToString("\n") {
        it.first + " ".repeat(width - it.first.length - it.second.textLength) + it.second.truncatedContent
    }
}

internal fun renderCollectionName(value: Collection<*>) =
    when (value) {
        is List -> "List"
        is Map<*, *> -> "Map"
        is Set -> "Set"
        else -> value.javaClass.simpleName
    }

internal fun renderValueForRowTable(value: Any?, forHtml: Boolean): RenderedContent =
    when (value) {
        is AnyFrame -> "DataFrame [${value.nrow} x ${value.ncol}]".let {
            val content = if (value.nrow == 1) it + " " + value[0].toString() else it
            RenderedContent.textWithLength(content, "DataFrame".length)
        }

        is AnyRow -> RenderedContent.textWithLength("DataRow $value", "DataRow".length)

        is Collection<*> -> renderCollectionName(value).let { RenderedContent.textWithLength("$it $value", it.length) }

        else -> if (forHtml) {
            renderValueForHtml(value, VALUE_TO_STRING_LIMIT_FOR_ROW_AS_TABLE, RendererDecimalFormat.DEFAULT)
        } else {
            renderValueForStdout(value, VALUE_TO_STRING_LIMIT_FOR_ROW_AS_TABLE)
        }
    }

internal fun renderValueForStdout(
    value: Any?,
    limit: Int = VALUE_TO_STRING_LIMIT_DEFAULT,
    decimalFormat: RendererDecimalFormat = RendererDecimalFormat.DEFAULT,
): RenderedContent =
    renderValueToString(value, decimalFormat)
        .truncate(limit)
        .let { it.copy(truncatedContent = it.truncatedContent.escapeNewLines()) }

internal fun renderValueToString(value: Any?, decimalFormat: RendererDecimalFormat): String =
    when (value) {
        is AnyFrame -> "[${value.size}]".let { if (value.nrow == 1) it + " " + value[0].toString() else it }

        is Double -> value.format(decimalFormat)

        is Float -> value.format(decimalFormat)

        is BigDecimal -> value.format(decimalFormat)

        is List<*> -> if (value.isEmpty()) "[ ]" else value.toString()

        is Array<*> -> if (value.isEmpty()) "[ ]" else value.toList().toString()

        is AnyRow -> value.renderToString(decimalFormat)

        else ->
            value?.asArrayAsListOrNull()
                ?.let { renderValueToString(it, decimalFormat) }
                ?: value.toString()
    }

internal fun Double.format(decimalFormat: RendererDecimalFormat): String = decimalFormat.format.format(this)

internal fun Float.format(decimalFormat: RendererDecimalFormat): String = decimalFormat.format.format(this)

internal fun BigDecimal.format(decimalFormat: RendererDecimalFormat): String = decimalFormat.format.format(this)
