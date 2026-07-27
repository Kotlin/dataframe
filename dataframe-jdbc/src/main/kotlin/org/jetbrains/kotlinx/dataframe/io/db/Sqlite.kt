package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.DataFrame
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinLocalTime
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.sqlite.SQLiteConfig
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Types
import java.time.ZoneOffset
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Instant
import kotlin.time.toKotlinInstant
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalTime as JavaLocalTime

/**
 * A user-provided converter from an SQLite declared column type name to
 * the [DataFrame] column type and a lambda thatн converts each stored value.
 *
 * The type parameter [T] is the **storage-class type** of the column — the actual class of
 * values returned by [java.sql.ResultSet.getObject][ResultSet.getObject] for that column.
 * The Xerial SQLite JDBC driver's `getObject(int)` returns exactly one of the following, chosen
 * from the runtime storage class of the value (not the declared column type):
 *
 *  - [java.lang.Integer] ([Int]) — `INTEGER` values that fit in `Int` (`-2^31 .. 2^31 - 1`)
 *  - [java.lang.Long] ([Long]) — `INTEGER` values outside `Int` range
 *  - [java.lang.Double] ([Double]) — `REAL` values
 *  - [java.lang.String] ([String]) — `TEXT` values
 *  - `byte[]` ([ByteArray]) — `BLOB` values
 *  - `null` — NULL
 *
 * The driver never produces any other type — in particular, declared `DATE` / `TIME` / `TIMESTAMP` /
 * `BOOLEAN` columns still surface as one of the six types above, not as their Java-time / Boolean
 * equivalent
 *
 * The mapping consists of a pair of:
 * 1. The [KType] of the resulting [DataFrame] column;
 * 2. The lambda that returns a converted value (which must fit the [KType] from the first component).
 *
 * ### Example
 * ```
 * val format = LocalDateTime.Format {
 *     year(); char('-'); monthNumber(); char('-'); day()
 *     char(' ')
 *     hour(); char(':'); minute(); char(':'); second()
 *     chars(" UTC")
 * }
 *
 * val sqliteCustom = Sqlite.withCustomConverters {
 *     // Every column declared with "MY_DATETIME" type is parsed from custom text into Instant.
 *     forType("MY_DATETIME") { raw: String? ->
 *         raw?.let { LocalDateTime.parse(it, format).toInstant(TimeZone.UTC) }
 *     }
 *     // Identity shortcut — pin the declared type LONGVARCHAR to `String?` regardless of
 *     // how SQLite's type affinity would classify it.
 *     forType<String?>("LONGVARCHAR")
 *     // The "ratio" column overrides its declared type — read as Double.
 *     forColumn("ratio") { raw: String -> raw.toDouble() }
 * }
 *
 * val df = DataFrame.readSqlTable(connection, "events", dbType = sqliteCustom)
 * ```
 */
public typealias SqliteCustomTypeConverter<T> = Pair<KType, ((rawValue: T) -> Any?)>

/**
 * DSL builder collected by [Sqlite.withCustomConverters]. Register converters via
 * [forType] (matches by declared SQL type name) or [forColumn] (matches by column name;
 * takes precedence over [forType]).
 *
 * Two overloads are available for each column or type converter:
 *  - **Converter form** — `forType<T, R>(name) { raw -> ... }` / `forColumn<T, R>(name) { raw -> ... }`.
 *    Accepts a lambda that converts each raw stored value; the [column's type][org.jetbrains.kotlinx.dataframe.DataColumn.type] is derived from
 *    the reified `R` via `typeOf<R>()`.
 *  - **Identity form** — `forType<T>(name)` / `forColumn<T>(name)`. Same shape but with no
 *    conversion — values pass through as `T`. Handy when SQLite's
 *    [type affinity](https://www.sqlite.org/datatype3.html#type_affinity) misclassifies a
 *    declared type and the built-in mapping picks the wrong Kotlin type.
 *
 * ### Example
 * ```
 * val format = LocalDateTime.Format {
 *     year(); char('-'); monthNumber(); char('-'); day()
 *     char(' ')
 *     hour(); char(':'); minute(); char(':'); second()
 *     chars(" UTC")
 * }
 *
 * val sqliteCustom = Sqlite.withCustomConverters {
 *     // Every column declared with "MY_DATETIME" type is parsed from custom text into Instant.
 *     forType("MY_DATETIME") { raw: String? ->
 *         raw?.let { LocalDateTime.parse(it, format).toInstant(TimeZone.UTC) }
 *     }
 *     // Identity shortcut — pin the declared type LONGVARCHAR to `String?` regardless of
 *     // how SQLite's type affinity would classify it.
 *     forType<String?>("LONGVARCHAR")
 *     // The "ratio" column overrides its declared type — read as Double.
 *     forColumn("ratio") { raw: String -> raw.toDouble() }
 * }
 *
 * val df = DataFrame.readSqlTable(connection, "events", dbType = sqliteCustom)
 * ```
 */
