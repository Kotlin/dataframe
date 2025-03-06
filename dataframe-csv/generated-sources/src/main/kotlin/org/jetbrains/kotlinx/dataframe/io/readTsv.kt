@file:JvmName("ReadTsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADJUST_CSV_SPECS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ALLOW_MISSING_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COL_TYPES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COMPRESSION
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.FIXED_COLUMN_WIDTHS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HAS_FIXED_WIDTH_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HEADER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.IGNORE_EMPTY_LINES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.IGNORE_EXCESS_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.IGNORE_SURROUNDING_SPACES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.PARSER_OPTIONS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.PARSE_PARALLEL
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.READ_LINES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.SKIP_LINES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.TRIM_INSIDE_QUOTED
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.TSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.impl.io.readDelimImpl
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.inputStream

/**
 * ### Read TSV File to [DataFrame]
 *
 * Reads any TSV file to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * The integration is built upon [Deephaven CSV](https://github.com/deephaven/deephaven-csv).
 *
 * ##### Similar Functions
 * With the overloads of [DataFrame.readTsv][readTsv]`()`, you can read any TSV by [File][File],
 * [Path][java.nio.file.Path], [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readTsv][readTsv]`("input.tsv")` or with some options:
 *
 * [DataFrame.readTsv][readTsv]`(`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`file = `[File][File]`("input.tsv"),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`parserOptions = `[ParserOptions][org.jetbrains.kotlinx.dataframe.api.ParserOptions]`(locale = `[Locale][java.util.Locale]`.`[US][java.util.Locale.US]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`colTypes = `[mapOf][mapOf]`("a" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`, `[ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.`[String][ColType.String]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`readLines = 1000L,`
 *
 * `)`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" TSV data from a [String] like this:
 *
 * [DataFrame.readTsvStr][readTsvStr]`("a,b,c", delimiter = ",")`
 *
 * @param path The file path to read.
 *   Can also be compressed as `.gz` or `.zip`, see [Compression][org.jetbrains.kotlinx.dataframe.io.Compression].
 * @param delimiter The field delimiter character. Default: '\t'.
 *
 *   Ignored if [hasFixedWidthColumns] is `true`.
 * @param compression The compression of the data.
 *   Default: [Compression.None], unless detected otherwise from the input file or url.
 * @param header Optional column titles. Default: empty list.
 *
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 * @param hasFixedWidthColumns Whether the data has fixed-width columns instead of a single delimiter.
 *   Default: `false`.
 *
 *   Fixed-width columns can occur, for instance, in multi-space delimited data, where the columns are separated
 *   by multiple spaces instead of a single delimiter, so columns are visually aligned.
 *   Columns widths are determined by the header in the data (if present), or manually by setting
 *   [fixedColumnWidths].
 * @param fixedColumnWidths The fixed column widths. Default: empty list.
 *
 *   Requires [hasFixedWidthColumns]. If empty, the column widths will be determined by the header in the data
 *   (if present), else, this manually sets the column widths.
 *   The number of widths should match the number of columns.
 * @param colTypes The expected [ColType] per column name. Default: empty map, a.k.a. infer every column type.
 *
 *   If supplied for a certain column name (inferred from data or given by [header]),
 *   the parser will parse the column with the specified name as the specified type, else it will infer the type.
 *
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 * @param skipLines The number of lines to skip before reading the header and data. Default: `0`.
 *
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 * @param readLines The maximum number of lines to read from the data. Default: `null`.
 *
 *   If `null`, all lines will be read.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Default, `null`.
 *
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *
 *   If [parserOptions] or any of the arguments are `null`, the global parser configuration
 *   ([DataFrame.parser][DataFrame.Companion.parser]) will be queried.
 *
 *   The only exceptions are:
 *   - [nullStrings][ParserOptions.nullStrings], which, if `null`,
 *   will take the global setting + [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_DELIM_NULL_STRINGS].
 *   - [skipTypes][ParserOptions.skipTypes], which will always add [typesDeephavenAlreadyParses][org.jetbrains.kotlinx.dataframe.impl.io.typesDeephavenAlreadyParses] to
 *   the given types or the global setting.
 * @param ignoreEmptyLines Whether to skip intermediate empty lines. Default: `false`.
 *
 *   If `false`, empty lines will be interpreted as having _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns Wether to allow rows with fewer columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too short will be interpreted as _empty_ values.
 * @param ignoreExcessColumns Whether to ignore rows with more columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too long will have those columns dropped.
 * @param quote The quote character. Default: `"`.
 *
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 *   For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * @param ignoreSurroundingSpaces Whether to ignore leading and trailing blanks around non-quoted fields.
 *   Default: `true`.
 * @param trimInsideQuoted Whether to ignore leading and trailing blanks inside quoted fields.
 *   Default: `false`.
 * @param parseParallel Whether to parse the data in parallel. Default: `true`.
 *
 *   If `true`, the data will be read and parsed in parallel by the Deephaven parser.
 *   This is usually faster, but can be turned off for debugging.
 */
public fun DataFrame.Companion.readTsv(
    path: Path,
    delimiter: Char = TSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    compression: Compression<*> = Compression.of(path),
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
): DataFrame<*> =
    path.inputStream().use {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            hasFixedWidthColumns = hasFixedWidthColumns,
            fixedColumnWidths = fixedColumnWidths,
            compression = compression,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            parserOptions = parserOptions,
            ignoreEmptyLines = ignoreEmptyLines,
            allowMissingColumns = allowMissingColumns,
            ignoreExcessColumns = ignoreExcessColumns,
            quote = quote,
            ignoreSurroundingSpaces = ignoreSurroundingSpaces,
            trimInsideQuoted = trimInsideQuoted,
            parseParallel = parseParallel,
            adjustCsvSpecs = ADJUST_CSV_SPECS,
        )
    }

