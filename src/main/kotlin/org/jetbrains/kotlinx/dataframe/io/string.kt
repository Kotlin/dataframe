package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.precision
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.truncate
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.jetbrains.kotlinx.dataframe.size
import java.math.BigDecimal

internal fun AnyFrame.renderToString(
    rowsLimit: Int = 20,
    valueLimit: Int = 40,
    borders: Boolean = false,
    alignLeft: Boolean = false,
    columnTypes: Boolean = false,
    title: Boolean = false,
    rowIndex: Boolean = true,
): String {
    val sb = StringBuilder()

    // title
    if (title) {
        sb.appendLine("Data Frame [${size()}]")
        sb.appendLine()
    }

    // data
    val rowsCount = rowsLimit.coerceAtMost(nrow())
    val cols = if (rowIndex) listOf((0 until rowsCount).toColumn()) + columns() else columns()
    val header = cols.mapIndexed { colIndex, col ->
        if (columnTypes && (!rowIndex || colIndex > 0)) {
            "${col.name()}:${renderType(col)}"
        } else col.name()
    }
    val values = cols.map {
        val top = it.take(rowsLimit)
        val precision = if (top.isNumber()) top.asNumbers().precision() else 0
        top.values().map {
            renderValueForStdout(it, valueLimit, precision = precision).truncatedContent
        }
    }
    val columnLengths = values.mapIndexed { col, vals -> (vals + header[col]).map { it.length }.maxOrNull()!! + 1 }

    // top border
    if (borders) {
        sb.append("\u230C")
        for (i in 1 until columnLengths.sum() + columnLengths.size) sb.append('-')
        sb.append("\u230D")
        sb.appendLine()
        sb.append("|")
    }

    // header
    for (col in header.indices) {
        val len = columnLengths[col]
        val str = header[col]
        val padded = if (alignLeft) str.padEnd(len) else str.padStart(len)
        sb.append(padded)
        if (borders) sb.append("|")
    }
    sb.appendLine()

    // header splitter
    if (borders) {
        sb.append("|")
        for (colLength in columnLengths) {
            for (i in 1..colLength) sb.append('-')
            sb.append("|")
        }
        sb.appendLine()
    }

    // data
    for (row in 0 until rowsCount) {
        if (borders) sb.append("|")
        for (col in values.indices) {
            val len = columnLengths[col]
            val str = values[col][row]
            val padded = if (alignLeft) str.padEnd(len) else str.padStart(len)
            sb.append(padded)
            if (borders) sb.append("|")
        }
        sb.appendLine()
    }

    // footer
    if (nrow() > rowsLimit) {
        sb.appendLine("...")
    } else if (borders) {
        sb.append("\u230E")
        for (i in 1 until columnLengths.sum() + columnLengths.size) sb.append('-')
        sb.append("\u230F")
        sb.appendLine()
    }
    return sb.toString()
}

internal val valueToStringLimitDefault = 1000
internal val valueToStringLimitForRowAsTable = 50

internal fun AnyRow.getVisibleValues(): List<Pair<String, Any?>> {
    fun Any?.skip(): Boolean = when (this) {
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
    return values
        .map { "${it.first}:${renderValueForStdout(it.second).truncatedContent}" }
        .joinToString(prefix = "{ ", postfix = " }")
}

internal fun AnyRow.renderToStringTable(forHtml: Boolean = false): String {
    if (size() == 0) return ""
    val pairs = owner.columns().map { it.name() to renderValueForRowTable(it[index], forHtml) }
    val width = pairs.map { it.first.length + it.second.textLength }.maxOrNull()!! + 4
    return pairs.joinToString("\n") {
        it.first + " ".repeat(width - it.first.length - it.second.textLength) + it.second.truncatedContent
    }
}

internal fun renderCollectionName(value: Collection<*>) = when (value) {
    is List -> "List"
    is Map<*, *> -> "Map"
    is Set -> "Set"
    else -> value.javaClass.simpleName
}

internal fun renderValueForRowTable(value: Any?, forHtml: Boolean): RenderedContent = when (value) {
    is AnyFrame -> "DataFrame [${value.nrow()} x ${value.ncol()}]".let {
        val content = if (value.nrow() == 1) it + " " + value[0].toString() else it
        RenderedContent.textWithLength(content, "DataFrame".length)
    }
    is AnyRow -> RenderedContent.textWithLength("DataRow $value", "DataRow".length)
    is Collection<*> -> renderCollectionName(value).let { RenderedContent.textWithLength("$it $value", it.length) }
    else -> if (forHtml) renderValueForHtml(value, valueToStringLimitForRowAsTable, defaultPrecision)
    else renderValueForStdout(value, valueToStringLimitForRowAsTable)
}

internal fun renderValueForStdout(
    value: Any?,
    limit: Int = valueToStringLimitDefault,
    precision: Int = defaultPrecision
): RenderedContent =
    renderValueToString(value, precision).truncate(limit)
        .let { it.copy(truncatedContent = it.truncatedContent.escapeNewLines()) }

internal val defaultPrecision = 6

internal fun renderValueToString(value: Any?, precision: Int) =
    when (value) {
        is AnyFrame -> "[${value.size}]".let { if (value.nrow() == 1) it + " " + value[0].toString() else it }
        is Double -> value.format(precision)
        is Float -> value.format(precision)
        is BigDecimal -> value.format(precision)
        is List<*> -> if (value.isEmpty()) "[ ]" else value.toString()
        else -> value.toString()
    }

internal fun internallyRenderable(value: Any?): Boolean {
    return when (value) {
        is AnyFrame, is Double, is List<*>, null, "" -> true
        else -> false
    }
}

internal fun Double.format(precision: Int): String = "%.${precision}f".format(this)
internal fun Float.format(precision: Int): String = "%.${precision}f".format(this)
internal fun BigDecimal.format(precision: Int): String = "%.${precision}f".format(this)
