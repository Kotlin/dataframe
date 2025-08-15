@file:Suppress("SqlDialectInspection")

package org.jetbrains.kotlinx.dataframe.io.local

import io.kotest.matchers.shouldBe
import org.duckdb.DuckDBConnection
import org.duckdb.DuckDBResultSet
import org.duckdb.JsonNode
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.reorderColumnsByName
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.assertInferredTypesMatchSchema
import org.jetbrains.kotlinx.dataframe.io.db.DuckDb
import org.jetbrains.kotlinx.dataframe.io.getSchemaForAllSqlTables
import org.jetbrains.kotlinx.dataframe.io.getSchemaForResultSet
import org.jetbrains.kotlinx.dataframe.io.getSchemaForSqlTable
import org.jetbrains.kotlinx.dataframe.io.readAllSqlTables
import org.jetbrains.kotlinx.dataframe.io.readDataFrame
import org.jetbrains.kotlinx.dataframe.io.readResultSet
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.file.Files
import java.sql.Blob
import java.sql.DriverManager
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.io.path.createTempDirectory

private const val URL = "jdbc:duckdb:"

class DuckDbTest {

    // region expected schemas
    @DataSchema
    data class Person(
        val id: Int,
        val name: String,
        val age: Int,
        val salary: Double,
        @ColumnName("hire_date")
        val hireDate: LocalDate,
    ) {
        companion object {
            val expected = listOf(
                Person(1, "John Doe", 30, 50000.0, LocalDate.of(2020, 1, 15)),
                Person(2, "Jane Smith", 28, 55000.0, LocalDate.of(2021, 3, 20)),
                Person(3, "Bob Johnson", 35, 65000.0, LocalDate.of(2019, 11, 10)),
                Person(4, "Alice Brown", 32, 60000.0, LocalDate.of(2020, 7, 1)),
            ).toDataFrame()
        }
    }

