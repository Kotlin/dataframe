package org.jetbrains.kotlinx.dataframe.api

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
import org.jetbrains.kotlinx.dataframe.impl.api.parseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.tryParseImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

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

/** @include [tryParseImpl] */
public fun DataColumn<String?>.tryParse(options: ParserOptions? = null): DataColumn<*> = tryParseImpl(options)

/**
 * Tries to parse a column of chars into a column of a different type.
 * Each parser in [Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried. If all the others fail, the final parser
 * returns strings.
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
