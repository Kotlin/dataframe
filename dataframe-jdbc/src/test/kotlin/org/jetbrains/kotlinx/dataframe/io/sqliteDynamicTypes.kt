package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.type
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.typeOf

/**
 * TODO:
 * Xerial SQLite JDBC driver seems to give identical metadata for `Int?` and `Long?` columns,
 * so we have to solve it #1747.
 */
class SqliteTestDynamicTypes {

    companion object {
        private lateinit var connection: Connection

        private val dbUrl =
            "jdbc:sqlite:${(this::class as Any).javaClass.classLoader
                .getResource("simple_int_long_nullable.sqlite").path}"

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

    private val df = DataFrame.readSqlTable(connection, "numbers")

    @Ignore
    @Test
    fun `INTEGER column with big values should be read as Long`() {
        df["int_col"].type shouldBe typeOf<Int?>()
        // Fails! #1747
        df["long_col"].type shouldBe typeOf<Long?>()
    }
}
