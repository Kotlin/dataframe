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
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.parseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.tryParseImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region KDoc-helpers

// endregion

/**
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] in which `String` and `Char` columns are parsed into other types.
 *
 * This is a special case of the [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] operation: instead of a target type,
 * every selected column is tried against the supported parsers, in the parsing order
 * documented on the website page, until one of them parses all of its values.
 * Columns that cannot be parsed into a more specific type are left unchanged,
 * and so are the columns of other types.
 *
 * Parsing can be customized with [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions]: for every option you don't supply (or supply `null`),
 * its value is taken from [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser].
 *
 * See also [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert], the general operation, which requires the target types to be specified explicitly.
 *
 * For more information: [See `parse` on the documentation website.](https://kotlin.github.io/dataframe/parse.html) [See Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parser-options) [See Parsing Order on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-order)
 * ### This Parse Overload
 *
 * When no columns are specified, all `String` and `Char` columns are parsed,
 * even those inside [<code>column groups</code>][ColumnGroup] and inside [<code>frame columns</code>][FrameColumn].
 *
 * ### Examples
 *
 * ```kotlin
 * df.parse()
 * ```
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter.
 * @return a new [<code>DataFrame</code>][DataFrame] with the parsed columns.
 */
@Refine
@Interpretable("ParseDefault")
public fun <T> DataFrame<T>.parse(options: ParserOptions? = null): DataFrame<T> =
    parse(options) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }

/**
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] in which `String` and `Char` columns are parsed into other types.
 *
 * This is a special case of the [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] operation: instead of a target type,
 * every selected column is tried against the supported parsers, in the parsing order
 * documented on the website page, until one of them parses all of its values.
 * Columns that cannot be parsed into a more specific type are left unchanged,
 * and so are the columns of other types.
 *
 * Parsing can be customized with [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions]: for every option you don't supply (or supply `null`),
 * its value is taken from [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser].
 *
 * See also [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert], the general operation, which requires the target types to be specified explicitly.
 *
 * For more information: [See `parse` on the documentation website.](https://kotlin.github.io/dataframe/parse.html) [See Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parser-options) [See Parsing Order on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-order)
 * ### This Parse Overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * To parse only particular columns, use a [<code>Columns Selector</code>][ColumnsSelector]; the columns outside
 * the selection are left unchanged — except that selecting a [<code>column group</code>][ColumnGroup] parses
 * the columns inside it, and selecting a [<code>frame column</code>][FrameColumn] parses all `String` and `Char`
 * columns inside its frames.
 *
 * ### Examples
 *
 * ```kotlin
 * df.parse { date and value }
 * ```
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter.
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to parse.
 * @return a new [<code>DataFrame</code>][DataFrame] with the parsed columns.
 */
@Refine
@Interpretable("Parse")
public fun <T> DataFrame<T>.parse(options: ParserOptions? = null, columns: ColumnsSelector<T, Any?>): DataFrame<T> =
    parseImpl(options, columns)

/**
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] in which `String` and `Char` columns are parsed into other types.
 *
 * This is a special case of the [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] operation: instead of a target type,
 * every selected column is tried against the supported parsers, in the parsing order
 * documented on the website page, until one of them parses all of its values.
 * Columns that cannot be parsed into a more specific type are left unchanged,
 * and so are the columns of other types.
 *
 * Parsing can be customized with [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions]: for every option you don't supply (or supply `null`),
 * its value is taken from [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser].
 *
 * See also [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert], the general operation, which requires the target types to be specified explicitly.
 *
 * For more information: [See `parse` on the documentation website.](https://kotlin.github.io/dataframe/parse.html) [See Parser Options on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parser-options) [See Parsing Order on the documentation website.](https://kotlin.github.io/dataframe/parse.html#parsing-order)
 * ### This Parse Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * To parse only particular columns, pass their names; the columns outside the selection are left
 * unchanged — except that naming a [<code>column group</code>][ColumnGroup] parses the columns inside it, and naming
 * a [<code>frame column</code>][FrameColumn] parses all `String` and `Char` columns inside its frames.
 *
 * ### Examples
 *
 * ```kotlin
 * df.parse("date", "value")
 * ```
 *
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to parse.
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter.
 * @return a new [<code>DataFrame</code>][DataFrame] with the parsed columns.
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

/** Tries to parse a column of strings into a column of a different type.
 * Each parser in [<code>Parsers</code>][org.jetbrains.kotlinx.dataframe.impl.api.Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried. If all the others fail, the final parser
 * simply returns the original string, leaving the column unchanged.
 *
 * For more information: [See `parse` on a DataColumn on the documentation website.](https://kotlin.github.io/dataframe/parse.html#on-a-datacolumn)
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter
 * @throws IllegalStateException if no valid parser is found (unlikely, unless the `String` parser is disabled)
 * @return a new column with parsed values */
public fun DataColumn<String?>.tryParse(options: ParserOptions? = null): DataColumn<*> = tryParseImpl(options)

/**
 * Tries to parse a column of chars into a column of a different type.
 * Each parser in [<code>Parsers</code>][Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried. If all the others fail, the final parser
 * returns strings.
 *
 * For more information: [See `parse` on a DataColumn on the documentation website.](https://kotlin.github.io/dataframe/parse.html#on-a-datacolumn)
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
 * Each parser in [<code>Parsers</code>][Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried.
 *
 * If all fail [<code>IllegalStateException</code>][IllegalStateException] is thrown. If you don't want this exception to be thrown,
 * use [<code>tryParse</code>][tryParse] instead.
 *
 * For more information: [See `parse` on a DataColumn on the documentation website.](https://kotlin.github.io/dataframe/parse.html#on-a-datacolumn)
 *
 * @param [options] options for parsing, like providing a locale or a custom date-time formatter
 * @throws IllegalStateException if no valid parser is found
 * @return a new column with parsed values
 */
public fun DataColumn<String?>.parse(options: ParserOptions? = null): DataColumn<*> =
    tryParse(options).also { if (it.isSubtypeOf<String?>()) error("Can't guess column type") }

/**
 * Tries to parse a column of chars as strings into a column of a different type.
 * Each parser in [<code>Parsers</code>][Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried.
 *
 * If all fail [<code>IllegalStateException</code>][IllegalStateException] is thrown. If you don't want this exception to be thrown,
 * use [<code>tryParse</code>][tryParse] instead.
 *
 * For more information: [See `parse` on a DataColumn on the documentation website.](https://kotlin.github.io/dataframe/parse.html#on-a-datacolumn)
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
 * Returns a new column in which every [<code>DataFrame</code>][DataFrame] cell is parsed recursively:
 * all `String` and `Char` columns inside each frame are parsed into other types,
 * as if [<code>parse</code>][parse] was called on that frame with no columns selected.
 * `null` cells stay `null`.
 *
 * For more information: [See `parse` on the documentation website.](https://kotlin.github.io/dataframe/parse.html) [See `parse` on a DataColumn on the documentation website.](https://kotlin.github.io/dataframe/parse.html#on-a-datacolumn)
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
