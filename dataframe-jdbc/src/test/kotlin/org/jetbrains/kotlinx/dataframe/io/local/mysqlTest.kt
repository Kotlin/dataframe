package org.jetbrains.kotlinx.dataframe.io.local

import org.jetbrains.kotlinx.dataframe.io.MySqlTestBase
import org.jetbrains.kotlinx.dataframe.io.setUpMySqlTestData
import org.jetbrains.kotlinx.dataframe.io.tearDownMySqlTestData
import org.junit.AfterClass
import org.junit.BeforeClass
import java.sql.Connection
import java.sql.DriverManager

private const val URL = "jdbc:mysql://localhost:3307"
private const val USER_NAME = "root"
private const val PASSWORD = "pass"

class MySqlLocalTest : MySqlTestBase() {
    override val connection: Connection get() = Companion.connection

    override fun connect(database: String?): Connection = openConnection(database)

    companion object {
        private lateinit var connection: Connection

        private fun openConnection(database: String?): Connection =
            DriverManager.getConnection(
                if (database == null) URL else "$URL/$database",
                USER_NAME,
                PASSWORD,
            )

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = openConnection(null)
            setUpMySqlTestData(connection)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            tearDownMySqlTestData(connection)
        }
    }
}
