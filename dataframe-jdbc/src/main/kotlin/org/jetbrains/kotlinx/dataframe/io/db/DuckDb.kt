package org.jetbrains.kotlinx.dataframe.io.db

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalTime
import org.duckdb.DuckDBColumnType.ARRAY
import org.duckdb.DuckDBColumnType.BIGINT
import org.duckdb.DuckDBColumnType.BIT
import org.duckdb.DuckDBColumnType.BLOB
import org.duckdb.DuckDBColumnType.BOOLEAN
import org.duckdb.DuckDBColumnType.DATE
import org.duckdb.DuckDBColumnType.DECIMAL
import org.duckdb.DuckDBColumnType.DOUBLE
import org.duckdb.DuckDBColumnType.ENUM
import org.duckdb.DuckDBColumnType.FLOAT
import org.duckdb.DuckDBColumnType.HUGEINT
import org.duckdb.DuckDBColumnType.INTEGER
import org.duckdb.DuckDBColumnType.INTERVAL
import org.duckdb.DuckDBColumnType.JSON
import org.duckdb.DuckDBColumnType.LIST
import org.duckdb.DuckDBColumnType.MAP
import org.duckdb.DuckDBColumnType.SMALLINT
import org.duckdb.DuckDBColumnType.STRUCT
import org.duckdb.DuckDBColumnType.TIME
import org.duckdb.DuckDBColumnType.TIMESTAMP
import org.duckdb.DuckDBColumnType.TIMESTAMP_MS
import org.duckdb.DuckDBColumnType.TIMESTAMP_NS
import org.duckdb.DuckDBColumnType.TIMESTAMP_S
import org.duckdb.DuckDBColumnType.TIMESTAMP_WITH_TIME_ZONE
import org.duckdb.DuckDBColumnType.TIME_WITH_TIME_ZONE
import org.duckdb.DuckDBColumnType.TINYINT
import org.duckdb.DuckDBColumnType.UBIGINT
import org.duckdb.DuckDBColumnType.UHUGEINT
import org.duckdb.DuckDBColumnType.UINTEGER
import org.duckdb.DuckDBColumnType.UNION
import org.duckdb.DuckDBColumnType.UNKNOWN
import org.duckdb.DuckDBColumnType.USMALLINT
import org.duckdb.DuckDBColumnType.UTINYINT
import org.duckdb.DuckDBColumnType.UUID
import org.duckdb.DuckDBColumnType.VARCHAR
import org.duckdb.DuckDBResultSetMetaData
import org.duckdb.JsonNode
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.readAllSqlTables
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Blob
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Struct
import java.util.Properties
import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Instant
import kotlin.time.toKotlinInstant
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid
import java.sql.Array as SqlArray
import java.sql.Timestamp as SqlTimestamp
import java.time.LocalDate as JavaLocalDate
import java.time.LocalTime as JavaLocalTime
import java.time.OffsetDateTime as JavaOffsetDateTime
import java.time.OffsetTime as JavaOffsetTime
import java.util.UUID as JavaUUID

private val logger = KotlinLogging.logger {}

/**
 * Represents the [DuckDB](http://duckdb.org/) database type.
 *
 * This class provides methods to convert data from a [ResultSet] to the appropriate type for DuckDB,
 * and to generate the corresponding [column schema][ColumnSchema].
 */
public object DuckDb : AdvancedDbType("duckdb") {

    /** the name of the class of the DuckDB JDBC driver */
    override val driverClassName: String = "org.duckdb.DuckDBDriver"

    override fun generateConverter(tableColumnMetadata: TableColumnMetadata): AnyJdbcToDataFrameConverter =
        parseDuckDbType(tableColumnMetadata.sqlTypeName, tableColumnMetadata.isNullable)

    private val duckDbTypeCache = mutableMapOf<Pair<String, Boolean>, AnyJdbcToDataFrameConverter>()

