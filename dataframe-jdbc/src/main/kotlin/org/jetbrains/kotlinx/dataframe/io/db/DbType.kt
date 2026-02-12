package org.jetbrains.kotlinx.dataframe.io.db

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
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
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.safeCast
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Instant
import kotlin.time.toKotlinInstant
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid
import java.time.LocalDateTime as JavaLocalDateTime
import java.util.Date as JavaDate

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
        Types.DATE to typeOf<JavaDate>(),
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
        val nameGenerator = ColumnNameGenerator()
        val columns = List(columnCount) {
            // SQL columns are 1-indexed
            val index = it + 1

            // Try to getColumnName, fallback to getColumnLabel, then generate name
            val columnName = try {
                rsMetaData.getColumnName(index)
            } catch (_: Exception) {
                try {
                    rsMetaData.getColumnLabel(index)
                } catch (_: Exception) {
                    null
                }
            }

            // Some JDBC drivers (e.g., Apache Hive) throw SQLFeatureNotSupportedException
            val tableName = try {
                rsMetaData.getTableName(index).takeUnless { it.isBlank() }
            } catch (_: Exception) {
                // Fallback: try to extract table name from column name if it contains '.'
                if (columnName?.contains('.') == true) {
                    columnName.substringAfterLast('.')
                } else {
                    null
                }
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

            // Generate DataFrame-compatible unique names in the same way as creating a DataFrame would
            val uniqueName = nameGenerator.addUnique(
                preferredName = columnName.orEmpty().ifEmpty { UNNAMED_COLUMN_PREFIX },
            )

            TableColumnMetadata(
                name = uniqueName,
                sqlTypeName = columnType,
                jdbcType = jdbcType,
                size = displaySize,
                javaClassName = javaClassName,
                isNullable = isNullable,
            )
        }

        return columns
    }

    public open fun getTableColumnsMetadata(
        connection: Connection,
        tableMetadata: TableMetadata,
    ): List<TableColumnMetadata> {
        val dbMetaData = connection.metaData
        return dbMetaData.getColumns(
            tableMetadata.catalogue,
            tableMetadata.schemaName,
            tableMetadata.name,
            null,
        ).use { rs ->
            buildList {
                val nameGenerator = ColumnNameGenerator()
                while (rs.next()) {
                    val columnName =
                        runCatching { rs.getString("COLUMN_NAME") }.getOrNull()
                            .orEmpty()
                            .ifEmpty { UNNAMED_COLUMN_PREFIX }
                            .let {
                                nameGenerator.addUnique(preferredName = it)
                            }
                    val sqlTypeName =
                        runCatching { rs.getString("TYPE_NAME") }.getOrNull()
                            ?: "OTHER"

                    val jdbcType =
                        runCatching { rs.getInt("DATA_TYPE") }.getOrNull()
                            .takeUnless { it == 0 }
                            ?: Types.OTHER

                    val isNullable =
                        runCatching { rs.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls }
                            .recoverCatching { !rs.getString("IS_NULLABLE").equals("NO", ignoreCase = true) }
                            .getOrDefault(true)

                    // TODO This can cause issues, for instance on H2. Needs watertight TYPE_NAME mapping
                    val javaClassName =
                        runCatching { rs.getString("COLUMN_CLASS") }.getOrNull()
                            ?: runCatching { rs.getString("UDT_CLASS_NAME") }.getOrNull()
                            ?: "java.lang.Object" // fallback; some drivers don’t expose class here

                    this += TableColumnMetadata(
                        name = columnName,
                        sqlTypeName = sqlTypeName,
                        jdbcType = jdbcType,
                        size = 0, // irrelevant when reading just the schema
                        javaClassName = javaClassName,
                        isNullable = isNullable,
                    )
                }
            }
        }
    }

    /**
     * Returns the [type][KType] of the objects returned by [getValueFromResultSet]
     * for the given [column][tableColumnMetadata]. Also called type `J`.
     *
     * While [DbType] contains a basic type mapping,
     * it's often necessary to override this function for specific JDBC implementations,
     * as each implementation can deviate from the standard type mapping and may return
     * unexpected types when [ResultSet.getObject] is called.
     *
     * @param [tableColumnMetadata] all information we have about the column
     * @return the type of the objects returned by [getValueFromResultSet] and
     *   [ResultSet.getObject] for the given column
     */
    public open fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType {
        val kType = when (tableColumnMetadata.jdbcType) {
            Types.OTHER ->
                when (tableColumnMetadata.javaClassName) {
                    "[B" -> typeOf<ByteArray>()
                    else -> typeOf<Any>()
                }

            Types.TIMESTAMP if tableColumnMetadata.javaClassName == "java.time.LocalDateTime" ->
                typeOf<JavaLocalDateTime>()

            Types.BINARY if tableColumnMetadata.javaClassName == "java.util.UUID" ->
                typeOf<UUID>()

            Types.REAL if tableColumnMetadata.javaClassName == "java.lang.Double" ->
                typeOf<Double>()

            Types.FLOAT if tableColumnMetadata.javaClassName == "java.lang.Double" ->
                typeOf<Double>()

            Types.NUMERIC if tableColumnMetadata.javaClassName == "java.lang.Double" ->
                typeOf<Double>()

            // Force BIGINT to always be Long, regardless of javaClassName
            // Some JDBC drivers (e.g., MariaDB) may report Integer for small BIGINT values
            // TODO: tableColumnMetadata.jdbcType == Types.BIGINT -> typeOf<Long>()

            else if tableColumnMetadata.javaClassName == "[B" ->
                typeOf<ByteArray>()

            else if tableColumnMetadata.javaClassName == "java.sql.Blob" ->
                typeOf<Blob>()

            else ->
                defaultJdbcTypeToKTypeMapping[tableColumnMetadata.jdbcType]
                    ?: typeOf<String>()
        }

        return kType.withNullability(tableColumnMetadata.isNullable)
    }

    /**
     * Extracts a value from the [result set][rs] for the given [column][tableColumnMetadata].
     *
     * This method can be overridden for custom database types to change low-level reading logic.
     *
     * The return value, of type [J], must match the [expectedJdbcType] parameter exactly.
     * This parameter is obtained from [getExpectedJdbcType].
     *
     * @param [J] the JDBC type, [expectedJdbcType]
     * @param [rs] the ResultSet to read from
     * @param [columnIndex] zero-based column index
     * @param [tableColumnMetadata] all information we have about the column
     * @param [expectedJdbcType] the type of the return value, [J], obtained from [getExpectedJdbcType]
     * @return the value extracted from the [result set][rs] for the given [column index][columnIndex]
     */
    @Suppress("UNCHECKED_CAST")
    public open fun <J> getValueFromResultSet(
        rs: ResultSet,
        columnIndex: Int,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
    ): J =
        try {
            rs.getObject(columnIndex + 1)
        } catch (_: Throwable) {
            // TODO?
            rs.getString(columnIndex + 1)
        } as J

    /**
     * Returns the [type][KType] of the objects returned by [preprocessValue]
     * for the given [column][tableColumnMetadata]. Also called type `D`.
     *
     * This is the type of the value after preprocessing the individual value,
     * which may differ from the JDBC type `J`.
     *
     * @param [tableColumnMetadata] all information we have about the column
     * @param [expectedJdbcType] the JDBC type, `J`, obtained from [getExpectedJdbcType]
     * @return the type of the objects returned by [preprocessValue] for the given column
     */
    public open fun getPreprocessedValueType(
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
    ): KType =
        when (tableColumnMetadata.jdbcType) {
            Types.TIMESTAMP if tableColumnMetadata.javaClassName == "java.time.LocalDateTime" ->
                typeOf<LocalDateTime>()

            Types.TIMESTAMP ->
                typeOf<Instant>()

            Types.BINARY if tableColumnMetadata.javaClassName == "java.util.UUID" ->
                typeOf<Uuid>()

            else ->
                expectedJdbcType
        }.withNullability(tableColumnMetadata.isNullable)

    /**
     * (Potentially) preprocesses the [value] for the given [column][tableColumnMetadata] before
     * collecting it in a DataFrame [DataColumn].
     *
     * While [DbType] contains some basic preprocessing logic, converting some Java classes to Kotlin ones,
     * it's often necessary to override this function for specific JDBC implementations.
     *
     * @param [J] the JDBC type, [expectedJdbcType]
     * @param [D] the type of the return value, [expectedPreprocessedValueType]
     * @param [value] the value to preprocess
     * @param [tableColumnMetadata] all information we have about the column
     * @param [expectedJdbcType] the JDBC type, [J], obtained from [getExpectedJdbcType]
     * @param [expectedPreprocessedValueType] the type of the return value, [D], obtained from [getPreprocessedValueType]
     * @return the preprocessed version of [value]
     */
    @Suppress("UNCHECKED_CAST")
    public open fun <J, D> preprocessValue(
        value: J,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
        expectedPreprocessedValueType: KType,
    ): D =
        when (tableColumnMetadata.jdbcType) {
            Types.TIMESTAMP if tableColumnMetadata.javaClassName == "java.time.LocalDateTime" ->
                (value as JavaLocalDateTime?)?.toKotlinLocalDateTime()

            Types.TIMESTAMP ->
                (value as Timestamp?)?.toInstant()?.toKotlinInstant()

            Types.BINARY if tableColumnMetadata.javaClassName == "java.util.UUID" ->
                (value as UUID?)?.toKotlinUuid()

            else ->
                value
        } as D

    /**
     * Returns the target [schema][ColumnSchema] of the given [column][tableColumnMetadata]
     * which [buildDataColumn] will adhere to. This schema corresponds to type `P`, in the sense that
     * it will describe the [schema][ColumnSchema] of `DataColumn<P>`.
     *
     * If `null` is returned, the [schema][ColumnSchema] cannot be determined before looking at the actual data.
     *
     * @param [tableColumnMetadata] all information we have about the column
     * @param [expectedValueType] the type of the values after preprocessing, `P`
     * @return the target [schema][ColumnSchema] of the given column,
     *   or `null` if it cannot be determined from the types alone.
     */
    public open fun getTargetColumnSchema(
        tableColumnMetadata: TableColumnMetadata,
        expectedValueType: KType,
    ): ColumnSchema? =
        when (tableColumnMetadata.jdbcType) {
            // buildDataColumn post-processes java.sql.Array -> Kotlin arrays, making the result type `Array<*>`
            Types.ARRAY -> ColumnSchema.Value(typeOf<Array<*>>().withNullability(expectedValueType.isMarkedNullable))

            else -> ColumnSchema.Value(expectedValueType)
        }

    /**
     * Builds a [DataColumn] from the given ([preprocessed][preprocessValue]) [values],
     * adhering to [targetColumnSchema].
     *
     * @param [D] the type of the values after preprocessing
     * @param [P] the type of the resulting [DataColumn][DataColumn]`<`[P][P]`>`, [targetColumnSchema]
     * @param [name] the name of the column
     * @param [values] the ([preprocessed][preprocessValue]) values to put in the column
     * @param [tableColumnMetadata] all information we have about the column
     * @param [targetColumnSchema] the schema of the column [DataColumn][DataColumn]`<`[P][P]`>`,
     *   as determined by [getTargetColumnSchema]
     * @param [inferNullability] whether to infer nullability from the runtime values (this is more expensive),
     *   as opposed to using the nullability information from the [targetColumnSchema]
     * @return the built [DataColumn]
     */
    public open fun <D, P> buildDataColumn(
        name: String,
        values: List<D>,
        tableColumnMetadata: TableColumnMetadata,
        targetColumnSchema: ColumnSchema?,
        inferNullability: Boolean,
    ): DataColumn<P> {
        val postProcessedValues = when (tableColumnMetadata.jdbcType) {
            // Special case which post-processes java.sql.Array -> Kotlin arrays
            Types.ARRAY -> handleArrayValues(values)

            else -> values
        }
        return postProcessedValues.toDataColumn(
            name = name,
            targetColumnSchema = targetColumnSchema,
            inferNullability = inferNullability,
        )
    }

    /**
     * Helper function to convert [this] list of values to a [DataColumn][DataColumn]`<`[P][P]`>`.
     *
     * **NOTE:** While this function can handle
     * [targetColumnSchema][targetColumnSchema]`  =  `[ColumnSchema.Group][ColumnSchema.Group],
     * and [this] being a [List][List]`<`[`DataRow<*>`][DataRow]`>`,
     * this should generally be avoided to circumvent creating `n` [data rows][DataRow],
     * (which essentially are `n` single-row [dataframes][DataFrame]).
     *
     * Instead, use [preprocessValue][preprocessValue] to convert to [Map][Map]`<`[String][String]`, `[Any?][Any]`>`
     * and then use the more efficient [Iterable<Map<String, Any?>>.toDataFrame()][Iterable.toDataFrame] in [buildDataColumn]:
     * ```kt
     * (values as List<Map<String, Any?>>)
     *     .toDataFrame()
     *     .asColumnGroup(name)
     *     .asDataColumn()
     * ```
     */
    protected fun <D, P> List<D>.toDataColumn(
        name: String,
        targetColumnSchema: ColumnSchema?,
        inferNullability: Boolean,
    ): DataColumn<P> =
        when (targetColumnSchema) {
            is ColumnSchema.Value ->
                DataColumn.createValueColumn(
                    name = name,
                    values = this,
                    infer = if (inferNullability) Infer.Nulls else Infer.None,
                    type = targetColumnSchema.type,
                ).cast()

            // NOTE: this case should be avoided.
            //  Creating `n` DataRows is heavy!
            is ColumnSchema.Group ->
                DataColumn.createColumnGroup(
                    name = name,
                    df = (this as List<AnyRow>).toDataFrame(),
                ).asDataColumn().cast()

            is ColumnSchema.Frame ->
                DataColumn.createFrameColumn(
                    name = name,
                    groups = this as List<AnyFrame>,
                    schema = lazy { targetColumnSchema.schema },
                ).cast()

            null ->
                DataColumn.createByInference(
                    name = name,
                    values = this,
                ).cast()
        }

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
     * todo?
     * Converts SQL Array objects to strongly-typed arrays.
     *
     * Extracts arrays from SQL Array objects and converts them to a consistent type
     * if all elements share the same type. Returns original arrays if types vary.
     *
     * @param values raw values containing SQL Array objects
     * @return list of consistently typed arrays, or original arrays if no common type exists
     */
    private fun handleArrayValues(values: List<Any?>): List<Array<*>?> {
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
}

// same as org.jetbrains.kotlinx.dataframe.impl.UNNAMED_COLUMN_PREFIX
internal const val UNNAMED_COLUMN_PREFIX = "untitled"
