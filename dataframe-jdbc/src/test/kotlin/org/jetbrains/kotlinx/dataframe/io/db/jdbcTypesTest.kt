@file:Suppress("ClassName")

package org.jetbrains.kotlinx.dataframe.io.db

import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.postgresql.geometric.PGbox
import org.postgresql.geometric.PGcircle
import org.postgresql.geometric.PGline
import org.postgresql.geometric.PGlseg
import org.postgresql.geometric.PGpath
import org.postgresql.geometric.PGpoint
import org.postgresql.geometric.PGpolygon
import org.postgresql.util.PGInterval
import org.postgresql.util.PGmoney
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Blob
import java.sql.Clob
import java.sql.NClob
import java.sql.Ref
import java.sql.RowId
import java.sql.SQLXML
import java.sql.Time
import java.sql.Types
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Date
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalDate as KotlinLocalDate
import kotlinx.datetime.LocalDateTime as KotlinLocalDateTime
import kotlinx.datetime.LocalTime as KotlinLocalTime

/**
 * Non-integration tests for [DbType.getExpectedJdbcType] and related type-mapping logic.
 *
 * Each DB owns a [TypeMapping] list that acts as the source of truth for its SQL → Kotlin type
 * mapping. The list is exercised for both nullable and non-nullable columns.
 *
 * See https://github.com/Kotlin/dataframe/issues/1736.
 */
@RunWith(Enclosed::class)
class JdbcTypesTest {

    class DefaultDbTypeTypes {

        // A concrete DbType whose behavior is exactly the default one from the base class.
        private object DefaultDbType : DbType("default") {
            override val driverClassName: String get() = "does.not.matter"

            override fun isSystemTable(tableMetadata: TableMetadata): Boolean = false

            override fun buildTableMetadata(tables: java.sql.ResultSet): TableMetadata =
                TableMetadata("t", null, null)
        }

        @Test
        fun `common SQL types map to the expected Kotlin type`() {
            assertMappings(DefaultDbType, commonJdbcTypeMappings)
        }

        @Test
        fun `TIMESTAMP with LocalDateTime driver class maps to java_time_LocalDateTime`() {
            assertMappings(DefaultDbType, listOf(timestampAsLocalDateTime))
        }

        @Test
        fun `BINARY with UUID driver class maps to UUID`() {
            assertMappings(DefaultDbType, listOf(binaryAsUuid))
        }

        @Test
        fun `Types_OTHER with byte array javaClassName maps to ByteArray`() {
            DefaultDbType.getExpectedJdbcType(
                createColumnMetadata(
                    sqlTypeName = "OTHER",
                    jdbcType = Types.OTHER,
                    javaClassName = "[B",
                    isNullable = false,
                ),
            ) shouldBe typeOf<ByteArray>()
        }

        @Test
        fun `Types_OTHER with generic Object maps to Any`() {
            DefaultDbType.getExpectedJdbcType(
                createColumnMetadata(
                    sqlTypeName = "OTHER",
                    jdbcType = Types.OTHER,
                    javaClassName = "java.lang.Object",
                    isNullable = true,
                ),
            ) shouldBe typeOf<Any?>()
        }

        @Test
        fun `unknown jdbcType falls back to String`() {
            DefaultDbType.getExpectedJdbcType(
                createColumnMetadata(
                    sqlTypeName = "MADE_UP",
                    jdbcType = UNKNOWN_JDBC_TYPE,
                    javaClassName = "java.lang.Object",
                    isNullable = false,
                ),
            ) shouldBe typeOf<String>()
        }
    }

    class MariaDbTypes {

        @Test
        fun `common SQL types map to the expected Kotlin type`() {
            assertMappings(MariaDb, commonJdbcTypeMappings)
        }

        @Test
        fun `MariaDB-specific overrides`() {
            assertMappings(MariaDb, mariaDbSpecificMappings)
        }

        @Test
        fun `unknown jdbcType falls back to String`() {
            assertUnknownMapsToString(MariaDb)
        }
    }

    class MySqlTypes {

        @Test
        fun `common SQL types map to the expected Kotlin type`() {
            assertMappings(MySql, commonJdbcTypeMappings)
        }

        @Test
        fun `MySQL-specific overrides`() {
            assertMappings(MySql, mySqlSpecificMappings)
        }

        @Test
        fun `unknown jdbcType falls back to String`() {
            assertUnknownMapsToString(MySql)
        }
    }

