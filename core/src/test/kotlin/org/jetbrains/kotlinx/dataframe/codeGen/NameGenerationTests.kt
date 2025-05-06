package org.jetbrains.kotlinx.dataframe.codeGen

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.kotlinx.dataframe.impl.codeGen.join
import org.jetbrains.kotlinx.dataframe.impl.codeGen.process
import org.junit.Test

class NameGenerationTests {

    val df = dataFrameOf("first column", "second_column", "____")(3, 5, 7)

    @Test
    fun `interface generation`() {
        val codeGen = CodeGenerator.create()
        val code = codeGen.generate(
            schema = df.schema(),
            name = "DataType",
            fields = true,
            extensionProperties = false,
            isOpen = false,
            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
            knownMarkers = emptyList(),
        ).code

        val expected =
            """
            @DataSchema(isOpen = false)
            interface DataType {
                @ColumnName("____")
                val `____`: kotlin.Int
                @ColumnName("first column")
                val `first column`: kotlin.Int
                val second_column: kotlin.Int
            }
            """.trimIndent()

        code.snippets.join() shouldBe expected
    }

    @Suppress("ktlint:standard:property-naming")
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
        val snippets = codeGen.process<DataRecord>()
        snippets shouldHaveSize 4
        val lines = snippets.join().split("\n")
        lines shouldHaveSize 8
        lines.forEach { line ->
            line.count { char -> char == '`' } shouldBe 2
        }
    }
}
