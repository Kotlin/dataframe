package org.jetbrains.dataframe.io

import org.jetbrains.dataframe.AnyFrame
import org.jetbrains.dataframe.AnyMany
import org.jetbrains.dataframe.AnyRow
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.DataFrameSize
import org.jetbrains.dataframe.RowColFormatter
import org.jetbrains.dataframe.columns.AnyCol
import org.jetbrains.dataframe.columns.ColumnGroup
import org.jetbrains.dataframe.impl.truncate
import org.jetbrains.dataframe.isSubtypeOf
import org.jetbrains.dataframe.jupyter.CellRenderer
import org.jetbrains.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.dataframe.jupyter.RenderedContent
import org.jetbrains.dataframe.size
import org.jetbrains.kotlinx.jupyter.api.HTML
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import java.io.InputStreamReader
import java.net.URL
import java.util.LinkedList

internal val tooltipLimit = 1000

internal fun getDefaultFooter(df: DataFrame<*>): String {
    return "DataFrame [${df.size}]"
}

internal interface CellContent

internal data class DataFrameReference(val dfId: Int, val size: DataFrameSize) : CellContent

internal data class HtmlContent(val html: String, val style: String?) : CellContent

internal data class ColumnDataForJs(
    val name: String,
    val nested: List<ColumnDataForJs>,
    val rightAlign: Boolean,
    val values: List<CellContent>
)

internal val formatter = DataFrameFormatter(
    "formatted",
    "null",
    "structural",
    "numbers",
    "dataFrameCaption",
)

internal fun getResources(vararg resource: String) = resource.joinToString(separator = "\n") { getResourceText(it) }

internal fun getResourceText(resource: String, vararg replacement: Pair<String, Any>): String {
    val res = DataFrame::class.java.getResourceAsStream(resource) ?: error("Resource '$resource' not found")
    var template = InputStreamReader(res).readText()
    replacement.forEach {
        template = template.replace(it.first, it.second.toString())
    }
    return template
}

internal fun tableJs(columns: List<ColumnDataForJs>, id: Int, rootId: Int): String {
    var index = 0
    val data = buildString {
        append("[")
        fun dfs(col: ColumnDataForJs): Int {
            val children = col.nested.map { dfs(it) }
            val colIndex = index++
            val values = col.values.joinToString(",", prefix = "[", postfix = "]") {
                when (it) {
                    is HtmlContent -> {
                        val html = "\"" + it.html.escapeForHtmlInJs() + "\""
                        if (it.style == null) {
                            html
                        } else "{ style: \"${it.style}\", value: $html}"
                    }
                    is DataFrameReference -> {
                        val text = "<b>DataFrame ${it.size}</b>"
                        "{ frameId: ${it.dfId}, value: \"$text\" }"
                    }
                    else -> error("Unsupported value type: ${it.javaClass}")
                }
            }
            append("{ name: \"${col.name.escapeForHtmlInJs()}\", children: $children, rightAlign: ${col.rightAlign}, values: $values }, \n")
            return colIndex
        }
        columns.forEach { dfs(it) }
        append("]")
    }
    val js = getResourceText("/addTable.js", "COLUMNS" to data, "ID" to id, "ROOT" to rootId)
    return js
}

internal var tableId = 0

// TODO: display tooltips for column headers
internal fun AnyFrame.toHtmlData(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer
): HtmlData {
    val scripts = mutableListOf<String>()
    val queue = LinkedList<Pair<AnyFrame, Int>>()

    fun AnyFrame.columnToJs(col: AnyCol): ColumnDataForJs {
        val values = rows().take(configuration.rowsLimit).map {
            val value = it[col]
            if (value is AnyFrame) {
                val id = tableId++
                queue.add(value to id)
                DataFrameReference(id, value.size)
            } else {
                val html = formatter.format(value, cellRenderer, configuration)
                val style = configuration.cellFormatter?.invoke(it, col)?.attributes()?.ifEmpty { null }?.joinToString(";") { "${it.first}:${it.second}" }
                HtmlContent(html, style)
            }
        }
        return ColumnDataForJs(
            col.name(),
            if (col is ColumnGroup<*>) col.columns().map { col.columnToJs(it) } else emptyList(),
            col.isSubtypeOf<Number?>(),
            values
        )
    }

    val rootId = tableId++
    queue.add(this to rootId)
    while (!queue.isEmpty()) {
        val (nextDf, nextId) = queue.pop()
        val preparedColumns = nextDf.columns().map { nextDf.columnToJs(it) }
        val js = tableJs(preparedColumns, nextId, rootId)
        scripts.add(js)
    }
    val body = getResourceText("/table.html", "ID" to rootId)
    val script = scripts.joinToString("\n") + "\n" + getResourceText("/renderTable.js", "ID" to rootId)
    return HtmlData("", body, script)
}