    @DataSchema
    data class GeneralPurposeTypes(
        @ColumnName("bigint_col")
        val bigintCol: Long,
        @ColumnName("binary_col")
        val binaryCol: Blob,
        @ColumnName("bit_col")
        val bitCol: String,
        @ColumnName("bitstring_col")
        val bitstringCol: String,
        @ColumnName("blob_col")
        val blobCol: Blob,
        @ColumnName("bool_col")
        val boolCol: Boolean,
        @ColumnName("boolean_col")
        val booleanCol: Boolean,
        @ColumnName("bpchar_col")
        val bpcharCol: String,
        @ColumnName("bytea_col")
        val byteaCol: Blob,
        @ColumnName("char_col")
        val charCol: String,
        @ColumnName("date_col")
        val dateCol: LocalDate,
        @ColumnName("datetime_col")
        val datetimeCol: Timestamp,
        @ColumnName("decimal_col")
        val decimalCol: BigDecimal,
        @ColumnName("double_col")
        val doubleCol: Double,
        @ColumnName("enum_col")
        val enumCol: String,
        @ColumnName("float4_col")
        val float4Col: Float,
        @ColumnName("float8_col")
        val float8Col: Double,
        @ColumnName("float_col")
        val floatCol: Float,
        @ColumnName("hugeint_col")
        val hugeintCol: BigInteger,
        @ColumnName("int128_col")
        val int128Col: BigInteger,
        @ColumnName("int16_col")
        val int16Col: Short,
        @ColumnName("int1_col")
        val int1Col: Byte,
        @ColumnName("int2_col")
        val int2Col: Short,
        @ColumnName("int32_col")
        val int32Col: Int,
        @ColumnName("int4_col")
        val int4Col: Int,
        @ColumnName("int64_col")
        val int64Col: Long,
        @ColumnName("int8_col")
        val int8Col: Long,
        @ColumnName("int_col")
        val intCol: Int,
        @ColumnName("integer_col")
        val integerCol: Int,
        @ColumnName("interval_col")
        val intervalCol: String,
        @ColumnName("json_col")
        val jsonCol: JsonNode,
        @ColumnName("logical_col")
        val logicalCol: Boolean,
        @ColumnName("long_col")
        val longCol: Long,
        @ColumnName("numeric_col")
        val numericCol: BigDecimal,
        @ColumnName("real_col")
        val realCol: Float,
        @ColumnName("short_col")
        val shortCol: Short,
        @ColumnName("signed_col")
        val signedCol: Int,
        @ColumnName("smallint_col")
        val smallintCol: Short,
        @ColumnName("string_col")
        val stringCol: String,
        @ColumnName("text_col")
        val textCol: String,
        @ColumnName("time_col")
        val timeCol: LocalTime,
        @ColumnName("timestamp_col")
        val timestampCol: Timestamp,
        @ColumnName("timestamptz_col")
        val timestamptzCol: OffsetDateTime,
        @ColumnName("timestampwtz_col")
        val timestampwtzCol: OffsetDateTime,
        @ColumnName("tinyint_col")
        val tinyintCol: Byte,
        @ColumnName("ubigint_col")
        val ubigintCol: BigInteger,
        @ColumnName("uhugeint_col")
        val uhugeintCol: BigInteger,
        @ColumnName("uint128_col")
        val uint128Col: BigInteger,
        @ColumnName("uint16_col")
        val uint16Col: Int,
        @ColumnName("uint32_col")
        val uint32Col: Long,
        @ColumnName("uint64_col")
        val uint64Col: BigInteger,
        @ColumnName("uint8_col")
        val uint8Col: Short,
        @ColumnName("uint_col")
        val uintCol: Long,
        @ColumnName("usmallint_col")
        val usmallintCol: Int,
        @ColumnName("utinyint_col")
        val utinyintCol: Short,
        @ColumnName("uuid_col")
        val uuidCol: UUID,
        @ColumnName("varbinary_col")
        val varbinaryCol: Blob,
        @ColumnName("varchar_col")
        val varcharCol: String,
    ) {
        companion object {
            val expected = listOf(
                GeneralPurposeTypes(
                    bigintCol = 9223372036854775807L,
                    binaryCol = DuckDBResultSet.DuckDBBlobResult(ByteBuffer.wrap("DEADBEEF".toByteArray())),
                    bitCol = "1010",
                    bitstringCol = "1010",
                    blobCol = DuckDBResultSet.DuckDBBlobResult(ByteBuffer.wrap("DEADBEEF".toByteArray())),
                    boolCol = true,
                    booleanCol = true,
                    bpcharCol = "test",
                    byteaCol = DuckDBResultSet.DuckDBBlobResult(ByteBuffer.wrap("DEADBEEF".toByteArray())),
                    charCol = "test",
                    dateCol = LocalDate.parse("2025-06-19"),
                    datetimeCol = Timestamp.valueOf("2025-06-19 12:34:56"),
                    decimalCol = BigDecimal("123.45"),
                    doubleCol = 3.14159,
                    enumCol = "female",
                    float4Col = 3.14f,
                    float8Col = 3.14159,
                    floatCol = 3.14f,
                    hugeintCol = BigInteger("170141183460469231731687303715884105727"),
                    int128Col = BigInteger("170141183460469231731687303715884105727"),
                    int16Col = 32767,
                    int1Col = 127,
                    int2Col = 32767,
                    int32Col = 2147483647,
                    int4Col = 2147483647,
                    int64Col = 9223372036854775807L,
                    int8Col = 9223372036854775807L,
                    intCol = 2147483647,
                    integerCol = 2147483647,
                    intervalCol = "1 year",
                    jsonCol = JsonNode("{\"key\": \"value\"}"),
                    logicalCol = true,
                    longCol = 9223372036854775807L,
                    numericCol = BigDecimal("123.45"),
                    realCol = 3.14f,
                    shortCol = 32767,
                    signedCol = 2147483647,
                    smallintCol = 32767,
                    stringCol = "test string",
                    textCol = "test text",
                    timeCol = LocalTime.parse("12:34:56"),
                    timestampCol = Timestamp.valueOf("2025-06-19 12:34:56"),
                    timestamptzCol = OffsetDateTime.parse("2025-06-19T12:34:56+02:00"),
                    timestampwtzCol = OffsetDateTime.parse("2025-06-19T12:34:56+02:00"),
                    tinyintCol = 127,
                    ubigintCol = BigInteger("18446744073709551615"),
                    uhugeintCol = BigInteger("340282366920938463463374607431768211455"),
                    uint128Col = BigInteger("340282366920938463463374607431768211455"),
                    uint16Col = 65535,
                    uint32Col = 4294967295L,
                    uint64Col = BigInteger("18446744073709551615"),
                    uint8Col = 255,
                    uintCol = 4294967295L,
                    usmallintCol = 65535,
                    utinyintCol = 255,
                    uuidCol = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
                    varbinaryCol = DuckDBResultSet.DuckDBBlobResult(ByteBuffer.wrap("DEADBEEF".toByteArray())),
                    varcharCol = "test string",
                ),
            ).toDataFrame()
        }
    }

