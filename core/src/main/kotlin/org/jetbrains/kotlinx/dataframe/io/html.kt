package org.jetbrains.kotlinx.dataframe.io

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.CellAttributes
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl
import org.jetbrains.kotlinx.dataframe.api.HeaderColFormatter
import org.jetbrains.kotlinx.dataframe.api.RowColFormatter
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.isSubtypeOf
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.impl.DataFrameSize
import org.jetbrains.kotlinx.dataframe.impl.columns.addParentPath
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.io.resizeKeepingAspectRatio
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.scale
import org.jetbrains.kotlinx.dataframe.impl.truncate
import org.jetbrains.kotlinx.dataframe.jupyter.CellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.jetbrains.kotlinx.dataframe.name
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.util.DISPLAY_CONFIGURATION
import org.jetbrains.kotlinx.dataframe.util.DISPLAY_CONFIGURATION_COPY
import org.jetbrains.kotlinx.dataframe.util.TO_HTML
import org.jetbrains.kotlinx.dataframe.util.TO_HTML_REPLACE
import org.jetbrains.kotlinx.dataframe.util.TO_STANDALONE_HTML
import org.jetbrains.kotlinx.dataframe.util.TO_STANDALONE_HTML_REPLACE
import org.jetbrains.kotlinx.dataframe.util.WRITE_HTML
import org.jetbrains.kotlinx.dataframe.util.WRITE_HTML_REPLACE
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Path
import java.util.LinkedList
import java.util.Random
import kotlin.io.path.writeText
import kotlin.math.ceil

internal val tooltipLimit = 1000

internal interface CellContent

internal data class DataFrameReference(val dfId: Int, val size: DataFrameSize) : CellContent

internal data class HtmlContent(val html: String, val style: String?) : CellContent

internal data class ColumnDataForJs(
    val column: AnyCol,
    val nested: List<ColumnDataForJs>,
    val rightAlign: Boolean,
    val values: List<CellContent>,
    val headerStyle: String?,
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
    /**
     * The choice of loader is crucial here: it should always be a class loaded by the same class loader as the resource we load.
     * I.e. [DataFrame] isn't a good fit because it might be loaded by Kotlin IDEA plugin (because Kotlin plugin
     * loads DataFrame compiler plugin), and plugin's classloader knows nothing about the resources.
     */
    val loader = HtmlContent::class.java
    val res = loader.getResourceAsStream(resource)
        ?: error("Resource '$resource' not found. Load was attempted by $loader, loaded by ${loader.classLoader}")
    var template = InputStreamReader(res).readText()
    replacement.forEach {
        template = template.replace(it.first, it.second.toString())
    }
    return template
}

internal fun ColumnDataForJs.renderHeader(): String {
    val tooltip = "${column.name}: ${renderType(column.type())}"
    val styleAttr = if (headerStyle != null) " style=\"$headerStyle\"" else ""
    return "<span title=\"$tooltip\"$styleAttr>${column.name()}</span>"
}

internal fun tableJs(
    columns: List<ColumnDataForJs>,
    id: Int,
    rootId: Int,
    nrow: Int,
): String {
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
                        } else {
                            "{ style: \"${it.style}\", value: $html}"
                        }
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
        "___NROW___" to nrow,
    )
    return js
}

internal var tableInSessionId = 0
internal var sessionId = (Random().nextInt() % 128) shl 24

internal fun nextTableId() = sessionId + (tableInSessionId++)

