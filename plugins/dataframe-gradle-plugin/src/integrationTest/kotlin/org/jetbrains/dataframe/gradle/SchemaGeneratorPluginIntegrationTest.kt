package org.jetbrains.dataframe.gradle

import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.util.*

class SchemaGeneratorPluginIntegrationTest {
    private companion object {
        private const val TRANSITIVE_DF_DEPENDENCY = "KSP has dependency on latest dataframe, but it's not yet published"
        private const val FIRST_NAME = "first.csv"
        private const val SECOND_NAME = "second.csv"
        private const val KOTLIN_VERSION = "1.6.0"
    }
    private lateinit var dataframeJarPath: String

    @Before
    fun before() {
        val properties = Properties().also {
            it.load(javaClass.getResourceAsStream("df.properties"))
        }
        dataframeJarPath = properties.getProperty("DATAFRAME_JAR")
    }

    @Test
    fun `compileKotlin depends on generateAll task`() {
        val (_, result) = runGradleBuild(":compileKotlin") { buildDir ->
            File(buildDir, FIRST_NAME).also { it.writeText(TestData.csvSample) }
            File(buildDir, SECOND_NAME).also { it.writeText(TestData.csvSample) }
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "$KOTLIN_VERSION"
                id("org.jetbrains.kotlin.plugin.dataframe")
            }
            
            repositories {
                mavenCentral() 
                mavenLocal()
            }
            
            dependencies {
                implementation(files("$dataframeJarPath"))
            }

            dataframes {
                schema {
                    data = file("$FIRST_NAME")
                    name = "Test"
                    packageName = "org.test"
                }
                schema {
                    data = file("$SECOND_NAME")
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
    fun `packageName convention is 'dataframe'`() {
        val (dir, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, TestData.csvName)
            dataFile.writeText(TestData.csvSample)

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenCentral() 
                    mavenLocal()
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
                }
                
                dataframes {
                    schema {
                        data = file("${TestData.csvName}")
                        name = "Data"
                    }
                }
            """.trimIndent()
        }
        result.task(":generateDataFrameData")?.outcome shouldBe TaskOutcome.SUCCESS
        File(dir, "src/main/kotlin/dataframe/Data.Generated.kt").exists() shouldBe true
    }

    @Test
    fun `fallback all properties to conventions`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, TestData.csvName)
            dataFile.writeText(TestData.csvSample)

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenCentral() 
                    mavenLocal()
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
                }
                
                dataframes {
                    schema {
                        data = file("${TestData.csvName}")
                    }
                }
            """.trimIndent()
        }
        result.task(":generateDataFrameData")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Ignore(TRANSITIVE_DF_DEPENDENCY)
    @Test
    fun `generated schemas and extensions resolved`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                import org.jetbrains.kotlinx.DataFrame
                import org.jetbrains.kotlinx.read
                import org.jetbrains.kotlinx.typed
                import org.jetbrains.kotlinx.filter
                
                fun main() {
                    val df = DataFrame.read("$dataFile").typed<Schema>()
                    val df1 = df.filter { age != null }
                }
            """.trimIndent())

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral() 
                    maven(url="https://jitpack.io")
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
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
    fun `generated schemas resolved in jvmMain source set for multiplatform project`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, TestData.csvName)
            dataFile.writeText(TestData.csvSample)
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("multiplatform") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenCentral() 
                    mavenLocal()
                }
                
                kotlin {
                    jvm()
                    
                    sourceSets {
                        val jvmMain by getting {
                            dependencies {
                                implementation(files("$dataframeJarPath"))
                            }
                        }
                    }
                }
                
                dataframes {
                    schema {
                        data = file("${TestData.csvName}")
                        name = "Schema"
                        packageName = ""
                    }
                }
            """.trimIndent()

        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Ignore(TRANSITIVE_DF_DEPENDENCY)
    @Test
    fun `kotlin identifiers generated from csv names`() {
        fun escapeDoubleQuotes(it: Char) = if (it == '"') "\"\"" else it.toString()

        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            val notSupportedChars = setOf('\n', '\r')
            (Char.MIN_VALUE..Char.MAX_VALUE).asSequence()
                .filterNot { it in notSupportedChars }
                .chunked(100) {
                    it.joinToString(separator = "", prefix = "\"", postfix = "\"", transform = ::escapeDoubleQuotes)
                }
                .let {
                    dataFile.writeText(it.joinToString(",") + "\n" + (0 until it.count()).joinToString(","))
                }

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                import org.jetbrains.kotlinx.DataFrame
                import org.jetbrains.kotlinx.read
                import org.jetbrains.kotlinx.typed
                import org.jetbrains.kotlinx.filter
                
                fun main() {
                    val df = DataFrame.read("$dataFile").typed<Schema>()
                }
            """.trimIndent())

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral() 
                    maven(url="https://jitpack.io")
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
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

    @Ignore(TRANSITIVE_DF_DEPENDENCY)
    @Test
    fun `code with preprocessing errors won't compile`() {
        val buildDir = Files.createTempDirectory("test").toFile()
        val buildFile = File(buildDir, "build.gradle.kts")
        val dataFile = File(buildDir, "data.csv")
        dataFile.writeText(TestData.csvSample)
        val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
        val main = File(kotlin, "Main.kt")
        main.writeText(
            """
                import org.jetbrains.kotlinx.DataFrame
                import org.jetbrains.kotlinx.read
                import org.jetbrains.kotlinx.typed
                import org.jetbrains.kotlinx.filter
                
                @org.jetbrains.dataframe.annotations.DataSchema
                interface MySchema<T> {
                    val age: Int
                }
                
                fun main() {
                    val df = DataFrame.read("$dataFile").typed<MySchema>()
                    val df1 = df.filter { age != null }
                }
            """.trimIndent()
        )
        buildFile.writeText(
            (@Suppress("DuplicatedCode")
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral() 
                    maven(url="https://jitpack.io")
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
                }
                
                kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")
            """.trimIndent())
        )
        val result = gradleRunner(buildDir, ":build").buildAndFail()
        result.task(":kspKotlin")?.outcome shouldBe TaskOutcome.FAILED
    }

