package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.jupyter.parser.JupyterParser
import org.jetbrains.jupyter.parser.notebook.CodeCell
import org.jetbrains.jupyter.parser.notebook.Output
import org.jetbrains.kotlinx.dataframe.BuildConfig
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.Locale

/**
 * Runs the example notebooks in the dev folder (`/examples/notebooks/dev`), only when
 * debug mode is on:
 * `kotlin.dataframe.debug=true`
 */
class SampleNotebooksTests : DataFrameJupyterTest() {

    /**
     * Skips the test if debug mode is off.
     */
    @Suppress("KotlinConstantConditions")
    @Before
    fun checkDebugMode() = Assume.assumeTrue(BuildConfig.DEBUG)

    @Test
    fun puzzles() = exampleTest("puzzles", "40 puzzles")

    @Test
    fun github() =
        exampleTest(
            dir = "github",
            cellClause = CellClause.stopAfter { cell ->
                "personal access token" in cell.source
            },
            cleanup = {
                File("jetbrains.json").delete()
            },
        )

    @Test
    fun titanic() =
        exampleTest(
            dir = "titanic",
            notebookName = "Titanic",
            replacer = CodeReplacer.byMap(
                testFile("titanic", "titanic.csv"),
            ),
        )

    @Test
    fun wine() =
        exampleTest(
            dir = "wine",
            notebookName = "WineNetWIthKotlinDL",
            replacer = CodeReplacer.byMap(
                testFile("wine", "winequality-red.csv"),
            ),
        )

    @Test
    fun netflix() {
        val currentLocale = Locale.getDefault()
        try {
            // Set explicit locale as of test data contains locale-dependent values (date for parsing)
            Locale.setDefault(Locale.forLanguageTag("en-US"))

            exampleTest(
                dir = "netflix",
                replacer = CodeReplacer.byMap(
                    testFile("netflix", "country_codes.csv"),
                    testFile("netflix", "netflix_titles.csv"),
                ),
            )
        } finally {
            Locale.setDefault(currentLocale)
        }
    }

    @Test
    fun movies() =
        exampleTest(
            dir = "movies",
            replacer = CodeReplacer.byMap(
                testFile("movies", "movies.csv"),
            ),
            // There is no tags data in repository
            cellClause = CellClause.stopAfter { cell ->
                "tags.csv" in cell.source
            },
        )

    @Test
    fun top12GermanCompanies() =
        exampleTest(
            dir = "top_12_german_companies",
            replacer = CodeReplacer.byMap(
                testFile("top_12_german_companies", "top_12_german_companies.csv"),
            ),
        )

    @Test
    fun json() =
        exampleTest(
            dir = "json",
            notebookName = "KeyValueAndOpenApi",
            cellClause = CellClause {
                // skip OOM cells
                it.metadata.tags?.contains("skiptest") != true
            },
            replacer = CodeReplacer.byMap(
                testFile("json", "api_guru_list.json"),
                testFile("json", "apiGuruMetrics.json"),
                testFile("json", "ApiGuruOpenApi.yaml"),
            ),
        )

    private fun doTest(
        notebookPath: String,
        replacer: CodeReplacer,
        cellClause: CellClause,
        cleanup: () -> Unit = {},
    ) {
        val notebookFile = File(notebookPath)
        val notebook = JupyterParser.parse(notebookFile)
        val finalClause = cellClause and CellClause.IS_CODE

        val codeCellsData = notebook.cells
            .filter { finalClause.isAccepted(it) }
            .map { CodeCellData(it.source, (it as? CodeCell)?.outputs.orEmpty()) }

        try {
            for (codeCellData in codeCellsData) {
                val code = codeCellData.code
                val codeToExecute = replacer.replace(code)

                // println("Executing code:\n$codeToExecute")
                val cellResult = execRendered(codeToExecute)
                // println(cellResult)
            }
        } finally {
            cleanup()
        }
    }

    private fun exampleTest(
        dir: String,
        notebookName: String? = null,
        replacer: CodeReplacer = CodeReplacer.DEFAULT,
        cellClause: CellClause = CellClause { true },
        cleanup: () -> Unit = {},
    ) {
        val fileName = if (notebookName == null) "$dir.ipynb" else "$notebookName.ipynb"
        doTest("$NOTEBOOK_EXAMPLES_PATH/$dir/$fileName", replacer, cellClause, cleanup)
    }

    data class CodeCellData(val code: String, val outputs: List<Output>)

    companion object {
        const val NOTEBOOK_EXAMPLES_PATH = "../examples/notebooks/dev"

        fun testFile(folder: String, fileName: String) = fileName to "$NOTEBOOK_EXAMPLES_PATH/$folder/$fileName"
    }
}