internal fun AnyFrame.toHtmlData(
    defaultConfiguration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer,
): DataFrameHtmlData {
    val scripts = mutableListOf<String>()
    val queue = LinkedList<RenderingQueueItem>()

    fun AnyFrame.columnToJs(
        col: ColumnWithPath<*>,
        rowsLimit: Int?,
        configuration: DisplayConfiguration,
    ): ColumnDataForJs {
        val values = if (rowsLimit != null) rows().take(rowsLimit) else rows()
        val scale = if (col.isNumber()) col.scale() else 1
        val format = if (scale > 0) {
            RendererDecimalFormat.fromPrecision(scale)
        } else {
            RendererDecimalFormat.of("%e")
        }
        val renderConfig = configuration.copy(decimalFormat = format)
        val contents = values.map { row ->
            val value = col[row]
            val dfLikeContent = value.toDataFrameLikeOrNull()
            if (dfLikeContent != null) {
                val df = dfLikeContent.df()
                if (df.isEmpty()) {
                    HtmlContent("", null)
                } else {
                    val id = nextTableId()
                    queue += RenderingQueueItem(df, id, dfLikeContent.configuration(defaultConfiguration))
                    DataFrameReference(id, df.size)
                }
            } else {
                val html = formatter.format(
                    value = downsizeBufferedImageIfNeeded(value, renderConfig),
                    renderer = cellRenderer,
                    configuration = renderConfig,
                )

                val formatter = renderConfig.cellFormatter
                    ?: return@map HtmlContent(html, null)

                // ask formatter for all attributes defined for this cell or any of its parents (outer column groups)
                val parentCols = col.path.indices
                    .map { i -> col.path.take(i + 1) }
                    .dropLast(1)
                    .map { ColumnWithPath(this@toHtmlData[it], it) }
                val parentAttributes = parentCols
                    .map { formatter(FormattingDsl, row, it) }
                    .reduceOrNull(CellAttributes?::and)

                val cellAttributes = formatter(FormattingDsl, row, col)

                val style = (parentAttributes and cellAttributes)
                    ?.attributes()
                    ?.ifEmpty { null }
                    ?.flatMap {
                        if (it.first == "color") {
                            // override all --text-color* variables that
                            // are used to color text of .numbers, .null, etc., inside DataFrame
                            listOf(
                                it,
                                "--text-color" to "${it.second} !important",
                                "--text-color-dark" to "${it.second} !important",
                                "--text-color-pale" to "${it.second} !important",
                                "--text-color-medium" to "${it.second} !important",
                            )
                        } else {
                            listOf(it)
                        }
                    }
                    ?.toMap() // removing duplicate keys, allowing only the final one to be applied
                    ?.entries
                    ?.joinToString(";") { "${it.key}:${it.value}" }
                HtmlContent(html, style)
            }
        }
        val headerStyle = run {
            val hf = configuration.headerFormatter
            if (hf == null) {
                null
            } else {
                // collect attributes from parents
                val parentCols = col.path.indices
                    .map { i -> col.path.take(i + 1) }
                    .dropLast(1)
                    .map { ColumnWithPath(this@toHtmlData[it], it) }
                val parentAttributes = parentCols
                    .map { hf(FormattingDsl, it) }
                    .reduceOrNull(CellAttributes?::and)
                val selfAttributes = hf(FormattingDsl, col)
                val attrs = parentAttributes and selfAttributes
                attrs
                    ?.attributes()
                    ?.ifEmpty { null }
                    ?.toMap()
                    ?.entries
                    ?.joinToString(";") { "${it.key}:${it.value}" }
            }
        }
        val nested = if (col is ColumnGroup<*>) {
            col.columns().map {
                col.columnToJs(it.addParentPath(col.path), rowsLimit, configuration)
            }
        } else {
            emptyList()
        }
        return ColumnDataForJs(
            column = col,
            nested = nested,
            rightAlign = col.isSubtypeOf<Number?>(),
            values = contents,
            headerStyle = headerStyle,
        )
    }

    val rootId = nextTableId()
    queue += RenderingQueueItem(this, rootId, defaultConfiguration)
    while (!queue.isEmpty()) {
        val (nextDf, nextId, configuration) = queue.pop()
        val rowsLimit = if (nextId == rootId) configuration.rowsLimit else configuration.nestedRowsLimit
        val preparedColumns = nextDf.columns().map {
            nextDf.columnToJs(it.addPath(), rowsLimit, configuration)
        }
        val js = tableJs(preparedColumns, nextId, rootId, nextDf.nrow)
        scripts.add(js)
    }
    val body = getResourceText("/table.html", "ID" to rootId)
    val script = scripts.joinToString("\n") + "\n" + getResourceText("/renderTable.js", "___ID___" to rootId)
    return DataFrameHtmlData(style = "", body = body, script = script)
}

private interface DataFrameLike {
    fun configuration(default: DisplayConfiguration): DisplayConfiguration

    fun df(): AnyFrame
}

private fun Any?.toDataFrameLikeOrNull(): DataFrameLike? =
    when (this) {
        is AnyFrame -> {
            object : DataFrameLike {
                override fun configuration(default: DisplayConfiguration) = default.copy(cellFormatter = null)

                override fun df(): AnyFrame = this@toDataFrameLikeOrNull
            }
        }

        is FormattedFrame<*> -> {
            object : DataFrameLike {
                override fun configuration(default: DisplayConfiguration): DisplayConfiguration =
                    getDisplayConfiguration(default)

                override fun df(): AnyFrame = df
            }
        }

        else -> null
    }

