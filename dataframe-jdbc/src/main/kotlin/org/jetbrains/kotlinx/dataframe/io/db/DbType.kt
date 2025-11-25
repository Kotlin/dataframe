package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.readAllSqlTables
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.math.BigDecimal
import java.sql.Blob
import java.sql.Clob
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.NClob
import java.sql.PreparedStatement
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
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.safeCast
import kotlin.reflect.full.starProjectedType

/**
 * The `DbType` class represents a database type used for reading dataframe from the database.
 *
 * @property [dbTypeInJdbcUrl] The name of the database as specified in the JDBC URL.
 */
public abstract class DbType(public val dbTypeInJdbcUrl: String) {
    /**
     * Represents the JDBC driver class name for a given database type.
     *
     * NOTE: It's important for usage in dataframe-gradle-plugin for force class loading.
     *
     * @return The JDBC driver class name as a [String].
     */
    public abstract val driverClassName: String

    /**
     * The table type(s) (`TABLE_TYPE`) of ordinary tables in the SQL database, used by
     * [readAllSqlTables], and [readAllSqlTables] as a filter when querying the database
     * for all the tables it has using [DatabaseMetaData.getTables].
     *
     * This is usually "TABLE" or "BASE TABLE", which is what [tableTypes] is set to by default,
     * but it can be overridden to any custom list of table types, or `null` to let the JDBC integration
     * return all types of tables.
     *
     * See [DatabaseMetaData.getTableTypes] for all supported table types of your specific database.
     */
    public open val tableTypes: List<String>? = listOf("TABLE", "BASE TABLE")

    /**
     * Specifies the default batch size for fetching rows from the database during query execution.
     *
     * This property determines how many rows are fetched in a single batch from the database.
     * A proper fetch size can improve performance by reducing the number of network round-trips required
     * when handling large result sets.
     *
     * Value is set to 1000 by default, but it can be overridden based on database-specific requirements
     * or performance considerations.
     */
    public open val defaultFetchSize: Int = 1000

    /**
     * Specifies the default timeout in seconds for database queries.
     *
     * If set to `null`, no timeout is applied, allowing queries to run indefinitely.
     * This property can be used to set a default query timeout for the database type,
     * which can help manage long-running queries.
     */
    public open val defaultQueryTimeout: Int? = null // null = no timeout

    /**
     * Returns a [ColumnSchema] produced from [tableColumnMetadata].
     */
    public abstract fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema?

    /**
     * Checks if the given table name is a system table for the specified database type.
     *
     * @param [tableMetadata] the table object representing the table from the database.
     * @return True if the table is a system table for the specified database type, false otherwise.
     */
    public abstract fun isSystemTable(tableMetadata: TableMetadata): Boolean

    /**
     * Builds the table metadata based on the database type and the ResultSet from the query.
     *
     * @param [tables] the ResultSet containing the table's meta-information.
     * @return the TableMetadata object representing the table metadata.
     */
    public abstract fun buildTableMetadata(tables: ResultSet): TableMetadata

    /**
     * Converts SQL data type to a Kotlin data type.
     *
     * @param [tableColumnMetadata] The metadata of the table column.
     * @return The corresponding Kotlin data type, or null if no mapping is found.
     */
    public abstract fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType?

    /**
     * Builds a SELECT query for reading from a table.
     *
     * @param [tableName] the name of the table to query.
     * @param [limit] the maximum number of rows to retrieve. If 0 or negative, no limit is applied.
     * @return the SQL query string.
     */
    public open fun buildSelectTableQueryWithLimit(tableName: String, limit: Int?): String {
        require(tableName.isNotBlank()) { "Table name cannot be blank" }

        val quotedTableName = quoteIdentifier(tableName)

        return if (limit != null && limit > 0) {
            buildSqlQueryWithLimit("SELECT * FROM $quotedTableName", limit)
        } else {
            "SELECT * FROM $quotedTableName"
        }
    }

