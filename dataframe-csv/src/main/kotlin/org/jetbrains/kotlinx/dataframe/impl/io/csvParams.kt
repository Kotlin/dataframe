package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.CsvSpecs
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE

/**
 * Contains both the default values of csv parameters and the parameter KDocs.
 */
@Suppress("ktlint:standard:class-naming", "ClassName", "KDocUnresolvedReference")
internal object CsvParams {

    /** @param inputStream Represents the CSV file to read. */
    interface INPUT_STREAM

    /** @param delimiter The field delimiter character. ',' for CSV, '\t' for TSV. The default is ','. */
    const val DELIMITER: Char = ','

    /**
     * @param header if empty, the header will be read from the CSV file
     *   else, if not empty, the CSV will be read as header-less with [header] as the column titles.
     *   Combine with [skipLines] if you want to overwrite a CSV header.
     *   The default is an empty list.
     */
    val HEADER: List<String> = emptyList()

    /**
     * @param isCompressed If `true`, the input stream is compressed and will be decompressed before reading.
     *   The default is `false`.
     */
    const val IS_COMPRESSED: Boolean = false

    /**
     * @param colTypes A map of column names to their expected [ColType]s. Can be supplied to force
     *   the parser to interpret a column as a specific type, e.g. `colTypes = mapOf("colName" to ColType.Int)`.
     *   You can also supply a [ColType] for [DEFAULT_COL_TYPE] to set the default column type.
     *   The default is an empty map.
     */
    val COL_TYPES: Map<String, ColType> = emptyMap()

    /**
     * @param skipLines The number of lines to skip before reading the header and data.
     *   Useful for files with metadata or comments at the beginning.
     *   The default is 0.
     */
    const val SKIP_LINES: Long = 0L

    /**
     * @param readLines The number of lines to read from the input stream.
     *   If `null`, all lines will be read.
     *   The default is `null`.
     */
    val READ_LINES: Long? = null

    /**
     * @param parserOptions Optional parsing options for columns initially read as [String].
     *   Types that the CSV reader can already parse are automatically added to [ParserOptions.skipTypes].
     *   The default is are empty [ParserOptions].
     */
    val PARSER_OPTIONS: ParserOptions = ParserOptions()

    /**
     * @param ignoreEmptyLines If `true`, empty lines will be skipped.
     *   The default is `false`.
     */
    const val IGNORE_EMPTY_LINES: Boolean = false

    /**
     * @param allowMissingColumns If this set to `true`, then rows that are too short
     *   (that have fewer columns than the header row) will be interpreted as if the missing columns contained
     *   the empty string.
     *   The default is `false`.
     */
    const val ALLOW_MISSING_COLUMNS: Boolean = false

    /**
     * @param ignoreExcessColumns If this set to `true`, then rows that are too long
     *   (that have more columns than the header row) will have those excess columns dropped.
     *   The default is `false`.
     */
    const val IGNORE_EXCESS_COLUMNS: Boolean = false

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
     * The default is `'"'`.
     */
    const val QUOTE: Char = '"'

    /**
     * @param ignoreSurroundingSpaces If `true`, leading and trailing blanks around non-quoted fields will be trimmed.
     *   The default is `true`.
     */
    const val IGNORE_SURROUNDING_SPACES: Boolean = true

    /**
     * @param trimInsideQuoted If `true`, leading and trailing blanks inside quoted fields will be trimmed.
     *   The default is `false`.
     */
    const val TRIM_INSIDE_QUOTED: Boolean = false

    /**
     * @param parseParallel If `true`, the CSV will be parsed in parallel using `runBlocking`.
     *   The default is `true`.
     */
    const val PARSE_PARALLEL: Boolean = true

    /**
     * @param additionalCsvSpecs Optional [CsvSpecs] object to configure additional
     *   CSV parsing options not covered by the other parameters.
     *   The default is an empty [CsvSpecs].
     */
    val ADDITIONAL_CSV_SPECS: CsvSpecs = CsvSpecs.builder().build()
}
