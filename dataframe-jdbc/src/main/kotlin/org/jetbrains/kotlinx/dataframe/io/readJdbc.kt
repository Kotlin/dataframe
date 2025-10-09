package org.jetbrains.kotlinx.dataframe.io

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.db.extractDBTypeFromConnection
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.math.BigDecimal
import java.sql.Blob
import java.sql.Clob
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.NClob
import java.sql.Ref
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.RowId
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Date
import java.util.UUID
import javax.sql.DataSource
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.safeCast
import kotlin.reflect.full.starProjectedType

private val logger = KotlinLogging.logger {}

/**
 * The default limit value.
 *
 * This constant represents the default limit value to be used in cases where no specific limit
 * is provided.
 *
 * @see Int.MIN_VALUE
 */
private const val DEFAULT_LIMIT = Int.MIN_VALUE

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [dbConfig] the configuration for the database, including URL, user, and password.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @param [strictValidation] if `true`, the method validates that the provided table name is in a valid format.
 *                           Default is `true` for strict validation.
 * @return the DataFrame containing the data from the SQL table.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 */
public fun DataFrame.Companion.readSqlTable(
    dbConfig: DbConnectionConfig,
    tableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
): AnyFrame =
    withReadOnlyConnection(dbConfig, dbType) { conn ->
        readSqlTable(conn, tableName, limit, inferNullability, dbType, strictValidation)
    }

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [dataSource] the [DataSource] to get a database connection from.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @param [strictValidation] if `true`, the method validates that the provided table name is in a valid format.
 *                           Default is `true` for strict validation.
 * @return the DataFrame containing the data from the SQL table.
 *
 * @see [DataSource.getConnection]
 */
public fun DataFrame.Companion.readSqlTable(
    dataSource: DataSource,
    tableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
): AnyFrame {
    dataSource.connection.use { connection ->
        return readSqlTable(connection, tableName, limit, inferNullability, dbType, strictValidation)
    }
}

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [connection] the database connection to read tables from.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [connection].
 * @param [strictValidation] if `true`, the method validates that the provided table name is in a valid format.
 *                           Default is `true` for strict validation.
 * @return the DataFrame containing the data from the SQL table.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readSqlTable(
    connection: Connection,
    tableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
): AnyFrame {
    if (strictValidation) {
        require(isValidTableName(tableName)) {
            "The provided table name '$tableName' is invalid. Please ensure it matches a valid table name in the database schema."
        }
    } else {
        logger.warn { "Strict validation is disabled. Make sure the table name '$tableName' is correct." }
    }

    val url = connection.metaData.url
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    val selectAllQuery = if (limit > 0) {
        determinedDbType.sqlQueryLimit("SELECT * FROM $tableName", limit)
    } else {
        "SELECT * FROM $tableName"
    }

    connection.prepareStatement(selectAllQuery).use { st ->
        logger.debug { "Connection with url:$url is established successfully." }

        st.executeQuery().use { rs ->
            val tableColumns = getTableColumnsMetadata(rs)
            return fetchAndConvertDataFromResultSet(tableColumns, rs, determinedDbType, limit, inferNullability)
        }
    }
}

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * NOTE: SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [sqlQuery] the SQL query to execute.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @param [strictValidation] if `true`, the method validates that the provided query is in a valid format.
 *                           Default is `true` for strict validation.
 * @return the DataFrame containing the result of the SQL query.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 */

public fun DataFrame.Companion.readSqlQuery(
    dbConfig: DbConnectionConfig,
    sqlQuery: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
): AnyFrame =
    withReadOnlyConnection(dbConfig, dbType) { conn ->
        readSqlQuery(conn, sqlQuery, limit, inferNullability, dbType, strictValidation)
    }

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * NOTE: SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 *
 * @param [dataSource] the [DataSource] to obtain a database connection from.
 * @param [sqlQuery] the SQL query to execute.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @param [strictValidation] if `true`, the method validates that the provided query is in a valid format.
 *                           Default is `true` for strict validation.
 * @return the DataFrame containing the result of the SQL query.
 *
 * @see [DataSource.getConnection]
 */
