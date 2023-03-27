package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.jupyter.api.Code
import org.junit.Test

class CodeGenerationTests : DataFrameJupyterTest() {

    private fun Code.checkCompilation() {
        lines().forEach {
            exec(it)
        }
    }

    @Test
    fun `nullable dataframe`() {
        """
            fun create(): AnyFrame? = dataFrameOf("a")(1)
            val df = create()
            df.a
        """.checkCompilation()
    }

    @Test
    fun `nullable columnGroup`() {
        """
            fun create(): AnyCol? = dataFrameOf("a")(1).asColumnGroup().asDataColumn()
            val col = create()
            col.a
        """.checkCompilation()
    }

    @Test
    fun `nullable dataRow`() {
        """
            fun create(): AnyRow? = dataFrameOf("a")(1).single()
            val row = create()
            row.a
        """.checkCompilation()
    }
}
