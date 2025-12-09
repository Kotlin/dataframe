package org.jetbrains.kotlinx.dataframe.io.db

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
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
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asValueColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.convertToLocalDate
import org.jetbrains.kotlinx.dataframe.api.convertToLocalTime
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.db.DuckDb.convertSqlTypeToKType
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
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Instant
import kotlin.time.toKotlinInstant
import kotlin.uuid.Uuid
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
public object DuckDb : DbType("duckdb") {

    /** the name of the class of the DuckDB JDBC driver */
    override val driverClassName: String = "org.duckdb.DuckDBDriver"

    /**
     * TODO: Unclear what this returned [KType] is useful for. Let's remove this function and just have
     *   [convertSqlTypeToColumnSchemaValue]
     */
    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType =
        convertSqlTypeToColumnSchemaValue(tableColumnMetadata).type

    /**
     * How a column from JDBC should be represented as DataFrame (value) column
     * See [convertSqlTypeToKType].
     */
    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema =
        parseDuckDbType(tableColumnMetadata.sqlTypeName, tableColumnMetadata.isNullable).targetSchema

    /**
     * TODO: This function achieves the same goal as [convertSqlTypeToKType].
     */
    override fun makeCommonSqlToKTypeMapping(tableColumnMetadata: TableColumnMetadata): Nothing =
        error("This function should not be called. Or exist, for that matter...")

    /**
     * TODO: I wanted to do the conversion here, but as I have no source type ánd target type
     *   it's impossible.
     *   It would be easier to do conversion on the entire column because we can borrow [DataColumn.convertTo].
     */
    override fun buildDataColumn(
        name: String,
        values: MutableList<Any?>,
        kType: KType,
        inferNullability: Boolean,
    ): DataColumn<*> {
        val sourceType = kType
        return super.buildDataColumn(name, values, kType, inferNullability)
    }

