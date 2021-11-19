package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.truncate
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.jetbrains.kotlinx.dataframe.size

public fun <T, G> GroupBy<T, G>.print(): Unit = println(this)

internal fun AnyFrame.renderToString(limit: Int = 20, truncate: Int = 40): String {
    val sb = StringBuilder()
    sb.appendLine("Data Frame [${size()}]")
    sb.appendLine()

    val outputRows = limit.coerceAtMost(nrow())
    val output = columns().map { it.values.take(limit).map { renderValueForStdout(it, truncate).truncatedContent } }
    val header = columns().map { "${it.name()}:${renderType(it)}" }
    val columnLengths = output.mapIndexed { col, values -> (values + header[col]).map { it.length }.maxOrNull()!! + 1 }

    sb.append("|")
    for (col in header.indices) {
        sb.append(header[col].padEnd(columnLengths[col]) + "|")
    }
    sb.appendLine()
    sb.append("|")
    for (colLength in columnLengths) {
        for (i in 1..colLength) sb.append('-')
        sb.append("|")
    }
    sb.appendLine()

    for (row in 0 until outputRows) {
        sb.append("|")
        for (col in output.indices) {
            sb.append(output[col][row].padEnd(columnLengths[col]) + "|")
        }
        sb.appendLine()
    }
    if (nrow() > limit) {
        sb.appendLine("...")
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
        .map { "${it.first}:${renderValueForStdout(it.second).truncatedContent}" }.joinToString(prefix = "{ ", postfix = " }")
}

public fun AnyRow.renderToStringTable(forHtml: Boolean = false): String {
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

public fun renderValueForRowTable(value: Any?, forHtml: Boolean): RenderedContent = when (value) {
    is AnyFrame -> "DataFrame [${value.nrow()} x ${value.ncol()}]".let {
        val content = if (value.nrow() == 1) it + " " + value[0].toString() else it
        RenderedContent.textWithLength(content, "DataFrame".length)
    }
    is AnyRow -> RenderedContent.textWithLength("DataRow $value", "DataRow".length)
    is Collection<*> -> renderCollectionName(value).let { RenderedContent.textWithLength("$it $value", it.length) }
    else -> if (forHtml) renderValueForHtml(value, valueToStringLimitForRowAsTable)
    else renderValueForStdout(value, valueToStringLimitForRowAsTable)
}

internal fun renderValueForStdout(value: Any?, truncate: Int = valueToStringLimitDefault): RenderedContent = renderValueToString(value).truncate(truncate).let { it.copy(truncatedContent = it.truncatedContent.escapeNewLines()) }

internal fun renderValueToString(value: Any?) =
    when (value) {
        is AnyFrame -> "[${value.size}]".let { if (value.nrow() == 1) it + " " + value[0].toString() else it }
        is Double -> value.format(6)
        is List<*> -> if (value.isEmpty()) "[ ]" else value.toString()
        else -> value.toString()
    }

internal fun internallyRenderable(value: Any?): Boolean {
    return when (value) {
        is AnyFrame, is Double, is List<*>, null, "" -> true
        else -> false
    }
}

public fun Double.format(digits: Int): String = "%.${digits}f".format(this)
