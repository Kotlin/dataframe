package org.jetbrains.kotlinx.dataframe.samples.guides

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.testcontainers.postgresql.PostgreSQLContainer
import java.net.URI
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

/**
 * The Kotlin half of `pandas-postgres-cases.json`: reading a PostgreSQL table and query results, one test per case.
 * The Pandas half is `pandas-validation/validate_pandas.py`, which runs the same cases on the same table.
 *
 * Run it with `./gradlew :samples:pandasPostgresTest`; the plain `:samples:test` excludes it. It needs a database
 * from one of two sources, and FAILS when it has neither:
 * - `PANDAS_POSTGRES_URL`, for example `postgresql://user:password@127.0.0.1:5432/postgres`: a throwaway PostgreSQL
 *   on this machine. An address that is not local is refused, so that the test is never pointed at someone's real
 *   database by accident; `PANDAS_POSTGRES_ALLOW_REMOTE=1` lifts that.
 * - `PANDAS_POSTGRES_DOCKER=1`: a PostgreSQL started with Testcontainers, as `dataframe-jdbc` does. The image comes
 *   from the system property `pandas.postgres.image`, which the Gradle task sets from the version catalog.
 *
 * In both cases the test creates a schema of its own with a random name, loads `sales-postgres.sql` into it and drops
 * it at the end.
 */
@RunWith(Parameterized::class)
class PandasPostgresCasesTest(private val id: String) {

    @Test
    fun caseHoldsOnPostgres() {
        val case = PandasCases.load(RESOURCE).single { it.id == id }
        PandasCases.failure(case, kotlinSide(database.connection)[id]) shouldBe null
    }

    /** One entry per `id` of the JSON file: the Kotlin DataFrame counterpart of the Pandas expression. */
    private fun kotlinSide(connection: Connection): Map<String, () -> Any?> {
        fun table() = DataFrame.readSqlTable(connection, "sales")

        fun query(sql: String) = DataFrame.readSqlQuery(connection, sql)
        return mapOf(
            "readTableRows" to { table().rowsCount() },
            "readTableColumns" to { table().columnNames() },
            "readTableMissing" to { table()["discount"].toList() },
            "readTableNumberTypes" to {
                table().let { sales ->
                    listOf("year", "revenue", "cost", "discount").map { sales[it].type().toString() }
                }
            },
            "queryFilter" to {
                query("SELECT revenue FROM sales WHERE region = 'North' ORDER BY revenue")["revenue"].toList()
            },
            "queryGroupTotals" to {
                query(
                    "SELECT region, SUM(revenue) AS total FROM sales GROUP BY region ORDER BY region",
                )["total"].toList()
            },
            "queryAverageCost" to {
                query("SELECT AVG(cost) AS average FROM sales GROUP BY region ORDER BY region")["average"].toList()
            },
            "queryAverageType" to {
                query("SELECT AVG(cost) AS average FROM sales")["average"].type().toString()
            },
            "queryCountMissing" to {
                query("SELECT COUNT(*) AS n FROM sales WHERE discount IS NULL")["n"][0]
            },
        )
    }

    companion object {
        private const val RESOURCE = "/pandas-postgres-cases.json"

        private val database = PandasPostgres()

        @JvmStatic
        @Parameters(name = "{0}")
        fun ids(): List<Array<Any>> = PandasCases.load(RESOURCE).map { arrayOf<Any>(it.id) }

        @BeforeClass
        @JvmStatic
        fun startDatabase() = database.start()

        @AfterClass
        @JvmStatic
        fun stopDatabase() = database.stop()
    }
}

/** The database of [PandasPostgresCasesTest]: connects to the address in the environment or starts a container. */
private class PandasPostgres {

    private var container: PostgreSQLContainer? = null
    private var schema: String? = null

    lateinit var connection: Connection
        private set

    fun start() {
        val address = System.getenv("PANDAS_POSTGRES_URL")
        val (url, user, password) = when {
            !address.isNullOrBlank() -> fromAddress(address)
            System.getenv("PANDAS_POSTGRES_DOCKER") == "1" -> fromContainer()
            else -> error("Set PANDAS_POSTGRES_URL to a throwaway local PostgreSQL, or PANDAS_POSTGRES_DOCKER=1.")
        }
        val name = "pandas_guide_" + UUID.randomUUID().toString().take(8)
        schema = name
        val separator = if ('?' in url) "&" else "?"
        connection = DriverManager.getConnection("$url${separator}currentSchema=$name", user, password)
        connection.createStatement().use { it.execute("CREATE SCHEMA $name") }
        val script = PandasPostgres::class.java.getResource("/sales-postgres.sql")!!.readText()
        connection.createStatement().use { it.execute(script) }
    }

    fun stop() {
        if (schema != null && this::connection.isInitialized) {
            connection.createStatement().use { it.execute("DROP SCHEMA $schema CASCADE") }
            connection.close()
        }
        container?.stop()
    }

    private fun fromAddress(address: String): Triple<String, String, String> {
        val uri = URI(address)
        check(uri.host in LOCAL_HOSTS || System.getenv("PANDAS_POSTGRES_ALLOW_REMOTE") == "1") {
            "PANDAS_POSTGRES_URL points at ${uri.host}, which is not local. " +
                "These tests create and drop a schema: use a throwaway local database, " +
                "or set PANDAS_POSTGRES_ALLOW_REMOTE=1 if you mean it."
        }
        val port = if (uri.port == -1) 5432 else uri.port
        return Triple(
            "jdbc:postgresql://${uri.host}:$port${uri.path}",
            uri.userInfo?.substringBefore(":").orEmpty(),
            uri.userInfo?.substringAfter(":", "").orEmpty(),
        )
    }

    private fun fromContainer(): Triple<String, String, String> {
        val image = System.getProperty("pandas.postgres.image")
            ?: error("pandas.postgres.image is not set: run this through :samples:pandasPostgresTest.")
        val postgres = PostgreSQLContainer(image)
        postgres.start()
        container = postgres
        return Triple(postgres.jdbcUrl, postgres.username, postgres.password)
    }

    private companion object {
        val LOCAL_HOSTS = setOf("localhost", "127.0.0.1", "[::1]")
    }
}
