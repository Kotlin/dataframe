package org.jetbrains.kotlinx.dataframe.io

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.io.db.DbType
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
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Date
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
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
 * Constant variable indicating the start of an SQL read query.
 * The value of this variable is "SELECT".
 */
private const val START_OF_READ_SQL_QUERY = "SELECT"

/**
 * Constant representing the separator used to separate multiple SQL queries.
 *
 * This separator is used when multiple SQL queries need to be executed together.
 * Each query should be separated by this separator to indicate the end of one query
 * and the start of the next query.
 */
private const val MULTIPLE_SQL_QUERY_SEPARATOR = ";"

/**
 * Represents a column in a database table to keep all required meta-information.
 *
 * @property [name] the name of the column.
 * @property [sqlTypeName] the SQL data type of the column.
 * @property [jdbcType] the JDBC data type of the column produced from [java.sql.Types].
 * @property [size] the size of the column.
 * @property [javaClassName] the class name in Java.
 * @property [isNullable] true if column could contain nulls.
 */
public data class TableColumnMetadata(
    val name: String,
    val sqlTypeName: String,
    val jdbcType: Int,
    val size: Int,
    val javaClassName: String,
    val isNullable: Boolean = false,
)

/**
 * Represents a table metadata to store information about a database table,
 * including its name, schema name, and catalogue name.
 *
 * NOTE: we need to extract both, [schemaName] and [catalogue]
 * because the different databases have different implementations of metadata.
 *
 * @property [name] the name of the table.
 * @property [schemaName] the name of the schema the table belongs to (optional).
 * @property [catalogue] the name of the catalogue the table belongs to (optional).
 */
public data class TableMetadata(val name: String, val schemaName: String?, val catalogue: String?)

/**
 * Represents the configuration for a database connection.
 *
 * @property [url] the URL of the database. Keep it in the following form jdbc:subprotocol:subnam
 * @property [user] the username used for authentication (optional, default is empty string).
 * @property [password] the password used for authentication (optional, default is empty string).
 */
public data class DbConnectionConfig(val url: String, val user: String = "", val password: String = "")

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [dbConfig] the configuration for the database, including URL, user, and password.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @return the DataFrame containing the data from the SQL table.
 */
