package org.jetbrains.kotlinx.dataframe.rendering

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldInclude
import io.kotest.matchers.string.shouldNotContain
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.CellAttributes
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.escapeHTML
import org.jetbrains.kotlinx.dataframe.io.formatter
import org.jetbrains.kotlinx.dataframe.io.maxDepth
import org.jetbrains.kotlinx.dataframe.io.maxWidth
import org.jetbrains.kotlinx.dataframe.io.print
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.io.renderToStringTable
import org.jetbrains.kotlinx.dataframe.io.tableInSessionId
import org.jetbrains.kotlinx.dataframe.io.toHtml
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.jetbrains.kotlinx.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.secondName
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URL
import java.text.DecimalFormatSymbols
import kotlin.reflect.typeOf

class RenderingTests : TestBase() {

    @Test
    fun `render row with unicode values as table`() {
        val value = "Шёл Шива по шоссе, сокрушая сущее.\r\nА на встречу Саша шла, круглое сосущая"
        val col by columnOf(value)
        val df = col.toDataFrame()
        val rendered = df[0].renderToStringTable()
        rendered shouldContain "Шива"
        rendered shouldNotContain "\n"
        rendered shouldNotContain "\r"
        rendered shouldContain "\\r"
        rendered shouldContain "А"
        rendered shouldContain "..."
        rendered shouldNotContain "Саша"
    }

    @Test
    fun `parse url`() {
        val df = dataFrameOf("url")("http://www.google.com").parse()
        df["url"].type() shouldBe typeOf<URL>()
    }

    @Test
    fun htmlTagsAreEscaped() {
        val df = dataFrameOf("name", "int")("<Air France> (12)", 1)
        val html = df.toHtml().toString()
        html shouldContain "&#60;Air France&#62;"
    }

    @Test
    fun unicodeEscapeSequencesAreEscaped() {
        val df = dataFrameOf("content")("""Hello\nfrom \x and \y""")
        val html = df.toHtml().toString()
        html shouldContain "Hello&#92;nfrom &#92;x and &#92;y"
    }

    @Test
    fun `long text is trimmed without escaping`() {
        val df = dataFrameOf("text")("asdfkjasdlkjfhasljkddasdasdasdasdasdasdhf")
        val html = df.toHtml().toString()
        html shouldNotContain "\\\\"
        html shouldNotContain "&#34;"
    }

    @Test
    fun `non ascii text`() {
        val value = "Шёл Шива по шоссе, сокрушая сущее"
        val df = dataFrameOf("text")(value)
        val script = df.toHtml().script
        script shouldContain value.escapeHTML()
    }

    @Test
    fun `empty row with nested empty row`() {
        val df = dataFrameOf("a", "b", "c")(null, null, null)
        val grouped = df
            .group("a", "b").into("d")
            .group("c", "d").into("e")[0]

        val formatted = formatter.format(grouped, DefaultCellRenderer, DisplayConfiguration())
        Jsoup.parse(formatted).text() shouldBe "{ }"

        grouped.renderToString() shouldBe "{ }"
    }

    @Test
    fun `render successfully`() {
        dataFrameOf("a", "b")(listOf(1, 1), listOf(2, 4))
            .group("a", "b")
            .into("g")
            .add("a") { 1 }
            .toHtml()
    }

    @Test
    fun `render URL`() {
        val df = dataFrameOf("url")("https://api.github.com/orgs/JetBrains")
        val html = df.parse().toHtml()
        html.toString() shouldNotContain RenderedContent::class.simpleName!!
    }

    @Test
    fun `render successfully 2`() {
        val df = dataFrameOf("name", "parent", "type")("Boston (MA)", "123wazxdPag5", "Campus")
            .move("parent").into { "parent"["id"] }
            .group { all() }.into("Campus")
        df.toHtml().print()
    }

    @Test
    fun `render double with exponent`() {
        val d = DecimalFormatSymbols.getInstance().decimalSeparator
        listOf(
            dataFrameOf("col")(1E27) to "1${d}000000e+27",
            dataFrameOf("col")(1.123) to "1${d}123",
            dataFrameOf("col")(1.0) to "1${d}0",
        ).forEach { (df, rendered) ->
            df.toHtml().script shouldContain rendered
        }
    }

