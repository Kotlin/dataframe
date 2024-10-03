package org.jetbrains.kotlinx.dataframe.impl.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.io.QuoteMode
import org.jetbrains.kotlinx.dataframe.io.toJson
import org.apache.commons.csv.QuoteMode as ApacheQuoteMode

internal fun writeCsvOrTsvImpl(
    df: AnyFrame,
    writer: Appendable,
    delimiter: Char,
    includeHeader: Boolean = CsvTsvParams.INCLUDE_HEADER,
    quote: Char? = CsvTsvParams.QUOTE,
    quoteMode: QuoteMode = CsvTsvParams.QUOTE_MODE,
    escapeChar: Char? = CsvTsvParams.ESCAPE_CHAR,
    commentChar: Char? = CsvTsvParams.COMMENT_CHAR,
    headerComments: List<String> = CsvTsvParams.HEADER_COMMENTS,
    recordSeparator: Char = CsvTsvParams.RECORD_SEPARATOR,
    additionalCsvFormat: CSVFormat = CsvTsvParams.ADDITIONAL_CSV_FORMAT,
) {
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
