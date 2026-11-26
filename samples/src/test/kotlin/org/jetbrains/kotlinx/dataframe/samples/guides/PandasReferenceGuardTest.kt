package org.jetbrains.kotlinx.dataframe.samples.guides

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import org.junit.Test
import java.io.File

/**
 * Drift guard for `pandasToKotlinDataFrameReference.md`.
 *
 * The tables of that page are hand-written inline code, so Korro cannot refresh them. This test takes every Kotlin
 * cell of every table and requires it to occur, verbatim, in [PandasReferenceCells] - a file the compiler checks.
 * A new or edited cell without a compiled twin turns this test red.
 *
 * A Kotlin column is one whose header starts with `Kotlin DataFrame` or is `Write instead`. Inside it, a code span is
 * a cell when it looks like a call: it starts with `df`, `df1`, `DataFrame.`, `dataFrameOf(` or `for (`. Other spans
 * are words from a note (`by`, `B`, `89.17`) and are skipped. A cell with `...` is a template and is skipped too.
 */
class PandasReferenceGuardTest {

    private val page = File("../docs/StardustDocs/topics/guides/pandasToKotlinDataFrameReference.md")
    private val cellsFile = File(
        "src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/guides/PandasReferenceCells.kt",
    )

    private val callLike = Regex("""^(df\d?[.\s\[{]|DataFrame\.|dataFrameOf\(|for \()""")

    private fun compact(text: String) = text.filterNot { it.isWhitespace() }

    private fun isKotlinColumn(header: String) = header.startsWith("Kotlin DataFrame") || header == "Write instead"

    private fun kotlinCells(): List<String> {
        val cells = mutableListOf<String>()
        var kotlinColumns: List<Int>? = null
        for (line in page.readLines()) {
            if (!line.startsWith("|")) {
                kotlinColumns = null
                continue
            }
            val parts = line.trim().trim('|').split("|").map { it.trim() }
            if (parts.all { part -> part.all { it in "-: " } }) continue // the |---|---| line
            val columns = kotlinColumns
            if (columns == null) { // the header line
                kotlinColumns = parts.indices.filter { isKotlinColumn(parts[it]) }
                continue
            }
            for (i in columns) {
                if (i >= parts.size) continue
                Regex("`([^`]+)`").findAll(parts[i]).map { it.groupValues[1] }.forEach { span ->
                    if (callLike.containsMatchIn(span) && "..." !in span) cells += span
                }
            }
        }
        return cells
    }

    @Test
    fun everyKotlinCellOfTheReferenceIsCompiled() {
        // Only code counts: a cell mentioned in a comment or in KDoc must not "confirm" itself.
        val code = cellsFile.readLines()
            .filterNot { line -> line.trim().let { it.startsWith("//") || it.startsWith("*") || it.startsWith("/*") } }
            .joinToString("\n")
        val compiled = compact(code)
        val cells = kotlinCells()

        // The page has ~70 call-like cells. A parser that quietly finds none must not pass.
        cells.size shouldBeGreaterThanOrEqual 50

        cells.filter { compact(it) !in compiled }.distinct().shouldBeEmpty()
    }
}
