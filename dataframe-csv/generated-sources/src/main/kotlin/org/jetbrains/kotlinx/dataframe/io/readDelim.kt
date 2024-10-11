package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams
import org.jetbrains.kotlinx.dataframe.impl.io.asURL
import org.jetbrains.kotlinx.dataframe.impl.io.catchHttpResponse
import org.jetbrains.kotlinx.dataframe.impl.io.compressionStateOf
import org.jetbrains.kotlinx.dataframe.impl.io.readDelimImpl
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL

/*
 * TODO these currently clash with :core's readDelim(Str) functions.
 *   When those are deprecated, we can let the users fall-back to these.
 *   They do the same as readCsv(Str).
 */

/**
 * ### Read Delimiter-Separated Text File to [DataFrame]
 *
 * Reads any delimiter-separated text file to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * ##### Other overloads
 * With the overloads of [DataFrame.readDelim][readDelim]`()`, you can read any delimiter-separated text by [File][File],
 * [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readDelim][readDelim]`("input.txt")`
 *
 * or [DataFrame.readDelim][readDelim]`(`[File][File]`("input.txt"))`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" delimiter-separated text data from a [String] using:
 *
 * [DataFrame.readDelimStr][readDelimStr]`("")`
 *
 * _**NOTE EXPERIMENTAL**: This is a new set of functions, replacing the old [DataFrame.readDelim][org.jetbrains.kotlinx.dataframe.io.readDelim]`()` functions.
 * They'll hopefully be faster and better. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv] to be able to use them._
 *
 * @param file The file to read. Can also be compressed as `.gz` or `.zip`, see [Compression][org.jetbrains.kotlinx.dataframe.io.Compression].
 * @param delimiter The field delimiter character. Default: ','.
 * @param compression The compression of the data.
 *   Default: [Compression.None][org.jetbrains.kotlinx.dataframe.io.Compression.None], unless detected otherwise from the input file or url.
 * @param header Optional column titles.
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 *   Default: empty list.
 * @param colTypes The expected [ColType] per column name.
 *   When given, the parser will read the column as that type, else it will infer the type.
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [DEFAULT_COL_TYPE][org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 *   Default: empty map, a.k.a. infer every column type.
 * @param skipLines The number of lines to skip before reading the header and data.
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 *   Default: `0`.
 * @param readLines The maximum number of lines to read from the data.
 *   If `null`, all lines will be read.
 *   Default: `null`, reads all lines.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *   Default, [defaultDelimParserOptions][org.jetbrains.kotlinx.dataframe.io.defaultDelimParserOptions]:
 *   ```
 *   ParserOptions(nullStrings = ["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"])
 *   ```
 * @param ignoreEmptyLines If `true`, intermediate empty lines will be skipped.
 *   Default: `false`, empty line will be interpreted as _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns If `true`, rows that are too short
 *   (fewer columns than the header) will be interpreted as _empty_ values.
 *   Default: `true`.
 * @param ignoreExcessColumns If `true`, rows that are too long
 *   (more columns than the header) will have those columns dropped.
 *   Default: `true`.
 * @param quote The quote character.
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 * For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * Default: `"`.
 * @param ignoreSurroundingSpaces If `true`, leading and trailing blanks around non-quoted fields will be trimmed.
 *   Default: `true`.
 * @param trimInsideQuoted If `true`, leading and trailing blanks inside quoted fields will be trimmed.
 *   Default: `false`.
 * @param parseParallel If `true`, the data will be parsed in parallel.
 *   Can be turned off for debugging.
 *   Default: `true`.
 */
@ExperimentalCsv
public fun DataFrame.Companion.readDelim(
    file: File,
    delimiter: Char = DelimParams.CSV_DELIMITER,
    header: List<String> = DelimParams.HEADER,
    compression: Compression<*> = compressionStateOf(file),
    colTypes: Map<String, ColType> = DelimParams.COL_TYPES,
    skipLines: Long = DelimParams.SKIP_LINES,
    readLines: Long? = DelimParams.READ_LINES,
    parserOptions: ParserOptions = DelimParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = DelimParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = DelimParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = DelimParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = DelimParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = DelimParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = DelimParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = DelimParams.PARSE_PARALLEL,
): DataFrame<*> =
    FileInputStream(file).use {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
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
        )
    }

