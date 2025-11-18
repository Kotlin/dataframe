package org.jetbrains.kotlinx.dataframe.io

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.db.extractDBTypeFromConnection
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import javax.sql.DataSource
import kotlin.reflect.KType

private val logger = KotlinLogging.logger {}

/**
 * Reads data from an SQL table and converts it into a DataFrame.
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
 * @param [dbConfig] the configuration for the database, including URL, user, and password.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @param [strictValidation] if `true`, the method validates that the provided table name is in a valid format.
 *                           Default is `true` for strict validation.
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return the DataFrame containing the data from the SQL table.
 */
public fun DataFrame.Companion.readSqlTable(
    dbConfig: DbConnectionConfig,
    tableName: String,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    validateLimit(limit)
    return withReadOnlyConnection(dbConfig, dbType) { conn ->
        readSqlTable(conn, tableName, limit, inferNullability, dbType, strictValidation, configureStatement)
    }
}

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [dataSource] the [DataSource] to get a database connection from.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @param [strictValidation] if `true`, the method validates that the provided table name is in a valid format.
 *                           Default is `true` for strict validation.
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return the DataFrame containing the data from the SQL table.
 *
 * @see [DataSource.getConnection]
 */
public fun DataFrame.Companion.readSqlTable(
    dataSource: DataSource,
    tableName: String,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    validateLimit(limit)
    dataSource.connection.use { connection ->
        return readSqlTable(
            connection,
            tableName,
            limit,
            inferNullability,
            dbType,
            strictValidation,
            configureStatement,
        )
    }
}

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [connection] the database connection to read tables from.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [connection].
 * @param [strictValidation] if `true`, the method validates that the provided table name is in a valid format.
 *                           Default is `true` for strict validation.
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return the DataFrame containing the data from the SQL table.
 *
 * @see [DriverManager.getConnection]
 */
public fun DataFrame.Companion.readSqlTable(
    connection: Connection,
    tableName: String,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    validateLimit(limit)
    if (strictValidation) {
        require(isValidTableName(tableName)) {
            "The provided table name '$tableName' is invalid. Please ensure it matches a valid table name in the database schema."
        }
    } else {
        logger.warn { "Strict validation is disabled. Make sure the table name '$tableName' is correct." }
    }

    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    // Build SQL query using DbType
    val sqlQuery = determinedDbType.buildSelectTableQueryWithLimit(tableName, limit)

    return executeQueryAndBuildDataFrame(
        connection,
        sqlQuery,
        determinedDbType,
        configureStatement,
        limit,
        inferNullability,
    )
}

/**
 * Reads a data frame from the specified database using the provided SQL query and configurations.
 *
 * @param [connection] The database connection to be used for executing the query.
 * @param [sqlQuery]  The SQL query string to be executed.
 * @param [determinedDbType]  The type of database being accessed, which determines specific configurations.
 * @param [configureStatement]  A lambda function to configure the prepared statement before execution.
 * @param [limit] the maximum number of rows to retrieve from the table.
 *                `null` (default) means no limit - all available rows will be fetched.
 * @param [inferNullability]  A flag to determine whether to infer nullability for result set fields.
 * @return The data frame constructed from the database query results.
 * @throws [IllegalStateException]  If an error occurs while reading from the database or processing the data.
 */
private fun executeQueryAndBuildDataFrame(
    connection: Connection,
    sqlQuery: String,
    determinedDbType: DbType,
    configureStatement: (PreparedStatement) -> Unit,
    limit: Int?,
    inferNullability: Boolean,
): AnyFrame =
    try {
        connection.prepareStatement(sqlQuery).use { statement ->
            logger.debug { "Connection established successfully (${connection.metaData.databaseProductName})" }
            determinedDbType.configureReadStatement(statement)
            configureStatement(statement)
            logger.debug { "Executing query: $sqlQuery" }
            statement.executeQuery().use { rs ->
                val tableColumns = getTableColumnsMetadata(rs)
                fetchAndConvertDataFromResultSet(tableColumns, rs, determinedDbType, limit, inferNullability)
            }
        }
    } catch (e: java.sql.SQLException) {
        // Provide the same type for all SQLExceptions from JDBC and enrich with additional information
        logger.error(e) { "Database operation failed: $sqlQuery" }
        throw IllegalStateException(
            "Failed to read from database. Query: $sqlQuery, Database: ${determinedDbType.dbTypeInJdbcUrl}",
            e,
        )
    } catch (e: Exception) {
        // Provide the same type for all unexpected errors from JDBC
        logger.error(e) { "Unexpected error: ${e.message}" }
        throw IllegalStateException("Unexpected error while reading from database", e)
    }

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * __NOTE:__ SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
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
 * @param [sqlQuery] the SQL query to execute.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @param [strictValidation] if `true`, the method validates that the provided query is in a valid format.
 *                           Default is `true` for strict validation.
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return the DataFrame containing the result of the SQL query.
 */