    /**
     * How a column type from JDBC, [sqlTypeName], is read in Java/Kotlin.
     * The returned type must exactly follow [ResultSet.getObject] of your specific database's JDBC driver.
     * Returning `null` defer the implementation to the default one (which may not always be correct).
     *
     * Following [org.duckdb.DuckDBVector.getObject] and converting the result to
     *
     */
    internal fun parseDuckDbType(sqlTypeName: String, isNullable: Boolean): AnyJdbcToDataFrameConverter =
        duckDbTypeCache.getOrPut(Pair(sqlTypeName, isNullable)) {
            when (DuckDBResultSetMetaData.TypeNameToType(sqlTypeName)) {
                BOOLEAN -> jdbcToDfConverterForValueColumnOf<Boolean>(isNullable)

                TINYINT -> jdbcToDfConverterForValueColumnOf<Byte>(isNullable)

                SMALLINT -> jdbcToDfConverterForValueColumnOf<Short>(isNullable)

                INTEGER -> jdbcToDfConverterForValueColumnOf<Int>(isNullable)

                BIGINT -> jdbcToDfConverterForValueColumnOf<Long>(isNullable)

                HUGEINT -> jdbcToDfConverterForValueColumnOf<BigInteger>(isNullable)

                UHUGEINT -> jdbcToDfConverterForValueColumnOf<BigInteger>(isNullable)

                UTINYINT -> jdbcToDfConverterForValueColumnOf<Short>(isNullable)

                USMALLINT -> jdbcToDfConverterForValueColumnOf<Int>(isNullable)

                UINTEGER -> jdbcToDfConverterForValueColumnOf<Long>(isNullable)

                UBIGINT -> jdbcToDfConverterForValueColumnOf<BigInteger>(isNullable)

                FLOAT -> jdbcToDfConverterForValueColumnOf<Float>(isNullable)

                DOUBLE -> jdbcToDfConverterForValueColumnOf<Double>(isNullable)

                DECIMAL -> jdbcToDfConverterForValueColumnOf<BigDecimal>(isNullable)

                TIME ->
                    jdbcToDfConverterWithPreprocessingForValueColumnOf<JavaLocalTime, LocalTime>(isNullable) {
                        it?.toKotlinLocalTime()
                    }

                // todo?
                TIME_WITH_TIME_ZONE -> jdbcToDfConverterForValueColumnOf<JavaOffsetTime>(isNullable)

                DATE -> jdbcToDfConverterWithPreprocessingForValueColumnOf<JavaLocalDate, LocalDate>(isNullable) {
                    it?.toKotlinLocalDate()
                }

                TIMESTAMP, TIMESTAMP_MS, TIMESTAMP_NS, TIMESTAMP_S ->
                    jdbcToDfConverterWithPreprocessingForValueColumnOf<SqlTimestamp, Instant>(isNullable) {
                        it?.toInstant()?.toKotlinInstant()
                    }

                // todo?
                TIMESTAMP_WITH_TIME_ZONE -> jdbcToDfConverterForValueColumnOf<JavaOffsetDateTime>(isNullable)

                // TODO!
                JSON -> jdbcToDfConverterForValueColumnOf<JsonNode>(isNullable)

                BLOB -> jdbcToDfConverterForValueColumnOf<Blob>(isNullable)

                UUID -> jdbcToDfConverterWithPreprocessingForValueColumnOf<JavaUUID, Uuid>(isNullable) {
                    it?.toKotlinUuid()
                }

                MAP -> {
                    val (key, value) = parseMapTypes(sqlTypeName)

                    val parsedKeyType = parseDuckDbType(key, false)
                    val parsedValueType = parseDuckDbType(value, true).castToAny()

                    val targetMapType = Map::class.createType(
                        listOf(
                            KTypeProjection.invariant(parsedKeyType.targetSchema.type),
                            KTypeProjection.invariant(parsedValueType.targetSchema.type),
                        ),
                    ).withNullability(isNullable)

                    jdbcToDfConverterWithPreprocessingForValueColumnOf<Map<String, Any?>, Map<String, Any?>>(
                        isNullable = isNullable,
                        preprocessedValueType = targetMapType,
                    ) { map ->
                        // only need to preprocess the values, as the keys are just Strings
                        map?.mapValues { (_, value) ->
                            parsedValueType.preprocessOrCast(value)
                        }
                    }
                }

                LIST, ARRAY -> {
                    val listType = parseListType(sqlTypeName)
                    val parsedListType =
                        parseDuckDbType(listType, true).castToAny()

                    val targetListType = List::class.createType(
                        listOf(
                            KTypeProjection.invariant(
                                parsedListType.targetSchema.type,
                            ),
                        ),
                    ).withNullability(isNullable)

                    // todo maybe List<DataRow> should become FrameColumn
                    jdbcToDfConverterWithPreprocessingForValueColumnOf<SqlArray, List<Any?>>(
                        isNullable = isNullable,
                        preprocessedValueType = targetListType,
                    ) { sqlArray ->
                        sqlArray
                            ?.toList()
                            ?.map { parsedListType.preprocessOrCast(it) } // recursively preprocess
                    }
                }

                STRUCT -> {
                    val structEntries = parseStructType(sqlTypeName)
                    val parsedStructEntries = structEntries.mapValues { (_, type) ->
                        parseDuckDbType(sqlTypeName = type, isNullable = true)
                    }

                    val targetSchema = ColumnSchema.Group(
                        schema = DataFrameSchemaImpl(parsedStructEntries.mapValues { it.value.targetSchema }),
                        contentType = typeOf<Any?>(),
                    )

                    jdbcToDfConverterWithProcessingFor<Struct, Map<String, Any?>, DataRow<*>>(
                        isNullable = isNullable,
                        targetSchema = targetSchema,
                        valuePreprocessor = { struct ->
                            // NOTE DataRows cannot be `null` in DataFrame, instead, all its fields become `null`
                            if (struct == null) {
                                parsedStructEntries.mapValues { null }
                            } else {
                                // read data from the struct
                                val attrs = struct.getAttributes(
                                    parsedStructEntries.mapValues {
                                        (it.value.expectedJdbcType.classifier!! as KClass<*>).java
                                    },
                                )

                                // and potentially, preprocess each value individually
                                parsedStructEntries.entries.withIndex().associate { (i, entry) ->
                                    entry.key to entry.value.castToAny().preprocessOrCast(attrs[i])
                                }
                            }
                        },
                        columnBuilder = { name, values, _ ->
                            (values as List<Map<String, Any?>>)
                                .toDataFrame()
                                .asColumnGroup(name)
                                .asDataColumn()
                        },
                    )
                }

                // Cannot handle this in Kotlin
                UNION -> jdbcToDfConverterForValueColumnOf<Any>(isNullable)

                VARCHAR -> jdbcToDfConverterForValueColumnOf<String>(isNullable)

                UNKNOWN, BIT, INTERVAL, ENUM -> jdbcToDfConverterForValueColumnOf<String>(isNullable)
            }
        }

