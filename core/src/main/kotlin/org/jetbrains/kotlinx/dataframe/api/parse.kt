package org.jetbrains.kotlinx.dataframe.api

import kotlinx.datetime.format.DateTimeFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.StringParser
import org.jetbrains.kotlinx.dataframe.impl.api.parseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.tryParseImpl
import org.jetbrains.kotlinx.dataframe.impl.io.FastDoubleParser
import org.jetbrains.kotlinx.dataframe.util.ADD_DATE_TIME_PATTERN
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.PARSER_OPTIONS
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.time.format.DateTimeFormatter as JavaDateTimeFormatter

/**
 * ### Global Parser Options
 *
 * These options are used to configure how [DataColumns][DataColumn] of type [String] or [String?][String]
 * should be parsed.
 * You can always pass a [ParserOptions] object to functions that perform parsing, like [tryParse], [parse],
 * or even [DataFrame.readCsv][DataFrame.Companion.readCsv] to override these options.
 */
public val DataFrame.Companion.parser: GlobalParserOptions
    get() = Parsers

@Refine
@Interpretable("ParseDefault")
public fun <T> DataFrame<T>.parse(options: ParserOptions? = null): DataFrame<T> =
    parse(options) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }

@Refine
@Interpretable("Parse")
public fun <T> DataFrame<T>.parse(options: ParserOptions? = null, columns: ColumnsSelector<T, Any?>): DataFrame<T> =
    parseImpl(options, columns)

