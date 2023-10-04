package org.jetbrains.kotlinx.dataframe.io

import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.H2
import org.jetbrains.kotlinx.dataframe.io.db.Sqlite
import org.jetbrains.kotlinx.dataframe.io.db.extractDBTypeFromURL
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

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
 * Represents a column in a database table to keep all required meta-information.
 *
 * @property [name] the name of the column.
 * @property [sqlTypeName] the SQL data type of the column.
 * @property [jdbcType] the JDBC data type of the column produced from [java.sql.Types].
 * @property [size] the size of the column.
 */
public data class TableColumnMetadata(val name: String, val sqlTypeName: String, val jdbcType: Int, val size: Int)

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
public data class DatabaseConfiguration(val url: String, val user: String = "", val password: String = "")

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [dbConfig] the configuration for the database, including URL, user, and password.
 * @param [tableName] the name of the table to read data from.
 * @return the DataFrame containing the data from the SQL table.
 */
public fun DataFrame.Companion.readSqlTable(dbConfig: DatabaseConfiguration, tableName: String): AnyFrame {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return readSqlTable(connection, tableName, DEFAULT_LIMIT)
    }
}

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [dbConfig] the configuration for the database, including URL, user, and password.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 * @return the DataFrame containing the data from the SQL table.
 */
public fun DataFrame.Companion.readSqlTable(dbConfig: DatabaseConfiguration, tableName: String, limit: Int): AnyFrame {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return readSqlTable(connection, tableName, limit)
    }
}

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [connection] the database connection to read tables from.
 * @param [tableName] the name of the table to read data from.
 * @return the DataFrame containing the data from the SQL table.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readSqlTable(connection: Connection, tableName: String): AnyFrame {
    return readSqlTable(connection, tableName, DEFAULT_LIMIT)
}

/**
 * Reads data from an SQL table and converts it into a DataFrame.
 *
 * @param [connection] the database connection to read tables from.
 * @param [tableName] the name of the table to read data from.
 * @param [limit] the maximum number of rows to retrieve from the table.
 * @return the DataFrame containing the data from the SQL table.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readSqlTable(connection: Connection, tableName: String, limit: Int): AnyFrame {
    var preparedQuery = "SELECT * FROM $tableName"
    if (limit > 0) preparedQuery += " LIMIT $limit"

    val url = connection.metaData.url
    val dbType = extractDBTypeFromURL(url)

    connection.createStatement().use { st ->
        logger.debug { "Connection with url:${url} is established successfully." }
        val tableColumns = getTableColumnsMetadata(connection, tableName)

        st.executeQuery(
            preparedQuery
        ).use { rs ->
            val data = fetchAndConvertDataFromResultSet(tableColumns, rs, dbType, limit)
            return data.toDataFrame()
        }
    }
}

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [sqlQuery] the SQL query to execute.
 * @return the DataFrame containing the result of the SQL query.
 */
public fun DataFrame.Companion.readSqlQuery(dbConfig: DatabaseConfiguration, sqlQuery: String): AnyFrame {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return readSqlQuery(connection, sqlQuery, DEFAULT_LIMIT)
    }
}

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [sqlQuery] the SQL query to execute.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 * @return the DataFrame containing the result of the SQL query.
 */
public fun DataFrame.Companion.readSqlQuery(dbConfig: DatabaseConfiguration, sqlQuery: String, limit: Int): AnyFrame {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return readSqlQuery(connection, sqlQuery, limit)
    }
}

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * @param [connection] the database connection to execute the SQL query.
 * @param [sqlQuery] the SQL query to execute.
 * @return the DataFrame containing the result of the SQL query.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readSqlQuery(connection: Connection, sqlQuery: String): AnyFrame {
    return readSqlQuery(connection, sqlQuery, DEFAULT_LIMIT)
}

/**
 * Converts the result of an SQL query to the DataFrame.
 *
 * @param [connection] the database connection to execute the SQL query.
 * @param [sqlQuery] the SQL query to execute.
 * @param [limit] the maximum number of rows to retrieve from the result of the SQL query execution.
 * @return the DataFrame containing the result of the SQL query.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readSqlQuery(connection: Connection, sqlQuery: String, limit: Int): AnyFrame {
    val url = connection.metaData.url
    val dbType = extractDBTypeFromURL(url)

    var internalSqlQuery = sqlQuery
    if (limit > 0) internalSqlQuery += " LIMIT $limit"

    logger.debug { "Executing SQL query: $internalSqlQuery" }

    connection.createStatement().use { st ->
        st.executeQuery(internalSqlQuery).use { rs ->
            val tableColumns = getTableColumnsMetadata(rs)
            val data = fetchAndConvertDataFromResultSet(tableColumns, rs, dbType, DEFAULT_LIMIT)

            logger.debug { "SQL query executed successfully. Converting data to DataFrame." }

            return data.toDataFrame()
        }
    }
}

/**
 * Reads the data from a [ResultSet] and converts it into a DataFrame.
 *
 * @param [resultSet] the ResultSet containing the data to read.
 * @param [dbType] the type of database that the ResultSet belongs to.
 * @return the DataFrame generated from the ResultSet data.
 */