    /**
     * Configures the provided `PreparedStatement` for optimized read operations.
     *
     * This method sets the fetch size for efficient streaming, applies a query timeout if specified,
     * and configures the fetch direction to forward-only for better performance in read-only operations.
     *
     * @param statement the `PreparedStatement` to be configured
     */
    public open fun configureReadStatement(statement: PreparedStatement) {
        // Set fetch size for better streaming performance
        statement.fetchSize = defaultFetchSize

        defaultQueryTimeout?.let {
            statement.queryTimeout = it
        }

        // Set the fetch direction (forward-only for read-only operations)
        statement.fetchDirection = ResultSet.FETCH_FORWARD
    }

    /**
     * Quotes an identifier (table or column name) according to database-specific rules.
     *
     * Examples:
     * - PostgreSQL: "tableName" or "schema"."table"
     * - MySQL: `tableName` or `schema`.`table`
     * - MS SQL: `[tableName]` or `[schema].[table]`
     * - SQLite/H2: no quotes for simple names
     *
     * @param [name] the identifier to quote (can contain dots for schema.table).
     * @return the quoted identifier.
     */
    public open fun quoteIdentifier(name: String): String {
        require(name.isNotBlank()) { "Identifier cannot be blank" }

        // Default: no quoting (works for SQLite, H2, simple names)
        return name
    }

    /**
     * Constructs a SQL query with a limit clause.
     *
     * @param sqlQuery The original SQL query.
     * @param limit The maximum number of rows to retrieve from the query. Default is 1.
     * @return A new SQL query with the limit clause added.
     */
    public open fun buildSqlQueryWithLimit(sqlQuery: String, limit: Int = 1): String = "$sqlQuery LIMIT $limit"