/**
 * ### Read Delimiter-Separated Text Url to [DataFrame]
 *
 * Reads any delimiter-separated text url to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * ##### Other overloads
 * With the overloads of [DataFrame.readDelim][readDelim]`()`, you can read any delimiter-separated text by [File][File],
 * [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readDelim][readDelim]`("input.txt")`
 *
 * or [DataFrame.readDelim][readDelim]`(`[File][File]`("input.txt"))`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" delimiter-separated text data from a [String] using:
 *
 * [DataFrame.readDelimStr][readDelimStr]`("")`
 *
 * _**NOTE EXPERIMENTAL**: This is a new set of functions, replacing the old [DataFrame.readDelim][org.jetbrains.kotlinx.dataframe.io.readDelim]`()` functions.
 * They'll hopefully be faster and better. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv] to be able to use them._
 *
 * @param url The URL from which to fetch the data. Can also be compressed as `.gz` or `.zip`, see [Compression][org.jetbrains.kotlinx.dataframe.io.Compression].
 * @param delimiter The field delimiter character. Default: ','.
 * @param compression The compression of the data.
 *   Default: [Compression.None][org.jetbrains.kotlinx.dataframe.io.Compression.None], unless detected otherwise from the input file or url.
 * @param header Optional column titles.
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 *   Default: empty list.
 * @param colTypes The expected [ColType] per column name.
 *   When given, the parser will read the column as that type, else it will infer the type.
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [DEFAULT_COL_TYPE][org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 *   Default: empty map, a.k.a. infer every column type.
 * @param skipLines The number of lines to skip before reading the header and data.
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 *   Default: `0`.
 * @param readLines The maximum number of lines to read from the data.
 *   If `null`, all lines will be read.
 *   Default: `null`, reads all lines.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *   Default, [defaultDelimParserOptions][org.jetbrains.kotlinx.dataframe.io.defaultDelimParserOptions]:
 *   ```
 *   ParserOptions(nullStrings = ["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"])
 *   ```
 * @param ignoreEmptyLines If `true`, intermediate empty lines will be skipped.
 *   Default: `false`, empty line will be interpreted as _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns If `true`, rows that are too short
 *   (fewer columns than the header) will be interpreted as _empty_ values.
 *   Default: `true`.
 * @param ignoreExcessColumns If `true`, rows that are too long
 *   (more columns than the header) will have those columns dropped.
 *   Default: `true`.
 * @param quote The quote character.
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 * For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * Default: `"`.
 * @param ignoreSurroundingSpaces If `true`, leading and trailing blanks around non-quoted fields will be trimmed.
 *   Default: `true`.
 * @param trimInsideQuoted If `true`, leading and trailing blanks inside quoted fields will be trimmed.
 *   Default: `false`.
 * @param parseParallel If `true`, the data will be parsed in parallel.
 *   Can be turned off for debugging.
 *   Default: `true`.
 */
@ExperimentalCsv
public fun DataFrame.Companion.readDelim(
    url: URL,
    delimiter: Char = DelimParams.CSV_DELIMITER,
    header: List<String> = DelimParams.HEADER,
    compression: Compression<*> = compressionStateOf(url),
    colTypes: Map<String, ColType> = DelimParams.COL_TYPES,
    skipLines: Long = DelimParams.SKIP_LINES,
    readLines: Long? = DelimParams.READ_LINES,
    parserOptions: ParserOptions = DelimParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = DelimParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = DelimParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = DelimParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = DelimParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = DelimParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = DelimParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = DelimParams.PARSE_PARALLEL,
): DataFrame<*> =
    catchHttpResponse(url) {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
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
        )
    }

/**
 * ### Read Delimiter-Separated Text File or URL to [DataFrame]
 *
 * Reads any delimiter-separated text file or url to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * ##### Other overloads
 * With the overloads of [DataFrame.readDelim][readDelim]`()`, you can read any delimiter-separated text by [File][File],
 * [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readDelim][readDelim]`("input.txt")`
 *
 * or [DataFrame.readDelim][readDelim]`(`[File][File]`("input.txt"))`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" delimiter-separated text data from a [String] using:
 *
 * [DataFrame.readDelimStr][readDelimStr]`("")`
 *
 * _**NOTE EXPERIMENTAL**: This is a new set of functions, replacing the old [DataFrame.readDelim][org.jetbrains.kotlinx.dataframe.io.readDelim]`()` functions.
 * They'll hopefully be faster and better. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv] to be able to use them._
 *
 * @param fileOrUrl The file or URL to read the data from. Can also be compressed as `.gz` or `.zip`, see [Compression][org.jetbrains.kotlinx.dataframe.io.Compression].
 * @param delimiter The field delimiter character. Default: ','.
 * @param compression The compression of the data.
 *   Default: [Compression.None][org.jetbrains.kotlinx.dataframe.io.Compression.None], unless detected otherwise from the input file or url.
 * @param header Optional column titles.
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 *   Default: empty list.
 * @param colTypes The expected [ColType] per column name.
 *   When given, the parser will read the column as that type, else it will infer the type.
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [DEFAULT_COL_TYPE][org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 *   Default: empty map, a.k.a. infer every column type.
 * @param skipLines The number of lines to skip before reading the header and data.
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 *   Default: `0`.
 * @param readLines The maximum number of lines to read from the data.
 *   If `null`, all lines will be read.
 *   Default: `null`, reads all lines.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *   Default, [defaultDelimParserOptions][org.jetbrains.kotlinx.dataframe.io.defaultDelimParserOptions]:
 *   ```
 *   ParserOptions(nullStrings = ["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"])
 *   ```
 * @param ignoreEmptyLines If `true`, intermediate empty lines will be skipped.
 *   Default: `false`, empty line will be interpreted as _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns If `true`, rows that are too short
 *   (fewer columns than the header) will be interpreted as _empty_ values.
 *   Default: `true`.
 * @param ignoreExcessColumns If `true`, rows that are too long
 *   (more columns than the header) will have those columns dropped.
 *   Default: `true`.
 * @param quote The quote character.
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 * For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * Default: `"`.
 * @param ignoreSurroundingSpaces If `true`, leading and trailing blanks around non-quoted fields will be trimmed.
 *   Default: `true`.
 * @param trimInsideQuoted If `true`, leading and trailing blanks inside quoted fields will be trimmed.
 *   Default: `false`.
 * @param parseParallel If `true`, the data will be parsed in parallel.
 *   Can be turned off for debugging.
 *   Default: `true`.
 */
