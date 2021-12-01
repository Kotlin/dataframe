package org.jetbrains.kotlinx.dataframe.codeGen

import io.kotest.matchers.shouldBe
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.dataframe.impl.codeGen.process
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.junit.Test

class NameGenerationTests {

    val df = dataFrameOf("first column", "second_column", "____")(3, 5, 7)

    @Test
    fun `interface generation`() {
        val codeGen = CodeGenerator.create()
        val code = codeGen.generate(df.schema(), "DataType", true, false, isOpen = false, MarkerVisibility.IMPLICIT_PUBLIC, emptyList()).code

        val expected = """
            @DataSchema(isOpen = false)
            interface DataType {
            	@ColumnName("____")
                val `____`: kotlin.Int
            	@ColumnName("first column")
                val `first column`: kotlin.Int
                val second_column: kotlin.Int
            }
        """.trimIndent()

        code.declarations shouldBe expected
    }

    @DataSchema
    interface DataRecord {
        @ColumnName("first column")
        val `first column`: Int
        @ColumnName("second column")
        val `second column`: Int
    }

    @Test
    fun `properties generation`() {
        val codeGen = ReplCodeGenerator.create()
        val code = codeGen.process<DataRecord>().split("\n")
        code.size shouldBe 4
        code.forEach {
            it.count { it == '`' } shouldBe 2
        }
    }
}
