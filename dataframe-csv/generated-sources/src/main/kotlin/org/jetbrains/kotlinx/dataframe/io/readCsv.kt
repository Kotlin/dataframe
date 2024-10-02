package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.impl.io.CsvParams
import org.jetbrains.kotlinx.dataframe.impl.io.catchHttpResponse
import org.jetbrains.kotlinx.dataframe.impl.io.isCompressed
import org.jetbrains.kotlinx.dataframe.impl.io.readCsvImpl
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL

/**
 * You can add a default column type to the `colTypes` parameter
 * by setting the key to [DEFAULT_COL_TYPE] and the value to the desired type.
 */
public const val DEFAULT_COL_TYPE: String = ".default"

public fun DataFrame.Companion.readCsv(
    file: File,
    delimiter: Char = CsvParams.DELIMITER,
    header: List<String> = CsvParams.HEADER,
    colTypes: Map<String, ColType> = CsvParams.COL_TYPES,
    skipLines: Long = CsvParams.SKIP_LINES,
    readLines: Long? = CsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvParams.PARSE_PARALLEL,
): DataFrame<*> =
    FileInputStream(file).use {
        readCsvImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            isCompressed = isCompressed(file),
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

public fun DataFrame.Companion.readCsv(
    url: URL,
    delimiter: Char = CsvParams.DELIMITER,
    header: List<String> = CsvParams.HEADER,
    colTypes: Map<String, ColType> = CsvParams.COL_TYPES,
    skipLines: Long = CsvParams.SKIP_LINES,
    readLines: Long? = CsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvParams.PARSE_PARALLEL,
): DataFrame<*> =
    catchHttpResponse(url) {
        readCsvImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            isCompressed = isCompressed(url),
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

public fun DataFrame.Companion.readCsv(
    fileOrUrl: String,
    delimiter: Char = CsvParams.DELIMITER,
    header: List<String> = CsvParams.HEADER,
    colTypes: Map<String, ColType> = CsvParams.COL_TYPES,
    skipLines: Long = CsvParams.SKIP_LINES,
    readLines: Long? = CsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvParams.PARSE_PARALLEL,
): DataFrame<*> =
    catchHttpResponse(asURL(fileOrUrl)) {
        readCsvImpl(
            inputStream = it,
            delimiter = delimiter,
            header = header,
            isCompressed = isCompressed(fileOrUrl),
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

// the only one with additionalCsvSpecs
public fun DataFrame.Companion.readCsv(
    inputStream: InputStream,
    delimiter: Char = CsvParams.DELIMITER,
    header: List<String> = CsvParams.HEADER,
    isCompressed: Boolean = CsvParams.IS_COMPRESSED,
    colTypes: Map<String, ColType> = CsvParams.COL_TYPES,
    skipLines: Long = CsvParams.SKIP_LINES,
    readLines: Long? = CsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvParams.PARSE_PARALLEL,
    additionalCsvSpecs: CsvSpecs = CsvParams.ADDITIONAL_CSV_SPECS,
): DataFrame<*> =
    readCsvImpl(
        inputStream = inputStream,
        delimiter = delimiter,
        header = header,
        isCompressed = isCompressed,
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

public fun DataFrame.Companion.readCsvStr(
    text: String,
    delimiter: Char = CsvParams.DELIMITER,
    header: List<String> = CsvParams.HEADER,
    isCompressed: Boolean = CsvParams.IS_COMPRESSED,
    colTypes: Map<String, ColType> = CsvParams.COL_TYPES,
    skipLines: Long = CsvParams.SKIP_LINES,
    readLines: Long? = CsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvParams.PARSE_PARALLEL,
): AnyFrame =
    readCsvImpl(
        inputStream = text.byteInputStream(),
        delimiter = delimiter,
        header = header,
        isCompressed = isCompressed,
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
