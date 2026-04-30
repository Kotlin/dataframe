@file:Suppress("UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.io

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
import org.jetbrains.kotlinx.dataframe.io.readAllSqlTables
import org.jetbrains.kotlinx.dataframe.io.readDataFrame
import org.jetbrains.kotlinx.dataframe.io.readDataFrameSchema
import org.jetbrains.kotlinx.dataframe.io.readResultSet
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.junit.Ignore
import org.junit.Test
import java.sql.DriverManager
import java.sql.ResultSet
import javax.sql.rowset.RowSetProvider

class ReadSQLDatabases {

    @Ignore
    @Test
    fun readSqlTablePostgreSql() {
        // SampleStart
        val url = "jdbc:postgresql://localhost:5432/testDatabase"
        val username = "postgres"
        val password = "password"

        val dbConfig = DbConnectionConfig(url, username, password)

        val tableName = "Customer"

        val df = DataFrame.readSqlTable(dbConfig, tableName, 100)

        df.print()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlTableDbConfig() {
        // SampleStart
        val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

        val users = DataFrame.readSqlTable(dbConfig, "Users")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlTableDbConfigLimit() {
        val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")
        // SampleStart
        val users = DataFrame.readSqlTable(dbConfig, "Users", limit = 100)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlTableConnection() {
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val users = DataFrame.readSqlTable(connection, "Users")

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlTableDataSource() {
        // SampleStart
        val config = HikariConfig().apply {
            jdbcUrl = "URL_TO_CONNECT_DATABASE"
            username = "USERNAME"
            password = "PASSWORD"
            maximumPoolSize = 10
            minimumIdle = 2
        }
        val dataSource = HikariDataSource(config)

        val users = DataFrame.readSqlTable(dataSource, "Users")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlTableExtension() {
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val users = connection.readDataFrame("Users", 100)

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQueryDbConfig() {
        // SampleStart
        val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

        val df = DataFrame.readSqlQuery(dbConfig, "SELECT * FROM Users WHERE age > 35")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQueryConnection() {
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val df = DataFrame.readSqlQuery(connection, "SELECT * FROM Users WHERE age > 35")

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQueryDataSource() {
        // SampleStart
        val config = HikariConfig().apply {
            jdbcUrl = "URL_TO_CONNECT_DATABASE"
            username = "USERNAME"
            password = "PASSWORD"
            maximumPoolSize = 10
            minimumIdle = 2
        }
        val dataSource = HikariDataSource(config)

        val df = DataFrame.readSqlQuery(dataSource, "SELECT * FROM Users WHERE age > 35")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQueryExtension() {
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val df = connection.readDataFrame("SELECT * FROM Users WHERE age > 35")

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readResultSetDbType() {
        val resultSet: ResultSet = RowSetProvider.newFactory().createCachedRowSet()
        // SampleStart
        // org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
        val df = DataFrame.readResultSet(resultSet, PostgreSql)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readResultSetConnection() {
        val resultSet: ResultSet = RowSetProvider.newFactory().createCachedRowSet()
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val df = DataFrame.readResultSet(resultSet, connection)

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readResultSetExtension() {
        val rs: ResultSet = RowSetProvider.newFactory().createCachedRowSet()
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val df = rs.readDataFrame(connection, 10)

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readAllSqlTablesDbConfig() {
        // SampleStart
        val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

        val dataframes = DataFrame.readAllSqlTables(dbConfig)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readAllSqlTablesConnection() {
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val dataframes = DataFrame.readAllSqlTables(connection)

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readAllSqlTablesDataSource() {
        // SampleStart
        val config = HikariConfig().apply {
            jdbcUrl = "URL_TO_CONNECT_DATABASE"
            username = "USERNAME"
            password = "PASSWORD"
            maximumPoolSize = 10
            minimumIdle = 2
        }
        val dataSource = HikariDataSource(config)

        val dataframes = DataFrame.readAllSqlTables(dataSource)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlTableSchemaDbConfig() {
        // SampleStart
        val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

        val schema = DataFrameSchema.readSqlTable(dbConfig, "Users")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlTableSchemaConnection() {
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val schema = DataFrameSchema.readSqlTable(connection, "Users")

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlTableSchemaDataSource() {
        // SampleStart
        val config = HikariConfig().apply {
            jdbcUrl = "URL_TO_CONNECT_DATABASE"
            username = "USERNAME"
            password = "PASSWORD"
            maximumPoolSize = 10
            minimumIdle = 2
        }
        val dataSource = HikariDataSource(config)

        val schema = DataFrameSchema.readSqlTable(dataSource, "Users")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQuerySchemaDbConfig() {
        // SampleStart
        val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

        val schema = DataFrameSchema.readSqlQuery(dbConfig, "SELECT * FROM Users WHERE age > 35")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQuerySchemaConnection() {
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val schema = DataFrameSchema.readSqlQuery(connection, "SELECT * FROM Users WHERE age > 35")

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQuerySchemaDataSource() {
        // SampleStart
        val config = HikariConfig().apply {
            jdbcUrl = "URL_TO_CONNECT_DATABASE"
            username = "USERNAME"
            password = "PASSWORD"
            maximumPoolSize = 10
            minimumIdle = 2
        }
        val dataSource = HikariDataSource(config)

        val schema = DataFrameSchema.readSqlQuery(dataSource, "SELECT * FROM Users WHERE age > 35")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQuerySchemaExtensionConnection() {
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val schema = connection.readDataFrameSchema("SELECT * FROM Users WHERE age > 35")

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQuerySchemaExtensionDbConfig() {
        // SampleStart
        val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

        val schema = dbConfig.readDataFrameSchema("SELECT * FROM Users WHERE age > 35")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlQuerySchemaExtensionDataSource() {
        // SampleStart
        val config = HikariConfig().apply {
            jdbcUrl = "URL_TO_CONNECT_DATABASE"
            username = "USERNAME"
            password = "PASSWORD"
            maximumPoolSize = 10
            minimumIdle = 2
        }
        val dataSource = HikariDataSource(config)

        val schema = dataSource.readDataFrameSchema("SELECT * FROM Users WHERE age > 35")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readResultSetSchemaDbType() {
        val resultSet: ResultSet = RowSetProvider.newFactory().createCachedRowSet()
        // SampleStart
        // org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
        val schema = DataFrameSchema.readResultSet(resultSet, PostgreSql)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readResultSetSchemaExtension() {
        val resultSet: ResultSet = RowSetProvider.newFactory().createCachedRowSet()
        // SampleStart
        // org.jetbrains.kotlinx.dataframe.io.db.PostgreSql
        val schema = resultSet.readDataFrameSchema(PostgreSql)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readAllSqlTablesSchemaDbConfig() {
        // SampleStart
        val dbConfig = DbConnectionConfig("URL_TO_CONNECT_DATABASE", "USERNAME", "PASSWORD")

        val schemas = DataFrameSchema.readAllSqlTables(dbConfig)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readAllSqlTablesSchemaConnection() {
        // SampleStart
        val connection = DriverManager.getConnection("URL_TO_CONNECT_DATABASE")

        val schemas = DataFrameSchema.readAllSqlTables(connection)

        connection.close()
        // SampleEnd
    }

    @Ignore
    @Test
    fun readAllSqlTablesSchemaDataSource() {
        // SampleStart
        val config = HikariConfig().apply {
            jdbcUrl = "URL_TO_CONNECT_DATABASE"
            username = "USERNAME"
            password = "PASSWORD"
            maximumPoolSize = 10
            minimumIdle = 2
        }
        val dataSource = HikariDataSource(config)

        val schemas = DataFrameSchema.readAllSqlTables(dataSource)
        // SampleEnd
    }
}
