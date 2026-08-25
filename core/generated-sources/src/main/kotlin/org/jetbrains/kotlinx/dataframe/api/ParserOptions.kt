package org.jetbrains.kotlinx.dataframe.api

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.KotlinxDateTimeLocaleSnippet
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.fromPattern
import org.jetbrains.kotlinx.dataframe.impl.io.FastDoubleParser
import org.jetbrains.kotlinx.dataframe.util.ADD_DATE_TIME_PATTERN
import org.jetbrains.kotlinx.dataframe.util.PARSER_OPTIONS
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.Locale
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.time.Instant as JavaInstant
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalTime as JavaLocalTime

/**
 * Global counterpart of [<code>ParserOptions</code>][ParserOptions].
 *
 * These options are used to configure how [<code>DataColumns</code>][DataColumn] of type [<code>String</code>][String] or [<code>String?</code>][String]
 * should be parsed.
 *
 * Settings changed here will affect the defaults for all parsing operations.
 * You can always pass a [<code>ParserOptions</code>][ParserOptions] object to functions that perform parsing, like [<code>tryParse</code>][tryParse], [<code>parse</code>][parse], [<code>convert</code>][convert],
 * or even [<code>DataFrame.readCsv</code>][DataFrame.Companion.readCsv] to override these options.
 *
 * The default values are set by [<code>Parsers.resetToDefault</code>][Parsers.resetToDefault].
 *
 * #### Parsing date-time strings
 *
 * DataFrame tries parsing date-time strings using
 * - Custom global Kotlin-, and Java date-time formats, if provided;
 * - Default Kotlin-, and Java ISO date-time formats.
 *
 * You can customize this behavior by:
 * - Forcing one or the other date-time format type by changing [<code>dateTimeLibrary</code>][dateTimeLibrary];
 * - Providing custom date-time formats/formatters and/or custom date-time patterns
 *   ([<code>addDateTimeFormat</code>][addDateTimeFormat], [<code>addDateTimeUnicodePattern</code>][addDateTimeUnicodePattern], [<code>addJavaDateTimeFormatter</code>][addJavaDateTimeFormatter], [<code>addJavaDateTimePattern</code>][addJavaDateTimePattern]);
 * - Resetting to default formats;
 *
 * Finally, if a parsing function is provided with [<code>ParserOptions</code>][ParserOptions] and [<code>ParserOptions.dateTime</code>][ParserOptions.dateTime] is not `null`,
 * the global [<code>dateTimeLibrary</code>][dateTimeLibrary] parser option will be overridden.
 *
 * Concretely, `ParserOptions(dateTime = DateTimeParserOptions.Java)` is equivalent to having
 * `DataFrame.parser.dateTimeLibrary = ParseDateTimeLibrary.JAVA` for that particular function call.
 *
 * In addition, if that [<code>DateTimeParserOptions</code>][DateTimeParserOptions] has any custom formats or patterns, the custom- and default
 * global formats will be ignored, allowing you to essentially override them.
 *
 * For more information: [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
 */
public interface GlobalParserOptions {

    /**
     * Adds a Java-based date-time formatter to the global parser options of DataFrame.
     *
     * DataFrame will attempt to parse [<code>Strings</code>][String] using your provided formatters first
     * before falling back to the default (ISO) formats.
     *
     * For example, you could add the [<code>DateTimeFormatter.RFC_1123_DATE_TIME</code>][DateTimeFormatter.RFC_1123_DATE_TIME] formatter:
     * ```kt
     * DataFrame.parser.addJavaDateTimeFormatter<java.time.LocalDateTime>(DateTimeFormatter.RFC_1123_DATE_TIME)
     * // or
     * DataFrame.parser.addJavaDateTimeFormatter(DateTimeFormatter.RFC_1123_DATE_TIME)
     * ```
     *
     * NOTE: Formatters provided to global parser options will be ignored for function calls provided
     * with custom [<code>DateTimeParserOptions.dateTimeFormats</code>][DateTimeParserOptions.dateTimeFormats].
     *
     * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
     *
     * @param [formatter] the Java date-time formatter to add.
     * @param [formatType] the expected java date-time type of the [<code>formatter</code>][formatter].
     *   If `null`, the formatter will be attempted for [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>JavaLocalDate</code>][JavaLocalDate], [<code>JavaLocalTime</code>][JavaLocalTime],
     *   and [<code>JavaInstant</code>][JavaInstant].
     * @see [addJavaDateTimePattern]
     */
    public fun addJavaDateTimeFormatter(formatter: DateTimeFormatter, formatType: KType? = null)

