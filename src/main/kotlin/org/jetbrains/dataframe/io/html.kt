package org.jetbrains.dataframe.io

import khttp.post
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

internal data class DataFrameReference(val dfId: Int, val size: DataFrameSize)

internal data class ColumnDataForJs(
    val name: String,
    val nested: List<ColumnDataForJs>,
    val rightAlign: Boolean,
    val values: List<Any>
)

internal val formatter = DataFrameFormatter(
    "formatted",
    "formatNull",
    "formatCurlyBrackets",
    "formatNumbers",
    "formatDataframes",
    "formatComma",
    "formatComma",
    "formatColumnNames",
    "formatSquareBrackets"
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

internal fun tableJs(columns: List<ColumnDataForJs>, id: Int): String {
    var index = 0
    val data = buildString {
        append("[")
        fun dfs(col: ColumnDataForJs): Int {
            val children = col.nested.map { dfs(it) }
            val colIndex = index++
            val values = col.values.joinToString(",", prefix = "[", postfix = "]") {
                when (it) {
                    is String -> "\"" + it.escapeForHtmlInJs() + "\""
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
    val js = getResourceText("/addTable.js", "COLUMNS" to data, "ID" to id)
    return js
}

internal var tableId = 0

// TODO: use configuration.cellFormatter to format cells (currently disabled)
// TODO: display tooltips for column headers and data

internal fun AnyFrame.toHtmlData(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer
): HtmlData {
    val scripts = mutableListOf<String>()
    val queue = LinkedList<Pair<AnyFrame, Int>>()

    fun AnyCol.toJs(): ColumnDataForJs {
        val values = values().take(configuration.rowsLimit).map {
            if (it is AnyFrame) {
                val id = tableId++
                queue.add(it to id)
                DataFrameReference(id, it.size)
            } else formatter.format(it, cellRenderer, configuration)
        }
        return ColumnDataForJs(
            name(),
            if (this is ColumnGroup<*>) columns().map { it.toJs() } else emptyList(),
            isSubtypeOf<Number?>(),
            values
        )
    }

    val id = tableId++
    queue.add(this to id)
    while (!queue.isEmpty()) {
        val (nextDf, nextId) = queue.pop()
        val preparedColumns = nextDf.columns().map { it.toJs() }
        val js = tableJs(preparedColumns, nextId)
        scripts.add(js)
    }
    val body = getResourceText("/table.html", "ID" to id)
    val script = scripts.joinToString("\n") + "\n" + getResourceText("/renderTable.js", "ID" to id)
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

internal fun String.escapeForHtmlInJsMultiline() = replace("\"", "\\\"").replace("\n", "<br>")

internal fun renderValueForHtml(value: Any?, truncate: Int): RenderedContent {
    val truncated = renderValueToString(value).truncate(truncate)
    return RenderedContent(truncated.escapeHTML(), truncated.length)
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
    val curlyBracketsClass: String,
    val numberClass: String,
    val dataFrameClass: String,
    val commaClass: String,
    val ellipsisClass: String,
    val colNameClass: String,
    val squareBracketsClass: String
) {

    private class FormatBuilder {

        var isFormatted: Boolean = false

        fun wrap(prefix: String, content: RenderedContent, postfix: String) = content.copy(content = prefix + content + postfix)

        fun String.addCss(css: String? = null): RenderedContent = RenderedContent.text(this).addCss(css)

        fun RenderedContent.addCss(css: String? = null): RenderedContent {
            return if (css != null) {
                isFormatted = true
                copy(content = "<span class=\"$css\">" + content + "</span>")
            } else this
        }
    }

    fun format(value: Any?, renderer: CellRenderer, configuration: DisplayConfiguration): String {
        val builder = FormatBuilder()
        val result = builder.render(value, renderer, configuration)?.let { if(builder.isFormatted) with(builder) { it.addCss(formattedClass) } else it }
        return result?.content ?: ""
    }

    private class Builder {
        private val sb = StringBuilder()

        var len: Int = 0
            private set;

        operator fun plusAssign(content: RenderedContent?) {
            if(content == null) return
            sb.append(content.content)
            len += content.textLength
        }

        fun result() = RenderedContent(sb.toString(), len)
    }

    private fun FormatBuilder.render(value: Any?, renderer: CellRenderer, configuration: DisplayConfiguration): RenderedContent? {

        val limit = configuration.cellContentLimit

        fun renderList(values: List<*>, prefix: String, postfix: String): RenderedContent {
            val sb = Builder()
            sb += prefix.addCss(squareBracketsClass)
            for(index in values.indices) {
                if (index > 0) {
                    sb += ", ".addCss(commaClass)
                }
                if(index < values.size - 1 && limit <= sb.len + "...".length + postfix.length){
                    if(limit == sb.len + "..".length + postfix.length)
                        sb += "..".addCss(ellipsisClass)
                    else
                        sb += "...".addCss(ellipsisClass)
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
                if(rendered == null) {
                    if(limit == sb.len + "..".length + postfix.length)
                        sb += "..".addCss(ellipsisClass)
                    else
                        sb += "...".addCss(ellipsisClass)
                    break
                }
                sb += rendered
            }
            sb += postfix.addCss(squareBracketsClass)
            return sb.result()
        }

        val result = when (value) {
            null -> "null".addCss(nullClass)
            is AnyRow -> {
                val values = value.getVisibleValues()
                when {
                    values.isEmpty() -> "{ }".addCss(nullClass)
                    else -> when (limit) {
                        4 -> "{..}".addCss(ellipsisClass)
                        5 -> "{...}".addCss(ellipsisClass)
                        6 -> "{ ...}".addCss(ellipsisClass)
                        7 -> "{ ... }".addCss(ellipsisClass)
                        else -> renderList(values, "{ ", " }")
                    }
                }
            }
            is AnyFrame -> renderer.content("DataFrame [${value.size}]", configuration).addCss(dataFrameClass)
            is AnyMany ->
                when {
                    value.isEmpty() -> "[ ]".addCss(nullClass)
                    else -> renderList(value, "[", "]")
                }
            is Pair<*, *> -> {
                val key = value.first.toString() + ": "
                val sizeOfValue = render(value.second, renderer, configuration.copy(cellContentLimit = 4))!!.textLength
                val keyLimit = limit - sizeOfValue
                if(key.length > keyLimit) {
                    if(limit > 3)
                        key.take(limit - 3).addCss(colNameClass) + "...".addCss(ellipsisClass)
                    else null
                }
                else {
                    key.addCss(colNameClass) + render(value.second, renderer, configuration.copy(cellContentLimit = limit - key.length))!!
                }
            }
            is Number -> renderer.content(value, configuration).addCss(numberClass)
            is URL -> wrap("<a href='$value' target='_blank'>", renderer.content(value.toString(), configuration), "</a>")
            is HtmlData -> RenderedContent.text(value.body)
            else -> renderer.content(value, configuration)
        }
        if(result != null && result.textLength > configuration.cellContentLimit) return null
        return result
    }
}
