package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.io.db.DuckDb
import org.jetbrains.kotlinx.dataframe.io.db.H2
import org.jetbrains.kotlinx.dataframe.io.db.Sqlite
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Blob
import java.sql.Clob
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Time
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Date
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * The same audit as [assertColumnTypesMatchValues] runs for the containerized databases, for the three
 * embedded ones, plus the [assertColumnTypes] pin of the type-mapping pages for each. They need no
 * Docker, so this runs in the ordinary `test` task.
 */
class EmbeddedColumnTypeAuditTest {

    @Test
    fun `H2 declared column types accept the values the driver returns`() {
        withH2 { assertColumnTypesMatchValues(H2(H2.Mode.Regular), H2_AUDIT_DDL, H2_AUDIT_INSERT) }
    }

    @Test
    fun `SQLite declared column types accept the values the driver returns`() {
        withSqlite { assertColumnTypesMatchValues(Sqlite.default, SQLITE_AUDIT_DDL, SQLITE_AUDIT_INSERT) }
    }

    @Test
    fun `DuckDB declared column types accept the values the driver returns`() {
        withDuckDb { assertColumnTypesMatchValues(DuckDb, DUCKDB_AUDIT_DDL, DUCKDB_AUDIT_INSERT) }
    }

    @Test
    fun `H2 columns are read as the type-mapping page documents`() {
        withH2 { assertColumnTypes(H2(H2.Mode.Regular), H2_AUDIT_DDL, H2_AUDIT_INSERT, H2_EXPECTED_TYPES) }
    }

    @Test
    fun `SQLite columns are read as the type-mapping page documents`() {
        withSqlite { assertColumnTypes(Sqlite.default, SQLITE_AUDIT_DDL, SQLITE_AUDIT_INSERT, SQLITE_EXPECTED_TYPES) }
    }

    @Test
    fun `DuckDB columns are read as the type-mapping page documents`() {
        withDuckDb { assertColumnTypes(DuckDb, DUCKDB_AUDIT_DDL, DUCKDB_AUDIT_INSERT, DUCKDB_EXPECTED_TYPES) }
    }

    private fun withH2(body: Connection.() -> Unit) =
        DriverManager.getConnection("jdbc:h2:mem:columnTypeAudit;DB_CLOSE_DELAY=-1").use(body)

    private fun withSqlite(body: Connection.() -> Unit) = DriverManager.getConnection("jdbc:sqlite::memory:").use(body)

    private fun withDuckDb(body: Connection.() -> Unit) = DriverManager.getConnection("jdbc:duckdb:").use(body)
}

@Language("SQL")
private val H2_AUDIT_DDL =
    """
    CREATE TABLE column_type_audit(
      tinyintCol TINYINT, smallintCol SMALLINT, intCol INT, bigintCol BIGINT,
      numericCol NUMERIC(10,2), decfloatCol DECFLOAT, realCol REAL, doubleCol DOUBLE PRECISION,
      booleanCol BOOLEAN,
      charCol CHAR(10), varcharCol VARCHAR(50), varcharIgnorecaseCol VARCHAR_IGNORECASE(50),
      clobCol CLOB,
      binaryCol BINARY(10), varbinaryCol VARBINARY(10), blobCol BLOB,
      uuidCol UUID, enumCol ENUM('x','y'),
      dateCol DATE, timeCol TIME, timeTzCol TIME WITH TIME ZONE,
      timestampCol TIMESTAMP, timestampTzCol TIMESTAMP WITH TIME ZONE,
      intervalCol INTERVAL DAY TO SECOND,
      jsonCol JSON, geometryCol GEOMETRY,
      intArrayCol INT ARRAY
    )
    """.trimIndent()

@Language("SQL")
private val H2_AUDIT_INSERT =
    """
    INSERT INTO column_type_audit VALUES(1,1,1,1,1.5,1.5,1.5,1.5,TRUE,
      'a','a','a','a',X'01',X'01',X'01',
      RANDOM_UUID(),'x',
      DATE '2024-01-01',TIME '10:00:00',TIME WITH TIME ZONE '10:00:00+02',
      TIMESTAMP '2024-01-01 10:00:00',TIMESTAMP WITH TIME ZONE '2024-01-01 10:00:00+02',
      INTERVAL '1' DAY,
      JSON '{"k":1}','POINT(1 1)',
      ARRAY[1,2])
    """.trimIndent()

