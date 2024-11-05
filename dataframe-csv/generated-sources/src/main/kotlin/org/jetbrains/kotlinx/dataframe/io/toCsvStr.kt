package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADDITIONAL_CSV_FORMAT
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COMMENT_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.CSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ESCAPE_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HEADER_COMMENTS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.INCLUDE_HEADER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE_MODE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.RECORD_SEPARATOR
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl

/**
 * ### Convert [DataFrame] to CSV String
 *
 * Converts [this] [DataFrame] to a CSV [String].
 *
 * Parameters you can use to customize the process include, for instance, [delimiter],
 * [includeHeader], [quoteMode], and [headerComments].
 * See the param list below for all settings.
 *
 * The integration is built upon [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/).
 *
 * ##### Similar Functions
 * With overloads of [DataFrame.writeCsv][writeCsv]`()`, you can write CSV to [File][File],
 * [Appendable], or [String].
 *
 * For example, [DataFrame.writeCsv][writeCsv]`("output.csv")`
 *
 * or [DataFrame.writeCsv][writeCsv]`(`[File][File]`("output.csv"), quoteMode = `[QuoteMode.ALL][org.jetbrains.kotlinx.dataframe.io.QuoteMode.ALL]`)`
 *
 * Converting to a [String] can be done like this:
 *
 * [DataFrame.toCsvStr][toCsvStr]`(delimiter = ",")`
 *
 * _**NOTE EXPERIMENTAL**: This is a new set of functions, replacing the old
 * [DataFrame.writeCSV][writeCSV]`()` and [DataFrame.toCsv][toCsv]`()` functions.
 * They'll hopefully be better. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv] to be able to use them._
 *
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
 */
@ExperimentalCsv
public fun AnyFrame.toCsvStr(
    delimiter: Char = CSV_DELIMITER,
    includeHeader: Boolean = INCLUDE_HEADER,
    quote: Char? = QUOTE,
    quoteMode: QuoteMode = QUOTE_MODE,
    escapeChar: Char? = ESCAPE_CHAR,
    commentChar: Char? = COMMENT_CHAR,
    headerComments: List<String> = HEADER_COMMENTS,
    recordSeparator: String = RECORD_SEPARATOR,
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
            additionalCsvFormat = ADDITIONAL_CSV_FORMAT,
        )
    }
