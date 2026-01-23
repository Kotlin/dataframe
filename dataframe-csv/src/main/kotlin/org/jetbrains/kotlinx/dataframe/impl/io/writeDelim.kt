@file:JvmName("WriteDelimDeephavenKt")

package org.jetbrains.kotlinx.dataframe.impl.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.documentationCsv.CommonWriteDelimDocs
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.ADJUST_CSV_FORMAT
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.CSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.WRITER_WRITE
import org.jetbrains.kotlinx.dataframe.io.AdjustCSVFormat
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
 * @include [ADJUST_CSV_FORMAT]
 */
internal fun writeDelimImpl(
    df: AnyFrame,
    writer: Appendable,
    delimiter: Char,
    includeHeader: Boolean,
    quote: Char?,
    quoteMode: QuoteMode,
    escapeChar: Char?,
    commentChar: Char?,
    headerComments: List<String>,
    recordSeparator: String,
    adjustCsvFormat: AdjustCSVFormat,
) {
    // setup CSV format
    val format = with(CSVFormat.Builder.create(CSVFormat.DEFAULT)) {
        setDelimiter(delimiter)
        setQuote(quote)
        setSkipHeaderRecord(!includeHeader)
        setQuoteMode(quoteMode.toApache())
        setRecordSeparator(recordSeparator)
        setEscape(escapeChar)
        setCommentMarker(commentChar)
        setHeaderComments(*headerComments.toTypedArray())
    }.let { adjustCsvFormat(it, it) }
        .get()

    // let the format handle the writing, only converting AnyRow and AnyFrame to JSON
    format.print(writer).use { printer ->
        if (includeHeader) {
            printer.printRecord(df.columnNames())
        }
        df.forEach {
            val values = it.values().map {
                when (it) {
                    is AnyRow -> try {
                        it.toJson()
                    } catch (_: NoClassDefFoundError) {
                        error(
                            "Encountered a DataRow value when writing to csv/tsv/delim. It must be serialized to JSON, requiring the 'dataframe-json' dependency.",
                        )
                    }

                    is AnyFrame -> try {
                        it.toJson()
                    } catch (_: NoClassDefFoundError) {
                        error(
                            "Encountered a DataFrame value when writing to csv/tsv/delim. It must be serialized to JSON, requiring the 'dataframe-json' dependency.",
                        )
                    }

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