private data class RenderingQueueItem(val df: DataFrame<*>, val id: Int, val configuration: DisplayConfiguration)

private const val DEFAULT_HTML_IMG_SIZE = 100

/**
 * This method resizes a BufferedImage if necessary, according to the provided DisplayConfiguration.
 * It is essential to prevent potential memory problems when serializing HTML data for display in the Kotlin Notebook plugin.
 *
 * @param value The input value to be checked and possibly downsized.
 * @param renderConfig The DisplayConfiguration to determine if downsizing is needed.
 * @return The downsized BufferedImage if value is a BufferedImage and downsizing is enabled in the DisplayConfiguration,
 *         otherwise returns the input value unchanged.
 */
private fun downsizeBufferedImageIfNeeded(value: Any?, renderConfig: DisplayConfiguration): Any? =
    when {
        value is BufferedImage && renderConfig.downsizeBufferedImage -> {
            value.resizeKeepingAspectRatio(DEFAULT_HTML_IMG_SIZE)
        }

        else -> value
    }

/**
 * Renders [this] [DataFrame] as static HTML (meaning no JS is used).
 * CSS rendering is enabled by default but can be turned off using [includeCss]
 *
 * __IMPORTANT:__ If JavaScript is enabled, the table inside the returned [DataFrameHtmlData] will be HIDDEN when rendered.
 * This is done so this function can be used as a fallback mechanism for [AnyFrame.toHtmlData], which
 * requires JavaScript to be displayed.
 * Call `.copy(script = "")` on the returned [DataFrameHtmlData] to remove this hiding/fallback mechanism.
 *
 * @param configuration optional configuration for rendering
 * @param cellRenderer optional cell renderer for rendering
 * @param includeCss whether to include CSS in the output. This is `true` by default but it can be set
 *  to `false` to emulate what happens in environments where custom CSS is not allowed.
 * @param openNestedDfs whether to open nested dataframes. This is `false` by default byt ut can be set
 *  to `true` to emulate what happens in environments where `<details>` tags are not supported.
 */
