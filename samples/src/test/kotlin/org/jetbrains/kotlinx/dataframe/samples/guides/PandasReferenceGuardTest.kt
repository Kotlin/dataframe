package org.jetbrains.kotlinx.dataframe.samples.guides

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import org.junit.Test
import java.io.File

/**
 * Drift guard for `pandasToKotlinDataFrameReference.md`.
 *
 * The tables of that page are hand-written inline code, so Korro cannot refresh them. This test takes every Kotlin
 * cell of every table and requires it to be, verbatim, one statement of [PandasReferenceCells] - a file the compiler
 * checks. A new or edited cell without a compiled twin turns this test red. Spacing is ignored; the whole statement
 * has to match, so a truncated cell does not pass because a longer statement contains it.
 *
 * A Kotlin column is one whose header starts with `Kotlin DataFrame` or is `Write instead`. Inside it, a code span is
 * a cell when it looks like a call: it starts with `df`, `df1`, `DataFrame.`, `dataFrameOf(` or `for (`. Other spans
 * are words from a note (`by`, `B`, `89.17`) and are skipped. A cell with `...` is a template and is skipped too.
 *
 * What this test does NOT look at: the `Notes` column, the prose and the "Rules" section, the Pandas columns, and the
 * left column of the table of names that do not exist. Those are checked by hand, by the cases of
 * `pandas-reference-cases.json` and by [PandasReferenceClaimsTest], or not at all.
 */
class PandasReferenceGuardTest {

    private val root = generateSequence(File("").absoluteFile) { it.parentFile }
        .first { File(it, "settings.gradle.kts").exists() }

    private val page = File(root, "docs/StardustDocs/topics/guides/pandasToKotlinDataFrameReference.md")

    private val cellsFile = File(
        root,
        "samples/src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/guides/PandasReferenceCells.kt",
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
        val statements = cellsFile.readLines()
            .map { it.trim() }
            .filterNot { it.isEmpty() || it.startsWith("//") || it.startsWith("*") || it.startsWith("/*") }
            .map { compact(it) }
            .toSet()
        val cells = kotlinCells()

        // The page has well over this many call-like cells. A parser that quietly finds far fewer is broken,
        // and an empty list would pass the check below.
        cells.size shouldBeGreaterThanOrEqual MINIMUM_CELLS_THE_PARSER_MUST_FIND

        cells.filter { compact(it) !in statements }.distinct().shouldBeEmpty()
    }

    private companion object {
        const val MINIMUM_CELLS_THE_PARSER_MUST_FIND = 60
    }
}
