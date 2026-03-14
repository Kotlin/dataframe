package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.db.Sqlite
import org.jetbrains.kotlinx.dataframe.type
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.typeOf

class SqliteTestCustomTypes {

    companion object {
        private lateinit var connection: Connection

        private val dbUrl =
            "jdbc:sqlite:${(this::class as Any).javaClass.classLoader
                .getResource("safe_moz_places_sample.sqlite").path}"

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection(dbUrl)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            try {
                connection.close()
            } catch (e: Exception) {
                // Log, but not fail
                println("Warning: Could not clean up test database file: ${e.message}")
            }
        }
    }

    private val sqliteCustomTypes = Sqlite.withCustomTypes(mapOf("LONGVARCHAR" to typeOf<String>()))

    private val df = DataFrame.readSqlTable(connection, "moz_places", dbType = sqliteCustomTypes)

    @Test
    fun `LONGVARCHAR columns should be read as String`() {
        df["url"].type shouldBe typeOf<String>()
        df["title"].type shouldBe typeOf<String?>()

        df["url"][0] shouldBe "https://support.example.org/products/browser"
        df["title"][0] shouldBe null
        df["title"][4] shouldBe "Column selectors | Sample Docs"
    }

}