public fun DataFrame.Companion.readSqlQuery(
    dbConfig: DbConnectionConfig,
    sqlQuery: String,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    validateLimit(limit)
    return withReadOnlyConnection(dbConfig, dbType) { conn ->
        readSqlQuery(conn, sqlQuery, limit, inferNullability, dbType, strictValidation, configureStatement)
    }
}

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * __NOTE:__ SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 *
 * @param [dataSource] the [DataSource] to obtain a database connection from.
 * @param [sqlQuery] the SQL query to execute.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @param [strictValidation] if `true`, the method validates that the provided query is in a valid format.
 *                           Default is `true` for strict validation.
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return the DataFrame containing the result of the SQL query.
 *
 * @see [DataSource.getConnection]
 */
public fun DataFrame.Companion.readSqlQuery(
    dataSource: DataSource,
    sqlQuery: String,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    validateLimit(limit)
    dataSource.connection.use { connection ->
        return readSqlQuery(connection, sqlQuery, limit, inferNullability, dbType, strictValidation, configureStatement)
    }
}

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * __NOTE:__ SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 *
 * @param [connection] the database connection to execute the SQL query.
 * @param [sqlQuery] the SQL query to execute.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [connection].
 * @param [strictValidation] if `true`, the method validates that the provided query is in a valid format.
 *                           Default is `true` for strict validation.
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return the DataFrame containing the result of the SQL query.
 *
 * @see [DriverManager.getConnection]
 */
public fun DataFrame.Companion.readSqlQuery(
    connection: Connection,
    sqlQuery: String,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    validateLimit(limit)
    if (strictValidation) {
        require(isValidSqlQuery(sqlQuery)) {
            "SQL query should start from SELECT and contain one query for reading data without any manipulation. " +
                "Also it should not contain any separators like `;`."
        }
    } else {
        logger.warn { "Strict validation is disabled. Ensure the SQL query '$sqlQuery' is correct and safe." }
    }

    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    val internalSqlQuery = limit?.let {
        determinedDbType.buildSqlQueryWithLimit(sqlQuery, it)
    } ?: sqlQuery

    return executeQueryAndBuildDataFrame(
        connection,
        internalSqlQuery,
        determinedDbType,
        configureStatement,
        limit,
        inferNullability,
    )
}

/**
 * Converts the result of an SQL query or SQL table (by name) to the DataFrame.
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
 * @param [sqlQueryOrTableName] the SQL query to execute or name of the SQL table.
 * It should be a name of one of the existing SQL tables,
 * or the SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [DbConnectionConfig].
 * @param [strictValidation] if `true`, the method validates that the provided query or table name is in a valid format.
 * Default is `true` for strict validation.
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return the DataFrame containing the result of the SQL query.
 */
public fun DbConnectionConfig.readDataFrame(
    sqlQueryOrTableName: String,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    validateLimit(limit)
    return when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.readSqlQuery(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
            dbType,
            strictValidation,
            configureStatement,
        )

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.readSqlTable(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
            dbType,
            strictValidation,
            configureStatement,
        )

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }
}

/**
 * Converts the result of an SQL query or SQL table (by name) to the DataFrame.
 *
 * @param [sqlQueryOrTableName] the SQL query to execute or name of the SQL table.
 * It should be a name of one of the existing SQL tables,
 * or the SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [Connection].
 * @param [strictValidation] if `true`, the method validates that the provided query or table name is in a valid format.
 * Default is `true` for strict validation.
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return the DataFrame containing the result of the SQL query.
 */
public fun Connection.readDataFrame(
    sqlQueryOrTableName: String,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    validateLimit(limit)
    return when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.readSqlQuery(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
            dbType,
            strictValidation,
            configureStatement,
        )

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.readSqlTable(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
            dbType,
            strictValidation,
            configureStatement,
        )

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }
}

