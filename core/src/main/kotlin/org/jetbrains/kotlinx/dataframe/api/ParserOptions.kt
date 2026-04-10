package org.jetbrains.kotlinx.dataframe.api

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.KotlinxDateTimeLocaleSnippet
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.fromPattern
import org.jetbrains.kotlinx.dataframe.impl.io.FastDoubleParser
import org.jetbrains.kotlinx.dataframe.util.ADD_DATE_TIME_PATTERN
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.Locale
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalTime as JavaLocalTime

/**
 * Global counterpart of [ParserOptions].
 * Settings changed here will affect the defaults for all parsing operations.
 *
 * The default values are set by [Parsers.resetToDefault].
 */
public interface GlobalParserOptions {

    public fun addJavaDateTimeFormatter(formatter: DateTimeFormatter, formatType: KType? = null)

    /**
     * Adds a Java-based date-time pattern to the global parser options of DataFrame.
     *
     * DataFrame will attempt to parse [Strings][String] using your provided patterns first
     * before falling back to the default (ISO) formats.
     *
     * For example, to always allow DataFrame to parse "12/24 2023" [Strings][String]:
     * ```kt
     * DataFrame.parser.addJavaDateTimePattern("MM/dd yyyy")
     * ```
     *
     * @param [formatType] the expected java date-time type of the [pattern].
     *   If null, the pattern will be attempted for [JavaLocalDateTime], [JavaLocalDate], and [JavaLocalTime].
     */
    public fun addJavaDateTimePattern(pattern: String, formatType: KType? = null)

    /**
     * __Deprecated:__
     *
     * We recommend using [addDateTimeFormat] instead, built on kotlinx-datetime. This
     * provides a good DSL.
     *
     * For example:
     * ```kt
     * DataFrame.parser.addDateTimeFormat(
     *     LocalDate.Format {
     *         monthNumber(padding = Padding.SPACE); char('/'); day(); char(' '); year()
     *     },
     * )
     * ```
     *
     * We do allow parsing by pattern too, but it requires an Opt-In and the exact type this pattern belongs to:
     * ```kt
     * @OptIn(FormatStringsInDatetimeFormats::class)
     * DataFrame.parser.addDateTimeUnicodePattern<LocalDate>("MM/dd yyyy")
     * ```
     *
     * If you want to keep using the Java-based date-time parsing, you can use [addJavaDateTimePattern].
     */
    @OptIn(FormatStringsInDatetimeFormats::class)
    @Deprecated(
        message = ADD_DATE_TIME_PATTERN,
        replaceWith = ReplaceWith("addDateTimeUnicodePattern<LocalDateTime>(pattern)"),
        level = DeprecationLevel.ERROR,
    )
    public fun addDateTimePattern(pattern: String): Unit = addDateTimeUnicodePattern<LocalDateTime>(pattern)

