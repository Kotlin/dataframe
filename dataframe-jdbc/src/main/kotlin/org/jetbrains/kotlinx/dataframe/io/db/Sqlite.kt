package org.jetbrains.kotlinx.dataframe.io.db

import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinLocalTime
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.sqlite.SQLiteConfig
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Instant
import kotlin.time.toKotlinInstant
import java.util.Date as JavaDate
import kotlinx.datetime.LocalDate as KotlinLocalDate
import kotlinx.datetime.LocalDateTime as KotlinLocalDateTime
import kotlinx.datetime.LocalTime as KotlinLocalTime

/**
 * Represents the Sqlite database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for Sqlite,
 * and to generate the corresponding column schema.
 *
 * Use [customTypesMap] to register custom types and provide the corresponding [KType] for each one.
 * [KType] must correspond to JDBC actual type.
 * Even for default Sqlite types, you can override the actual [KType] after reading the column
 * by providing a custom type in [customTypesMap].
 */
public class Sqlite(public val customTypesMap: Map<String, KType> = mapOf()) : DbType("sqlite") {
    override val driverClassName: String
        get() = "org.sqlite.JDBC"

    // SQLite is dynamically typed with only five storage classes (NULL, INTEGER, REAL, TEXT, BLOB).
    // The declared column type is a hint (type affinity), so a column declared DATE/DATETIME/
    // TIMESTAMP/DECIMAL/NUMERIC can actually hold a String, Integer, or Double at runtime.
    //
    // - For DATE / DATETIME / TIME / TIMESTAMP we detect the declared type by name (Xerial changes
    //   the reported `jdbcType` based on the stored value's storage class — e.g. a DATE column
    //   with a REAL value is reported as `Types.FLOAT`) and return an idiomatic Kotlin date-time
    //   type (`kotlinx.datetime.LocalDate` / `LocalDateTime` / `LocalTime` / `kotlin.time.Instant`).
    //   The raw storage value is converted in `preprocessValue`.
    // - For DECIMAL and NUMERIC, we trust the driver-reported `javaClassName` (the actual stored
    //   value's class): a NUMERIC column can hold a genuinely mixed set of ints and doubles, and
    //   there's no natural "canonical" numeric type to promote them to.
    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType {
        customTypesMap[tableColumnMetadata.sqlTypeName]?.let {
            return it.withNullability(tableColumnMetadata.isNullable)
        }
        val nullable = tableColumnMetadata.isNullable
        val declaredUpper = tableColumnMetadata.sqlTypeName.uppercase()

        // Date/time detection by declared type name (SQLite type affinity via substring
        // matching). Order matters: DATETIME must be checked before DATE/TIME; TIMESTAMP before
        // TIME.
        when {
            "DATETIME" in declaredUpper ->
                return typeOf<KotlinLocalDateTime>().withNullability(nullable)

            "TIMESTAMP" in declaredUpper ->
                return typeOf<Instant>().withNullability(nullable)

            "DATE" in declaredUpper ->
                return typeOf<KotlinLocalDate>().withNullability(nullable)

            "TIME" in declaredUpper ->
                return typeOf<KotlinLocalTime>().withNullability(nullable)
        }

        // Numeric ambiguity: trust storage class.
        when (tableColumnMetadata.jdbcType) {
            Types.DECIMAL, Types.NUMERIC ->
                javaClassNameToKType(tableColumnMetadata.javaClassName)?.let {
                    return it.withNullability(nullable)
                }
        }

        return super.getExpectedJdbcType(tableColumnMetadata)
    }

    // Reads a raw value from the ResultSet.
    // - BOOLEAN/BIT: SQLite stores booleans as INTEGER; use rs.getBoolean so the value matches
    //   the Boolean schema type produced by getExpectedJdbcType.
    override fun <J> getValueFromResultSet(
        rs: ResultSet,
        columnIndex: Int,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
    ): J {
        val idx = columnIndex + 1
        return when (tableColumnMetadata.jdbcType) {
            Types.BOOLEAN, Types.BIT -> {
                val value = rs.getBoolean(idx)
                @Suppress("UNCHECKED_CAST")
                (if (rs.wasNull()) null else value) as J
            }

            else -> super.getValueFromResultSet(rs, columnIndex, tableColumnMetadata, expectedJdbcType)
        }
    }

    // For DECIMAL/NUMERIC we already resolved the DataFrame type from the storage class in
    // getExpectedJdbcType, so we keep that as-is. For other types we let the base decide
    // (base maps TIMESTAMP → Instant, BINARY(UUID) → Uuid, etc.).
    override fun getPreprocessedValueType(
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
    ): KType =
        when (tableColumnMetadata.jdbcType) {
            Types.DECIMAL, Types.NUMERIC -> expectedJdbcType
            else -> super.getPreprocessedValueType(tableColumnMetadata, expectedJdbcType)
        }