    /**
     * Adds a Java-based date-time pattern to the global parser options of DataFrame.
     *
     * DataFrame will attempt to parse [<code>Strings</code>][String] using your provided patterns first
     * before falling back to the default (ISO) formats.
     *
     * For example, to always allow DataFrame to parse "12/24 2023" [<code>Strings</code>][String]:
     * ```kt
     * DataFrame.parser.addJavaDateTimePattern<java.time.LocalDate>("MM/dd yyyy")
     * // or
     * DataFrame.parser.addJavaDateTimePattern("MM/dd yyyy")
     * ```
     *
     * NOTE: Patterns provided to global parser options will be ignored for function calls provided
     * with custom [<code>DateTimeParserOptions.dateTimeFormats</code>][DateTimeParserOptions.dateTimeFormats].
     *
     * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
     *
     * @param [pattern] the date-time pattern to add.
     * @param [formatType] the expected java date-time type of the [<code>pattern</code>][pattern].
     *   If `null`, the pattern will be attempted for [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>JavaLocalDate</code>][JavaLocalDate], [<code>JavaLocalTime</code>][JavaLocalTime],
     *   and [<code>JavaInstant</code>][JavaInstant].
     * @see [addJavaDateTimeFormatter]
     */
    public fun addJavaDateTimePattern(pattern: String, formatType: KType? = null)

    /**
     * __Deprecated:__
     *
     * We recommend using [<code>addDateTimeFormat</code>][addDateTimeFormat] instead, built on kotlinx-datetime. This
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
     * If you want to keep using the Java-based date-time parsing, you can use [<code>addJavaDateTimePattern</code>][addJavaDateTimePattern].
     *
     * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
     */
    @OptIn(FormatStringsInDatetimeFormats::class)
    @Deprecated(
        message = ADD_DATE_TIME_PATTERN,
        replaceWith = ReplaceWith("addDateTimeUnicodePattern<LocalDateTime>(pattern)"),
        level = DeprecationLevel.ERROR,
    )
    public fun addDateTimePattern(pattern: String): Unit = addDateTimeUnicodePattern<LocalDateTime>(pattern)

    /**
     * Adds a unicode date-time pattern to the global parser options of DataFrame.
     *
     * NOTE: Requires `@OptIn(FormatStringsInDatetimeFormats::class)` to be used, as usage
     * of [<code>addDateTimeFormat</code>][addDateTimeFormat] is recommended.
     *
     * DataFrame will attempt to parse [<code>Strings</code>][String] using your provided patterns first
     * before falling back to the default (ISO) formats.
     *
     * For example, to always allow DataFrame to parse "12/24 2023" [<code>Strings</code>][String]:
     * ```kt
     * @OptIn(FormatStringsInDatetimeFormats::class)
     * DataFrame.parser.addDateTimeUnicodePattern<LocalDate>("MM/dd yyyy")
     * ```
     * This is a shortcut for:
     * ```kt
     * DataFrame.parser.addDateTimeFormat(LocalDate.Format { byUnicodePattern("MM/dd yyyy") })
     * ```
     *
     * NOTE: Patterns provided to global parser options will be ignored for function calls provided
     * with custom [<code>DateTimeParserOptions.dateTimeFormats</code>][DateTimeParserOptions.dateTimeFormats].
     *
     * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
     *
     * See also: [<code>DateTimeComponents fallback mechanism</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeComponentsFallback]
     *
     * @param [pattern] the date-time pattern to add.
     * @param [formatType] the expected date-time type of the [<code>pattern</code>][pattern].
     *
     * @see [addDateTimeFormat]
     * @see [DateTimeFormatBuilder.byUnicodePattern]
     */
    @FormatStringsInDatetimeFormats
    public fun addDateTimeUnicodePattern(pattern: String, formatType: KType)

    /**
     * Adds [<code>format</code>][format] to the global parser options of DataFrame.
     *
     * DataFrame will attempt to parse [<code>Strings</code>][String] using your provided formats first
     * before falling back to the default (ISO) formats.
     *
     * For example, to always allow DataFrame to parse "12/24 2023" [<code>Strings</code>][String]:
     * ```kt
     * DataFrame.parser.addDateTimeFormat(
     *     LocalDate.Format {
     *         monthNumber(padding = Padding.SPACE); char('/'); day(); char(' '); year()
     *     },
     * )
     * ```
     *
     * NOTE: Formats provided to global parser options will be ignored for function calls provided
     * with custom [<code>DateTimeParserOptions.dateTimeFormats</code>][DateTimeParserOptions.dateTimeFormats].
     *
     * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
     *
     * See also: [<code>DateTimeComponents fallback mechanism</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeComponentsFallback]
     *
     * @param [format] the date-time format to add.
     * @param [formatType] the expected date-time type of the [<code>format</code>][format].
     * @see [addDateTimeUnicodePattern]
     */
    public fun addDateTimeFormat(format: DateTimeFormat<out Any>, formatType: KType)

