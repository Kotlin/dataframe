package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.documentationCsv.CommonWriteDelimDocs
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.ADJUST_CSV_FORMAT
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.COMMENT_CHAR
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.ESCAPE_CHAR
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.HEADER_COMMENTS
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.INCLUDE_HEADER
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.QUOTE_MODE
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.RECORD_SEPARATOR
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.TSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl

/**
 * @include [CommonWriteDelimDocs.TsvDocs]
 * @set [CommonWriteDelimDocs.WRITE_OR_CONVERT] Convert
 * @set [CommonWriteDelimDocs.DATA_TITLE] String
 * @set [CommonWriteDelimDocs.DATA] [String]
 * @include [TSV_DELIMITER]
 * @include [CommonWriteDelimDocs.CommonWriteParams]
 */
public fun AnyFrame.toTsvStr(
    includeHeader: Boolean = INCLUDE_HEADER,
    delimiter: Char = TSV_DELIMITER,
    quote: Char? = QUOTE,
    quoteMode: QuoteMode = QUOTE_MODE,
    escapeChar: Char? = ESCAPE_CHAR,
    commentChar: Char? = COMMENT_CHAR,
    headerComments: List<String> = HEADER_COMMENTS,
    recordSeparator: String = RECORD_SEPARATOR,
): String =
    buildString {
        writeDelimImpl(
            df = this@toTsvStr,
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
