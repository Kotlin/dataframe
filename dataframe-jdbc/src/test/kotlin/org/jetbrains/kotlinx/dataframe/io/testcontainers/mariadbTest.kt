package org.jetbrains.kotlinx.dataframe.io.testcontainers

import org.jetbrains.kotlinx.dataframe.io.MariadbTestBase
import org.jetbrains.kotlinx.dataframe.io.setUpMariadbTestData
import org.jetbrains.kotlinx.dataframe.io.tearDownMariadbTestData
import org.junit.AfterClass
import org.junit.BeforeClass
import org.testcontainers.mariadb.MariaDBContainer
import java.sql.Connection
import java.sql.DriverManager

private const val USER_NAME = "root"
private const val PASSWORD = "pass"

class MariadbContainerTest : MariadbTestBase() {
    override val connection: Connection get() = Companion.connection

    override fun connect(database: String?): Connection = openConnection(database)

    companion object {
        private val mariadb: MariaDBContainer = MariaDBContainer(BuildConfig.MARIADB_IMAGE).apply {
            withUsername(USER_NAME)
            withPassword(PASSWORD)
        }

        private lateinit var connection: Connection

        private val rootUrl: String
            get() = "jdbc:mariadb://${mariadb.host}:${mariadb.firstMappedPort}"

        private fun openConnection(database: String?): Connection =
            DriverManager.getConnection(
                if (database == null) rootUrl else "$rootUrl/$database",
                USER_NAME,
                PASSWORD,
            )

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            mariadb.start()
            connection = openConnection(null)
            setUpMariadbTestData(connection)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            tearDownMariadbTestData(connection)
            mariadb.stop()
        }
    }
}
