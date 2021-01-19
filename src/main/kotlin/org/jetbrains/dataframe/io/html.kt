package org.jetbrains.dataframe.io

import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.Image
import org.jetbrains.dataframe.truncate

fun DataFrame<*>.toHTML(limit: Int = 20, truncate: Int = 40): String {
    val sb = StringBuilder()
    sb.append("<html><body>")
    sb.append("<table><tr>")
    columns().forEach {
        sb.append("<th style=\"text-align:left\">${it.name()}</th>")
    }
    sb.append("</tr>")
    rows().take(limit).forEach {
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
                    tooltip = renderValue(it)
                    content = tooltip.truncate(truncate)
                }
            }
            sb.append("<td style=\"text-align:left\" title=\"$tooltip\">$content</td>")
        }
        sb.append("</tr>")
    }
    sb.append("</table>")
    if (limit < nrow())
        sb.append("<p>... only showing top $limit of ${nrow()} rows</p>")
    sb.append("</body></html>")
    return sb.toString()
}
