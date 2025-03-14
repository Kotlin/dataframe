package org.jetbrains.kotlinx.dataframe.jupyter

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.jupyter.api.Code
import org.junit.Test

class CodeGenerationTests : DataFrameJupyterTest() {

    private fun Code.checkCompilation() {
        lines().forEach {
            execRendered(it)
        }
    }

    @Test
    fun `Type erased dataframe`() {
        @Language("kts")
        val a = """
            fun create(): Any? = dataFrameOf("a")(1)
            val df = create()
            df.a
        """.checkCompilation()
    }

    @Test
    fun `nullable dataframe`() {
        @Language("kts")
        val a = """
            fun create(): AnyFrame? = dataFrameOf("a")(1)
            val df = create()
            df.a
        """.checkCompilation()
    }

    @Test
    fun `nullable columnGroup`() {
        @Language("kts")
        val a = """
            fun create(): AnyCol? = dataFrameOf("a")(1).asColumnGroup().asDataColumn()
            val col = create()
            col.a
        """.checkCompilation()
    }

    @Test
    fun `nullable dataRow`() {
        @Language("kts")
        val a = """
            fun create(): AnyRow? = dataFrameOf("a")(1).single()
            val row = create()
            row.a
        """.checkCompilation()
    }

    @Test
    fun `interface without body compiled correctly`() {
        """
            val a = dataFrameOf("a")(1, 2, 3)
            val b = dataFrameOf("b")(1, 2, 3)
            val ab = dataFrameOf("a", "b")(1, 2)
            ab.a
        """.checkCompilation()
    }
}
