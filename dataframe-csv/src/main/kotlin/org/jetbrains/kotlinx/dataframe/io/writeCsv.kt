@file:JvmName("WriteCsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.documentation.WriteDelim
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl
import java.io.File
import java.io.FileWriter

/**
 * @include [WriteDelim.CsvDocs]
 * @set [WriteDelim.WriteOrConvertArg] Write
 * @set [WriteDelim.DataTitleArg] File
 * @set [WriteDelim.DataArg] file
 * @include [DelimParams.CSV_DELIMITER]
 * @include [WriteDelim.CommonWriteParams]
 */
@ExperimentalCsv
public fun AnyFrame.writeCsv(
    file: File,
    delimiter: Char = DelimParams.CSV_DELIMITER,
    includeHeader: Boolean = DelimParams.INCLUDE_HEADER,
    quote: Char? = DelimParams.QUOTE,
    quoteMode: QuoteMode = DelimParams.QUOTE_MODE,
    escapeChar: Char? = DelimParams.ESCAPE_CHAR,
    commentChar: Char? = DelimParams.COMMENT_CHAR,
    headerComments: List<String> = DelimParams.HEADER_COMMENTS,
    recordSeparator: String = DelimParams.RECORD_SEPARATOR,
): Unit =
    writeDelimImpl(
        df = this,
        writer = FileWriter(file),
        delimiter = delimiter,
        includeHeader = includeHeader,
        quote = quote,
        quoteMode = quoteMode,
        escapeChar = escapeChar,
        commentChar = commentChar,
        headerComments = headerComments,
        recordSeparator = recordSeparator,
    )

/**
 * @include [WriteDelim.CsvDocs]
 * @set [WriteDelim.WriteOrConvertArg] Write
 * @set [WriteDelim.DataTitleArg] File
 * @set [WriteDelim.DataArg] file
 * @include [DelimParams.CSV_DELIMITER]
 * @include [WriteDelim.CommonWriteParams]
 */
@ExperimentalCsv
public fun AnyFrame.writeCsv(
    path: String,
    delimiter: Char = DelimParams.CSV_DELIMITER,
    includeHeader: Boolean = DelimParams.INCLUDE_HEADER,
    quote: Char? = DelimParams.QUOTE,
    quoteMode: QuoteMode = DelimParams.QUOTE_MODE,
    escapeChar: Char? = DelimParams.ESCAPE_CHAR,
    commentChar: Char? = DelimParams.COMMENT_CHAR,
    headerComments: List<String> = DelimParams.HEADER_COMMENTS,
    recordSeparator: String = DelimParams.RECORD_SEPARATOR,
): Unit =
    writeDelimImpl(
        df = this,
        writer = FileWriter(path),
        delimiter = delimiter,
        includeHeader = includeHeader,
        quote = quote,
        quoteMode = quoteMode,
        escapeChar = escapeChar,
        commentChar = commentChar,
        headerComments = headerComments,
        recordSeparator = recordSeparator,
    )

/**
 * @include [WriteDelim.CsvDocs]
 * @set [WriteDelim.WriteOrConvertArg] Write
 * @set [WriteDelim.DataTitleArg] Appendable
 * @set [WriteDelim.DataArg] [Appendable]
 * @include [DelimParams.CSV_DELIMITER]
 * @include [WriteDelim.CommonWriteParams]
 *
 *
 * {@comment only one with additionalCsvFormat}
 */
@ExperimentalCsv
public fun AnyFrame.writeCsv(
    writer: Appendable,
    delimiter: Char = DelimParams.CSV_DELIMITER,
    includeHeader: Boolean = DelimParams.INCLUDE_HEADER,
    quote: Char? = DelimParams.QUOTE,
    quoteMode: QuoteMode = DelimParams.QUOTE_MODE,
    escapeChar: Char? = DelimParams.ESCAPE_CHAR,
    commentChar: Char? = DelimParams.COMMENT_CHAR,
    headerComments: List<String> = DelimParams.HEADER_COMMENTS,
    recordSeparator: String = DelimParams.RECORD_SEPARATOR,
    additionalCsvFormat: CSVFormat = DelimParams.ADDITIONAL_CSV_FORMAT,
): Unit =
    writeDelimImpl(
        df = this,
        writer = writer,
        delimiter = delimiter,
        includeHeader = includeHeader,
        quote = quote,
        quoteMode = quoteMode,
        escapeChar = escapeChar,
        commentChar = commentChar,
        headerComments = headerComments,
        recordSeparator = recordSeparator,
        additionalCsvFormat = additionalCsvFormat,
    )

/**
 * @include [WriteDelim.CsvDocs]
 * @set [WriteDelim.WriteOrConvertArg] Convert
 * @set [WriteDelim.DataTitleArg] String
 * @set [WriteDelim.DataArg] [String]
 * @include [DelimParams.CSV_DELIMITER]
 * @include [WriteDelim.CommonWriteParams]
 */
@ExperimentalCsv
public fun AnyFrame.toCsvStr(
    delimiter: Char = DelimParams.CSV_DELIMITER,
    includeHeader: Boolean = DelimParams.INCLUDE_HEADER,
    quote: Char? = DelimParams.QUOTE,
    quoteMode: QuoteMode = DelimParams.QUOTE_MODE,
    escapeChar: Char? = DelimParams.ESCAPE_CHAR,
    commentChar: Char? = DelimParams.COMMENT_CHAR,
    headerComments: List<String> = DelimParams.HEADER_COMMENTS,
    recordSeparator: String = DelimParams.RECORD_SEPARATOR,
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