    // Converts the raw stored value into the type the DataFrame column expects. Dispatched by
    // the target Kotlin type, so a user's `customTypesMap` opt-out (e.g. forcing DATETIME →
    // String) automatically skips conversion. Unsupported combinations throw with a clear
    // message.
    override fun <J, D> preprocessValue(
        value: J,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
        expectedPreprocessedValueType: KType,
    ): D {
        val target = expectedPreprocessedValueType.classifier
        @Suppress("UNCHECKED_CAST")
        return when (target) {
            Instant::class -> convertToInstant(value, tableColumnMetadata) as D
            KotlinLocalDate::class -> convertToLocalDate(value, tableColumnMetadata) as D
            KotlinLocalDateTime::class -> convertToLocalDateTime(value, tableColumnMetadata) as D
            KotlinLocalTime::class -> convertToLocalTime(value, tableColumnMetadata) as D

            // DECIMAL / NUMERIC (or any other type resolved via storage class): return as-is.
            else -> {
                if (tableColumnMetadata.jdbcType == Types.DECIMAL ||
                    tableColumnMetadata.jdbcType == Types.NUMERIC
                ) {
                    return value as D
                }
                super.preprocessValue(
                    value = value,
                    tableColumnMetadata = tableColumnMetadata,
                    expectedJdbcType = expectedJdbcType,
                    expectedPreprocessedValueType = expectedPreprocessedValueType,
                )
            }
        }
    }

    private fun javaClassNameToKType(className: String): KType? =
        when (className) {
            "java.lang.String" -> typeOf<String>()
            "java.lang.Integer" -> typeOf<Int>()
            "java.lang.Long" -> typeOf<Long>()
            "java.lang.Double" -> typeOf<Double>()
            "java.lang.Float" -> typeOf<Float>()
            "java.lang.Boolean" -> typeOf<Boolean>()
            "[B" -> typeOf<ByteArray>()
            else -> null
        }

    // ---------- date/time storage → target conversions ----------

    private fun convertToInstant(value: Any?, meta: TableColumnMetadata): Instant? =
        when (value) {
            null -> null
            is Timestamp -> value.toInstant().toKotlinInstant()
            is LocalDateTime -> value.toInstant(ZoneOffset.UTC).toKotlinInstant()
            is JavaDate -> value.toInstant().toKotlinInstant()
            // SQLite convention: INTEGER = Unix seconds since 1970-01-01 UTC.
            is Long -> Instant.fromEpochSeconds(value)
            is Int -> Instant.fromEpochSeconds(value.toLong())
            // SQLite convention: REAL = Julian day (days since -4713-11-24 12:00 UTC).
            is Double -> julianDayToInstant(value)
            is String -> parseStringAsInstant(value, meta)
            else -> unsupportedConversion(value, "kotlin.time.Instant", meta)
        }

    private fun convertToLocalDate(value: Any?, meta: TableColumnMetadata): KotlinLocalDate? =
        when (value) {
            null -> null
            is LocalDate -> value.toKotlinLocalDate()
            is java.sql.Date -> value.toLocalDate().toKotlinLocalDate()
            is Timestamp -> value.toLocalDateTime().toLocalDate().toKotlinLocalDate()
            is JavaDate -> value.toInstant().atZone(ZoneOffset.UTC).toLocalDate().toKotlinLocalDate()
            is Long -> instantToLocalDate(Instant.fromEpochSeconds(value))
            is Int -> instantToLocalDate(Instant.fromEpochSeconds(value.toLong()))
            is Double -> instantToLocalDate(julianDayToInstant(value))
            is String -> parseStringAsLocalDate(value, meta)
            else -> unsupportedConversion(value, "kotlinx.datetime.LocalDate", meta)
        }

    private fun convertToLocalDateTime(value: Any?, meta: TableColumnMetadata): KotlinLocalDateTime? =
        when (value) {
            null -> null
            is LocalDateTime -> value.toKotlinLocalDateTime()
            is Timestamp -> value.toLocalDateTime().toKotlinLocalDateTime()
            is JavaDate -> LocalDateTime.ofInstant(value.toInstant(), ZoneOffset.UTC).toKotlinLocalDateTime()
            is Long -> instantToLocalDateTime(Instant.fromEpochSeconds(value))
            is Int -> instantToLocalDateTime(Instant.fromEpochSeconds(value.toLong()))
            is Double -> instantToLocalDateTime(julianDayToInstant(value))
            is String -> parseStringAsLocalDateTime(value, meta)
            else -> unsupportedConversion(value, "kotlinx.datetime.LocalDateTime", meta)
        }