    private fun SqlArray.toList(): List<Any?> =
        when (val array = this.array) {
            is IntArray -> array.toList()
            is LongArray -> array.toList()
            is ShortArray -> array.toList()
            is ByteArray -> array.toList()
            is FloatArray -> array.toList()
            is DoubleArray -> array.toList()
            is BooleanArray -> array.toList()
            is CharArray -> array.toList()
            is Array<*> -> array.toList()
            is SqlArray -> array.toList()
            else -> error("unknown array type $array")
        }

    /** Parses "MAP(X, Y)" into "X" and "Y", taking parentheses into account */
    internal fun parseMapTypes(typeString: String): Pair<String, String> {
        if (!typeString.startsWith("MAP(") || !typeString.endsWith(")")) {
            error("invalid MAP type: $typeString")
        }

        val content = typeString.removeSurrounding("MAP(", ")")

        // Find the comma that separates key and value types
        var parenCount = 0
        var commaIndex = -1
        for (i in content.indices) {
            when (content[i]) {
                '(' -> parenCount++

                ')' -> parenCount--

                ',' -> if (parenCount == 0) {
                    commaIndex = i
                    break
                }
            }
        }

        if (commaIndex == -1) error("invalid MAP type: $typeString")
        val keyType = content.take(commaIndex).trim()
        val valueType = content.substring(commaIndex + 1).trim()
        return Pair(keyType, valueType)
    }

