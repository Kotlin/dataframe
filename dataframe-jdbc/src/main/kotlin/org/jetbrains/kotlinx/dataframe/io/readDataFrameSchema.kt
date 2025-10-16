package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.extractDBTypeFromConnection
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import javax.sql.DataSource
import kotlin.use

/**
 * Retrieves the schema for an SQL table using the provided database configuration.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [tableName] the name of the SQL table for which to retrieve the schema.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @return the [DataFrameSchema] object representing the schema of the SQL table
 */
public fun DataFrameSchema.Companion.readSqlTable(
    dbConfig: DbConnectionConfig,
    tableName: String,
    dbType: DbType? = null,
): DataFrameSchema =
    withReadOnlyConnection(dbConfig, dbType) { connection ->
        readSqlTable(connection, tableName, dbType)
    }

/**
 * Retrieves the schema for an SQL table using the provided [DataSource].
 *
 * ### Example with HikariCP:
 * ```kotlin
 * import com.zaxxer.hikari.HikariConfig
 * import com.zaxxer.hikari.HikariDataSource
 *
 * val config = HikariConfig().apply {
 *     jdbcUrl = "jdbc:postgresql://localhost:5432/mydb"
 *     username = "user"
 *     password = "password"
 * }
 * val dataSource = HikariDataSource(config)
 *
 * // Get the schema for a specific table
 * val customersSchema = DataFrame.getSchemaForSqlTable(dataSource, "customers")
 *
 * // Inspect the schema
 * println(customersSchema.columns)
 * ```
 *
 * @param [dataSource] the [DataSource] to get a database connection from.
 * @param [tableName] the name of the SQL table for which to retrieve the schema.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @return the schema of the SQL table as a [DataFrameSchema] object.
 * @see [DataSource.getConnection]
 */
public fun DataFrameSchema.Companion.readSqlTable(
    dataSource: DataSource,
    tableName: String,
    dbType: DbType? = null,
): DataFrameSchema {
    dataSource.connection.use { connection ->
        return readSqlTable(connection, tableName, dbType)
    }
}

/**
 * Retrieves the schema for an SQL table using the provided database connection.
 *
 * @param [connection] the database connection.
 * @param [tableName] the name of the SQL table for which to retrieve the schema.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [connection].
 * @return the schema of the SQL table as a [DataFrameSchema] object.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrameSchema.Companion.readSqlTable(
    connection: Connection,
    tableName: String,
    dbType: DbType? = null,
): DataFrameSchema {
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    // Read just 1 row to get the schema
    val singleRowDataFrame = DataFrame.readSqlTable(
        connection = connection,
        tableName = tableName,
        limit = 1,
        inferNullability = false, // Schema extraction doesn't need nullability inference
        dbType = determinedDbType,
        strictValidation = true,
    )

    return singleRowDataFrame.schema()
}

/**
 * Retrieves the schema of an SQL query result using the provided database configuration.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [sqlQuery] the SQL query to execute and retrieve the schema from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 */
public fun DataFrameSchema.Companion.readSqlQuery(
    dbConfig: DbConnectionConfig,
    sqlQuery: String,
    dbType: DbType? = null,
): DataFrameSchema =
    withReadOnlyConnection(dbConfig, dbType) { connection ->
        readSqlQuery(connection, sqlQuery, dbType)
    }

/**
 * Retrieves the schema of an SQL query result using the provided [DataSource].
 *
 * ### Example with HikariCP:
 * ```kotlin
 * import com.zaxxer.hikari.HikariConfig
 * import com.zaxxer.hikari.HikariDataSource
 *
 * val config = HikariConfig().apply {
 *     jdbcUrl = "jdbc:postgresql://localhost:5432/mydb"
 *     username = "user"
 *     password = "password"
 * }
 * val dataSource = HikariDataSource(config)
 *
 * // Get the schema for a SQL query
 * val querySchema = DataFrame.getSchemaForSqlQuery(
 *     dataSource,
 *     "SELECT name, age, city FROM customers WHERE age > 25"
 * )
 *
 * // Inspect the schema
 * println(querySchema.columns)
 * ```
 *
 * @param [dataSource] the [DataSource] to get a database connection from.
 * @param [sqlQuery] the SQL query to execute and retrieve the schema from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 *
 * @see [DataSource.getConnection]
 */