public fun DataFrame.Companion.readResultSet(resultSet: ResultSet, dbType: DbType): AnyFrame {
    return readResultSet(resultSet, dbType, DEFAULT_LIMIT)
}

/**
 * Reads the data from a ResultSet and converts it into a DataFrame.
 *
 * @param [resultSet] the ResultSet containing the data to read.
 * @param [dbType] the type of database that the ResultSet belongs to.
 * @param [limit] the maximum number of rows to read from the ResultSet.
 * @return the DataFrame generated from the ResultSet data.
 */
public fun DataFrame.Companion.readResultSet(resultSet: ResultSet, dbType: DbType, limit: Int): AnyFrame {
    val tableColumns = getTableColumnsMetadata(resultSet)
    val data = fetchAndConvertDataFromResultSet(tableColumns, resultSet, dbType, limit)
    return data.toDataFrame()
}

/**
 * Reads the data from a ResultSet and converts it into a DataFrame.
 *
 * @param [resultSet] the ResultSet containing the data to read.
 * @param [connection] the connection to the database (it's required to extract the database type).
 * @return the DataFrame generated from the ResultSet data.
 */
public fun DataFrame.Companion.readResultSet(resultSet: ResultSet, connection: Connection): AnyFrame {
    return readResultSet(resultSet, connection, DEFAULT_LIMIT)
}

/**
 * Reads the data from a ResultSet and converts it into a DataFrame.
 *
 * @param [resultSet] the ResultSet containing the data to read.
 * @param [connection] the connection to the database (it's required to extract the database type).
 * @param [limit] the maximum number of rows to read from the ResultSet.
 * @return the DataFrame generated from the ResultSet data.
 */
public fun DataFrame.Companion.readResultSet(resultSet: ResultSet, connection: Connection, limit: Int): AnyFrame {
    val url = connection.metaData.url
    val dbType = extractDBTypeFromURL(url)

    return readResultSet(resultSet, dbType, limit)
}

/**
 * Reads all non-system tables from a database and returns them as a list of data frames
 * using the provided database configuration.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @return a list of [AnyFrame] objects representing the non-system tables from the database.
 */
public fun DataFrame.Companion.readAllSqlTables(dbConfig: DatabaseConfiguration): List<AnyFrame> {
    return readAllSqlTables(dbConfig, DEFAULT_LIMIT)
}

/**
 * Reads all tables from the given database using the provided database configuration and limit.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [limit] the maximum number of rows to read from each table.
 * @return a list of [AnyFrame] objects representing the non-system tables from the database.
 */
public fun DataFrame.Companion.readAllSqlTables(dbConfig: DatabaseConfiguration, limit: Int): List<AnyFrame> {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return readAllSqlTables(connection, limit)
    }
}

/**
 * Reads all non-system tables from a database and returns them as a list of data frames.
 *
 * @param [connection] the database connection to read tables from.
 * @return a list of [AnyFrame] objects representing the non-system tables from the database.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readAllSqlTables(connection: Connection): List<AnyFrame> {
    return readAllSqlTables(connection, DEFAULT_LIMIT)
}

/**
 * Reads all non-system tables from a database and returns them as a list of data frames.
 *
 * @param [connection] the database connection to read tables from.
 * @param [limit] the maximum number of rows to read from each table.
 * @return a list of [AnyFrame] objects representing the non-system tables from the database.
 *
 * @see DriverManager.getConnection
 */
public fun DataFrame.Companion.readAllSqlTables(connection: Connection, limit: Int): List<AnyFrame> {
    val metaData = connection.metaData
    val url = connection.metaData.url
    val dbType = extractDBTypeFromURL(url)

    // exclude a system and other tables without data, but it looks like it supported badly for many databases
    val tables = metaData.getTables(null, null, null, arrayOf("TABLE"))

    val dataFrames = mutableListOf<AnyFrame>()

    while (tables.next()) {
        val table = buildTableMetadata(dbType, tables)
        if (!dbType.isSystemTable(table)) {
            // we filter her second time because of specific logic with SQLite and possible issues with future databases
            logger.debug { "Reading table: ${table.name}" }
            val dataFrame = readSqlTable(connection, table.name, limit)
            dataFrames += dataFrame
            logger.debug { "Finished reading table: ${table.name}" }
        }
    }

    return dataFrames
}

/**
 * Builds the table metadata based on the database type and the ResultSet from the query.
 *
 * @param [dbType] the type of the database being used.
 * @param [tableResultSet] the ResultSet containing the table's meta-information.
 * @return the TableMetadata object representing the table metadata.
 */
