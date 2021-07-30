package org.jetbrains.dataframe.gradle

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

internal class SchemaGeneratorPluginTes {

    lateinit var dataDir: File

    @Before
    fun before() {
        dataDir = File("../../../../data")
    }

    @Test
    fun `plugin configured via configure`() {
        val (_, result) = runGradleBuild(":generateTest") {
            """
            import java.net.URL
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "1.4.10"
                id("org.jetbrains.dataframe.schema-generator-base")
            }
            
            repositories {
                mavenCentral() 
            }

            configure<SchemaGeneratorExtension> {
                schema {
                    data = URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json")
                    name = "Test"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin configured via extension DSL`() {
        val (_, result) = runGradleBuild(":generateTest") {
            """
            import java.net.URL
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "1.4.10"
                id("org.jetbrains.dataframe.schema-generator-base")
            }
            
            repositories {
                mavenCentral() 
            }

            dataframes {
                schema {
                    data = URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json")
                    name = "Test"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin configured via extension DSL with Groovy`() {
        val buildDir = Files.createTempDirectory("test").toFile()
        val buildFile = File(buildDir, "build.gradle")
        buildFile.writeText(
            """
                import java.net.URL
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    id "org.jetbrains.kotlin.jvm" version "1.4.10"
                    id "org.jetbrains.dataframe.schema-generator-base"
                }
                
                repositories {
                    mavenCentral() 
                }
    
                dataframes {
                    schema {
                        data = new URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json")
                        name = "Test"
                        packageName = "org.test"
                    }
                }
            """.trimIndent()
        )
        val result = gradleRunner(buildDir, ":generateTest").build()
        result.task(":generateTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin configure multiple schemas from URLs via extension`() {
        val (_, result) = runGradleBuild(":generateAll") {
            """
            import java.net.URL
            
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "1.4.10"
                id("org.jetbrains.dataframe.schema-generator-base")
            }
            
            repositories {
                mavenCentral() 
            }

            dataframes {
                schema {
                    data = URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json")
                    name = "Test"
                    packageName = "org.test"
                }
                schema {
                    data = URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/ghost.json")
                    name = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateTest")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":generateSchema")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin configure multiple schemas from files via extension`() {
        val (_, result) = runGradleBuild(":generateAll") {
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "1.4.10"
                id("org.jetbrains.dataframe.schema-generator-base")
            }
            
            repositories {
                mavenCentral() 
            }

            dataframes {
                schema {
                    data = File("$dataDir/ghost.json")
                    name = "Test"
                    packageName = "org.test"
                }
                schema {
                    data = File("$dataDir/playlistItems.json")
                    name = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateTest")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":generateSchema")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin configure multiple schemas from strings via extension`() {
        val (_, result) = runGradleBuild(":generateAll") { buildDir ->
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension 
               
            plugins {
                kotlin("jvm") version "1.4.10"
                id("org.jetbrains.dataframe.schema-generator-base")
            }
            
            repositories {
                mavenCentral() 
            }

            dataframes {
                schema {
                    data = "$dataDir/ghost.json"
                    name = "Test"
                    packageName = "org.test"
                }
                schema {
                    data = "$dataDir/playlistItems.json"
                    name = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateTest")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":generateSchema")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `compileKotlin depends on generateAll task`() {
        val (_, result) = runGradleBuild(":compileKotlin") { buildDir ->
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "1.4.10"
                id("org.jetbrains.dataframe.schema-generator-base")
            }
            
            repositories {
                mavenCentral() 
            }
            
            dependencies {
                implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
            }

            dataframes {
                schema {
                    data = File("$dataDir/ghost.json")
                    name = "Test"
                    packageName = "org.test"
                }
                schema {
                    data = File("$dataDir/playlistItems.json")
                    name = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateTest")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":generateSchema")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin doesn't break multiplatform build without JVM`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            val kotlin = File(buildDir, "src/jsMain/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                fun main() {
                    console.log("Hello, Kotlin/JS!")
                }
            """.trimIndent())
            dataFile.writeText(TestData.csvSample)
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("multiplatform") version "1.4.10"
                    id("org.jetbrains.dataframe.schema-generator-base")
                }
                
                repositories {
                    mavenCentral() 
                }
                
                kotlin {
                    sourceSets {
                        js {
                            browser()
                        }
                    }
                }
                
                dataframes {
                    schema {
                        data = "$dataFile"
                        name = "Schema"
                        packageName = ""
                        src = file("$buildDir")
                    }
                }
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `fallback all properties to conventions`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "1.4.10"
                    id("org.jetbrains.dataframe.schema-generator-base")
                }
                
                repositories {
                    mavenCentral() 
                }
                
                dependencies {
                    implementation("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")
                }
                
                dataframes {
                    schema {
                        data = "$dataFile"
                    }
                }
            """.trimIndent()
        }
        result.task(":generateData")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `most specific sourceSet is used in the packageName inference`() {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply(KotlinPlatformJvmPlugin::class.java)
        project.extensions.getByType(KotlinJvmProjectExtension::class.java).apply {
            sourceSets.create("main1")
        }
        project.extensions.getByType(SchemaGeneratorExtension::class.java).apply {
            sourceSet = "main"
            schema {
                sourceSet = "main1"
                data = "123"
                name = "321"
            }
        }
        project.file("src/main1/kotlin/org/example/test").also { it.mkdirs() }
        project.evaluate()
        (project.tasks.getByName("generate321") as GenerateDataSchemaTask).dataSchema.get()
            .shouldBe(project.file("src/main1/kotlin/org/example/test/dataframe/Generated321.kt"))
    }
}
