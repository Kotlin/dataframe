package org.jetbrains.dataframe.gradle

import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.Test
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.DriverManager
import kotlin.io.path.absolutePathString
import kotlin.io.path.writeText

class DataFrameReadTest {
    @Test
    fun `file that does not exists`() {
        val temp = Files.createTempDirectory("")
        val definitelyDoesNotExists = temp.resolve("absolutelyRandomName")
        shouldThrow<FileNotFoundException> {
            DataFrame.read(definitelyDoesNotExists)
        }
    }

    @Test
    fun `file with invalid json`() {
        val temp = Files.createTempDirectory("")
        val invalidJson = temp.resolve("test.json").also { it.writeText(".") }
        shouldThrow<IllegalStateException> {
            DataFrame.read(invalidJson)
        }
    }

    @Test
    fun `file with invalid csv`() {
        val temp = Files.createTempDirectory("")
        val invalidCsv = temp.resolve("test.csv").also { it.writeText("") }
        DataFrame.read(invalidCsv).isEmpty() shouldBe true
    }

    @Test
    fun `invalid url`() {
        val exception = shouldThrowAny {
            DataFrame.read("http:://example.com")
        }
        exception.asClue {
            (exception is IllegalArgumentException || exception is IOException) shouldBe true
        }
    }

    @Test
    fun `valid url`() {
        useHostedJson("{}") {
            DataFrame.read(URL(it))
        }
    }

    @Test
    fun `path that is valid url`() {
        useHostedJson("{}") {
            DataFrame.read(it)
        }
    }

    @Test
    fun `URL with invalid JSON`() {
        useHostedJson("<invalid json>") { url ->
            shouldThrow<SerializationException> {
                DataFrame.read(url).also { println(it) }
            }
        }
    }

    @Test
    fun `data accessible and readable`() {
        shouldNotThrowAny {
            DataFrame.readCsv(Paths.get("../../data/jetbrains repositories.csv").absolutePathString(), skipLines = 1)
        }
    }

    @Test
    fun `csvSample is valid csv`() {
        val temp = Files.createTempFile("f", "csv")
        temp.writeText(TestData.csvSample)

        val df = DataFrame.read(temp)
        df.columnNames() shouldBe listOf("name", "age")
    }

    @Test
    fun `jdbcSample is valid jdbc`() {
        DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false")
            .use { connection ->
                // Create table Customer
                connection.createStatement().execute(
                    """
                    CREATE TABLE Customer (
                        id INT PRIMARY KEY,
                        name VARCHAR(50),
                        age INT
                    )
                    """.trimIndent(),
                )

                // Create table Sale
                connection.createStatement().execute(
                    """
                    CREATE TABLE Sale (
                        id INT PRIMARY KEY,
                        customerId INT,
                        amount DECIMAL(10, 2)
                    )
                    """.trimIndent(),
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

                val dfCustomer = DataFrame.readSqlTable(connection, "Customer")
                dfCustomer.columnNames() shouldBe listOf("id", "name", "age")

                val dfSale = DataFrame.readSqlTable(connection, "Sale")
                dfSale.columnNames() shouldBe listOf("id", "customerId", "amount")
            }
    }
}
