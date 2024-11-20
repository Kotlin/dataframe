package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.StringParser
import org.jetbrains.kotlinx.dataframe.impl.api.parseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.tryParseImpl
import org.jetbrains.kotlinx.dataframe.typeClass
import org.jetbrains.kotlinx.dataframe.util.PARSER_OPTIONS
import org.jetbrains.kotlinx.dataframe.util.PARSER_OPTIONS_COPY
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.reflect.KProperty

public val DataFrame.Companion.parser: GlobalParserOptions get() = Parsers

public fun <T> DataFrame<T>.parse(options: ParserOptions? = null, columns: ColumnsSelector<T, Any?>): DataFrame<T> =
    parseImpl(options, columns)

public fun <T> DataFrame<T>.parse(vararg columns: String, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C> DataFrame<T>.parse(vararg columns: ColumnReference<C>, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C> DataFrame<T>.parse(vararg columns: KProperty<C>, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumnSet() }

public interface GlobalParserOptions {

    public fun addDateTimePattern(pattern: String)

    public fun addNullString(str: String)

    public fun resetToDefault()

    public var locale: Locale
}

/**
 * ### Options for parsing [String]`?` columns
 *
 * @param locale locale to use for parsing dates and numbers, defaults to the System default locale.
 *   If specified instead of [dateTimeFormatter], it will be used in combination with [dateTimePattern]
 *   to create a [DateTimeFormatter]. Just providing [locale] will not allow you to parse
 *   locale-specific dates!
 * @param dateTimeFormatter a [DateTimeFormatter] to use for parsing dates, if not specified, it will be created
 *   from [dateTimePattern] and [locale]. If neither [dateTimeFormatter] nor [dateTimePattern] are specified,
 *   [DateTimeFormatter.ISO_LOCAL_DATE_TIME] will be used.
 * @param dateTimePattern a pattern to use for parsing dates. If specified instead of [dateTimeFormatter],
 *   it will be used to create a [DateTimeFormatter].
 * @param nullStrings a set of strings that should be treated as `null` values. By default, it's
 *   ["null", "NULL", "NA", "N/A"].
 * @param useFastDoubleParser whether to use the new _experimental_ FastDoubleParser, defaults to `false` for now.
 */
public data class ParserOptions(
    val locale: Locale? = null,
    // TODO, migrate to kotlinx.datetime.format.DateTimeFormat? https://github.com/Kotlin/dataframe/issues/876
    val dateTimeFormatter: DateTimeFormatter? = null,
    val dateTimePattern: String? = null,
    val nullStrings: Set<String>? = null,
    val useFastDoubleParser: Boolean = false,
) {

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
        dateTimeFormatter = dateTimeFormatter,
        dateTimePattern = dateTimePattern,
        nullStrings = nullStrings,
        useFastDoubleParser = false,
    )

    /** For binary compatibility. */
    @Deprecated(
        message = PARSER_OPTIONS_COPY,
        level = DeprecationLevel.HIDDEN,
    )
    public fun copy(
        locale: Locale? = this.locale,
        dateTimeFormatter: DateTimeFormatter? = this.dateTimeFormatter,
        dateTimePattern: String? = this.dateTimePattern,
        nullStrings: Set<String>? = this.nullStrings,
    ): ParserOptions =
        ParserOptions(
            locale = locale,
            dateTimeFormatter = dateTimeFormatter,
            dateTimePattern = dateTimePattern,
            nullStrings = nullStrings,
            useFastDoubleParser = useFastDoubleParser,
        )

    internal fun getDateTimeFormatter(): DateTimeFormatter? =
        when {
            dateTimeFormatter != null -> dateTimeFormatter
            dateTimePattern != null && locale != null -> DateTimeFormatter.ofPattern(dateTimePattern, locale)
            dateTimePattern != null -> DateTimeFormatter.ofPattern(dateTimePattern)
            else -> null
        }
}

/** @include [tryParseImpl] */
public fun DataColumn<String?>.tryParse(options: ParserOptions? = null): DataColumn<*> = tryParseImpl(options)

public fun <T> DataFrame<T>.parse(options: ParserOptions? = null): DataFrame<T> =
    parse(options) {
        colsAtAnyDepth { !it.isColumnGroup() }
    }

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
    tryParse(options).also { if (it.typeClass == String::class) error("Can't guess column type") }

@JvmName("parseAnyFrameNullable")
public fun DataColumn<AnyFrame?>.parse(options: ParserOptions? = null): DataColumn<AnyFrame?> =
    map { it?.parse(options) }
