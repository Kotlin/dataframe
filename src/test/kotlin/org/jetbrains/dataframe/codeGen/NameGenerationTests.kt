package org.jetbrains.dataframe.codeGen

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.annotations.ColumnName
import org.jetbrains.dataframe.annotations.DataSchema
import org.jetbrains.dataframe.dataFrameOf
import org.jetbrains.dataframe.impl.codeGen.CodeGeneratorImpl
import org.junit.Test

class NameGenerationTests {

    val df = dataFrameOf("first column", "second column")(3, 5)

    @DataSchema
    interface DataRecord{
        @ColumnName("first column")
        val `first column`: Int
        @ColumnName("second column")
        val `second column`: Int
    }

    @Test
    fun `interface generation`(){

        val codeGen = CodeGeneratorImpl()
        val code = codeGen.generate(df)

        val expected ="""
            @DataSchema(isOpen = false)
            interface DataFrameType1{
            	@ColumnName("first column")
                val `first column`: Int
            	@ColumnName("second column")
                val `second column`: Int
            }
        """.trimIndent()

        code!!.declarations shouldBe expected
    }

    @Test
    fun `properties generation`(){

        val codeGen = CodeGeneratorImpl()
        val code = codeGen.generateExtensionProperties(DataRecord::class)!!.split("\n")
        code.size shouldBe 4
        code.forEach {
            it.count { it == '`' } shouldBe 2
        }
    }
}