public fun DataFrameSchema.Companion.readSqlQuery(
    dataSource: DataSource,
    sqlQuery: String,
    dbType: DbType? = null,
): DataFrameSchema {
    dataSource.connection.use { connection ->
        return readSqlQuery(connection, sqlQuery, dbType)
    }
}

/**
 * Retrieves the schema of an SQL query result using the provided database connection.
 *
 * @param [connection] the database connection.
 * @param [sqlQuery] the SQL query to execute and retrieve the schema from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [connection].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrameSchema.Companion.readSqlQuery(
    connection: Connection,
    sqlQuery: String,
    dbType: DbType? = null,
): DataFrameSchema {
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    // Read just 1 row to get the schema
    val singleRowDataFrame = DataFrame.readSqlQuery(
        connection = connection,
        sqlQuery = sqlQuery,
        limit = 1,
        inferNullability = false, // Schema extraction doesn't need nullability inference
        dbType = determinedDbType,
        strictValidation = true,
    )

    return singleRowDataFrame.schema()
}

/**
 * Retrieves the schema of an SQL query result or the SQL table using the provided database configuration.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 *
 * @param [sqlQueryOrTableName] the SQL query to execute and retrieve the schema from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [DbConnectionConfig].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 */
public fun DbConnectionConfig.readDataFrameSchema(
    sqlQueryOrTableName: String,
    dbType: DbType? = null,
): DataFrameSchema =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrameSchema.readSqlQuery(this, sqlQueryOrTableName, dbType)

        isSqlTableName(sqlQueryOrTableName) -> DataFrameSchema.readSqlTable(this, sqlQueryOrTableName, dbType)

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }

/**
 * Retrieves the schema of an SQL query result or the SQL table using the provided [DataSource].
 *
 * ### Example with HikariCP:
 * ```kotlin
 * import com.zaxxer.hikari.HikariConfig
 * import com.zaxxer.hikari.HikariDataSource
 *
 * val config = HikariConfig().apply {
 *     jdbcUrl = "jdbc:postgresql://localhost:5432/mydb"
 *     username = "user"
 *     password = "password"
 * }
 * val dataSource = HikariDataSource(config)
 *
 * // Get schema for a table
 * val tableSchema = dataSource.getDataFrameSchema("customers")
 *
 * // Or get schema for a query
 * val querySchema = dataSource.getDataFrameSchema("SELECT name, age FROM customers WHERE age > 25")
 *
 * // Inspect the schema
 * println(tableSchema.columns)
 * ```
 *
 * @param [sqlQueryOrTableName] the SQL query to execute or name of the SQL table.
 * It should be a name of one of the existing SQL tables,
 * or the SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [DataSource].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 *
 * @see [DataSource.getConnection]
 */
public fun DataSource.readDataFrameSchema(sqlQueryOrTableName: String, dbType: DbType? = null): DataFrameSchema {
    connection.use { conn ->
        return when {
            isSqlQuery(sqlQueryOrTableName) -> DataFrameSchema.readSqlQuery(conn, sqlQueryOrTableName, dbType)

            isSqlTableName(sqlQueryOrTableName) -> DataFrameSchema.readSqlTable(conn, sqlQueryOrTableName, dbType)

            else -> throw IllegalArgumentException(
                "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
            )
        }
    }
}

/**
 * Retrieves the schema of an SQL query result or the SQL table using the provided database configuration.
 *
 * @param [sqlQueryOrTableName] the SQL query to execute and retrieve the schema from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [Connection].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 */
public fun Connection.readDataFrameSchema(sqlQueryOrTableName: String, dbType: DbType? = null): DataFrameSchema =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrameSchema.readSqlQuery(this, sqlQueryOrTableName, dbType)

        isSqlTableName(sqlQueryOrTableName) -> DataFrameSchema.readSqlTable(this, sqlQueryOrTableName, dbType)

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }

