package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.documentation.CommonWriteDelimDocs
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.COMMENT_CHAR
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.CSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.ESCAPE_CHAR
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.HEADER_COMMENTS
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.INCLUDE_HEADER
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.QUOTE_MODE
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.RECORD_SEPARATOR
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl

/**
 * @include [CommonWriteDelimDocs.CsvDocs]
 * @set [CommonWriteDelimDocs.WriteOrConvertArg] Convert
 * @set [CommonWriteDelimDocs.DataTitleArg] String
 * @set [CommonWriteDelimDocs.DataArg] [String]
 * @include [CSV_DELIMITER]
 * @include [CommonWriteDelimDocs.CommonWriteParams]
 */
@ExperimentalCsv
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
        )
    }