@ExperimentalCsv
public fun DataFrame.Companion.readDelim(
    fileOrUrl: String,
    delimiter: Char = DelimParams.CSV_DELIMITER,
    header: List<String> = DelimParams.HEADER,
    compression: Compression<*> = compressionStateOf(fileOrUrl),
    colTypes: Map<String, ColType> = DelimParams.COL_TYPES,
    skipLines: Long = DelimParams.SKIP_LINES,
    readLines: Long? = DelimParams.READ_LINES,
    parserOptions: ParserOptions = DelimParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = DelimParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = DelimParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = DelimParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = DelimParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = DelimParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = DelimParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = DelimParams.PARSE_PARALLEL,
): DataFrame<*> =
    catchHttpResponse(asURL(fileOrUrl = fileOrUrl)) {
        readDelimImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
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
        )
    }

/**
 *
 * ### Read Delimiter-Separated Text InputStream to [DataFrame]
 *
 * Reads any delimiter-separated text input stream to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * ##### Other overloads
 * With the overloads of [DataFrame.readDelim][readDelim]`()`, you can read any delimiter-separated text by [File][File],
 * [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readDelim][readDelim]`("input.txt")`
 *
 * or [DataFrame.readDelim][readDelim]`(`[File][File]`("input.txt"))`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" delimiter-separated text data from a [String] using:
 *
 * [DataFrame.readDelimStr][readDelimStr]`("")`
 *
 * _**NOTE EXPERIMENTAL**: This is a new set of functions, replacing the old [DataFrame.readDelim][org.jetbrains.kotlinx.dataframe.io.readDelim]`()` functions.
 * They'll hopefully be faster and better. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv] to be able to use them._
 *
 * @param inputStream Represents the file to read.
 * @param delimiter The field delimiter character. Default: ','.
 * @param compression The compression of the data.
 *   Default: [Compression.None][org.jetbrains.kotlinx.dataframe.io.Compression.None], unless detected otherwise from the input file or url.
 * @param header Optional column titles.
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 *   Default: empty list.
 * @param colTypes The expected [ColType] per column name.
 *   When given, the parser will read the column as that type, else it will infer the type.
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [DEFAULT_COL_TYPE][org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 *   Default: empty map, a.k.a. infer every column type.
 * @param skipLines The number of lines to skip before reading the header and data.
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 *   Default: `0`.
 * @param readLines The maximum number of lines to read from the data.
 *   If `null`, all lines will be read.
 *   Default: `null`, reads all lines.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *   Default, [defaultDelimParserOptions][org.jetbrains.kotlinx.dataframe.io.defaultDelimParserOptions]:
 *   ```
 *   ParserOptions(nullStrings = ["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"])
 *   ```
 * @param ignoreEmptyLines If `true`, intermediate empty lines will be skipped.
 *   Default: `false`, empty line will be interpreted as _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns If `true`, rows that are too short
 *   (fewer columns than the header) will be interpreted as _empty_ values.
 *   Default: `true`.
 * @param ignoreExcessColumns If `true`, rows that are too long
 *   (more columns than the header) will have those columns dropped.
 *   Default: `true`.
 * @param quote The quote character.
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 * For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * Default: `"`.
 * @param ignoreSurroundingSpaces If `true`, leading and trailing blanks around non-quoted fields will be trimmed.
 *   Default: `true`.
 * @param trimInsideQuoted If `true`, leading and trailing blanks inside quoted fields will be trimmed.
 *   Default: `false`.
 * @param parseParallel If `true`, the data will be parsed in parallel.
 *   Can be turned off for debugging.
 *   Default: `true`.
 * @param additionalCsvSpecs Optional [CsvSpecs] to configure additional
 *   parsing options not covered by the other parameters.
 *   The (default) values of other parameters will override the values in [additionalCsvSpecs].
 *   Default: `null`.
 */
