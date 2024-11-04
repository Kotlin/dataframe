@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.CommonReadDelimDocs.DataTitleArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonReadDelimDocs.FileTypeArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonReadDelimDocs.FileTypeTitleArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonReadDelimDocs.FunctionLinkArg
import org.jetbrains.kotlinx.dataframe.documentation.CommonReadDelimDocs.OldFunctionLinkArg
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_PARSER_OPTIONS
import org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.Locale

/**
 * ### Read $[FileTypeTitleArg] $[DataTitleArg] to [DataFrame]
 *
 * Reads any $[FileTypeArg] $[DataArg] to a [DataFrame][DataFrame].
 *
 * Parameters you can use to customize the reading process include, for instance, \[delimiter\],
 * \[header\], \[colTypes\], \[readLines\], and \[parserOptions\].
 * See the param list below for all settings.
 *
 * The integration is built upon {@include [DocumentationUrls.Deephaven]}.
 *
 * ##### Similar Functions
 * With the overloads of $[FunctionLinkArg]`()`, you can read any $[FileTypeArg] by [File][File],
 * [URL][URL], or [InputStream][InputStream].
 * Reading by file path or URL can also be done by passing a [String].
 *
 * For example, $[FunctionLinkArg]`("input.$[CommonReadDelimDocs.FileExtensionArg]")` or with some options:
 *
 * $[FunctionLinkArg]`(`
 *
 * {@include [Indent]}`file = `[File][File]`("input.$[CommonReadDelimDocs.FileExtensionArg]"),`
 *
 * {@include [Indent]}`parserOptions = `[DEFAULT_PARSER_OPTIONS][DEFAULT_PARSER_OPTIONS]`.copy(locale = `[Locale][java.util.Locale]`.`[US][java.util.Locale.US]`),`
 *
 * {@include [Indent]}`colTypes = `[mapOf][mapOf]`("a" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`, `[ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.`[String][ColType.String]`),`
 *
 * {@include [Indent]}`readLines = 1000L,`
 *
 * `)`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. \[compression\] is automatically detected.
 *
 * You can also read "raw" $[FileTypeArg] data from a [String] like this:
 *
 * $[StrFunctionLinkArg]`("a,b,c", delimiter = ",")`
 *
 * _**NOTE EXPERIMENTAL**: This is a new set of functions, replacing the old $[OldFunctionLinkArg]`()` functions.
 * They'll hopefully be faster and better. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][ExperimentalCsv] to be able to use them._
 *
 * @comment Some helper arguments for the function links
 * @set [FunctionLinkArg] \[DataFrame.${[FunctionNameArg]}\]\[${[FunctionNameArg]}\]
 * @set [StrFunctionLinkArg] \[DataFrame.${[FunctionNameArg]}Str\]\[${[FunctionNameArg]}Str\]
 * @set [OldFunctionLinkArg] \[DataFrame.${[OldFunctionNameArg]}\]\[org.jetbrains.kotlinx.dataframe.io.${[OldFunctionNameArg]}\]
 */
internal interface CommonReadDelimDocs {

    /**
     * @include [CommonReadDelimDocs]
     * @set [FileTypeTitleArg] CSV
     * @set [FileTypeArg] CSV
     * @set [FileExtensionArg] csv
     * @set [FunctionNameArg] readCsv
     * @set [OldFunctionNameArg] readCSV
     */
    interface CsvDocs

    /**
     * @include [CommonReadDelimDocs]
     * @set [FileTypeTitleArg] TSV
     * @set [FileTypeArg] TSV
     * @set [FileExtensionArg] tsv
     * @set [FunctionNameArg] readTsv
     * @set [OldFunctionNameArg] readTSV
     */
    interface TsvDocs

    /**
     * @include [CommonReadDelimDocs]
     * @set [FileTypeTitleArg] Delimiter-Separated Text
     * @set [FileTypeArg] delimiter-separated text
     * @set [FileExtensionArg] txt
     * @set [FunctionNameArg] readDelim
     * @set [OldFunctionNameArg] readDelim{@comment cannot differentiate between old and new}
     */
    interface DelimDocs

    /**
     * @include [DelimParams.HEADER]
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
    interface DataTitleArg

    // something like "file" or "file or url"
    interface DataArg

    // Like "CSV" or "TSV", capitalized
    interface FileTypeTitleArg

    // Like "CSV" or "TSV"
    interface FileTypeArg

    // like "csv" or "txt"
    interface FileExtensionArg

    // Function name, like "readCsv"
    interface FunctionNameArg

    // Old function name, like "readCSV"
    interface OldFunctionNameArg

    // A link to the main function, set by ReadDelim itself
    interface FunctionLinkArg

    // A link to the str function, set by ReadDelim itself
    interface StrFunctionLinkArg

    // A link to the old function, set by ReadDelim itself
    interface OldFunctionLinkArg
}
