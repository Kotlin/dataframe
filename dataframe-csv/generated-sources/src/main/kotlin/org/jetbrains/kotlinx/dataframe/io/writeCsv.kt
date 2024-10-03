package org.jetbrains.kotlinx.dataframe.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.impl.io.CsvTsvParams
import org.jetbrains.kotlinx.dataframe.impl.io.writeCsvOrTsvImpl
import java.io.File
import java.io.FileWriter

@ExperimentalCsv
public fun AnyFrame.writeCsv(
    file: File,
    delimiter: Char = CsvTsvParams.CSV_DELIMITER,
    includeHeader: Boolean = CsvTsvParams.INCLUDE_HEADER,
    quote: Char? = CsvTsvParams.QUOTE,
    quoteMode: QuoteMode = CsvTsvParams.QUOTE_MODE,
    escapeChar: Char? = CsvTsvParams.ESCAPE_CHAR,
    commentChar: Char? = CsvTsvParams.COMMENT_CHAR,
    headerComments: List<String> = CsvTsvParams.HEADER_COMMENTS,
    recordSeparator: Char = CsvTsvParams.RECORD_SEPARATOR,
): Unit =
    writeCsvOrTsvImpl(
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
public fun AnyFrame.writeCsv(
    path: String,
    delimiter: Char = CsvTsvParams.CSV_DELIMITER,
    includeHeader: Boolean = CsvTsvParams.INCLUDE_HEADER,
    quote: Char? = CsvTsvParams.QUOTE,
    quoteMode: QuoteMode = CsvTsvParams.QUOTE_MODE,
    escapeChar: Char? = CsvTsvParams.ESCAPE_CHAR,
    commentChar: Char? = CsvTsvParams.COMMENT_CHAR,
    headerComments: List<String> = CsvTsvParams.HEADER_COMMENTS,
    recordSeparator: Char = CsvTsvParams.RECORD_SEPARATOR,
): Unit =
    writeCsvOrTsvImpl(
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
public fun AnyFrame.writeCsv(
    writer: Appendable,
    delimiter: Char = CsvTsvParams.CSV_DELIMITER,
    includeHeader: Boolean = CsvTsvParams.INCLUDE_HEADER,
    quote: Char? = CsvTsvParams.QUOTE,
    quoteMode: QuoteMode = CsvTsvParams.QUOTE_MODE,
    escapeChar: Char? = CsvTsvParams.ESCAPE_CHAR,
    commentChar: Char? = CsvTsvParams.COMMENT_CHAR,
    headerComments: List<String> = CsvTsvParams.HEADER_COMMENTS,
    recordSeparator: Char = CsvTsvParams.RECORD_SEPARATOR,
    additionalCsvFormat: CSVFormat = CsvTsvParams.ADDITIONAL_CSV_FORMAT,
): Unit =
    writeCsvOrTsvImpl(
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
public fun AnyFrame.toCsvStr(
    delimiter: Char = CsvTsvParams.CSV_DELIMITER,
    includeHeader: Boolean = CsvTsvParams.INCLUDE_HEADER,
    quote: Char? = CsvTsvParams.QUOTE,
    quoteMode: QuoteMode = CsvTsvParams.QUOTE_MODE,
    escapeChar: Char? = CsvTsvParams.ESCAPE_CHAR,
    commentChar: Char? = CsvTsvParams.COMMENT_CHAR,
    headerComments: List<String> = CsvTsvParams.HEADER_COMMENTS,
    recordSeparator: Char = CsvTsvParams.RECORD_SEPARATOR,
): String =
    buildString {
        writeCsvOrTsvImpl(
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
