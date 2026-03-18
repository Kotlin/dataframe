package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.columnsCount
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
import java.math.BigDecimal

public fun AnyFrame.renderToString(
    rowsLimit: Int = 20,
    valueLimit: Int = 40,
    borders: Boolean = false,
    alignLeft: Boolean = false,
    columnTypes: Boolean = false,
    title: Boolean = false,
    rowIndex: Boolean = true,
): String {
    val sb = StringBuilder()
    val table = prepareTable(rowsLimit, valueLimit, columnTypes, rowIndex)
    val columnLengths = table.values.mapIndexed { col, vals ->
        (vals + table.header[col]).maxOf { it.length } + 1
    }

    // title
    if (title) {
        sb.appendLine("DataFrame [${size()}]")
        sb.appendLine()
    }

    // top border
    if (borders) {
        sb.append("\u230C")
        repeat(columnLengths.sum() + columnLengths.size - 1) { sb.append('-') }
        sb.append("\u230D")
        sb.appendLine()
        sb.append("|")
    }

    // header
    for (col in table.header.indices) {
        val len = columnLengths[col]
        val str = table.header[col]
        val padded = if (alignLeft) str.padEnd(len) else str.padStart(len)
        sb.append(padded)
        if (borders) sb.append("|")
    }
    sb.appendLine()

    // header splitter
    if (borders) {
        sb.append("|")
        for (colLength in columnLengths) {
            repeat(colLength) { sb.append('-') }
            sb.append("|")
        }
        sb.appendLine()
    }

    // data
    for (row in 0 until table.rowsCount) {
        if (borders) sb.append("|")
        for (col in table.values.indices) {
            val len = columnLengths[col]
            val str = table.values[col][row]
            val padded = if (alignLeft) str.padEnd(len) else str.padStart(len)
            sb.append(padded)
            if (borders) sb.append("|")
        }
        sb.appendLine()
    }

    // footer
    if (table.totalRows > rowsLimit) {
        sb.appendLine("...")
    } else if (borders) {
        sb.append("\u230E")
        repeat(columnLengths.sum() + columnLengths.size - 1) { sb.append('-') }
        sb.append("\u230F")
        sb.appendLine()
    }
    return sb.toString()
}

private class PreparedTable(
    val header: List<String>,
    val values: List<List<String>>,
    val rowsCount: Int,
    val totalRows: Int,
)

private fun AnyFrame.prepareTable(
    rowsLimit: Int,
    valueLimit: Int,
    columnTypes: Boolean,
    rowIndex: Boolean,
    escapeValue: (String) -> String = { it },
): PreparedTable {
    val rowsCount = rowsLimit.coerceAtMost(nrow)
    val cols = if (rowIndex) listOf((0 until rowsCount).toColumn()) + columns() else columns()
    val header = cols.mapIndexed { colIndex, col ->
        if (columnTypes && (!rowIndex || colIndex > 0)) {
            "${col.name()}:${renderType(col)}"
        } else {
            col.name()
        }
    }
    val values = cols.map { col ->
        val top = col.take(rowsLimit)
        val precision = if (top.isNumber()) top.asNumbers().scale() else 0
        val decimalFormat =
            if (precision >= 0) RendererDecimalFormat.fromPrecision(precision) else RendererDecimalFormat.of("%e")
        top.values().map {
            escapeValue(renderValueForStdout(it, valueLimit, decimalFormat = decimalFormat).truncatedContent)
        }
    }
    return PreparedTable(header, values, rowsCount, nrow)
}

public fun AnyFrame.renderToMarkdown(
    rowsLimit: Int = 20,
    valueLimit: Int = 40,
    alignLeft: Boolean = false,
    columnTypes: Boolean = false,
    title: Boolean = false,
    rowIndex: Boolean = true,
): String {
    val table = prepareTable(rowsLimit, valueLimit, columnTypes, rowIndex) { it.replace("|", "\\|") }

    val sb = StringBuilder()
    if (title) {
        sb.appendLine("**DataFrame [${size()}]**")
        sb.appendLine()
    }

    // header
    sb.append("|")
    for (col in table.header) {
        sb.append(" ${col.replace("|", "\\|")} |")
    }
    sb.appendLine()

    // separator
    sb.append("|")
    repeat(table.header.size) {
        sb.append(if (alignLeft) ":---|" else "---:|")
    }
    sb.appendLine()

    // data
    for (row in 0 until table.rowsCount) {
        sb.append("|")
        for (col in table.values.indices) {
            sb.append(" ${table.values[col][row]} |")
        }
        sb.appendLine()
    }

    // footer
    if (table.totalRows > rowsLimit) {
        sb.appendLine()
        sb.appendLine("*... ${table.totalRows - rowsLimit} more rows*")
    }
    return sb.toString()
}

internal val valueToStringLimitDefault = 1000
internal val valueToStringLimitForRowAsTable = 50

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

internal fun AnyRow.renderToString(): String {
    val values = getVisibleValues()
    if (values.isEmpty()) return "{ }"
    return values.joinToString(
        prefix = "{ ",
        postfix = " }",
    ) { "${it.first}:${renderValueForStdout(it.second).truncatedContent}" }
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
            renderValueForHtml(value, valueToStringLimitForRowAsTable, RendererDecimalFormat.DEFAULT)
        } else {
            renderValueForStdout(value, valueToStringLimitForRowAsTable)
        }
    }

internal fun renderValueForStdout(
    value: Any?,
    limit: Int = valueToStringLimitDefault,
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

        else ->
            value?.asArrayAsListOrNull()
                ?.let { renderValueToString(it, decimalFormat) }
                ?: value.toString()
    }

internal fun Double.format(decimalFormat: RendererDecimalFormat): String = decimalFormat.format.format(this)

internal fun Float.format(decimalFormat: RendererDecimalFormat): String = decimalFormat.format.format(this)

internal fun BigDecimal.format(decimalFormat: RendererDecimalFormat): String = decimalFormat.format.format(this)
