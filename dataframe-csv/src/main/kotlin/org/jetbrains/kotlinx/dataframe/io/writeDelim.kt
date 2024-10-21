@file:JvmName("WriteCsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.documentation.CommonWriteDelimDocs
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.ADDITIONAL_CSV_FORMAT
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.COMMENT_CHAR
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.DELIM_DELIMITER
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.ESCAPE_CHAR
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.FILE_WRITE
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.HEADER_COMMENTS
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.INCLUDE_HEADER
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.PATH_WRITE
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.QUOTE_MODE
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.RECORD_SEPARATOR
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams.WRITER_WRITE
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl
import java.io.File
import java.io.FileWriter

/**
 * @include [CommonWriteDelimDocs.DelimDocs]
 * @set [CommonWriteDelimDocs.WriteOrConvertArg] Write
 * @set [CommonWriteDelimDocs.DataTitleArg] File
 * @set [CommonWriteDelimDocs.DataArg] file
 * @include [FILE_WRITE]
 * @include [DELIM_DELIMITER]
 * @include [CommonWriteDelimDocs.CommonWriteParams]
 */
@ExperimentalCsv
public fun AnyFrame.writeDelim(
    file: File,
    delimiter: Char = DELIM_DELIMITER,
    includeHeader: Boolean = INCLUDE_HEADER,
    quote: Char? = QUOTE,
    quoteMode: QuoteMode = QUOTE_MODE,
    escapeChar: Char? = ESCAPE_CHAR,
    commentChar: Char? = COMMENT_CHAR,
    headerComments: List<String> = HEADER_COMMENTS,
    recordSeparator: String = RECORD_SEPARATOR,
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
 * @include [CommonWriteDelimDocs.DelimDocs]
 * @set [CommonWriteDelimDocs.WriteOrConvertArg] Write
 * @set [CommonWriteDelimDocs.DataTitleArg] File
 * @set [CommonWriteDelimDocs.DataArg] file
 * @include [PATH_WRITE]
 * @include [DELIM_DELIMITER]
 * @include [CommonWriteDelimDocs.CommonWriteParams]
 */
@ExperimentalCsv
public fun AnyFrame.writeDelim(
    path: String,
    delimiter: Char = DELIM_DELIMITER,
    includeHeader: Boolean = INCLUDE_HEADER,
    quote: Char? = QUOTE,
    quoteMode: QuoteMode = QUOTE_MODE,
    escapeChar: Char? = ESCAPE_CHAR,
    commentChar: Char? = COMMENT_CHAR,
    headerComments: List<String> = HEADER_COMMENTS,
    recordSeparator: String = RECORD_SEPARATOR,
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
 * {@comment only one with additionalCsvFormat}
 * @include [CommonWriteDelimDocs.DelimDocs]
 * @set [CommonWriteDelimDocs.WriteOrConvertArg] Write
 * @set [CommonWriteDelimDocs.DataTitleArg] Appendable
 * @set [CommonWriteDelimDocs.DataArg] [Appendable]
 * @include [WRITER_WRITE]
 * @include [DELIM_DELIMITER]
 * @include [CommonWriteDelimDocs.CommonWriteParams]
 * @include [ADDITIONAL_CSV_FORMAT]
 */
@ExperimentalCsv
public fun AnyFrame.writeDelim(
    writer: Appendable,
    delimiter: Char = DELIM_DELIMITER,
    includeHeader: Boolean = INCLUDE_HEADER,
    quote: Char? = QUOTE,
    quoteMode: QuoteMode = QUOTE_MODE,
    escapeChar: Char? = ESCAPE_CHAR,
    commentChar: Char? = COMMENT_CHAR,
    headerComments: List<String> = HEADER_COMMENTS,
    recordSeparator: String = RECORD_SEPARATOR,
    additionalCsvFormat: CSVFormat = ADDITIONAL_CSV_FORMAT,
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
