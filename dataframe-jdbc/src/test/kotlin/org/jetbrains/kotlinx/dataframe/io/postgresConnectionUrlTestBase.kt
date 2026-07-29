package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.junit.Test
import org.postgresql.util.PSQLException
import java.sql.DriverManager

private const val URL_PARAMS = "connectTimeout=10&tcpKeepAlive=true"

private const val TABLE_NAME = "table1"

abstract class PostgresConnectionUrlTestBase {
    protected abstract val baseUrl: String

    protected abstract val userName: String

    protected abstract val password: String

    private val urlWithLoginPassword: String
        get() = "$baseUrl?user=$userName&password=$password&$URL_PARAMS"
    private val urlNoLoginPassword: String get() = "$baseUrl?$URL_PARAMS"
    private val urlWithPassword: String get() = "$baseUrl?password=$password&$URL_PARAMS"
    private val urlWithLogin: String get() = "$baseUrl?user=$userName&$URL_PARAMS"

    @Test
    fun `read from table with login and password in connection URL`() {
        DriverManager.getConnection(urlWithLoginPassword).use { connection ->
            createPostgresTestData(connection)

            val df1 = DataFrame.readSqlTable(connection, TABLE_NAME).cast<Table1Postgres>()
            val result1 = df1.filter { "id"<Int>() == 1 }

            result1[0][2] shouldBe 11

            val df2 = connection.readDataFrame(TABLE_NAME).cast<Table1Postgres>()
            val result2 = df2.filter { "id"<Int>() == 1 }

            result2[0][2] shouldBe 11

            clearPostgresTestData(connection)
        }
    }

    @Test
    fun `read from table with login and password in connection URL for DBConfig`() {
        DriverManager.getConnection(urlWithLoginPassword).use { connection ->
            createPostgresTestData(connection)

            val dbConfig = DbConnectionConfig(urlWithLoginPassword)
            val df1 = DataFrame.readSqlTable(dbConfig = dbConfig, TABLE_NAME).cast<Table1Postgres>()
            val result1 = df1.filter { "id"<Int>() == 1 }

            result1[0][2] shouldBe 11

            val df2 = dbConfig.readDataFrame(TABLE_NAME).cast<Table1Postgres>()
            val result2 = df2.filter { "id"<Int>() == 1 }

            result2[0][2] shouldBe 11

            clearPostgresTestData(connection)
        }
    }

    @Test
    fun `read from table without login and password`() {
        val dbConfig = DbConnectionConfig(urlNoLoginPassword)

        shouldThrow<PSQLException> {
            testReadFromTable(dbConfig)
        }
    }

    @Test
    fun `read from table with password only`() {
        val dbConfig = DbConnectionConfig(urlWithPassword)

        shouldThrow<PSQLException> {
            testReadFromTable(dbConfig)
        }
    }

    @Test
    fun `read from table with login only`() {
        val dbConfig = DbConnectionConfig(urlWithLogin)

        shouldThrow<PSQLException> {
            testReadFromTable(dbConfig)
        }
    }

    private fun testReadFromTable(dbConfig: DbConnectionConfig) {
        DriverManager.getConnection(urlWithLoginPassword).use { connection ->
            createPostgresTestData(connection)

            val df2 = dbConfig.readDataFrame(TABLE_NAME).cast<Table1Postgres>()
            val result2 = df2.filter { "id"<Int>() == 1 }

            result2[0][2] shouldBe 11

            clearPostgresTestData(connection)
        }
    }
}
