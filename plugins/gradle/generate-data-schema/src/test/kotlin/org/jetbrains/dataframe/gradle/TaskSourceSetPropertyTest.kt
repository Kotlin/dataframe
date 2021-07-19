package org.jetbrains.dataframe.gradle

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldNotBe
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test
import java.io.File

class TaskSourceSetPropertyTest {
    @Test
    fun `extension sourceSet don't exist`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            packageName = "org.example.test"
            sourceSet = "myMain"
            schema {
                data = "123"
                name = "org.example.my.321"
            }
        }
        val exception = shouldThrow<ProjectConfigurationException> {
            project.evaluate()
        }
        exception.causes.single().message shouldContain "SourceSet myMain not found in root project 'test'"
    }

    @Test
    fun `task evaluates most specific source set first`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            packageName = "org.example.test"
            sourceSet = "myMain"
            schema {
                data = "123"
                sourceSet = "myMain1"
                name = "org.example.my.321"
            }
        }
        val exception = shouldThrow<ProjectConfigurationException> {
            project.evaluate()
        }
        exception.causes.single().message shouldContain "SourceSet myMain1 not found in root project 'test'"
    }

    @Test
    fun `src convention is main source set`() {
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
                    id("org.jetbrains.dataframe.schema-generator")
                }
                
                repositories {
                    mavenCentral() 
                }
                
                dependencies {
                    implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
                }
                
                schemaGenerator {
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
    fun `src convention is jvmMain source set for multiplatform project`() {
        val (_, result) = runGradleBuild(":generateAll") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("multiplatform") version "1.4.10"
                    id("org.jetbrains.dataframe.schema-generator")
                }
                
                repositories {
                    mavenCentral() 
                }
                
                kotlin {
                    jvm()
                    
                    sourceSets {
                        val jvmMain by getting {
                            dependencies {
                                implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
                            }
                        }
                    }
                }
                
                schemaGenerator {
                    schema {
                        data = "$dataFile"
                        name = "Schema"
                        packageName = ""
                    }
                }
            """.trimIndent()

        }
        result.task(":generateAll")?.outcome shouldBe TaskOutcome.SUCCESS
    }
}
