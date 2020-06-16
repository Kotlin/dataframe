package krangl.typed

fun TypedDataFrame<*>.toHTML(limit: Int = 20, truncate: Int = 50) : String {
    val sb = StringBuilder()
    sb.append("<html><body>")
    sb.append("<table><tr>")
    columns.forEach {
        sb.append("<th style=\"text-align:left\">${it.name}</th>")
    }
    sb.append("</tr>")
    rows.take(limit).forEach {
        sb.append("<tr>")
        it.values.map { it.second.toString() }.forEach {
            val truncated = if (truncate > 0 && it.length > truncate) {
                if (truncate < 4) it.substring(0, truncate)
                else it.substring(0, truncate - 3) + "..."
            } else {
                it
            }
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