public fun AnyFrame.toStaticHtml(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer = DefaultCellRenderer,
    includeCss: Boolean = true,
    openNestedDfs: Boolean = false,
): DataFrameHtmlData {
    val df = this
    val id = "static_df_${nextTableId()}"

    // Retrieve all columns, including nested ones
    val flattenedCols = getColumnsWithPaths { colsAtAnyDepth().filter { !it.isColumnGroup() } }

    // Get a grid of columns for the header, as well as the side borders for each cell
    val colGrid = getColumnsHeaderGrid()
    val borders = colGrid.last().map { it.borders - Border.BOTTOM }

    // Limit for number of rows in dataframes inside frame columns
    val nestedRowsLimit = configuration.nestedRowsLimit
    val rowsLimit = configuration.rowsLimit

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
    fun StringBuilder.emitHeader() =
        emitTag("thead") {
            for (row in colGrid) {
                emitTag("tr") {
                    for ((j, col) in row.withIndex()) {
                        val colBorders = col.borders.toMutableSet()
                        // check if the next cell has a left border, and if so, add a right border to this cell
                        if (row.getOrNull(j + 1)?.borders?.contains(Border.LEFT) == true) {
                            colBorders += Border.RIGHT
                        }
                        emitTag("th", "${colBorders.toClass()} style=\"text-align:left\"") {
                            append(col.columnWithPath?.name ?: "")
                        }
                    }
                }
            }
        }

    // Adds a single cell to the html. DataRows from column groups already need to be split up into separate cells.
    fun StringBuilder.emitCell(
        cellValue: Any?,
        row: AnyRow,
        col: ColumnWithPath<*>,
        borders: Set<Border>,
    ): Unit =
        emitTag("td", "${borders.toClass()} style=\"vertical-align:top\"") {
            when (col) {
                // uses the <details> and <summary> to create a collapsible cell for dataframes
                is FrameColumn<*> -> {
                    cellValue as AnyFrame
                    emitTag("details", if (openNestedDfs) "open" else "") {
                        emitTag("summary") {
                            append("DataFrame [${cellValue.size}]")
                        }

                        // nestedRowsLimit becomes the rowsLimit for nested DFs
                        // while the new nested rows limit is halved, keeping at least 1
                        val newRowsLimit = nestedRowsLimit
                        val newNestedRowsLimit = nestedRowsLimit?.let { ceil(it / 2.0).toInt() }

                        // add the dataframe as a nested table limiting the number of rows if needed
                        // CSS will not be included here, as it is already included in the main table
                        cellValue.toStaticHtml(
                            configuration = configuration.copy(
                                rowsLimit = newRowsLimit,
                                nestedRowsLimit = newNestedRowsLimit,
                            ),
                            cellRenderer = cellRenderer,
                            includeCss = false,
                            openNestedDfs = openNestedDfs,
                        ).let { append(it.body) }

                        val size = cellValue.rowsCount()
                        if (size > newRowsLimit ?: Int.MAX_VALUE) {
                            emitTag("p") {
                                append("... showing only top $newRowsLimit of $size rows")
                            }
                        }
                    }
                }

                // Else use the default cell renderer
                else ->
                    append(cellRenderer.content(cellValue, configuration).truncatedContent)
            }
        }

    // Adds a single row to the html. This row uses the flattened columns to get them displayed non-collapsed.
    fun StringBuilder.emitRow(row: AnyRow) =
        emitTag("tr") {
            for ((i, col) in flattenedCols.withIndex()) {
                val border = borders[i].toMutableSet()
                // check if the next cell has a left border, and if so, add a right border to this cell
                if (borders.getOrNull(i + 1)?.contains(Border.LEFT) == true) {
                    border += Border.RIGHT
                }
                val cell = row[col.path()]
                emitCell(cell, row, col, border)
            }
        }

    // Adds the body of the html. This body contains all the cols and rows of the dataframe.
    fun StringBuilder.emitBody() =
        emitTag("tbody") {
            val rowsCountToRender = minOf(rowsCount(), rowsLimit ?: Int.MAX_VALUE)
            for (rowIndex in 0..<rowsCountToRender) {
                emitRow(df[rowIndex])
            }
        }

    // Base function which adds the table to the html.
    fun StringBuilder.emitTable() =
        emitTag("table", """class="dataframe" id="$id"""") {
            emitHeader()
            emitBody()
        }

    return DataFrameHtmlData(
        body = buildString { emitTable() },
        // will hide the table if JS is enabled
        script =
            """
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
    BOTTOM("bottomBorder"),
}

/**
 * Converts a set of borders to a class string for html/css rendering.
 */
private fun Set<Border>.toClass(): String =
    if (isEmpty()) {
        ""
    } else {
        "class=\"${joinToString(" ") { it.className }}\""
    }

/**
 * Wrapper class which contains a nullable [ColumnWithPath] and a set of [Border]s.
 * (Empty cells can have borders too)
 */
private data class ColumnWithPathWithBorder<T>(
    val columnWithPath: ColumnWithPath<T>? = null,
    val borders: Set<Border> = emptySet(),
)

/** Returns the depth of the most-nested column in this df/group, starting at 0 */
internal fun AnyFrame.maxDepth(startingAt: Int = 0): Int =
    columns().maxOfOrNull {
        if (it is ColumnGroup<*>) {
            it.maxDepth(startingAt + 1)
        } else {
            startingAt
        }
    } ?: startingAt

/** Returns the max number of columns needed to display this column flattened */
internal fun BaseColumn<*>.maxWidth(): Int =
    if (this is ColumnGroup<*>) {
        columns().sumOf { it.maxWidth() }.coerceAtLeast(1)
    } else {
        1
    }

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
        MutableList(maxDepth + 1) { depth ->
            MutableList(maxWidth) {
                // already adding bottom borders for the last row
                val borders = if (depth == maxDepth) setOf(Border.BOTTOM) else emptySet()
                ColumnWithPathWithBorder<Any?>(borders = borders)
            }
        }

    fun ColumnWithPath<*>.addChildren(depth: Int = 0, breadth: Int = 0) {
        var breadth = breadth
        val children = cols()
        val lastIndex = children.lastIndex
        for ((i, child) in cols().withIndex()) {
            matrix[depth][breadth] = matrix[depth][breadth].copy(columnWithPath = child)

            // draw colGroup side borders unless at start/end of table
            val borders = mutableSetOf<Border>()
            if (i == 0 && breadth != 0) borders += Border.LEFT
            if (i == lastIndex && breadth != maxWidth - 1) borders += Border.RIGHT

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

@Deprecated(TO_HTML, ReplaceWith(TO_HTML_REPLACE), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.toHTML(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer = DefaultCellRenderer,
    getFooter: (DataFrame<T>) -> String? = { "DataFrame [${it.size}]" },
): DataFrameHtmlData = toHtml(configuration, cellRenderer, getFooter)

@Deprecated(TO_STANDALONE_HTML, ReplaceWith(TO_STANDALONE_HTML_REPLACE), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.toStandaloneHTML(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer = DefaultCellRenderer,
    getFooter: (DataFrame<T>) -> String? = { "DataFrame [${it.size}]" },
): DataFrameHtmlData = toStandaloneHtml(configuration, cellRenderer, getFooter)

/**
 * Returns a [DataFrameHtmlData] with CSS- and script definitions for DataFrame.
 *
 * To change the formatting of certain cells or columns in the dataframe,
 * use [DataFrame.format].
 *
 * Use [toHtml] if you don't need the [DataFrameHtmlData] to include CSS- and script definitions.
 *
 * The [DataFrameHtmlData] can be saved as an *.html file and displayed in the browser.
 * If you save it as a file and find it in the project tree,
 * the ["Open in browser"](https://www.jetbrains.com/help/idea/editing-html-files.html#ws_html_preview_output_procedure)
 * feature of IntelliJ IDEA will automatically reload the file content when it's updated.
 *
 * By default, cell content is formatted as text
 * Use [RenderedContent.media] or [IMG], [IFRAME] if you need custom HTML inside a cell.
 *
 * __NOTE:__ In Kotlin Notebook, output [DataFrame] directly, or use [toHtml],
 * as that environment already has CSS- and script definitions for DataFrame.
 * Using [toStandaloneHtml] might produce unexpected results.
 *
 * @param [configuration] The [DisplayConfiguration] to use. Default: [DisplayConfiguration.DEFAULT].
 * @param [cellRenderer] Mostly for internal usage, use [DefaultCellRenderer] if unsure.
 * @param [getFooter] Allows you to specify how to render the footer text beneath the dataframe.
 *   Default: `"DataFrame [rows x cols]"`
 * @see toHtml
 */
public fun <T> DataFrame<T>.toStandaloneHtml(
    configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT,
    cellRenderer: CellRenderer = DefaultCellRenderer,
    getFooter: (DataFrame<T>) -> String? = { "DataFrame [${it.size}]" },
): DataFrameHtmlData = toHtml(configuration, cellRenderer, getFooter).withTableDefinitions()

/**
 * Returns a [DataFrameHtmlData] without additional definitions.
 * Can be rendered in Jupyter kernel (Kotlin Notebook) environments or other environments that already have
 * CSS- and script definitions for DataFrame.
 *
 * To change the formatting of certain cells or columns in the dataframe,
 * use [DataFrame.format].
 *
 * Use [toStandaloneHtml] if you need the [DataFrameHtmlData] to include CSS- and script definitions.
 *
 * By default, cell content is formatted as text
 * Use [RenderedContent.media] or [IMG], [IFRAME] if you need custom HTML inside a cell.
 *
 * @param [configuration] The [DisplayConfiguration] to use. Default: [DisplayConfiguration.DEFAULT].
 * @param [cellRenderer] Mostly for internal usage, use [DefaultCellRenderer] if unsure.
 * @param [getFooter] Allows you to specify how to render the footer text beneath the dataframe.
 *   Default: `"DataFrame [rows x cols]"`
 * @see toStandaloneHtml
 */
public fun <T> DataFrame<T>.toHtml(
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
 * Container for HTML data, often containing a dataframe table.
 *
 * It can be used to compose rendered dataframe tables with additional HTML elements,
 * or to simply print the HTML or write it to file.
 */
public class DataFrameHtmlData(
    @Language("css") public val style: String = "",
    @Language("html", prefix = "<body>", suffix = "</body>") public val body: String = "",
    @Language("js") public val script: String = "",
) {
    override fun toString(): String =
        buildString {
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

    public fun writeHtml(destination: File) {
        destination.writeText(toString())
    }

    public fun writeHtml(destination: String) {
        File(destination).writeText(toString())
    }

    public fun writeHtml(destination: Path) {
        destination.writeText(toString())
    }

    @Deprecated(WRITE_HTML, ReplaceWith(WRITE_HTML_REPLACE), DeprecationLevel.ERROR)
    public fun writeHTML(destination: File) {
        destination.writeText(toString())
    }

    @Deprecated(WRITE_HTML, ReplaceWith(WRITE_HTML_REPLACE), DeprecationLevel.ERROR)
    public fun writeHTML(destination: String) {
        File(destination).writeText(toString())
    }

    @Deprecated(WRITE_HTML, ReplaceWith(WRITE_HTML_REPLACE), DeprecationLevel.ERROR)
    public fun writeHTML(destination: Path) {
        destination.writeText(toString())
    }

    /**
     * Opens a new tab in your default browser.
     * Consider [writeHtml] with the [HTML file auto-reload](https://www.jetbrains.com/help/idea/editing-html-files.html#ws_html_preview_output_procedure) feature of IntelliJ IDEA if you want to experiment with the output and run program multiple times
     */
    public fun openInBrowser() {
        val file = File.createTempFile("df_rendering", ".html")
        writeHtml(file)
        val uri = file.toURI()
        val desktop = Desktop.getDesktop()
        desktop.browse(uri)
    }

    public fun withTableDefinitions(): DataFrameHtmlData = tableDefinitions() + this

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataFrameHtmlData) return false

        if (style != other.style) return false
        if (body != other.body) return false
        if (script != other.script) return false

        return true
    }

    override fun hashCode(): Int {
        var result = style.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + script.hashCode()
        return result
    }

    public fun copy(
        style: String = this.style,
        body: String = this.body,
        script: String = this.script,
    ): DataFrameHtmlData = DataFrameHtmlData(style = style, body = body, script = script)

    public operator fun component1(): String = style

    public operator fun component2(): String = body

    public operator fun component3(): String = script

    public companion object {
        /**
         * @return CSS and JS required to render DataFrame tables
         * Can be used as a starting point to create page with multiple tables
         * @see DataFrame.toHtml
         * @see DataFrameHtmlData.plus
         */
        public fun tableDefinitions(includeJs: Boolean = true, includeCss: Boolean = true): DataFrameHtmlData =
            DataFrameHtmlData(
                style = if (includeCss) getResources("/table.css") else "",
                script = if (includeJs) getResourceText("/init.js") else "",
                body = "",
            )
    }
}

/**
 * A collection of settings for rendering dataframes as HTML tables or native
 * Kotlin Notebook table output.
 *
 * @param rowsLimit null to disable rows limit
 * @param cellContentLimit -1 to disable content trimming
 * @param enableFallbackStaticTables true to add additional pure HTML table that will be visible only if JS  is disabled;
 * For example hosting *.ipynb files with outputs on GitHub
 * @param cellFormatter Optional cell formatter applied to data cells during HTML rendering.
 * @param headerFormatter Optional header formatter applied to column headers; supports inheritance for nested column groups.
 */
public data class DisplayConfiguration(
    var rowsLimit: Int? = 20,
    var nestedRowsLimit: Int? = 5,
    var cellContentLimit: Int = 40,
    var cellFormatter: RowColFormatter<*, *>? = null,
    var headerFormatter: HeaderColFormatter<*>? = null,
    var decimalFormat: RendererDecimalFormat = RendererDecimalFormat.DEFAULT,
    var isolatedOutputs: Boolean = flagFromEnv("LETS_PLOT_HTML_ISOLATED_FRAME"),
    internal val localTesting: Boolean = flagFromEnv("KOTLIN_DATAFRAME_LOCAL_TESTING"),
    var useDarkColorScheme: Boolean = false,
    var enableFallbackStaticTables: Boolean = true,
    var downsizeBufferedImage: Boolean = true,
) {
    public companion object {
        public val DEFAULT: DisplayConfiguration = DisplayConfiguration()
    }

    /** For binary compatibility. */
    @Deprecated(
        message = DISPLAY_CONFIGURATION,
        level = DeprecationLevel.HIDDEN,
    )
    public constructor(
        rowsLimit: Int? = 20,
        nestedRowsLimit: Int? = 5,
        cellContentLimit: Int = 40,
        cellFormatter: RowColFormatter<*, *>? = null,
        decimalFormat: RendererDecimalFormat = RendererDecimalFormat.DEFAULT,
        isolatedOutputs: Boolean = flagFromEnv("LETS_PLOT_HTML_ISOLATED_FRAME"),
        localTesting: Boolean = flagFromEnv("KOTLIN_DATAFRAME_LOCAL_TESTING"),
        useDarkColorScheme: Boolean = false,
        enableFallbackStaticTables: Boolean = true,
        downsizeBufferedImage: Boolean = true,
    ) : this (
        rowsLimit,
        nestedRowsLimit,
        cellContentLimit,
        cellFormatter,
        null,
        decimalFormat,
        isolatedOutputs,
        localTesting,
        useDarkColorScheme,
        enableFallbackStaticTables,
        downsizeBufferedImage,
    )

    /** For binary compatibility. */
    @Deprecated(
        message = DISPLAY_CONFIGURATION_COPY,
        level = DeprecationLevel.HIDDEN,
    )
    public fun copy(
        rowsLimit: Int? = this.rowsLimit,
        nestedRowsLimit: Int? = this.nestedRowsLimit,
        cellContentLimit: Int = this.cellContentLimit,
        cellFormatter: RowColFormatter<*, *>? = this.cellFormatter,
        decimalFormat: RendererDecimalFormat = this.decimalFormat,
        isolatedOutputs: Boolean = this.isolatedOutputs,
        localTesting: Boolean = this.localTesting,
        useDarkColorScheme: Boolean = this.useDarkColorScheme,
        enableFallbackStaticTables: Boolean = this.enableFallbackStaticTables,
        downsizeBufferedImage: Boolean = this.downsizeBufferedImage,
    ): DisplayConfiguration =
        DisplayConfiguration(
            rowsLimit,
            nestedRowsLimit,
            cellContentLimit,
            cellFormatter,
            null,
            decimalFormat,
            isolatedOutputs,
            localTesting,
            useDarkColorScheme,
            enableFallbackStaticTables,
            downsizeBufferedImage,
        )

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

        public fun of(format: String): RendererDecimalFormat = RendererDecimalFormat(format)

        internal val DEFAULT: RendererDecimalFormat = fromPrecision(DEFAULT_PRECISION)
    }
}

internal const val DEFAULT_PRECISION = 6

private fun flagFromEnv(envName: String): Boolean = System.getenv(envName)?.toBooleanStrictOrNull() ?: false

internal fun String.escapeNewLines() = replace("\n", "\\n").replace("\r", "\\r")

internal fun String.escapeForHtmlInJs() = replace("\"", "\\\"").escapeNewLines()

internal fun renderValueForHtml(value: Any?, truncate: Int, format: RendererDecimalFormat): RenderedContent =
    formatter.truncate(renderValueToString(value, format), truncate)

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

    private fun RenderedContent.addCss(css: String? = null): RenderedContent =
        if (css != null) {
            copy(truncatedContent = "<span class=\"$css\">$truncatedContent</span>", isFormatted = true)
        } else {
            this
        }

    internal fun truncate(str: String, limit: Int): RenderedContent =
        if (limit in 1 until str.length) {
            val ellipsis = "...".ellipsis(str)
            if (limit < 4) {
                ellipsis
            } else {
                val len = (limit - 3).coerceAtLeast(1)
                RenderedContent.textWithLength(str.substring(0, len).escapeHTML(), len) + ellipsis
            }
        } else {
            RenderedContent.textWithLength(str.escapeHTML(), str.length)
        }

    fun format(value: Any?, renderer: CellRenderer, configuration: DisplayConfiguration): String {
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
                    values.size - 1 -> limit - sb.len - postfix.length

                    // last
                    values.size - 2 -> { // prev before last
                        val sizeOfLast = render(
                            values.last(),
                            renderer,
                            configuration.copy(cellContentLimit = 4),
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

        val result: RenderedContent? = when (value) {
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
                    render(value.second, renderer, configuration.copy(cellContentLimit = 3))
                        ?: "...".addCss(structuralClass)
                val sizeOfValue = shortValue.textLength
                val keyLimit = limit - sizeOfValue
                if (key.length > keyLimit) {
                    if (limit > 3) {
                        ("$key...").truncate(limit).addCss(structuralClass)
                    } else {
                        null
                    }
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
                "</a>",
            )

            is DataFrameHtmlData -> RenderedContent.text(value.body)

            is IMG -> RenderedContent.media(value.toString())

            is IFRAME -> RenderedContent.media(value.toString())

            is RenderedContent -> value

            else -> renderer.content(value, configuration)
        }
        if (result != null && result.textLength > configuration.cellContentLimit) return null
        return result
    }
}