/**
 * Converts the result of an SQL query or SQL table (by name) to the DataFrame.
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
 * // Read from a table
 * val customersDF = dataSource.readDataFrame("customers", limit = 100)
 *
 * // Or execute a query
 * val queryDF = dataSource.readDataFrame("SELECT * FROM orders WHERE amount > 100")
 * ```
 *
 * @param [sqlQueryOrTableName] the SQL query to execute or name of the SQL table.
 * It should be a name of one of the existing SQL tables,
 * or the SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [DataSource].
 * @param [strictValidation] if `true`, the method validates that the provided query or table name is in a valid format.
 * Default is `true` for strict validation.
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return the DataFrame containing the result of the SQL query.
 *
 * @see [DataSource.getConnection]
 */
public fun DataSource.readDataFrame(
    sqlQueryOrTableName: String,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    validateLimit(limit)
    connection.use { conn ->
        return when {
            isSqlQuery(sqlQueryOrTableName) -> DataFrame.readSqlQuery(
                conn,
                sqlQueryOrTableName,
                limit,
                inferNullability,
                dbType,
                strictValidation,
                configureStatement,
            )

            isSqlTableName(sqlQueryOrTableName) -> DataFrame.readSqlTable(
                conn,
                sqlQueryOrTableName,
                limit,
                inferNullability,
                dbType,
                strictValidation,
                configureStatement,
            )

            else -> throw IllegalArgumentException(
                "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
            )
        }
    }
}

/**
 * Reads the data from a [ResultSet][java.sql.ResultSet] and converts it into a DataFrame.
 *
 * A [ResultSet][java.sql.ResultSet] object maintains a cursor pointing to its current row of data.
 * By default, a ResultSet object is not updatable and has a cursor that can only move forward.
 * Therefore, you can iterate through it only once, from the first row to the last row.
 *
 * For more details, refer to the official Java documentation on [ResultSet][java.sql.ResultSet].
 *
 * NOTE: Reading from the [ResultSet][java.sql.ResultSet] could potentially change its state.
 *
 * @param [resultSet] the [ResultSet][java.sql.ResultSet] containing the data to read.
 * Its state may be altered after the read operation.
 * @param [dbType] the type of database that the [ResultSet] belongs to.
 * @param [limit] the maximum number of rows to read from the [ResultSet][java.sql.ResultSet].
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * @see [java.sql.ResultSet]
 */
public fun DataFrame.Companion.readResultSet(
    resultSet: ResultSet,
    dbType: DbType,
    limit: Int? = null,
    inferNullability: Boolean = true,
): AnyFrame {
    validateLimit(limit)
    val tableColumns = getTableColumnsMetadata(resultSet)
    return fetchAndConvertDataFromResultSet(tableColumns, resultSet, dbType, limit, inferNullability)
}

/**
 * Reads the data from a [ResultSet][java.sql.ResultSet] and converts it into a DataFrame.
 *
 * A [ResultSet][java.sql.ResultSet] object maintains a cursor pointing to its current row of data.
 * By default, a ResultSet object is not updatable and has a cursor that can only move forward.
 * Therefore, you can iterate through it only once, from the first row to the last row.
 *
 * For more details, refer to the official Java documentation on [ResultSet][java.sql.ResultSet].
 *
 * NOTE: Reading from the [ResultSet][java.sql.ResultSet] could potentially change its state.
 *
 * @param [dbType] the type of database that the [ResultSet] belongs to.
 * @param [limit] the maximum number of rows to read from the [ResultSet][java.sql.ResultSet].
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * @see [java.sql.ResultSet]
 */
public fun ResultSet.readDataFrame(dbType: DbType, limit: Int? = null, inferNullability: Boolean = true): AnyFrame {
    validateLimit(limit)
    return DataFrame.readResultSet(this, dbType, limit, inferNullability)
}

/**
 * Reads the data from a [ResultSet][java.sql.ResultSet] and converts it into a DataFrame.
 *
 * A [ResultSet][java.sql.ResultSet] object maintains a cursor pointing to its current row of data.
 * By default, a ResultSet object is not updatable and has a cursor that can only move forward.
 * Therefore, you can iterate through it only once, from the first row to the last row.
 *
 * For more details, refer to the official Java documentation on [ResultSet][java.sql.ResultSet].
 *
 * __NOTE:__ Reading from the [ResultSet][java.sql.ResultSet] could potentially change its state.
 *
 * @param [resultSet] the [ResultSet][java.sql.ResultSet] containing the data to read.
 * Its state may be altered after the read operation.
 * @param [connection] the connection to the database (it's required to extract the database type)
 * that the [ResultSet] belongs to.
 * @param [limit] the maximum number of rows to read from the [ResultSet][java.sql.ResultSet].
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [resultSet].
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * @see [java.sql.ResultSet]
 */
