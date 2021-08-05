package org.jetbrains.dataframe.rendering

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.dataframe.columnOf
import org.jetbrains.dataframe.dataFrameOf
import org.jetbrains.dataframe.getType
import org.jetbrains.dataframe.group
import org.jetbrains.dataframe.into
import org.jetbrains.dataframe.io.DisplayConfiguration
import org.jetbrains.dataframe.io.formatter
import org.jetbrains.dataframe.io.renderToString
import org.jetbrains.dataframe.io.renderToStringTable
import org.jetbrains.dataframe.io.toHTML
import org.jetbrains.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.dataframe.parse
import org.jetbrains.dataframe.toDataFrame
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
        println(html)
        html shouldContain "&#60;Air France&#62;"
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
