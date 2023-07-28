package org.jetbrains.dataframe.ksp

import com.tschuchort.compiletesting.SourceFile
import io.kotest.assertions.asClue
import io.kotest.inspectors.forAtLeastOne
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import org.jetbrains.dataframe.ksp.runner.KotlinCompileTestingCompilationResult
import org.jetbrains.dataframe.ksp.runner.KspCompilationTestRunner
import org.jetbrains.dataframe.ksp.runner.TestCompilationParameters
import org.junit.After
import org.junit.Before
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.test.Test

const val connectionUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false"

@Suppress("unused")
class DataFrameJdbcSymbolProcessorTest {

    lateinit var connection: Connection

    companion object {
        val imports = """
            import org.jetbrains.kotlinx.dataframe.annotations.*
            import org.jetbrains.kotlinx.dataframe.columns.*
            import org.jetbrains.kotlinx.dataframe.* 
        """.trimIndent()

        const val generatedFile = "HelloJdbc${'$'}Extensions.kt"
    }

    @Before
    fun setup() {
        KspCompilationTestRunner.compilationDir.deleteRecursively()

        connection = DriverManager.getConnection(connectionUrl)
        createTestDatabase(connection)
    }

    @After
    fun close() {
        try {
            connection.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private val jetbrainsCsv = File("../../data/jetbrains repositories.csv")

    @Test
    fun `imported schema resolved`() {
        useHostedFile(jetbrainsCsv) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                @file:ImportDataSchema(
                    "Schema", 
                    "$it",
                    
                )
                package org.example
                import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema

                fun resolve() = Schema.readCSV()
                            """.trimIndent()
                        )
                    )
                )
            )
            result.successfulCompilation shouldBe true
        }
    }

    @Test
    fun `io error on schema import`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                @file:ImportDataSchema(
                    "Schema", 
                    "123",
                )
                package org.example
                import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                        """.trimIndent()
                    )
                )
            )
        )
        result.successfulCompilation shouldBe false
    }

    @Test
    fun `normalization disabled`() {
        useHostedFile(jetbrainsCsv) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                @file:ImportDataSchema(
                    "Schema", 
                    "$it",
                    normalizationDelimiters = []
                )
                package org.example
                import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                            """.trimIndent()
                        )
                    )
                )
            )
            println(result.kspGeneratedFiles)
            result.inspectLines("Schema.Generated.kt") {
                it.forAtLeastOne { it shouldContain "full_name" }
            }
        }
    }

    @Test
    fun `normalization enabled`() {
        useHostedFile(jetbrainsCsv) {
            val result = KspCompilationTestRunner.compile(
                TestCompilationParameters(
                    sources = listOf(
                        SourceFile.kotlin(
                            "MySources.kt",
                            """
                @file:ImportDataSchema(
                    "Schema", 
                    "$it",
                )
                package org.example
                import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
                import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                            """.trimIndent()
                        )
                    )
                )
            )
            println(result.kspGeneratedFiles)
            result.inspectLines("Schema.Generated.kt") {
                it.forAtLeastOne { it shouldContain "fullName" }
            }
        }
    }

    @Test
    fun `jdbc schema resolved`() {
        val result = KspCompilationTestRunner.compile(
            TestCompilationParameters(
                sources = listOf(
                    SourceFile.kotlin(
                        "MySources.kt",
                        """
                @file:ImportDataSchema(name = "Customer", path = "$connectionUrl")
                
                package test
                
                import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
                import org.jetbrains.kotlinx.dataframe.api.filter
                import org.jetbrains.kotlinx.dataframe.DataFrame
                import org.jetbrains.kotlinx.dataframe.api.cast
                import java.sql.Connection
                import java.sql.DriverManager
                import java.sql.SQLException
                import org.jetbrains.kotlinx.dataframe.io.readFromDB
                
                fun main() {    
                    DriverManager.getConnection("$connectionUrl").use { connection ->
                        val df = DataFrame.readFromDB(connection, "fakeCatalogue", "Customer").cast<Customer>()
                        val df1 = df.filter { age > 35 }
                       // TODO: uncomment if Code Generation will be fully supported
                       // val df2 = Customer.readFromDB(connection, "fakeCatalogue", "Customer")
                       // val df3 = df2.filter { age > 35 }
                    }
                }
            """.trimIndent()
                    )
                )
            )
        )
        result.successfulCompilation shouldBe true
    }

    // TODO: make them internal out of scope
    private fun KotlinCompileTestingCompilationResult.inspectLines(f: (List<String>) -> Unit) {
        inspectLines(generatedFile, f)
    }

    private fun KotlinCompileTestingCompilationResult.inspectLines(filename: String, f: (List<String>) -> Unit) {
        kspGeneratedFiles.single { it.name == filename }.readLines().asClue(f)
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

}
