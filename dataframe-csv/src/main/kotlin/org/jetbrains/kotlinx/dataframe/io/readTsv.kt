@file:JvmName("ReadTsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.documentation.CommonReadDelimDocs
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADJUST_CSV_SPECS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ALLOW_MISSING_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COL_TYPES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COMPRESSION
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.FILE_OR_URL_READ
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.FILE_READ
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.FIXED_COLUMN_WIDTHS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HAS_FIXED_WIDTH_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HEADER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.IGNORE_EMPTY_LINES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.IGNORE_EXCESS_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.IGNORE_SURROUNDING_SPACES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.INPUT_STREAM_READ
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.PARSER_OPTIONS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.PARSE_PARALLEL
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.PATH_READ
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
 * @include [CommonReadDelimDocs.TsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] File
 * @set [CommonReadDelimDocs.DATA] file
 * @include [PATH_READ]
 * @include [TSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
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
 * @include [CommonReadDelimDocs.TsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] File
 * @set [CommonReadDelimDocs.DATA] file
 * @include [FILE_READ]
 * @include [TSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
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
 * @include [CommonReadDelimDocs.TsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] Url
 * @set [CommonReadDelimDocs.DATA] url
 * @include [DelimParams.URL_READ]
 * @include [TSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
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
 * @include [CommonReadDelimDocs.TsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] File or URL
 * @set [CommonReadDelimDocs.DATA] file or url
 * @include [FILE_OR_URL_READ]
 * @include [TSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
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
 * {@comment the only one with adjustCsvSpecs}
 * @include [CommonReadDelimDocs.TsvDocs]
 * @set [CommonReadDelimDocs.DATA_TITLE] InputStream
 * @set [CommonReadDelimDocs.DATA] input stream
 * @include [INPUT_STREAM_READ]
 * @include [TSV_DELIMITER]
 * @include [COMPRESSION]
 * @include [CommonReadDelimDocs.CommonReadParams]
 * @include [ADJUST_CSV_SPECS]
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