@Refine
@Interpretable("ParseString")
public fun <T> DataFrame<T>.parse(vararg columns: String, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.parse(vararg columns: ColumnReference<C>, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.parse(vararg columns: KProperty<C>, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumnSet() }

/**
 * Global counterpart of [ParserOptions].
 * Settings changed here will affect the defaults for all parsing operations.
 *
 * The default values are set by [Parsers.resetToDefault].
 */
public interface GlobalParserOptions {

    /**
     * Adds a Java-based date-time pattern to the global parser options of DataFrame.
     *
     * __NOTE__: Adding any Java-based date-time argument to DataFrame's parser options will
     * disable the default kotlinx-datetime-based date-time parsing.
     *
     * DataFrame will attempt to parse [Strings][String] using your provided patterns first
     * before falling back to the default (ISO) formats.
     *
     * For example, to always allow DataFrame to parse "12/24 2023" [Strings][String]:
     * ```kt
     * DataFrame.parser.addJavaDateTimePattern("MM/dd yyyy")
     * ```
     */
    public fun addJavaDateTimePattern(pattern: String)

    /**
     * __Deprecated:__
     *
     * We recommend using [addDateTimeFormat] instead, built on kotlinx-datetime:
     *
     * For example:
     * ```kt
     * DataFrame.parser.addDateTimeFormat(
     *     LocalDateTime.Format { byUnicodePattern(dateTimePattern) }
     * )
     * ```
     *
     * If you want to keep using the Java-based date-time parsing, you can use [addJavaDateTimePattern].
     */
    @Deprecated(
        message = ADD_DATE_TIME_PATTERN,
        replaceWith = ReplaceWith("addJavaDateTimePattern(pattern)"),
        level = DeprecationLevel.ERROR,
    )
    public fun addDateTimePattern(pattern: String): Unit = addJavaDateTimePattern(pattern)

    /**
     * Adds [format] to the global parser options of DataFrame.
     *
     * DataFrame will attempt to parse [Strings][String] using your provided formats first
     * before falling back to the default (ISO) formats.
     *
     * For example, to always allow DataFrame to parse "12/24 2023" [Strings][String]:
     * ```kt
     * DataFrame.parser.addDateTimeFormat(
     *     LocalDate.Format {
     *         monthNumber(padding = Padding.SPACE); char('/'); day(); char(' '); year()
     *     }
     * )
     * ```
     */
    public fun addDateTimeFormat(format: DateTimeFormat<*>)

    public fun addNullString(str: String)

    /** This function can be called to skip some types. Parsing will be attempted for all other types. */
    public fun addSkipType(type: KType)

    /** Whether to use [FastDoubleParser], defaults to `true`. Please report any issues you encounter. */
    public var useFastDoubleParser: Boolean

    public fun resetToDefault()

    public var locale: Locale

    public val nulls: Set<String>

    public val skipTypes: Set<KType>

    /**
     * Whether to allow parsing UUIDs to the experimental [Uuid] type.
     * By default, this is false and UUIDs are not recognized.
     *
     * NOTE: Interacting with a [Uuid][Uuid] in your code might require
     * `@`[OptIn][OptIn]`(`[ExperimentalUuidApi][ExperimentalUuidApi]`::class)`.
     * In notebooks, add `-opt-in=kotlin.uuid.ExperimentalUuidApi` to the compiler arguments.
     */
    public var parseExperimentalUuid: Boolean

    /**
     * Whether to allow parsing to the [kotlin.time.Instant] type.
     * This is marked "stable" from Kotlin 2.3.0+, so, by default this is `true`.
     *
     * If false, instants are recognized as the deprecated [kotlinx.datetime.Instant] type (#1350).
     *
     * NOTE: If you are using an older Kotlin version,
     * interacting with an [Instant][kotlin.time.Instant] in your code might require
     * `@`[OptIn][OptIn]`(`[ExperimentalTime][kotlin.time.ExperimentalTime]`::class)`.
     * In notebooks, add `-opt-in=kotlin.time.ExperimentalTime` to the compiler arguments.
     */
    public var parseExperimentalInstant: Boolean
}

/**
 * ### Options for parsing [String]`?` columns
 *
 * These options are used to configure how [DataColumn]s of type [String] or [String?][String] should be parsed.
 * They can be passed to [tryParse] and [parse] functions.
 *
 * You can also use the [DataFrame.parser][DataFrame.Companion.parser] property to access and modify
 * the global parser configuration.
 *
 * If any of the arguments in [ParserOptions] are `null` (or [ParserOptions] itself is `null`),
 * the global configuration will be queried.
 *
 * #### Parsing date-time strings
 *
 * For parsing date-time strings, the kotlinx-datetime library is used by default.
 * You can define your own [dateTimeFormats] to customize date-time parsing.
 *
 * For example, to always allow DataFrame to parse "12/24 2023" [Strings][String]:
 * ```kt
 * dateTimeFormats = setOf(
 *     LocalDate.Format {
 *         monthNumber(padding = Padding.SPACE); char('/'); day(); char(' '); year()
 *     }
 * )
 * ```
 *
 * If you want to use `java.time.*` based date-time parsing instead, you can
 * - skip all kotlin date types by providing:
 *   ```kt
 *   skipTypes = setOf(
 *       typeOf<kotlinx.datetime.LocalDate>(),
 *       typeOf<kotlinx.datetime.LocalDateTime>(),
 *       typeOf<kotlinx.datetime.LocalTime>(),
 *   )
 *   ```
 * - or provide either [javaDateTimeFormatter] or [javaDateTimePattern].
 *
 * @param locale locale to use for parsing dates and numbers, defaults to the System default locale.
 *   If specified instead of [javaDateTimeFormatter], it will be used in combination with [javaDateTimePattern]
 *   to create a [JavaDateTimeFormatter]. Just providing [locale] will not allow you to parse
 *   locale-specific dates!
 * @param javaDateTimeFormatter a [JavaDateTimeFormatter] to use for parsing dates, if not specified, it will be created
 *   from [javaDateTimePattern] and [locale]. If neither [javaDateTimeFormatter] nor [javaDateTimePattern] are specified,
 *   [JavaDateTimeFormatter.ISO_LOCAL_DATE_TIME] will be used.
 *   Specifying [javaDateTimeFormatter] disable kotlinx-datetime based parsing.
 * @param javaDateTimePattern a pattern to use for parsing dates. If specified instead of [javaDateTimeFormatter],
 *   it will be used to create a [JavaDateTimeFormatter].
 *   Specifying [javaDateTimePattern] disable kotlinx-datetime based parsing.
 * @param dateTimeFormats a set of custom kotlinx-datetime formats to use for parsing dates and other timestamps.
 *   If specified, these formats will be attempted before the default ISO formats.
 * @param nullStrings a set of strings that should be treated as `null` values. By default, it's
 *   `["null", "NULL", "NA", "N/A"]`.
 * @param skipTypes a set of types that should be skipped during parsing. Parsing will be attempted for all other types.
 *   By default, it's an empty set. To skip all types except a specified one, use [convertTo] instead.
 * @param useFastDoubleParser whether to use [FastDoubleParser], defaults to `true`. Please report any issues you encounter.
 * @param parseExperimentalUuid whether to allow parsing UUIDs to the experimental [Uuid] type.
 *   By default, this is false and UUIDs are not recognized.
 *   NOTE: Interacting with a [Uuid][Uuid] in your code might require
 *   `@`[OptIn][OptIn]`(`[ExperimentalUuidApi][ExperimentalUuidApi]`::class)`.
 *   In notebooks, add `-opt-in=kotlin.uuid.ExperimentalUuidApi` to the compiler arguments.
 * @param parseExperimentalInstant whether to allow parsing to the [kotlin.time.Instant] type.
 *    This is marked "stable" from Kotlin 2.3.0+, so, by default this is `true`.
 *    If false, instants are recognized as the deprecated [kotlinx.datetime.Instant] type (#1350).
 *   NOTE: If you are using an older Kotlin version,
 *   interacting with an [Instant][kotlin.time.Instant] in your code might require
 *   `@`[OptIn][OptIn]`(`[ExperimentalTime][kotlin.time.ExperimentalTime]`::class)`.
 *   In notebooks, add `-opt-in=kotlin.time.ExperimentalTime` to the compiler arguments.
 */
public class ParserOptions private constructor(
    public val locale: Locale? = null,
    public val javaDateTimeFormatter: JavaDateTimeFormatter? = null,
    public val javaDateTimePattern: String? = null,
    public val dateTimeFormats: Set<DateTimeFormat<*>>? = null,
    public val nullStrings: Set<String>? = null,
    public val skipTypes: Set<KType>? = null,
    public val useFastDoubleParser: Boolean? = null,
    public val parseExperimentalUuid: Boolean? = null,
    public val parseExperimentalInstant: Boolean? = null,
) {

    init {
        require(listOf(javaDateTimeFormatter, javaDateTimePattern, dateTimeFormats).count { it != null } <= 1) {
            "ParserOptions can only use one of the following arguments at a time: javaDateTimeFormatter, javaDateTimePattern, dateTimeFormats"
        }
    }

    /**
     * We parse to Kotlin datetime types by default.
     * However, if we have a [javaDateTimeFormatter] or [javaDateTimePattern], we parse to Java datetime types.
     */
    internal val javaDateTimeArgumentsProvided = javaDateTimeFormatter != null || javaDateTimePattern != null

    // Kotlin datetime constructor (default)
    public constructor(
        locale: Locale? = null,
        dateTimeFormats: Set<DateTimeFormat<*>>? = null,
        nullStrings: Set<String>? = null,
        skipTypes: Set<KType>? = null,
        useFastDoubleParser: Boolean? = null,
        parseExperimentalUuid: Boolean? = null,
        parseExperimentalInstant: Boolean? = null,
    ) : this(
        locale = locale,
        javaDateTimeFormatter = null,
        javaDateTimePattern = null,
        dateTimeFormats = dateTimeFormats,
        nullStrings = nullStrings,
        skipTypes = skipTypes,
        useFastDoubleParser = useFastDoubleParser,
        parseExperimentalUuid = parseExperimentalUuid,
        parseExperimentalInstant = parseExperimentalInstant,
    )

    // javaDateTimeFormatter constructor
    public constructor(
        locale: Locale? = null,
        javaDateTimeFormatter: JavaDateTimeFormatter?,
        nullStrings: Set<String>? = null,
        skipTypes: Set<KType>? = null,
        useFastDoubleParser: Boolean? = null,
        parseExperimentalUuid: Boolean? = null,
        parseExperimentalInstant: Boolean? = null,
    ) : this(
        locale = locale,
        javaDateTimeFormatter = javaDateTimeFormatter,
        javaDateTimePattern = null,
        dateTimeFormats = null,
        nullStrings = nullStrings,
        skipTypes = skipTypes,
        useFastDoubleParser = useFastDoubleParser,
        parseExperimentalUuid = parseExperimentalUuid,
        parseExperimentalInstant = parseExperimentalInstant,
    )

    // javaDateTimePattern constructor 2
    public constructor(
        locale: Locale? = null,
        javaDateTimePattern: String?,
        nullStrings: Set<String>? = null,
        skipTypes: Set<KType>? = null,
        useFastDoubleParser: Boolean? = null,
        parseExperimentalUuid: Boolean? = null,
        parseExperimentalInstant: Boolean? = null,
    ) : this(
        locale = locale,
        javaDateTimeFormatter = null,
        javaDateTimePattern = javaDateTimePattern,
        dateTimeFormats = null,
        nullStrings = nullStrings,
        skipTypes = skipTypes,
        useFastDoubleParser = useFastDoubleParser,
        parseExperimentalUuid = parseExperimentalUuid,
        parseExperimentalInstant = parseExperimentalInstant,
    )

    // region deprecated constructors

    /** For binary compatibility. */
    @Deprecated(
        message = PARSER_OPTIONS,
        level = DeprecationLevel.HIDDEN,
    )
    public constructor(
        locale: Locale? = null,
        dateTimeFormatter: JavaDateTimeFormatter? = null,
        dateTimePattern: String? = null,
        nullStrings: Set<String>? = null,
        skipTypes: Set<KType>? = null,
        useFastDoubleParser: Boolean? = null,
        parseExperimentalUuid: Boolean? = null,
        parseExperimentalInstant: Boolean? = null,
    ) : this(
        locale = locale,
        javaDateTimeFormatter = dateTimeFormatter,
        javaDateTimePattern = dateTimePattern,
        dateTimeFormats = null,
        nullStrings = nullStrings,
        skipTypes = skipTypes,
        useFastDoubleParser = useFastDoubleParser,
        parseExperimentalUuid = parseExperimentalUuid,
        parseExperimentalInstant = parseExperimentalInstant,
    )

    /** For binary compatibility. */
    @Deprecated(
        message = PARSER_OPTIONS,
        level = DeprecationLevel.HIDDEN,
    )
    public constructor(
        locale: Locale? = null,
        dateTimeFormatter: DateTimeFormatter? = null,
        dateTimePattern: String? = null,
        nullStrings: Set<String>? = null,
        skipTypes: Set<KType>? = null,
        useFastDoubleParser: Boolean? = null,
    ) : this(
        locale = locale,
        javaDateTimeFormatter = dateTimeFormatter,
        javaDateTimePattern = dateTimePattern,
        dateTimeFormats = null,
        nullStrings = nullStrings,
        skipTypes = skipTypes,
        useFastDoubleParser = useFastDoubleParser,
        parseExperimentalUuid = null,
        parseExperimentalInstant = null,
    )

    /** For binary compatibility. */
    @Deprecated(
        message = PARSER_OPTIONS,
        level = DeprecationLevel.HIDDEN,
    )
    public constructor(
        locale: Locale? = null,
        dateTimeFormatter: DateTimeFormatter? = null,
        dateTimePattern: String? = null,
        nullStrings: Set<String>? = null,
    ) : this(
        locale = locale,
        javaDateTimeFormatter = dateTimeFormatter,
        javaDateTimePattern = dateTimePattern,
        dateTimeFormats = null,
        nullStrings = nullStrings,
        skipTypes = null,
        useFastDoubleParser = null,
        parseExperimentalUuid = null,
        parseExperimentalInstant = null,
    )

    // endregion

    internal fun getJavaDateTimeFormatter(): JavaDateTimeFormatter? =
        when {
            javaDateTimeFormatter != null -> javaDateTimeFormatter

            javaDateTimePattern != null && locale != null ->
                JavaDateTimeFormatter.ofPattern(javaDateTimePattern, locale)

            javaDateTimePattern != null -> JavaDateTimeFormatter.ofPattern(javaDateTimePattern)

            else -> null
        }

    public fun copy(
        locale: Locale? = this.locale,
        javaDateTimeFormatter: JavaDateTimeFormatter? = this.javaDateTimeFormatter,
        javaDateTimePattern: String? = this.javaDateTimePattern,
        dateTimeFormats: Set<DateTimeFormat<*>>? = this.dateTimeFormats,
        nullStrings: Set<String>? = this.nullStrings,
        skipTypes: Set<KType>? = this.skipTypes,
        useFastDoubleParser: Boolean? = this.useFastDoubleParser,
        parseExperimentalUuid: Boolean? = this.parseExperimentalUuid,
        parseExperimentalInstant: Boolean? = this.parseExperimentalInstant,
    ): ParserOptions =
        ParserOptions(
            locale = locale,
            javaDateTimeFormatter = javaDateTimeFormatter,
            javaDateTimePattern = javaDateTimePattern,
            dateTimeFormats = dateTimeFormats,
            nullStrings = nullStrings,
            skipTypes = skipTypes,
            useFastDoubleParser = useFastDoubleParser,
            parseExperimentalUuid = parseExperimentalUuid,
            parseExperimentalInstant = parseExperimentalInstant,
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParserOptions

        if (useFastDoubleParser != other.useFastDoubleParser) return false
        if (parseExperimentalUuid != other.parseExperimentalUuid) return false
        if (parseExperimentalInstant != other.parseExperimentalInstant) return false
        if (javaDateTimeArgumentsProvided != other.javaDateTimeArgumentsProvided) return false
        if (locale != other.locale) return false
        if (javaDateTimeFormatter != other.javaDateTimeFormatter) return false
        if (javaDateTimePattern != other.javaDateTimePattern) return false
        if (dateTimeFormats != other.dateTimeFormats) return false
        if (nullStrings != other.nullStrings) return false
        if (skipTypes != other.skipTypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = useFastDoubleParser?.hashCode() ?: 0
        result = 31 * result + (parseExperimentalUuid?.hashCode() ?: 0)
        result = 31 * result + (parseExperimentalInstant?.hashCode() ?: 0)
        result = 31 * result + javaDateTimeArgumentsProvided.hashCode()
        result = 31 * result + (locale?.hashCode() ?: 0)
        result = 31 * result + (javaDateTimeFormatter?.hashCode() ?: 0)
        result = 31 * result + (javaDateTimePattern?.hashCode() ?: 0)
        result = 31 * result + (dateTimeFormats?.hashCode() ?: 0)
        result = 31 * result + (nullStrings?.hashCode() ?: 0)
        result = 31 * result + (skipTypes?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "ParserOptions(locale=$locale, javaDateTimeFormatter=$javaDateTimeFormatter, javaDateTimePattern=$javaDateTimePattern, dateTimeFormats=$dateTimeFormats, nullStrings=$nullStrings, skipTypes=$skipTypes, useFastDoubleParser=$useFastDoubleParser, parseExperimentalUuid=$parseExperimentalUuid, parseExperimentalInstant=$parseExperimentalInstant, parseToJavaDateTimes=$javaDateTimeArgumentsProvided)"
}

/** @include [tryParseImpl] */
public fun DataColumn<String?>.tryParse(options: ParserOptions? = null): DataColumn<*> = tryParseImpl(options)

/**
 * Tries to parse a column of chars into a column of a different type.
 * Each parser in [Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried. If all the others fail, the final parser
 * returns strings.
 *
 * Parsers that are [covered by][StringParser.coveredBy] other parsers are skipped.
 *
 * @param options options for parsing, like providing a locale or a custom date-time formatter
 * @throws IllegalStateException if no valid parser is found (unlikely, unless the `String` parser is disabled)
 * @return a new column with parsed values
 */
@JvmName("tryParseChar")
public fun DataColumn<Char?>.tryParse(options: ParserOptions? = null): DataColumn<*> =
    map { it?.toString() }.tryParseImpl(options)

/**
 * Tries to parse a column of strings into a column of a different type.
 * Each parser in [Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried.
 *
 * If all fail [IllegalStateException] is thrown. If you don't want this exception to be thrown,
 * use [tryParse] instead.
 *
 * Parsers that are [covered by][StringParser.coveredBy] other parsers are skipped.
 *
 * @param options options for parsing, like providing a locale or a custom date-time formatter
 * @throws IllegalStateException if no valid parser is found
 * @return a new column with parsed values
 */
public fun DataColumn<String?>.parse(options: ParserOptions? = null): DataColumn<*> =
    tryParse(options).also { if (it.isSubtypeOf<String?>()) error("Can't guess column type") }

/**
 * Tries to parse a column of chars as strings into a column of a different type.
 * Each parser in [Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried.
 *
 * If all fail [IllegalStateException] is thrown. If you don't want this exception to be thrown,
 * use [tryParse] instead.
 *
 * Parsers that are [covered by][StringParser.coveredBy] other parsers are skipped.
 *
 * @param options options for parsing, like providing a locale or a custom date-time formatter
 * @return a new column with parsed values
 */
@JvmName("parseChar")
public fun DataColumn<Char?>.parse(options: ParserOptions? = null): DataColumn<*> =
    map { it?.toString() }
        .tryParse(options)
        .also { if (it.isSubtypeOf<Char?>() || it.isSubtypeOf<String?>()) error("Can't guess column type") }

@JvmName("parseAnyFrameNullable")
public fun DataColumn<AnyFrame?>.parse(options: ParserOptions? = null): DataColumn<AnyFrame?> =
    map { it?.parse(options) }