/**
 * ### Read TSV File to [DataFrame]
 *
 * Reads any TSV file to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * The integration is built upon [Deephaven CSV](https://github.com/deephaven/deephaven-csv).
 *
 * ##### Similar Functions
 * With the overloads of [DataFrame.readTsv][readTsv]`()`, you can read any TSV by [File][File],
 * [Path][java.nio.file.Path], [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readTsv][readTsv]`("input.tsv")` or with some options:
 *
 * [DataFrame.readTsv][readTsv]`(`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`file = `[File][File]`("input.tsv"),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`parserOptions = `[ParserOptions][org.jetbrains.kotlinx.dataframe.api.ParserOptions]`(locale = `[Locale][java.util.Locale]`.`[US][java.util.Locale.US]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`colTypes = `[mapOf][mapOf]`("a" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`, `[ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.`[String][ColType.String]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`readLines = 1000L,`
 *
 * `)`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" TSV data from a [String] like this:
 *
 * [DataFrame.readTsvStr][readTsvStr]`("a,b,c", delimiter = ",")`
 *
 * @param file The file to read.
 *   Can also be compressed as `.gz` or `.zip`, see [Compression][org.jetbrains.kotlinx.dataframe.io.Compression].
 * @param delimiter The field delimiter character. Default: '\t'.
 *
 *   Ignored if [hasFixedWidthColumns] is `true`.
 * @param compression The compression of the data.
 *   Default: [Compression.None], unless detected otherwise from the input file or url.
 * @param header Optional column titles. Default: empty list.
 *
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 * @param hasFixedWidthColumns Whether the data has fixed-width columns instead of a single delimiter.
 *   Default: `false`.
 *
 *   Fixed-width columns can occur, for instance, in multi-space delimited data, where the columns are separated
 *   by multiple spaces instead of a single delimiter, so columns are visually aligned.
 *   Columns widths are determined by the header in the data (if present), or manually by setting
 *   [fixedColumnWidths].
 * @param fixedColumnWidths The fixed column widths. Default: empty list.
 *
 *   Requires [hasFixedWidthColumns]. If empty, the column widths will be determined by the header in the data
 *   (if present), else, this manually sets the column widths.
 *   The number of widths should match the number of columns.
 * @param colTypes The expected [ColType] per column name. Default: empty map, a.k.a. infer every column type.
 *
 *   If supplied for a certain column name (inferred from data or given by [header]),
 *   the parser will parse the column with the specified name as the specified type, else it will infer the type.
 *
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 * @param skipLines The number of lines to skip before reading the header and data. Default: `0`.
 *
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 * @param readLines The maximum number of lines to read from the data. Default: `null`.
 *
 *   If `null`, all lines will be read.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Default, `null`.
 *
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *
 *   If [parserOptions] or any of the arguments are `null`, the global parser configuration
 *   ([DataFrame.parser][DataFrame.Companion.parser]) will be queried.
 *
 *   The only exceptions are:
 *   - [nullStrings][ParserOptions.nullStrings], which, if `null`,
 *   will take the global setting + [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_DELIM_NULL_STRINGS].
 *   - [skipTypes][ParserOptions.skipTypes], which will always add [typesDeephavenAlreadyParses][org.jetbrains.kotlinx.dataframe.impl.io.typesDeephavenAlreadyParses] to
 *   the given types or the global setting.
 * @param ignoreEmptyLines Whether to skip intermediate empty lines. Default: `false`.
 *
 *   If `false`, empty lines will be interpreted as having _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns Wether to allow rows with fewer columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too short will be interpreted as _empty_ values.
 * @param ignoreExcessColumns Whether to ignore rows with more columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too long will have those columns dropped.
 * @param quote The quote character. Default: `"`.
 *
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 *   For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * @param ignoreSurroundingSpaces Whether to ignore leading and trailing blanks around non-quoted fields.
 *   Default: `true`.
 * @param trimInsideQuoted Whether to ignore leading and trailing blanks inside quoted fields.
 *   Default: `false`.
 * @param parseParallel Whether to parse the data in parallel. Default: `true`.
 *
 *   If `true`, the data will be read and parsed in parallel by the Deephaven parser.
 *   This is usually faster, but can be turned off for debugging.
 */
