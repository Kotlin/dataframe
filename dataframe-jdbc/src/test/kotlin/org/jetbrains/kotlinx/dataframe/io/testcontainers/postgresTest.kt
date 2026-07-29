package org.jetbrains.kotlinx.dataframe.io.testcontainers

import org.jetbrains.kotlinx.dataframe.io.PostgresTestBase
import org.jetbrains.kotlinx.dataframe.io.createPostgresTestData
import org.jetbrains.kotlinx.dataframe.io.tearDownPostgresTestData
import org.junit.AfterClass
import org.junit.BeforeClass
import org.testcontainers.postgresql.PostgreSQLContainer
import java.sql.Connection
import java.sql.DriverManager

private const val USER_NAME = "postgres"
private const val PASSWORD = "pass"
private const val DATABASE_NAME = "test"

class PostgresContainerTest : PostgresTestBase() {
    override val connection: Connection get() = Companion.connection

    companion object {
        private val postgres: PostgreSQLContainer = PostgreSQLContainer(BuildConfig.POSTGRES_IMAGE).apply {
            withDatabaseName(DATABASE_NAME)
            withUsername(USER_NAME)
            withPassword(PASSWORD)
        }

        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            postgres.start()
            connection = DriverManager.getConnection(postgres.jdbcUrl, USER_NAME, PASSWORD)
            createPostgresTestData(connection)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            tearDownPostgresTestData(connection)
            postgres.stop()
        }
    }
}
