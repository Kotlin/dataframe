package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.jupyter.parser.JupyterParser
import org.jetbrains.jupyter.parser.notebook.CodeCell
import org.jetbrains.jupyter.parser.notebook.Output
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.util.Locale

class SampleNotebooksTests : DataFrameJupyterTest() {
    @Test
    @Ignore
    fun puzzles() = exampleTest("puzzles", "40 puzzles")

    @Test
    fun github() =
        exampleTest(
            "github",
            cellClause =
                CellClause.stopAfter { cell ->
                    "personal access token" in cell.source
                },
            cleanup = {
                File("jetbrains.json").delete()
            },
        )

    @Test
    @Ignore
    fun titanic() =
        exampleTest(
            "titanic",
            "Titanic",
            replacer =
                CodeReplacer.byMap(
                    "../../idea-examples/" to "$IDEA_EXAMPLES_PATH/",
                ),
        )

    @Test
    @Ignore
    fun wine() =
        exampleTest(
            "wine",
            "WineNetWIthKotlinDL",
            replacer =
                CodeReplacer.byMap(
                    testFile("wine", "winequality-red.csv"),
                ),
        )

    @Test
    @Ignore
    fun netflix() {
        val currentLocale = Locale.getDefault()
        try {
            // Set explicit locale as of test data contains locale-dependent values (date for parsing)
            Locale.setDefault(Locale.forLanguageTag("en-US"))

            exampleTest(
                "netflix",
                replacer =
                    CodeReplacer.byMap(
                        testFile("netflix", "country_codes.csv"),
                        testFile("netflix", "netflix_titles.csv"),
                    ),
            )
        } finally {
            Locale.setDefault(currentLocale)
        }
    }

    @Test
    @Ignore
    fun movies() =
        exampleTest(
            "movies",
            replacer =
                CodeReplacer.byMap(
                    "ml-latest/movies.csv" to "$IDEA_EXAMPLES_PATH/movies/src/main/resources/movies.csv",
                ),
            // There is no tags data in repository
            cellClause =
                CellClause.stopAfter { cell ->
                    "tags.csv" in cell.source
                },
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

        val codeCellsData =
            notebook.cells
                .filter { finalClause.isAccepted(it) }
                .map { CodeCellData(it.source, (it as? CodeCell)?.outputs.orEmpty()) }

        try {
            for (codeCellData in codeCellsData) {
                val code = codeCellData.code
                val codeToExecute = replacer.replace(code)

                println("Executing code:\n$codeToExecute")
                val cellResult = execRendered(codeToExecute)
                println(cellResult)
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
        const val IDEA_EXAMPLES_PATH = "../examples/idea-examples"
        const val NOTEBOOK_EXAMPLES_PATH = "../examples/notebooks"

        fun testFile(
            folder: String,
            fileName: String,
        ) = fileName to "$NOTEBOOK_EXAMPLES_PATH/$folder/$fileName"
    }
}
