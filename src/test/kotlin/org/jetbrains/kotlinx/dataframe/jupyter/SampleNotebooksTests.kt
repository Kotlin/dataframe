package org.jetbrains.kotlinx.dataframe.jupyter

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import org.junit.Ignore
import org.junit.Test
import java.io.File

class SampleNotebooksTests : DataFrameJupyterTest() {
    @Test
    fun puzzles() = exampleTest("puzzles", "40 puzzles")

    @Test
    @Ignore("Execution of this test leads to GitHub API rate limit exceeding")
    fun github() = exampleTest("github") {
        File("jetbrains.json").delete()
    }

    @Test
    fun titanic() = exampleTest(
        "titanic",
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
        cleanup: () -> Unit = {}
    ) {
        val notebookFile = File(notebookPath)
        val notebook = parser.decodeFromString(serializer<JupyterNotebook>(), notebookFile.readText())
        val codeCellsData = notebook.cells.mapNotNull {
            if (it.cell_type == "code") {
                CodeCellData(it.source.joinToString(""), it.outputs)
            } else null
        }

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
        cleanup: () -> Unit = {}
    ) {
        val fileName = if (notebookName == null) "$dir.ipynb" else "$notebookName.ipynb"
        doTest("$jupyterExamplesPath/$dir/$fileName", replacer, cleanup)
    }

    data class CodeCellData(
        val code: String,
        val outputs: List<JupyterOutput>,
    )

    @Serializable
    data class JupyterNotebook(
        val cells: List<JupyterCell>,
        val metadata: JsonObject? = null,
    )

    @Serializable
    data class JupyterCell(
        val cell_type: String,
        val id: String? = null,
        val metadata: JsonObject? = null,
        val source: List<String> = emptyList(),
        val outputs: List<JupyterOutput> = emptyList(),
    )

    @Serializable
    data class JupyterOutput(
        val data: JsonObject? = null,
        val execution_count: Int? = null,
        val metadata: JsonObject? = null,
        val output_type: String,
    )

    companion object {
        val parser = Json { ignoreUnknownKeys = true }
        const val jupyterExamplesPath = "examples/jupyter-notebooks"

        fun testFile(folder: String, fileName: String) = fileName to "$jupyterExamplesPath/$folder/$fileName"
    }
}
