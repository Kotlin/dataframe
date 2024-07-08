package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.jetbrains.kotlinx.jupyter.testkit.ReplProvider
import org.junit.Test

class KtorClientIntegrationTest : JupyterReplTestCase(ReplProvider.forLibrariesTesting(listOf("dataframe", "ktor-client"))) {
    @Test
    fun `extension function for NotebookHttpResponse is available`() {
        execRaw("""%use dataframe""")
        execRaw("""%use ktor-client""")
        val test = """
            fun test() {
                http.get("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains.json").bodyAsDataFrame()
            }""".trimIndent()
        execRaw(test)
    }
}
