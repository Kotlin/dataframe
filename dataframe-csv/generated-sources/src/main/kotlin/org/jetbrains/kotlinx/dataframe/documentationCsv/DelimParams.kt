package org.jetbrains.kotlinx.dataframe.documentationCsv

import io.deephaven.csv.CsvSpecs
import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.impl.io.typesDeephavenAlreadyParses
import org.jetbrains.kotlinx.dataframe.io.AdjustCSVFormat
import org.jetbrains.kotlinx.dataframe.io.AdjustCsvSpecs
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.Compression
import org.jetbrains.kotlinx.dataframe.io.DefaultNullStringsContentLink
import org.jetbrains.kotlinx.dataframe.io.QuoteMode
import java.nio.charset.Charset

/**
 * Contains both the default values of csv/tsv parameters and the parameter KDocs.
 */
@Suppress("ktlint:standard:class-naming", "ClassName", "KDocUnresolvedReference")
internal object DelimParams {

    /**
     * @param path The file path to read.
     *   Use [charset] to specify the encoding.
     *   Can also be compressed as `.gz` or `.zip`, see [Compression][Compression].
     */
    interface PATH_READ

    /**
     * @param file The file to read.
     *   Use [charset] to specify the encoding.
     *   Can also be compressed as `.gz` or `.zip`, see [Compression][Compression].
     */
    interface FILE_READ

    /**
     * @param url The URL from which to fetch the data.
     *   Use [charset] to specify the encoding.
     *   Can also be compressed as `.gz` or `.zip`, see [Compression][Compression].
     */
    interface URL_READ

    /**
     * @param fileOrUrl The file path or URL to read the data from.
     *   Use [charset] to specify the encoding.
     *   Can also be compressed as `.gz` or `.zip`, see [Compression][Compression].
     */
    interface FILE_OR_URL_READ

    /**
     * @param inputStream Represents the file to read.
     *   Use [charset] to specify the encoding.
     */
    interface INPUT_STREAM_READ

    /** @param text The raw data to read in the form of a [String]. */
    interface TEXT_READ

    /** @param file The file to write to. */
    interface FILE_WRITE

    /** @param path The path pointing to a file to write to. */
    interface PATH_WRITE

    /** @param writer The [Appendable] to write to. */
    interface WRITER_WRITE

    /**
     * @param charset The [character set][java.nio.charset.Charset] the input is encoded in.
     *   Default: `null`
     *
     *   If `null`, the Charset will be read from the BOM of the provided input,
     *   defaulting to [UTF-8][Charsets.UTF_8] if no BOM is found.
     */
    val CHARSET: Charset? = null

    /**
     * @param delimiter The field delimiter character. Default: ','.
     *
     *   Ignored if [hasFixedWidthColumns] is `true`.
     */
    const val CSV_DELIMITER: Char = ','

    /**
     * @param delimiter The field delimiter character. Default: '\t'.
     *
     *   Ignored if [hasFixedWidthColumns] is `true`.
     */
    const val TSV_DELIMITER: Char = '\t'

    /**
     * @param delimiter The field delimiter character. Default: ','.
     *
     *   Ignored if [hasFixedWidthColumns] is `true`.
     */
    const val DELIM_DELIMITER: Char = ','

    /**
     * @param header Optional column titles. Default: empty list.
     *
     *   If non-empty, the data will be read with [header] as the column titles
     *   (use [skipLines] if there's a header in the data).
     *   If empty (default), the header will be read from the data.
     */
    val HEADER: List<String> = emptyList()

    /**
     * @param hasFixedWidthColumns Whether the data has fixed-width columns instead of a single delimiter.
     *   Default: `false`.
     *
     *   Fixed-width columns can occur, for instance, in multi-space delimited data, where the columns are separated
     *   by multiple spaces instead of a single delimiter, so columns are visually aligned.
     *   Column widths are determined by the header in the data (if present), or manually by setting
     *   [fixedColumnWidths].
     */
    const val HAS_FIXED_WIDTH_COLUMNS: Boolean = false

    /**
     * @param fixedColumnWidths The fixed column widths. Default: empty list.
     *
     *   Requires [hasFixedWidthColumns]. If empty, the column widths will be determined by the header in the data
     *   (if present), else, this manually sets the column widths.
     *   The number of widths should match the number of columns.
     */
    val FIXED_COLUMN_WIDTHS: List<Int> = emptyList()

    /**
     * @param compression The compression of the data.
     *   Default: [Compression.None], unless detected otherwise from the input file or url.
     */
    val COMPRESSION: Compression<*> = Compression.None

