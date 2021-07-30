package org.jetbrains.dataframe.ksp

import com.tschuchort.compiletesting.SourceFile
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class DataFrameSymbolProcessorTest {

    @Test
    fun `all`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
            sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                class OuterClass

                @DataSchema(isOpen = false)
                interface Hello {
                    val name: String
                    val `test name`: InnerClass
                    
                    class InnerClass
                }

                val DataFrameBase<Hello>.test1: DataColumn<String> get() = name
                val DataFrameBase<Hello>.test2: DataColumn<Hello.InnerClass> get() = `test name`
                val DataRowBase<Hello>.test3: String get() = name
                val DataRowBase<Hello>.test4: Hello.InnerClass get() = `test name`
            """.trimIndent()))
        ))
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `column name from annotation is used`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    @ColumnName("test-name")
                    val `test name`: Int
                }

                val DataFrameBase<Hello>.test2: DataColumn<Int> get() = `test name`
                val DataRowBase<Hello>.test4: Int get() = `test name`
            """.trimIndent()))
            ))
        result.kspGeneratedFiles.find { it.name == "Hello${'$'}Extensions.kt" }?.readText()?.shouldContain("this[\"test-name\"]")
        result.successfulCompilation shouldBe true
    }

    @Test
    fun `extension accessible from same package`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                package org.example

                import org.jetbrains.dataframe.annotations.*
                import org.jetbrains.dataframe.columns.*
                import org.jetbrains.dataframe.*

                @DataSchema(isOpen = false)
                interface Hello {
                    val name: String
                }

                val DataFrameBase<Hello>.test1: DataColumn<String> get() = name
                val DataRowBase<Hello>.test2: String get() = name
            """.trimIndent()))
            ))
        result.successfulCompilation shouldBe true
    }
}
