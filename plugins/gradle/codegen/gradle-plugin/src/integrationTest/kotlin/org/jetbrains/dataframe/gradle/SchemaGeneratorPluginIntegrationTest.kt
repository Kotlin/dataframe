package org.jetbrains.dataframe.gradle

import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test
import java.io.File

class SchemaGeneratorPluginIntegrationTest {
    @Test
    fun `generated code resolved`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                import org.jetbrains.dataframe.DataFrame
                import org.jetbrains.dataframe.io.read
                import org.jetbrains.dataframe.typed
                import org.jetbrains.dataframe.filter
                
                fun main() {
                    val df = DataFrame.read("$dataFile").typed<Schema>()
                    val df1 = df.filter { age != null }
                }
            """.trimIndent())

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "1.4.10"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral() 
                }
                
                dependencies {
                    implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
                }
                
                kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")

                dataframes {
                    schema {
                        data = "$dataFile"
                        name = "Schema"
                        packageName = ""
                    }
                }
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `code with preprocessing errors won't compile`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                import org.jetbrains.dataframe.DataFrame
                import org.jetbrains.dataframe.io.read
                import org.jetbrains.dataframe.typed
                import org.jetbrains.dataframe.filter
                
                @org.jetbrains.dataframe.annotations.DataSchema
                interface MySchema<T> {
                    val age: Int
                }
                
                fun main() {
                    val df = DataFrame.read("$dataFile").typed<MySchema>()
                    val df1 = df.filter { age != null }
                }
            """.trimIndent())

            @Suppress("DuplicatedCode")
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "1.4.10"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral() 
                }
                
                dependencies {
                    implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
                }
                
                kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.FAILED
    }
}
