package org.jetbrains.kotlinx.dataframe.impl.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.documentation.CommonWriteDelimDocs
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADDITIONAL_CSV_FORMAT
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COMMENT_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.CSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ESCAPE_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HEADER_COMMENTS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.INCLUDE_HEADER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE_MODE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.RECORD_SEPARATOR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.WRITER_WRITE
import org.jetbrains.kotlinx.dataframe.io.QuoteMode
import org.jetbrains.kotlinx.dataframe.io.toJson
import org.apache.commons.csv.QuoteMode as ApacheQuoteMode

/**
 * Writes [df] to [writer] in a delimiter-separated format.
 *
 * @param df The data to write.
 * @include [WRITER_WRITE]
 * @include [CSV_DELIMITER]
 * @include [CommonWriteDelimDocs.CommonWriteParams]
 * @include [ADDITIONAL_CSV_FORMAT]
 */
internal fun writeDelimImpl(
    df: AnyFrame,
    writer: Appendable,
    delimiter: Char,
    includeHeader: Boolean = INCLUDE_HEADER,
    quote: Char? = QUOTE,
    quoteMode: QuoteMode = QUOTE_MODE,
    escapeChar: Char? = ESCAPE_CHAR,
    commentChar: Char? = COMMENT_CHAR,
    headerComments: List<String> = HEADER_COMMENTS,
    recordSeparator: String = RECORD_SEPARATOR,
    additionalCsvFormat: CSVFormat = ADDITIONAL_CSV_FORMAT,
) {
    // setup CSV format
    val format = with(CSVFormat.Builder.create(additionalCsvFormat)) {
        setDelimiter(delimiter)
        setQuote(quote)
        setSkipHeaderRecord(!includeHeader)
        setQuoteMode(quoteMode.toApache())
        setRecordSeparator(recordSeparator)
        setEscape(escapeChar)
        setCommentMarker(commentChar)
        setHeaderComments(*headerComments.toTypedArray())
    }.build()

    // let the format handle the writing, only converting AnyRow and AnyFrame to JSON
    format.print(writer).use { printer ->
        if (includeHeader) {
            printer.printRecord(df.columnNames())
        }
        df.forEach {
            val values = it.values().map {
                when (it) {
                    is AnyRow -> it.toJson()
                    is AnyFrame -> it.toJson()
                    else -> it
                }
            }
            printer.printRecord(values)
        }
    }
}

internal fun QuoteMode.toApache(): ApacheQuoteMode =
    when (this) {
        QuoteMode.ALL -> ApacheQuoteMode.ALL
        QuoteMode.MINIMAL -> ApacheQuoteMode.MINIMAL
        QuoteMode.NON_NUMERIC -> ApacheQuoteMode.NON_NUMERIC
        QuoteMode.NONE -> ApacheQuoteMode.NONE
        QuoteMode.ALL_NON_NULL -> ApacheQuoteMode.ALL_NON_NULL
    }