public fun DataFrame.Companion.readResultSet(
    resultSet: ResultSet,
    connection: Connection,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
): AnyFrame {
    validateLimit(limit)
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    return readResultSet(resultSet, determinedDbType, limit, inferNullability)
}

/**
 * Reads the data from a [ResultSet][java.sql.ResultSet] and converts it into a DataFrame.
 *
 * A [ResultSet][java.sql.ResultSet] object maintains a cursor pointing to its current row of data.
 * By default, a ResultSet object is not updatable and has a cursor that can only move forward.
 * Therefore, you can iterate through it only once, from the first row to the last row.
 *
 * For more details, refer to the official Java documentation on [ResultSet][java.sql.ResultSet].
 *
 * __NOTE:__ Reading from the [ResultSet][java.sql.ResultSet] could potentially change its state.
 *
 * @param [connection] the connection to the database (it's required to extract the database type)
 * that the [ResultSet] belongs to.
 * @param [limit] the maximum number of rows to read from the [ResultSet][java.sql.ResultSet].
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [ResultSet].
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * @see [java.sql.ResultSet]
 */
public fun ResultSet.readDataFrame(
    connection: Connection,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
): AnyFrame {
    validateLimit(limit)
    return DataFrame.readResultSet(this, connection, limit, inferNullability, dbType)
}

/**
 * Reads all non-system tables from a database and returns them
 * as a map of SQL tables and corresponding dataframes using the provided database configuration and limit.
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
 * @param [limit] the maximum number of rows to read from each table.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [catalogue] a name of the catalog from which tables will be retrieved. A null value retrieves tables from all catalogs.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return a map of [String] to [AnyFrame] objects representing the non-system tables from the database.
 */
public fun DataFrame.Companion.readAllSqlTables(
    dbConfig: DbConnectionConfig,
    catalogue: String? = null,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    configureStatement: (PreparedStatement) -> Unit = {},
): Map<String, AnyFrame> {
    validateLimit(limit)
    return withReadOnlyConnection(dbConfig, dbType) { connection ->
        readAllSqlTables(connection, catalogue, limit, inferNullability, dbType, configureStatement)
    }
}

/**
 * Reads all non-system tables from a database and returns them
 * as a map of SQL tables and corresponding dataframes.
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
 * // Read all tables from the database
 * val allTables = DataFrame.readAllSqlTables(dataSource, limit = 100)
 *
 * // Access individual tables
 * val customersDF = allTables["customers"]
 * val ordersDF = allTables["orders"]
 * ```
 *
 * @param [dataSource] the [DataSource] to get a database connection from.
 * @param [catalogue] a name of the catalog from which tables will be retrieved. A null value retrieves tables from all catalogs.
 * @param [limit] the maximum number of rows to read from each table.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return a map of [String] to [AnyFrame] objects representing the non-system tables from the database.
 *
 * @see [DataSource.getConnection]
 */
public fun DataFrame.Companion.readAllSqlTables(
    dataSource: DataSource,
    catalogue: String? = null,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    configureStatement: (PreparedStatement) -> Unit = {},
): Map<String, AnyFrame> {
    validateLimit(limit)
    dataSource.connection.use { connection ->
        return readAllSqlTables(connection, catalogue, limit, inferNullability, dbType, configureStatement)
    }
}

/**
 * Reads all non-system tables from a database and returns them
 * as a map of SQL tables and corresponding dataframes.
 *
 * @param [connection] the database connection to read tables from.
 * @param [limit] the maximum number of rows to read from each table.
 *                `null` (default) means no limit - all available rows will be fetched
 *                or positive integer (e.g., `100`) - fetch at most that many rows
 * @param [catalogue] a name of the catalog from which tables will be retrieved. A null value retrieves tables from all catalogs.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [connection].
 * @param [configureStatement] optional lambda to configure the [PreparedStatement] before execution.
 *                            This allows for custom tuning of fetch size, query timeout, and other JDBC parameters.
 * @return a map of [String] to [AnyFrame] objects representing the non-system tables from the database.
 *
 * @see [DriverManager.getConnection]
 */
