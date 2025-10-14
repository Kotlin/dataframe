package org.jetbrains.kotlinx.dataframe.io.db

import io.github.oshai.kotlinlogging.KotlinLogging
import org.duckdb.DuckDBColumnType
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
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.db.DuckDb.convertSqlTypeToKType
import org.jetbrains.kotlinx.dataframe.io.readAllSqlTables
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Array
import java.sql.Blob
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Struct
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Properties
import java.util.UUID
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

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
     * How a column type from JDBC, [tableColumnMetadata], is read in Java/Kotlin.
     * The returned type must exactly follow [ResultSet.getObject] of your specific database's JDBC driver.
     * Returning `null` defer the implementation to the default one (which may not always be correct).
     *
     * Following [org.duckdb.DuckDBVector.getObject].
     */
    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType =
        tableColumnMetadata.sqlTypeName.toKType(tableColumnMetadata.isNullable)

    /**
     * How a column from JDBC should be represented as DataFrame (value) column
     * See [convertSqlTypeToKType].
     */
    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema {
        val type = convertSqlTypeToKType(tableColumnMetadata)
        return ColumnSchema.Value(type)
    }

    /**
     * Follows exactly [org.duckdb.DuckDBVector.getObject].
     *
     * "// dataframe-jdbc" is added for all types that are covered correctly by
     * [org.jetbrains.kotlinx.dataframe.io.makeCommonSqlToKTypeMapping] at the moment, however, to cover
     * all nested types, we'll use a full type-map for all [DuckDB types][DuckDBColumnType] exactly.
     */
    @Suppress("ktlint:standard:blank-line-between-when-conditions")
    internal fun String.toKType(isNullable: Boolean): KType {
        val sqlTypeName = this
        return when (DuckDBResultSetMetaData.TypeNameToType(sqlTypeName)) {
            BOOLEAN -> typeOf<Boolean>() // dataframe-jdbc
            TINYINT -> typeOf<Byte>()
            SMALLINT -> typeOf<Short>()
            INTEGER -> typeOf<Int>() // dataframe-jdbc
            BIGINT -> typeOf<Long>() // dataframe-jdbc
            HUGEINT -> typeOf<BigInteger>()
            UHUGEINT -> typeOf<BigInteger>()
            UTINYINT -> typeOf<Short>()
            USMALLINT -> typeOf<Int>()
            UINTEGER -> typeOf<Long>()
            UBIGINT -> typeOf<BigInteger>()
            FLOAT -> typeOf<Float>() // dataframe-jdbc
            DOUBLE -> typeOf<Double>() // dataframe-jdbc
            DECIMAL -> typeOf<BigDecimal>() // dataframe-jdbc
            TIME -> typeOf<LocalTime>()
            TIME_WITH_TIME_ZONE -> typeOf<OffsetTime>() // dataframe-jdbc
            DATE -> typeOf<LocalDate>()
            TIMESTAMP, TIMESTAMP_MS, TIMESTAMP_NS, TIMESTAMP_S -> typeOf<Timestamp>() // dataframe-jdbc
            TIMESTAMP_WITH_TIME_ZONE -> typeOf<OffsetDateTime>() // dataframe-jdbc
            JSON -> typeOf<JsonNode>()
            BLOB -> typeOf<Blob>()
            UUID -> typeOf<UUID>()
            MAP -> {
                val (key, value) = parseMapTypes(sqlTypeName)
                Map::class.createType(
                    listOf(
                        KTypeProjection.invariant(key.toKType(false)),
                        KTypeProjection.invariant(value.toKType(true)),
                    ),
                )
            }

            LIST, ARRAY -> {
                // TODO requires #1266 and #1273 for specific types
                //   val listType = parseListType(sqlTypeName)
                //   Array::class.createType(
                //       listOf(KTypeProjection.invariant(listType.toKType(true))),
                //   )
                typeOf<Array>()
            }

            STRUCT -> typeOf<Struct>() // TODO requires #1266 for specific types
            UNION -> typeOf<Any>() // Cannot handle this in Kotlin
            VARCHAR -> typeOf<String>()
            UNKNOWN, BIT, INTERVAL, ENUM -> typeOf<String>()
        }.withNullability(isNullable)
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

    /**
     * How to filter out system tables from user-created ones when using
     * [DataFrame.readAllSqlTables][DataFrame.Companion.readAllSqlTables] and
     * [DataFrame.getSchemaForAllSqlTables][DataFrame.Companion.getSchemaForAllSqlTables].
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
     * [DataFrame.getSchemaForAllSqlTables][DataFrame.Companion.getSchemaForAllSqlTables].
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
     * @return A configured [java.sql.Connection] instance.
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