// TODO: move to DB abstract method
private fun buildTableMetadata(dbType: DbType, tableResultSet: ResultSet): TableMetadata =
    when (dbType) {
        is H2, Sqlite -> TableMetadata(
            tableResultSet.getString("TABLE_NAME"),
            tableResultSet.getString("TABLE_SCHEM"),
            tableResultSet.getString("TABLE_CAT"))
        else -> {
            TableMetadata(
                tableResultSet.getString("table_name"),
                tableResultSet.getString("table_schem"),
                tableResultSet.getString("table_cat"))
        }
    }

/**
 * Retrieves the schema for an SQL table using the provided database configuration.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [tableName] the name of the SQL table for which to retrieve the schema.
 * @return the DataFrameSchema object representing the schema of the SQL table
 */
public fun DataFrame.Companion.getSchemaForSqlTable(dbConfig: DatabaseConfiguration, tableName: String): DataFrameSchema {
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
public fun DataFrame.Companion.getSchemaForSqlTable(
    connection: Connection,
    tableName: String
): DataFrameSchema {
    val url = connection.metaData.url
    val dbType = extractDBTypeFromURL(url)

    connection.createStatement().use {
        logger.debug { "Connection with url:${connection.metaData.url} is established successfully." }

        val tableColumns = getTableColumnsMetadata(connection, tableName)

        return buildSchemaByTableColumns(tableColumns, dbType)
    }
}

/**
 * Retrieves the schema of an SQL query result using the provided database configuration.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @param [sqlQuery] the SQL query to execute and retrieve the schema from.
 * @return the schema of the SQL query as a [DataFrameSchema] object.
 */
public fun DataFrame.Companion.getSchemaForSqlQuery(dbConfig: DatabaseConfiguration, sqlQuery: String): DataFrameSchema {
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
    val url = connection.metaData.url
    val dbType = extractDBTypeFromURL(url)

    connection.createStatement().use { st ->
        st.executeQuery(sqlQuery).use { rs ->
            val tableColumns = getTableColumnsMetadata(rs)
            return buildSchemaByTableColumns(tableColumns, dbType)
        }
    }
}

/**
 * Retrieves the schema from ResultSet.
 *
 * NOTE: This function will not close connection and result set and not retrieve data from the result set.
 *
 * @param [resultSet] the ResultSet obtained from executing a database query.
 * @param [dbType] the type of database that the ResultSet belongs to.
 * @return the schema of the ResultSet as a [DataFrameSchema] object.
 */
public fun DataFrame.Companion.getSchemaForResultSet(resultSet: ResultSet, dbType: DbType): DataFrameSchema {
    val tableColumns = getTableColumnsMetadata(resultSet)
    return buildSchemaByTableColumns(tableColumns, dbType)
}

/**
 * Retrieves the schema from ResultSet.
 *
 * NOTE: [connection] is required to extract the database type.
 * This function will not close connection and result set and not retrieve data from the result set.
 *
 * @param [resultSet] the ResultSet obtained from executing a database query.
 * @param [connection] the connection to the database (it's required to extract the database type).
 * @return the schema of the ResultSet as a [DataFrameSchema] object.
 */
public fun DataFrame.Companion.getSchemaForResultSet(resultSet: ResultSet, connection: Connection): DataFrameSchema {
    val url = connection.metaData.url
    val dbType = extractDBTypeFromURL(url)

    val tableColumns = getTableColumnsMetadata(resultSet)
    return buildSchemaByTableColumns(tableColumns, dbType)
}

/**
 * Retrieves the schema of all non-system tables in the database using the provided database configuration.
 *
 * @param [dbConfig] the database configuration to connect to the database, including URL, user, and password.
 * @return a list of DataFrameSchema objects representing the schema of each non-system table.
 */
public fun DataFrame.Companion.getSchemaForAllSqlTables(dbConfig: DatabaseConfiguration): List<DataFrameSchema> {
    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password).use { connection ->
        return getSchemaForAllSqlTables(connection)
    }
}

/**
 * Retrieves the schema of all non-system tables in the database using the provided database connection.
 *
 * @param [connection] the database connection.
 * @return a list of DataFrameSchema objects representing the schema of each non-system table.
 */