public fun DataFrame.Companion.readAllSqlTables(
    connection: Connection,
    catalogue: String? = null,
    limit: Int? = null,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    configureStatement: (PreparedStatement) -> Unit = {},
): Map<String, AnyFrame> {
    validateLimit(limit)
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)
    val metaData = connection.metaData
    val tablesResultSet = retrieveTableMetadata(metaData, catalogue, determinedDbType)

    return buildMap {
        while (tablesResultSet.next()) {
            val tableMetadata = determinedDbType.buildTableMetadata(tablesResultSet)

            // We filter here a second time because of specific logic with SQLite and possible issues with future databases
            if (determinedDbType.isSystemTable(tableMetadata)) {
                continue
            }

            val fullTableName = buildFullTableName(catalogue, tableMetadata.schemaName, tableMetadata.name)

            val dataFrame = readTableAsDataFrame(
                connection,
                fullTableName,
                limit,
                inferNullability,
                dbType,
                configureStatement,
            )

            put(fullTableName, dataFrame)
        }
    }
}

private fun retrieveTableMetadata(metaData: DatabaseMetaData, catalogue: String?, dbType: DbType): ResultSet {
    // Exclude system- and other tables without data (it looks like it is supported badly for many databases)
    val tableTypes = dbType.tableTypes?.toTypedArray()
    return metaData.getTables(catalogue, null, null, tableTypes)
}

private fun buildFullTableName(catalogue: String?, schemaName: String?, tableName: String): String {
    // TODO: both cases is schema specified or not in URL
    // in h2 database name is recognized as a schema name https://www.h2database.com/html/features.html#database_url
    // https://stackoverflow.com/questions/20896935/spring-hibernate-h2-database-schema-not-found
    // could be Dialect/Database specific
    return when {
        catalogue != null && schemaName != null -> "$catalogue.$schemaName.$tableName"
        catalogue != null -> "$catalogue.$tableName"
        else -> tableName
    }
}

private fun readTableAsDataFrame(
    connection: Connection,
    tableName: String,
    limit: Int?,
    inferNullability: Boolean,
    dbType: DbType?,
    configureStatement: (PreparedStatement) -> Unit = {},
): AnyFrame {
    logger.debug { "Reading table: $tableName" }

    val dataFrame = DataFrame.readSqlTable(
        connection,
        tableName,
        limit,
        inferNullability,
        dbType,
        true,
        configureStatement,
    )

    logger.debug { "Finished reading table: $tableName" }

    return dataFrame
}

/**
 * Retrieves the metadata of the columns in the result set.
 *
 * @param rs the result set
 * @return a mutable list of [TableColumnMetadata] objects,
 *         where each TableColumnMetadata object contains information such as the column type,
 *         JDBC type, size, and name.
 */
internal fun getTableColumnsMetadata(rs: ResultSet): MutableList<TableColumnMetadata> {
    val metaData: ResultSetMetaData = rs.metaData
    val numberOfColumns: Int = metaData.columnCount
    val tableColumns = mutableListOf<TableColumnMetadata>()
    val columnNameCounter = mutableMapOf<String, Int>()
    val databaseMetaData: DatabaseMetaData = rs.statement.connection.metaData
    val catalog: String? = rs.statement.connection.catalog.takeUnless { it.isNullOrBlank() }
    val schema: String? = rs.statement.connection.schema.takeUnless { it.isNullOrBlank() }

    for (i in 1 until numberOfColumns + 1) {
        val tableName = metaData.getTableName(i)
        val columnName = metaData.getColumnName(i)

        // this algorithm works correctly only for SQL Table and ResultSet opened on one SQL table
        val columnResultSet: ResultSet =
            databaseMetaData.getColumns(catalog, schema, tableName, columnName)
        val isNullable = if (columnResultSet.next()) {
            columnResultSet.getString("IS_NULLABLE") == "YES"
        } else {
            true // we assume that it's nullable by default
        }

        val name = manageColumnNameDuplication(columnNameCounter, columnName)
        val size = metaData.getColumnDisplaySize(i)
        val type = metaData.getColumnTypeName(i)
        val jdbcType = metaData.getColumnType(i)
        val javaClassName = metaData.getColumnClassName(i)

        tableColumns += TableColumnMetadata(name, type, jdbcType, size, javaClassName, isNullable)
    }
    return tableColumns
}