    @DataSchema
    data class NestedTypes(
        @ColumnName("ijstruct_col")
        val ijstructCol: java.sql.Struct,
        @ColumnName("intarray_col")
        val intarrayCol: java.sql.Array,
        @ColumnName("intlist_col")
        val intlistCol: java.sql.Array,
        @ColumnName("intstringmap_col")
        val intstringmapCol: Map<Int, String?>,
        @ColumnName("intstrinstinggmap_col")
        val intstrinstinggmapCol: Map<Int, Map<String, String?>?>,
        @ColumnName("stringarray_col")
        val stringarrayCol: java.sql.Array,
        @ColumnName("stringlist_col")
        val stringlistCol: java.sql.Array,
        @ColumnName("stringlistlist_col")
        val stringlistlistCol: java.sql.Array,
        @ColumnName("union_col")
        val unionCol: Any,
    )

    // endregion

    @Test
    fun `read simple dataframe from DuckDB`() {
        val df: AnyFrame
        val schema: DataFrameSchema
        val subset: AnyFrame
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

            df = DataFrame.readSqlTable(connection, "test_table")
            schema = DataFrame.getSchemaForSqlTable(connection, "test_table")

            subset = DataFrame.readSqlQuery(connection, """SELECT test_table.name, test_table.age FROM test_table""")
        }

        schema.compare(Person.expected.schema()).isSuperOrEqual() shouldBe true

        df.cast<Person>(verify = true) shouldBe Person.expected
        df.assertInferredTypesMatchSchema()