public fun DataFrame.Companion.readSqlQuery(
    dataSource: DataSource,
    sqlQuery: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
): AnyFrame {
    dataSource.connection.use { connection ->
        return readSqlQuery(connection, sqlQuery, limit, inferNullability, dbType, strictValidation)
    }
}

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * NOTE: SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 *
 * @param [connection] the database connection to execute the SQL query.
 * @param [sqlQuery] the SQL query to execute.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [connection].
 * @param [strictValidation] if `true`, the method validates that the provided query is in a valid format.
 *                           Default is `true` for strict validation.
 * @return the DataFrame containing the result of the SQL query.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readSqlQuery(
    connection: Connection,
    sqlQuery: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
): AnyFrame {
    if (strictValidation) {
        require(isValidSqlQuery(sqlQuery)) {
            "SQL query should start from SELECT and contain one query for reading data without any manipulation. " +
                "Also it should not contain any separators like `;`."
        }
    } else {
        logger.warn { "Strict validation is disabled. Ensure the SQL query '$sqlQuery' is correct and safe." }
    }

    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    val internalSqlQuery = if (limit > 0) determinedDbType.sqlQueryLimit(sqlQuery, limit) else sqlQuery

    logger.debug { "Executing SQL query: $internalSqlQuery" }

    connection.prepareStatement(internalSqlQuery).use { st ->
        st.executeQuery().use { rs ->
            val tableColumns = getTableColumnsMetadata(rs)
            return fetchAndConvertDataFromResultSet(tableColumns, rs, determinedDbType, limit, inferNullability)
        }
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
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [DbConnectionConfig].
 * @param [strictValidation] if `true`, the method validates that the provided query or table name is in a valid format.
 * Default is `true` for strict validation.
 * @return the DataFrame containing the result of the SQL query.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 */
public fun DbConnectionConfig.readDataFrame(
    sqlQueryOrTableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
): AnyFrame =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.readSqlQuery(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
            dbType,
            strictValidation,
        )

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.readSqlTable(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
            dbType,
            strictValidation,
        )

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }



/**
 * Converts the result of an SQL query or SQL table (by name) to the DataFrame.
 *
 * @param [sqlQueryOrTableName] the SQL query to execute or name of the SQL table.
 * It should be a name of one of the existing SQL tables,
 * or the SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [Connection].
 * @param [strictValidation] if `true`, the method validates that the provided query or table name is in a valid format.
 * Default is `true` for strict validation.
 * @return the DataFrame containing the result of the SQL query.
 */
public fun Connection.readDataFrame(
    sqlQueryOrTableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
): AnyFrame =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.readSqlQuery(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
            dbType,
            strictValidation,
        )

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.readSqlTable(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
            dbType,
            strictValidation,
        )

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }

/**
 * Converts the result of an SQL query or SQL table (by name) to the DataFrame.
 *
 * @param [sqlQueryOrTableName] the SQL query to execute or name of the SQL table.
 * It should be a name of one of the existing SQL tables,
 * or the SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [DataSource].
 * @param [strictValidation] if `true`, the method validates that the provided query or table name is in a valid format.
 * Default is `true` for strict validation.
 * @return the DataFrame containing the result of the SQL query.
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
 * @see [DataSource.getConnection]
 */