    private fun convertToLocalTime(value: Any?, meta: TableColumnMetadata): KotlinLocalTime? =
        when (value) {
            null -> null
            is LocalTime -> value.toKotlinLocalTime()
            is java.sql.Time -> value.toLocalTime().toKotlinLocalTime()
            // Interpret as seconds since midnight.
            is Long -> LocalTime.ofSecondOfDay(value).toKotlinLocalTime()
            is Int -> LocalTime.ofSecondOfDay(value.toLong()).toKotlinLocalTime()
            is String -> parseStringAsLocalTime(value, meta)
            else -> unsupportedConversion(value, "kotlinx.datetime.LocalTime", meta)
        }

    private fun parseStringAsInstant(s: String, meta: TableColumnMetadata): Instant {
        // Try, in order: full ISO instant, LocalDateTime (T or space separator), LocalDate.
        runCatching { return Instant.parse(s) }
        val normalised = s.replace(' ', 'T')
        runCatching {
            return LocalDateTime.parse(normalised).toInstant(ZoneOffset.UTC).toKotlinInstant()
        }
        runCatching {
            return LocalDate.parse(s).atStartOfDay(ZoneOffset.UTC).toInstant().toKotlinInstant()
        }
        error(
            "SQLite: cannot parse '$s' from column '${meta.name}' (declared '${meta.sqlTypeName}') " +
                "as an ISO 8601 date/time. Use `Sqlite(customTypesMap = mapOf(\"${meta.sqlTypeName}\" to typeOf<String>()))` " +
                "to read it as raw text instead.",
        )
    }

    private fun parseStringAsLocalDate(s: String, meta: TableColumnMetadata): KotlinLocalDate {
        runCatching { return LocalDate.parse(s).toKotlinLocalDate() }
        // Also accept full date-time / instant strings — truncate to the date portion.
        runCatching { return instantToLocalDate(parseStringAsInstant(s, meta)) }
        error(
            "SQLite: cannot parse '$s' from column '${meta.name}' (declared '${meta.sqlTypeName}') " +
                "as an ISO 8601 date. Use customTypesMap to read it as raw text instead.",
        )
    }

    private fun parseStringAsLocalDateTime(s: String, meta: TableColumnMetadata): KotlinLocalDateTime {
        val normalised = s.replace(' ', 'T')
        runCatching { return LocalDateTime.parse(normalised).toKotlinLocalDateTime() }
        runCatching { return LocalDate.parse(s).atStartOfDay().toKotlinLocalDateTime() }
        // As a last resort, accept ISO instant strings and convert to LocalDateTime at UTC.
        runCatching { return instantToLocalDateTime(Instant.parse(s)) }
        error(
            "SQLite: cannot parse '$s' from column '${meta.name}' (declared '${meta.sqlTypeName}') " +
                "as an ISO 8601 date-time. Use customTypesMap to read it as raw text instead.",
        )
    }

    private fun parseStringAsLocalTime(s: String, meta: TableColumnMetadata): KotlinLocalTime {
        runCatching { return LocalTime.parse(s).toKotlinLocalTime() }
        error(
            "SQLite: cannot parse '$s' from column '${meta.name}' (declared '${meta.sqlTypeName}') " +
                "as an ISO 8601 time. Use customTypesMap to read it as raw text instead.",
        )
    }

    private fun instantToLocalDate(instant: Instant): KotlinLocalDate =
        java.time.Instant.ofEpochSecond(instant.epochSeconds)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .toKotlinLocalDate()

    private fun instantToLocalDateTime(instant: Instant): KotlinLocalDateTime =
        java.time.Instant.ofEpochSecond(instant.epochSeconds)
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime()
            .toKotlinLocalDateTime()

    private fun julianDayToInstant(julianDay: Double): Instant {
        val epochSeconds = ((julianDay - JULIAN_DAY_UNIX_EPOCH) * SECONDS_PER_DAY).toLong()
        return Instant.fromEpochSeconds(epochSeconds)
    }

    private fun unsupportedConversion(value: Any?, target: String, meta: TableColumnMetadata): Nothing =
        error(
            "SQLite: cannot convert value of type ${value?.javaClass?.name} " +
                "from column '${meta.name}' (declared '${meta.sqlTypeName}') to $target.",
        )

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean = tableMetadata.name.startsWith("sqlite_")

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("TABLE_NAME"),
            tables.getString("TABLE_SCHEM"),
            tables.getString("TABLE_CAT"),
        )

    override fun createConnection(dbConfig: DbConnectionConfig): Connection =
        if (dbConfig.readOnly) {
            val config = SQLiteConfig()
            config.setReadOnly(true)
            config.createConnection(dbConfig.url)
        } else {
            DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password)
        }

    public companion object {
        public val default: Sqlite = Sqlite()

        public fun withCustomTypes(customTypesMap: Map<String, KType>): Sqlite = Sqlite(customTypesMap)

        // Julian day number at Unix epoch (1970-01-01 00:00 UTC).
        private const val JULIAN_DAY_UNIX_EPOCH = 2440587.5
        private const val SECONDS_PER_DAY = 86_400
    }
}