public class SqliteCustomConvertersBuilder @PublishedApi internal constructor() {
    @PublishedApi
    internal val typeMappings: MutableMap<String, SqliteCustomTypeConverter<*>> = mutableMapOf()

    @PublishedApi
    internal val columnMappings: MutableMap<String, SqliteCustomTypeConverter<*>> = mutableMapOf()

    /**
     * Register a converter for every column with the given declared SQL type name.
     *
     * @param T the storage class of the raw stored value ([String], [Int], [Long], [Double],
     *    or [ByteArray]  or their nullable variants).
     * @param R the target Kotlin type for the resulting DataFrame column. Resolved via
     *   `typeOf<R>()` — must be a non-abstract type reachable by reflection.
     *
     * @param [sqlTypeName] name of the declared SQL type (as written in `CREATE TABLE`).
     * @param [convert] lambda to convert the raw stored value to the target Kotlin type.
     */
    public inline fun <T, reified R> forType(
        sqlTypeName: String,
        crossinline convert: (T) -> R,
    ) {
        val mapping: SqliteCustomTypeConverter<T> = typeOf<R>() to { raw -> convert(raw) }
        typeMappings[sqlTypeName] = mapping
    }

    /**
     * Register an **identity converter** for every column with the given declared SQL type name:
     * values pass through unchanged, but the [DataFrame] [column type][org.jetbrains.kotlinx.dataframe.DataColumn.type]
     * is fixed to `T` (resolved via `typeOf<T>()`).
     *
     * Useful when SQLite's [type affinity](https://www.sqlite.org/datatype3.html#type_affinity)
     * misclassifies your column and the built-in mapping resolves the wrong Kotlin type. Example:
     * a column declared `MY_ID` has NUMERIC affinity (no `INT`/`CHAR`/`BLOB`/`REAL`/`FLOA`/`DOUB`
     * substring), so SQLite will happily convert `'42'` to an integer on insert; declaring
     * `forType<String>("MY_ID")` pins the column to `String` regardless.
     *
     * @param T the target Kotlin type for the resulting DataFrame column — must be one of the
     *   storage-class types the driver actually returns ([String], [Int], [Long], [Double],
     *   or [ByteArray] or their nullable variants).
     *
     * ### Example
     * ```
     * Sqlite.withCustomConverters {
     *     forType<String?>("LONGVARCHAR")   // read as `String?` even though affinity says NUMERIC
     *     forType<ByteArray?>("BINARY_ID")  // read as raw bytes
     * }
     * ```
     */
    public inline fun <reified T> forType(sqlTypeName: String) {
        val mapping: SqliteCustomTypeConverter<T> = typeOf<T>() to { it }
        typeMappings[sqlTypeName] = mapping
    }

    /**
     * Register a converter for a specific column by name. Column-name overrides take
     * precedence over type-name overrides registered via [forType].
     *
     * @param T the storage class of the raw stored value.
     * @param R the target Kotlin type for the resulting DataFrame column.
     */
    public inline fun <T, reified R> forColumn(
        columnName: String,
        crossinline convert: (T) -> R,
    ) {
        val mapping: SqliteCustomTypeConverter<T> = typeOf<R>() to { raw -> convert(raw) }
        columnMappings[columnName] = mapping
    }

    /**
     * Register an **identity converter** for a specific column by name: values pass through
     * unchanged, but the DataFrame column type is fixed to `T` (resolved via `typeOf<T>()`).
     * Column-name overrides take precedence over type-name overrides.
     *
     * Useful for a single column whose declared SQL type is misleading — either because SQLite's
     * type affinity picks the wrong bucket or because you want a stricter type than the shared
     * declared type would give.
     *
     * @param T the target Kotlin type for the resulting DataFrame column — must be one of the
     *   storage-class types the driver actually returns ([String], [Int], [Long], [Double],
     *   or [ByteArray] or their nullable variants).
     *
     * ### Example
     * ```
     * Sqlite.withCustomConverters {
     *     forColumn<String>("uuid")      // treat the `uuid` column as raw text
     *     forColumn<ByteArray?>("payload")
     * }
     * ```
     */
    public inline fun <reified T> forColumn(columnName: String) {
        val mapping: SqliteCustomTypeConverter<T> = typeOf<T>() to { it }
        columnMappings[columnName] = mapping
    }
}

