package org.jetbrains.kotlinx.dataframe.documentation

import io.deephaven.csv.CsvSpecs
import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.Compression
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_PARSER_OPTIONS
import org.jetbrains.kotlinx.dataframe.io.DefaultNullStringsContentLink
import org.jetbrains.kotlinx.dataframe.io.QuoteMode

/**
 * Contains both the default values of csv/tsv parameters and the parameter KDocs.
 */
@Suppress("ktlint:standard:class-naming", "ClassName", "KDocUnresolvedReference")
internal object DelimParams {

    /** @param file The file to read. Can also be compressed as `.gz` or `.zip`, see [Compression]. */
    interface FILE_READ

    /** @param url The URL from which to fetch the data. Can also be compressed as `.gz` or `.zip`, see [Compression]. */
    interface URL_READ

    /** @param fileOrUrl The file path or URL to read the data from. Can also be compressed as `.gz` or `.zip`, see [Compression]. */
    interface FILE_OR_URL_READ

    /** @param inputStream Represents the file to read. */
    interface INPUT_STREAM_READ

    /** @param text The raw data to read in the form of a [String]. */
    interface TEXT_READ

    /** @param file The file to write to. */
    interface FILE_WRITE

    /** @param path The path pointing to a file to write to. */
    interface PATH_WRITE

    /** @param writer The [Appendable] to write to. */
    interface WRITER_WRITE

    /** @param delimiter The field delimiter character. Default: ','. */
    const val CSV_DELIMITER: Char = ','

    /** @param delimiter The field delimiter character. Default: '\\t'. */
    const val TSV_DELIMITER: Char = '\t'

    /** @param delimiter The field delimiter character. Default: ','. */
    const val DELIM_DELIMITER: Char = ','

    /**
     * @param header Optional column titles.
     *   If non-empty, the data will be read with \[header\] as the column titles
     *   (use \[skipLines\] if there's a header in the data).
     *   If empty (default), the header will be read from the data.
     *   Default: empty list.
     */
    val HEADER: List<String> = emptyList()

    /**
     * @param compression The compression of the data.
     *   Default: [Compression.None], unless detected otherwise from the input file or url.
     */
    val COMPRESSION: Compression<*> = Compression.None

    /**
     * @param colTypes The expected [ColType] per column name.
     *   When given, the parser will read the column as that type, else it will infer the type.
     *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
     *   You can also set [DEFAULT_COL_TYPE][DEFAULT_COL_TYPE]` `[to][to]` `[ColType][ColType]`.X`
     *   to set a _default_ column type, like [ColType.String].
     *   Default: empty map, a.k.a. infer every column type.
     */
    val COL_TYPES: Map<String, ColType> = emptyMap()

    /**
     * @param skipLines The number of lines to skip before reading the header and data.
     *   Useful for files with metadata, or comments at the beginning, or to give a custom \[header\].
     *   Default: `0`.
     */
    const val SKIP_LINES: Long = 0L

    /**
     * @param readLines The maximum number of lines to read from the data.
     *   If `null`, all lines will be read.
     *   Default: `null`, reads all lines.
     */
    val READ_LINES: Long? = null

    /**
     * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
     *   Can configure locale, date format, double parsing, skipping types, etc.
     *
     *   **NOTE:** Make sure to use [DEFAULT_PARSER_OPTIONS][DEFAULT_PARSER_OPTIONS]`.copy()` to override the desired options.
     *
     *   Default, [DEFAULT_PARSER_OPTIONS]:
     *
     *   [ParserOptions][ParserOptions]`(`
     *
     *   {@include [Indent]}[nullStrings][ParserOptions.nullStrings]`  =  `{@include [DefaultNullStringsContentLink]}`,`
     *
     *   {@include [Indent]}[useFastDoubleParser][ParserOptions.useFastDoubleParser]` = true,`
     *
     *   `)`
     */
    val PARSER_OPTIONS: ParserOptions = DEFAULT_PARSER_OPTIONS

    /**
     * @param ignoreEmptyLines If `true`, intermediate empty lines will be skipped.
     *   Default: `false`, empty line will be interpreted as _empty_ values if \[allowMissingColumns\].
     */
    const val IGNORE_EMPTY_LINES: Boolean = false

    /**
     * @param allowMissingColumns If `true`, rows that are too short
     *   (fewer columns than the header) will be interpreted as _empty_ values.
     *   Default: `true`.
     */
    const val ALLOW_MISSING_COLUMNS: Boolean = true

    /**
     * @param ignoreExcessColumns If `true`, rows that are too long
     *   (more columns than the header) will have those columns dropped.
     *   Default: `true`.
     */
    const val IGNORE_EXCESS_COLUMNS: Boolean = true

    /**
     * @param quote The quote character.
     *   Used when field- or line delimiters should be interpreted as literal text.
     *
     * For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
     * Default: `"`.
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
     * @param parseParallel If `true`, the data will be parsed in parallel.
     *   Can be turned off for debugging.
     *   Default: `true`.
     */
    const val PARSE_PARALLEL: Boolean = true

    /**
     * @param additionalCsvSpecs Optional [CsvSpecs] to configure additional
     *   parsing options not covered by the other parameters.
     *   The (default) values of other parameters will override the values in \[additionalCsvSpecs\].
     *   Default: `null`.
     */
    val ADDITIONAL_CSV_SPECS: CsvSpecs? = null

    /**
     * @param includeHeader If `true`, the header will be included in the output, else it will not.
     *   Default: `true`.
     */
    const val INCLUDE_HEADER: Boolean = true

    /**
     * @param quoteMode The default [QuoteMode] to use when writing CSV / TSV files.
     *   Default: [QuoteMode.MINIMAL].
     */
    val QUOTE_MODE: QuoteMode = QuoteMode.MINIMAL

    /**
     * @param escapeChar The escape character to use when writing CSV / TSV files with [QuoteMode.NONE].
     *   Default: `null`. This will double-quote the value.
     */
    val ESCAPE_CHAR: Char? = null

    /**
     * @param commentChar The character that indicates a comment line in a CSV / TSV file.
     *   Default: `'#'`.
     */
    const val COMMENT_CHAR: Char = '#'

    /**
     * @param recordSeparator The character that separates records in a CSV / TSV file.
     *   Default: `'\\n'`, a Unix-newline.
     */
    const val RECORD_SEPARATOR: String = "\n"

    /**
     * @param headerComments A list of comments to include at the beginning of the CSV / TSV file.
     *   Default: empty list.
     */
    val HEADER_COMMENTS: List<String> = emptyList()

    /**
     * @param additionalCsvFormat Optional [CSVFormat] object to configure additional CSV / TSV printing options
     *   not covered by the other parameters. The (default) values of other parameters will override the values in
     *   [additionalCsvFormat].
     *   Default: [CSVFormat.DEFAULT].
     */
    val ADDITIONAL_CSV_FORMAT: CSVFormat = CSVFormat.DEFAULT
}
