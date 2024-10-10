package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.CsvSpecs
import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.CsvCompression
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE
import org.jetbrains.kotlinx.dataframe.io.QuoteMode

/**
 * Contains both the default values of csv/tsv parameters and the parameter KDocs.
 */
@Suppress("ktlint:standard:class-naming", "ClassName", "KDocUnresolvedReference")
internal object DelimParams {

    /** @param file The file to read. */
    interface FILE

    /** @param url The URL from which to fetch the data. */
    interface URL

    /** @param fileOrUrl The file or URL to read the data from. */
    interface FILE_OR_URL

    /** @param inputStream Represents the file to read. */
    interface INPUT_STREAM

    /** @param delimiter The field delimiter character. Default: ','. */
    const val CSV_DELIMITER: Char = ','

    /** @param delimiter The field delimiter character. Default: '\t'. */
    const val TSV_DELIMITER: Char = '\t'

    /**
     * @param header If not empty, the data will be read with [header] as the column titles.
     *   If empty, the header will be read from the data.
     *   If you want to overwrite the header from the data, use [skipLines].
     *   Default: empty list.
     */
    val HEADER: List<String> = emptyList()

    /**
     * @param compression Determines the compression of the data.
     *   Default: [CsvCompression.None], unless it can be detected from the input file/url.
     */
    val COMPRESSION: CsvCompression<*> = CsvCompression.None

    /**
     * @param colTypes A map of column names to their expected [ColType]s. Can be supplied to force
     *   the parser to interpret a column as a specific type, e.g. `colTypes = mapOf("colName" to ColType.Int)`.
     *   You can also supply a [ColType] for [DEFAULT_COL_TYPE] to set the default column type.
     *   Default: empty map.
     */
    val COL_TYPES: Map<String, ColType> = emptyMap()

    /**
     * @param skipLines The number of lines to skip before reading the header and data.
     *   Useful for files with metadata or comments at the beginning.
     *   Default: 0.
     */
    const val SKIP_LINES: Long = 0L

    /**
     * @param readLines The number of lines to read from the input stream.
     *   If `null`, all lines will be read.
     *   Default: `null`.
     */
    val READ_LINES: Long? = null

    /**
     * @param parserOptions Optional parsing options for columns initially read as [String].
     *   Default:
     *   ```
     *   ParserOptions(
     *     nullStrings = ["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"],
     *     skipTypes = types Deephaven already parses, like Int, Long, Double, etc.
     *   )
     *   ```
     */
    val PARSER_OPTIONS: ParserOptions = ParserOptions(
        nullStrings = defaultNullStrings,
        skipTypes = typesDeephavenAlreadyParses,
    )

    /**
     * @param ignoreEmptyLines If `true`, intermediate empty lines will be skipped.
     *   Default: `false`.
     */
    const val IGNORE_EMPTY_LINES: Boolean = false

    /**
     * @param allowMissingColumns If this set to `true`, then rows that are too short
     *   (that have fewer columns than the header row) will be interpreted as if the missing columns contained
     *   the empty string.
     *   Default: `true`.
     */
    const val ALLOW_MISSING_COLUMNS: Boolean = true

    /**
     * @param ignoreExcessColumns If this set to `true`, then rows that are too long
     *   (that have more columns than the header row) will have those excess columns dropped.
     *   Default: `true`.
     */
    const val IGNORE_EXCESS_COLUMNS: Boolean = true

    /**
     * @param quote The quote character (used when you want field or line delimiters to be interpreted as literal text.
     *
     * For example: `123,"hello, there",456,`
     *
     * Would be read as the three fields:
     * - `123`
     * - `hello, there`
     * - `456`
     *
     * Default: `'"'`.
     */
    const val QUOTE: Char = '"'

    /**
     * @param ignoreSurroundingSpaces If `true`, leading and trailing blanks around non-quoted fields will be trimmed.
     *   Default: `true`.
     */
    const val IGNORE_SURROUNDING_SPACES: Boolean = true

    /**
     * @param trimInsideQuoted If `true`, leading and trailing blanks inside quoted fields will be trimmed.
     *   Default: `false`.
     */
    const val TRIM_INSIDE_QUOTED: Boolean = false

    /**
     * @param parseParallel If `true`, the CSV will be parsed in parallel using `runBlocking`.
     *   Default: `true`.
     */
    const val PARSE_PARALLEL: Boolean = true

    /**
     * @param additionalCsvSpecs Optional [CsvSpecs] object to configure additional
     *   CSV parsing options not covered by the other parameters. The (default) values of
     *   other parameters will override the values in [additionalCsvSpecs].
     *   Default: empty [CsvSpecs].
     */
    val ADDITIONAL_CSV_SPECS: CsvSpecs = CsvSpecs.builder().build()

    /**
     * @param includeHeader If `true`, the header will be included in the output, else it won't.
     *   Default: `true`.
     */
    const val INCLUDE_HEADER: Boolean = true

    /**
     * @param quoteMode The default [QuoteMode] to use when writing CSV/TSV files.
     *   Default: [QuoteMode.MINIMAL].
     */
    val QUOTE_MODE: QuoteMode = QuoteMode.MINIMAL

    /**
     * @param escapeChar The escape character to use when writing CSV/TSV files with [QuoteMode.NONE].
     *   Default: `null`. This will double-quote the value.
     */
    val ESCAPE_CHAR: Char? = null

    /**
     * @param commentChar The character that indicates a comment line in a CSV/TSV file.
     *   Default: `'#'`.
     */
    const val COMMENT_CHAR: Char = '#'

    /**
     * @param recordSeparator The character that separates records in a CSV/TSV file.
     *   Default: `'\n'`.
     */
    const val RECORD_SEPARATOR: String = "\n"

    /**
     * @param headerComments A list of comments to include at the beginning of the CSV/TSV file.
     *   Default: empty list.
     */
    val HEADER_COMMENTS: List<String> = emptyList()

    /**
     * @param additionalCsvFormat Optional [CSVFormat] object to configure additional CSV/TSV printing options
     *   not covered by the other parameters. The (default) values of other parameters will override the values in
     *   [additionalCsvFormat].
     *   Default: [CSVFormat.DEFAULT].
     */
    val ADDITIONAL_CSV_FORMAT: CSVFormat = CSVFormat.DEFAULT
}
