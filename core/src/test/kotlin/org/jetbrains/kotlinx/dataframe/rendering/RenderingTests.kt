package org.jetbrains.kotlinx.dataframe.rendering

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.escapeHTML
import org.jetbrains.kotlinx.dataframe.io.formatter
import org.jetbrains.kotlinx.dataframe.io.print
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.io.renderToStringTable
import org.jetbrains.kotlinx.dataframe.io.toHTML
import org.jetbrains.kotlinx.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.kotlinx.dataframe.jupyter.RenderedContent
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URL
import kotlin.reflect.typeOf

class RenderingTests {

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
    fun `long text is trimmed without escaping`() {
        val df = dataFrameOf("text")("asdfkjasdlkjfhasljkddasdasdasdasdasdasdhf")
        val html = df.toHTML(includeInit = false).toString()
        html shouldNotContain "\\\\"
        html shouldNotContain "&#34;"
    }

    @Test
    fun `non ascii text`() {
        val value = "Шёл Шива по шоссе, сокрушая сущее"
        val df = dataFrameOf("text")(value)
        val script = df.toHTML(includeInit = false).script
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
        listOf(
            dataFrameOf("col")(1E27) to "1.000000e+27",
            dataFrameOf("col")(1.123) to "1.123",
            dataFrameOf("col")(1.0) to "1.0",
        ).forEach { (df, rendered) ->
            df.toHTML().script shouldContain rendered
        }
    }
}
