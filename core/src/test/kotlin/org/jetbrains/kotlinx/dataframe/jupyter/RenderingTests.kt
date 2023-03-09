package org.jetbrains.kotlinx.dataframe.jupyter

import com.beust.klaxon.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.Test

class RenderingTests : JupyterReplTestCase() {
    @Test
    fun `dataframe is rendered to html`() {
        @Language("kts")
        val html = execHtml(
            """
            val name by column<String>()
            val height by column<Int>()
            val df = dataFrameOf(name, height)(
                "Bill", 135,
                "Charlie", 160
            )
            df
            """.trimIndent()
        )
        html shouldContain "Bill"

        @Language("kts")
        val useRes = exec(
            """
            USE {
                render<Int> { (it * 2).toString() }
            }
            """.trimIndent()
        )
        useRes shouldBe Unit

        val html2 = execHtml("df")
        html2 shouldContain (135 * 2).toString()
        html2 shouldContain (160 * 2).toString()
    }

    @Test
    fun `rendering options`() {
        @Language("kts")
        val html1 = execHtml(
            """
            data class Person(val age: Int, val name: String)
            val df = (1..70).map { Person(it, "A".repeat(it)) }.toDataFrame()
            df
            """.trimIndent()
        )
        html1 shouldContain "showing only top 20 of 70 rows"

        @Language("kts")
        val html2 = execHtml(
            """
            dataFrameConfig.display.rowsLimit = 50
            df
            """.trimIndent()
        )
        html2 shouldContain "showing only top 50 of 70 rows"
    }

    @Test
    fun `dark color scheme`() {
        fun execSimpleDf() = execHtml("""dataFrameOf("a", "b")(1, 2, 3, 4)""")

        val htmlLight = execSimpleDf()
        val r1 = exec("notebook.changeColorScheme(ColorScheme.DARK); 1")
        val htmlDark = execSimpleDf()

        r1 shouldBe 1
        val darkClassAttribute = """theme='dark'"""
        htmlLight shouldNotContain darkClassAttribute
        htmlDark shouldContain darkClassAttribute
    }

    @Test
    fun `test kotlin notebook plugin utils rows subset`() {
        @Language("kts")
        val result = exec<MimeTypedResult>(
            """
            data class Row(val id: Int)
            val df = (1..100).map { Row(it) }.toDataFrame()
            KotlinNotebookPluginUtils.getRowsSubsetForRendering(df, 20 , 50)
            """.trimIndent()
        )

        val json = parseDataframeJson(result)

        json.int("nrow") shouldBe 30
        json.int("ncol") shouldBe 1

        val rows = json.array<JsonArray<*>>("kotlin_dataframe")!!
        rows.getObj(0).int("id") shouldBe 21
        rows.getObj(rows.lastIndex).int("id") shouldBe 50
    }

    private fun parseDataframeJson(result: MimeTypedResult): JsonObject {
        val parser = Parser.default()
        return parser.parse(StringBuilder(result["application/kotlindataframe+json"]!!)) as JsonObject
    }

    private fun JsonArray<*>.getObj(index: Int) = this.get(index) as JsonObject

    @Test
    fun `test kotlin notebook plugin utils groupby`() {
        @Language("kts")
        val result = exec<MimeTypedResult>(
            """
            data class Row(val id: Int, val group: Int)
            val df = (1..100).map { Row(it, if (it <= 50) 1 else 2) }.toDataFrame()
            KotlinNotebookPluginUtils.getRowsSubsetForRendering(df.groupBy("group"), 0, 10)
            """.trimIndent()
        )

        val json = parseDataframeJson(result)

        json.int("nrow") shouldBe 2
        json.int("ncol") shouldBe 2

        val rows = json.array<JsonArray<*>>("kotlin_dataframe")!!
        rows.getObj(0).array<JsonObject>("group1")!!.size shouldBe 50
        rows.getObj(1).array<JsonObject>("group1")!!.size shouldBe 50
    }
}