public fun DataFrame.Companion.readTsv(
    file: File,
    delimiter: Char = TSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    compression: Compression<*> = Compression.of(file),
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
): DataFrame<*> =
    FileInputStream(file).use {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            hasFixedWidthColumns = hasFixedWidthColumns,
            fixedColumnWidths = fixedColumnWidths,
            compression = compression,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            parserOptions = parserOptions,
            ignoreEmptyLines = ignoreEmptyLines,
            allowMissingColumns = allowMissingColumns,
            ignoreExcessColumns = ignoreExcessColumns,
            quote = quote,
            ignoreSurroundingSpaces = ignoreSurroundingSpaces,
            trimInsideQuoted = trimInsideQuoted,
            parseParallel = parseParallel,
            adjustCsvSpecs = ADJUST_CSV_SPECS,
        )
    }

/**
 * ### Read TSV Url to [DataFrame]
 *
 * Reads any TSV url to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * The integration is built upon [Deephaven CSV](https://github.com/deephaven/deephaven-csv).
 *
 * ##### Similar Functions
 * With the overloads of [DataFrame.readTsv][readTsv]`()`, you can read any TSV by [File][File],
 * [Path][java.nio.file.Path], [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readTsv][readTsv]`("input.tsv")` or with some options:
 *
 * [DataFrame.readTsv][readTsv]`(`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`file = `[File][File]`("input.tsv"),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`parserOptions = `[ParserOptions][org.jetbrains.kotlinx.dataframe.api.ParserOptions]`(locale = `[Locale][java.util.Locale]`.`[US][java.util.Locale.US]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`colTypes = `[mapOf][mapOf]`("a" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`, `[ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.`[String][ColType.String]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`readLines = 1000L,`
 *
 * `)`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" TSV data from a [String] like this:
 *
 * [DataFrame.readTsvStr][readTsvStr]`("a,b,c", delimiter = ",")`
 *
 * @param url The URL from which to fetch the data.
 *   Can also be compressed as `.gz` or `.zip`, see [Compression][org.jetbrains.kotlinx.dataframe.io.Compression].
 * @param delimiter The field delimiter character. Default: '\t'.
 *
 *   Ignored if [hasFixedWidthColumns] is `true`.
 * @param compression The compression of the data.
 *   Default: [Compression.None], unless detected otherwise from the input file or url.
 * @param header Optional column titles. Default: empty list.
 *
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 * @param hasFixedWidthColumns Whether the data has fixed-width columns instead of a single delimiter.
 *   Default: `false`.
 *
 *   Fixed-width columns can occur, for instance, in multi-space delimited data, where the columns are separated
 *   by multiple spaces instead of a single delimiter, so columns are visually aligned.
 *   Columns widths are determined by the header in the data (if present), or manually by setting
 *   [fixedColumnWidths].
 * @param fixedColumnWidths The fixed column widths. Default: empty list.
 *
 *   Requires [hasFixedWidthColumns]. If empty, the column widths will be determined by the header in the data
 *   (if present), else, this manually sets the column widths.
 *   The number of widths should match the number of columns.
 * @param colTypes The expected [ColType] per column name. Default: empty map, a.k.a. infer every column type.
 *
 *   If supplied for a certain column name (inferred from data or given by [header]),
 *   the parser will parse the column with the specified name as the specified type, else it will infer the type.
 *
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 * @param skipLines The number of lines to skip before reading the header and data. Default: `0`.
 *
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 * @param readLines The maximum number of lines to read from the data. Default: `null`.
 *
 *   If `null`, all lines will be read.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Default, `null`.
 *
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *
 *   If [parserOptions] or any of the arguments are `null`, the global parser configuration
 *   ([DataFrame.parser][DataFrame.Companion.parser]) will be queried.
 *
 *   The only exceptions are:
 *   - [nullStrings][ParserOptions.nullStrings], which, if `null`,
 *   will take the global setting + [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_DELIM_NULL_STRINGS].
 *   - [skipTypes][ParserOptions.skipTypes], which will always add [typesDeephavenAlreadyParses][org.jetbrains.kotlinx.dataframe.impl.io.typesDeephavenAlreadyParses] to
 *   the given types or the global setting.
 * @param ignoreEmptyLines Whether to skip intermediate empty lines. Default: `false`.
 *
 *   If `false`, empty lines will be interpreted as having _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns Wether to allow rows with fewer columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too short will be interpreted as _empty_ values.
 * @param ignoreExcessColumns Whether to ignore rows with more columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too long will have those columns dropped.
 * @param quote The quote character. Default: `"`.
 *
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 *   For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * @param ignoreSurroundingSpaces Whether to ignore leading and trailing blanks around non-quoted fields.
 *   Default: `true`.
 * @param trimInsideQuoted Whether to ignore leading and trailing blanks inside quoted fields.
 *   Default: `false`.
 * @param parseParallel Whether to parse the data in parallel. Default: `true`.
 *
 *   If `true`, the data will be read and parsed in parallel by the Deephaven parser.
 *   This is usually faster, but can be turned off for debugging.
 */
