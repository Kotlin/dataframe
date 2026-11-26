package org.jetbrains.kotlinx.dataframe.io.testcontainers

import org.jetbrains.kotlinx.dataframe.io.MySqlTestBase
import org.jetbrains.kotlinx.dataframe.io.setUpMySqlTestData
import org.jetbrains.kotlinx.dataframe.io.tearDownMySqlTestData
import org.junit.AfterClass
import org.junit.BeforeClass
import org.testcontainers.mysql.MySQLContainer
import java.sql.Connection
import java.sql.DriverManager

private const val USER_NAME = "root"
private const val PASSWORD = "pass"

class MySqlContainerTest : MySqlTestBase() {
    override val connection: Connection get() = Companion.connection

    override fun connect(database: String?): Connection = openConnection(database)

    companion object {
        private val mysql: MySQLContainer = MySQLContainer(BuildConfig.MYSQL_IMAGE).apply {
            withUsername(USER_NAME)
            withPassword(PASSWORD)
        }

        private lateinit var connection: Connection

        private val rootUrl: String
            get() = "jdbc:mysql://${mysql.host}:${mysql.firstMappedPort}"

        private fun openConnection(database: String?): Connection =
            DriverManager.getConnection(
                if (database == null) rootUrl else "$rootUrl/$database",
                USER_NAME,
                PASSWORD,
            )

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            mysql.start()
            connection = openConnection(null)
            setUpMySqlTestData(connection)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            tearDownMySqlTestData(connection)
            mysql.stop()
        }
    }
}
