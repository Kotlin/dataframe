package org.jetbrains.kotlinx.dataframe.api

import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.CoroutineProvider
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.StringParser
import org.jetbrains.kotlinx.dataframe.impl.api.parseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.tryParseImpl
import org.jetbrains.kotlinx.dataframe.typeClass
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.reflect.KProperty

public val DataFrame.Companion.parser: GlobalParserOptions get() = Parsers

public inline fun <T> DataFrame<T>.parse(
    options: ParserOptions? = null,
    runInCoroutine: CoroutineProvider<DataFrame<T>> = { runBlocking(block = it) },
    noinline columns: ColumnsSelector<T, Any?> = { colsAtAnyDepth { !it.isColumnGroup() } },
): DataFrame<T> = parseImpl(options, runInCoroutine, columns)

public inline fun <T> DataFrame<T>.parse(
    vararg columns: String,
    runInCoroutine: CoroutineProvider<DataFrame<T>> = { runBlocking(block = it) },
    options: ParserOptions? = null,
): DataFrame<T> = parse(options, runInCoroutine) { columns.toColumnSet() }

public inline fun <T, C> DataFrame<T>.parse(
    vararg columns: ColumnReference<C>,
    runInCoroutine: CoroutineProvider<DataFrame<T>> = { runBlocking(block = it) },
    options: ParserOptions? = null,
): DataFrame<T> = parse(options, runInCoroutine) { columns.toColumnSet() }

public inline fun <T, C> DataFrame<T>.parse(
    vararg columns: KProperty<C>,
    runInCoroutine: CoroutineProvider<DataFrame<T>> = { runBlocking(block = it) },
    options: ParserOptions? = null,
): DataFrame<T> = parse(options, runInCoroutine) { columns.toColumnSet() }

public interface GlobalParserOptions {

    public fun addDateTimePattern(pattern: String)

    public fun addNullString(str: String)

    public fun resetToDefault()

    public var locale: Locale
}

public data class ParserOptions(
    val locale: Locale? = null,
    // TODO, migrate to kotlinx.datetime.format.DateTimeFormat? https://github.com/Kotlin/dataframe/issues/876
    val dateTimeFormatter: DateTimeFormatter? = null,
    val dateTimePattern: String? = null,
    val nullStrings: Set<String>? = null,
) {
    internal fun getDateTimeFormatter(): DateTimeFormatter? =
        when {
            dateTimeFormatter != null -> dateTimeFormatter
            dateTimePattern != null && locale != null -> DateTimeFormatter.ofPattern(dateTimePattern, locale)
            dateTimePattern != null -> DateTimeFormatter.ofPattern(dateTimePattern)
            else -> null
        }
}

/** Tries to parse a column of strings into a column of a different type.
 * Each parser in [Parsers][org.jetbrains.kotlinx.dataframe.impl.api.Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried. If all the others fail, the final parser
 * simply returns the original string, leaving the column unchanged.
 *
 * Parsers that are [covered by][org.jetbrains.kotlinx.dataframe.impl.api.StringParser.coveredBy] other parsers are skipped.
 *
 * @param options options for parsing, like providing a locale or a custom date-time formatter
 * @throws IllegalStateException if no valid parser is found (unlikely, unless the `String` parser is disabled)
 * @return a new column with parsed values */
public fun DataColumn<String?>.tryParse(options: ParserOptions? = null): DataColumn<*> = tryParseImpl(options)

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
