package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.documentation.CommonReadDelimDocs
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADJUST_CSV_SPECS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ALLOW_MISSING_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COL_TYPES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.CSV_DELIMITER
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
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.TEXT_READ
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.TRIM_INSIDE_QUOTED
import org.jetbrains.kotlinx.dataframe.impl.io.readDelimImpl

/**
 * @include [CommonReadDelimDocs.CsvDocs]
 * @set [CommonReadDelimDocs.DataTitleArg] String
 * @set [CommonReadDelimDocs.DataArg] [String]
 * @include [TEXT_READ]
 * @include [CSV_DELIMITER]
 * @include [CommonReadDelimDocs.CommonReadParams]
 */
public fun DataFrame.Companion.readCsvStr(
    text: String,
    delimiter: Char = CSV_DELIMITER,
    header: List<String> = HEADER,
    hasFixedWidthColumns: Boolean = HAS_FIXED_WIDTH_COLUMNS,
    fixedColumnWidths: List<Int> = FIXED_COLUMN_WIDTHS,
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
    readDelimImpl(
        inputStream = text.byteInputStream(),
        delimiter = delimiter,
        header = header,
        hasFixedWidthColumns = hasFixedWidthColumns,
        fixedColumnWidths = fixedColumnWidths,
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
        adjustCsvSpecs = ADJUST_CSV_SPECS,
    )
