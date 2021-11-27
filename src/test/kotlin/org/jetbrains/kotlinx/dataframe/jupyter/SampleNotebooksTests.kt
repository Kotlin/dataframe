package org.jetbrains.kotlinx.dataframe.jupyter

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.Test
import java.io.File

class SampleNotebooksTests : JupyterReplTestCase() {
    @Test
    fun puzzles() = exampleTest("puzzles", "40 puzzles")

    private fun doTest(notebookPath: String) {
        val notebookFile = File(notebookPath)
        val notebook = parser.decodeFromString(serializer<JupyterNotebook>(), notebookFile.readText())
        val codeCellsData = notebook.cells.mapNotNull {
            if (it.cell_type == "code") {
                CodeCellData(it.source.joinToString(""), it.outputs)
            } else null
        }

        for (codeCellData in codeCellsData) {
            val code = codeCellData.code
            if ("%use dataframe" in code || "%useLatest" in code) {
                println("Skipping code:\n$code")
                continue
            }

            println("Executing code:\n$code")
            val cellResult = exec(code)
            println(cellResult)
        }
    }

    private fun exampleTest(dir: String, notebookName: String? = null) {
        val fileName = if (notebookName == null) "$dir.ipynb" else "$notebookName.ipynb"
        doTest("examples/jupyter-notebooks/$dir/$fileName")
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
    }
}
