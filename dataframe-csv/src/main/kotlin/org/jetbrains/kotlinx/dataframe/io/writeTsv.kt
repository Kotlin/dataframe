package org.jetbrains.kotlinx.dataframe.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl
import java.io.File
import java.io.FileWriter

@ExperimentalCsv
public fun AnyFrame.writeTsv(
    file: File,
    delimiter: Char = DelimParams.TSV_DELIMITER,
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

@ExperimentalCsv
public fun AnyFrame.writeTsv(
    path: String,
    delimiter: Char = DelimParams.TSV_DELIMITER,
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

// only one with additionalCsvFormat
@ExperimentalCsv
public fun AnyFrame.writeTsv(
    writer: Appendable,
    delimiter: Char = DelimParams.TSV_DELIMITER,
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

@ExperimentalCsv
public fun AnyFrame.toTsvStr(
    includeHeader: Boolean = DelimParams.INCLUDE_HEADER,
    delimiter: Char = DelimParams.TSV_DELIMITER,
    quote: Char? = DelimParams.QUOTE,
    quoteMode: QuoteMode = DelimParams.QUOTE_MODE,
    escapeChar: Char? = DelimParams.ESCAPE_CHAR,
    commentChar: Char? = DelimParams.COMMENT_CHAR,
    headerComments: List<String> = DelimParams.HEADER_COMMENTS,
    recordSeparator: String = DelimParams.RECORD_SEPARATOR,
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
        )
    }