public fun DataFrame.Companion.readSqlTable(
    dbConfig: DbConnectionConfig,
    tableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return readSqlTable(connection, tableName, limit, inferNullability)
    }
}

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [connection] the database connection to read tables from.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @return the DataFrame containing the data from the SQL table.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readSqlTable(
    connection: Connection,
    tableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame {
    val url = connection.metaData.url
    val dbType = extractDBTypeFromConnection(connection)

    val selectAllQuery = if (limit > 0) {
        dbType.sqlQueryLimit("SELECT * FROM $tableName", limit)
    } else {
        "SELECT * FROM $tableName"
    }

    connection.createStatement().use { st ->
        logger.debug { "Connection with url:$url is established successfully." }

        st.executeQuery(selectAllQuery).use { rs ->
            val tableColumns = getTableColumnsMetadata(rs)
            return fetchAndConvertDataFromResultSet(tableColumns, rs, dbType, limit, inferNullability)
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
 * @return the DataFrame containing the result of the SQL query.
 */
public fun DataFrame.Companion.readSqlQuery(
    dbConfig: DbConnectionConfig,
    sqlQuery: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return readSqlQuery(connection, sqlQuery, limit, inferNullability)
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
 * @return the DataFrame containing the result of the SQL query.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readSqlQuery(
    connection: Connection,
    sqlQuery: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame {
    require(isValid(sqlQuery)) {
        "SQL query should start from SELECT and contain one query for reading data without any manipulation. " +
            "Also it should not contain any separators like `;`."
    }

    val dbType = extractDBTypeFromConnection(connection)

    val internalSqlQuery = if (limit > 0) dbType.sqlQueryLimit(sqlQuery, limit) else sqlQuery

    logger.debug { "Executing SQL query: $internalSqlQuery" }

    connection.createStatement().use { st ->
        st.executeQuery(internalSqlQuery).use { rs ->
            val tableColumns = getTableColumnsMetadata(rs)
            return fetchAndConvertDataFromResultSet(tableColumns, rs, dbType, limit, inferNullability)
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
 * @return the DataFrame containing the result of the SQL query.
 */
public fun DbConnectionConfig.readDataFrame(
    sqlQueryOrTableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.readSqlQuery(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
        )

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.readSqlTable(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
        )

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }

private fun isSqlQuery(sqlQueryOrTableName: String): Boolean {
    val queryPattern = Regex("(?i)\\b(SELECT)\\b")
    return queryPattern.containsMatchIn(sqlQueryOrTableName.trim())
}

private fun isSqlTableName(sqlQueryOrTableName: String): Boolean {
    // Match table names with optional schema and catalog (e.g., catalog.schema.table)
    val tableNamePattern = Regex("^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*){0,2}$")
    return tableNamePattern.matches(sqlQueryOrTableName.trim())
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
 * @return the DataFrame containing the result of the SQL query.
 */
public fun Connection.readDataFrame(
    sqlQueryOrTableName: String,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.readSqlQuery(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
        )

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.readSqlTable(
            this,
            sqlQueryOrTableName,
            limit,
            inferNullability,
        )

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }

/** SQL query is accepted only if it starts from SELECT */
private fun isValid(sqlQuery: String): Boolean {
    val normalizedSqlQuery = sqlQuery.trim().uppercase()

    return normalizedSqlQuery.startsWith(START_OF_READ_SQL_QUERY) &&
        !normalizedSqlQuery.contains(MULTIPLE_SQL_QUERY_SEPARATOR)
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
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * [java.sql.ResultSet]: https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html
 */
public fun DataFrame.Companion.readResultSet(
    resultSet: ResultSet,
    connection: Connection,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame {
    val dbType = extractDBTypeFromConnection(connection)

    return readResultSet(resultSet, dbType, limit, inferNullability)
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
 * @return the DataFrame generated from the [ResultSet][java.sql.ResultSet] data.
 *
 * [java.sql.ResultSet]: https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html
 */
public fun ResultSet.readDataFrame(
    connection: Connection,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): AnyFrame = DataFrame.Companion.readResultSet(this, connection, limit, inferNullability)

/**
 * Reads all non-system tables from a database and returns them
 * as a map of SQL tables and corresponding dataframes using the provided database configuration and limit.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [limit] the maximum number of rows to read from each table.
 * @param [catalogue] a name of the catalog from which tables will be retrieved. A null value retrieves tables from all catalogs.
 * @param [inferNullability] indicates how the column nullability should be inferred.
 * @return a map of [String] to [AnyFrame] objects representing the non-system tables from the database.
 */
public fun DataFrame.Companion.readAllSqlTables(
    dbConfig: DbConnectionConfig,
    catalogue: String? = null,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): Map<String, AnyFrame> {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return readAllSqlTables(connection, catalogue, limit, inferNullability)
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
 * @return a map of [String] to [AnyFrame] objects representing the non-system tables from the database.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readAllSqlTables(
    connection: Connection,
    catalogue: String? = null,
    limit: Int = DEFAULT_LIMIT,
    inferNullability: Boolean = true,
): Map<String, AnyFrame> {
    val metaData = connection.metaData
    val dbType = extractDBTypeFromConnection(connection)

    // exclude a system and other tables without data, but it looks like it is supported badly for many databases
    val tables = metaData.getTables(catalogue, null, null, arrayOf("TABLE"))

    val dataFrames = mutableMapOf<String, AnyFrame>()

    while (tables.next()) {
        val table = dbType.buildTableMetadata(tables)
        if (!dbType.isSystemTable(table)) {
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

            val dataFrame = readSqlTable(connection, tableName, limit, inferNullability)
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
 * @return the [DataFrameSchema] object representing the schema of the SQL table
 */
public fun DataFrame.Companion.getSchemaForSqlTable(dbConfig: DbConnectionConfig, tableName: String): DataFrameSchema {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return getSchemaForSqlTable(connection, tableName)
    }
}

/**
 * Retrieves the schema for an SQL table using the provided database connection.
 *
 * @param [connection] the database connection.
 * @param [tableName] the name of the SQL table for which to retrieve the schema.
 * @return the schema of the SQL table as a [DataFrameSchema] object.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.getSchemaForSqlTable(connection: Connection, tableName: String): DataFrameSchema {
    val dbType = extractDBTypeFromConnection(connection)

    val sqlQuery = "SELECT * FROM $tableName"
    val selectFirstRowQuery = dbType.sqlQueryLimit(sqlQuery, limit = 1)

    connection.createStatement().use { st ->
        st.executeQuery(selectFirstRowQuery).use { rs ->
            val tableColumns = getTableColumnsMetadata(rs)
            return buildSchemaByTableColumns(tableColumns, dbType)
        }
    }
}

/**
 * Retrieves the schema of an SQL query result using the provided database configuration.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [sqlQuery] the SQL query to execute and retrieve the schema from.
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 */
public fun DataFrame.Companion.getSchemaForSqlQuery(dbConfig: DbConnectionConfig, sqlQuery: String): DataFrameSchema {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return getSchemaForSqlQuery(connection, sqlQuery)
    }
}

/**
 * Retrieves the schema of an SQL query result using the provided database connection.
 *
 * @param [connection] the database connection.
 * @param [sqlQuery] the SQL query to execute and retrieve the schema from.
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.getSchemaForSqlQuery(connection: Connection, sqlQuery: String): DataFrameSchema {
    val dbType = extractDBTypeFromConnection(connection)

    connection.createStatement().use { st ->
        st.executeQuery(sqlQuery).use { rs ->
            val tableColumns = getTableColumnsMetadata(rs)
            return buildSchemaByTableColumns(tableColumns, dbType)
        }
    }
}

/**
 * Retrieves the schema of an SQL query result or the SQL table using the provided database configuration.
 *
 * @param [sqlQueryOrTableName] the SQL query to execute and retrieve the schema from.
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 */
public fun DbConnectionConfig.getDataFrameSchema(sqlQueryOrTableName: String): DataFrameSchema =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlQuery(this, sqlQueryOrTableName)

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlTable(this, sqlQueryOrTableName)

        else -> throw IllegalArgumentException(
            "$sqlQueryOrTableName should be SQL query or name of one of the existing SQL tables!",
        )
    }

/**
 * Retrieves the schema of an SQL query result or the SQL table using the provided database configuration.
 *
 * @param [sqlQueryOrTableName] the SQL query to execute and retrieve the schema from.
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 */
public fun Connection.getDataFrameSchema(sqlQueryOrTableName: String): DataFrameSchema =
    when {
        isSqlQuery(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlQuery(this, sqlQueryOrTableName)

        isSqlTableName(sqlQueryOrTableName) -> DataFrame.getSchemaForSqlTable(this, sqlQueryOrTableName)

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
 * @param [dbType] the type of database that the [ResultSet] belongs to.
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
 * @param [dbType] the type of database that the [ResultSet] belongs to.
 * @return the schema of the [ResultSet] as a [DataFrameSchema] object.
 */
public fun ResultSet.getDataFrameSchema(dbType: DbType): DataFrameSchema = DataFrame.getSchemaForResultSet(this, dbType)

/**
 * Retrieves the schema from [ResultSet].
 *
 * NOTE: [connection] is required to extract the database type.
 * This function will not close connection and result set and not retrieve data from the result set.
 *
 * @param [resultSet] the [ResultSet] obtained from executing a database query.
 * @param [connection] the connection to the database (it's required to extract the database type).
 * @return the schema of the [ResultSet] as a [DataFrameSchema] object.
 */
public fun DataFrame.Companion.getSchemaForResultSet(resultSet: ResultSet, connection: Connection): DataFrameSchema {
    val dbType = extractDBTypeFromConnection(connection)

    val tableColumns = getTableColumnsMetadata(resultSet)
    return buildSchemaByTableColumns(tableColumns, dbType)
}

/**
 * Retrieves the schema from [ResultSet].
 *
 * NOTE: This function will not close connection and result set and not retrieve data from the result set.
 *
 * @param [connection] the connection to the database (it's required to extract the database type).
 * @return the schema of the [ResultSet] as a [DataFrameSchema] object.
 */
public fun ResultSet.getDataFrameSchema(connection: Connection): DataFrameSchema =
    DataFrame.getSchemaForResultSet(this, connection)

/**
 * Retrieves the schemas of all non-system tables in the database using the provided database configuration.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @return a map of [String, DataFrameSchema] objects representing the table name and its schema for each non-system table.
 */
public fun DataFrame.Companion.getSchemaForAllSqlTables(dbConfig: DbConnectionConfig): Map<String, DataFrameSchema> {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return getSchemaForAllSqlTables(connection)
    }
}

/**
 * Retrieves the schemas of all non-system tables in the database using the provided database connection.
 *
 * @param [connection] the database connection.
 * @return a map of [String, DataFrameSchema] objects representing the table name and its schema for each non-system table.
 */
public fun DataFrame.Companion.getSchemaForAllSqlTables(connection: Connection): Map<String, DataFrameSchema> {
    val metaData = connection.metaData
    val dbType = extractDBTypeFromConnection(connection)

    val tableTypes = arrayOf("TABLE")
    // exclude a system and other tables without data
    val tables = metaData.getTables(null, null, null, tableTypes)

    val dataFrameSchemas = mutableMapOf<String, DataFrameSchema>()

    while (tables.next()) {
        val jdbcTable = dbType.buildTableMetadata(tables)
        if (!dbType.isSystemTable(jdbcTable)) {
            // we filter her a second time because of specific logic with SQLite and possible issues with future databases
            val tableName = jdbcTable.name
            val dataFrameSchema = getSchemaForSqlTable(connection, tableName)
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
        DataColumn.createValueColumn(
            name = tableColumns[index].name,
            values = values,
            infer = convertNullabilityInference(inferNullability),
            type = kotlinTypesForSqlColumns[index]!!,
        )
    }.toDataFrame()

    logger.debug {
        "DataFrame with ${dataFrame.rowsCount()} rows and ${dataFrame.columnsCount()} columns created as a result of SQL query."
    }

    return dataFrame
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
        Types.ARRAY to Array<Any>::class,
        Types.BLOB to Blob::class,
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

    fun determineKotlinClass(tableColumnMetadata: TableColumnMetadata): KClass<*> {
        return when {
            tableColumnMetadata.jdbcType == Types.OTHER -> when (tableColumnMetadata.javaClassName) {
                "[B" -> ByteArray::class
                else -> Any::class
            }
            tableColumnMetadata.jdbcType == Types.BINARY && tableColumnMetadata.javaClassName == "java.util.UUID" -> UUID::class
            else -> jdbcTypeToKTypeMapping[tableColumnMetadata.jdbcType] ?: String::class
        }
    }

    val kClass: KClass<*> = determineKotlinClass(tableColumnMetadata)
    return kClass.createType(nullable = tableColumnMetadata.isNullable)
}


