package org.jetbrains.kotlinx.dataframe.io.local

import org.jetbrains.kotlinx.dataframe.io.MsSqlTestBase
import org.jetbrains.kotlinx.dataframe.io.setUpMsSqlTestData
import org.jetbrains.kotlinx.dataframe.io.tearDownMsSqlTestData
import org.junit.AfterClass
import org.junit.BeforeClass
import java.sql.Connection
import java.sql.DriverManager

private const val URL = "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true"
private const val USER_NAME = "root"
private const val PASSWORD = "pass"

class MsSqlLocalTest : MsSqlTestBase() {
    override val connection: Connection get() = Companion.connection

    override fun connect(database: String?): Connection =
        DriverManager.getConnection(
            if (database == null) URL else "$URL;databaseName=$database",
            USER_NAME,
            PASSWORD,
        )

    companion object {
        private lateinit var connection: Connection

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)
            setUpMsSqlTestData(connection)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            tearDownMsSqlTestData(connection)
        }
    }
}
