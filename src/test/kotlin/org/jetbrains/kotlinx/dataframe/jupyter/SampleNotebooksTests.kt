package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.jupyter.testkit.notebook.JupyterNotebookParser
import org.jetbrains.kotlinx.jupyter.testkit.notebook.JupyterOutput
import org.junit.Ignore
import org.junit.Test
import java.io.File

class SampleNotebooksTests : DataFrameJupyterTest() {
    @Test
    fun puzzles() = exampleTest("puzzles", "40 puzzles")

    @Test
    fun github() = exampleTest(
        "github",
        cellClause = CellClause.stopAfter { cell ->
            cell.source.any { cellLine ->
                cellLine.contains("personal access token")
            } 
        },
        cleanup = {
            listOf(
                "jetbrains.json",
                "src/test/kotlin/org/jetbrains/kotlinx/dataframe/samples/api/Repository.Generated.kt"
            ).forEach { File(it).delete() }
        }
    )

    @Test
    fun titanic() = exampleTest(
        "titanic", "Titanic",
        replacer = CodeReplacer.byMap(
            "../../idea-examples/" to "examples/idea-examples/"
        )
    )

    @Test
    fun wine() = exampleTest(
        "wine", "WineNetWIthKotlinDL",
        replacer = CodeReplacer.byMap(
            testFile("wine", "winequality-red.csv")
        )
    )

    @Test
    fun netflix() = exampleTest(
        "netflix",
        replacer = CodeReplacer.byMap(
            testFile("netflix", "country_codes.csv"),
            testFile("netflix", "netflix_titles.csv"),
        )
    )

    @Test
    @Ignore("Please provide a file ml-latest/movies.csv")
    fun movies() = exampleTest("movies")

    private fun doTest(
        notebookPath: String,
        replacer: CodeReplacer,
        cellClause: CellClause,
        cleanup: () -> Unit = {}
    ) {
        val notebookFile = File(notebookPath)
        val notebook = JupyterNotebookParser.parse(notebookFile)
        val finalClause = cellClause and CellClause.IS_CODE

        val codeCellsData = notebook.cells
            .filter { finalClause.isAccepted(it) }
            .map { CodeCellData(it.source.joinToString(""), it.outputs) }

        try {
            for (codeCellData in codeCellsData) {
                val code = codeCellData.code
                val codeToExecute = replacer.replace(code)

                println("Executing code:\n$codeToExecute")
                val cellResult = exec(codeToExecute)
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
        cleanup: () -> Unit = {}
    ) {
        val fileName = if (notebookName == null) "$dir.ipynb" else "$notebookName.ipynb"
        doTest("$jupyterExamplesPath/$dir/$fileName", replacer, cellClause, cleanup)
    }

    data class CodeCellData(
        val code: String,
        val outputs: List<JupyterOutput>,
    )

    companion object {
        const val jupyterExamplesPath = "examples/jupyter-notebooks"

        fun testFile(folder: String, fileName: String) = fileName to "$jupyterExamplesPath/$folder/$fileName"
    }
}