public data class HtmlData(val style: String, val body: String, val script: String) {
    override fun toString(): String = """
        <html>
        <head>
            <style type="text/css">
                $style
            </style>
        </head>
        <body>
            $body
        </body>
        <script>
            $script
        </script>
        </html>
    """.trimIndent()

    public fun toJupyter(): MimeTypedResult = HTML(toString())

    public operator fun plus(other: HtmlData): HtmlData =
        HtmlData(style + "\n" + other.style, body + "\n" + other.body, script + "\n" + other.script)
}

internal fun initHtml(): HtmlData =
    HtmlData(style = getResources("/table.css", "/formatting.css"), script = getResourceText("/init.js"), body = "")

public fun <T> DataFrame<T>.html(): String = toHTML(includeInit = true).toString()

public fun <T> DataFrame<T>.toHTML(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    includeInit: Boolean = false,
    cellRenderer: CellRenderer = DefaultCellRenderer,
    getFooter: (DataFrame<T>) -> String = { "DataFrame [${it.size}]" }
): HtmlData {
    val limit = configuration.rowsLimit

    val footer = getFooter(this)
    val bodyFooter = if (limit < nrow()) {
        "<p>... showing only top $limit of ${nrow()} rows</p><p>$footer</p>"
    } else "<p>$footer</p>"

    val tableHtml = toHtmlData(configuration, cellRenderer)
    val html = tableHtml + HtmlData("", bodyFooter, "")

    return if (includeInit) initHtml() + html else html
}

public data class DisplayConfiguration(
    var rowsLimit: Int = 20,
    var cellContentLimit: Int = 40,
    var cellFormatter: RowColFormatter<*>? = null,
) {
    public companion object {
        public val DEFAULT: DisplayConfiguration = DisplayConfiguration()
    }
}

internal fun String.escapeNewLines() = replace("\n", "\\n")

internal fun String.escapeForHtmlInJs() = replace("\"", "\\\"").escapeNewLines()

internal fun renderValueForHtml(value: Any?, truncate: Int): RenderedContent {
    return formatter.truncate(renderValueToString(value), truncate)
}

internal fun String.escapeHTML(): String {
    val str = this
    return buildString {
        for (c in str) {
            if (c.code > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                append("&#")
                append(c.code)
                append(';')
            } else {
                append(c)
            }
        }
    }
}

