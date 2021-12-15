package org.jetbrains.dataframe.gradle

import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadCsvMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadJsonMethod
import org.junit.Test
import java.io.File
import kotlin.reflect.KClass

annotation class RelatedGenerator(vararg val clazz: KClass<*>)

class ApiChangesDetectionTest : AbstractDataFramePluginIntegrationTest() {
    @RelatedGenerator(
        GenerateDataSchemaTask::class,
        DefaultReadCsvMethod::class,
        DefaultReadJsonMethod::class
    )
    @Test
    fun `cast api`() {
        compiles {
            """
                import ${DataFrame::class.qualifiedName!!}
                import org.jetbrains.kotlinx.dataframe.api.cast 
                
                interface Marker
                
                fun DataFrame<*>.resolveApi() {
                    cast<Marker>()
                    cast<Marker>(true)
                }
            """.trimIndent()
        }
    }

    @RelatedGenerator(
        GenerateDataSchemaTask::class,
        DefaultReadJsonMethod::class
    )
    @Test
    fun `read json api`() {
        compiles {
            """
                import ${DataFrame::class.qualifiedName!!}
                import org.jetbrains.kotlinx.dataframe.io.readJson
                
                fun DataFrame<*>.resolveApi(s: String) {
                    DataFrame.readJson(s)
                }
            """.trimIndent()
        }
    }

    @RelatedGenerator(
        GenerateDataSchemaTask::class,
        DefaultReadCsvMethod::class,
    )
    @Test
    fun `read csv api`() {
        compiles {
            """
                import ${DataFrame::class.qualifiedName!!}
                import org.jetbrains.kotlinx.dataframe.io.readCSV
                
                fun DataFrame<*>.resolveApi(s: String, ch: Char) {
                    DataFrame.readCSV(s, ch)
                }
            """.trimIndent()
        }
    }

    private fun compiles(code: () -> String) {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText(code())
            """
                plugins {
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
                }
                
                repositories {
                    mavenCentral()
                    mavenLocal()
                }
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }
}
