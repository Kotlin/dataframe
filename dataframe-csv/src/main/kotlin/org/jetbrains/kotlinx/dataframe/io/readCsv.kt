@file:JvmName("ReadCsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.documentation.CommonReadDelimDocs
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.ADDITIONAL_CSV_SPECS
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.ALLOW_MISSING_COLUMNS
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.COL_TYPES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.COMPRESSION
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.CSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.FILE_OR_URL_READ
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.FILE_READ
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.HEADER
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.IGNORE_EMPTY_LINES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.IGNORE_EXCESS_COLUMNS
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.IGNORE_SURROUNDING_SPACES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.INPUT_STREAM_READ
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.PARSER_OPTIONS
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.PARSE_PARALLEL
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.READ_LINES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.SKIP_LINES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.TRIM_INSIDE_QUOTED
import org.jetbrains.kotlinx.dataframe.impl.io.asURL
import org.jetbrains.kotlinx.dataframe.impl.io.catchHttpResponse
import org.jetbrains.kotlinx.dataframe.impl.io.compressionStateOf
import org.jetbrains.kotlinx.dataframe.impl.io.readDelimImpl
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL

/**
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DataTitleArg] File
 * @set [CommonReadDelimDocs.DataArg] file
 * @include [FILE_READ]
 * @include [CSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
    file: File,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    compression: Compression<*> = compressionStateOf(file),
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions = PARSER_OPTIONS,
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
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DataTitleArg] Url
 * @set [CommonReadDelimDocs.DataArg] url
 * @include [DelimParams.URL_READ]
 * @include [CSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
    url: URL,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    compression: Compression<*> = compressionStateOf(url),
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions = PARSER_OPTIONS,
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
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DataTitleArg] File or URL
 * @set [CommonReadDelimDocs.DataArg] file or url
 * @include [FILE_OR_URL_READ]
 * @include [CSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
    fileOrUrl: String,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    compression: Compression<*> = compressionStateOf(fileOrUrl),
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
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
 * {@comment the only one with additionalCsvSpecs}
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DataTitleArg] InputStream
 * @set [CommonReadDelimDocs.DataArg] input stream
 * @include [INPUT_STREAM_READ]
 * @include [CSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 * @include [ADDITIONAL_CSV_SPECS]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
    inputStream: InputStream,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    compression: Compression<*> = COMPRESSION,
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
    additionalCsvSpecs: CsvSpecs? = ADDITIONAL_CSV_SPECS,
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
