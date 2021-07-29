package org.jetbrains.dataframe.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import kotlin.test.Test

class MyTest {

    @Test
    fun test() {
        val compilation = KotlinCompilation().apply {
            sources = listOf(SourceFile.kotlin("Test.kt", """
                fun main() {
                    println("Hello, world")
                }
            """.trimIndent()))
            val test = listOf(annotations, dataColumn, dataFrame, dataRow, SourceFile.kotlin("MySources.kt", """
                import org.jetbrains.dataframe.annotations.*
                
                @DataSchema
                interface Hello {
                    val name: String
                    val `test name`: Int
                }      
            """.trimIndent()))
            symbolProcessorProviders = listOf(DataFrameSymbolProcessorProvider())
        }
        val result = compilation.compile()
        println(result.outputDirectory)
        assert(result.exitCode == KotlinCompilation.ExitCode.OK)
    }
}