/**
 * Represents the Sqlite database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for Sqlite,
 * and to generate the corresponding column schema.
 *
 * Two levels of custom overrides are supported, with the following resolution order (first match
 * wins):
 *  1. [customColumnsMap] — keyed by the **column name**. Use this to override a specific column,
 *     regardless of its declared type.
 *  2. [customTypesMap] — keyed by the declared **SQL type name** (as written in `CREATE TABLE`).
 *     Use this to override every column that shares a declared type.
 *  3. The built-in SQLite conversion for BOOLEAN, DATE, DATETIME, TIME, TIMESTAMP, DECIMAL,
 *     NUMERIC.
 *  4. The base `DbType` mapping.
 *
 * Both maps take a [SqliteCustomTypeConverter] lambda that returns both the target [KType] and the
 * converted value.
 */
public class Sqlite(
    public val customTypesMap: Map<String, SqliteCustomTypeConverter<*>> = emptyMap(),
    public val customColumnsMap: Map<String, SqliteCustomTypeConverter<*>> = emptyMap(),
) : DbType("sqlite") {
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
        val nullable = tableColumnMetadata.isNullable
        // Column-name override wins over type-name override. Column nullability from the
        // schema is always applied on top of the KType the user declared.
        customMappingFor(tableColumnMetadata)?.let { (kType, _) ->
            return kType
        }
        val declaredUpper = tableColumnMetadata.sqlTypeName.uppercase()

        // Date/time detection by declared type name substring matching
        when {
            "DATETIME" in declaredUpper ->
                return typeOf<LocalDateTime>().withNullability(nullable)

            "TIMESTAMP" in declaredUpper ->
                return typeOf<Instant>().withNullability(nullable)

            "DATE" in declaredUpper ->
                return typeOf<LocalDate>().withNullability(nullable)

            "TIME" in declaredUpper ->
                return typeOf<LocalTime>().withNullability(nullable)
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
    // the target Kotlin type — this uniformly handles the SQLite "declared type ≠ storage class"
    // mismatch for BOOLEAN, DATE, DATETIME, TIME, and TIMESTAMP. Custom mappings from
    // `customTypesMap` take precedence and completely replace the built-in conversion.
    override fun <J, D> preprocessValue(
        value: J,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
        expectedPreprocessedValueType: KType,
    ): D {
        customMappingFor(tableColumnMetadata)?.let { (_, convert) ->
            @Suppress("UNCHECKED_CAST")
            return convert(value) as D
        }
        val target = expectedPreprocessedValueType.classifier
        @Suppress("UNCHECKED_CAST")
        return when (target) {
            Boolean::class -> convertToBoolean(value, tableColumnMetadata) as D
            Instant::class -> convertToInstant(value, tableColumnMetadata) as D
            LocalDate::class -> convertToLocalDate(value, tableColumnMetadata) as D
            LocalDateTime::class -> convertToLocalDateTime(value, tableColumnMetadata) as D
            LocalTime::class -> convertToLocalTime(value, tableColumnMetadata) as D

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

    /**
     * Resolves the effective custom mapping for a column: by-name takes precedence over by-type.
     * The returned mapping is cast so the converter accepts `Any?` — at runtime the user's
     * declared `T` is erased and the raw value is passed straight through.
     */
    @Suppress("UNCHECKED_CAST")
    private fun customMappingFor(tableColumnMetadata: TableColumnMetadata): Pair<KType, (Any?) -> Any?>? =
        (
            customColumnsMap[tableColumnMetadata.name]
                ?: customTypesMap[tableColumnMetadata.sqlTypeName]
        ) as Pair<KType, (Any?) -> Any?>?

    private fun javaClassNameToKType(className: String): KType? =
        when (className) {
            "java.lang.String" -> typeOf<String>()
            "java.lang.Integer" -> typeOf<Int>()
            "java.lang.Long" -> typeOf<Long>()
            "java.lang.Double" -> typeOf<Double>()
            "[B" -> typeOf<ByteArray>()
            else -> null
        }

    // ---------- storage class → target conversions ----------
    //
    // The Xerial SQLite JDBC driver's `ResultSet.getObject(int)` inspects the runtime storage
    // class (never the declared column type) and returns exactly one of:
    //
    //   INTEGER  → java.lang.Integer  (values in Int range)
    //   INTEGER  → java.lang.Long     (values outside Int range)
    //   REAL     → java.lang.Double
    //   TEXT     → java.lang.String
    //   BLOB     → byte[]  (i.e. `ByteArray`)
    //   NULL     → null
    //
    // No other type ever arrives here.
    // Consequently, each `convertToX` below only branches on `null`, `Int`, `Long`, `Double`,
    // `String`, `ByteArray` where they make sense.

    private fun convertToBoolean(value: Any?, meta: TableColumnMetadata): Boolean? =
        when (value) {
            null -> null
            // SQLite convention: booleans are stored as INTEGER (0/1). Any non-zero → true.
            is Int -> value != 0
            is Long -> value != 0L
            // Some users may store booleans as REAL. Any non-zero → true.
            is Double -> value != 0.0
            is String -> when (value.trim().lowercase()) {
                "true", "1", "yes", "y", "t" -> true
                "false", "0", "no", "n", "f" -> false
                else -> parseFailure(value, "Boolean", meta)
            }

            else -> unsupportedConversion(value, "Boolean", meta)
        }

    private fun convertToInstant(value: Any?, meta: TableColumnMetadata): Instant? =
        when (value) {
            null -> null
            // SQLite convention: INTEGER = Unix seconds since 1970-01-01 UTC.
            is Int -> Instant.fromEpochSeconds(value.toLong())
            is Long -> Instant.fromEpochSeconds(value)
            // SQLite convention: REAL = Julian day (days since -4713-11-24 12:00 UTC).
            is Double -> julianDayToInstant(value)
            is String -> parseStringAsInstant(value, meta)
            else -> unsupportedConversion(value, "kotlin.time.Instant", meta)
        }

    private fun convertToLocalDate(value: Any?, meta: TableColumnMetadata): LocalDate? =
        when (value) {
            null -> null
            is Int -> instantToLocalDate(Instant.fromEpochSeconds(value.toLong()))
            is Long -> instantToLocalDate(Instant.fromEpochSeconds(value))
            // SQLite convention: REAL = Julian day (days since -4713-11-24 12:00 UTC).
            is Double -> instantToLocalDate(julianDayToInstant(value))
            is String -> parseStringAsLocalDate(value, meta)
            else -> unsupportedConversion(value, "kotlinx.datetime.LocalDate", meta)
        }

    private fun convertToLocalDateTime(value: Any?, meta: TableColumnMetadata): LocalDateTime? =
        when (value) {
            null -> null
            is Int -> instantToLocalDateTime(Instant.fromEpochSeconds(value.toLong()))
            is Long -> instantToLocalDateTime(Instant.fromEpochSeconds(value))
            is Double -> instantToLocalDateTime(julianDayToInstant(value))
            is String -> parseStringAsLocalDateTime(value, meta)
            else -> unsupportedConversion(value, "kotlinx.datetime.LocalDateTime", meta)
        }

    private fun convertToLocalTime(value: Any?, meta: TableColumnMetadata): LocalTime? =
        when (value) {
            null -> null
            // Interpret INTEGER as seconds since midnight.
            is Int -> JavaLocalTime.ofSecondOfDay(value.toLong()).toKotlinLocalTime()
            is Long -> JavaLocalTime.ofSecondOfDay(value).toKotlinLocalTime()
            is String -> parseStringAsLocalTime(value, meta)
            else -> unsupportedConversion(value, "kotlinx.datetime.LocalTime", meta)
        }

    private fun parseStringAsInstant(s: String, meta: TableColumnMetadata): Instant {
        // Try, in order: full ISO instant, LocalDateTime (T or space separator), LocalDate.
        runCatching { return Instant.parse(s) }
        val normalised = s.replace(' ', 'T')
        runCatching {
            return JavaLocalDateTime.parse(normalised).toInstant(ZoneOffset.UTC).toKotlinInstant()
        }
        runCatching {
            return JavaLocalDate.parse(s).atStartOfDay(ZoneOffset.UTC).toInstant().toKotlinInstant()
        }
        parseFailure(s, "an ISO 8601 date/time", meta)
    }

    private fun parseStringAsLocalDate(s: String, meta: TableColumnMetadata): LocalDate {
        runCatching { return JavaLocalDate.parse(s).toKotlinLocalDate() }
        // Also accept full date-time / instant strings — truncate to the date portion.
        runCatching { return instantToLocalDate(parseStringAsInstant(s, meta)) }
        parseFailure(s, "an ISO 8601 date", meta)
    }

    private fun parseStringAsLocalDateTime(s: String, meta: TableColumnMetadata): LocalDateTime {
        val normalised = s.replace(' ', 'T')
        runCatching { return JavaLocalDateTime.parse(normalised).toKotlinLocalDateTime() }
        runCatching { return JavaLocalDate.parse(s).atStartOfDay().toKotlinLocalDateTime() }
        // As a last resort, accept ISO instant strings and convert to LocalDateTime at UTC.
        runCatching { return instantToLocalDateTime(Instant.parse(s)) }
        parseFailure(s, "an ISO 8601 date-time", meta)
    }

    private fun parseStringAsLocalTime(s: String, meta: TableColumnMetadata): LocalTime {
        runCatching { return JavaLocalTime.parse(s).toKotlinLocalTime() }
        parseFailure(s, "an ISO 8601 time", meta)
    }

    private fun instantToLocalDate(instant: Instant): LocalDate =
        java.time.Instant.ofEpochSecond(instant.epochSeconds)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .toKotlinLocalDate()

    private fun instantToLocalDateTime(instant: Instant): LocalDateTime =
        java.time.Instant.ofEpochSecond(instant.epochSeconds)
            .atZone(ZoneOffset.UTC)
            .toLocalDateTime()
            .toKotlinLocalDateTime()

    private fun julianDayToInstant(julianDay: Double): Instant {
        val epochSeconds = ((julianDay - JULIAN_DAY_UNIX_EPOCH) * SECONDS_PER_DAY).toLong()
        return Instant.fromEpochSeconds(epochSeconds)
    }

    private fun unsupportedConversion(value: Any?, target: String, meta: TableColumnMetadata): Nothing =
        conversionError(
            "cannot convert value of type ${value?.javaClass?.name} to $target",
            meta,
        )

    private fun parseFailure(value: Any?, target: String, meta: TableColumnMetadata): Nothing =
        conversionError("cannot parse '$value' as $target", meta)

    private fun conversionError(problem: String, meta: TableColumnMetadata): Nothing =
        error(
            "SQLite: $problem from column '${meta.name}' (declared '${meta.sqlTypeName}'). " +
                "Register a custom converter for this type or column via " +
                "`Sqlite.withCustomConverters { } to override the built-in mapping.",
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
        /**
         * Default [Sqlite] instance with no custom overrides.
         *
         * Uses built-in SQLite conversions for most common types (`BOOLEAN`, `DATE`, `DATETIME`, `TIME`, `TIMESTAMP`,
         * `DECIMAL`, `NUMERIC`).
         *
         * See [Sqlite.withCustomConverters] to register custom type converters.
         */
        public val default: Sqlite = Sqlite()

        /**
         * Builds a [Sqlite] with custom type converters registered via a [SqliteCustomConvertersBuilder] DSL block.
         *
         * * use [forType][SqliteCustomConvertersBuilder.forType] to register a converter
         * keyed by the declared SQL type name (as written in `CREATE TABLE`);
         * * use [forColumn][SqliteCustomConvertersBuilder.forColumn] to register a converter keyed by column name
         * (takes precedence over [forType][SqliteCustomConvertersBuilder.forType] for the named column).
         *
         * Both DSL functions accept two generic type parameters:
         *  - `T` — the storage class of the raw stored value [String], [Int], [Long], [Double],
         *         or [ByteArray] or their nullable variants).
         *  - `R` — the target Kotlin type of the resulting DataFrame column; must be reified.
         *
         * The converting lambda receives the raw value and returns the converted result;
         *
         * ### Example
         * ```
         * val format = LocalDateTime.Format {
         *     year(); char('-'); monthNumber(); char('-'); day()
         *     char(' ')
         *     hour(); char(':'); minute(); char(':'); second()
         *     chars(" UTC")
         * }
         *
         * val sqliteCustom = Sqlite.withCustomConverters {
         *     // Every column declared with "MY_DATETIME" type is parsed from custom text into Instant.
         *     forType("MY_DATETIME") { raw: String? ->
         *         raw?.let { LocalDateTime.parse(it, format).toInstant(TimeZone.UTC) }
         *     }
         *     // The "ratio" column overrides its declared type — read as Double.
         *     forColumn("ratio") { raw: String -> raw.toDouble() }
         * }
         *
         * val df = DataFrame.readSqlTable(connection, "events", dbType = sqliteCustom)
         * ```
         */
        public fun withCustomConverters(block: SqliteCustomConvertersBuilder.() -> Unit): Sqlite {
            val builder = SqliteCustomConvertersBuilder().also { it.block() }
            return Sqlite(builder.typeMappings, builder.columnMappings)
        }

        // Julian day number at Unix epoch (1970-01-01 00:00 UTC).
        private const val JULIAN_DAY_UNIX_EPOCH = 2440587.5
        private const val SECONDS_PER_DAY = 86_400
    }
}