    /**
     * SQLite is dynamically typed: it has only 5 storage classes (NULL, INTEGER, REAL, TEXT, BLOB)
     * and picks one per row based on the value, guided by "type affinity" derived from the
     * declared column type. The Xerial JDBC driver reports metadata based on the actual stored
     * value, so `getExpectedJdbcType` sees driver-specific `jdbcType` and `javaClassName` combos
     * that differ from other databases. The tests below reflect that.
     */
    class SqliteTypes {

        @Test
        fun `INTEGER affinity — declared int-like types map to Int or Long`() {
            assertMappings(Sqlite.default, sqliteIntegerAffinityMappings)
        }

        @Test
        fun `REAL affinity — declared real-like types map to Double`() {
            assertMappings(Sqlite.default, sqliteRealAffinityMappings)
        }

        @Test
        fun `TEXT affinity — declared text-like types map to String`() {
            assertMappings(Sqlite.default, sqliteTextAffinityMappings)
        }

        @Test
        fun `BLOB affinity — declared BLOB maps to ByteArray`() {
            assertMappings(Sqlite.default, sqliteBlobAffinityMappings)
        }

        @Test
        fun `NUMERIC affinity — declared numeric-like types map by declared type`() {
            assertMappings(Sqlite.default, sqliteNumericAffinityMappings)
        }

        @Test
        fun `BOOLEAN declared type resolves to Boolean`() {
            // Xerial reports Types.BOOLEAN metadata even though values are stored as INTEGER;
            // the schema type is Boolean, and `getValueFromResultSet` converts each row via
            // `rs.getBoolean` (see Sqlite.getValueFromResultSet).
            Sqlite.default.getExpectedJdbcType(
                createColumnMetadata(
                    sqlTypeName = "BOOLEAN",
                    jdbcType = Types.BOOLEAN,
                    javaClassName = "java.lang.Integer",
                    isNullable = false,
                ),
            ) shouldBe typeOf<Boolean>()

            Sqlite.default.getExpectedJdbcType(
                createColumnMetadata(
                    sqlTypeName = "BOOLEAN",
                    jdbcType = Types.BOOLEAN,
                    javaClassName = "java.lang.Integer",
                    isNullable = true,
                ),
            ) shouldBe typeOf<Boolean?>()
        }

        @Test
        fun `unrecognised declared type is treated by NUMERIC affinity`() {
            // For an unknown declared type Xerial applies NUMERIC affinity and reports the
            // jdbcType of the actual stored value; for a text sample that is VARCHAR.
            Sqlite.default.getExpectedJdbcType(
                createColumnMetadata(
                    sqlTypeName = "CUSTOM_TYPE",
                    jdbcType = Types.VARCHAR,
                    javaClassName = "java.lang.String",
                    isNullable = false,
                ),
            ) shouldBe typeOf<String>()
        }

        @Test
        fun `customTypesMap overrides the default mapping by SQL type name`() {
            val custom = Sqlite(
                customTypesMap = mapOf(
                    "INTEGER" to typeOf<Long>(),
                    "MY_TYPE" to typeOf<String>(),
                ),
            )
            // INTEGER is normally Int, but is overridden to Long
            custom.getExpectedJdbcType(
                createColumnMetadata(
                    sqlTypeName = "INTEGER",
                    jdbcType = Types.INTEGER,
                    javaClassName = "java.lang.Integer",
                    isNullable = true,
                ),
            ) shouldBe typeOf<Long?>()
            // Custom type name is respected as-is
            custom.getExpectedJdbcType(
                createColumnMetadata(
                    sqlTypeName = "MY_TYPE",
                    jdbcType = Types.OTHER,
                    javaClassName = "java.lang.Object",
                    isNullable = false,
                ),
            ) shouldBe typeOf<String>()
        }

        @Test
        fun `unknown jdbcType falls back to String`() {
            assertUnknownMapsToString(Sqlite.default)
        }
    }

    class PostgreSqlTypes {

        @Test
        fun `common SQL types map to the expected Kotlin type`() {
            assertMappings(PostgreSql, commonJdbcTypeMappings)
        }

        @Test
        fun `PGobject types map to their PGobject Kotlin types`() {
            assertMappings(PostgreSql, postgreSqlSpecificMappings)
        }

