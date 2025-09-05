package org.jetbrains.kotlinx.dataframe.samples.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.Ignore
import org.junit.Test
import java.sql.DriverManager

class DuckDb {

    @Ignore
    @Test
    fun readSqlTable() {
        // SampleStart
        val url = "jdbc:duckdb:/testDatabase"
        val username = "duckdb"
        val password = "password"

        val dbConfig = DbConnectionConfig(url, username, password)

        val tableName = "Customer"

        val df = DataFrame.readSqlTable(dbConfig, tableName)
        // SampleEnd
    }

    // source: https://duckdb.org/docs/stable/core_extensions/iceberg/overview.html
    @Ignore
    @Test
    fun readIcebergExtension() {
        // SampleStart
        // Creating an in-memory DuckDB database
        val connection = DriverManager.getConnection("jdbc:duckdb:")
        val df = connection.use { connection ->
            // install and load Iceberg
            connection.createStatement().execute("INSTALL iceberg; LOAD iceberg;")

            // query a table from Iceberg using a specific SQL query
            DataFrame.readSqlQuery(
                connection = connection,
                sqlQuery = "SELECT * FROM iceberg_scan('data/iceberg/lineitem_iceberg', allow_moved_paths = true);",
            )
        }
        // SampleEnd
    }
}
