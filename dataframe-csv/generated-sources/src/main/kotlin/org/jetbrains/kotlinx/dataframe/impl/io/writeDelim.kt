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
 * @param writer The [Appendable] to write to.
 * @param delimiter The field delimiter character. Default: ','.
 *
 *   Ignored if [hasFixedWidthColumns] is `true`.
 * @param includeHeader Whether to include the header in the output. Default: `true`.
 * @param quote The quote character. Default: `"`.
 *
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 *   For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * @param quoteMode The [QuoteMode][org.jetbrains.kotlinx.dataframe.io.QuoteMode] to use when writing CSV / TSV files.
 *   Default: [QuoteMode.MINIMAL][org.jetbrains.kotlinx.dataframe.io.QuoteMode.MINIMAL].
 * @param escapeChar The escape character to use when writing CSV / TSV files with [QuoteMode.NONE][org.jetbrains.kotlinx.dataframe.io.QuoteMode.NONE].
 *   Default: `null`. This will double-quote the value.
 * @param commentChar The character that indicates a comment line in a CSV / TSV file.
 *   Default: `'#'`.
 * @param headerComments A list of comments to include at the beginning of the CSV / TSV file.
 *   Default: empty list.
 * @param recordSeparator The character that separates records in a CSV / TSV file.
 *   Default: `'\n'`, a Unix-newline.
 * @param adjustCsvFormat Optional extra [CSVFormat] configuration. Default: `{ it }`.
 *
 *   Before instantiating the [CSVFormat], the [CSVFormat.Builder] will be passed to this lambda.
 *   This will allow you to configure/overwrite any CSV / TSV writing options.
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