        @Test
        fun `PGobject lookup is case-insensitive`() {
            PostgreSql.getExpectedJdbcType(
                createColumnMetadata(
                    sqlTypeName = "POINT",
                    jdbcType = Types.OTHER,
                    javaClassName = "org.postgresql.geometric.PGpoint",
                    isNullable = true,
                ),
            ) shouldBe typeOf<PGpoint?>()
        }

        @Test
        fun `unknown jdbcType falls back to String`() {
            assertUnknownMapsToString(PostgreSql)
        }
    }

    class MsSqlTypes {

        @Test
        fun `common SQL types map to the expected Kotlin type`() {
            assertMappings(MsSql, commonJdbcTypeMappings)
        }

        @Test
        fun `unknown jdbcType falls back to String`() {
            assertUnknownMapsToString(MsSql)
        }
    }

    class H2Types {

        @Test
        fun `Regular mode uses default type mappings`() {
            assertMappings(H2(H2.Mode.Regular), commonJdbcTypeMappings)
        }

        @Test
        fun `MySql mode delegates to MySQL-specific overrides`() {
            assertMappings(H2(H2.Mode.MySql), mySqlSpecificMappings)
        }

        @Test
        fun `MariaDb mode delegates to MariaDB-specific overrides`() {
            assertMappings(H2(H2.Mode.MariaDb), mariaDbSpecificMappings)
        }

        @Test
        fun `unknown jdbcType falls back to String`() {
            assertUnknownMapsToString(H2(H2.Mode.Regular))
        }
    }
}

// -------------------- Type mapping model & helpers --------------------

/**
 * A single row in the JDBC → Kotlin type mapping table.
 *
 * @property sqlTypeName the human-readable SQL type name (e.g. "BIGINT")
 * @property jdbcType a constant from [java.sql.Types]
 * @property javaClassName the JDBC-reported class name for this column (as returned by
 *   [java.sql.ResultSetMetaData.getColumnClassName])
 * @property expectedType the expected non-nullable Kotlin type
 */
internal data class TypeMapping(
    val sqlTypeName: String,
    val jdbcType: Int,
    val javaClassName: String,
    val expectedType: KType,
)

internal const val UNKNOWN_JDBC_TYPE: Int = -9999

/**
 * Test helper that constructs a [TableColumnMetadata] with sensible defaults.
 * Not a mock — a lightweight factory to keep test call sites readable.
 */
internal fun createColumnMetadata(
    name: String = "col",
    sqlTypeName: String,
    jdbcType: Int,
    size: Int = 10,
    javaClassName: String,
    isNullable: Boolean,
): TableColumnMetadata =
    TableColumnMetadata(
        name = name,
        sqlTypeName = sqlTypeName,
        jdbcType = jdbcType,
        size = size,
        javaClassName = javaClassName,
        isNullable = isNullable,
    )

/**
 * Verifies each mapping resolves correctly for both nullable and non-nullable columns.
 * Runs the full type-resolution pipeline (`getExpectedJdbcType` → `getPreprocessedValueType`)
 * and compares against the **final DataFrame column type**, which is what the reference
 * documentation describes.
 */
internal fun assertMappings(dbType: DbType, mappings: List<TypeMapping>) {
    mappings.forEach { m ->
        listOf(false, true).forEach { isNullable ->
            val meta = createColumnMetadata(
                sqlTypeName = m.sqlTypeName,
                jdbcType = m.jdbcType,
                javaClassName = m.javaClassName,
                isNullable = isNullable,
            )
            val jdbcType = dbType.getExpectedJdbcType(meta)
            val finalType = dbType.getPreprocessedValueType(meta, jdbcType)
            finalType shouldBe m.expectedType.withNullability(isNullable)
        }
    }
}

internal fun assertUnknownMapsToString(dbType: DbType) {
    dbType.getExpectedJdbcType(
        createColumnMetadata(
            sqlTypeName = "MADE_UP",
            jdbcType = UNKNOWN_JDBC_TYPE,
            javaClassName = "java.lang.Object",
            isNullable = false,
        ),
    ) shouldBe typeOf<String>()

    dbType.getExpectedJdbcType(
        createColumnMetadata(
            sqlTypeName = "MADE_UP",
            jdbcType = UNKNOWN_JDBC_TYPE,
            javaClassName = "java.lang.Object",
            isNullable = true,
        ),
    ) shouldBe typeOf<String?>()
}

// -------------------- Type mapping tables --------------------