    /** Parses "X[]" and "X[123]" into "X", and "X[][]" into "X[]" */
    internal fun parseListType(typeString: String): String {
        if (!typeString.endsWith("]")) {
            error("invalid LIST/ARRAY type: $typeString")
        }

        return typeString.take(typeString.indexOfLast { it == '[' })
    }

    /** Parses "STRUCT(v VARCHAR, i INTEGER)" into [("v", "VARCHAR"), ("i", "INTEGER")] */
    internal fun parseStructType(typeString: String): Map<String, String> {
        if (!typeString.startsWith("STRUCT(")) {
            error("invalid STRUCT type: $typeString")
        }
        return typeString.removeSurrounding("STRUCT(", ")")
            .split(",")
            .map { it.trim().split(" ") }
            .associate { (name, type) -> name to type }
    }

    /**
     * How to filter out system tables from user-created ones when using
     * [DataFrame.readAllSqlTables][DataFrame.Companion.readAllSqlTables] and
     * [DataFrameSchema.readAllSqlTables][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.Companion.readAllSqlTables].
     *
     * The names of these can sometimes be found in the specific JDBC integration.
     */
    override fun isSystemTable(tableMetadata: TableMetadata): Boolean =
        tableMetadata.schemaName?.lowercase()?.contains("information_schema") == true ||
            tableMetadata.schemaName?.lowercase()?.contains("system") == true ||
            tableMetadata.name.lowercase().contains("system_")

    /**
     * How to retrieve the correct table metadata when using
     * [DataFrame.readAllSqlTables][DataFrame.Companion.readAllSqlTables] and
     * [DataFrameSchema.readAllSqlTables][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema.Companion.readAllSqlTables].
     * The names of these can be found in the [DatabaseMetaData] implementation of the DuckDB JDBC integration.
     */
    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("TABLE_NAME"),
            tables.getString("TABLE_SCHEM"),
            tables.getString("TABLE_CAT"),
        )

    /**
     * Creates a database connection using the provided configuration.
     *
     * DuckDB does not support changing read-only status after connection creation,
     * but supports read-only mode through connection parameters.
     *
     * @param [dbConfig] The database configuration containing URL, credentials, and read-only flag.
     * @return A configured [Connection] instance.
     */
    override fun createConnection(dbConfig: DbConnectionConfig): Connection {
        val properties = Properties().apply {
            dbConfig.user.takeIf { it.isNotEmpty() }?.let { setProperty("user", it) }
            dbConfig.password.takeIf { it.isNotEmpty() }?.let { setProperty("password", it) }

            // Handle DuckDB limitation: in-memory databases cannot be opened in read-only mode
            if (dbConfig.readOnly && !dbConfig.url.isInMemoryDuckDb()) {
                setProperty("access_mode", "read_only")
            } else if (dbConfig.readOnly) {
                logger.warn {
                    "Cannot create read-only in-memory DuckDB database (url=${dbConfig.url}). " +
                        "In-memory databases require write access for initialization. Connection will be created without read-only mode."
                }
            }
        }

        return DriverManager.getConnection(dbConfig.url, properties)
    }

    /**
     * Checks if the DuckDB URL represents an in-memory database.
     * In-memory DuckDB URLs are either "jdbc:duckdb:" or "jdbc:duckdb:" followed only by whitespace.
     */
    private fun String.isInMemoryDuckDb(): Boolean =
        this.trim() == "jdbc:duckdb:" || matches("jdbc:duckdb:\\s*$".toRegex())
}