public fun DataFrame.Companion.getSchemaForAllSqlTables(connection: Connection): List<DataFrameSchema> {
    val metaData = connection.metaData
    val url = connection.metaData.url
    val dbType = extractDBTypeFromURL(url)

    val tableTypes = arrayOf("TABLE")
    // exclude system and other tables without data
    val tables = metaData.getTables(null, null, null, tableTypes)

    val dataFrameSchemas = mutableListOf<DataFrameSchema>()

    while (tables.next()) {
        val jdbcTable = buildTableMetadata(dbType, tables)
        if (!dbType.isSystemTable(jdbcTable)) {
            // we filter her second time because of specific logic with SQLite and possible issues with future databases
            val dataFrameSchema = getSchemaForSqlTable(connection, jdbcTable.name)
            dataFrameSchemas += dataFrameSchema
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
 * @return a DataFrameSchema object representing the schema built from the table columns.
 */
private fun buildSchemaByTableColumns(tableColumns: MutableMap<String, TableColumnMetadata>, dbType: DbType): DataFrameSchema {
    val schemaColumns = tableColumns.map {
        Pair(it.key, dbType.toColumnSchema(it.value))
    }.toMap()

    return DataFrameSchemaImpl(
        columns = schemaColumns
    )
}

/**
 * Retrieves the metadata of the columns in the result set.
 *
 * @param [rs] the result set
 * @return a mutable map of column names to [TableColumnMetadata] objects,
 * where each TableColumnMetadata object contains information such as the column type,
 * JDBC type, size, and name.
 */
private fun getTableColumnsMetadata(rs: ResultSet): MutableMap<String, TableColumnMetadata> {
    val metaData: ResultSetMetaData = rs.metaData
    val numberOfColumns: Int = metaData.columnCount

    val tableColumns = mutableMapOf<String, TableColumnMetadata>()

    for (i in 1 until numberOfColumns + 1) {
        val name = metaData.getColumnName(i)
        val size = metaData.getColumnDisplaySize(i)
        val type = metaData.getColumnTypeName(i)
        val jdbcType = metaData.getColumnType(i)

        // TODO: add strategy for multiple columns handling (throw exception, ignore, create columns with addtional indexes in name)
        // column names should be unique
        check(!tableColumns.containsKey(name)) { "Multiple columns with name $name from table ${metaData.getTableName(i)}. Rename columns to make it unique." }

        tableColumns += Pair(name, TableColumnMetadata(name, type, jdbcType, size))

    }
    return tableColumns
}

/**
 * Retrieves the metadata of columns for a given table.
 *
 * @param [connection] the database connection
 * @param [tableName] the name of the table
 * @return a mutable map of column names to [TableColumnMetadata] objects,
 * where each TableColumnMetadata object contains information such as the column type,
 * JDBC type, size, and name.
 */
private fun getTableColumnsMetadata(connection: Connection, tableName: String): MutableMap<String, TableColumnMetadata> {
    val dbMetaData: DatabaseMetaData = connection.metaData
    val columns: ResultSet = dbMetaData.getColumns(null, null, tableName, null)
    val tableColumns = mutableMapOf<String, TableColumnMetadata>()

    while (columns.next()) {
        val name = columns.getString("COLUMN_NAME")
        val type = columns.getString("TYPE_NAME")
        val jdbcType = columns.getInt("DATA_TYPE")
        val size = columns.getInt("COLUMN_SIZE")
        tableColumns += Pair(name, TableColumnMetadata(name, type, jdbcType, size))
    }
    return tableColumns
}

/**
 * Fetches and converts data from a ResultSet into a mutable map.
 *
 * @param [tableColumns] a map containing the column metadata for the table.
 * @param [rs] the ResultSet object containing the data to be fetched and converted.
 * @param [dbType] the type of the database.
 * @param [limit] the maximum number of rows to fetch and convert.
 * @return A mutable map containing the fetched and converted data.
 */
private fun fetchAndConvertDataFromResultSet(
    tableColumns: MutableMap<String, TableColumnMetadata>,
    rs: ResultSet,
    dbType: DbType,
    limit: Int
): MutableMap<String, MutableList<Any?>> {
    // map<columnName; columndata>
    val data = mutableMapOf<String, MutableList<Any?>>()

    // init data
    tableColumns.forEach { (columnName, _) ->
        data[columnName] = mutableListOf()
    }

    var counter = 0

    if (limit > 0) {
        while (rs.next() && counter < limit) {
            handleRow(tableColumns, data, dbType, rs)
            counter++
            // if (counter % 1000 == 0) logger.debug { "Loaded $counter rows." } // TODO: https://github.com/Kotlin/dataframe/issues/455
        }
    } else {
        while (rs.next()) {
            handleRow(tableColumns, data, dbType, rs)
            counter++
            // if (counter % 1000 == 0) logger.debug { "Loaded $counter rows." } // TODO: https://github.com/Kotlin/dataframe/issues/455
        }
    }

    return data
}

private fun handleRow(
    tableColumns: MutableMap<String, TableColumnMetadata>,
    data: MutableMap<String, MutableList<Any?>>,
    dbType: DbType,
    rs: ResultSet
) {
    tableColumns.forEach { (columnName, jdbcColumn) ->
        data[columnName] = (data[columnName]!! + dbType.convertDataFromResultSet(rs, jdbcColumn)).toMutableList()
    }
}



