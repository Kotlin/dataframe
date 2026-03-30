package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.size

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
    for ((i, col) in table.header.withIndex()) {
        val type = table.types?.getOrNull(i)?.takeIf { it.isNotEmpty() }?.let { ":$it" } ?: ""
        sb.append(" ${col.replace("|", "\\|")}$type |")
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
