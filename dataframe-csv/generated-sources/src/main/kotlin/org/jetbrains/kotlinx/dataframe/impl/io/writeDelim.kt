package org.jetbrains.kotlinx.dataframe.impl.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADDITIONAL_CSV_FORMAT
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COMMENT_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ESCAPE_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HEADER_COMMENTS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.INCLUDE_HEADER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE_MODE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.RECORD_SEPARATOR
import org.jetbrains.kotlinx.dataframe.io.QuoteMode
import org.jetbrains.kotlinx.dataframe.io.toJson
import org.apache.commons.csv.QuoteMode as ApacheQuoteMode

/**
 * Writes [df] to [writer] in a delimiter-separated format.
 *
 * @param df The data to write.
 * @param writer The [Appendable] to write to.
 * @param delimiter The field delimiter character. Default: ','.
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
 * @param additionalCsvFormat Optional [CSVFormat]. Default: [CSVFormat.DEFAULT].
 *
 *   A [CSVFormat] instance can be supplied to configure additional CSV / TSV printing options
 *   not covered by the other parameters. The (default) values of other parameters will override the values in
 *   [additionalCsvFormat].
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
