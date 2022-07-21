package org.jetbrains.dataframe.gradle

import io.kotest.assertions.asClue
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.junit.Test
import java.io.File
import java.nio.file.Files

internal class SchemaGeneratorPluginTest {

    private companion object {
        private val KOTLIN_VERSION = TestData.kotlinVersion
    }

    @Test
    fun `plugin configured via configure`() {
        val (_, result) = runGradleBuild(":generateDataFrameTest") {
            """
            import java.net.URL
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "$KOTLIN_VERSION"
                id("org.jetbrains.kotlinx.dataframe")
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
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin configured via extension DSL`() {
        val (_, result) = runGradleBuild(":generateDataFrameTest") {
            """
            import java.net.URL
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "$KOTLIN_VERSION"
                id("org.jetbrains.kotlinx.dataframe")
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
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
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
                    id "org.jetbrains.kotlin.jvm" version "$KOTLIN_VERSION"
                    id "org.jetbrains.kotlinx.dataframe"
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
        val result = gradleRunner(buildDir, ":generateDataFrameTest").build()
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `delimiters configured with Groovy`() {
        val buildDir = Files.createTempDirectory("test").toFile()
        val buildFile = File(buildDir, "build.gradle")
        buildFile.writeText(
            """
                import java.net.URL
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    id "org.jetbrains.kotlin.jvm" version "$KOTLIN_VERSION"
                    id "org.jetbrains.kotlinx.dataframe"
                }
                
                repositories {
                    mavenCentral() 
                }
    
                dataframes {
                    schema {
                        data = new URL("https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/playlistItems.json")
                        name = "Test"
                        packageName = "org.test"
                        withNormalizationBy('-_\t ')
                    }
                }
            """.trimIndent()
        )
        val result = gradleRunner(buildDir, ":generateDataFrameTest").build()
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin configure multiple schemas from URLs via extension`() {
        val (_, result) = runGradleBuild(":generateDataFrames") {
            """
            import java.net.URL
            
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "$KOTLIN_VERSION"
                id("org.jetbrains.kotlinx.dataframe")
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
                    data = URL("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
                    name = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":generateDataFrameSchema")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin configure multiple schemas from strings via extension`() {
        val (_, result) = runGradleBuild(":generateDataFrames") { buildDir ->
            File(buildDir, "data").also {
                it.mkdirs()
                File(it, TestData.csvName).writeText(TestData.csvSample)
                File(it, TestData.jsonName).writeText(TestData.jsonSample)
            }
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension 
               
            plugins {
                kotlin("jvm") version "$KOTLIN_VERSION"
                id("org.jetbrains.kotlinx.dataframe")
            }
            
            repositories {
                mavenCentral() 
            }

            dataframes {
                schema {
                    data = "data/${TestData.csvName}"
                    name = "Test"
                    packageName = "org.test"
                }
                schema {
                    data = "data/${TestData.jsonName}"
                    name = "Schema"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":generateDataFrameSchema")?.outcome shouldBe TaskOutcome.SUCCESS
    }


    @Test
    fun `data is string and relative path`() {
        val (_, result) = runGradleBuild(":generateDataFrameTest") { buildDir ->
            val dataDir = File(buildDir, "data").also { it.mkdirs() }
            File(dataDir, TestData.jsonName).writeText(TestData.jsonSample)
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension 
               
            plugins {
                kotlin("jvm") version "$KOTLIN_VERSION"
                id("org.jetbrains.kotlinx.dataframe")
            }
            
            repositories {
                mavenCentral() 
            }

            dataframes {
                schema {
                    data = "data/${TestData.jsonName}"
                    name = "Test"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `data is string and absolute path`() {
        val (_, result) = runGradleBuild(":generateDataFrameTest") { buildDir ->
            val dataDir = File(buildDir, "data").also { it.mkdirs() }
            val file = File(dataDir, TestData.jsonName).also { it.writeText(TestData.jsonSample) }
            val absolutePath = file.absolutePath.replace(File.separatorChar, '/')
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension 
               
            plugins {
                kotlin("jvm") version "$KOTLIN_VERSION"
                id("org.jetbrains.kotlinx.dataframe")
            }
            
            repositories {
                mavenCentral() 
            }

            dataframes {
                schema {
                    data = "$absolutePath"
                    name = "Test"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `data is string and url`() {
        val (_, result) = runGradleBuild(":generateDataFrameTest") { buildDir ->
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension 
               
            plugins {
                kotlin("jvm") version "$KOTLIN_VERSION"
                id("org.jetbrains.kotlinx.dataframe")
            }
            
            repositories {
                mavenCentral() 
            }

            dataframes {
                schema {
                    data = "https://raw.githubusercontent.com/Kotlin/dataframe/8ea139c35aaf2247614bb227756d6fdba7359f6a/data/ghost.json"
                    name = "Test"
                    packageName = "org.test"
                }
            }
            """.trimIndent()
        }
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `custom csv delimiter`() {
        val (buildDir, result) = runGradleBuild(":generateDataFrameTest") { buildDir ->
            val csv = "semicolons.csv"
            val data = File(buildDir, csv)
            data.writeText("""
                a;b;c
                1;2;3
            """.trimIndent())
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension 
               
            plugins {
                kotlin("jvm") version "$KOTLIN_VERSION"
                id("org.jetbrains.kotlinx.dataframe")
            }
            
            repositories {
                mavenCentral() 
            }

            dataframes {
                schema {
                    data = "$csv"
                    name = "Test"
                    packageName = "org.test"
                    csvOptions {
                        delimiter = ';'
                    }
                }
            }
            """.trimIndent()
        }
        result.task(":generateDataFrameTest")?.outcome shouldBe TaskOutcome.SUCCESS
        File(buildDir, "build/generated/dataframe/main/kotlin/org/test/Test.Generated.kt").asClue {
            it.readLines().let {
                it.forOne {
                    it.shouldContain("val a")
                }
                it.forOne {
                    it.shouldContain("val b")
                }
                it.forOne {
                    it.shouldContain("val c")
                }
            }
        }
    }

    @Test
    fun `most specific sourceSet is used in the packageName inference`() {
        val project = makeProject()
        project.plugins.apply(SchemaGeneratorPlugin::class.java)
        project.plugins.apply("org.jetbrains.kotlin.jvm")
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
        (project.tasks.getByName("generateDataFrame321") as GenerateDataSchemaTask).dataSchema.get()
            .shouldBe(project.file("build/generated/dataframe/main1/kotlin/org/example/test/dataframe/321.Generated.kt"))
    }
}