    @FormatStringsInDatetimeFormats
    public fun addDateTimeUnicodePattern(pattern: String, formatType: KType)

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
     *     },
     * )
     * ```
     */
    public fun addDateTimeFormat(format: DateTimeFormat<out Any>, formatType: KType)

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

    /**
     * DataFrame supports parsing to either kotlinx-datetime or java.time types.
     *
     * By default, this is `null`, meaning we try Kotlin types first, and if that fails, we try Java types.
     *
     * This can be adjusted to force either one.
     *
     * We recommend using Kotlin types, however
     * @include [KotlinxDateTimeLocaleSnippet].
     *
     * @see [addDateTimeFormat]
     * @see [addJavaDateTimePattern]
     */
    public var dateTimeLibrary: ParseDateTimeLibrary?
}

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

/**
 * DataFrame supports parsing to either kotlinx-datetime or java.time types.
 *
 * By default, this is `null`, meaning we try Kotlin types first, and if that fails, we try Java types.
 *
 * This can be adjusted to force either one.
 *
 * We recommend using Kotlin types, however
 * @include [KotlinxDateTimeLocaleSnippet].
 */
public enum class ParseDateTimeLibrary {

    /** https://github.com/Kotlin/kotlinx-datetime */
    KOTLIN,

    /** https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html */
    JAVA,
}

/** @include [GlobalParserOptions.addDateTimeFormat] */
public inline fun <reified T : Any> GlobalParserOptions.addDateTimeFormat(format: DateTimeFormat<out T>) {
    addDateTimeFormat(format = format, formatType = typeOf<T>())
}

/** @include [GlobalParserOptions.addDateTimeUnicodePattern] */
@FormatStringsInDatetimeFormats
public inline fun <reified T : Any> GlobalParserOptions.addDateTimeUnicodePattern(pattern: String) {
    addDateTimeUnicodePattern(pattern = pattern, formatType = typeOf<T>())
}

/** [GlobalParserOptions.addJavaDateTimeFormatter] */
public inline fun <reified T : Temporal> GlobalParserOptions.addJavaDateTimeFormatter(formatter: DateTimeFormatter) {
    addJavaDateTimeFormatter(formatter = formatter, formatType = typeOf<T>())
}

/** @include [GlobalParserOptions.addJavaDateTimePattern] */
public inline fun <reified T : Temporal> GlobalParserOptions.addJavaDateTimePattern(pattern: String) {
    addJavaDateTimePattern(pattern = pattern, formatType = typeOf<T>())
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
 * TODO
 *
 * @param locale locale to use for numbers (and Java date-time types), defaults to the System default locale.
 * @param dateTime TODO can be used to force kotlin/java, or setting this will override default and global
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
public class ParserOptions(
    public val locale: Locale? = null,
    public val dateTime: DateTimeParserOptions<*>? = null,
    public val nullStrings: Set<String>? = null,
    public val skipTypes: Set<KType>? = null,
    public val useFastDoubleParser: Boolean? = null,
    public val parseExperimentalUuid: Boolean? = null,
    public val parseExperimentalInstant: Boolean? = null,
) {
    public fun copy(
        locale: Locale? = this.locale,
        dateTimeParserOptions: DateTimeParserOptions<*>? = this.dateTime?.copy(),
        nullStrings: Iterable<String>? = this.nullStrings,
        skipTypes: Iterable<KType>? = this.skipTypes,
        useFastDoubleParser: Boolean? = this.useFastDoubleParser,
        parseExperimentalUuid: Boolean? = this.parseExperimentalUuid,
        parseExperimentalInstant: Boolean? = this.parseExperimentalInstant,
    ): ParserOptions =
        ParserOptions(
            locale = locale,
            dateTime = dateTimeParserOptions,
            nullStrings = nullStrings?.toSet(),
            skipTypes = skipTypes?.toSet(),
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
        if (locale != other.locale) return false
        if (dateTime != other.dateTime) return false
        if (nullStrings != other.nullStrings) return false
        if (skipTypes != other.skipTypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = useFastDoubleParser?.hashCode() ?: 0
        result = 31 * result + (parseExperimentalUuid?.hashCode() ?: 0)
        result = 31 * result + (parseExperimentalInstant?.hashCode() ?: 0)
        result = 31 * result + (locale?.hashCode() ?: 0)
        result = 31 * result + (dateTime?.hashCode() ?: 0)
        result = 31 * result + (nullStrings?.hashCode() ?: 0)
        result = 31 * result + (skipTypes?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "ParserOptions(locale=$locale, dateTimeParserOptions=$dateTime, nullStrings=$nullStrings, skipTypes=$skipTypes, useFastDoubleParser=$useFastDoubleParser, parseExperimentalUuid=$parseExperimentalUuid, parseExperimentalInstant=$parseExperimentalInstant)"
}

public sealed class DateTimeParserOptions<T>(public open val dateTimeFormats: Set<Pair<KType?, T>>?) {

    public abstract fun copy(): DateTimeParserOptions<T>

    /**
     * Kotlin(x) variant of [DateTimeParserOptions] using [DateTimeFormat].
     *
     * If supplied to [ParserOptions.dateTime],
     * parsing will run in Kotlin time mode (similar to setting
     * [DataFrame.parser.dateTimeLibrary][GlobalParserOptions.dateTimeLibrary] to [ParseDateTimeLibrary.KOTLIN]).
     *
     * Additionally, if [dateTimeFormats] is not `null`, a.k.a. any format or pattern is provided,
     * parsing will use the provided formats __ONLY__. Default formats and those in the
     * [global parser options][DataFrame.Companion.parser] will be ignored.
     */
    public open class Kotlin private constructor(
        override val dateTimeFormats: Set<Pair<KType, DateTimeFormat<out Any>>>? = null,
    ) : DateTimeParserOptions<DateTimeFormat<out Any>>(dateTimeFormats) {

        public companion object : Kotlin() {
            @JvmName("fromSet")
            public operator fun invoke(dateTimeFormats: Set<Pair<KType, DateTimeFormat<out Any>>>? = null): Kotlin =
                Kotlin(dateTimeFormats = dateTimeFormats)

            @JvmName("fromFormats")
            public operator fun invoke(
                dateTimeFormat: Pair<KType, DateTimeFormat<out Any>>,
                vararg dateTimeFormats: Pair<KType, DateTimeFormat<out Any>>,
            ): Kotlin = Kotlin(dateTimeFormats = setOf(dateTimeFormat, *dateTimeFormats))

            @JvmName("fromPatterns")
            @FormatStringsInDatetimeFormats
            public operator fun invoke(
                unicodePattern: Pair<KType, String>,
                vararg unicodePatterns: Pair<KType, String>,
            ): Kotlin =
                Kotlin(
                    dateTimeFormats = setOf(unicodePattern, *unicodePatterns)
                        .map { (formatType, pattern) ->
                            formatType to DateTimeFormat.fromPattern(pattern, formatType)
                        }.toSet(),
                )
        }

        override fun copy(): Kotlin = Kotlin(dateTimeFormats = dateTimeFormats)

        public fun copy(
            dateTimeFormats: Iterable<Pair<KType, DateTimeFormat<out Any>>>? = this.dateTimeFormats,
        ): Kotlin = Kotlin(dateTimeFormats = dateTimeFormats?.toSet())

        public fun withFormat(format: DateTimeFormat<out Any>?, formatType: KType): Kotlin =
            if (format == null) {
                this
            } else {
                copy(
                    dateTimeFormats = dateTimeFormats.orEmpty() + (formatType.withNullability(false) to format),
                )
            }

        public inline fun <reified T : Any> withFormat(format: DateTimeFormat<out T>?): Kotlin =
            withFormat(format = format, formatType = typeOf<T>())

        @FormatStringsInDatetimeFormats
        public fun withPattern(pattern: String?, formatType: KType): Kotlin =
            if (pattern == null) {
                this
            } else {
                withFormat(
                    format = DateTimeFormat.fromPattern(pattern, formatType),
                    formatType = formatType,
                )
            }

        @FormatStringsInDatetimeFormats
        public inline fun <reified T : Any> withPattern(pattern: String?): Kotlin =
            withPattern(pattern = pattern, formatType = typeOf<T>())

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Kotlin) return false

            if (dateTimeFormats != other.dateTimeFormats) return false

            return true
        }

        override fun hashCode(): Int = dateTimeFormats?.hashCode() ?: 0

        override fun toString(): String = "DateTimeParserOptions.Kotlin(dateTimeFormats=$dateTimeFormats)"
    }

    /**
     * Java time variant of [DateTimeParserOptions] using [DateTimeFormatter].
     *
     * If supplied to [ParserOptions.dateTime],
     * parsing will run in Java time mode (similar to setting
     * [DataFrame.parser.dateTimeLibrary][GlobalParserOptions.dateTimeLibrary] to [ParseDateTimeLibrary.JAVA]).
     *
     * Additionally, if [dateTimeFormats] is not `null`, a.k.a. any formatter or pattern is provided,
     * parsing will use the provided formatters __ONLY__. Default formatters and those in the
     * [global parser options][DataFrame.Companion.parser] will be ignored.
     *
     * @param locale locale for date/time parsing, falls back to [ParserOptions.locale] if `null`
     */
    public open class Java private constructor(
        public val locale: Locale? = null,
        override val dateTimeFormats: Set<Pair<KType?, DateTimeFormatter>>? = null,
    ) : DateTimeParserOptions<DateTimeFormatter>(dateTimeFormats) {

        public companion object : Java() {
            @JvmName("fromSet")
            public operator fun invoke(
                locale: Locale? = null,
                dateTimeFormats: Set<Pair<KType?, DateTimeFormatter>>? = null,
            ): Java = Java(locale = locale, dateTimeFormats = dateTimeFormats)

            @JvmName("fromFormats")
            public operator fun invoke(
                locale: Locale?,
                dateTimeFormat: Pair<KType?, DateTimeFormatter>,
                vararg dateTimeFormats: Pair<KType?, DateTimeFormatter>,
            ): Java = Java(locale = locale, dateTimeFormats = setOf(dateTimeFormat, *dateTimeFormats))

            @JvmName("fromFormats")
            public operator fun invoke(
                dateTimeFormat: Pair<KType?, DateTimeFormatter>,
                vararg dateTimeFormats: Pair<KType?, DateTimeFormatter>,
            ): Java = invoke(null, dateTimeFormat, *dateTimeFormats)

            @JvmName("fromPatterns")
            public operator fun invoke(
                locale: Locale? = null,
                dateTimePattern: Pair<KType?, String>,
                vararg dateTimePatterns: Pair<KType?, String>,
            ): Java =
                Java(
                    locale = locale,
                    dateTimeFormats = setOf(
                        dateTimePattern,
                        *dateTimePatterns,
                    ).map { (formatType, pattern) ->
                        formatType to DateTimeFormatter.ofPattern(pattern)
                    }.toSet(),
                )

            @JvmName("fromPatterns")
            public operator fun invoke(
                dateTimePattern: Pair<KType?, String>,
                vararg dateTimePatterns: Pair<KType?, String>,
            ): Java = invoke(null, dateTimePattern, *dateTimePatterns)
        }

        override fun copy(): Java =
            Java(
                locale = locale,
                dateTimeFormats = dateTimeFormats,
            )

        public fun copy(
            locale: Locale? = this.locale,
            dateTimeFormats: Iterable<Pair<KType?, DateTimeFormatter>>? = this.dateTimeFormats,
        ): Java =
            Java(
                locale = locale,
                dateTimeFormats = dateTimeFormats?.toSet(),
            )

        public fun withLocale(locale: Locale?): Java = copy(locale = locale)

        public fun withFormatter(formatter: DateTimeFormatter?, formatType: KType? = null): Java =
            if (formatter == null) {
                this
            } else {
                copy(dateTimeFormats = dateTimeFormats.orEmpty() + (formatType to formatter))
            }

        public inline fun <reified T : Temporal> withFormatter(formatter: DateTimeFormatter?): Java =
            withFormatter(formatter = formatter, formatType = typeOf<T>())

        public fun withPattern(pattern: String?, formatType: KType?): Java =
            if (pattern == null) {
                this
            } else {
                withFormatter(formatter = DateTimeFormatter.ofPattern(pattern), formatType = formatType)
            }

        public fun withPattern(pattern: String?): Java =
            withPattern(
                pattern = pattern,
                formatType = null,
            )

        @JvmSynthetic
        @JvmName("withDateTimePatternReified")
        public inline fun <reified T : Temporal> withPattern(pattern: String?): Java =
            withPattern(pattern = pattern, formatType = typeOf<T>())

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Java) return false

            if (locale != other.locale) return false
            if (dateTimeFormats != other.dateTimeFormats) return false

            return true
        }

        override fun hashCode(): Int {
            var result = locale?.hashCode() ?: 0
            result = 31 * result + (dateTimeFormats?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String = "DateTimeParserOptions.Java(locale=$locale, dateTimeFormats=$dateTimeFormats)"
    }
}
