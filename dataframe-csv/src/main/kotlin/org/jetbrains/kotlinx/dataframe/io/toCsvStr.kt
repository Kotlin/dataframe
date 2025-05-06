package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.documentation.CommonWriteDelimDocs
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADJUST_CSV_FORMAT
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COMMENT_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.CSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ESCAPE_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HEADER_COMMENTS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.INCLUDE_HEADER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE_MODE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.RECORD_SEPARATOR
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl

/**
 * @include [CommonWriteDelimDocs.CsvDocs]
 * @set [CommonWriteDelimDocs.WRITE_OR_CONVERT] Convert
 * @set [CommonWriteDelimDocs.DATA_TITLE] String
 * @set [CommonWriteDelimDocs.DATA] [String]
 * @include [CSV_DELIMITER]
 * @include [CommonWriteDelimDocs.CommonWriteParams]
 */
public fun AnyFrame.toCsvStr(
    delimiter: Char = CSV_DELIMITER,
    includeHeader: Boolean = INCLUDE_HEADER,
    quote: Char? = QUOTE,
    quoteMode: QuoteMode = QUOTE_MODE,
    escapeChar: Char? = ESCAPE_CHAR,
    commentChar: Char? = COMMENT_CHAR,
    headerComments: List<String> = HEADER_COMMENTS,
    recordSeparator: String = RECORD_SEPARATOR,
): String =
    buildString {
        writeDelimImpl(
            df = this@toCsvStr,
            writer = this,
            delimiter = delimiter,
            includeHeader = includeHeader,
            quote = quote,
            quoteMode = quoteMode,
            escapeChar = escapeChar,
            commentChar = commentChar,
            headerComments = headerComments,
            recordSeparator = recordSeparator,
            adjustCsvFormat = ADJUST_CSV_FORMAT,
        )
    }
