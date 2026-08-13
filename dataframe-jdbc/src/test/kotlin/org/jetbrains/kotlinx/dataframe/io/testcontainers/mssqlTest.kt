package org.jetbrains.kotlinx.dataframe.io.testcontainers

import org.jetbrains.kotlinx.dataframe.io.MsSqlTestBase
import org.jetbrains.kotlinx.dataframe.io.setUpMsSqlTestData
import org.jetbrains.kotlinx.dataframe.io.tearDownMsSqlTestData
import org.junit.AfterClass
import org.junit.BeforeClass
import org.testcontainers.mssqlserver.MSSQLServerContainer
import java.sql.Connection
import java.sql.DriverManager

private const val USER_NAME = "sa"
private const val PASSWORD = "A_Str0ng_Required_Password"

class MsSqlContainerTest : MsSqlTestBase() {
    override val connection: Connection get() = Companion.connection

    override fun connect(database: String?): Connection =
        DriverManager.getConnection(
            if (database == null) rootUrl else "$rootUrl;databaseName=$database",
            USER_NAME,
            PASSWORD,
        )

    companion object {
        private val mssql: MSSQLServerContainer = MSSQLServerContainer(BuildConfig.MSSQL_IMAGE).apply {
            acceptLicense()
            withPassword(PASSWORD)
        }

        private lateinit var connection: Connection

        private val rootUrl: String
            get() = "jdbc:sqlserver://${mssql.host}:${mssql.firstMappedPort};encrypt=true;trustServerCertificate=true"

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            mssql.start()
            connection = DriverManager.getConnection(rootUrl, USER_NAME, PASSWORD)
            setUpMsSqlTestData(connection)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            tearDownMsSqlTestData(connection)
            mssql.stop()
        }
    }
}