    @Ignore(TRANSITIVE_DF_DEPENDENCY)
    @Test
    fun `preprocessor generates extensions for DataSchema`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                import org.jetbrains.kotlinx.DataFrame
                import org.jetbrains.kotlinx.read
                import org.jetbrains.kotlinx.typed
                import org.jetbrains.kotlinx.filter
                
                @org.jetbrains.dataframe.annotations.DataSchema
                interface MySchema {
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
                    kotlin("jvm") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral()
                    maven(url="https://jitpack.io")
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
                }
                
                kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `generated code compiles in explicit api mode`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, TestData.csvName)
            dataFile.writeText(TestData.csvSample)

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenCentral() 
                    mavenLocal()
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
                }
                
                kotlin {
                    explicitApi()
                }
                
                dataframes {
                    schema {
                        data = file("${TestData.csvName}")
                    }
                }
            """.trimIndent()
        }
        result.task(":generateDataFrameData")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin doesn't break multiplatform build without JVM`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, TestData.csvName)
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
                    kotlin("multiplatform") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenCentral()
                    mavenLocal()
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
                        data = file("${TestData.csvName}")
                        name = "Schema"
                        packageName = ""
                        src = buildDir
                    }
                }
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `plugin doesn't break multiplatform build with JVM`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, TestData.csvName)
            val kotlin = File(buildDir, "src/jvmMain/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                fun main() {
                    println("Hello, Kotlin/JVM!")
                }
            """.trimIndent())
            dataFile.writeText(TestData.csvSample)
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("multiplatform") version "$KOTLIN_VERSION"
                    id("org.jetbrains.kotlin.plugin.dataframe")
                }
                
                repositories {
                    mavenCentral()
                    mavenLocal()
                }
                
                kotlin {
                    sourceSets {
                        jvm()
                    }
                }
                
                dataframes {
                    schema {
                        data = file("${TestData.csvName}")
                        name = "Schema"
                        packageName = ""
                        src = buildDir
                    }
                }
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }
}