/**
 * Manages the duplication of column names by appending a unique identifier to the original name if necessary.
 *
 * @param columnNameCounter a mutable map that keeps track of the count for each column name.
 * @param originalName the original name of the column to be managed.
 * @return the modified column name that is free from duplication.
 */
internal fun manageColumnNameDuplication(columnNameCounter: MutableMap<String, Int>, originalName: String): String {
    var name = originalName
    val count = columnNameCounter[originalName]

    if (count != null) {
        var incrementedCount = count + 1
        while (columnNameCounter.containsKey("${originalName}_$incrementedCount")) {
            incrementedCount++
        }
        columnNameCounter[originalName] = incrementedCount
        name = "${originalName}_$incrementedCount"
    } else {
        columnNameCounter[originalName] = 0
    }

    return name
}

/**
 * Fetches and converts data from a ResultSet into a mutable map.
 *
 * @param [tableColumns] a list containing the column metadata for the table.
 * @param [rs] the ResultSet object containing the data to be fetched and converted.
 * @param [dbType] the type of the database.
 * @param [limit] the maximum number of rows to retrieve from the table.
 *                `null` (default) means no limit - all available rows will be fetched.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @return A mutable map containing the fetched and converted data.
 */
internal fun fetchAndConvertDataFromResultSet(
    tableColumns: MutableList<TableColumnMetadata>,
    rs: ResultSet,
    dbType: DbType,
    limit: Int?,
    inferNullability: Boolean,
): AnyFrame {
    val columnKTypes = buildColumnKTypes(tableColumns, dbType)
    val columnData = readAllRowsFromResultSet(rs, tableColumns, columnKTypes, dbType, limit)
    val dataFrame = buildDataFrameFromColumnData(columnData, tableColumns, columnKTypes, dbType, inferNullability)

    logger.debug {
        "DataFrame with ${dataFrame.rowsCount()} rows and ${dataFrame.columnsCount()} columns created as a result of SQL query."
    }

    return dataFrame
}

/**
 * Builds a map of column indices to their Kotlin types.
 */
private fun buildColumnKTypes(tableColumns: List<TableColumnMetadata>, dbType: DbType): Map<Int, KType> =
    tableColumns.indices.associateWith { index ->
        generateKType(dbType, tableColumns[index])
    }

/**
 * Reads all rows from ResultSet and returns a column-oriented data structure.
 * Returns mutable lists to allow efficient post-processing without copying.
 */
private fun readAllRowsFromResultSet(
    rs: ResultSet,
    tableColumns: List<TableColumnMetadata>,
    columnKTypes: Map<Int, KType>,
    dbType: DbType,
    limit: Int?,
): List<MutableList<Any?>> {
    val columnsCount = tableColumns.size
    val columnData = List(columnsCount) { mutableListOf<Any?>() }
    var rowsRead = 0

    while (rs.next() && (limit == null || rowsRead < limit)) {
        repeat(columnsCount) { columnIndex ->
            val value = dbType.extractValueFromResultSet(
                rs = rs,
                columnIndex = columnIndex,
                columnMetadata = tableColumns[columnIndex],
                kType = columnKTypes.getValue(columnIndex),
            )
            columnData[columnIndex].add(value)
        }
        rowsRead++
        // if (rowsRead % 1000 == 0) logger.debug { "Loaded $rowsRead rows." } // TODO: https://github.com/Kotlin/dataframe/issues/455
    }

    return columnData
}

/**
 * Builds DataFrame from column-oriented data.
 * Accepts mutable lists to enable efficient in-place transformations.
 */
private fun buildDataFrameFromColumnData(
    columnData: List<MutableList<Any?>>,
    tableColumns: List<TableColumnMetadata>,
    columnKTypes: Map<Int, KType>,
    dbType: DbType,
    inferNullability: Boolean,
): AnyFrame =
    columnData.mapIndexed { index, values ->
        dbType.buildDataColumn(
            name = tableColumns[index].name,
            values = values,
            kType = columnKTypes.getValue(index),
            inferNullability = inferNullability,
        )
    }.toDataFrame()

/**
 * Generates a KType based on the given database type and table column metadata.
 *
 * @param dbType The database type.
 * @param tableColumnMetadata The table column metadata.
 *
 * @return The generated KType.
 */
internal fun generateKType(dbType: DbType, tableColumnMetadata: TableColumnMetadata): KType =
    dbType.convertSqlTypeToKType(tableColumnMetadata)
        ?: dbType.makeCommonSqlToKTypeMapping(tableColumnMetadata)
