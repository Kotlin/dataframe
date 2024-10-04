package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.impl.io.CsvTsvParams
import org.jetbrains.kotlinx.dataframe.impl.io.asURL
import org.jetbrains.kotlinx.dataframe.impl.io.catchHttpResponse
import org.jetbrains.kotlinx.dataframe.impl.io.compressionStateOf
import org.jetbrains.kotlinx.dataframe.impl.io.readCsvOrTsvImpl
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL

/**
 * You can add a default column type to the `colTypes` parameter
 * by setting the key to [DEFAULT_COL_TYPE] and the value to the desired type.
 */
public const val DEFAULT_COL_TYPE: String = ".default"

@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
    file: File,
    delimiter: Char = CsvTsvParams.CSV_DELIMITER,
    header: List<String> = CsvTsvParams.HEADER,
    compression: CsvCompression<*> = compressionStateOf(file),
    colTypes: Map<String, ColType> = CsvTsvParams.COL_TYPES,
    skipLines: Long = CsvTsvParams.SKIP_LINES,
    readLines: Long? = CsvTsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvTsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvTsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvTsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvTsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvTsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvTsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvTsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvTsvParams.PARSE_PARALLEL,
): DataFrame<*> =
    FileInputStream(file).use {
        readCsvOrTsvImpl(
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

@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
    url: URL,
    delimiter: Char = CsvTsvParams.CSV_DELIMITER,
    header: List<String> = CsvTsvParams.HEADER,
    compression: CsvCompression<*> = compressionStateOf(url),
    colTypes: Map<String, ColType> = CsvTsvParams.COL_TYPES,
    skipLines: Long = CsvTsvParams.SKIP_LINES,
    readLines: Long? = CsvTsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvTsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvTsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvTsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvTsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvTsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvTsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvTsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvTsvParams.PARSE_PARALLEL,
): DataFrame<*> =
    catchHttpResponse(url) {
        readCsvOrTsvImpl(
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

@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
    fileOrUrl: String,
    delimiter: Char = CsvTsvParams.CSV_DELIMITER,
    header: List<String> = CsvTsvParams.HEADER,
    compression: CsvCompression<*> = compressionStateOf(fileOrUrl),
    colTypes: Map<String, ColType> = CsvTsvParams.COL_TYPES,
    skipLines: Long = CsvTsvParams.SKIP_LINES,
    readLines: Long? = CsvTsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvTsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvTsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvTsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvTsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvTsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvTsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvTsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvTsvParams.PARSE_PARALLEL,
): DataFrame<*> =
    catchHttpResponse(asURL(fileOrUrl = fileOrUrl)) {
        readCsvOrTsvImpl(
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

// the only one with additionalCsvSpecs
@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
    inputStream: InputStream,
    delimiter: Char = CsvTsvParams.CSV_DELIMITER,
    header: List<String> = CsvTsvParams.HEADER,
    compression: CsvCompression<*> = CsvTsvParams.COMPRESSION,
    colTypes: Map<String, ColType> = CsvTsvParams.COL_TYPES,
    skipLines: Long = CsvTsvParams.SKIP_LINES,
    readLines: Long? = CsvTsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvTsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvTsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvTsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvTsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvTsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvTsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvTsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvTsvParams.PARSE_PARALLEL,
    additionalCsvSpecs: CsvSpecs = CsvTsvParams.ADDITIONAL_CSV_SPECS,
): DataFrame<*> =
    readCsvOrTsvImpl(
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

@ExperimentalCsv
public fun DataFrame.Companion.readCsvStr(
    text: String,
    delimiter: Char = CsvTsvParams.CSV_DELIMITER,
    header: List<String> = CsvTsvParams.HEADER,
    compression: CsvCompression<*> = CsvTsvParams.COMPRESSION,
    colTypes: Map<String, ColType> = CsvTsvParams.COL_TYPES,
    skipLines: Long = CsvTsvParams.SKIP_LINES,
    readLines: Long? = CsvTsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvTsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvTsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvTsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvTsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvTsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvTsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvTsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvTsvParams.PARSE_PARALLEL,
): DataFrame<*> =
    readCsvOrTsvImpl(
        inputStream = text.byteInputStream(),
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