public fun DataSource.readDataFrame(
    sqlQueryOrTableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
    strictValidation: Boolean = true,
): AnyFrame {
    connection.use { conn ->
        return when {
            isSqlQuery(sqlQueryOrTableName) -> DataFrame.readSqlQuery(
                conn,
                sqlQueryOrTableName,
                limit,
                inferNullability,
                dbType,
                strictValidation,
            )

            isSqlTableName(sqlQueryOrTableName) -> DataFrame.readSqlTable(
                conn,
                sqlQueryOrTableName,
                limit,
                inferNullability,
                dbType,
                strictValidation,
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
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * [java.sql.ResultSet]: https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html
 */
public fun DataFrame.Companion.readResultSet(
    resultSet: ResultSet,
    dbType: DbType,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame {
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
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * [java.sql.ResultSet]: https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html
 */
public fun ResultSet.readDataFrame(
    dbType: DbType,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame = DataFrame.Companion.readResultSet(this, dbType, limit, inferNullability)

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
 * @param [connection] the connection to the database (it's required to extract the database type)
 * that the [ResultSet] belongs to.
 * @param [limit] the maximum number of rows to read from the [ResultSet][java.sql.ResultSet].
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [resultSet].
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * [java.sql.ResultSet]: https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html
 */
public fun DataFrame.Companion.readResultSet(
    resultSet: ResultSet,
    connection: Connection,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
): AnyFrame {
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
 * NOTE: Reading from the [ResultSet][java.sql.ResultSet] could potentially change its state.
 *
 * @param [connection] the connection to the database (it's required to extract the database type)
 * that the [ResultSet] belongs to.
 * @param [limit] the maximum number of rows to read from the [ResultSet][java.sql.ResultSet].
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [ResultSet].
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * [java.sql.ResultSet]: https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html
 */
public fun ResultSet.readDataFrame(
    connection: Connection,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
): AnyFrame = DataFrame.Companion.readResultSet(this, connection, limit, inferNullability, dbType)

/**
 * Reads all non-system tables from a database and returns them
 * as a map of SQL tables and corresponding dataframes using the provided database configuration and limit.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [limit] the maximum number of rows to read from each table.
 * @param [catalogue] a name of the catalog from which tables will be retrieved. A null value retrieves tables from all catalogs.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @return a map of [String] to [AnyFrame] objects representing the non-system tables from the database.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 */
public fun DataFrame.Companion.readAllSqlTables(
    dbConfig: DbConnectionConfig,
    catalogue: String? = null,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
): Map<String, AnyFrame> =
    withReadOnlyConnection(dbConfig, dbType) { connection ->
        readAllSqlTables(connection, catalogue, limit, inferNullability, dbType)
    }

/**
 * Reads all non-system tables from a database and returns them
 * as a map of SQL tables and corresponding dataframes.
 *
 * @param [dataSource] the [DataSource] to get a database connection from.
 * @param [catalogue] a name of the catalog from which tables will be retrieved. A null value retrieves tables from all catalogs.
 * @param [limit] the maximum number of rows to read from each table.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @return a map of [String] to [AnyFrame] objects representing the non-system tables from the database.
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
 * @see [DataSource.getConnection]
 */
public fun DataFrame.Companion.readAllSqlTables(
    dataSource: DataSource,
    catalogue: String? = null,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
): Map<String, AnyFrame> {
    dataSource.connection.use { connection ->
        return readAllSqlTables(connection, catalogue, limit, inferNullability, dbType)
    }
}

/**
 * Reads all non-system tables from a database and returns them
 * as a map of SQL tables and corresponding dataframes.
 *
 * @param [connection] the database connection to read tables from.
 * @param [limit] the maximum number of rows to read from each table.
 * @param [catalogue] a name of the catalog from which tables will be retrieved. A null value retrieves tables from all catalogs.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [connection].
 * @return a map of [String] to [AnyFrame] objects representing the non-system tables from the database.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readAllSqlTables(
    connection: Connection,
    catalogue: String? = null,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
    dbType: DbType? = null,
): Map<String, AnyFrame> {
    val metaData = connection.metaData
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    // exclude system- and other tables without data (it looks like it is supported badly for many databases)
    val tableTypes = determinedDbType.tableTypes?.toTypedArray()
    val tables = metaData.getTables(catalogue, null, null, tableTypes)

    val dataFrames = mutableMapOf<String, AnyFrame>()

    while (tables.next()) {
        val table = determinedDbType.buildTableMetadata(tables)
        if (!determinedDbType.isSystemTable(table)) {
            // we filter here a second time because of specific logic with SQLite and possible issues with future databases
            val tableName = when {
                catalogue != null && table.schemaName != null -> "$catalogue.${table.schemaName}.${table.name}"
                catalogue != null && table.schemaName == null -> "$catalogue.${table.name}"
                else -> table.name
            }
            // TODO: both cases is schema specified or not in URL
            // in h2 database name is recognized as a schema name https://www.h2database.com/html/features.html#database_url
            // https://stackoverflow.com/questions/20896935/spring-hibernate-h2-database-schema-not-found
            // could be Dialect/Database specific
            logger.debug { "Reading table: $tableName" }

            val dataFrame = readSqlTable(connection, tableName, limit, inferNullability, dbType)
            dataFrames += tableName to dataFrame
            logger.debug { "Finished reading table: $tableName" }
        }
    }

    return dataFrames
}

/**
 * Retrieves the schema for an SQL table using the provided database configuration.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [tableName] the name of the SQL table for which to retrieve the schema.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @return the [DataFrameSchema] object representing the schema of the SQL table
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 */
public fun DataFrame.Companion.getSchemaForSqlTable(
    dbConfig: DbConnectionConfig,
    tableName: String,
    dbType: DbType? = null,
): DataFrameSchema =
    withReadOnlyConnection(dbConfig, dbType) { connection ->
        getSchemaForSqlTable(connection, tableName, dbType)
    }

/**
 * Retrieves the schema for an SQL table using the provided [DataSource].
 *
 * @param [dataSource] the [DataSource] to get a database connection from.
 * @param [tableName] the name of the SQL table for which to retrieve the schema.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @return the schema of the SQL table as a [DataFrameSchema] object.
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
 * @see [DataSource.getConnection]
 */
public fun DataFrame.Companion.getSchemaForSqlTable(
    dataSource: DataSource,
    tableName: String,
    dbType: DbType? = null,
): DataFrameSchema {
    dataSource.connection.use { connection ->
        return getSchemaForSqlTable(connection, tableName, dbType)
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
public fun DataFrame.Companion.getSchemaForSqlTable(
    connection: Connection,
    tableName: String,
    dbType: DbType? = null,
): DataFrameSchema {
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    // Read just 1 row to get the schema
    val singleRowDataFrame = readSqlTable(
        connection = connection,
        tableName = tableName,
        limit = 1,
        inferNullability = false, // Schema extraction doesn't need nullability inference
        dbType = determinedDbType,
        strictValidation = true
    )

    return singleRowDataFrame.schema()
}

/**
 * Retrieves the schema of an SQL query result using the provided database configuration.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [sqlQuery] the SQL query to execute and retrieve the schema from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 */
public fun DataFrame.Companion.getSchemaForSqlQuery(
    dbConfig: DbConnectionConfig,
    sqlQuery: String,
    dbType: DbType? = null,
): DataFrameSchema =
    withReadOnlyConnection(dbConfig, dbType) { connection ->
        getSchemaForSqlQuery(connection, sqlQuery, dbType)
    }

/**
 * Retrieves the schema of an SQL query result using the provided [DataSource].
 *
 * @param [dataSource] the [DataSource] to get a database connection from.
 * @param [sqlQuery] the SQL query to execute and retrieve the schema from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
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
 * @see [DataSource.getConnection]
 */
public fun DataFrame.Companion.getSchemaForSqlQuery(
    dataSource: DataSource,
    sqlQuery: String,
    dbType: DbType? = null,
): DataFrameSchema {
    dataSource.connection.use { connection ->
        return getSchemaForSqlQuery(connection, sqlQuery, dbType)
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
public fun DataFrame.Companion.getSchemaForSqlQuery(
    connection: Connection,
    sqlQuery: String,
    dbType: DbType? = null,
): DataFrameSchema {
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)

    // Read just 1 row to get the schema
    val singleRowDataFrame = readSqlQuery(
        connection = connection,
        sqlQuery = sqlQuery,
        limit = 1,
        inferNullability = false, // Schema extraction doesn't need nullability inference
        dbType = determinedDbType,
        strictValidation = true
    )

    return singleRowDataFrame.schema()
}

/**
 * Retrieves the schema of an SQL query result or the SQL table using the provided database configuration.
 *
 * @param [sqlQueryOrTableName] the SQL query to execute and retrieve the schema from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [DbConnectionConfig].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 */
public fun DbConnectionConfig.getDataFrameSchema(
    sqlQueryOrTableName: String,
    dbType: DbType? = null,
): DataFrameSchema =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlQuery(this, sqlQueryOrTableName, dbType)

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlTable(this, sqlQueryOrTableName, dbType)

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }

/**
 * Retrieves the schema of an SQL query result or the SQL table using the provided [DataSource].
 *
 * @param [sqlQueryOrTableName] the SQL query to execute or name of the SQL table.
 * It should be a name of one of the existing SQL tables,
 * or the SQL query should start from SELECT and contain one query for reading data without any manipulation.
 * It should not contain `;` symbol.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [DataSource].
 * @return the schema of the SQL query as a [DataFrameSchema] object.
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
 * @see [DataSource.getConnection]
 */
public fun DataSource.getDataFrameSchema(sqlQueryOrTableName: String, dbType: DbType? = null): DataFrameSchema {
    connection.use { conn ->
        return when {
            isSqlQuery(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlQuery(conn, sqlQueryOrTableName, dbType)

            isSqlTableName(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlTable(conn, sqlQueryOrTableName, dbType)

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
public fun Connection.getDataFrameSchema(sqlQueryOrTableName: String, dbType: DbType? = null): DataFrameSchema =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlQuery(this, sqlQueryOrTableName, dbType)

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlTable(this, sqlQueryOrTableName, dbType)

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
public fun DataFrame.Companion.getSchemaForResultSet(resultSet: ResultSet, dbType: DbType): DataFrameSchema {
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
public fun ResultSet.getDataFrameSchema(dbType: DbType): DataFrameSchema = DataFrame.getSchemaForResultSet(this, dbType)

/**
 * Retrieves the schemas of all non-system tables in the database using the provided database configuration.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dbConfig].
 * @return a map of [String, DataFrameSchema] objects representing the table name and its schema for each non-system table.
 *
 * ### Default Behavior:
 * If [DbConnectionConfig.readOnly] is `true` (which is the default), the connection will be:
 * - explicitly set as read-only via `Connection.setReadOnly(true)`
 * - used with `autoCommit = false`
 * - automatically rolled back after reading, ensuring no changes to the database
 *
 * Even if [DbConnectionConfig.readOnly] is set to `false`, the library still prevents data-modifying queries
 * and only permits safe `SELECT` operations internally.
 */
public fun DataFrame.Companion.getSchemaForAllSqlTables(
    dbConfig: DbConnectionConfig,
    dbType: DbType? = null,
): Map<String, DataFrameSchema> =
    withReadOnlyConnection(dbConfig, dbType) { connection ->
        getSchemaForAllSqlTables(connection, dbType)
    }

/**
 * Retrieves the schemas of all non-system tables in the database using the provided [DataSource].
 *
 * @param [dataSource] the DataSource to get a database connection from.
 * @param [dbType] the type of database, could be a custom object, provided by user, optional, default is `null`,
 * in that case the [dbType] will be recognized from the [dataSource].
 * @return a map of [String, DataFrameSchema] objects representing the table name and its schema for each non-system table.
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
 * @see [DataSource.getConnection]
 */
public fun DataFrame.Companion.getSchemaForAllSqlTables(
    dataSource: DataSource,
    dbType: DbType? = null,
): Map<String, DataFrameSchema> {
    dataSource.connection.use { connection ->
        return getSchemaForAllSqlTables(connection, dbType)
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
public fun DataFrame.Companion.getSchemaForAllSqlTables(
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
            val dataFrameSchema = getSchemaForSqlTable(connection, tableName, determinedDbType)
            dataFrameSchemas += tableName to dataFrameSchema
        }
    }

    return dataFrameSchemas
}

/**
 * Builds a DataFrame schema based on the given table columns.
 *
 * @param [tableColumns] a mutable map containing the table columns, where the key represents the column name
 * and the value represents the metadata of the column
 * @param [dbType] the type of database.
 * @return a [DataFrameSchema] object representing the schema built from the table columns.
 */
private fun buildSchemaByTableColumns(tableColumns: MutableList<TableColumnMetadata>, dbType: DbType): DataFrameSchema {
    val schemaColumns = tableColumns.associate {
        Pair(it.name, generateColumnSchemaValue(dbType, it))
    }

    return DataFrameSchemaImpl(
        columns = schemaColumns,
    )
}

private fun generateColumnSchemaValue(dbType: DbType, tableColumnMetadata: TableColumnMetadata): ColumnSchema =
    dbType.convertSqlTypeToColumnSchemaValue(tableColumnMetadata)
        ?: ColumnSchema.Value(makeCommonSqlToKTypeMapping(tableColumnMetadata))

/**
 * Retrieves the metadata of the columns in the result set.
 *
 * @param rs the result set
 * @return a mutable list of [TableColumnMetadata] objects,
 *         where each TableColumnMetadata object contains information such as the column type,
 *         JDBC type, size, and name.
 */
private fun getTableColumnsMetadata(rs: ResultSet): MutableList<TableColumnMetadata> {
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
private fun manageColumnNameDuplication(columnNameCounter: MutableMap<String, Int>, originalName: String): String {
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

// Utility function to cast arrays based on the type of elements
private fun <T : Any> castArray(array: Array<*>, elementType: KClass<T>): List<T> =
    array.mapNotNull { elementType.safeCast(it) }

/**
 * Fetches and converts data from a ResultSet into a mutable map.
 *
 * @param [tableColumns] a list containing the column metadata for the table.
 * @param [rs] the ResultSet object containing the data to be fetched and converted.
 * @param [dbType] the type of the database.
 * @param [limit] the maximum number of rows to fetch and convert.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @return A mutable map containing the fetched and converted data.
 */
private fun fetchAndConvertDataFromResultSet(
    tableColumns: MutableList<TableColumnMetadata>,
    rs: ResultSet,
    dbType: DbType,
    limit: Int,
    inferNullability: Boolean,
): AnyFrame {
    val data = List(tableColumns.size) { mutableListOf<Any?>() }

    val kotlinTypesForSqlColumns = mutableMapOf<Int, KType>()
    List(tableColumns.size) { index ->
        kotlinTypesForSqlColumns[index] = generateKType(dbType, tableColumns[index])
    }

    var counter = 0

    if (limit > 0) {
        while (counter < limit && rs.next()) {
            extractNewRowFromResultSetAndAddToData(tableColumns, data, rs, kotlinTypesForSqlColumns)
            counter++
            // if (counter % 1000 == 0) logger.debug { "Loaded $counter rows." } // TODO: https://github.com/Kotlin/dataframe/issues/455
        }
    } else {
        while (rs.next()) {
            extractNewRowFromResultSetAndAddToData(tableColumns, data, rs, kotlinTypesForSqlColumns)
            counter++
            // if (counter % 1000 == 0) logger.debug { "Loaded $counter rows." } // TODO: https://github.com/Kotlin/dataframe/issues/455
        }
    }

    val dataFrame = data.mapIndexed { index, values ->
        // TODO: add override handlers from dbType to intercept the final parcing before column creation
        val correctedValues = if (kotlinTypesForSqlColumns[index]!!.classifier == Array::class) {
            handleArrayValues(values)
        } else {
            values
        }

        DataColumn.createValueColumn(
            name = tableColumns[index].name,
            values = correctedValues,
            infer = convertNullabilityInference(inferNullability),
            type = kotlinTypesForSqlColumns[index]!!,
        )
    }.toDataFrame()

    logger.debug {
        "DataFrame with ${dataFrame.rowsCount()} rows and ${dataFrame.columnsCount()} columns created as a result of SQL query."
    }

    return dataFrame
}

private fun handleArrayValues(values: MutableList<Any?>): List<Any> {
    // Intermediate variable for the first mapping
    val sqlArrays = values.mapNotNull {
        (it as? java.sql.Array)?.array?.let { array -> array as? Array<*> }
    }

    // Flatten the arrays to iterate through all elements and filter out null values, then map to component types
    val allElementTypes = sqlArrays
        .flatMap { array ->
            (array.javaClass.componentType?.kotlin?.let { listOf(it) } ?: emptyList())
        } // Get the component type of each array and convert it to a Kotlin class, if available

    // Find distinct types and ensure there's only one distinct type
    val commonElementType = allElementTypes
        .distinct() // Get unique element types
        .singleOrNull() // Ensure there's only one unique element type, otherwise return null
        ?: Any::class // Fallback to Any::class if multiple distinct types or no elements found

    return if (commonElementType != Any::class) {
        sqlArrays.map { castArray(it, commonElementType).toTypedArray() }
    } else {
        sqlArrays
    }
}

private fun convertNullabilityInference(inferNullability: Boolean) = if (inferNullability) Infer.Nulls else Infer.None

private fun extractNewRowFromResultSetAndAddToData(
    tableColumns: MutableList<TableColumnMetadata>,
    data: List<MutableList<Any?>>,
    rs: ResultSet,
    kotlinTypesForSqlColumns: MutableMap<Int, KType>,
) {
    repeat(tableColumns.size) { i ->
        data[i].add(
            try {
                rs.getObject(i + 1)
                // TODO: add a special handler for Blob via Streams
            } catch (_: Throwable) {
                val kType = kotlinTypesForSqlColumns[i]!!
                // TODO: expand for all the types like in generateKType function
                if (kType.isSupertypeOf(String::class.starProjectedType)) rs.getString(i + 1) else rs.getString(i + 1)
            },
        )
    }
}

/**
 * Generates a KType based on the given database type and table column metadata.
 *
 * @param dbType The database type.
 * @param tableColumnMetadata The table column metadata.
 *
 * @return The generated KType.
 */
private fun generateKType(dbType: DbType, tableColumnMetadata: TableColumnMetadata): KType =
    dbType.convertSqlTypeToKType(tableColumnMetadata)
        ?: makeCommonSqlToKTypeMapping(tableColumnMetadata)

/**
 * Creates a mapping between common SQL types and their corresponding KTypes.
 *
 * @param tableColumnMetadata The metadata of the table column.
 * @return The KType associated with the SQL type or a default type if no mapping is found.
 */
private fun makeCommonSqlToKTypeMapping(tableColumnMetadata: TableColumnMetadata): KType {
    val jdbcTypeToKTypeMapping = mapOf(
        Types.BIT to Boolean::class,
        Types.TINYINT to Int::class,
        Types.SMALLINT to Int::class,
        Types.INTEGER to Int::class,
        Types.BIGINT to Long::class,
        Types.FLOAT to Float::class,
        Types.REAL to Float::class,
        Types.DOUBLE to Double::class,
        Types.NUMERIC to BigDecimal::class,
        Types.DECIMAL to BigDecimal::class,
        Types.CHAR to String::class,
        Types.VARCHAR to String::class,
        Types.LONGVARCHAR to String::class,
        Types.DATE to Date::class,
        Types.TIME to Time::class,
        Types.TIMESTAMP to Timestamp::class,
        Types.BINARY to ByteArray::class,
        Types.VARBINARY to ByteArray::class,
        Types.LONGVARBINARY to ByteArray::class,
        Types.NULL to String::class,
        Types.JAVA_OBJECT to Any::class,
        Types.DISTINCT to Any::class,
        Types.STRUCT to Any::class,
        Types.ARRAY to Array::class,
        Types.BLOB to ByteArray::class,
        Types.CLOB to Clob::class,
        Types.REF to Ref::class,
        Types.DATALINK to Any::class,
        Types.BOOLEAN to Boolean::class,
        Types.ROWID to RowId::class,
        Types.NCHAR to String::class,
        Types.NVARCHAR to String::class,
        Types.LONGNVARCHAR to String::class,
        Types.NCLOB to NClob::class,
        Types.SQLXML to SQLXML::class,
        Types.REF_CURSOR to Ref::class,
        Types.TIME_WITH_TIMEZONE to OffsetTime::class,
        Types.TIMESTAMP_WITH_TIMEZONE to OffsetDateTime::class,
    )

    fun determineKotlinClass(tableColumnMetadata: TableColumnMetadata): KClass<*> =
        when {
            tableColumnMetadata.jdbcType == Types.OTHER -> when (tableColumnMetadata.javaClassName) {
                "[B" -> ByteArray::class
                else -> Any::class
            }

            tableColumnMetadata.javaClassName == "[B" -> ByteArray::class

            tableColumnMetadata.javaClassName == "java.sql.Blob" -> Blob::class

            tableColumnMetadata.jdbcType == Types.TIMESTAMP &&
                tableColumnMetadata.javaClassName == "java.time.LocalDateTime" -> LocalDateTime::class

            tableColumnMetadata.jdbcType == Types.BINARY &&
                tableColumnMetadata.javaClassName == "java.util.UUID" -> UUID::class

            tableColumnMetadata.jdbcType == Types.REAL &&
                tableColumnMetadata.javaClassName == "java.lang.Double" -> Double::class

            tableColumnMetadata.jdbcType == Types.FLOAT &&
                tableColumnMetadata.javaClassName == "java.lang.Double" -> Double::class

            tableColumnMetadata.jdbcType == Types.NUMERIC &&
                tableColumnMetadata.javaClassName == "java.lang.Double" -> Double::class

            else -> jdbcTypeToKTypeMapping[tableColumnMetadata.jdbcType] ?: String::class
        }

    fun createArrayTypeIfNeeded(kClass: KClass<*>, isNullable: Boolean): KType =
        if (kClass == Array::class) {
            val typeParam = kClass.typeParameters[0].createType()
            kClass.createType(
                arguments = listOf(kotlin.reflect.KTypeProjection.invariant(typeParam)),
                nullable = isNullable,
            )
        } else {
            kClass.createType(nullable = isNullable)
        }

    val kClass: KClass<*> = determineKotlinClass(tableColumnMetadata)
    val kType = createArrayTypeIfNeeded(kClass, tableColumnMetadata.isNullable)
    return kType
}