@Language("SQL")
private val SQLITE_AUDIT_DDL =
    """
    CREATE TABLE column_type_audit(
      integerCol INTEGER, intCol INT, bigintCol BIGINT, tinyintCol TINYINT, smallintCol SMALLINT,
      realCol REAL, doubleCol DOUBLE, floatCol FLOAT,
      numericCol NUMERIC, decimalCol DECIMAL(10,2), booleanCol BOOLEAN,
      textCol TEXT, varcharCol VARCHAR(50), charCol CHARACTER(10), clobCol CLOB,
      blobCol BLOB, dateCol DATE, datetimeCol DATETIME
    )
    """.trimIndent()

@Language("SQL")
private val SQLITE_AUDIT_INSERT =
    """
    INSERT INTO column_type_audit VALUES(1,1,1,1,1,1.5,1.5,1.5,1.5,1.5,1,
      'a','a','a','a',X'01','2024-01-01','2024-01-01 10:00:00')
    """.trimIndent()

@Language("SQL")
private val DUCKDB_AUDIT_DDL =
    """
    CREATE TABLE column_type_audit(
      tinyintCol TINYINT, smallintCol SMALLINT, integerCol INTEGER, bigintCol BIGINT,
      hugeintCol HUGEINT, utinyintCol UTINYINT, usmallintCol USMALLINT, uintegerCol UINTEGER,
      ubigintCol UBIGINT,
      floatCol FLOAT, doubleCol DOUBLE, decimalCol DECIMAL(10,2), booleanCol BOOLEAN,
      varcharCol VARCHAR, blobCol BLOB, uuidCol UUID,
      dateCol DATE, timeCol TIME, timestampCol TIMESTAMP, timestampTzCol TIMESTAMPTZ,
      intervalCol INTERVAL,
      listCol INTEGER[], structCol STRUCT(a INTEGER, b VARCHAR), mapCol MAP(VARCHAR, INTEGER)
    )
    """.trimIndent()

@Language("SQL")
private val DUCKDB_AUDIT_INSERT =
    """
    INSERT INTO column_type_audit VALUES(1,1,1,1,1,1,1,1,1,1.5,1.5,1.5,TRUE,
      'a','\x01'::BLOB,'80000000-0000-0000-0000-000000000000'::UUID,
      DATE '2024-01-01',TIME '10:00:00',TIMESTAMP '2024-01-01 10:00:00',
      TIMESTAMPTZ '2024-01-01 10:00:00+02',INTERVAL 1 DAY,
      [1,2],{'a': 1, 'b': 'x'},MAP{'k': 1})
    """.trimIndent()

/**
 * The column type every probe column of [H2_AUDIT_DDL] is read as, taken from a real read — the claim
 * `readSqlTypeMapping_H2.md` publishes. See [assertColumnTypes]; H2 reports unquoted identifiers
 * upper-cased.
 *
 * `ENUM`, `GEOMETRY` and `INTERVAL` really are read as [Any] today, and `JSON` as its raw bytes; the
 * page says so, and these entries are what keeps it honest rather than an endorsement of the mapping.
 */
private val H2_EXPECTED_TYPES: Map<String, KType> = mapOf(
    "TINYINTCOL" to typeOf<Int>(),
    "SMALLINTCOL" to typeOf<Int>(),
    "INTCOL" to typeOf<Int>(),
    "BIGINTCOL" to typeOf<Long>(),
    "NUMERICCOL" to typeOf<BigDecimal>(),
    "DECFLOATCOL" to typeOf<BigDecimal>(),
    "REALCOL" to typeOf<Float>(),
    "DOUBLECOL" to typeOf<Double>(),
    "BOOLEANCOL" to typeOf<Boolean>(),
    "CHARCOL" to typeOf<String>(),
    "VARCHARCOL" to typeOf<String>(),
    "VARCHARIGNORECASECOL" to typeOf<String>(),
    "CLOBCOL" to typeOf<Clob>(),
    "BINARYCOL" to typeOf<ByteArray>(),
    "VARBINARYCOL" to typeOf<ByteArray>(),
    "BLOBCOL" to typeOf<Blob>(),
    "UUIDCOL" to typeOf<Uuid>(),
    "ENUMCOL" to typeOf<Any>(),
    "DATECOL" to typeOf<Date>(),
    "TIMECOL" to typeOf<Time>(),
    "TIMETZCOL" to typeOf<OffsetTime>(),
    "TIMESTAMPCOL" to typeOf<Instant>(),
    "TIMESTAMPTZCOL" to typeOf<OffsetDateTime>(),
    "INTERVALCOL" to typeOf<Any>(),
    "JSONCOL" to typeOf<ByteArray>(),
    "GEOMETRYCOL" to typeOf<Any>(),
    "INTARRAYCOL" to typeOf<Array<*>>(),
)