/**
 * Retrieves the schema from [ResultSet].
 *
 * NOTE: This function will not close connection and result set and not retrieve data from the result set.
 *
 * @param [resultSet] the [ResultSet] obtained from executing a database query.
 * @param [dbType] the type of database that the [ResultSet] belongs to, could be a custom object, provided by user.
 * @return the schema of the [ResultSet] as a [DataFrameSchema] object.
 */
public fun DataFrameSchema.Companion.readResultSet(resultSet: ResultSet, dbType: DbType): DataFrameSchema {
    val tableColumns = getTableColumnsMetadata(resultSet)
    return buildSchemaByTableColumns(tableColumns, dbType)
}

/**
 * Retrieves the schema from [ResultSet].
 *
 * NOTE: This function will not close connection and result set and not retrieve data from the result set.
 *
 * @param [dbType] the type of database that the [ResultSet] belongs to, could be a custom object, provided by user.
 * @return the schema of the [ResultSet] as a [DataFrameSchema] object.
 */
public fun ResultSet.readDataFrameSchema(dbType: DbType): DataFrameSchema = DataFrameSchema.readResultSet(this, dbType)

/**
 * Retrieves the schemas of all non-system tables in the database using the provided database configuration.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @return a map of [String, DataFrameSchema] objects representing the table name and its schema for each non-system table.
 */
public fun DataFrameSchema.Companion.readAllSqlTables(
    dbConfig: DbConnectionConfig,
    dbType: DbType? = null,
): Map<String, DataFrameSchema> =
    withReadOnlyConnection(dbConfig, dbType) { connection ->
        readAllSqlTables(connection, dbType)
    }

/**
 * Retrieves the schemas of all non-system tables in the database using the provided [DataSource].
 *
 * ### Example with HikariCP:
 * ```kotlin
 * import com.zaxxer.hikari.HikariConfig
 * import com.zaxxer.hikari.HikariDataSource
 *
 * val config = HikariConfig().apply {
 *     jdbcUrl = "jdbc:postgresql://localhost:5432/mydb"
 *     username = "user"
 *     password = "password"
 * }
 * val dataSource = HikariDataSource(config)
 *
 * // Get schemas for all tables
 * val allSchemas = DataFrame.getSchemaForAllSqlTables(dataSource)
 *
 * // Access individual table schemas
 * val customersSchema = allSchemas["customers"]
 * val ordersSchema = allSchemas["orders"]
 *
 * // Iterate through all schemas
 * allSchemas.forEach { (tableName, schema) ->
 *     println("Table: \$tableName, Columns: \${schema.columns.keys}")
 * }
 * ```
 *
 * @param [dataSource] the DataSource to get a database connection from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @return a map of [String, DataFrameSchema] objects representing the table name and its schema for each non-system table.
 *
 * @see [DataSource.getConnection]
 */
public fun DataFrameSchema.Companion.readAllSqlTables(
    dataSource: DataSource,
    dbType: DbType? = null,
): Map<String, DataFrameSchema> {
    dataSource.connection.use { connection ->
        return readAllSqlTables(connection, dbType)
    }
}

/**
 * Retrieves the schemas of all non-system tables in the database using the provided database connection.
 *
 * @param [connection] the database connection.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [connection].
 * @return a map of [String, DataFrameSchema] objects representing the table name and its schema for each non-system table.
 */
public fun DataFrameSchema.Companion.readAllSqlTables(
    connection: Connection,
    dbType: DbType? = null,
): Map<String, DataFrameSchema> {
    val metaData = connection.metaData
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    // exclude system- and other tables without data
    val tableTypes = determinedDbType.tableTypes?.toTypedArray()
    val tables = metaData.getTables(null, null, null, tableTypes)

    val dataFrameSchemas = mutableMapOf<String, DataFrameSchema>()

    while (tables.next()) {
        val jdbcTable = determinedDbType.buildTableMetadata(tables)
        if (!determinedDbType.isSystemTable(jdbcTable)) {
            // we filter her a second time because of specific logic with SQLite and possible issues with future databases
            val tableName = jdbcTable.name
            val dataFrameSchema = readSqlTable(connection, tableName, determinedDbType)
            dataFrameSchemas += tableName to dataFrameSchema
        }
    }

    return dataFrameSchemas
}