        subset.assertInferredTypesMatchSchema()
        subset["name"] shouldBe df["name"]
        subset["age"] shouldBe df["age"]
        subset.columnsCount() shouldBe 2
    }

    @Test
    fun `read simple dataframe from DuckDB ResultSet`() {
        val df: AnyFrame
        val schema: DataFrameSchema
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

            connection.prepareStatement("SELECT * FROM test_table").executeQuery().use { rs ->
                df = DataFrame.readResultSet(rs, DuckDb)
                schema = DataFrame.getSchemaForResultSet(rs, DuckDb)
            }
        }

        schema.compare(Person.expected.schema()).isSuperOrEqual() shouldBe true

        df.cast<Person>(verify = true) shouldBe Person.expected
        df.assertInferredTypesMatchSchema()
    }

    @Test
    fun `read all tables`() {
        val dfs: Map<String, AnyFrame>
        val schemas: Map<String, DataFrameSchema>
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

            dfs = DataFrame.readAllSqlTables(connection = connection)
            schemas = DataFrame.getSchemaForAllSqlTables(connection = connection)
        }

        val df = dfs["test_table"]!!
        val schema = schemas["test_table"]!!

        schema.compare(Person.expected.schema()).isSuperOrEqual() shouldBe true

        df.cast<Person>(verify = true) shouldBe Person.expected
        df.assertInferredTypesMatchSchema()
    }

    /**
     * https://duckdb.org/docs/stable/sql/data_types/overview.html
     */
    @Test
    fun `read each general-purpose DuckDB type`() {
        val df: AnyFrame
        val schema: DataFrameSchema
        DriverManager.getConnection(URL).use { connection ->
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

            schema = DataFrame.getSchemaForSqlTable(connection, "table1")
            df = DataFrame.readSqlTable(connection, "table1").reorderColumnsByName()
        }

        schema.compare(GeneralPurposeTypes.expected.schema()).isSuperOrEqual() shouldBe true

        // on some systems OffsetDateTime's get converted to UTC sometimes, let's compare them as Instant instead
        fun AnyFrame.fixOffsetDateTime() = convert { colsOf<OffsetDateTime>() }.with { it.toInstant() }

        df.cast<GeneralPurposeTypes>(verify = true).fixOffsetDateTime() shouldBe
            GeneralPurposeTypes.expected.fixOffsetDateTime()
        df.assertInferredTypesMatchSchema()
    }

    /**
     * https://duckdb.org/docs/stable/sql/data_types/overview.html
     */
    @Test
    fun `read each nested DuckDB type`() {
        val df: AnyFrame
        val schema: DataFrameSchema
        DriverManager.getConnection(URL).use { connection ->
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

            schema = DataFrame.getSchemaForSqlTable(connection, "table2")
            df = DataFrame.readSqlTable(connection, "table2")
        }

        df.assertInferredTypesMatchSchema()

        df.cast<NestedTypes>(verify = true)
        df as DataFrame<NestedTypes>

        df.single().let {
            it[{ "intarray_col"<java.sql.Array>() }].array shouldBe arrayOf(1, 2, null)
            it[{ "stringarray_col"<java.sql.Array>() }].array shouldBe arrayOf("a", "ab", "abc")
            it[{ "intlist_col"<java.sql.Array>() }].array shouldBe arrayOf(1, 2, 3)
            it[{ "stringlist_col"<java.sql.Array>() }].array shouldBe arrayOf("a", "ab", "abc")
            (it[{ "stringlistlist_col"<java.sql.Array>() }].array as Array<*>)
                .map { (it as java.sql.Array?)?.array } shouldBe listOf(arrayOf("a", "ab"), arrayOf("abc"), null)
            it[{ "intstringmap_col"<Map<Int, String?>>() }] shouldBe mapOf(1 to "value1", 200 to "value2")
            it[{ "intstrinstinggmap_col"<Map<Int, Map<String, String?>>>() }] shouldBe mapOf(
                1 to mapOf("value1" to "a", "value2" to "b"),
                200 to mapOf("value1" to "c", "value2" to "d"),
            )
            it[{ "ijstruct_col"<java.sql.Struct>() }].attributes shouldBe arrayOf<Any>(42, "answer")
            it[{ "union_col"<Any>() }] shouldBe 2
        }
    }

    @Test
    fun `change read mode`() {
        // Test in-memory database (cannot be read-only)
        val config = DbConnectionConfig("jdbc:duckdb:")
        val df = config.readDataFrame("SELECT 1, 2, 3")
        df.values().toList() shouldBe listOf(1, 2, 3)
    }

    @Test
    fun `change read mode with persistent database`() {
        // Test read-only mode with a temporary file
        val tempDir = createTempDirectory("duckdb-test-")
        val dbPath = tempDir.resolve("test.duckdb")
        try {
            // First, create the database with actual data using plain JDBC to allow DDL/DML
            DriverManager.getConnection("jdbc:duckdb:${dbPath.toAbsolutePath()}").use { connection ->
                connection.createStatement().use { st ->
                    st.executeUpdate("CREATE TABLE test_data(col1 INTEGER, col2 INTEGER, col3 INTEGER)")
                    st.executeUpdate("INSERT INTO test_data VALUES (1, 2, 3)")
                }
            }

            // Now test read-only access via our API
            val config = DbConnectionConfig("jdbc:duckdb:${dbPath.toAbsolutePath()}", readOnly = true)
            val df = config.readDataFrame("SELECT col1, col2, col3 FROM test_data")
            df.values().toList() shouldBe listOf(1, 2, 3)
        } finally {
            Files.deleteIfExists(dbPath)
            Files.deleteIfExists(tempDir)
        }
    }
}
