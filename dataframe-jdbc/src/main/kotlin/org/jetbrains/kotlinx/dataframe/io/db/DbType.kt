package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.asValueColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
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
import kotlin.collections.toTypedArray
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.safeCast
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

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

    /** Default mapping of [Java SQL Types][Types] to [KType]. */
    protected val defaultJdbcTypeToKTypeMapping: Map<Int, KType> = mapOf(
        Types.BIT to typeOf<Boolean>(),
        Types.TINYINT to typeOf<Int>(),
        Types.SMALLINT to typeOf<Int>(),
        Types.INTEGER to typeOf<Int>(),
        Types.BIGINT to typeOf<Long>(),
        Types.FLOAT to typeOf<Float>(),
        Types.REAL to typeOf<Float>(),
        Types.DOUBLE to typeOf<Double>(),
        Types.NUMERIC to typeOf<BigDecimal>(),
        Types.DECIMAL to typeOf<BigDecimal>(),
        Types.CHAR to typeOf<String>(),
        Types.VARCHAR to typeOf<String>(),
        Types.LONGVARCHAR to typeOf<String>(),
        Types.DATE to typeOf<Date>(),
        Types.TIME to typeOf<Time>(),
        Types.TIMESTAMP to typeOf<Timestamp>(),
        Types.BINARY to typeOf<ByteArray>(),
        Types.VARBINARY to typeOf<ByteArray>(),
        Types.LONGVARBINARY to typeOf<ByteArray>(),
        Types.NULL to typeOf<String>(),
        Types.JAVA_OBJECT to typeOf<Any>(),
        Types.DISTINCT to typeOf<Any>(),
        Types.STRUCT to typeOf<Any>(),
        Types.ARRAY to typeOf<Array<*>>(),
        Types.BLOB to typeOf<ByteArray>(),
        Types.CLOB to typeOf<Clob>(),
        Types.REF to typeOf<Ref>(),
        Types.DATALINK to typeOf<Any>(),
        Types.BOOLEAN to typeOf<Boolean>(),
        Types.ROWID to typeOf<RowId>(),
        Types.NCHAR to typeOf<String>(),
        Types.NVARCHAR to typeOf<String>(),
        Types.LONGNVARCHAR to typeOf<String>(),
        Types.NCLOB to typeOf<NClob>(),
        Types.SQLXML to typeOf<SQLXML>(),
        Types.REF_CURSOR to typeOf<Ref>(),
        Types.TIME_WITH_TIMEZONE to typeOf<OffsetTime>(),
        Types.TIMESTAMP_WITH_TIMEZONE to typeOf<OffsetDateTime>(),
    )

    private val typeInformationCache = mutableMapOf<TableColumnMetadata, AnyTypeInformation>()

    /**
     * Returns a [TypeInformation] produced from [tableColumnMetadata].
     */
    public fun getOrGenerateTypeInformation(tableColumnMetadata: TableColumnMetadata): AnyTypeInformation =
        typeInformationCache.getOrPut(tableColumnMetadata) { generateTypeInformation(tableColumnMetadata) }

    /**
     * Returns a [TypeInformation] produced from [tableColumnMetadata].
     *
     * This function can be overridden by returning your own [TypeInformation] or a subtype of that.
     * Do note that this class needs to be stateless, so this function can be memoized.
     */
    public open fun generateTypeInformation(tableColumnMetadata: TableColumnMetadata): AnyTypeInformation {
        val kType = when {
            tableColumnMetadata.jdbcType == Types.OTHER ->
                when (tableColumnMetadata.javaClassName) {
                    "[B" -> typeOf<ByteArray>()
                    else -> typeOf<Any>()
                }

            tableColumnMetadata.javaClassName == "[B" -> typeOf<ByteArray>()

            tableColumnMetadata.javaClassName == "java.sql.Blob" -> typeOf<Blob>()

            tableColumnMetadata.jdbcType == Types.TIMESTAMP &&
                tableColumnMetadata.javaClassName == "java.time.LocalDateTime" -> typeOf<LocalDateTime>()

            tableColumnMetadata.jdbcType == Types.BINARY &&
                tableColumnMetadata.javaClassName == "java.util.UUID" -> typeOf<UUID>()

            tableColumnMetadata.jdbcType == Types.REAL &&
                tableColumnMetadata.javaClassName == "java.lang.Double" -> typeOf<Double>()

            tableColumnMetadata.jdbcType == Types.FLOAT &&
                tableColumnMetadata.javaClassName == "java.lang.Double" -> typeOf<Double>()

            tableColumnMetadata.jdbcType == Types.NUMERIC &&
                tableColumnMetadata.javaClassName == "java.lang.Double" -> typeOf<Double>()

            // Force BIGINT to always be Long, regardless of javaClassName
            // Some JDBC drivers (e.g., MariaDB) may report Integer for small BIGINT values
            // TODO: tableColumnMetadata.jdbcType == Types.BIGINT -> typeOf<Long>()

            else -> defaultJdbcTypeToKTypeMapping[tableColumnMetadata.jdbcType]
                ?: typeOf<String>()
        }

        // TODO add preprocessors for common types, like sql Arrays, Java datetimes, etc.

        val postprocessor =
            when (tableColumnMetadata.jdbcType) {
                Types.ARRAY ->
                    DbColumnPostprocessor<Array<*>, Any> { column, _ ->
                        handleArrayValues(column.asValueColumn())
                    }

                else -> null
            }

        return typeInformationWithPostprocessingFor(
            jdbcSourceType = kType.withNullability(tableColumnMetadata.isNullable),
            targetSchema = ColumnSchema.Value(kType.withNullability(tableColumnMetadata.isNullable)),
            columnPostprocessor = postprocessor?.castToAny(),
        )
    }

    /**
     * Extracts a value from the ResultSet for the given column.
     * This method can be overridden by custom database types to change low-level reading logic.
     *
     * @param [rs] the ResultSet to read from
     * @param [columnIndex] zero-based column index
     * @param [typeInformation]
     * @return the extracted value, or null
     */
    public open fun <J : Any> getValueFromResultSet(
        rs: ResultSet,
        columnIndex: Int,
        typeInformation: TypeInformation<J, *, *>,
    ): J? =
        try {
            rs.getObject(columnIndex + 1)
        } catch (_: Throwable) {
            // TODO?
            rs.getString(columnIndex + 1)
        } as J?

    public fun <J : Any, D : Any> preprocessValuesFromResultSet(
        value: J?,
        typeInformation: TypeInformation<J, D, *>,
    ): D? = typeInformation.preprocess(value)

    public open fun <D : Any> buildDataColumn(
        name: String,
        values: List<D?>,
        typeInformation: TypeInformation<*, D, *>,
        inferNullability: Boolean,
    ): DataColumn<D?> =
        when (val schema = typeInformation.targetSchema) {
            is ColumnSchema.Value ->
                DataColumn.createValueColumn(
                    name = name,
                    values = values,
                    infer = if (inferNullability) Infer.Nulls else Infer.None,
                    type = schema.type,
                )

            // TODO, this should be postponed to post-processing.
            //  List<AnyRow>.toDataFrame() is heavy!
            is ColumnSchema.Group ->
                DataColumn.createColumnGroup(
                    name = name,
                    df = (values as List<AnyRow>).toDataFrame(),
                ).asDataColumn().cast()

            is ColumnSchema.Frame ->
                DataColumn.createFrameColumn(
                    name = name,
                    groups = values as List<AnyFrame>,
                    schema = lazy { schema.schema },
                ).cast()
        }

    public fun <D : Any, P : Any> postProcessDataColumn(
        column: DataColumn<D?>,
        typeInformation: TypeInformation<*, D, P>,
    ): DataColumn<P?> = typeInformation.postprocess(column)

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
     * Retrieves column metadata from a JDBC ResultSet.
     *
     * This method reads column metadata from [ResultSetMetaData] with graceful fallbacks
     * for JDBC drivers that throw [java.sql.SQLFeatureNotSupportedException] for certain methods
     * (e.g., Apache Hive).
     *
     * Fallback behavior for unsupported methods:
     * - `getColumnName()` → `getColumnLabel()` → `"column_N"`
     * - `getTableName()` → extract from column name if contains '.' → `null`
     * - `isNullable()` → [DatabaseMetaData.getColumns] → `true` (assume nullable)
     * - `getColumnTypeName()` → `"OTHER"`
     * - `getColumnType()` → [Types.OTHER]
     * - `getColumnDisplaySize()` → `0`
     * - `getColumnClassName()` → `"java.lang.Object"`
     *
     * Override this method in subclasses to provide database-specific behavior
     * (for example, to disable fallback for databases like Teradata or Oracle
     * where [DatabaseMetaData.getColumns] is known to be slow).
     *
     * @param resultSet The [ResultSet] containing query results.
     * @return A list of [TableColumnMetadata] objects.
     */
    public open fun getTableColumnsMetadata(resultSet: ResultSet): List<TableColumnMetadata> {
        val rsMetaData = resultSet.metaData
        val connection = resultSet.statement.connection
        val dbMetaData = connection.metaData

        // Some JDBC drivers (e.g., Hive) throw SQLFeatureNotSupportedException
        val catalog = try {
            connection.catalog.takeUnless { it.isNullOrBlank() }
        } catch (_: Exception) {
            null
        }

        val schema = try {
            connection.schema.takeUnless { it.isNullOrBlank() }
        } catch (_: Exception) {
            null
        }

        val columnCount = rsMetaData.columnCount
        val columns = mutableListOf<TableColumnMetadata>()
        val nameCounter = mutableMapOf<String, Int>()

        for (index in 1..columnCount) {
            // Try to getColumnName, fallback to getColumnLabel, then generate name
            val columnName = try {
                rsMetaData.getColumnName(index)
            } catch (_: Exception) {
                try {
                    rsMetaData.getColumnLabel(index)
                } catch (_: Exception) {
                    "column$index"
                }
            }

            // Some JDBC drivers (e.g., Apache Hive) throw SQLFeatureNotSupportedException
            val tableName = try {
                rsMetaData.getTableName(index).takeUnless { it.isBlank() }
            } catch (_: Exception) {
                // Fallback: try to extract table name from column name if it contains '.'
                val dotIndex = columnName.lastIndexOf('.')
                if (dotIndex > 0) columnName.take(dotIndex) else null
            }

            // Try to detect nullability from ResultSetMetaData
            val isNullable = try {
                when (rsMetaData.isNullable(index)) {
                    ResultSetMetaData.columnNoNulls -> false

                    ResultSetMetaData.columnNullable -> true

                    // Unknown nullability: assume it nullable, may trigger fallback
                    ResultSetMetaData.columnNullableUnknown -> true

                    else -> true
                }
            } catch (_: Exception) {
                // Some drivers may throw for unsupported features
                // Try fallback to DatabaseMetaData, with additional safety
                try {
                    dbMetaData.getColumns(catalog, schema, tableName, columnName).use { cols ->
                        if (cols.next()) !cols.getString("IS_NULLABLE").equals("NO", ignoreCase = true) else true
                    }
                } catch (_: Exception) {
                    // Fallback failed, assume nullable as the safest default
                    true
                }
            }

            // adding fallbacks to avoid SQLException
            val columnType = try {
                rsMetaData.getColumnTypeName(index)
            } catch (_: Exception) {
                "OTHER"
            }

            val jdbcType = try {
                rsMetaData.getColumnType(index)
            } catch (_: Exception) {
                Types.OTHER
            }

            val displaySize = try {
                rsMetaData.getColumnDisplaySize(index)
            } catch (_: Exception) {
                0
            }

            val javaClassName = try {
                rsMetaData.getColumnClassName(index)
            } catch (_: Exception) {
                "java.lang.Object"
            }

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

    /**
     * todo?
     * Converts SQL Array objects to strongly-typed arrays.
     *
     * Extracts arrays from SQL Array objects and converts them to a consistent type
     * if all elements share the same type. Returns original arrays if types vary.
     *
     * @param values raw values containing SQL Array objects
     * @return list of consistently typed arrays, or original arrays if no common type exists
     */
    private fun handleArrayValues(values: ValueColumn<Any?>): DataColumn<Any> {
        // Intermediate variable for the first mapping
        val sqlArrays = values.values().mapNotNull {
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
        }.toColumn(values.name())
    }

    /** Utility function to cast arrays based on the type of elements */
    private fun <T : Any> castArray(array: Array<*>, elementType: KClass<T>): List<T> =
        array.mapNotNull { elementType.safeCast(it) }
}
