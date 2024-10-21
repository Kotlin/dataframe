package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.documentation.CommonReadDelimDocs
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.ALLOW_MISSING_COLUMNS
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.COL_TYPES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.DELIM_DELIMITER
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.HEADER
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.IGNORE_EMPTY_LINES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.IGNORE_EXCESS_COLUMNS
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.IGNORE_SURROUNDING_SPACES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.PARSER_OPTIONS
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.PARSE_PARALLEL
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.READ_LINES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.SKIP_LINES
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.TEXT_READ
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.TRIM_INSIDE_QUOTED
import org.jetbrains.kotlinx.dataframe.impl.io.readDelimImpl

/**
 * @include [CommonReadDelimDocs.DelimDocs]
 * @set [CommonReadDelimDocs.DataTitleArg] String
 * @set [CommonReadDelimDocs.DataArg] [String]
 * @include [TEXT_READ]
 * @include [DELIM_DELIMITER]
 * @include [CommonReadDelimDocs.CommonReadParams]
 */
@ExperimentalCsv
public fun DataFrame.Companion.readDelimStr(
    text: String,
    delimiter: Char = DELIM_DELIMITER,
    header: List<String> = HEADER,
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
