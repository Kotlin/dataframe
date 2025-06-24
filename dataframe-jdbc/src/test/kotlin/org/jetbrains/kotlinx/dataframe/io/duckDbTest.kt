@file:Suppress("SqlDialectInspection")

package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
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
import org.duckdb.DuckDBConnection
import org.duckdb.DuckDBResultSetMetaData
import org.duckdb.DuckDBResultSetMetaData.type_to_int
import org.duckdb.DuckDBStruct
import org.duckdb.JsonNode
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.inferType
import org.jetbrains.kotlinx.dataframe.api.isNotEmpty
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.DuckDb.toKType
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Blob
import java.sql.Clob
import java.sql.DriverManager
import java.sql.NClob
import java.sql.Ref
import java.sql.ResultSet
import java.sql.RowId
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Date
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

private const val URL = "jdbc:duckdb:"

object DuckDb : DbType("duckdb") {
    override val driverClassName = "org.duckdb.DuckDBDriver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema {
        val type = convertSqlTypeToKType(tableColumnMetadata)
        return ColumnSchema.Value(type)
    }

    // TODO?
    override fun isSystemTable(tableMetadata: TableMetadata): Boolean =
        tableMetadata.schemaName?.lowercase()?.contains("information_schema") == true ||
            tableMetadata.schemaName?.lowercase()?.contains("system") == true ||
            tableMetadata.name.lowercase().contains("system_")

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("TABLE_NAME"),
            tables.getString("TABLE_SCHEM"),
            tables.getString("TABLE_CAT"),
        )

    /**
     * Follows exactly [org.duckdb.DuckDBVector.getObject].
     *
     * I added a "// dataframe-jdbc" comment for all types that are covered correctly by
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
                        KTypeProjection.covariant(value.toKType(true)),
                    ),
                )
            }

            LIST, ARRAY -> {
                // TODO requires #1266 and #1273 for specific types
                //   val listType = parseListType(sqlTypeName)
                //   Array::class.createType(
                //       listOf(KTypeProjection.covariant(listType.toKType(true))),
                //   )
                typeOf<java.sql.Array>()
            }

            STRUCT -> typeOf<java.sql.Struct>() // TODO requires #1266 for specific types
            UNION -> typeOf<Any>() // Cannot handle this in Kotlin
            VARCHAR -> typeOf<String>()
            UNKNOWN, BIT, INTERVAL, ENUM -> typeOf<String>()
        }.withNullability(isNullable)
    }

    // Parses "MAP(X, Y)" into "X" and "Y", taking parentheses into account
    fun parseMapTypes(typeString: String): Pair<String, String> {
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

    // Parses "X[]" and "X[123]" into "X", and "X[][]" into "X[]"
    fun parseListType(typeString: String): String {
        if (!typeString.endsWith("]")) {
            error("invalid LIST/ARRAY type: $typeString")
        }

        return typeString.take(typeString.indexOfLast { it == '[' })
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType =
        tableColumnMetadata.sqlTypeName.toKType(tableColumnMetadata.isNullable)
}

class DuckDbTest {

    @Test
    fun `Type comparison`() {
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

        fun createArrayTypeIfNeeded(kClass: KClass<*>, isNullable: Boolean): KType =
            if (kClass == Array::class) {
                val typeParam = kClass.typeParameters[0].createType()
                kClass.createType(
                    arguments = listOf(KTypeProjection.invariant(typeParam)),
                    nullable = isNullable,
                )
            } else {
                kClass.createType(nullable = isNullable)
            }

        DuckDBColumnType.entries.map {
            val mine = it.name.toKType(false)
            val lyosha = createArrayTypeIfNeeded(jdbcTypeToKTypeMapping[type_to_int(it)] ?: String::class, false)

            Triple(it, mine, lyosha)
        }.partition { it.second == it.third }.let { (equal, notEqual) ->
            println("Correct matches:")
            for ((it, mine, lyosha) in equal) {
                println("$it: duckdb: $mine, df: $lyosha")
            }
            println()
            println("Incorrect matches:")
            for ((it, mine, lyosha) in notEqual) {
                println("$it: duckdb: $mine, df: $lyosha")
            }
        }
    }

    @Test
    fun `read simple dataframe from DuckDB`() {
        val df = DriverManager.getConnection(URL).use { connection ->
            connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS test_table (
                    id INTEGER PRIMARY KEY,
                    name VARCHAR,
                    age INTEGER,
                    salary DOUBLE,
                    hire_date DATE
                )
                """.trimIndent(),
            ).executeUpdate()

            connection.prepareStatement(
                """
                INSERT INTO test_table (id, name, age, salary, hire_date)
                VALUES 
                    (1, 'John Doe', 30, 50000.00, '2020-01-15'),
                    (2, 'Jane Smith', 28, 55000.00, '2021-03-20'),
                    (3, 'Bob Johnson', 35, 65000.00, '2019-11-10'),
                    (4, 'Alice Brown', 32, 60000.00, '2020-07-01')
                """.trimIndent(),
            ).executeUpdate()

            DataFrame.readSqlTable(connection, "test_table", dbType = DuckDb)
        }

        df.print(borders = true, columnTypes = true)
        df.isNotEmpty() shouldBe true
    }

    @Test
    fun `read all tables`() {
        DriverManager.getConnection(URL).use { connection ->
            connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS test_table (
                    id INTEGER PRIMARY KEY,
                    name VARCHAR,
                    age INTEGER,
                    salary DOUBLE,
                    hire_date DATE
                )
                """.trimIndent(),
            ).executeUpdate()

            connection.prepareStatement(
                """
                INSERT INTO test_table (id, name, age, salary, hire_date)
                VALUES 
                    (1, 'John Doe', 30, 50000.00, '2020-01-15'),
                    (2, 'Jane Smith', 28, 55000.00, '2021-03-20'),
                    (3, 'Bob Johnson', 35, 65000.00, '2019-11-10'),
                    (4, 'Alice Brown', 32, 60000.00, '2020-07-01')
                """.trimIndent(),
            ).executeUpdate()

            DataFrame.readAllSqlTables(connection = connection, dbType = DuckDb).isNotEmpty() shouldBe true
        }
    }

    /**
     * https://duckdb.org/docs/stable/sql/data_types/overview.html
     */
    @Test
    fun `read each general-purpose DuckDB type`() {
        val df = DriverManager.getConnection(URL).use { connection ->
            connection as DuckDBConnection
            connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS table1 (
                    bigint_col BIGINT,
                    int8_col INT8,
                    int64_col INT64,
                    long_col LONG,
                    bit_col BIT,
                    bitstring_col BITSTRING,
                    blob_col BLOB,
                    bytea_col BYTEA,
                    binary_col BINARY,
                    varbinary_col VARBINARY,
                    boolean_col BOOLEAN,
                    bool_col BOOL,
                    logical_col LOGICAL,
                    date_col DATE,
                    decimal_col DECIMAL(10,2),
                    numeric_col NUMERIC(10,2),
                    double_col DOUBLE,
                    float8_col FLOAT8,
                    float_col FLOAT,
                    float4_col FLOAT4,
                    real_col REAL,
                    hugeint_col HUGEINT,
                    int128_col INT128,
                    integer_col INTEGER,
                    int4_col INT4,
                    int32_col INT32,
                    int_col INT,
                    signed_col SIGNED,
                    interval_col INTERVAL,
                    json_col JSON,
                    smallint_col SMALLINT,
                    int2_col INT2,
                    int16_col INT16,
                    short_col SHORT,
                    time_col TIME,
                    timestampwtz_col TIMESTAMP WITH TIME ZONE,
                    timestamptz_col TIMESTAMPTZ,
                    timestamp_col TIMESTAMP,
                    datetime_col DATETIME,
                    tinyint_col TINYINT,
                    int1_col INT1,
                    ubigint_col UBIGINT,
                    uint64_col UINT64,
                    uhugeint_col UHUGEINT,
                    uint128_col UINT128,
                    uint_col UINTEGER,
                    uint32_col UINT32,
                    usmallint_col USMALLINT,
                    uint16_col UINT16,
                    utinyint_col UTINYINT,
                    uint8_col UINT8,
                    uuid_col UUID,
                    varchar_col VARCHAR,
                    char_col CHAR(10),
                    bpchar_col BPCHAR(10),
                    text_col TEXT,
                    string_col STRING,
                    enum_col ENUM('male', 'female', 'other')
                )
                """.trimIndent(),
            ).executeUpdate()

            connection.prepareStatement(
                """
                INSERT INTO table1 VALUES (
                    9223372036854775807,                    -- bigint
                    9223372036854775807,                    -- int8
                    9223372036854775807,                    -- int64
                    9223372036854775807,                    -- long
                    '1010',                                -- bit
                    '1010',                                -- bitstring
                    'DEADBEEF'::BLOB,                      -- blob
                    'DEADBEEF'::BLOB,                      -- bytea
                    'DEADBEEF'::BLOB,                      -- binary
                    'DEADBEEF'::BLOB,                      -- varbinary
                    true,                                   -- boolean
                    true,                                   -- bool
                    true,                                   -- logical
                    '2025-06-19',                          -- date
                    123.45,                                 -- decimal
                    123.45,                                 -- numeric
                    3.14159,                               -- double
                    3.14159,                               -- float8
                    3.14,                                  -- float
                    3.14,                                  -- float4
                    3.14,                                  -- real
                    '170141183460469231731687303715884105727',  -- hugeint
                    '170141183460469231731687303715884105727',  -- int128
                    2147483647,                            -- integer
                    2147483647,                            -- int4
                    2147483647,                            -- int32
                    2147483647,                            -- int
                    2147483647,                            -- signed
                    INTERVAL '1' YEAR,                     -- interval
                    '{"key": "value"}'::JSON,              -- json
                    32767,                                 -- smallint
                    32767,                                 -- int2
                    32767,                                 -- int16
                    32767,                                 -- short
                    '12:34:56',                            -- time
                    '2025-06-19 12:34:56+02',             -- timestampwtz
                    '2025-06-19 12:34:56+02',             -- timestamptz
                    '2025-06-19 12:34:56',                -- timestamp
                    '2025-06-19 12:34:56',                -- datetime
                    127,                                   -- tinyint
                    127,                                   -- int1
                    18446744073709551615,                 -- ubigint
                    18446744073709551615,                 -- uint64
                    '340282366920938463463374607431768211455',  -- uhugeint
                    '340282366920938463463374607431768211455',  -- uint128
                    4294967295,                           -- uinteger
                    4294967295,                           -- uint32
                    65535,                                -- usmallint
                    65535,                                -- uint16
                    255,                                  -- utinyint
                    255,                                  -- uint8
                    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',  -- uuid
                    'test string',                         -- varchar
                    'test',                                -- char
                    'test',                                -- bpchar
                    'test text',                           -- text
                    'test string',                          -- string
                    'female'                               -- enum
                )
                """.trimIndent(),
            ).executeUpdate()

            DataFrame.getSchemaForSqlTable(connection, "table1", dbType = DuckDb).print()
            DataFrame.readSqlTable(connection, "table1", dbType = DuckDb)
        }

        df.print(columnTypes = true, borders = true)
        df.inferType().print(columnTypes = true, borders = true)

        (df.columnTypes() zip df.inferType().columnTypes()).forEach { (provided, inferred) ->
            inferred.isSubtypeOf(provided) shouldBe true
        }
    }

    /**
     * https://duckdb.org/docs/stable/sql/data_types/overview.html
     */
    @Test
    fun `read each nested DuckDB type`() {
        val df = DriverManager.getConnection(URL).use { connection ->
            connection as DuckDBConnection
            connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS table2 (
                    intarray_col INTEGER[3],
                    stringarray_col VARCHAR[3],
                    intlist_col INTEGER[],
                    stringlist_col VARCHAR[],
                    stringlistlist_col VARCHAR[][],
                    intstringmap_col MAP(INTEGER, VARCHAR),
                    intstrinstinggmap_col MAP(INTEGER, MAP(VARCHAR, VARCHAR)),
                    ijstruct_col STRUCT(i INTEGER, j VARCHAR),
                    union_col UNION(num INTEGER, text VARCHAR),
                )
                """.trimIndent(),
            ).executeUpdate()

            connection.prepareStatement(
                """
                INSERT INTO table2 VALUES (
                    array_value(1, 2, NULL),                    -- int array
                    array_value('a', 'ab', 'abc'),           -- string array
                    list_value(1, 2, 3),                     -- int list
                    list_value('a', 'ab', 'abc'),            -- string list
                    list_value(list_value('a', 'ab'), list_value('abc'), NULL),            -- string list list
                    MAP { 1: 'value1', 200: 'value2' },      -- int string map
                    MAP { 1: MAP { 'value1': 'a', 'value2': 'b' }, 200: MAP { 'value1': 'c', 'value2': 'd' } }, -- int string string map
                    { 'i': 42, 'j': 'answer' },               -- struct
                    union_value(num := 2),                    -- union
                )
                """.trimIndent(),
            ).executeUpdate()

            DataFrame.getSchemaForSqlTable(connection, "table2", dbType = DuckDb).print()
            DataFrame.readSqlTable(connection, "table2", dbType = DuckDb)
        }

        df.print(columnTypes = true, borders = true)
        df.inferType().print(columnTypes = true, borders = true)

        (df.columnTypes() zip df.inferType().columnTypes()).forEach { (provided, inferred) ->
            inferred.isSubtypeOf(provided) shouldBe true
        }
    }
}