    /**
     * @param colTypes The expected [ColType] per column name. Default: empty map, a.k.a. infer every column type.
     *
     *   If supplied for a certain column name (inferred from data or given by [header]),
     *   the parser will parse the column with the specified name as the specified type, else it will infer the type.
     *
     *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
     *   You can also set [ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.X`
     *   to set a _default_ column type, like [ColType.String].
     */
    val COL_TYPES: Map<String, ColType> = emptyMap()

    /**
     * @param skipLines The number of lines to skip before reading the header and data. Default: `0`.
     *
     *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
     */
    const val SKIP_LINES: Long = 0L

    /**
     * @param readLines The maximum number of lines to read from the data. Default: `null`.
     *
     *   If `null`, all lines will be read.
     */
    val READ_LINES: Long? = null

    /**
     * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
     *   Default, `null`.
     *
     *   Can configure locale, date format, double parsing, skipping types, etc.
     *
     *   If [parserOptions] or any of the arguments are `null`, the global parser configuration
     *   ([DataFrame.parser][DataFrame.Companion.parser]) will be queried.
     *
     *   The only exceptions are:
     *   - [nullStrings][ParserOptions.nullStrings], which, if `null`,
     *   will take the global setting + [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_DELIM_NULL_STRINGS].
     *   - [skipTypes][ParserOptions.skipTypes], which will always add [typesDeephavenAlreadyParses] to
     *   the given types or the global setting.
     */
    val PARSER_OPTIONS: ParserOptions? = null

    /**
     * @param ignoreEmptyLines Whether to skip intermediate empty lines. Default: `false`.
     *
     *   If `false`, empty lines will be interpreted as having _empty_ values if [allowMissingColumns].
     */
    const val IGNORE_EMPTY_LINES: Boolean = false

    /**
     * @param allowMissingColumns Whether to allow rows with fewer columns than the header. Default: `true`.
     *
     *   If `true`, rows that are too short will be interpreted as _empty_ values.
     */
    const val ALLOW_MISSING_COLUMNS: Boolean = true

    /**
     * @param ignoreExcessColumns Whether to ignore rows with more columns than the header. Default: `true`.
     *
     *   If `true`, rows that are too long will have those columns dropped.
     */
    const val IGNORE_EXCESS_COLUMNS: Boolean = true

    /**
     * @param quote The quote character. Default: `"`.
     *
     *   Used when field- or line delimiters should be interpreted as literal text.
     *
     *   For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
     */
    const val QUOTE: Char = '"'

    /**
     * @param ignoreSurroundingSpaces Whether to ignore leading and trailing blanks around non-quoted fields.
     *   Default: `true`.
     */
    const val IGNORE_SURROUNDING_SPACES: Boolean = true

    /**
     * @param trimInsideQuoted Whether to ignore leading and trailing blanks inside quoted fields.
     *   Default: `false`.
     */
    const val TRIM_INSIDE_QUOTED: Boolean = false

    /**
     * @param parseParallel Whether to parse the data in parallel. Default: `true`.
     *
     *   If `true`, the data will be read and parsed in parallel by the Deephaven parser.
     *   This is usually faster but can be turned off for debugging.
     */
    const val PARSE_PARALLEL: Boolean = true

    /**
     * @param adjustCsvSpecs Optional extra [CsvSpecs] configuration. Default: `{ it }`.
     *
     *   Before instantiating the [CsvSpecs], the [CsvSpecs.Builder] will be passed to this lambda.
     *   This will allow you to configure/overwrite any CSV / TSV parsing options.
     */
    val ADJUST_CSV_SPECS: AdjustCsvSpecs = { it }

    /** @param includeHeader Whether to include the header in the output. Default: `true`. */
    const val INCLUDE_HEADER: Boolean = true

    /**
     * @param quoteMode The [QuoteMode] to use when writing CSV / TSV files.
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
     *   Default: `'\n'`, a Unix-newline.
     */
    const val RECORD_SEPARATOR: String = "\n"

    /**
     * @param headerComments A list of comments to include at the beginning of the CSV / TSV file.
     *   Default: empty list.
     */
    val HEADER_COMMENTS: List<String> = emptyList()

    /**
     * @param adjustCsvFormat Optional extra [CSVFormat] configuration. Default: `{ it }`.
     *
     *   Before instantiating the [CSVFormat], the [CSVFormat.Builder] will be passed to this lambda.
     *   This will allow you to configure/overwrite any CSV / TSV writing options.
     */
    val ADJUST_CSV_FORMAT: AdjustCSVFormat = { it }
}
