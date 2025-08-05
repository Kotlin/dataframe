@file:Suppress("SqlDialectInspection")

package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.duckdb.DuckDBConnection
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.inferType
import org.jetbrains.kotlinx.dataframe.api.isNotEmpty
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.io.db.DuckDb
import org.junit.Test
import java.sql.DriverManager
import kotlin.reflect.full.isSubtypeOf

private const val URL = "jdbc:duckdb:"

class DuckDbTest {

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
