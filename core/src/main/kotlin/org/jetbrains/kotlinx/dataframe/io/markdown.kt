package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.size

public fun AnyFrame.renderToMarkdown(
    rowsLimit: Int? = 20,
    valueLimit: Int? = 40,
    alignment: MarkdownAlignment = MarkdownAlignment.UNSPECIFIED,
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
    for ((i, col) in table.header.withIndex()) {
        val type = table.types?.getOrNull(i)?.takeIf { it.isNotEmpty() }?.let { ":$it" } ?: ""
        sb.append(" ${col.replace("|", "\\|")}$type |")
    }
    sb.appendLine()

    // separator
    sb.append("|")
    repeat(table.header.size) {
        sb.append(alignment.separator)
        sb.append("|")
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
    if (table.totalRows > table.rowsCount) {
        sb.appendLine()
        sb.appendLine("*... ${table.totalRows - table.rowsCount} more rows*")
    }
    return sb.toString()
}

public enum class MarkdownAlignment(public val separator: String) {
    LEFT(":---"),
    RIGHT("---:"),
    CENTER(":---:"),
    UNSPECIFIED("---"),
}