public fun DataFrame.Companion.readTsv(
    url: URL,
    delimiter: Char = TSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    compression: Compression<*> = Compression.of(url),
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
): DataFrame<*> =
    catchHttpResponse(url) {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            hasFixedWidthColumns = hasFixedWidthColumns,
            fixedColumnWidths = fixedColumnWidths,
            compression = compression,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            parserOptions = parserOptions,
            ignoreEmptyLines = ignoreEmptyLines,
            allowMissingColumns = allowMissingColumns,
            ignoreExcessColumns = ignoreExcessColumns,
            quote = quote,
            ignoreSurroundingSpaces = ignoreSurroundingSpaces,
            trimInsideQuoted = trimInsideQuoted,
            parseParallel = parseParallel,
            adjustCsvSpecs = ADJUST_CSV_SPECS,
        )
    }

/**
 * ### Read TSV File or URL to [DataFrame]
 *
 * Reads any TSV file or url to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * The integration is built upon [Deephaven CSV](https://github.com/deephaven/deephaven-csv).
 *
 * ##### Similar Functions
 * With the overloads of [DataFrame.readTsv][readTsv]`()`, you can read any TSV by [File][File],
 * [Path][java.nio.file.Path], [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readTsv][readTsv]`("input.tsv")` or with some options:
 *
 * [DataFrame.readTsv][readTsv]`(`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`file = `[File][File]`("input.tsv"),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`parserOptions = `[ParserOptions][org.jetbrains.kotlinx.dataframe.api.ParserOptions]`(locale = `[Locale][java.util.Locale]`.`[US][java.util.Locale.US]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`colTypes = `[mapOf][mapOf]`("a" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`, `[ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.`[String][ColType.String]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`readLines = 1000L,`
 *
 * `)`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" TSV data from a [String] like this:
 *
 * [DataFrame.readTsvStr][readTsvStr]`("a,b,c", delimiter = ",")`
 *
 * @param fileOrUrl The file path or URL to read the data from.
 *   Can also be compressed as `.gz` or `.zip`, see [Compression][org.jetbrains.kotlinx.dataframe.io.Compression].
 * @param delimiter The field delimiter character. Default: '\t'.
 *
 *   Ignored if [hasFixedWidthColumns] is `true`.
 * @param compression The compression of the data.
 *   Default: [Compression.None], unless detected otherwise from the input file or url.
 * @param header Optional column titles. Default: empty list.
 *
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 * @param hasFixedWidthColumns Whether the data has fixed-width columns instead of a single delimiter.
 *   Default: `false`.
 *
 *   Fixed-width columns can occur, for instance, in multi-space delimited data, where the columns are separated
 *   by multiple spaces instead of a single delimiter, so columns are visually aligned.
 *   Columns widths are determined by the header in the data (if present), or manually by setting
 *   [fixedColumnWidths].
 * @param fixedColumnWidths The fixed column widths. Default: empty list.
 *
 *   Requires [hasFixedWidthColumns]. If empty, the column widths will be determined by the header in the data
 *   (if present), else, this manually sets the column widths.
 *   The number of widths should match the number of columns.
 * @param colTypes The expected [ColType] per column name. Default: empty map, a.k.a. infer every column type.
 *
 *   If supplied for a certain column name (inferred from data or given by [header]),
 *   the parser will parse the column with the specified name as the specified type, else it will infer the type.
 *
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 * @param skipLines The number of lines to skip before reading the header and data. Default: `0`.
 *
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 * @param readLines The maximum number of lines to read from the data. Default: `null`.
 *
 *   If `null`, all lines will be read.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Default, `null`.
 *
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *
 *   If [parserOptions] or any of the arguments are `null`, the global parser configuration
 *   ([DataFrame.parser][DataFrame.Companion.parser]) will be queried.
 *
 *   The only exceptions are:
 *   - [nullStrings][ParserOptions.nullStrings], which, if `null`,
 *   will take the global setting + [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_DELIM_NULL_STRINGS].
 *   - [skipTypes][ParserOptions.skipTypes], which will always add [typesDeephavenAlreadyParses][org.jetbrains.kotlinx.dataframe.impl.io.typesDeephavenAlreadyParses] to
 *   the given types or the global setting.
 * @param ignoreEmptyLines Whether to skip intermediate empty lines. Default: `false`.
 *
 *   If `false`, empty lines will be interpreted as having _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns Wether to allow rows with fewer columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too short will be interpreted as _empty_ values.
 * @param ignoreExcessColumns Whether to ignore rows with more columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too long will have those columns dropped.
 * @param quote The quote character. Default: `"`.
 *
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 *   For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * @param ignoreSurroundingSpaces Whether to ignore leading and trailing blanks around non-quoted fields.
 *   Default: `true`.
 * @param trimInsideQuoted Whether to ignore leading and trailing blanks inside quoted fields.
 *   Default: `false`.
 * @param parseParallel Whether to parse the data in parallel. Default: `true`.
 *
 *   If `true`, the data will be read and parsed in parallel by the Deephaven parser.
 *   This is usually faster, but can be turned off for debugging.
 */
