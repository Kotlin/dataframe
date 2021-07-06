package org.jetbrains.dataframe.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test
import java.io.File
import java.nio.file.Files


internal class SchemaGeneratorPluginTes {

    @Test
    fun `plugin configured via extension`() {
        val result = runGradleBuild(":generateTest") { buildDir ->
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
               id("org.jetbrains.dataframe.schema-generator")
            }

            configure<SchemaGeneratorExtension> {
                schema {
                    src = buildDir
                    data = java.net.URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json")
                    interfaceName = "Test"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        assert(result.task(":generateTest")?.outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `plugin configured via extension DSL`() {
        val result = runGradleBuild(":generateTest") { buildDir ->
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
               id("org.jetbrains.dataframe.schema-generator")
            }

            schemaGenerator {
                schema {
                    src = buildDir
                    data = java.net.URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json")
                    interfaceName = "Test"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        assert(result.task(":generateTest")?.outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `plugin configure muplitple schemas from URLs via extension`() {
        val result = runGradleBuild(":generateAll") { buildDir ->
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
               id("org.jetbrains.dataframe.schema-generator")
            }

            schemaGenerator {
                schema {
                    src = buildDir
                    data = java.net.URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json")
                    interfaceName = "Test"
                    packageName = "org.test"
                }
                schema {
                    src = buildDir
                    data = java.net.URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/ghost.json")
                    interfaceName = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        assert(result.task(":generateTest")?.outcome == TaskOutcome.SUCCESS)
        assert(result.task(":generateSchema")?.outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `plugin configure muplitple schemas from files via extension`() {
        val dataDir = File("../../../data")
        val result = runGradleBuild(":generateAll") { buildDir ->
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
               id("org.jetbrains.dataframe.schema-generator")
            }

            schemaGenerator {
                schema {
                    src = buildDir
                    data = File("$dataDir/ghost.json")
                    interfaceName = "Test"
                    packageName = "org.test"
                }
                schema {
                    src = buildDir
                    data = File("$dataDir/playlistItems.json")
                    interfaceName = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        assert(result.task(":generateTest")?.outcome == TaskOutcome.SUCCESS)
        assert(result.task(":generateSchema")?.outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `plugin configure muplitple schemas from strings via extension`() {
        val dataDir = File("../../../data")
        val result = runGradleBuild(":generateAll") { buildDir ->
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
               id("org.jetbrains.dataframe.schema-generator")
            }

            schemaGenerator {
                schema {
                    src = buildDir
                    data = "$dataDir/ghost.json"
                    interfaceName = "Test"
                    packageName = "org.test"
                }
                schema {
                    src = buildDir
                    data = "$dataDir/playlistItems.json"
                    interfaceName = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        assert(result.task(":generateTest")?.outcome == TaskOutcome.SUCCESS)
        assert(result.task(":generateSchema")?.outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `compileKotlin depends on generateAll task`() {
        val dataDir = File("../../../data")
        val result = runGradleBuild(":compileKotlin") { buildDir ->
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "1.4.10"
               id("org.jetbrains.dataframe.schema-generator")
            }
            
            repositories {
                mavenCentral() 
            }

            schemaGenerator {
                schema {
                    src = buildDir
                    data = File("$dataDir/ghost.json")
                    interfaceName = "Test"
                    packageName = "org.test"
                }
                schema {
                    src = buildDir
                    data = File("$dataDir/playlistItems.json")
                    interfaceName = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        assert(result.task(":generateTest")?.outcome == TaskOutcome.SUCCESS)
        assert(result.task(":generateSchema")?.outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `generated code resolved`() {
        val result = runGradleBuild(":build") { buildDir ->
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
                        src = File("$kotlin")
                        interfaceName = "Schema"
                        packageName = ""
                    }
                }
            """.trimIndent()
        }
        assert(result.task(":build")?.outcome == TaskOutcome.SUCCESS)
    }

    private fun runGradleBuild(task: String, build: (File) -> String): BuildResult {
        val buildDir = Files.createTempDirectory("test").toFile()
        val buildFile = File(buildDir, "build.gradle.kts")
        buildFile.writeText(build(buildDir))
        return gradleRunner(buildDir, task).build()
    }

    private fun gradleRunner(buildDir: File, task: String) = GradleRunner.create()
        .withProjectDir(buildDir)
        .withGradleVersion("7.0")
        .withPluginClasspath()
        .withArguments(task)
        .withDebug(true)
}