    /**
     * Adds [<code>str</code>][str] to the [<code>collection of Strings</code>][nulls]
     * that will be parsed to `null`.
     *
     * For more information: [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     */
    public fun addNullString(str: String)

    /**
     * This function can be called to skip some types.
     * Parsing will be attempted for [<code>all other types</code>][availableParserTypes].
     *
     * For more information: [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     */
    public fun addSkipType(type: KType)

    /**
     * Whether to use [<code>FastDoubleParser</code>][FastDoubleParser], defaults to `true`. Please report any issues you encounter.
     * This can be overridden by passing a custom [<code>ParserOptions</code>][ParserOptions] to the parsing function call.
     *
     * For more information: [See Parsing Doubles on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-doubles)
     */
    public var useFastDoubleParser: Boolean

    /**
     * Resets the global parser options.
     *
     * For more information: [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     */
    public fun resetToDefault()

    /**
     * The Locale to use for parsing numbers (and Java date-time types),
     * defaults to the System default locale.
     * This can be overridden by passing a custom [<code>ParserOptions</code>][ParserOptions] to the parsing function call.
     *
     * For more information: [See Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parser-options) [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     */
    public var locale: Locale

    /**
     * When a [<code>String</code>][String] is encountered matching any of these, it will be parsed to `null`.
     * This can be overridden by passing a custom [<code>ParserOptions</code>][ParserOptions] to the parsing function call.
     *
     * Defaults to `["null", "NULL", "NA", "N/A"]`.
     *
     * For more information: [See Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parser-options) [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     *
     * @see addNullString
     */
    public val nulls: Set<String>

    /**
     * Types in this set will be skipped during parsing.
     * Parsing will be attempted for [<code>all other types</code>][availableParserTypes].
     *
     * For more information: [See Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parser-options) [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     *
     * @see addSkipType
     */
    public val skipTypes: Set<KType>

    /**
     * Provides an overview of all types DataFrame can parse to.
     * This cannot be adjusted yet (#962).
     *
     * For more information: [See Parsing Order on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-order) [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     */
    public val availableParserTypes: Set<KType>

    /**
     * Whether to allow parsing UUIDs to the [<code>Uuid</code>][Uuid] type.
     * This is marked "stable" from Kotlin 2.4.0+, so, by default this is `true`.
     *
     * NOTE: If you are using an older Kotlin version,
     * interacting with a [<code>Uuid</code>][Uuid] in your code might require
     * `@`[<code>OptIn</code>][OptIn]`(`[<code>ExperimentalUuidApi</code>][ExperimentalUuidApi]`::class)`.
     * In notebooks, add `-opt-in=kotlin.uuid.ExperimentalUuidApi` to the compiler arguments.
     *
     * For more information: [See Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parser-options) [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     */
    public var parseExperimentalUuid: Boolean

    /**
     * Whether to allow parsing to the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] type.
     * This is marked "stable" from Kotlin 2.3.0+, so, by default this is `true`.
     *
     * If false, instants are recognized as the deprecated [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] type (#1350).
     *
     * NOTE: If you are using an older Kotlin version,
     * interacting with an [<code>Instant</code>][kotlin.time.Instant] in your code might require
     * `@`[<code>OptIn</code>][OptIn]`(`[<code>ExperimentalTime</code>][kotlin.time.ExperimentalTime]`::class)`.
     * In notebooks, add `-opt-in=kotlin.time.ExperimentalTime` to the compiler arguments.
     *
     * For more information: [See Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parser-options) [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     */
    public var parseExperimentalInstant: Boolean

    /**
     * DataFrame supports parsing to either kotlin(x)-datetime or java.time types.
     *
     * By default, this is `null`, meaning we try Kotlin types first, and if that fails, we try Java types.
     *
     * This can be adjusted to force either one.
     *
     * We recommend using Kotlin types, however
     * kotlinx-datetime [lacks localization support](https://github.com/Kotlin/kotlinx-datetime/discussions/253).
     *
     * If you need to provide a custom [<code>java.util.Locale</code>][java.util.Locale], we recommend parsing
     * to a [<code>java.time</code>][java.time]-based class first by adjusting the parser options before converting it to [<code>kotlinx.datetime</code>][kotlinx.datetime].
     *
     * See also: [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions], [<code>DataFrame.parser.dateTimeLibrary</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.dateTimeLibrary]
     *
     * This setting is overridden in any function where a given [<code>ParserOptions.dateTime</code>][ParserOptions.dateTime] is not null.
     *
     * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings) [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options)
     *
     * @see [addDateTimeFormat]
     * @see [addJavaDateTimePattern]
     */
    public var dateTimeLibrary: ParseDateTimeLibrary?
}