    override fun extractValueFromResultSet(
        rs: ResultSet,
        columnIndex: Int,
        columnMetadata: TableColumnMetadata,
        kType: KType,
    ): Any? {
        // TODO This '+ 1' is easily forgotten if I need to override this function to do any conversion
        val result = rs.getObject(columnIndex + 1)

        // TODO: where is the [ColumnSchema] when I need it?
        //   Now I need to call my [parseDuckDbType] function again...
        val parsedType = parseDuckDbType(columnMetadata.sqlTypeName, columnMetadata.isNullable)

        // TODO doing it as a column
        val convertedResult = parsedType.converter(columnOf(result)).single()
        return convertedResult
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

/**
 * How a column type from JDBC, [sqlTypeName], is read in Java/Kotlin.
 * The returned type must exactly follow [ResultSet.getObject] of your specific database's JDBC driver.
 * Returning `null` defer the implementation to the default one (which may not always be correct).
 *
 * Following [org.duckdb.DuckDBVector.getObject] and converting the result to
 *
 */
internal fun parseDuckDbType(sqlTypeName: String, isNullable: Boolean): ParsedType =
    when (DuckDBResultSetMetaData.TypeNameToType(sqlTypeName)) {
        BOOLEAN -> parsedTypeForValueColumnOf<Boolean>(isNullable)

        TINYINT -> parsedTypeForValueColumnOf<Byte>(isNullable)

        SMALLINT -> parsedTypeForValueColumnOf<Short>(isNullable)

        INTEGER -> parsedTypeForValueColumnOf<Int>(isNullable)

        BIGINT -> parsedTypeForValueColumnOf<Long>(isNullable)

        HUGEINT -> parsedTypeForValueColumnOf<BigInteger>(isNullable)

        UHUGEINT -> parsedTypeForValueColumnOf<BigInteger>(isNullable)

        UTINYINT -> parsedTypeForValueColumnOf<Short>(isNullable)

        USMALLINT -> parsedTypeForValueColumnOf<Int>(isNullable)

        UINTEGER -> parsedTypeForValueColumnOf<Long>(isNullable)

        UBIGINT -> parsedTypeForValueColumnOf<BigInteger>(isNullable)

        FLOAT -> parsedTypeForValueColumnOf<Float>(isNullable)

        DOUBLE -> parsedTypeForValueColumnOf<Double>(isNullable)

        DECIMAL -> parsedTypeForValueColumnOf<BigDecimal>(isNullable)

        // DataFrame can do this conversion
        TIME -> parsedTypeForValueColumnOf<JavaLocalTime, LocalTime>(isNullable) { it.convertTo() }

        // todo?
        TIME_WITH_TIME_ZONE -> parsedTypeForValueColumnOf<JavaOffsetTime>(isNullable)

        // DataFrame can do this conversion
        DATE -> parsedTypeForValueColumnOf<JavaLocalDate, LocalDate>(isNullable) { it.convertTo() }

        TIMESTAMP, TIMESTAMP_MS, TIMESTAMP_NS, TIMESTAMP_S ->
            parsedTypeForValueColumnOf<SqlTimestamp, Instant>(isNullable) {
                it.map {
                    it?.toInstant()?.toKotlinInstant()
                }.asValueColumn().cast()
            }

        // todo?
        TIMESTAMP_WITH_TIME_ZONE -> parsedTypeForValueColumnOf<JavaOffsetDateTime>(isNullable)

        // TODO!
        JSON -> parsedTypeForValueColumnOf<JsonNode>(isNullable)

        BLOB -> parsedTypeForValueColumnOf<Blob>(isNullable)

        UUID -> parsedTypeForValueColumnOf<JavaUUID, Uuid>(isNullable) { it.convertTo() }

        MAP -> {
            val (key, value) = parseMapTypes(sqlTypeName)
            val sourceMapType = Map::class.createType(
                listOf(
                    KTypeProjection.invariant(parseDuckDbType(key, false).sourceType),
                    KTypeProjection.invariant(parseDuckDbType(value, true).sourceType),
                ),
            )
            val targetMapType = Map::class.createType(
                listOf(
                    KTypeProjection.invariant(parseDuckDbType(key, false).targetSchema.type),
                    KTypeProjection.invariant(parseDuckDbType(value, true).targetSchema.type),
                ),
            )

            ParsedType(
                sourceType = sourceMapType,
                targetSchema = ColumnSchema.Value(targetMapType),
                converter = { it },
            )
        }

        LIST, ARRAY -> {
            // TODO requires #1266 and #1273 for specific types
            val listType = parseListType(sqlTypeName)
            val parsedListType = parseDuckDbType(listType, true)
            val targetListType = List::class.createType(
                listOf(KTypeProjection.invariant(parsedListType.targetSchema.type)),
            )
            // todo maybe List<DataRow> should become FrameColumn
            ParsedType(
                sourceType = typeOf<SqlArray>(),
                targetSchema = ColumnSchema.Value(targetListType),
                converter = { it },
            )
        }

        // TODO requires #1266 for specific types
        STRUCT -> parsedTypeForValueColumnOf<Struct>(isNullable)

        // Cannot handle this in Kotlin
        UNION -> parsedTypeForValueColumnOf<Any>(isNullable)

        VARCHAR -> parsedTypeForValueColumnOf<String>(isNullable)

        UNKNOWN, BIT, INTERVAL, ENUM -> parsedTypeForValueColumnOf<String>(isNullable)
    }

/**
 * @property sourceType the source type of the column as read by [ResultSet.getObject] of our specific database's JDBC driver.
 * @property targetSchema the target schema of the column. This can have a different [kType][ColumnSchema.type] than [sourceType]!
 *   If so, the values need to be converted in [DbType.buildDataColumn].
 * @property converter a function that converts the source column to the target column type
 */
internal data class ParsedType(
    val sourceType: KType,
    val targetSchema: ColumnSchema,
    val converter: (DataColumn<*>) -> DataColumn<*>,
)

internal inline fun <reified SourceType> parsedTypeForValueColumnOf(isNullable: Boolean): ParsedType {
    val type = typeOf<SourceType>().withNullability(isNullable)
    return ParsedType(
        sourceType = type,
        targetSchema = ColumnSchema.Value(type),
        converter = { it },
    )
}

internal inline fun <reified SourceType, reified TargetType> parsedTypeForValueColumnOf(
    isNullable: Boolean,
    noinline converter: (DataColumn<SourceType?>) -> DataColumn<TargetType?>,
): ParsedType =
    ParsedType(
        sourceType = typeOf<SourceType>().withNullability(isNullable),
        targetSchema = ColumnSchema.Value(typeOf<TargetType>().withNullability(isNullable)),
        converter = converter as (DataColumn<*>) -> DataColumn<*>,
    )

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