/**
 * The default SQL → Kotlin type mapping applied by [DbType].
 * Every DB that does not override the given entry falls through to this table.
 */
internal val commonJdbcTypeMappings: List<TypeMapping> = listOf(
    TypeMapping("BIT", Types.BIT, "java.lang.Boolean", typeOf<Boolean>()),
    TypeMapping("TINYINT", Types.TINYINT, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("SMALLINT", Types.SMALLINT, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("INTEGER", Types.INTEGER, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("BIGINT", Types.BIGINT, "java.lang.Long", typeOf<Long>()),
    TypeMapping("FLOAT", Types.FLOAT, "java.lang.Float", typeOf<Float>()),
    TypeMapping("REAL", Types.REAL, "java.lang.Float", typeOf<Float>()),
    TypeMapping("DOUBLE", Types.DOUBLE, "java.lang.Double", typeOf<Double>()),
    TypeMapping("NUMERIC", Types.NUMERIC, "java.math.BigDecimal", typeOf<BigDecimal>()),
    TypeMapping("DECIMAL", Types.DECIMAL, "java.math.BigDecimal", typeOf<BigDecimal>()),
    TypeMapping("CHAR", Types.CHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("VARCHAR", Types.VARCHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("LONGVARCHAR", Types.LONGVARCHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("NCHAR", Types.NCHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("NVARCHAR", Types.NVARCHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("LONGNVARCHAR", Types.LONGNVARCHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("DATE", Types.DATE, "java.sql.Date", typeOf<Date>()),
    TypeMapping("TIME", Types.TIME, "java.sql.Time", typeOf<Time>()),
    TypeMapping("TIMESTAMP", Types.TIMESTAMP, "java.sql.Timestamp", typeOf<Instant>()),
    TypeMapping("TIME_WITH_TIMEZONE", Types.TIME_WITH_TIMEZONE, "java.time.OffsetTime", typeOf<OffsetTime>()),
    TypeMapping(
        "TIMESTAMP_WITH_TIMEZONE",
        Types.TIMESTAMP_WITH_TIMEZONE,
        "java.time.OffsetDateTime",
        typeOf<OffsetDateTime>(),
    ),
    TypeMapping("BINARY", Types.BINARY, "[B", typeOf<ByteArray>()),
    TypeMapping("VARBINARY", Types.VARBINARY, "[B", typeOf<ByteArray>()),
    TypeMapping("LONGVARBINARY", Types.LONGVARBINARY, "[B", typeOf<ByteArray>()),
    TypeMapping("BOOLEAN", Types.BOOLEAN, "java.lang.Boolean", typeOf<Boolean>()),
    TypeMapping("BLOB", Types.BLOB, "java.sql.Blob", typeOf<Blob>()),
    TypeMapping("CLOB", Types.CLOB, "java.sql.Clob", typeOf<Clob>()),
    TypeMapping("NCLOB", Types.NCLOB, "java.sql.NClob", typeOf<NClob>()),
    TypeMapping("SQLXML", Types.SQLXML, "java.sql.SQLXML", typeOf<SQLXML>()),
    TypeMapping("ROWID", Types.ROWID, "java.sql.RowId", typeOf<RowId>()),
    TypeMapping("REF", Types.REF, "java.sql.Ref", typeOf<Ref>()),
    TypeMapping("REF_CURSOR", Types.REF_CURSOR, "java.sql.Ref", typeOf<Ref>()),
)

/** TIMESTAMP columns whose JDBC-reported class is `java.time.LocalDateTime` are preprocessed to `kotlinx.datetime.LocalDateTime`. */
internal val timestampAsLocalDateTime: TypeMapping = TypeMapping(
    sqlTypeName = "TIMESTAMP",
    jdbcType = Types.TIMESTAMP,
    javaClassName = "java.time.LocalDateTime",
    expectedType = typeOf<KotlinLocalDateTime>(),
)

/** BINARY columns storing UUIDs (`java.util.UUID`) are preprocessed to `kotlin.uuid.Uuid`. */
internal val binaryAsUuid: TypeMapping = TypeMapping(
    sqlTypeName = "BINARY",
    jdbcType = Types.BINARY,
    javaClassName = "java.util.UUID",
    expectedType = typeOf<Uuid>(),
)

/**
 * MariaDB overrides on top of [commonJdbcTypeMappings]. Also includes entries that reflect
 * the MariaDB JDBC driver's specific `javaClassName` reporting (e.g. `BLOB` as `[B`,
 * `SMALLINT` as `java.lang.Short`).
 */
internal val mariaDbSpecificMappings: List<TypeMapping> = listOf(
    TypeMapping("BIGINT UNSIGNED", Types.BIGINT, "java.math.BigInteger", typeOf<BigInteger>()),
    TypeMapping("INT UNSIGNED", Types.INTEGER, "java.lang.Long", typeOf<Long>()),
    TypeMapping("INTEGER UNSIGNED", Types.INTEGER, "java.lang.Long", typeOf<Long>()),
    TypeMapping("SMALLINT", Types.SMALLINT, "java.lang.Short", typeOf<Short>()),
    // MariaDB reports BLOB columns as `[B` (byte array), not `java.sql.Blob`.
    TypeMapping("BLOB", Types.BLOB, "[B", typeOf<ByteArray>()),
    // YEAR columns are reported as Types.DATE by the driver.
    TypeMapping("YEAR", Types.DATE, "java.sql.Date", typeOf<Date>()),
)

/** MySQL overrides on top of [commonJdbcTypeMappings]. */
internal val mySqlSpecificMappings: List<TypeMapping> = listOf(
    TypeMapping("BIGINT UNSIGNED", Types.BIGINT, "java.math.BigInteger", typeOf<BigInteger>()),
    TypeMapping("INT UNSIGNED", Types.INTEGER, "java.lang.Long", typeOf<Long>()),
)

/** PostgreSQL PGobject overrides — matched by `sqlTypeName` (case-insensitively). */
internal val postgreSqlSpecificMappings: List<TypeMapping> = listOf(
    TypeMapping("box", Types.OTHER, "org.postgresql.geometric.PGbox", typeOf<PGbox>()),
    TypeMapping("circle", Types.OTHER, "org.postgresql.geometric.PGcircle", typeOf<PGcircle>()),
    TypeMapping("line", Types.OTHER, "org.postgresql.geometric.PGline", typeOf<PGline>()),
    TypeMapping("lseg", Types.OTHER, "org.postgresql.geometric.PGlseg", typeOf<PGlseg>()),
    TypeMapping("path", Types.OTHER, "org.postgresql.geometric.PGpath", typeOf<PGpath>()),
    TypeMapping("point", Types.OTHER, "org.postgresql.geometric.PGpoint", typeOf<PGpoint>()),
    TypeMapping("polygon", Types.OTHER, "org.postgresql.geometric.PGpolygon", typeOf<PGpolygon>()),
    TypeMapping("money", Types.OTHER, "org.postgresql.util.PGmoney", typeOf<PGmoney>()),
    TypeMapping("interval", Types.OTHER, "org.postgresql.util.PGInterval", typeOf<PGInterval>()),
)

// -------------------- SQLite-specific mappings --------------------
//
// SQLite has only 5 storage classes (NULL, INTEGER, REAL, TEXT, BLOB) and applies "type affinity"
// per column based on the declared type:
//   - INTEGER affinity: declared type contains "INT"
//   - TEXT    affinity: declared type contains "CHAR", "CLOB", or "TEXT"
//   - BLOB    affinity: declared type contains "BLOB" or is missing
//   - REAL    affinity: declared type contains "REAL", "FLOA", or "DOUB"
//   - NUMERIC affinity: everything else (e.g. BOOLEAN, DATE, DATETIME, DECIMAL, NUMERIC, ...)
//
// The Xerial JDBC driver reports the declared type in `sqlTypeName` and infers `jdbcType` from
// affinity, but reports `javaClassName` from the *actual stored value*. The mappings below
// reflect what `getExpectedJdbcType` returns for a column of the given declared type once
// a representative value has been inserted (i.e. the common case for reading data).

/** INTEGER affinity — declared int-like types storing an int value. */
internal val sqliteIntegerAffinityMappings: List<TypeMapping> = listOf(
    TypeMapping("INTEGER", Types.INTEGER, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("INT", Types.INTEGER, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("MEDIUMINT", Types.INTEGER, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("TINYINT", Types.TINYINT, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("SMALLINT", Types.SMALLINT, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("INT2", Types.SMALLINT, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("BIGINT", Types.BIGINT, "java.lang.Integer", typeOf<Long>()),
    TypeMapping("UNSIGNED BIG INT", Types.BIGINT, "java.lang.Integer", typeOf<Long>()),
    TypeMapping("INT8", Types.BIGINT, "java.lang.Integer", typeOf<Long>()),
)

/** REAL affinity — declared real-like types storing a floating-point value. */
internal val sqliteRealAffinityMappings: List<TypeMapping> = listOf(
    TypeMapping("REAL", Types.REAL, "java.lang.Double", typeOf<Double>()),
    TypeMapping("FLOAT", Types.FLOAT, "java.lang.Double", typeOf<Double>()),
    TypeMapping("DOUBLE", Types.DOUBLE, "java.lang.Double", typeOf<Double>()),
    TypeMapping("DOUBLE PRECISION", Types.DOUBLE, "java.lang.Double", typeOf<Double>()),
)

/** TEXT affinity — declared text-like types storing a string. */
internal val sqliteTextAffinityMappings: List<TypeMapping> = listOf(
    TypeMapping("TEXT", Types.VARCHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("VARCHAR", Types.VARCHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("NVARCHAR", Types.VARCHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("CHAR", Types.CHAR, "java.lang.String", typeOf<String>()),
    TypeMapping("NCHAR", Types.CHAR, "java.lang.String", typeOf<String>()),
    // CLOB falls through to `defaultJdbcTypeToKTypeMapping[Types.CLOB]` (= java.sql.Clob),
    // regardless of the reported javaClassName.
    TypeMapping("CLOB", Types.CLOB, "java.lang.String", typeOf<java.sql.Clob>()),
)

/** BLOB affinity — declared BLOB storing binary bytes. */
internal val sqliteBlobAffinityMappings: List<TypeMapping> = listOf(
    TypeMapping("BLOB", Types.BLOB, "java.lang.Object", typeOf<ByteArray>()),
)

/**
 * NUMERIC affinity — DECIMAL and NUMERIC follow the actual storage class (numeric ambiguity;
 * there is no single "canonical" numeric type). DATE / DATETIME / TIME / TIMESTAMP get an
 * idiomatic Kotlin date-time type in the DataFrame schema (`kotlinx.datetime.LocalDate` /
 * `kotlinx.datetime.LocalDateTime` / `kotlinx.datetime.LocalTime` / `kotlin.time.Instant`) and
 * are converted from their storage class during preprocessing — see the SQLite type mapping doc.
 *
 * Detection is by `sqlTypeName` (not `jdbcType`), because Xerial changes the reported jdbcType
 * based on the actual storage: e.g. a `DATE` column with a REAL value is reported as `FLOAT`.
 */
internal val sqliteNumericAffinityMappings: List<TypeMapping> = listOf(
    TypeMapping("NUMERIC", Types.NUMERIC, "java.lang.Double", typeOf<Double>()),
    TypeMapping("NUMERIC", Types.NUMERIC, "java.lang.Integer", typeOf<Int>()),
    TypeMapping("DECIMAL", Types.DECIMAL, "java.lang.Double", typeOf<Double>()),

    // DATE with all three storage classes:
    TypeMapping("DATE", Types.DATE, "java.lang.String", typeOf<KotlinLocalDate>()),    // TEXT (ISO)
    TypeMapping("DATE", Types.DATE, "java.lang.Integer", typeOf<KotlinLocalDate>()),   // INTEGER (Unix)
    TypeMapping("DATE", Types.FLOAT, "java.lang.Double", typeOf<KotlinLocalDate>()),   // REAL (Julian day)

    // DATETIME variants:
    TypeMapping("DATETIME", Types.DATE, "java.lang.String", typeOf<KotlinLocalDateTime>()),
    TypeMapping("DATETIME", Types.DATE, "java.lang.Integer", typeOf<KotlinLocalDateTime>()),

    // TIME variants:
    TypeMapping("TIME", Types.TIME, "java.lang.String", typeOf<KotlinLocalTime>()),
    TypeMapping("TIME", Types.INTEGER, "java.lang.Integer", typeOf<KotlinLocalTime>()),

    // TIMESTAMP with all three storage classes:
    TypeMapping("TIMESTAMP", Types.TIMESTAMP, "java.lang.String", typeOf<Instant>()),   // TEXT (ISO)
    TypeMapping("TIMESTAMP", Types.TIMESTAMP, "java.lang.Integer", typeOf<Instant>()),  // INTEGER (Unix)
    TypeMapping("TIMESTAMP", Types.FLOAT, "java.lang.Double", typeOf<Instant>()),       // REAL (Julian day)
)
