package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.documentation.ReadDelim
import org.jetbrains.kotlinx.dataframe.documentation.ReadDelim.CommonReadParams
import org.jetbrains.kotlinx.dataframe.documentation.ReadDelim.CsvDocs
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams
import org.jetbrains.kotlinx.dataframe.impl.io.asURL
import org.jetbrains.kotlinx.dataframe.impl.io.catchHttpResponse
import org.jetbrains.kotlinx.dataframe.impl.io.compressionStateOf
import org.jetbrains.kotlinx.dataframe.impl.io.readDelimImpl
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL

/**
 * @include [CsvDocs]
 * @set [ReadDelim.DataTitleArg] File
 * @set [ReadDelim.DataArg] file
 * @include [DelimParams.FILE]
 * @include [DelimParams.CSV_DELIMITER]
 * @include [DelimParams.COMPRESSION]
 * @include [CommonReadParams]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
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
 * @include [CsvDocs]
 * @set [ReadDelim.DataTitleArg] Url
 * @set [ReadDelim.DataArg] url
 * @include [DelimParams.URL]
 * @include [DelimParams.CSV_DELIMITER]
 * @include [DelimParams.COMPRESSION]
 * @include [CommonReadParams]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
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
 * @include [CsvDocs]
 * @set [ReadDelim.DataTitleArg] File or URL
 * @set [ReadDelim.DataArg] file or url
 * @include [DelimParams.FILE_OR_URL]
 * @include [DelimParams.CSV_DELIMITER]
 * @include [DelimParams.COMPRESSION]
 * @include [CommonReadParams]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
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
 * {@comment the only one with additionalCsvSpecs}
 * @include [CsvDocs]
 * @set [ReadDelim.DataTitleArg] InputStream
 * @set [ReadDelim.DataArg] input stream
 * @include [DelimParams.INPUT_STREAM]
 * @include [DelimParams.CSV_DELIMITER]
 * @include [DelimParams.COMPRESSION]
 * @include [CommonReadParams]
 * @include [DelimParams.ADDITIONAL_CSV_SPECS]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readCsv(
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
 * @include [CsvDocs]
 * @set [ReadDelim.DataTitleArg] String
 * @set [ReadDelim.DataArg] [String]
 * @include [DelimParams.TEXT]
 * @include [DelimParams.CSV_DELIMITER]
 * @include [CommonReadParams]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readCsvStr(
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
