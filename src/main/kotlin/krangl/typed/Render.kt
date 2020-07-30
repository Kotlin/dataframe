package krangl.typed

internal fun String.truncate(limit: Int) = if (limit in 1 until length) {
    if (limit < 4) substring(0, limit)
    else substring(0, limit - 3) + "..."
} else {
    this
}

fun TypedDataFrame<*>.toHTML(limit: Int = 20, truncate: Int = 50): String {
    val sb = StringBuilder()
    sb.append("<html><body>")
    sb.append("<table><tr>")
    columns.forEach {
        sb.append("<th style=\"text-align:left\">${it.name}</th>")
    }
    sb.append("</tr>")
    rows.take(limit).forEach {
        sb.append("<tr>")
        it.values.map { it.toString() }.forEach {
            val truncated = it.truncate(truncate)
            sb.append("<td style=\"text-align:left\" title=\"$it\">$truncated</td>")
        }
        sb.append("</tr>")
    }
    sb.append("</table>")
    if (limit < rows.count())
        sb.append("<p>... only showing top $limit rows</p>")
    sb.append("</body></html>")
    return sb.toString()
}

internal fun TypedDataFrame<*>.renderToString(limit: Int = 20, truncate: Int = 20): String {
    val sb = StringBuilder()
    sb.appendln("Data Frame: [$size]")
    sb.appendln()

    val outputRows = limit.coerceAtMost(nrow)
    val output = columns.map { it.values.take(limit).map { it.toString().truncate(truncate) } }
    val header = columnNames()
    val columnLengths = output.mapIndexed { col, values -> (values + header[col]).map { it.length }.max()!! + 1 }

    val width = columnLengths.sum() + columnLengths.size + 1

    sb.append("|")
    for (col in header.indices) {
        sb.append(header[col].padEnd(columnLengths[col]) + "|")
    }
    sb.appendln()
    sb.append("|")
    for (colLength in columnLengths) {
        for (i in 1..colLength) sb.append('-')
        sb.append("|")
    }
    sb.appendln()

    for(row in 0 until outputRows){
        sb.append("|")
        for(col in output.indices){
            sb.append(output[col][row].padEnd(columnLengths[col]) + "|")
        }
        sb.appendln()
    }
    if(nrow > limit)
        sb.appendln("...")
    return sb.toString()
}