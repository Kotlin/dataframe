@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentationCsv

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams.CHARSET
import org.jetbrains.kotlinx.dataframe.io.ColType
import java.io.File
import java.io.InputStream
import java.net.URL

/**
 * ### Read $[FILE_TYPE_TITLE] $[DATA_TITLE] to [DataFrame]
 *
 * Reads any $[FILE_TYPE] $[DATA] to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, \[delimiter\],
 * \[header\], \[colTypes\], \[readLines\], and \[parserOptions\].
 * See the param list below for all settings.
 *
 * The integration is built upon {@include [DocumentationUrls.Deephaven]}.
 *
 * ##### Similar Functions
 * With the overloads of $[FUNCTION_LINK]`()`, you can read any $[FILE_TYPE] by [File][File],
 * [Path][java.nio.file.Path], [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, $[FUNCTION_LINK]`("input.$[CommonReadDelimDocs.FILE_EXTENSION]")` or with some options:
 *
 * $[FUNCTION_LINK]`(`
 *
 * {@include [Indent]}`file = `[File][File]`("input.$[CommonReadDelimDocs.FILE_EXTENSION]"),`
 *
 * {@include [Indent]}`parserOptions = `[ParserOptions][org.jetbrains.kotlinx.dataframe.api.ParserOptions]`(locale = `[Locale][java.util.Locale]`.`[US][java.util.Locale.US]`),`
 *
 * {@include [Indent]}`colTypes = `[mapOf][mapOf]`("a" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`, `[ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.`[String][ColType.String]`),`
 *
 * {@include [Indent]}`readLines = 1000L,`
 *
 * `)`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. \[compression\] is automatically detected.
 *
 * You can also read "raw" $[FILE_TYPE] data from a [String] like this:
 *
 * $[STR_FUNCTION_LINK]`("a,b,c", delimiter = ",")`
 *
 * @comment Some helper arguments for the function links
 * @set [FUNCTION_LINK] \[DataFrame.${[FUNCTION_NAME]}\]\[${[FUNCTION_NAME]}\]
 * @set [STR_FUNCTION_LINK] \[DataFrame.${[FUNCTION_NAME]}Str\]\[${[FUNCTION_NAME]}Str\]
 * @set [OLD_FUNCTION_LINK] \[DataFrame.${[OLD_FUNCTION_NAME]}\]\[org.jetbrains.kotlinx.dataframe.io.${[OLD_FUNCTION_NAME]}\]
 */
@Suppress("ClassName")
internal interface CommonReadDelimDocs {

    /**
     * @include [CommonReadDelimDocs]
     * @set [FILE_TYPE_TITLE] CSV
     * @set [FILE_TYPE] CSV
     * @set [FILE_EXTENSION] csv
     * @set [FUNCTION_NAME] readCsv
     * @set [OLD_FUNCTION_NAME] readCSV
     */
    interface CsvDocs

    /**
     * @include [CommonReadDelimDocs]
     * @set [FILE_TYPE_TITLE] TSV
     * @set [FILE_TYPE] TSV
     * @set [FILE_EXTENSION] tsv
     * @set [FUNCTION_NAME] readTsv
     * @set [OLD_FUNCTION_NAME] readTSV
     */
    interface TsvDocs

    /**
     * @include [CommonReadDelimDocs]
     * @set [FILE_TYPE_TITLE] Delimiter-Separated Text
     * @set [FILE_TYPE] delimiter-separated text
     * @set [FILE_EXTENSION] txt
     * @set [FUNCTION_NAME] readDelim
     * @set [OLD_FUNCTION_NAME] readDelim{@comment cannot differentiate between old and new}
     */
    interface DelimDocs

    /**
     * @include [CHARSET]
     * @include [DelimParams.HEADER]
     * @include [DelimParams.HAS_FIXED_WIDTH_COLUMNS]
     * @include [DelimParams.FIXED_COLUMN_WIDTHS]
     * @include [DelimParams.COL_TYPES]
     * @include [DelimParams.SKIP_LINES]
     * @include [DelimParams.READ_LINES]
     * @include [DelimParams.PARSER_OPTIONS]
     * @include [DelimParams.IGNORE_EMPTY_LINES]
     * @include [DelimParams.ALLOW_MISSING_COLUMNS]
     * @include [DelimParams.IGNORE_EXCESS_COLUMNS]
     * @include [DelimParams.QUOTE]
     * @include [DelimParams.IGNORE_SURROUNDING_SPACES]
     * @include [DelimParams.TRIM_INSIDE_QUOTED]
     * @include [DelimParams.PARSE_PARALLEL]
     */
    interface CommonReadParams

    // something like "File" or "File/URL"
    interface DATA_TITLE

    // something like "file" or "file or url"
    interface DATA

    // Like "CSV" or "TSV", capitalized
    interface FILE_TYPE_TITLE

    // Like "CSV" or "TSV"
    interface FILE_TYPE

    // like "csv" or "txt"
    interface FILE_EXTENSION

    // Function name, like "readCsv"
    interface FUNCTION_NAME

    // Old function name, like "readCSV"
    interface OLD_FUNCTION_NAME

    // A link to the main function, set by ReadDelim itself
    interface FUNCTION_LINK

    // A link to the str function, set by ReadDelim itself
    interface STR_FUNCTION_LINK

    // A link to the old function, set by ReadDelim itself
    interface OLD_FUNCTION_LINK
}