/** Global counterpart of [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions].
 *
 * These options are used to configure how [<code>DataColumns</code>][org.jetbrains.kotlinx.dataframe.DataColumn] of type [<code>String</code>][String] or [<code>String?</code>][String]
 * should be parsed.
 *
 * Settings changed here will affect the defaults for all parsing operations.
 * You can always pass a [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions] object to functions that perform parsing, like [<code>tryParse</code>][org.jetbrains.kotlinx.dataframe.api.tryParse], [<code>parse</code>][org.jetbrains.kotlinx.dataframe.api.parse], [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert],
 * or even [<code>DataFrame.readCsv</code>][DataFrame.Companion.readCsv] to override these options.
 *
 * The default values are set by [<code>Parsers.resetToDefault</code>][org.jetbrains.kotlinx.dataframe.impl.api.Parsers.resetToDefault].
 *
 * #### Parsing date-time strings
 *
 * DataFrame tries parsing date-time strings using
 * - Custom global Kotlin-, and Java date-time formats, if provided;
 * - Default Kotlin-, and Java ISO date-time formats.
 *
 * You can customize this behavior by:
 * - Forcing one or the other date-time format type by changing [<code>dateTimeLibrary</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.dateTimeLibrary];
 * - Providing custom date-time formats/formatters and/or custom date-time patterns
 *   ([<code>addDateTimeFormat</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.addDateTimeFormat], [<code>addDateTimeUnicodePattern</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.addDateTimeUnicodePattern], [<code>addJavaDateTimeFormatter</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.addJavaDateTimeFormatter], [<code>addJavaDateTimePattern</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.addJavaDateTimePattern]);
 * - Resetting to default formats;
 *
 * Finally, if a parsing function is provided with [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions] and [<code>ParserOptions.dateTime</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions.dateTime] is not `null`,
 * the global [<code>dateTimeLibrary</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.dateTimeLibrary] parser option will be overridden.
 *
 * Concretely, `ParserOptions(dateTime = DateTimeParserOptions.Java)` is equivalent to having
 * `DataFrame.parser.dateTimeLibrary = ParseDateTimeLibrary.JAVA` for that particular function call.
 *
 * In addition, if that [<code>DateTimeParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions] has any custom formats or patterns, the custom- and default
 * global formats will be ignored, allowing you to essentially override them.
 *
 * For more information: [See Global Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#global-parser-options) */
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
 * kotlinx-datetime [lacks localization support](https://github.com/Kotlin/kotlinx-datetime/discussions/253).
 *
 * If you need to provide a custom [<code>java.util.Locale</code>][java.util.Locale], we recommend parsing
 * to a [<code>java.time</code>][java.time]-based class first by adjusting the parser options before converting it to [<code>kotlinx.datetime</code>][kotlinx.datetime].
 *
 * See also: [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions], [<code>DataFrame.parser.dateTimeLibrary</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.dateTimeLibrary]
 *
 * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
 */
public enum class ParseDateTimeLibrary {

    /** https://github.com/Kotlin/kotlinx-datetime */
    KOTLIN,

    /** https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html */
    JAVA,
}

/** Adds [<code>format</code>][org.jetbrains.kotlinx.dataframe.api.format] to the global parser options of DataFrame.
 *
 * DataFrame will attempt to parse [<code>Strings</code>][String] using your provided formats first
 * before falling back to the default (ISO) formats.
 *
 * For example, to always allow DataFrame to parse "12/24 2023" [<code>Strings</code>][String]:
 * ```kt
 * DataFrame.parser.addDateTimeFormat(
 *     LocalDate.Format {
 *         monthNumber(padding = Padding.SPACE); char('/'); day(); char(' '); year()
 *     },
 * )
 * ```
 *
 * NOTE: Formats provided to global parser options will be ignored for function calls provided
 * with custom [<code>DateTimeParserOptions.dateTimeFormats</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions.dateTimeFormats].
 *
 * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
 *
 * See also: [<code>DateTimeComponents fallback mechanism</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeComponentsFallback]
 *
 * @param [format][org.jetbrains.kotlinx.dataframe.api.format] the date-time format to add.
 * @param [formatType] the expected date-time type of the [<code>format</code>][org.jetbrains.kotlinx.dataframe.api.format].
 * @see [addDateTimeUnicodePattern][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.addDateTimeUnicodePattern] */
public inline fun <reified T : Any> GlobalParserOptions.addDateTimeFormat(format: DateTimeFormat<out T>) {
    addDateTimeFormat(format = format, formatType = typeOf<T>())
}

/** Adds a unicode date-time pattern to the global parser options of DataFrame.
 *
 * NOTE: Requires `@OptIn(FormatStringsInDatetimeFormats::class)` to be used, as usage
 * of [<code>addDateTimeFormat</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.addDateTimeFormat] is recommended.
 *
 * DataFrame will attempt to parse [<code>Strings</code>][String] using your provided patterns first
 * before falling back to the default (ISO) formats.
 *
 * For example, to always allow DataFrame to parse "12/24 2023" [<code>Strings</code>][String]:
 * ```kt
 * @OptIn(FormatStringsInDatetimeFormats::class)
 * DataFrame.parser.addDateTimeUnicodePattern<LocalDate>("MM/dd yyyy")
 * ```
 * This is a shortcut for:
 * ```kt
 * DataFrame.parser.addDateTimeFormat(LocalDate.Format { byUnicodePattern("MM/dd yyyy") })
 * ```
 *
 * NOTE: Patterns provided to global parser options will be ignored for function calls provided
 * with custom [<code>DateTimeParserOptions.dateTimeFormats</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions.dateTimeFormats].
 *
 * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
 *
 * See also: [<code>DateTimeComponents fallback mechanism</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeComponentsFallback]
 *
 * @param [pattern] the date-time pattern to add.
 * @param [formatType] the expected date-time type of the [<code>pattern</code>][pattern].
 *
 * @see [addDateTimeFormat][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.addDateTimeFormat]
 * @see [DateTimeFormatBuilder.byUnicodePattern] */
@FormatStringsInDatetimeFormats
public inline fun <reified T : Any> GlobalParserOptions.addDateTimeUnicodePattern(pattern: String) {
    addDateTimeUnicodePattern(pattern = pattern, formatType = typeOf<T>())
}

/** Adds a Java-based date-time formatter to the global parser options of DataFrame.
 *
 * DataFrame will attempt to parse [<code>Strings</code>][String] using your provided formatters first
 * before falling back to the default (ISO) formats.
 *
 * For example, you could add the [<code>DateTimeFormatter.RFC_1123_DATE_TIME</code>][DateTimeFormatter.RFC_1123_DATE_TIME] formatter:
 * ```kt
 * DataFrame.parser.addJavaDateTimeFormatter<java.time.LocalDateTime>(DateTimeFormatter.RFC_1123_DATE_TIME)
 * // or
 * DataFrame.parser.addJavaDateTimeFormatter(DateTimeFormatter.RFC_1123_DATE_TIME)
 * ```
 *
 * NOTE: Formatters provided to global parser options will be ignored for function calls provided
 * with custom [<code>DateTimeParserOptions.dateTimeFormats</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions.dateTimeFormats].
 *
 * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
 *
 * @param [formatter] the Java date-time formatter to add.
 * @param [formatType] the expected java date-time type of the [<code>formatter</code>][formatter].
 *   If `null`, the formatter will be attempted for [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>JavaLocalDate</code>][JavaLocalDate], [<code>JavaLocalTime</code>][JavaLocalTime],
 *   and [<code>JavaInstant</code>][JavaInstant].
 * @see [addJavaDateTimePattern][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.addJavaDateTimePattern] */
public inline fun <reified T : Temporal> GlobalParserOptions.addJavaDateTimeFormatter(formatter: DateTimeFormatter) {
    addJavaDateTimeFormatter(formatter = formatter, formatType = typeOf<T>())
}

/** Adds a Java-based date-time pattern to the global parser options of DataFrame.
 *
 * DataFrame will attempt to parse [<code>Strings</code>][String] using your provided patterns first
 * before falling back to the default (ISO) formats.
 *
 * For example, to always allow DataFrame to parse "12/24 2023" [<code>Strings</code>][String]:
 * ```kt
 * DataFrame.parser.addJavaDateTimePattern<java.time.LocalDate>("MM/dd yyyy")
 * // or
 * DataFrame.parser.addJavaDateTimePattern("MM/dd yyyy")
 * ```
 *
 * NOTE: Patterns provided to global parser options will be ignored for function calls provided
 * with custom [<code>DateTimeParserOptions.dateTimeFormats</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions.dateTimeFormats].
 *
 * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
 *
 * @param [pattern] the date-time pattern to add.
 * @param [formatType] the expected java date-time type of the [<code>pattern</code>][pattern].
 *   If `null`, the pattern will be attempted for [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>JavaLocalDate</code>][JavaLocalDate], [<code>JavaLocalTime</code>][JavaLocalTime],
 *   and [<code>JavaInstant</code>][JavaInstant].
 * @see [addJavaDateTimeFormatter][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.addJavaDateTimeFormatter] */
public inline fun <reified T : Temporal> GlobalParserOptions.addJavaDateTimePattern(pattern: String) {
    addJavaDateTimePattern(pattern = pattern, formatType = typeOf<T>())
}

/**
 * When using [<code>DataFrame.convert</code>][DataFrame.convert] or [<code>DataColumn.convertTo</code>][DataColumn.convertTo] to
 * convert from [<code>String</code>][String] to a kotlinx-datetime type, like [<code>LocalDate</code>][LocalDate], fails to parse,
 * the [<code>DateTimeComponents</code>][DateTimeComponents] fallback-mechanism kicks in.
 *
 * Oftentimes it may namely be possible to parse the date-time string to the more flexible [<code>DateTimeComponents</code>][DateTimeComponents]
 * first and then convert that to [<code>LocalDate</code>][LocalDate] with a potential little loss of information.
 *
 * This means we can successfully call:
 * ```kt
 * columnOf("Mon, 30 Jun 2008 11:05:30 -0300").convertTo<LocalDate>()
 * ```
 * even though
 * ```kt
 * columnOf("Mon, 30 Jun 2008 11:05:30 -0300").parse()
 * ```
 * would produce a [<code>DateTimeComponents</code>][DateTimeComponents] column.
 *
 * Take this mechanism into account when providing custom [<code>DateTimeFormats</code>][DateTimeFormat] to the
 * ([<code>global</code>][GlobalParserOptions]) [<code>ParserOptions</code>][ParserOptions].
 *
 * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
 */
public typealias DateTimeComponentsFallback = Nothing

/**
 * ### Options for parsing [<code>String</code>][String]`?` columns
 *
 * These options are used to configure how [<code>DataColumn</code>][DataColumn]s of type [<code>String</code>][String] or [<code>String?</code>][String] should be parsed.
 * They can be passed to [<code>tryParse</code>][tryParse] and [<code>parse</code>][parse] functions.
 *
 * You can also use the [<code>DataFrame.parser</code>][DataFrame.Companion.parser] property to access and modify
 * the global parser configuration.
 *
 * If any of the arguments in [<code>ParserOptions</code>][ParserOptions] are `null` (or [<code>ParserOptions</code>][ParserOptions] itself is `null`),
 * the global configuration will be queried.
 *
 * For more information: [See Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parser-options)
 *
 * #### Parsing date-time strings
 *
 * By default, DataFrame tries parsing date-time strings using custom formats and patterns defined
 * in the [<code>global parser options</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser] and using default ISO formats.
 * This is done for both Kotlin- and Java date-time types in order.
 *
 * However, if a parsing function is provided with [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions] and [<code>ParserOptions.dateTime</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions.dateTime] is not `null`,
 * the global [<code>GlobalParserOptions.dateTimeLibrary</code>][org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions.dateTimeLibrary] parser option will be overridden.
 *
 * Concretely, `ParserOptions(dateTime = DateTimeParserOptions.Java)` is equivalent to having
 * `DataFrame.parser.dateTimeLibrary = ParseDateTimeLibrary.JAVA` for that particular function call.
 *
 * In addition, if that [<code>DateTimeParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeParserOptions.DateTimeParserOptions] has any custom formats or patterns, the custom- and default
 * global formats will be ignored, allowing you to essentially override them.
 *
 * For example:
 * ```kt
 * // force parsing using Java types only
 * df.parse(
 *     ParserOptions(dateTime = DateTimeParserOptions.Java),
 * )
 * // force parsing using the specified Kotlin LocalDate format only
 * val dateFormat = LocalDate.Format {
 *     monthNumber(padding = Padding.SPACE); char('/'); day(); char(' '); year()
 * }
 * df.parse(
 *     ParserOptions(
 *         dateTime = DateTimeParserOptions.Kotlin
 *             .withFormat(dateFormat),
 *     ),
 * )
 * ```
 *
 * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
 *
 * See also: [<code>DateTimeComponents fallback mechanism</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeComponentsFallback]
 *
 * @param locale locale to use for numbers (and Java date-time types), defaults to the System default locale.
 * @param dateTime Can be used to force parsing to Kotlin-, or Java date-time classes, and override
 *   default and custom global date-time formats. By default, it's `null`, meaning we try Kotlin types first,
 *   and if that fails, we try Java types.
 * @param nullStrings a set of strings that should be treated as `null` values. By default, it's
 *   `["null", "NULL", "NA", "N/A"]`.
 * @param skipTypes a set of types that should be skipped during parsing. Parsing will be attempted for all other types.
 *   By default, it's an empty set. To skip all types except a specified one, use [<code>convertTo</code>][convertTo] instead.
 * @param useFastDoubleParser whether to use [<code>FastDoubleParser</code>][FastDoubleParser], defaults to `true`. Please report any issues you encounter.
 * @param parseExperimentalUuid whether to allow parsing UUIDs to the [<code>Uuid</code>][Uuid] type.
 *   This is marked "stable" from Kotlin 2.4.0+, so, by default this is `true`.
 *   NOTE: If you are using an older Kotlin version,
 *   interacting with a [<code>Uuid</code>][Uuid] in your code might require
 *   `@`[<code>OptIn</code>][OptIn]`(`[<code>ExperimentalUuidApi</code>][ExperimentalUuidApi]`::class)`.
 *   In notebooks, add `-opt-in=kotlin.uuid.ExperimentalUuidApi` to the compiler arguments.
 * @param parseExperimentalInstant whether to allow parsing to the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] type.
 *    This is marked "stable" from Kotlin 2.3.0+, so, by default this is `true`.
 *    If false, instants are recognized as the deprecated [kotlinx.datetime.Instant] type (#1350).
 *   NOTE: If you are using an older Kotlin version,
 *   interacting with an [<code>Instant</code>][kotlin.time.Instant] in your code might require
 *   `@`[<code>OptIn</code>][OptIn]`(`[<code>ExperimentalTime</code>][kotlin.time.ExperimentalTime]`::class)`.
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

    // region deprecated constructors

    @Suppress("DEPRECATION")
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
        dateTimeFormatter = dateTimeFormatter,
        dateTimePattern = dateTimePattern,
        nullStrings = nullStrings,
        skipTypes = skipTypes,
        useFastDoubleParser = useFastDoubleParser,
        parseExperimentalUuid = null,
        parseExperimentalInstant = null,
    )

    @Suppress("DEPRECATION")
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
        dateTimeFormatter = dateTimeFormatter,
        dateTimePattern = dateTimePattern,
        nullStrings = nullStrings,
        skipTypes = null,
        useFastDoubleParser = null,
    )

    @Deprecated(
        message = PARSER_OPTIONS,
        level = DeprecationLevel.WARNING,
    )
    public constructor(
        locale: Locale? = null,
        dateTimeFormatter: DateTimeFormatter? = null,
        dateTimePattern: String? = null,
        nullStrings: Set<String>? = null,
        skipTypes: Set<KType>? = null,
        useFastDoubleParser: Boolean? = null,
        parseExperimentalUuid: Boolean? = null,
        parseExperimentalInstant: Boolean? = null,
    ) : this(
        locale = locale,
        dateTime = 0.run {
            require(dateTimeFormatter == null || dateTimePattern == null) {
                "dateTimeFormatter and dateTimePattern cannot be both specified"
            }
            when {
                dateTimeFormatter != null -> DateTimeParserOptions.Java.withFormatter(dateTimeFormatter)
                dateTimePattern != null -> DateTimeParserOptions.Java.withPattern(dateTimePattern)
                else -> null
            }
        },
        nullStrings = nullStrings,
        skipTypes = skipTypes,
        useFastDoubleParser = useFastDoubleParser,
        parseExperimentalUuid = parseExperimentalUuid,
        parseExperimentalInstant = parseExperimentalInstant,
    )
    // endregion
}

/**
 * By default, DataFrame tries parsing date-time strings using custom formats and patterns defined
 * in the [<code>global parser options</code>][DataFrame.Companion.parser] and using default ISO formats.
 * This is done for both Kotlin- and Java date-time types in order.
 *
 * However, if a parsing function is provided with [<code>ParserOptions</code>][ParserOptions] and [<code>ParserOptions.dateTime</code>][ParserOptions.dateTime] is not `null`,
 * the global [<code>GlobalParserOptions.dateTimeLibrary</code>][GlobalParserOptions.dateTimeLibrary] parser option will be overridden.
 *
 * Concretely, `ParserOptions(dateTime = DateTimeParserOptions.Java)` is equivalent to having
 * `DataFrame.parser.dateTimeLibrary = ParseDateTimeLibrary.JAVA` for that particular function call.
 *
 * In addition, if that [<code>DateTimeParserOptions</code>][DateTimeParserOptions] has any custom formats or patterns, the custom- and default
 * global formats will be ignored, allowing you to essentially override them.
 *
 * For example:
 * ```kt
 * // force parsing using Java types only
 * df.parse(
 *     ParserOptions(dateTime = DateTimeParserOptions.Java),
 * )
 * // force parsing using the specified Kotlin LocalDate format only
 * val dateFormat = LocalDate.Format {
 *     monthNumber(padding = Padding.SPACE); char('/'); day(); char(' '); year()
 * }
 * df.parse(
 *     ParserOptions(
 *         dateTime = DateTimeParserOptions.Kotlin
 *             .withFormat(dateFormat),
 *     ),
 * )
 * ```
 *
 * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
 *
 * See also: [<code>DateTimeComponents fallback mechanism</code>][org.jetbrains.kotlinx.dataframe.api.DateTimeComponentsFallback]
 */
public sealed class DateTimeParserOptions<T>(public open val dateTimeFormats: Set<Pair<KType?, T>>?) {

    public abstract fun copy(): DateTimeParserOptions<T>

    /**
     * Kotlin(x) variant of [<code>DateTimeParserOptions</code>][DateTimeParserOptions] using [<code>DateTimeFormat</code>][DateTimeFormat].
     *
     * If supplied to [<code>ParserOptions.dateTime</code>][ParserOptions.dateTime],
     * parsing will run in Kotlin time mode (similar to setting
     * [<code>DataFrame.parser.dateTimeLibrary</code>][GlobalParserOptions.dateTimeLibrary] to [<code>ParseDateTimeLibrary.KOTLIN</code>][ParseDateTimeLibrary.KOTLIN]).
     *
     * Additionally, if [<code>dateTimeFormats</code>][dateTimeFormats] is not `null`, a.k.a. any format or pattern is provided,
     * parsing will use the provided formats __ONLY__. Default formats and those in the
     * [<code>global parser options</code>][DataFrame.Companion.parser] will be ignored.
     *
     * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
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

        public fun withFormat(format: DateTimeFormat<out Any>?, formatType: KType): Kotlin {
            if (format == null) return this
            return copy(
                dateTimeFormats = dateTimeFormats.orEmpty() + (formatType.withNullability(false) to format),
            )
        }

        public inline fun <reified T : Any> withFormat(format: DateTimeFormat<out T>?): Kotlin =
            withFormat(format = format, formatType = typeOf<T>())

        @FormatStringsInDatetimeFormats
        public fun withPattern(pattern: String?, formatType: KType): Kotlin {
            if (pattern == null) return this
            return withFormat(
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
     * Java time variant of [<code>DateTimeParserOptions</code>][DateTimeParserOptions] using [<code>DateTimeFormatter</code>][DateTimeFormatter].
     *
     * If supplied to [<code>ParserOptions.dateTime</code>][ParserOptions.dateTime],
     * parsing will run in Java time mode (similar to setting
     * [<code>DataFrame.parser.dateTimeLibrary</code>][GlobalParserOptions.dateTimeLibrary] to [<code>ParseDateTimeLibrary.JAVA</code>][ParseDateTimeLibrary.JAVA]).
     *
     * Additionally, if [<code>dateTimeFormats</code>][dateTimeFormats] is not `null`, a.k.a. any formatter or pattern is provided,
     * parsing will use the provided formatters __ONLY__. Default formatters and those in the
     * [<code>global parser options</code>][DataFrame.Companion.parser] will be ignored.
     *
     * For more information: [See Parsing Date-time Strings on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-date-time-strings)
     *
     * @param locale locale for date/time parsing, falls back to [<code>ParserOptions.locale</code>][ParserOptions.locale] if `null`
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

        public fun withFormatter(formatter: DateTimeFormatter?, formatType: KType?): Java {
            if (formatter == null) return this
            return copy(dateTimeFormats = dateTimeFormats.orEmpty() + (formatType to formatter))
        }

        public fun withFormatter(formatter: DateTimeFormatter?): Java = withFormatter(formatter, null)

        @JvmName("withFormatterTyped")
        public inline fun <reified T : Temporal> withFormatter(formatter: DateTimeFormatter?): Java =
            withFormatter(formatter = formatter, formatType = typeOf<T>())

        public fun withPattern(pattern: String?, formatType: KType?): Java {
            if (pattern == null) return this
            return withFormatter(formatter = DateTimeFormatter.ofPattern(pattern), formatType = formatType)
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