    /**
     * Creates a database connection using the provided configuration.
     * This method is only called when working with [DbConnectionConfig] (internally managed connections).
     *
     * Some databases (like [Sqlite]) require read-only mode to be set during connection creation
     * rather than after the connection is established.
     *
     * @param [dbConfig] The database configuration containing URL, credentials, and read-only flag.
     * @return A configured [Connection] instance.
     */
    public open fun createConnection(dbConfig: DbConnectionConfig): Connection {
        val connection = DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password)
        if (dbConfig.readOnly) {
            connection.isReadOnly = true
        }
        return connection
    }

    /**
     * Extracts a value from the ResultSet for the given column.
     * This method can be overridden by custom database types to provide specialized parsing logic.
     *
     * @param [rs] the ResultSet to read from
     * @param [columnIndex] zero-based column index
     * @param [columnMetadata] metadata for the column
     * @param [kType] the Kotlin type for this column
     * @return the extracted value, or null
     */
    public open fun extractValueFromResultSet(
        rs: ResultSet,
        columnIndex: Int,
        columnMetadata: TableColumnMetadata,
        kType: KType,
    ): Any? =
        try {
            rs.getObject(columnIndex + 1)
            // TODO: add a special handler for Blob via Streams
        } catch (_: Throwable) {
            // TODO: expand for all the types like in generateKType function
            if (kType.isSupertypeOf(String::class.starProjectedType)) {
                rs.getString(columnIndex + 1)
            } else {
                rs.getString(columnIndex + 1)
            }
        }

    /**
     * Builds a single DataColumn with proper type handling.
     * Accepts a mutable list to allow efficient post-processing.
     */
    public open fun buildDataColumn(
        name: String,
        values: MutableList<Any?>,
        kType: KType,
        inferNullability: Boolean,
    ): DataColumn<*> {
        val correctedValues = postProcessColumnValues(values, kType)

        return DataColumn.createValueColumn(
            name = name,
            values = correctedValues,
            infer = convertNullabilityInference(inferNullability),
            type = kType,
        )
    }

    private fun convertNullabilityInference(inferNullability: Boolean) =
        if (inferNullability) Infer.Nulls else Infer.None

    /**
     * Processes the column values retrieved from the database and performs transformations based on the provided
     * Kotlin type and column metadata. The method allows for custom post-processing logic, such as handling
     * specific database column types, including arrays.
     *
     * @param values the list of raw values retrieved from the database for the column.
     * @param kType the Kotlin type that the column values should be transformed to.
     * @return a list of processed column values, with transformations applied where necessary, or the original list if no transformation is needed.
     */
    private fun postProcessColumnValues(values: MutableList<Any?>, kType: KType): List<Any?> =
        when {
            /* EXAMPLE: columnMetadata.sqlTypeName == "MY_CUSTOM_ARRAY" -> {
                values.map { /* custom transformation */ }
            } */
            kType.classifier == Array::class -> {
                handleArrayValues(values)
            }

            else -> values
        }

    /**
     * Converts SQL Array objects to strongly-typed arrays.
     *
     * Extracts arrays from SQL Array objects and converts them to a consistent type
     * if all elements share the same type. Returns original arrays if types vary.
     *
     * @param values raw values containing SQL Array objects
     * @return list of consistently typed arrays, or original arrays if no common type exists
     */
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

    /** Utility function to cast arrays based on the type of elements */
    private fun <T : Any> castArray(array: Array<*>, elementType: KClass<T>): List<T> =
        array.mapNotNull { elementType.safeCast(it) }

    /**
     * Creates a mapping between common SQL types and their corresponding KTypes.
     *
     * @param tableColumnMetadata The metadata of the table column.
     * @return The KType associated with the SQL type or a default type if no mapping is found.
     */
    public open fun makeCommonSqlToKTypeMapping(tableColumnMetadata: TableColumnMetadata): KType {
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

    /**
     * Retrieves column metadata from a JDBC ResultSet.
     *
     * By default, this method reads column metadata from [ResultSetMetaData],
     * which is fast and supported by most JDBC drivers.
     * If the driver does not provide sufficient information (e.g., `isNullable` unknown),
     * it falls back to using [DatabaseMetaData.getColumns] for affected columns.
     *
     * Override this method in subclasses to provide database-specific behavior
     * (for example, to disable fallback for databases like Teradata or Oracle
     * where `DatabaseMetaData.getColumns` is known to be slow).
     *
     * @param resultSet The [ResultSet] containing query results.
     * @return A list of [TableColumnMetadata] objects.
     */
    public open fun getTableColumnsMetadata(resultSet: ResultSet): List<TableColumnMetadata> {
        val rsMetaData = resultSet.metaData
        val connection = resultSet.statement.connection
        val dbMetaData = connection.metaData
        val catalog = connection.catalog.takeUnless { it.isNullOrBlank() }
        val schema = connection.schema.takeUnless { it.isNullOrBlank() }

        val columnCount = rsMetaData.columnCount
        val columns = mutableListOf<TableColumnMetadata>()
        val nameCounter = mutableMapOf<String, Int>()

        for (index in 1..columnCount) {
            val columnName = rsMetaData.getColumnName(index)
            val tableName = rsMetaData.getTableName(index)

            // Try to detect nullability from ResultSetMetaData
            val isNullable = try {
                when (rsMetaData.isNullable(index)) {
                    ResultSetMetaData.columnNoNulls -> false

                    ResultSetMetaData.columnNullable -> true

                    ResultSetMetaData.columnNullableUnknown -> {
                        // Unknown nullability: assume it nullable, may trigger fallback
                        true
                    }

                    else -> true
                }
            } catch (_: Exception) {
                // Some drivers may throw for unsupported features
                // In that case, fallback to DatabaseMetaData
                dbMetaData.getColumns(catalog, schema, tableName, columnName).use { cols ->
                    if (cols.next()) !cols.getString("IS_NULLABLE").equals("NO", ignoreCase = true) else true
                }
            }

            val columnType = rsMetaData.getColumnTypeName(index)
            val jdbcType = rsMetaData.getColumnType(index)
            val displaySize = rsMetaData.getColumnDisplaySize(index)
            val javaClassName = rsMetaData.getColumnClassName(index)

            val uniqueName = manageColumnNameDuplication(nameCounter, columnName)

            columns += TableColumnMetadata(
                uniqueName,
                columnType,
                jdbcType,
                displaySize,
                javaClassName,
                isNullable,
            )
        }

        return columns
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
}
