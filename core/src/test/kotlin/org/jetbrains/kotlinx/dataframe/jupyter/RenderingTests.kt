package org.jetbrains.kotlinx.dataframe.jupyter

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
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
        val useRes = execRendered(
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
        val r1 = execRendered("notebook.changeColorScheme(ColorScheme.DARK); 1")
        val htmlDark = execSimpleDf()

        r1 shouldBe 1
        val darkClassAttribute = """theme='dark'"""
        htmlLight shouldNotContain darkClassAttribute
        htmlDark shouldContain darkClassAttribute
    }

    @Test
    fun `test kotlin notebook plugin utils rows subset`() {
        val json = executeScriptAndParseDataframeResult(
            """
            data class Row(val id: Int)
            val df = (1..100).map { Row(it) }.toDataFrame()
            KotlinNotebookPluginUtils.getRowsSubsetForRendering(df, 20 , 50)
            """.trimIndent()
        )

        assertDataFrameDimensions(json, 30, 1)

        val rows = json["kotlin_dataframe"]!!.jsonArray
        rows.getObj(0)["id"]?.jsonPrimitive?.int shouldBe 21
        rows.getObj(rows.lastIndex)["id"]?.jsonPrimitive?.int shouldBe 50
    }

    /**
     * Executes the given `script` and parses the resulting DataFrame as a `JsonObject`.
     *
     * @param script the script to be executed
     * @return the parsed DataFrame result as a `JsonObject`
     */
    private fun executeScriptAndParseDataframeResult(@Language("kts") script: String): JsonObject {
        val result = execRendered<MimeTypedResult>(script)
        return parseDataframeJson(result)
    }

    private fun assertDataFrameDimensions(json: JsonObject, expectedRows: Int, expectedColumns: Int) {
        json["nrow"]?.jsonPrimitive?.int shouldBe expectedRows
        json["ncol"]?.jsonPrimitive?.int shouldBe expectedColumns
    }

    private fun parseDataframeJson(result: MimeTypedResult): JsonObject {
        return Json.decodeFromString<JsonObject>(result["application/kotlindataframe+json"]!!)
    }

    private fun JsonArray.getObj(index: Int) = this[index] as JsonObject

    @Test
    fun `test kotlin notebook plugin utils sort by one column asc`() {
        val json = executeScriptAndParseDataframeResult(
            """
            data class CustomRow(val id: Int, val category: String)
            val df = (1..100).map { CustomRow(it, if (it % 2 == 0) "even" else "odd") }.toDataFrame()
            KotlinNotebookPluginUtils.sortByColumns(df, listOf(listOf("id")), listOf(false))
            """.trimIndent()
        )

        assertDataFrameDimensions(json, 100, 2)
        assertSortedById(json, false)
    }

    @Suppress("UNCHECKED_CAST")
    private fun assertSortedById(json: JsonObject, desc: Boolean) {
        val rows = json["kotlin_dataframe"]!!.jsonArray as List<JsonObject>
        var previousId = if (desc) 101 else 0
        rows.forEach { row: JsonObject ->
            val currentId = row["id"]!!.jsonPrimitive.int
            if (desc) currentId shouldBeLessThan previousId else currentId shouldBeGreaterThan previousId
            previousId = currentId
        }
    }

    @Test
    fun `test kotlin notebook plugin utils sort by one column desc`() {
        val json = executeScriptAndParseDataframeResult(
            """
            data class CustomRow(val id: Int, val category: String)
            val df = (1..100).map { CustomRow(it, if (it % 2 == 0) "even" else "odd") }.toDataFrame()
            KotlinNotebookPluginUtils.sortByColumns(df, listOf(listOf("id")), listOf(true))
            """.trimIndent()
        )

        assertDataFrameDimensions(json, 100, 2)
        assertSortedById(json, true)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test kotlin notebook plugin utils sort by multiple columns`() {
        val json = executeScriptAndParseDataframeResult(
            """
            data class CustomRow(val id: Int, val category: String)
            val df = (1..100).map { CustomRow(it, if (it % 2 == 0) "even" else "odd") }.toDataFrame()
            KotlinNotebookPluginUtils.getRowsSubsetForRendering(
                KotlinNotebookPluginUtils.sortByColumns(df, listOf(listOf("category"), listOf("id")), listOf(true, false)),
                0, 100
            )
            """.trimIndent()
        )

        assertDataFrameDimensions(json, 100, 2)

        val rows = json["kotlin_dataframe"]!!.jsonArray as List<JsonObject>
        assertSortedByCategory(rows)
        assertSortedById(rows)
    }

    private fun assertSortedByCategory(rows: List<JsonObject>) {
        rows.forEachIndexed { i, row ->
            val currentCategory = row["category"]!!.jsonPrimitive.content
            if (i < 50) currentCategory shouldBe "odd"
            else currentCategory shouldBe "even"
        }
    }

    private fun assertSortedById(rows: List<JsonObject>) {
        var previousCategory = "odd"
        var previousId = 0
        for (row in rows) {
            val currentCategory = row["category"]!!.jsonPrimitive.content
            val currentId = row["id"]!!.jsonPrimitive.int

            if (previousCategory == "odd" && currentCategory == "even") {
                previousId shouldBeGreaterThan currentId
            } else if (previousCategory == currentCategory) {
                previousId shouldBeLessThan currentId
            }

            previousCategory = currentCategory
            previousId = currentId
        }
    }

    @Test
    fun `test kotlin dataframe conversion groupby`() {
        val json = executeScriptAndParseDataframeResult(
            """
            data class Row(val id: Int, val group: Int)
            val df = (1..100).map { Row(it, if (it <= 50) 1 else 2) }.toDataFrame()
            KotlinNotebookPluginUtils.convertToDataFrame(df.groupBy("group"))
            """.trimIndent()
        )

        assertDataFrameDimensions(json, 2, 2)

        val rows = json["kotlin_dataframe"]!!.jsonArray
        rows.getObj(0).get("group1")!!.jsonArray.size shouldBe 50
        rows.getObj(1).get("group1")!!.jsonArray.size shouldBe 50
    }

    // Regression KTNB-424
    @Test
    fun `test kotlin dataframe conversion ReducedGroupBy`() {
        shouldNotThrow<Throwable> {
            val json = executeScriptAndParseDataframeResult(
                """
                data class Row(val id: Int, val group: Int)
                val df = (1..100).map { Row(it, if (it <= 50) 1 else 2) }.toDataFrame()
                KotlinNotebookPluginUtils.convertToDataFrame(df.groupBy("group").first())
                """.trimIndent()
            )

            assertDataFrameDimensions(json, 2, 2)
        }
    }
}
