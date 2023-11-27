package org.jetbrains.kotlinx.dataframe.io

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.FormattingDSL
import org.jetbrains.kotlinx.dataframe.api.RowColFormatter
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.isSubtypeOf
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.depth
import org.jetbrains.kotlinx.dataframe.impl.DataFrameSize
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.scale
import org.jetbrains.kotlinx.dataframe.impl.truncate
import org.jetbrains.kotlinx.dataframe.jupyter.CellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.jetbrains.kotlinx.dataframe.name
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.util.DATAFRAME_HTML_MESSAGE
import org.jetbrains.kotlinx.dataframe.util.DATAFRAME_HTML_REPLACE
import java.awt.Desktop
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Path
import java.util.*
import kotlin.io.path.writeText

internal val tooltipLimit = 1000

internal interface CellContent

internal data class DataFrameReference(val dfId: Int, val size: DataFrameSize) : CellContent

internal data class HtmlContent(val html: String, val style: String?) : CellContent

internal data class ColumnDataForJs(
    val column: AnyCol,
    val nested: List<ColumnDataForJs>,
    val rightAlign: Boolean,
    val values: List<CellContent>,
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

internal fun ColumnDataForJs.renderHeader(): String {
    val tooltip = "${column.name}: ${renderType(column.type())}"
    return "<span title=\"$tooltip\">${column.name()}</span>"
}

internal fun tableJs(columns: List<ColumnDataForJs>, id: Int, rootId: Int, nrow: Int): String {
    var index = 0
    val data = buildString {
        append("[")
        fun appendColWithChildren(col: ColumnDataForJs): Int {
            val children = col.nested.map { appendColWithChildren(it) }
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

            val colName = col.renderHeader().escapeForHtmlInJs()
            append("{ name: \"$colName\", children: $children, rightAlign: ${col.rightAlign}, values: $values }, \n")

            return@appendColWithChildren colIndex
        }
        columns.forEach { appendColWithChildren(it) }
        append("]")
    }
    val js = getResourceText(
        "/addTable.js",
        "___COLUMNS___" to data,
        "___ID___" to id,
        "___ROOT___" to rootId,
        "___NROW___" to nrow
    )
    return js
}

internal var tableInSessionId = 0
internal var sessionId = (Random().nextInt() % 128) shl 24
internal fun nextTableId() = sessionId + (tableInSessionId++)

internal fun AnyFrame.toHtmlData(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer,
): DataFrameHtmlData {
    val scripts = mutableListOf<String>()
    val queue = LinkedList<Pair<AnyFrame, Int>>()

    fun AnyFrame.columnToJs(col: AnyCol, rowsLimit: Int?): ColumnDataForJs {
        val values = if (rowsLimit != null) rows().take(rowsLimit) else rows()
        val scale = if (col.isNumber()) col.asNumbers().scale() else 1
        val format = if (scale > 0) {
            RendererDecimalFormat.fromPrecision(scale)
        } else {
            RendererDecimalFormat.of("%e")
        }
        val renderConfig = configuration.copy(decimalFormat = format)
        val contents = values.map {
            val value = it[col]
            if (value is AnyFrame) {
                if (value.isEmpty()) {
                    HtmlContent("", null)
                } else {
                    val id = nextTableId()
                    queue.add(value to id)
                    DataFrameReference(id, value.size)
                }
            } else {
                val html = formatter.format(value, cellRenderer, renderConfig)
                val style = renderConfig.cellFormatter?.invoke(FormattingDSL, it, col)?.attributes()?.ifEmpty { null }
                    ?.joinToString(";") { "${it.first}:${it.second}" }
                HtmlContent(html, style)
            }
        }
        return ColumnDataForJs(
            col,
            if (col is ColumnGroup<*>) col.columns().map { col.columnToJs(it, rowsLimit) } else emptyList(),
            col.isSubtypeOf<Number?>(),
            contents
        )
    }

    val rootId = nextTableId()
    queue.add(this to rootId)
    while (!queue.isEmpty()) {
        val (nextDf, nextId) = queue.pop()
        val rowsLimit = if (nextId == rootId) configuration.rowsLimit else configuration.nestedRowsLimit
        val preparedColumns = nextDf.columns().map { nextDf.columnToJs(it, rowsLimit) }
        val js = tableJs(preparedColumns, nextId, rootId, nextDf.nrow)
        scripts.add(js)
    }
    val body = getResourceText("/table.html", "ID" to rootId)
    val script = scripts.joinToString("\n") + "\n" + getResourceText("/renderTable.js", "___ID___" to rootId)
    return DataFrameHtmlData("", body, script)
}

/**
 * Renders [this] [DataFrame] as static HTML (meaning no JS is used).
 * CSS rendering is enabled by default but can be turned off using [includeCss]
 */
public fun AnyFrame.toStaticHtml(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer = DefaultCellRenderer,
    includeCss: Boolean = true,
): DataFrameHtmlData {
    val df = this
    val id = "static_df_${nextTableId()}"

    // Retrieve all columns, including nested ones
    val flattenedCols = getColumnsWithPaths { cols { !it.isColumnGroup() }.recursively() }

    // Get a grid of columns for the header, as well as the side borders for each cell
    val colGrid = getColumnsHeaderGrid()
    val borders = colGrid.last().map { it.borders - Border.BOTTOM }

    // Limit for number of rows in dataframes inside frame columns
    val nestedRowsLimit = configuration.nestedRowsLimit

    // Adds the given tag to the html, with the given attributes and contents
    fun StringBuilder.emitTag(tag: String, attributes: String = "", tagContents: StringBuilder.() -> Unit) {
        append("<$tag")
        if (attributes.isNotEmpty()) {
            append(" ")
            append(attributes)
        }
        append(">")
        tagContents()
        append("</$tag>")
    }

    // Adds a header to the html. This header contains all the column names including nested ones
    // properly laid out with borders.
    fun StringBuilder.emitHeader() = emitTag("thead") {
        for (row in colGrid) {
            emitTag("tr") {
                for ((j, col) in row.withIndex()) {
                    val colBorders = col.borders.toMutableSet()
                    // check if the next cell has a left border, and if so, add a right border to this cell
                    if (row.getOrNull(j + 1)?.borders?.contains(Border.LEFT) == true) {
                        colBorders += Border.RIGHT
                    }
                    emitTag("th", colBorders.toClass()) {
                        append(col.columnWithPath?.name ?: "")
                    }
                }
            }
        }
    }

    // Adds a single cell to the html. DataRows from column groups already need to be split up into separate cells.
    fun StringBuilder.emitCell(cellValue: Any?, borders: Set<Border>): Unit =
        emitTag("td", borders.toClass()) {
            when (cellValue) {
                // uses the <details> and <summary> to create a collapsible cell for dataframes
                is AnyFrame ->
                    emitTag("details") {
                        emitTag("summary") {
                            append("DataFrame [${cellValue.size}]")
                        }
                        // add the dataframe as a nested table limiting the number of rows if needed
                        // CSS will not be included here, as it is already included in the main table
                        append(
                            cellValue.take(nestedRowsLimit ?: Int.MAX_VALUE)
                                .toStaticHtml(configuration, cellRenderer, includeCss = false)
                                .toString()
                        )
                        val size = cellValue.rowsCount()
                        if (size > (nestedRowsLimit ?: Int.MAX_VALUE)) {
                            emitTag("p") {
                                append("... showing only top $nestedRowsLimit of $size rows")
                            }
                        }
                    }

                // Else use the default cell renderer
                else ->
                    append(cellRenderer.content(cellValue, configuration).truncatedContent)
            }
        }

    // Adds a single row to the html. This row uses the flattened columns to get them displayed non-collapsed.
    fun StringBuilder.emitRow(row: AnyRow) = emitTag("tr") {
        for ((i, col) in flattenedCols.withIndex()) {
            val border = borders[i].toMutableSet()
            // check if the next cell has a left border, and if so, add a right border to this cell
            if (borders.getOrNull(i + 1)?.contains(Border.LEFT) == true) {
                border += Border.RIGHT
            }
            val cell = row[col.path()]
            emitCell(cell, border)
        }
    }

    // Adds the body of the html. This body contains all the cols and rows of the dataframe.
    fun StringBuilder.emitBody() = emitTag("tbody") {
        val rowsCountToRender = minOf(rowsCount(), configuration.rowsLimit ?: Int.MAX_VALUE)
        for (rowIndex in 0..<rowsCountToRender) {
            emitRow(df[rowIndex])
        }
    }

    // Base function which adds the table to the html.
    fun StringBuilder.emitTable() = emitTag("table", """class="dataframe" id="$id"""") {
        emitHeader()
        emitBody()
    }

    return DataFrameHtmlData(
        body = buildString { emitTable() },
        // will hide the table if JS is enabled
        script = """
            document.getElementById("$id").style.display = "none";
        """.trimIndent(),
    ) + DataFrameHtmlData.tableDefinitions(includeJs = false, includeCss = includeCss)
}

/**
 * Border enum used for static rendering of tables in html/css.
 * @see toClass
 */
private enum class Border(val className: String) {
    // NOTE: these don't render unless at leftmost; add rightBorder to the previous cell.
    LEFT("leftBorder"),
    RIGHT("rightBorder"),
    BOTTOM("bottomBorder");
}

/**
 * Converts a set of borders to a class string for html/css rendering.
 */
private fun Set<Border>.toClass(): String =
    if (isEmpty()) ""
    else "class=\"${joinToString(" ") { it.className }}\""

/**
 * Wrapper class which contains a nullable [ColumnWithPath] and a set of [Border]s.
 * (Empty cells can have borders too)
 */
private data class ColumnWithPathWithBorder<T>(
    val columnWithPath: ColumnWithPath<T>? = null,
    val borders: Set<Border> = emptySet(),
)

/** Returns the depth of the most-nested column in this df/group, starting at 0 */
internal fun AnyFrame.maxDepth(): Int =
    getColumnsWithPaths { all().rec() }.maxOfOrNull { it.depth } ?: 0

/** Returns the max number of columns needed to display this column flattened */
internal fun BaseColumn<*>.maxWidth(): Int =
    if (this is ColumnGroup<*>) columns().sumOf { it.maxWidth() }.coerceAtLeast(1)
    else 1

/**
 * Given a [DataFrame], this function returns a depth-first "matrix" containing all columns
 * laid out in such a way that they can be used to render the header of a table. The
 * [ColumnWithPathWithBorder.columnWithPath] is `null` when nothing should be rendered in that cell.
 * Borders are included too, also for `null` cells.
 *
 * For example:
 * ```
 * `colGroup` `    |` `|colD` `colC`
 * `colA    ` `colB|` `|    ` `    `
 *  ----       ----   ----    ----
 * ```
 */
private fun AnyFrame.getColumnsHeaderGrid(): List<List<ColumnWithPathWithBorder<*>>> {
    val colGroup = asColumnGroup()
    val maxDepth = maxDepth()
    val maxWidth = colGroup.maxWidth()
    val matrix =
        MutableList(maxDepth + 1) { MutableList(maxWidth) { ColumnWithPathWithBorder<Any?>() } }

    fun ColumnWithPath<*>.addChildren(depth: Int = 0, breadth: Int = 0) {
        var breadth = breadth
        val children = children()
        val lastIndex = children.lastIndex
        for ((i, child) in children().withIndex()) {
            matrix[depth][breadth] = matrix[depth][breadth].copy(columnWithPath = child)

            // draw colGroup side borders unless at start/end of table
            val borders = mutableSetOf<Border>()
            if (i == 0 && breadth != 0) borders += Border.LEFT
            if (i == lastIndex && breadth != maxWidth - 1) borders += Border.RIGHT

            // draw bottom border if at max depth
            if (depth == maxDepth) borders += Border.BOTTOM

            if (borders.isNotEmpty()) {
                // draw borders in other cells
                // from depth - 1 (to include not just the children but the current cell too)
                for (j in (depth - 1).coerceAtLeast(0)..maxDepth) {
                    matrix[j][breadth] = matrix[j][breadth].let { it.copy(borders = it.borders + borders) }
                }
            }

            // recurse if needed to render children of the current column group
            if (child is ColumnGroup<*>) {
                child.addChildren(depth + 1, breadth)
            }
            breadth += child.maxWidth()
        }
    }
    colGroup.addPath().addChildren()
    return matrix
}

internal fun DataFrameHtmlData.print() = println(this)

@Deprecated(
    message = DATAFRAME_HTML_MESSAGE,
    replaceWith = ReplaceWith(DATAFRAME_HTML_REPLACE, "org.jetbrains.kotlinx.dataframe.io.toStandaloneHTML"),
    level = DeprecationLevel.ERROR,
)
public fun <T> DataFrame<T>.html(): String = toStandaloneHTML().toString()

/**
 * @return DataFrameHtmlData with table script and css definitions. Can be saved as an *.html file and displayed in the browser
 */
public fun <T> DataFrame<T>.toStandaloneHTML(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer = DefaultCellRenderer,
    getFooter: (DataFrame<T>) -> String? = { "DataFrame [${it.size}]" },
): DataFrameHtmlData = toHTML(configuration, cellRenderer, getFooter).withTableDefinitions()

/**
 * @return DataFrameHtmlData without additional definitions. Can be rendered in Jupyter kernel environments
 */
public fun <T> DataFrame<T>.toHTML(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer = DefaultCellRenderer,
    getFooter: (DataFrame<T>) -> String? = { "DataFrame [${it.size}]" },
): DataFrameHtmlData {
    val limit = configuration.rowsLimit ?: Int.MAX_VALUE

    val footer = getFooter(this)
    val bodyFooter = footer?.let {
        buildString {
            val openPTag = "<p class=\"dataframe_description\">"
            if (limit < nrow) {
                append(openPTag)
                append("... showing only top $limit of $nrow rows</p>")
            }
            append(openPTag)
            append(footer)
            append("</p>")
        }
    }

    var tableHtml = toHtmlData(configuration, cellRenderer)

    if (configuration.enableFallbackStaticTables) {
        tableHtml += toStaticHtml(configuration, DefaultCellRenderer)
    }

    if (bodyFooter != null) {
        tableHtml += DataFrameHtmlData("", bodyFooter, "")
    }

    return tableHtml
}

/**
 * Container for HTML page data in form of String
 * Can be used to compose rendered dataframe tables with additional HTML elements
 */
public data class DataFrameHtmlData(
    @Language("css") val style: String = "",
    @Language("html", prefix = "<body>", suffix = "</body>") val body: String = "",
    @Language("js") val script: String = "",
) {
    override fun toString(): String = buildString {
        appendLine("<html>")
        if (style.isNotBlank()) {
            appendLine("<head>")
            appendLine("<style type=\"text/css\">")
            appendLine(style)
            appendLine("</style>")
            appendLine("</head>")
        }
        if (body.isNotBlank()) {
            appendLine("<body>")
            appendLine(body)
            appendLine("</body>")
        }
        if (script.isNotBlank()) {
            appendLine("<script>")
            appendLine(script)
            appendLine("</script>")
        }
        appendLine("</html>")
    }

    public operator fun plus(other: DataFrameHtmlData): DataFrameHtmlData =
        DataFrameHtmlData(
            style = when {
                style.isBlank() -> other.style
                other.style.isBlank() -> style
                else -> style + "\n" + other.style
            },
            body = when {
                body.isBlank() -> other.body
                other.body.isBlank() -> body
                else -> body + "\n" + other.body
            },
            script = when {
                script.isBlank() -> other.script
                other.script.isBlank() -> script
                else -> script + "\n" + other.script
            },
        )

    public fun writeHTML(destination: File) {
        destination.writeText(toString())
    }

    public fun writeHTML(destination: Path) {
        destination.writeText(toString())
    }

    public fun openInBrowser() {
        val file = File.createTempFile("df_rendering", ".html")
        writeHTML(file)
        val uri = file.toURI()
        val desktop = Desktop.getDesktop()
        desktop.browse(uri)
    }

    public fun withTableDefinitions(): DataFrameHtmlData = tableDefinitions() + this

    public companion object {
        /**
         * @return CSS and JS required to render DataFrame tables
         * Can be used as a starting point to create page with multiple tables
         * @see DataFrame.toHTML
         * @see DataFrameHtmlData.plus
         */
        public fun tableDefinitions(
            includeJs: Boolean = true,
            includeCss: Boolean = true,
        ): DataFrameHtmlData = DataFrameHtmlData(
            style = if (includeCss) getResources("/table.css") else "",
            script = if (includeJs) getResourceText("/init.js") else "",
            body = "",
        )
    }
}

/**
 * @param rowsLimit null to disable rows limit
 * @param cellContentLimit -1 to disable content trimming
 * @param enableFallbackStaticTables true to add additional pure HTML table that will be visible only if JS  is disabled;
 * For example hosting *.ipynb files with outputs on GitHub
 */
public data class DisplayConfiguration(
    var rowsLimit: Int? = 20,
    var nestedRowsLimit: Int? = 5,
    var cellContentLimit: Int = 40,
    var cellFormatter: RowColFormatter<*, *>? = null,
    var decimalFormat: RendererDecimalFormat = RendererDecimalFormat.DEFAULT,
    var isolatedOutputs: Boolean = flagFromEnv("LETS_PLOT_HTML_ISOLATED_FRAME"),
    internal val localTesting: Boolean = flagFromEnv("KOTLIN_DATAFRAME_LOCAL_TESTING"),
    var useDarkColorScheme: Boolean = false,
    var enableFallbackStaticTables: Boolean = true,
) {
    public companion object {
        public val DEFAULT: DisplayConfiguration = DisplayConfiguration()
    }

    /** DSL accessor. */
    public operator fun invoke(block: DisplayConfiguration.() -> Unit): DisplayConfiguration = apply(block)
}

@JvmInline
public value class RendererDecimalFormat private constructor(internal val format: String) {
    public companion object {
        public fun fromPrecision(precision: Int): RendererDecimalFormat {
            require(precision >= 0) { "precision must be >= 0. for custom format use RendererDecimalFormat.of" }
            return RendererDecimalFormat("%.${precision}f")
        }

        public fun of(format: String): RendererDecimalFormat {
            return RendererDecimalFormat(format)
        }

        internal val DEFAULT: RendererDecimalFormat = fromPrecision(defaultPrecision)
    }
}

internal const val defaultPrecision = 6

private fun flagFromEnv(envName: String): Boolean {
    return System.getenv(envName)?.toBooleanStrictOrNull() ?: false
}

internal fun String.escapeNewLines() = replace("\n", "\\n").replace("\r", "\\r")

internal fun String.escapeForHtmlInJs() = replace("\"", "\\\"").escapeNewLines()

internal fun renderValueForHtml(value: Any?, truncate: Int, format: RendererDecimalFormat): RenderedContent {
    return formatter.truncate(renderValueToString(value, format), truncate)
}

internal fun String.escapeHTML(): String {
    val str = this
    return buildString {
        for (c in str) {
            if (c.code > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&' || c == '\\') {
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
    val dataFrameClass: String,
) {

    private fun wrap(prefix: String, content: RenderedContent, postfix: String): RenderedContent =
        content.copy(truncatedContent = prefix + content.truncatedContent + postfix)

    private fun String.addCss(css: String? = null): RenderedContent = RenderedContent.text(this).addCss(css)

    private fun String.ellipsis(fullText: String) = addCss(structuralClass).copy(fullContent = fullText)

    private fun String.structural() = addCss(structuralClass)

    private fun RenderedContent.addCss(css: String? = null): RenderedContent {
        return if (css != null) {
            copy(truncatedContent = "<span class=\"$css\">$truncatedContent</span>", isFormatted = true)
        } else this
    }

    internal fun truncate(str: String, limit: Int): RenderedContent {
        return if (limit in 1 until str.length) {
            val ellipsis = "...".ellipsis(str)
            if (limit < 4) ellipsis
            else {
                val len = (limit - 3).coerceAtLeast(1)
                RenderedContent.textWithLength(str.substring(0, len).escapeHTML(), len) + ellipsis
            }
        } else {
            RenderedContent.textWithLength(str.escapeHTML(), str.length)
        }
    }

    fun format(
        value: Any?,
        renderer: CellRenderer,
        configuration: DisplayConfiguration,
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
                        )?.textLength ?: 3
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
                            3 -> "...".structural()
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
            is List<*> ->
                when {
                    value.isEmpty() -> "[ ]".addCss(nullClass)
                    else -> renderList(value, "[", "]").let {
                        it.copy(fullContent = value.joinToString("\n") { it.toString() })
                    }
                }

            is Pair<*, *> -> {
                val key = value.first.toString() + ": "
                val shortValue =
                    render(value.second, renderer, configuration.copy(cellContentLimit = 3)) ?: "...".addCss(
                        structuralClass
                    )
                val sizeOfValue = shortValue.textLength
                val keyLimit = limit - sizeOfValue
                if (key.length > keyLimit) {
                    if (limit > 3) {
                        ("$key...").truncate(limit).addCss(structuralClass)
                    } else null
                } else {
                    val renderedValue =
                        render(value.second, renderer, configuration.copy(cellContentLimit = limit - key.length))
                            ?: "...".addCss(structuralClass)
                    key.addCss(structuralClass) + renderedValue
                }
            }

            is Number -> renderer.content(value, configuration).addCss(numberClass)
            is URL -> wrap(
                "<a href='$value' target='_blank'>",
                renderer.content(value.toString(), configuration),
                "</a>"
            )

            is DataFrameHtmlData -> RenderedContent.text(value.body)
            else -> renderer.content(value, configuration)
        }
        if (result != null && result.textLength > configuration.cellContentLimit) return null
        return result
    }
}
