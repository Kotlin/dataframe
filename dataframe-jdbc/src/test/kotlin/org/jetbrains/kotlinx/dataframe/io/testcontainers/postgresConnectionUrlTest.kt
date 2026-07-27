package org.jetbrains.kotlinx.dataframe.io.testcontainers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.readDataFrame
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.postgresql.util.PSQLException
import org.testcontainers.postgresql.PostgreSQLContainer
import java.sql.DriverManager

private const val USER_NAME = "postgres"
private const val PASSWORD = "pass"
private const val DATABASE_NAME = "test"
private const val URL_PARAMS = "connectTimeout=10&tcpKeepAlive=true"

private const val TABLE_NAME = "table1"

class PostgresConnectionUrlTest {
    companion object {
        private val postgres: PostgreSQLContainer = PostgreSQLContainer(POSTGRES_IMAGE).apply {
            withDatabaseName(DATABASE_NAME)
            withUsername(USER_NAME)
            withPassword(PASSWORD)
        }

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            postgres.start()
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            postgres.stop()
        }

        private val baseUrl: String
            get() = "jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/$DATABASE_NAME"
        private val urlWithLoginPassword: String
            get() = "$baseUrl?user=$USER_NAME&password=$PASSWORD&$URL_PARAMS"
        private val urlNoLoginPassword: String get() = "$baseUrl?$URL_PARAMS"
        private val urlWithPassword: String get() = "$baseUrl?password=$PASSWORD&$URL_PARAMS"
        private val urlWithLogin: String get() = "$baseUrl?user=$USER_NAME&$URL_PARAMS"
    }

    @Test
    fun `read from table with login and password in connection URL`() {
        DriverManager.getConnection(urlWithLoginPassword).use { connection ->
            createTestData(connection)

            val df1 = DataFrame.readSqlTable(connection, TABLE_NAME).cast<Table1>()
            val result1 = df1.filter { "id"<Int>() == 1 }

            result1[0][2] shouldBe 11

            val df2 = connection.readDataFrame(TABLE_NAME).cast<Table1>()
            val result2 = df2.filter { "id"<Int>() == 1 }

            result2[0][2] shouldBe 11

            clearTestData(connection)
        }
    }

    @Test
    fun `read from table with login and password in connection URL for DBConfig`() {
        DriverManager.getConnection(urlWithLoginPassword).use { connection ->
            createTestData(connection)

            val dbConfig = DbConnectionConfig(urlWithLoginPassword)
            val df1 = DataFrame.readSqlTable(dbConfig = dbConfig, TABLE_NAME).cast<Table1>()
            val result1 = df1.filter { "id"<Int>() == 1 }

            result1[0][2] shouldBe 11

            val df2 = dbConfig.readDataFrame(TABLE_NAME).cast<Table1>()
            val result2 = df2.filter { "id"<Int>() == 1 }

            result2[0][2] shouldBe 11

            clearTestData(connection)
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
            createTestData(connection)

            val df2 = dbConfig.readDataFrame(TABLE_NAME).cast<Table1>()
            val result2 = df2.filter { "id"<Int>() == 1 }

            result2[0][2] shouldBe 11

            clearTestData(connection)
        }
    }
}
