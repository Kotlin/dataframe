package org.jetbrains.dataframe

import kotlin.reflect.KType

internal fun String.truncate(limit: Int) = if (limit in 1 until length) {
    if (limit < 4) substring(0, limit)
    else substring(0, limit - 3) + "..."
} else {
    this
}

data class Image(val url: String)

fun DataFrame<*>.toHTML(limit: Int = 20, truncate: Int = 40): String {
    val sb = StringBuilder()
    sb.append("<html><body>")
    sb.append("<table><tr>")
    columns.forEach {
        sb.append("<th style=\"text-align:left\">${it.name}</th>")
    }
    sb.append("</tr>")
    rows.take(limit).forEach {
        sb.append("<tr>")
        it.values.forEach {
            val tooltip: String
            val content: String
            when(it) {
                is Image -> {
                    tooltip = it.url
                    content = "<img src=\"${it.url}\"/>"
                }
                else -> {
                    tooltip = it.toString()
                    content = tooltip.truncate(truncate)
                }
            }
            sb.append("<td style=\"text-align:left\" title=\"$tooltip\">$content</td>")
        }
        sb.append("</tr>")
    }
    sb.append("</table>")
    if (limit < nrow)
        sb.append("<p>... only showing top $limit of $nrow rows</p>")
    sb.append("</body></html>")
    return sb.toString()
}

internal fun renderSchema(df: DataFrame<*>): String =
        df.columns.map { "${it.name}:${renderType(it)}"}.joinToString()

internal fun renderType(column: DataCol) =
    when(column.kind()) {
        ColumnKind.Data -> {
            val type = column.type
            val result = type.toString()
            if (result.startsWith("kotlin.")) result.substring(7)
            else result
        }
        ColumnKind.Table -> {
            val table = column.asTable()
            "[${renderSchema(table.df)}]"
        }
        ColumnKind.Group -> {
            val group = column.asGrouped()
            "{${renderSchema(group.df)}}"
        }
    }

internal fun renderValue(value: Any?) =
    when {
        value is DataFrame<*> -> "${value.nrow} rows"
        else -> value.toString()
    }

internal fun DataFrame<*>.renderToString(limit: Int = 20, truncate: Int = 40): String {
    val sb = StringBuilder()
    sb.appendLine("Data Frame [${size()}]")
    sb.appendLine()

    val outputRows = limit.coerceAtMost(nrow)
    val output = columns.map { it.values.take(limit).map { renderValue(it).truncate(truncate) } }
    val header = columns.map { "${it.name}:${renderType(it)}"}
    val columnLengths = output.mapIndexed { col, values -> (values + header[col]).map { it.length }.max()!! + 1 }

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

    for(row in 0 until outputRows){
        sb.append("|")
        for(col in output.indices){
            sb.append(output[col][row].padEnd(columnLengths[col]) + "|")
        }
        sb.appendLine()
    }
    if(nrow > limit)
        sb.appendLine("...")
    return sb.toString()
}