/**
 * The column type every probe column of [SQLITE_AUDIT_DDL] is read as, taken from a real read — the
 * claim `readSqlTypeMapping_SQLite.md` publishes. See [assertColumnTypes].
 *
 * `bigintCol` is the case #2087 fixed: Xerial reports and returns an `Integer` for it because the
 * stored value fits in one, and the column is still read as [Long] because its *declared* type says so.
 */
private val SQLITE_EXPECTED_TYPES: Map<String, KType> = mapOf(
    "integerCol" to typeOf<Int>(),
    "intCol" to typeOf<Int>(),
    "bigintCol" to typeOf<Long>(),
    "tinyintCol" to typeOf<Int>(),
    "smallintCol" to typeOf<Int>(),
    "realCol" to typeOf<Double>(),
    "doubleCol" to typeOf<Double>(),
    "floatCol" to typeOf<Double>(),
    "numericCol" to typeOf<Double>(),
    "decimalCol" to typeOf<Double>(),
    "booleanCol" to typeOf<Boolean>(),
    "textCol" to typeOf<String>(),
    "varcharCol" to typeOf<String>(),
    "charCol" to typeOf<String>(),
    "clobCol" to typeOf<String>(),
    "blobCol" to typeOf<ByteArray>(),
    "dateCol" to typeOf<LocalDate>(),
    "datetimeCol" to typeOf<LocalDateTime>(),
)

/**
 * The column type every probe column of [DUCKDB_AUDIT_DDL] is read as, taken from a real read — the
 * claim `readSqlTypeMapping_DuckDB.md` publishes. See [assertColumnTypes].
 *
 * The three structured types are what `DuckDb` exists for: a `LIST` becomes a [List] column, a
 * `STRUCT` a [org.jetbrains.kotlinx.dataframe.DataRow] one, and a `MAP` a [Map] one. The unsigned
 * integer types each widen to the smallest Kotlin type that fits them.
 */
private val DUCKDB_EXPECTED_TYPES: Map<String, KType> = mapOf(
    "tinyintCol" to typeOf<Byte>(),
    "smallintCol" to typeOf<Short>(),
    "integerCol" to typeOf<Int>(),
    "bigintCol" to typeOf<Long>(),
    "hugeintCol" to typeOf<BigInteger>(),
    "utinyintCol" to typeOf<Short>(),
    "usmallintCol" to typeOf<Int>(),
    "uintegerCol" to typeOf<Long>(),
    "ubigintCol" to typeOf<BigInteger>(),
    "floatCol" to typeOf<Float>(),
    "doubleCol" to typeOf<Double>(),
    "decimalCol" to typeOf<BigDecimal>(),
    "booleanCol" to typeOf<Boolean>(),
    "varcharCol" to typeOf<String>(),
    "blobCol" to typeOf<Blob>(),
    "uuidCol" to typeOf<Uuid>(),
    "dateCol" to typeOf<LocalDate>(),
    "timeCol" to typeOf<LocalTime>(),
    "timestampCol" to typeOf<Instant>(),
    "timestampTzCol" to typeOf<OffsetDateTime>(),
    "intervalCol" to typeOf<String>(),
    "listCol" to typeOf<List<Int?>>(),
    "structCol" to typeOf<DataRow<*>>(),
    "mapCol" to typeOf<Map<String, Int?>>(),
)
