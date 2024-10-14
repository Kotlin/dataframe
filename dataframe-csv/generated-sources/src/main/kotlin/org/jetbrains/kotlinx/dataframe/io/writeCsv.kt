package org.jetbrains.kotlinx.dataframe.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl
import java.io.File
import java.io.FileWriter

/**
 * TODO
 * @param delimiter The field delimiter character. Default: ','.
 * @param includeHeader If `true`, the header will be included in the output, else it will not.
 *   Default: `true`.
 * @param quote The quote character.
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 * For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * Default: `"`.
 * @param quoteMode The default [QuoteMode][org.jetbrains.kotlinx.dataframe.io.QuoteMode] to use when writing CSV / TSV files.
 *   Default: [QuoteMode.MINIMAL][org.jetbrains.kotlinx.dataframe.io.QuoteMode.MINIMAL].
 * @param escapeChar The escape character to use when writing CSV / TSV files with [QuoteMode.NONE][org.jetbrains.kotlinx.dataframe.io.QuoteMode.NONE].
 *   Default: `null`. This will double-quote the value.
 * @param commentChar The character that indicates a comment line in a CSV / TSV file.
 *   Default: `'#'`.
 * @param headerComments A list of comments to include at the beginning of the CSV / TSV file.
 *   Default: empty list.
 * @param recordSeparator The character that separates records in a CSV / TSV file.
 *   Default: `'\n'`, a Unix-newline.
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

// only one with additionalCsvFormat
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