internal class DataFrameFormatter(
    val formattedClass: String,
    val nullClass: String,
    val structuralClass: String,
    val numberClass: String,
    val dataFrameClass: String
) {

    private fun wrap(prefix: String, content: RenderedContent, postfix: String) = content.copy(truncatedContent = prefix + content + postfix)

    private fun String.addCss(css: String? = null): RenderedContent = RenderedContent.text(this).addCss(css)

    private fun String.ellipsis(fullText: String) = addCss(structuralClass).copy(fullContent = fullText)

    private fun String.structural() = addCss(structuralClass)

    private fun RenderedContent.addCss(css: String? = null): RenderedContent {
        return if (css != null) {
            copy(truncatedContent = "<span class=\"$css\">" + truncatedContent + "</span>", isFormatted = true)
        } else this
    }

    internal fun truncate(str: String, limit: Int): RenderedContent {
        return if (limit in 1 until str.length) {
            val ellipsis = "...".ellipsis(str)
            if (limit < 4) ellipsis
            else RenderedContent.text(str.substring(0, Math.max(limit - 3, 1)).escapeHTML()) + ellipsis
        } else {
            RenderedContent.text(str.escapeHTML())
        }
    }

    fun format(
        value: Any?,
        renderer: CellRenderer,
        configuration: DisplayConfiguration
    ): String {
        val result = render(value, renderer, configuration)
        return when {
            result == null -> ""
            result.isFormatted || result.isTruncated -> {
                val tooltip = result.fullContent?.escapeHTML() ?: ""
                return "<span class=\"$formattedClass\" title=\"$tooltip\">${result.truncatedContent}</span>"
            }
            else -> result.truncatedContent
        }
    }

    private class Builder {

        private val sb = StringBuilder()

        private var isFormatted: Boolean = false

        var len: Int = 0
            private set

        var isTruncated: Boolean = false

        operator fun plusAssign(content: RenderedContent?) {
            if (content == null) return
            sb.append(content.truncatedContent)
            len += content.textLength
            if (content.isTruncated) isTruncated = true
            if (content.isFormatted) isFormatted = true
        }

        fun result() = RenderedContent(sb.toString(), len, if (isTruncated) "" else null, isFormatted)
    }

    private fun render(value: Any?, renderer: CellRenderer, configuration: DisplayConfiguration): RenderedContent? {
        val limit = configuration.cellContentLimit

        fun renderList(values: List<*>, prefix: String, postfix: String): RenderedContent {
            val sb = Builder()
            sb += prefix.addCss(structuralClass)
            for (index in values.indices) {
                if (index > 0) {
                    sb += ", ".addCss(structuralClass)
                }
                fun addEllipsis() {
                    if (limit == sb.len + "..".length + postfix.length) {
                        sb += "..".addCss(structuralClass)
                    } else {
                        sb += "...".addCss(structuralClass)
                    }
                    sb.isTruncated = true
                }

                if (index < values.size - 1 && limit <= sb.len + "...".length + postfix.length) {
                    addEllipsis()
                    break
                }
                val valueLimit = when (index) {
                    values.size - 1 -> limit - sb.len - postfix.length // last
                    values.size - 2 -> { // prev before last
                        val sizeOfLast = render(
                            values.last(),
                            renderer,
                            configuration.copy(cellContentLimit = 4)
                        )!!.textLength
                        limit - sb.len - ", ".length - sizeOfLast - postfix.length
                    }
                    else -> limit - sb.len - ", ...".length - postfix.length
                }

                val rendered = render(values[index], renderer, configuration.copy(cellContentLimit = valueLimit))
                if (rendered == null || (rendered.textLength == 3 && rendered.isTruncated)) {
                    addEllipsis()
                    break
                }
                sb += rendered
            }
            sb += postfix.addCss(structuralClass)
            return sb.result()
        }

        val result = when (value) {
            null -> "null".addCss(nullClass)
            is AnyRow -> {
                val values = value.getVisibleValues()
                when {
                    values.isEmpty() -> "{ }".addCss(nullClass)
                    else -> {
                        when (limit) {
                            4 -> "{..}".structural()
                            5 -> "{...}".structural()
                            6 -> "{ ...}".structural()
                            7 -> "{ ... }".structural()
                            else -> renderList(values, "{ ", " }")
                        }.let {
                            it.copy(fullContent = values.joinToString("\n") { it.first + ": " + it.second })
                        }
                    }
                }
            }
            is AnyFrame -> renderer.content("DataFrame [${value.size}]", configuration).addCss(dataFrameClass)
            is AnyMany ->
                when {
                    value.isEmpty() -> "[ ]".addCss(nullClass)
                    else -> renderList(value, "[", "]").let {
                        it.copy(fullContent = value.joinToString("\n") { it.toString() })
                    }
                }
            is Pair<*, *> -> {
                val key = value.first.toString() + ": "
                val shortValue = render(value.second, renderer, configuration.copy(cellContentLimit = 3))
                val sizeOfValue = shortValue!!.textLength
                val keyLimit = limit - sizeOfValue
                if (key.length > keyLimit) {
                    if (limit > 3) {
                        (key + "...").truncate(limit).addCss(structuralClass)
                    } else null
                } else {
                    key.addCss(structuralClass) + render(value.second, renderer, configuration.copy(cellContentLimit = limit - key.length))!!
                }
            }
            is Number -> renderer.content(value, configuration).addCss(numberClass)
            is URL -> wrap("<a href='$value' target='_blank'>", renderer.content(value.toString(), configuration), "</a>")
            is HtmlData -> RenderedContent.text(value.body)
            else -> renderer.content(value, configuration)
        }
        if (result != null && result.textLength > configuration.cellContentLimit) return null
        return result
    }
}