    @Test
    fun `static rendering should be present`() {
        val df = dataFrameOf("a", "b")(listOf(1, 1), listOf(2, 4))
        val actualHtml = df.toHtml()

        val body = actualHtml.body.lines().joinToString("") { it.trimStart() }

        body shouldContain
            """
            <thead>
            <tr>
            <th class="bottomBorder" style="text-align:left">a</th>
            <th class="bottomBorder" style="text-align:left">b</th>
            </tr>
            </thead>
            <tbody>
            <tr>
            <td  style="vertical-align:top">[1, 1]</td>
            <td  style="vertical-align:top">[2, 4]</td>
            </tr>
            </tbody>
            </table>
            """.trimIndent().replace("\n", "")
    }

    @Test
    fun `max depth`() {
        df.maxDepth() shouldBe 1
        dfGroup.maxDepth() shouldBe 2
        emptyDataFrame<Any>().maxDepth() shouldBe 0
    }

    @Test
    fun `max width`() {
        dfGroup.asColumnGroup("").maxWidth() shouldBe 8
        dfGroup.name.maxWidth() shouldBe 4
        dfGroup.name.firstName.maxWidth() shouldBe 3
        dfGroup.name.lastName.maxWidth() shouldBe 1
        dfGroup.name.firstName.secondName.maxWidth() shouldBe 1
    }

    @Test
    fun `render array types correctly`() {
        val df = dataFrameOf(
            columnOf(1, null).named("a"),
            columnOf(intArrayOf(1), intArrayOf(2)).named("b"),
            // TODO https://github.com/Kotlin/dataframe/issues/679
            // columnOf(arrayOf(1), arrayOf(2)).named("d"),
            DataColumn.createValueColumn("c", listOf(arrayOf(1), arrayOf(2))),
            columnOf(arrayOf(1, null), arrayOf(2, null)).named("d"),
        )

        val schema = df.schema()
        val rendered = schema.toString()
        rendered shouldBe "a: Int?\nb: IntArray\nc: Array<Int>\nd: Array<Int?>"
    }

    @Test
    fun `render nested FormattedFrame as DataFrame`() {
        val empty = object : CellAttributes {
            override fun attributes(): List<Pair<String, String>> = emptyList()
        }
        val df = dataFrameOf("b")(1)

        val formatted = dataFrameOf("a")(df.format { all() }.with { empty })
        val nestedFrame = dataFrameOf("a")(df)
        val configuration = DisplayConfiguration(enableFallbackStaticTables = false)
        tableInSessionId = 0
        val formattedHtml = formatted.toStandaloneHtml(configuration).toString()
        tableInSessionId = 0
        val regularHtml = nestedFrame.toStandaloneHtml(configuration).toString()

        formattedHtml.replace("api.FormattedFrame", "DataFrame") shouldBe regularHtml
    }

    @Test
    fun `render cell attributes for nested FormattedFrame`() {
        val df = dataFrameOf("a")(dataFrameOf("b")(1).format { all() }.with { background(green) })
        val html = df.toStandaloneHtml()
        html.toString() shouldInclude "style: \"background-color"
    }

    @Test
    fun `render img`() {
        val src = "https://github.com/Kotlin/dataframe/blob/master/docs/StardustDocs/images/gettingStarted.png?raw=true"
        val df = dataFrameOf("img")(IMG(src))
        df.toStandaloneHtml().toString() shouldInclude
            """values: ["<img src=\"https://github.com/Kotlin/dataframe/blob/master/docs/StardustDocs/images/gettingStarted.png?raw=true\" style=\"\"/>"]"""
    }

    @Test
    fun `render custom content`() {
        val df = dataFrameOf("customUrl")(RenderedContent.media("""<a href="http://example.com">Click me!</a>"""))
        df.toStandaloneHtml().toString() shouldInclude
            """values: ["<a href=\"http://example.com\">Click me!</a>"]"""
    }

    @Test
    fun `render iframe content`() {
        val df = dataFrameOf("iframe")(IFRAME("http://example.com"))
        df.toStandaloneHtml().toString() shouldInclude
            """values: ["<iframe src=\"http://example.com\"""
    }
}
