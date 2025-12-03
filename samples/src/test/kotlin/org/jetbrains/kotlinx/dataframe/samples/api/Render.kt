package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.chunked
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.duplicateRows
import org.jetbrains.kotlinx.dataframe.api.forEachIndexed
import org.jetbrains.kotlinx.dataframe.api.reorderColumnsByName
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.toHtml
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.jetbrains.kotlinx.dataframe.jupyter.ChainedCellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.net.URI
import kotlin.io.path.Path

class Render : DataFrameSampleHelper("toHTML", "api") {

    private val df: AnyFrame = dataFrameOf(
        "name" to columnOf(
            "firstName" to columnOf("Alice", "Bob", "Charlie", "Charlie", "Bob", "Alice", "Charlie"),
            "lastName" to columnOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley", "Wolf", "Byrd"),
        ),
        "age" to columnOf(15, 45, 20, 40, 30, 20, 30),
        "city" to columnOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null, "Moscow"),
        "weight" to columnOf(54, 87, null, null, 68, 55, 90),
        "isHappy" to columnOf(true, true, false, true, true, false, true),
    )

    @Test
    @Ignore
    fun useRenderingResult() {
        // SampleStart
        val configuration = DisplayConfiguration(rowsLimit = null)
        df.toStandaloneHtml(configuration).openInBrowser()
        df.toStandaloneHtml(configuration).writeHtml(File("/path/to/file"))
        df.toStandaloneHtml(configuration).writeHtml(Path("/path/to/file"))
        // SampleEnd
    }

    @Test
    fun composeTables_strings() {
        val df = dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice", "Bob", "Charlie", "Charlie", "Bob", "Alice", "Charlie"),
                "lastName" to columnOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley", "Wolf", "Byrd"),
            ),
            "age" to columnOf(15, 45, 20, 40, 30, 20, 30),
            "city" to columnOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null, "Moscow"),
            "weight" to columnOf(54, 87, null, null, 68, 55, 90),
            "isHappy" to columnOf(true, true, false, true, true, false, true),
        )
        // SampleStart
        val df1 = df.reorderColumnsByName()
        val df2 = df.sortBy("age")
        val df3 = df.sortByDesc("age")

        listOf(df1, df2, df3).fold(DataFrameHtmlData.tableDefinitions()) { acc, df ->
            acc + df.toHtml()
        }
        // SampleEnd
    }

    @Test
    fun composeTables_properties() {
        val df = dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice", "Bob", "Charlie", "Charlie", "Bob", "Alice", "Charlie"),
                "lastName" to columnOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley", "Wolf", "Byrd"),
            ),
            "age" to columnOf(15, 45, 20, 40, 30, 20, 30),
            "city" to columnOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null, "Moscow"),
            "weight" to columnOf(54, 87, null, null, 68, 55, 90),
            "isHappy" to columnOf(true, true, false, true, true, false, true),
        )
        // SampleStart
        val df1 = df.reorderColumnsByName()
        val df2 = df.sortBy { age }
        val df3 = df.sortByDesc { age }

        listOf(df1, df2, df3).fold(DataFrameHtmlData.tableDefinitions()) { acc, df -> acc + df.toHtml() }
        // SampleEnd
    }

    @Test
    fun configureCellOutput() {
        // SampleStart
        df.toHtml(DisplayConfiguration(cellContentLimit = -1))
        // SampleEnd
    }

    @Test
    fun displayImg() {
        // SampleStart
        val htmlData = dataFrameOf(
            "kotlinLogo" to columnOf(
                IMG("https://kotlin.github.io/dataframe/images/kotlin-logo.svg"),
            ),
        ).toStandaloneHtml()
        // SampleEnd
        // .openInBrowser()
    }

    @Test
    fun displayIFrame() {
        // SampleStart
        val htmlData = dataFrameOf(
            "documentationPages" to columnOf(
                IFRAME(
                    src = "https://kotlin.github.io/dataframe/tohtml.html",
                    width = 850,
                    height = 500,
                ),
            ),
        ).toStandaloneHtml()
        // SampleEnd
        // .openInBrowser()
    }

    @Test
    fun displayURL() {
        // SampleStart
        val htmlData = dataFrameOf(
            "documentationPages" to columnOf(
                URI("https://kotlin.github.io/dataframe/format.html").toURL(),
                URI("https://kotlin.github.io/dataframe/tohtml.html").toURL(),
                URI("https://kotlin.github.io/dataframe/jupyterrendering.html").toURL(),
            ),
        )
            .toStandaloneHtml()
        // SampleEnd
        // .openInBrowser()
    }

    @Test
    fun displayMediaContent_strings() {
        // SampleStart
        val htmlData = dataFrameOf(
            "documentationPages" to columnOf(
                "https://kotlin.github.io/dataframe/format.html",
                "https://kotlin.github.io/dataframe/tohtml.html",
                "https://kotlin.github.io/dataframe/jupyterrendering.html",
            ),
        )
            .convert { "documentationPages"<String>() }.with {
                val uri = URI(it)
                RenderedContent.media("""<a href='$uri'>${uri.path}</a>""")
            }
            .toStandaloneHtml()
        // SampleEnd
        // .openInBrowser()
    }

    @Test
    fun displayMediaContent_properties() {
        // SampleStart
        val htmlData = dataFrameOf(
            "documentationPages" to columnOf(
                "https://kotlin.github.io/dataframe/format.html",
                "https://kotlin.github.io/dataframe/tohtml.html",
                "https://kotlin.github.io/dataframe/jupyterrendering.html",
            ),
        )
            .convert { documentationPages }.with {
                val uri = URI(it)
                RenderedContent.media("""<a href='$uri'>${uri.path}</a>""")
            }
            .toStandaloneHtml()
        // SampleEnd
        // .openInBrowser()
    }

    @Test
    fun cellRenderer() {
        // SampleStart
        class CustomArrayCellRenderer : ChainedCellRenderer(DefaultCellRenderer) {
            override fun maybeContent(value: Any?, configuration: DisplayConfiguration): RenderedContent? {
                if (value is Boolean) {
                    return RenderedContent.text(if (value) "✓" else "✗")
                }
                // return null to delegate work to parent renderer: DefaultCellRenderer
                return null
            }

            override fun maybeTooltip(value: Any?, configuration: DisplayConfiguration): String? {
                // return null to delegate work to parent renderer: DefaultCellRenderer
                return null
            }
        }

        val htmlData = df.toStandaloneHtml(cellRenderer = CustomArrayCellRenderer())
        // SampleEnd
        // .openInBrowser()
    }

    @Test
    fun df() {
        // SampleStart
        val df = dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice", "Bob", "Charlie", "Charlie", "Bob", "Alice", "Charlie"),
                "lastName" to columnOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley", "Wolf", "Byrd"),
            ),
            "age" to columnOf(15, 45, 20, 40, 30, 20, 30),
            "city" to columnOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null, "Moscow"),
            "weight" to columnOf(54, 87, null, null, 68, 55, 90),
            "isHappy" to columnOf(true, true, false, true, true, false, true),
        )
        // SampleEnd
    }

    @Test
    fun appendCustomHtml() {
        // SampleStart
        val pages = df.duplicateRows(10).chunked(20)
        val files = pages.indices.map { i -> File("page$i.html") }
        val navLinks = files.mapIndexed { i, file ->
            """<a href="${file.name}">Page ${i + 1}</a>"""
        }.joinToString(" | ")

        pages.forEachIndexed { i, page ->
            val output = files[i]
            page.toStandaloneHtml().plus(DataFrameHtmlData(body = navLinks))
            // uncomment
            // .writeHtml(output)
        }
        // SampleEnd
    }

    @Test
    fun interactiveJs() {
        // SampleStart
        val selectCellInteraction = DataFrameHtmlData(
            style =
                """
                td:hover {
                    background-color: rgba(0, 123, 255, 0.15);
                    cursor: pointer;
                }
                """.trimIndent(),
            script =
                """
                (function() {
                    let cells = document.querySelectorAll('td');
                    cells.forEach(function(cell) {
                        cell.addEventListener('click', function(e) {
                            let content = cell.textContent;
                            alert(content);
                        });
                    });
                })();
                """.trimIndent(),
        )

        // keep in mind JS script initialization order.
        val htmlData = df.toStandaloneHtml().plus(selectCellInteraction)
        // SampleEnd
        // .openInBrowser()
    }
}
