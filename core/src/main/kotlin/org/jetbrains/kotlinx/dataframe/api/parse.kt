package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.parseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.tryParseImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region KDoc-helpers

/**
 * Returns a [DataFrame] in which `String` and `Char` columns are parsed into other types.
 *
 * This is a special case of the [convert] operation: instead of a target type,
 * every selected column is tried against the supported parsers, in the parsing order
 * documented on the website page, until one of them parses all of its values.
 * Columns that cannot be parsed into a more specific type are left unchanged,
 * and so are the columns of other types.
 *
 * Parsing can be customized with [ParserOptions]: for every option you don't supply (or supply `null`),
 * its value is taken from [DataFrame.parser][DataFrame.Companion.parser].
 *
 * See also [convert], the general operation, which requires the target types to be specified explicitly.
 *
 * For more information: {@include [DocumentationUrls.Parse]} {@include [DocumentationUrls.ParserOptions]} {@include [DocumentationUrls.ParsingOrder]}
 */
@ExcludeFromSources
internal interface ParseDocs

/** {@set [SelectingColumns.OPERATION] [parse][parse]} */
@ExcludeFromSources
private typealias SetParseOperationArg = Nothing

/**
 * @include [ParseDocs]
 * ### This Parse Overload
 */
@ExcludeFromSources
private typealias CommonParseDocs = Nothing

// endregion

/**
 * @include [CommonParseDocs]
 *
 * When no columns are specified, all `String` and `Char` columns are parsed,
 * even those inside [column groups][ColumnGroup] and inside [frame columns][FrameColumn].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.Parse.parseAll]
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter.
 * @return a new [DataFrame] with the parsed columns.
 */
@Refine
@Interpretable("ParseDefault")
public fun <T> DataFrame<T>.parse(options: ParserOptions? = null): DataFrame<T> =
    parse(options) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }

/**
 * @include [CommonParseDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl] {@include [SetParseOperationArg]}
 *
 * To parse only particular columns, use a [Columns Selector][ColumnsSelector]; the columns outside
 * the selection are left unchanged — except that selecting a [column group][ColumnGroup] parses
 * the columns inside it, and selecting a [frame column][FrameColumn] parses all `String` and `Char`
 * columns inside its frames.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.Parse.parseSome]
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter.
 * @param [columns\] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to parse.
 * @return a new [DataFrame] with the parsed columns.
 */
@Refine
@Interpretable("Parse")
public fun <T> DataFrame<T>.parse(options: ParserOptions? = null, columns: ColumnsSelector<T, Any?>): DataFrame<T> =
    parseImpl(options, columns)

/**
 * @include [CommonParseDocs]
 * @include [SelectingColumns.ColumnNamesApi] {@include [SetParseOperationArg]}
 *
 * To parse only particular columns, pass their names; the columns outside the selection are left
 * unchanged — except that naming a [column group][ColumnGroup] parses the columns inside it, and naming
 * a [frame column][FrameColumn] parses all `String` and `Char` columns inside its frames.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.Parse.parseSomeByName]
 *
 * @param [columns\] The [Column Names][String] used to select the columns of this [DataFrame] to parse.
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter.
 * @return a new [DataFrame] with the parsed columns.
 */
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
 * For more information: {@include [DocumentationUrls.ParseOnDataColumn]}
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter
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
 * For more information: {@include [DocumentationUrls.ParseOnDataColumn]}
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter
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
 * For more information: {@include [DocumentationUrls.ParseOnDataColumn]}
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter
 * @return a new column with parsed values
 */
@JvmName("parseChar")
public fun DataColumn<Char?>.parse(options: ParserOptions? = null): DataColumn<*> =
    map { it?.toString() }
        .tryParse(options)
        .also { if (it.isSubtypeOf<Char?>() || it.isSubtypeOf<String?>()) error("Can't guess column type") }

/**
 * Returns a new column in which every [DataFrame] cell is parsed recursively:
 * all `String` and `Char` columns inside each frame are parsed into other types,
 * as if [parse] was called on that frame with no columns selected.
 * `null` cells stay `null`.
 *
 * For more information: {@include [DocumentationUrls.Parse]} {@include [DocumentationUrls.ParseOnDataColumn]}
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter.
 * @return a new column with the parsed frames.
 */
@JvmName("parseAnyFrameNullable")
public fun DataColumn<DataFrame<*>?>.parse(options: ParserOptions? = null): DataColumn<DataFrame<*>?> =
    when {
        // a column without null cells (always true for a [FrameColumn]) maps with a non-null lambda,
        // so `map` rebuilds a FrameColumn; a nullable lambda would create a ValueColumn of frames,
        // which the column invariant forbids
        values().none { it == null } -> map { (it as DataFrame<*>).parse(options) }

        else -> map { it?.parse(options) }
    }
