package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.documentationCsv.CommonWriteDelimDocs
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.ADJUST_CSV_FORMAT
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.COMMENT_CHAR
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.DELIM_DELIMITER
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.ESCAPE_CHAR
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.HEADER_COMMENTS
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.INCLUDE_HEADER
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.QUOTE_MODE
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.RECORD_SEPARATOR
import org.jetbrains.kotlinx.dataframe.impl.io.writeDelimImpl

/**
 * ### Convert [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to Delimiter-Separated Text String
 *
 * Converts [this][this] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to a delimiter-separated text [<code>String</code>][String].
 *
 * Parameters you can use to customize the process include, for instance, [delimiter],
 * [includeHeader], [quoteMode], and [headerComments].
 * See the param list below for all settings.
 *
 * The integration is built upon [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/).
 *
 * ##### Similar Functions
 * With overloads of [DataFrame.writeDelim][writeDelim]`()`, you can write delimiter-separated text to [<code>File</code>][File], [<code>Path</code>][java.nio.file.Path],
 * [<code>Appendable</code>][Appendable], or [<code>String</code>][String].
 *
 * For example, [DataFrame.writeDelim][writeDelim]`("output.txt")`
 *
 * or [DataFrame.writeDelim][writeDelim]`(`[<code>File</code>][File]`("output.txt"), quoteMode = `[<code>QuoteMode.ALL</code>][org.jetbrains.kotlinx.dataframe.io.QuoteMode.ALL]`)`
 *
 * Converting to a [<code>String</code>][String] can be done like this:
 *
 * [DataFrame.toDelimStr][toDelimStr]`(delimiter = ",")`
 *
 * @param delimiter The field delimiter character. Default: ','.
 *
 *   Ignored if [hasFixedWidthColumns] is `true`.
 * @param includeHeader Whether to include the header in the output. Default: `true`.
 * @param quote The quote character. Default: `"`.
 *
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 *   For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * @param quoteMode The [<code>QuoteMode</code>][org.jetbrains.kotlinx.dataframe.io.QuoteMode] to use when writing CSV / TSV files.
 *   Default: [<code>QuoteMode.MINIMAL</code>][org.jetbrains.kotlinx.dataframe.io.QuoteMode.MINIMAL].
 * @param escapeChar The escape character to use when writing CSV / TSV files with [<code>QuoteMode.NONE</code>][org.jetbrains.kotlinx.dataframe.io.QuoteMode.NONE].
 *   Default: `null`. This will double-quote the value.
 * @param commentChar The character that indicates a comment line in a CSV / TSV file.
 *   Default: `'#'`.
 * @param headerComments A list of comments to include at the beginning of the CSV / TSV file.
 *   Default: empty list.
 * @param recordSeparator The character that separates records in a CSV / TSV file.
 *   Default: `'\n'`, a Unix-newline.
 */
public fun AnyFrame.toDelimStr(
    delimiter: Char = DELIM_DELIMITER,
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
            df = this@toDelimStr,
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
