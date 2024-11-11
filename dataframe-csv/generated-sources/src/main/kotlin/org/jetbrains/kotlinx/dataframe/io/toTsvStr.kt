package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADJUST_CSV_FORMAT
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COMMENT_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ESCAPE_CHAR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HEADER_COMMENTS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.INCLUDE_HEADER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE_MODE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.RECORD_SEPARATOR
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.TSV_DELIMITER
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl

/**
 * ### Convert [DataFrame] to TSV String
 *
 * Converts [this] [DataFrame] to a TSV [String].
 *
 * Parameters you can use to customize the process include, for instance, [delimiter],
 * [includeHeader], [quoteMode], and [headerComments].
 * See the param list below for all settings.
 *
 * The integration is built upon [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/).
 *
 * ##### Similar Functions
 * With overloads of [DataFrame.writeTsv][writeTsv]`()`, you can write TSV to [File][File],
 * [Appendable], or [String].
 *
 * For example, [DataFrame.writeTsv][writeTsv]`("output.tsv")`
 *
 * or [DataFrame.writeTsv][writeTsv]`(`[File][File]`("output.tsv"), quoteMode = `[QuoteMode.ALL][org.jetbrains.kotlinx.dataframe.io.QuoteMode.ALL]`)`
 *
 * Converting to a [String] can be done like this:
 *
 * [DataFrame.toTsvStr][toTsvStr]`(delimiter = ",")`
 *
 * _**NOTE EXPERIMENTAL**: This is a new set of functions, replacing the old
 * [DataFrame.writeCSV][writeCSV]`()` and [DataFrame.toCsv][toCsv]`()` functions.
 * They'll hopefully be better. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv] to be able to use them._
 *
 * @param delimiter The field delimiter character. Default: '\t'.
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
public fun AnyFrame.toTsvStr(
    includeHeader: Boolean = INCLUDE_HEADER,
    delimiter: Char = TSV_DELIMITER,
    quote: Char? = QUOTE,
    quoteMode: QuoteMode = QUOTE_MODE,
    escapeChar: Char? = ESCAPE_CHAR,
    commentChar: Char? = COMMENT_CHAR,
    headerComments: List<String> = HEADER_COMMENTS,
    recordSeparator: String = RECORD_SEPARATOR,
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
            adjustCsvFormat = ADJUST_CSV_FORMAT,
        )
    }