public fun DataFrame.Companion.readTsv(
    fileOrUrl: String,
    delimiter: Char = TSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    compression: Compression<*> = Compression.of(fileOrUrl),
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
): DataFrame<*> =
    catchHttpResponse(asUrl(fileOrUrl = fileOrUrl)) {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            hasFixedWidthColumns = hasFixedWidthColumns,
            fixedColumnWidths = fixedColumnWidths,
            compression = compression,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            parserOptions = parserOptions,
            ignoreEmptyLines = ignoreEmptyLines,
            allowMissingColumns = allowMissingColumns,
            ignoreExcessColumns = ignoreExcessColumns,
            quote = quote,
            ignoreSurroundingSpaces = ignoreSurroundingSpaces,
            trimInsideQuoted = trimInsideQuoted,
            parseParallel = parseParallel,
            adjustCsvSpecs = ADJUST_CSV_SPECS,
        )
    }

/**
 *
 * ### Read TSV InputStream to [DataFrame]
 *
 * Reads any TSV input stream to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * The integration is built upon [Deephaven CSV](https://github.com/deephaven/deephaven-csv).
 *
 * ##### Similar Functions
 * With the overloads of [DataFrame.readTsv][readTsv]`()`, you can read any TSV by [File][File],
 * [Path][java.nio.file.Path], [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readTsv][readTsv]`("input.tsv")` or with some options:
 *
 * [DataFrame.readTsv][readTsv]`(`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`file = `[File][File]`("input.tsv"),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`parserOptions = `[ParserOptions][org.jetbrains.kotlinx.dataframe.api.ParserOptions]`(locale = `[Locale][java.util.Locale]`.`[US][java.util.Locale.US]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`colTypes = `[mapOf][mapOf]`("a" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`, `[ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.`[String][ColType.String]`),`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`readLines = 1000L,`
 *
 * `)`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" TSV data from a [String] like this:
 *
 * [DataFrame.readTsvStr][readTsvStr]`("a,b,c", delimiter = ",")`
 *
 * @param inputStream Represents the file to read.
 * @param delimiter The field delimiter character. Default: '\t'.
 *
 *   Ignored if [hasFixedWidthColumns] is `true`.
 * @param compression The compression of the data.
 *   Default: [Compression.None], unless detected otherwise from the input file or url.
 * @param header Optional column titles. Default: empty list.
 *
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 * @param hasFixedWidthColumns Whether the data has fixed-width columns instead of a single delimiter.
 *   Default: `false`.
 *
 *   Fixed-width columns can occur, for instance, in multi-space delimited data, where the columns are separated
 *   by multiple spaces instead of a single delimiter, so columns are visually aligned.
 *   Columns widths are determined by the header in the data (if present), or manually by setting
 *   [fixedColumnWidths].
 * @param fixedColumnWidths The fixed column widths. Default: empty list.
 *
 *   Requires [hasFixedWidthColumns]. If empty, the column widths will be determined by the header in the data
 *   (if present), else, this manually sets the column widths.
 *   The number of widths should match the number of columns.
 * @param colTypes The expected [ColType] per column name. Default: empty map, a.k.a. infer every column type.
 *
 *   If supplied for a certain column name (inferred from data or given by [header]),
 *   the parser will parse the column with the specified name as the specified type, else it will infer the type.
 *
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 * @param skipLines The number of lines to skip before reading the header and data. Default: `0`.
 *
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 * @param readLines The maximum number of lines to read from the data. Default: `null`.
 *
 *   If `null`, all lines will be read.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Default, `null`.
 *
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *
 *   If [parserOptions] or any of the arguments are `null`, the global parser configuration
 *   ([DataFrame.parser][DataFrame.Companion.parser]) will be queried.
 *
 *   The only exceptions are:
 *   - [nullStrings][ParserOptions.nullStrings], which, if `null`,
 *   will take the global setting + [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_DELIM_NULL_STRINGS].
 *   - [skipTypes][ParserOptions.skipTypes], which will always add [typesDeephavenAlreadyParses][org.jetbrains.kotlinx.dataframe.impl.io.typesDeephavenAlreadyParses] to
 *   the given types or the global setting.
 * @param ignoreEmptyLines Whether to skip intermediate empty lines. Default: `false`.
 *
 *   If `false`, empty lines will be interpreted as having _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns Wether to allow rows with fewer columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too short will be interpreted as _empty_ values.
 * @param ignoreExcessColumns Whether to ignore rows with more columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too long will have those columns dropped.
 * @param quote The quote character. Default: `"`.
 *
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 *   For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * @param ignoreSurroundingSpaces Whether to ignore leading and trailing blanks around non-quoted fields.
 *   Default: `true`.
 * @param trimInsideQuoted Whether to ignore leading and trailing blanks inside quoted fields.
 *   Default: `false`.
 * @param parseParallel Whether to parse the data in parallel. Default: `true`.
 *
 *   If `true`, the data will be read and parsed in parallel by the Deephaven parser.
 *   This is usually faster, but can be turned off for debugging.
 * @param adjustCsvSpecs Optional extra [CsvSpecs] configuration. Default: `{ it }`.
 *
 *   Before instantiating the [CsvSpecs], the [CsvSpecs.Builder] will be passed to this lambda.
 *   This will allow you to configure/overwrite any CSV / TSV parsing options.
 */
public fun DataFrame.Companion.readTsv(
    inputStream: InputStream,
    delimiter: Char = TSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
    compression: Compression<*> = COMPRESSION,
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions? = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
    adjustCsvSpecs: AdjustCsvSpecs = ADJUST_CSV_SPECS,
): DataFrame<*> =
    readDelimImpl(
        inputStream = inputStream,
        delimiter = delimiter,
        header = header,
        hasFixedWidthColumns = hasFixedWidthColumns,
        fixedColumnWidths = fixedColumnWidths,
        compression = compression,
        colTypes = colTypes,
        skipLines = skipLines,
        readLines = readLines,
        parserOptions = parserOptions,
        ignoreEmptyLines = ignoreEmptyLines,
        allowMissingColumns = allowMissingColumns,
        ignoreExcessColumns = ignoreExcessColumns,
        quote = quote,
        ignoreSurroundingSpaces = ignoreSurroundingSpaces,
        trimInsideQuoted = trimInsideQuoted,
        parseParallel = parseParallel,
        adjustCsvSpecs = adjustCsvSpecs,
    )
