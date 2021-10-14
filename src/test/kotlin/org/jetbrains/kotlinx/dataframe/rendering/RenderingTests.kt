package org.jetbrains.kotlinx.dataframe.rendering

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.getType
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.escapeHTML
import org.jetbrains.kotlinx.dataframe.io.formatter
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.io.renderToStringTable
import org.jetbrains.kotlinx.dataframe.io.toHTML
import org.jetbrains.kotlinx.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.kotlinx.dataframe.toDataFrame
import org.jsoup.Jsoup
import org.junit.Test
import java.net.URL

class RenderingTests {

    @Test
    fun `render row with unicode values as table`() {
        val value = "Шёл Шива по шоссе, сокрушая сущее.\nА на встречу Саша шла, круглое сосущая"
        val col by columnOf(value)
        val df = col.toDataFrame()
        val rendered = df[0].renderToStringTable()
        rendered.contains("Шива") shouldBe true
        rendered.contains("\n") shouldBe false
        rendered.contains("А") shouldBe true
        rendered.contains("...") shouldBe true
        rendered.contains("Саша") shouldBe false
    }

    @Test
    fun `parse url`() {
        val df = dataFrameOf("url")("http://www.google.com").parse()
        df["url"].type() shouldBe getType<URL>()
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
}
