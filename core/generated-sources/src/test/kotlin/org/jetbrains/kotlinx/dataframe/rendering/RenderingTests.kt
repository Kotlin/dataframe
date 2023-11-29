package org.jetbrains.kotlinx.dataframe.rendering

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.frameColumn
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.escapeHTML
import org.jetbrains.kotlinx.dataframe.io.formatter
import org.jetbrains.kotlinx.dataframe.io.maxDepth
import org.jetbrains.kotlinx.dataframe.io.maxWidth
import org.jetbrains.kotlinx.dataframe.io.print
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.io.renderToStringTable
import org.jetbrains.kotlinx.dataframe.io.toHTML
import org.jetbrains.kotlinx.dataframe.io.toStaticHtml
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
        val html = df.toHTML().toString()
        html shouldContain "&#60;Air France&#62;"
    }

    @Test
    fun unicodeEscapeSequencesAreEscaped() {
        val df = dataFrameOf("content")("""Hello\nfrom \x and \y""")
        val html = df.toHTML().toString()
        html shouldContain "Hello&#92;nfrom &#92;x and &#92;y"
    }

    @Test
    fun `long text is trimmed without escaping`() {
        val df = dataFrameOf("text")("asdfkjasdlkjfhasljkddasdasdasdasdasdasdhf")
        val html = df.toHTML().toString()
        html shouldNotContain "\\\\"
        html shouldNotContain "&#34;"
    }

    @Test
    fun `non ascii text`() {
        val value = "Шёл Шива по шоссе, сокрушая сущее"
        val df = dataFrameOf("text")(value)
        val script = df.toHTML().script
        script shouldContain value.escapeHTML()
    }

    @Test
    fun `empty row with nested empty row`() {
        val df = dataFrameOf("a", "b", "c")(null, null, null)
        val grouped = df.group("a", "b").into("d").group("c", "d").into("e")[0]

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
            .toHTML()
    }

    @Test
    fun `render URL`() {
        val df = dataFrameOf("url")("https://api.github.com/orgs/JetBrains")
        val html = df.parse().toHTML()
        html.toString() shouldNotContain RenderedContent::class.simpleName!!
    }

    @Test
    fun `render successfully 2`() {
        val df = dataFrameOf("name", "parent", "type")("Boston (MA)", "123wazxdPag5", "Campus")
            .move("parent").into { "parent"["id"] }
            .group { all() }.into("Campus")
        df.toHTML().print()
    }

    @Test
    fun `render double with exponent`() {
        val d = DecimalFormatSymbols.getInstance().decimalSeparator
        listOf(
            dataFrameOf("col")(1E27) to "1${d}000000e+27",
            dataFrameOf("col")(1.123) to "1${d}123",
            dataFrameOf("col")(1.0) to "1${d}0",
        ).forEach { (df, rendered) ->
            df.toHTML().script shouldContain rendered
        }
    }

    @Test
    fun `static rendering should be present`() {
        val df = dataFrameOf("a", "b")(listOf(1, 1), listOf(2, 4))
        val actualHtml = df.toHTML()

        val body = actualHtml.body.lines().joinToString("") { it.trimStart() }

        body shouldContain """
            <thead>
            <tr>
            <th class="bottomBorder">a</th>
            <th class="bottomBorder">b</th>
            </tr>
            </thead>
            <tbody>
            <tr>
            <td>[1, 1]</td>
            <td>[2, 4]</td>
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
    fun `test static rendering TODO temp`() {
        val df = DataFrame.read("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains.json")

//        df.toHTML().openInBrowser()
        df.toStaticHtml(openNestedDfs = true, includeCss = false).copy(script = "").openInBrowser()
//        df.get(frameColumn("repos")).get(0).toStaticHtml(openNestedDfs = false).copy(script = "").openInBrowser()
    }
}
