package org.jetbrains.kotlinx.dataframe.samples.guides

import io.kotest.matchers.collections.shouldBeEmpty
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.Test
import java.net.URI
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

/**
 * The Kotlin half of `pandas-postgres-cases.json`: reading a PostgreSQL table and query results. The Pandas half is
 * `pandas-validation/validate_pandas.py`, which runs the same cases on the same table.
 *
 * Needs a throwaway PostgreSQL on this machine and `PANDAS_POSTGRES_URL`, for example
 * `postgresql://user:password@127.0.0.1:5432/postgres`. Run it with `./gradlew :samples:pandasPostgresTest`;
 * the plain `:samples:test` excludes it. Without the variable the test FAILS, it does not skip.
 *
 * The test creates a schema of its own with a random name, loads `sales-postgres.sql` into it and drops it at the end.
 * It refuses an address that is not local, so that it is never pointed at someone's real database by accident;
 * `PANDAS_POSTGRES_ALLOW_REMOTE=1` lifts that.
 */
class PandasPostgresCasesTest {

    private val address: URI = URI(
        System.getenv("PANDAS_POSTGRES_URL") ?: error("PANDAS_POSTGRES_URL is not set"),
    )

    private val localHosts = setOf("localhost", "127.0.0.1", "[::1]")

    private fun jdbcUrl(schema: String): String {
        val port = if (address.port == -1) 5432 else address.port
        return "jdbc:postgresql://${address.host}:$port${address.path}?currentSchema=$schema"
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

    @Test
    fun everyCaseHoldsOnPostgres() {
        check(address.host in localHosts || System.getenv("PANDAS_POSTGRES_ALLOW_REMOTE") == "1") {
            "PANDAS_POSTGRES_URL points at ${address.host}, which is not local. " +
                "These tests create and drop a schema: use a throwaway local database, " +
                "or set PANDAS_POSTGRES_ALLOW_REMOTE=1 if you mean it."
        }
        val schema = "pandas_guide_" + UUID.randomUUID().toString().take(8)
        val user = address.userInfo?.substringBefore(":").orEmpty()
        val password = address.userInfo?.substringAfter(":", "").orEmpty()
        val script = javaClass.getResource("/sales-postgres.sql")!!.readText()

        DriverManager.getConnection(jdbcUrl(schema), user, password).use { connection ->
            connection.createStatement().use { it.execute("CREATE SCHEMA $schema") }
            try {
                connection.createStatement().use { it.execute(script) }
                PandasCases.check("/pandas-postgres-cases.json", kotlinSide(connection)).shouldBeEmpty()
            } finally {
                connection.createStatement().use { it.execute("DROP SCHEMA $schema CASCADE") }
            }
        }
    }
}
