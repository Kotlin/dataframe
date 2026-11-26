package org.jetbrains.kotlinx.dataframe.io.local

import org.jetbrains.kotlinx.dataframe.io.PostgresTestBase
import org.jetbrains.kotlinx.dataframe.io.createPostgresTestData
import org.jetbrains.kotlinx.dataframe.io.tearDownPostgresTestData
import org.junit.AfterClass
import org.junit.BeforeClass
import java.sql.Connection
import java.sql.DriverManager

private const val URL = "jdbc:postgresql://localhost:5432/test"
private const val USER_NAME = "postgres"
private const val PASSWORD = "pass"

class PostgresLocalTest : PostgresTestBase() {
    override val connection: Connection get() = Companion.connection

    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)
            createPostgresTestData(connection)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            tearDownPostgresTestData(connection)
        }
    }
}
