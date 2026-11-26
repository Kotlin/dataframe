package org.jetbrains.kotlinx.dataframe.io.testcontainers

import org.jetbrains.kotlinx.dataframe.io.PostgresConnectionUrlTestBase
import org.junit.AfterClass
import org.junit.BeforeClass
import org.testcontainers.postgresql.PostgreSQLContainer

private const val USER_NAME = "postgres"
private const val PASSWORD = "pass"
private const val DATABASE_NAME = "test"

class PostgresConnectionUrlContainerTest : PostgresConnectionUrlTestBase() {
    override val baseUrl: String
        get() = "jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/$DATABASE_NAME"

    override val userName: String get() = USER_NAME

    override val password: String get() = PASSWORD

    companion object {
        private val postgres: PostgreSQLContainer = PostgreSQLContainer(BuildConfig.POSTGRES_IMAGE).apply {
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
    }
}
