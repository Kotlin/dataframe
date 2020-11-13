package org.jetbrains.dataframe.codeGen

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.CodeGenerator
import org.jetbrains.dataframe.dataFrameOf
import org.junit.Test

class NameGenerationTests {

    @Test
    fun `column name with space`(){

        val df = dataFrameOf("first column", "second column")(3, 5)
        val codeGen = CodeGenerator()
        val code = codeGen.generate(df)

        val expected ="""
            @DataFrameType(isOpen = false)
            interface DataFrameType1{
            	@ColumnName("first column")
                val `first column`: Int
            	@ColumnName("second column")
                val `second column`: Int
            }
        """.trimIndent()

        code[0] shouldBe expected
    }
}