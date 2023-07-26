package org.jetbrains.dataframe.gradle

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class SchemaGeneratorPluginIntegrationTest : AbstractDataFramePluginIntegrationTest() {
    private companion object {
        private const val FIRST_NAME = "first.csv"
        private const val SECOND_NAME = "second.csv"
    }

    @Test
    fun `compileKotlin depends on generateAll task`() {
        val (_, result) = runGradleBuild(":compileKotlin") { buildDir ->
            File(buildDir, FIRST_NAME).also { it.writeText(TestData.csvSample) }
            File(buildDir, SECOND_NAME).also { it.writeText(TestData.csvSample) }
            """
            import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                
            plugins {
                kotlin("jvm") version "$kotlinVersion"
                id("org.jetbrains.kotlinx.dataframe")
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
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
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
        File(dir, "build/generated/dataframe").walkBottomUp().toList().asClue {
            File(dir, "build/generated/dataframe/main/kotlin/dataframe/Data.Generated.kt").exists() shouldBe true
        }
    }

    @Test
    fun `fallback all properties to conventions`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, TestData.csvName)
            dataFile.writeText(TestData.csvSample)

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
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

    @Test
    fun `generated schemas resolved`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                import org.example.Schema
            """.trimIndent())

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
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
                        data = file("${TestData.csvName}")
                        name = "org.example.Schema"
                    }
                }
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `generated schemas resolved in jvmMain source set for multiplatform project`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            val kotlin = File(buildDir, "src/jvmMain/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                import org.example.Schema
            """.trimIndent())
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("multiplatform") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
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
                        name = "org.example.Schema"
                    }
                }
            """.trimIndent()

        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Ignore
    @Test
    fun `kotlin identifiers generated from csv names`() {
        fun escapeDoubleQuotes(it: Char) = if (it == '"') "\"\"" else it.toString()

        val (_, result) = runGradleBuild(":build") { buildDir ->
            val filename = "data.csv"
            val dataFile = File(buildDir, filename)
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
                import org.jetbrains.kotlinx.dataframe.DataFrame
                import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
                import org.jetbrains.kotlinx.dataframe.io.read
                import org.jetbrains.kotlinx.dataframe.api.cast
                import org.jetbrains.kotlinx.dataframe.api.filter
                
                fun main() {
                        val df = DataFrame.read("${TestData.csvName}").cast<Schema>()
                }
            """.trimIndent())

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral() 
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
                }
                
                kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")

                dataframes {
                    schema {
                        data = "${TestData.csvName}"
                        name = "Schema"
                        packageName = ""
                    }
                }
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    fun `preprocessor generates extensions for DataSchema`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                import org.jetbrains.kotlinx.dataframe.DataFrame
                import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
                import org.jetbrains.kotlinx.dataframe.io.read
                import org.jetbrains.kotlinx.dataframe.api.cast
                import org.jetbrains.kotlinx.dataframe.api.filter
                
                @DataSchema
                interface MySchema {
                    val age: Int
                }
                
                fun main() {
                    val df = DataFrame.read("${TestData.csvName}").cast<MySchema>()
                    val df1 = df.filter { age != null }
                }
            """.trimIndent())

            @Suppress("DuplicatedCode")
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral()
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
    fun `preprocessor imports schema from local file`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, "data.csv")
            dataFile.writeText(TestData.csvSample)

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                @file:ImportDataSchema(name = "MySchema", path = "${TestData.csvName}")
                
                package test
                
                import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                import org.jetbrains.kotlinx.dataframe.api.filter
                
                fun main() {
                    val df = MySchema.readCSV()
                    val df1 = df.filter { age != null }
                }
            """.trimIndent())

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral()
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
    fun `preprocessor imports schema from database`() {
        val connectionUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false"
        //val connection = DriverManager.getConnection(connectionUrl)

        val (_, result) = runGradleBuild(":build") { buildDir ->
           // createTestDatabase(connection)

            val kotlin = File(buildDir, "src/main/kotlin").also { it.mkdirs() }
            val main = File(kotlin, "Main.kt")
            main.writeText("""
                @file:ImportDataSchema(name = "Customer", path = "$connectionUrl")
                
                package test
                
                import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                import org.jetbrains.kotlinx.dataframe.api.filter
                import java.sql.Connection
                import java.sql.DriverManager
                import java.sql.SQLException
                
                fun main() {    
                    DriverManager.getConnection("$connectionUrl").use { connection ->
                        val df = DataFrame.readFromDB(connection, "fakeCatalogue", "Customer").cast<Customer>()
                        val df1 = df.filter { age > 35 }
                    }
                }
            """.trimIndent()) // TODO: this snippet should be correct

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral()
                }
                
                dependencies {
                    implementation(files("$dataframeJarPath"))
                }
                
                kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS

        // TODO: think about best place to close resources
        /*try {
            connection.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }*/
    }

    private fun createTestDatabase(connection: Connection) {
        // Crate table Customer
        connection.createStatement().execute(
            """
                    CREATE TABLE Customer (
                        id INT PRIMARY KEY,
                        name VARCHAR(50),
                        age INT
                    )
                """.trimIndent()
        )

        // Create table Sale
        connection.createStatement().execute(
            """
                    CREATE TABLE Sale (
                        id INT PRIMARY KEY,
                        customerId INT,
                        amount DECIMAL(10, 2)
                    )
                """.trimIndent()
        )

        // add data to the Customer table
        connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (1, 'John', 40)")
        connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (2, 'Alice', 25)")
        connection.createStatement().execute("INSERT INTO Customer (id, name, age) VALUES (3, 'Bob', 47)")

        // add data to the Sale table
        connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (1, 1, 100.50)")
        connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (2, 2, 50.00)")
        connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (3, 1, 75.25)")
        connection.createStatement().execute("INSERT INTO Sale (id, customerId, amount) VALUES (4, 3, 35.15)")
    }

    @Test
    fun `generated code compiles in explicit api mode`() {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, TestData.csvName)
            dataFile.writeText(TestData.csvSample)

            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                    
                plugins {
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
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
                    kotlin("multiplatform") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
                }
                
                repositories {
                    mavenCentral()
                    mavenLocal()
                }
                
                kotlin {
                    sourceSets {
                        js(IR) {
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
                    kotlin("multiplatform") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
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

    @Test
    fun `companion object for csv compiles`() {
        testCompanionObject(TestData.csvName, TestData.csvSample)
    }

    @Test
    fun `companion object for json compiles`() {
        testCompanionObject(TestData.jsonName, TestData.jsonSample)
    }

    private fun testCompanionObject(dataName: String, dataSample: String) {
        val (_, result) = runGradleBuild(":build") { buildDir ->
            val dataFile = File(buildDir, dataName)
            dataFile.writeText(dataSample)
            """
                import org.jetbrains.dataframe.gradle.SchemaGeneratorExtension    
                            
                plugins {
                    kotlin("jvm") version "$kotlinVersion"
                    id("org.jetbrains.kotlinx.dataframe")
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
                        data = file("${dataName}")
                        name = "Schema"
                    }
                }
            """.trimIndent()
        }
        result.task(":build")?.outcome shouldBe TaskOutcome.SUCCESS
    }
}