@ExperimentalCsv
public fun DataFrame.Companion.readDelim(
    inputStream: InputStream,
    delimiter: Char = DelimParams.CSV_DELIMITER,
    header: List<String> = DelimParams.HEADER,
    compression: Compression<*> = DelimParams.COMPRESSION,
    colTypes: Map<String, ColType> = DelimParams.COL_TYPES,
    skipLines: Long = DelimParams.SKIP_LINES,
    readLines: Long? = DelimParams.READ_LINES,
    parserOptions: ParserOptions = DelimParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = DelimParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = DelimParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = DelimParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = DelimParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = DelimParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = DelimParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = DelimParams.PARSE_PARALLEL,
    additionalCsvSpecs: CsvSpecs? = DelimParams.ADDITIONAL_CSV_SPECS,
): DataFrame<*> =
    readDelimImpl(
        inputStream = inputStream,
        delimiter = delimiter,
        header = header,
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
        additionalCsvSpecs = additionalCsvSpecs,
    )

/**
 * ### Read Delimiter-Separated Text String to [DataFrame]
 *
 * Reads any delimiter-separated text [String] to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, [delimiter],
 * [header], [colTypes], [readLines], and [parserOptions].
 * See the param list below for all settings.
 *
 * ##### Other overloads
 * With the overloads of [DataFrame.readDelim][readDelim]`()`, you can read any delimiter-separated text by [File][File],
 * [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, [DataFrame.readDelim][readDelim]`("input.txt")`
 *
 * or [DataFrame.readDelim][readDelim]`(`[File][File]`("input.txt"))`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. [compression] is automatically detected.
 *
 * You can also read "raw" delimiter-separated text data from a [String] using:
 *
 * [DataFrame.readDelimStr][readDelimStr]`("")`
 *
 * _**NOTE EXPERIMENTAL**: This is a new set of functions, replacing the old [DataFrame.readDelim][org.jetbrains.kotlinx.dataframe.io.readDelim]`()` functions.
 * They'll hopefully be faster and better. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv] to be able to use them._
 *
 * @param text The raw data to read in the form of a [String].
 * @param delimiter The field delimiter character. Default: ','.
 * @param header Optional column titles.
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 *   Default: empty list.
 * @param colTypes The expected [ColType] per column name.
 *   When given, the parser will read the column as that type, else it will infer the type.
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [DEFAULT_COL_TYPE][org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 *   Default: empty map, a.k.a. infer every column type.
 * @param skipLines The number of lines to skip before reading the header and data.
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 *   Default: `0`.
 * @param readLines The maximum number of lines to read from the data.
 *   If `null`, all lines will be read.
 *   Default: `null`, reads all lines.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *   Default, [defaultDelimParserOptions][org.jetbrains.kotlinx.dataframe.io.defaultDelimParserOptions]:
 *   ```
 *   ParserOptions(nullStrings = ["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"])
 *   ```
 * @param ignoreEmptyLines If `true`, intermediate empty lines will be skipped.
 *   Default: `false`, empty line will be interpreted as _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns If `true`, rows that are too short
 *   (fewer columns than the header) will be interpreted as _empty_ values.
 *   Default: `true`.
 * @param ignoreExcessColumns If `true`, rows that are too long
 *   (more columns than the header) will have those columns dropped.
 *   Default: `true`.
 * @param quote The quote character.
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 * For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * Default: `"`.
 * @param ignoreSurroundingSpaces If `true`, leading and trailing blanks around non-quoted fields will be trimmed.
 *   Default: `true`.
 * @param trimInsideQuoted If `true`, leading and trailing blanks inside quoted fields will be trimmed.
 *   Default: `false`.
 * @param parseParallel If `true`, the data will be parsed in parallel.
 *   Can be turned off for debugging.
 *   Default: `true`.
 */
@ExperimentalCsv
public fun DataFrame.Companion.readDelimStr(
    text: String,
    delimiter: Char = DelimParams.CSV_DELIMITER,
    header: List<String> = DelimParams.HEADER,
    colTypes: Map<String, ColType> = DelimParams.COL_TYPES,
    skipLines: Long = DelimParams.SKIP_LINES,
    readLines: Long? = DelimParams.READ_LINES,
    parserOptions: ParserOptions = DelimParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = DelimParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = DelimParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = DelimParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = DelimParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = DelimParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = DelimParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = DelimParams.PARSE_PARALLEL,
): DataFrame<*> =
    readDelimImpl(
        inputStream = text.byteInputStream(),
        delimiter = delimiter,
        header = header,
        compression = Compression.None, // of course
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
    )
