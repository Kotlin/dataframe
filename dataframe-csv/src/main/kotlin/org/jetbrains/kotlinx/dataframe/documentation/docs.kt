@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.ReadDelim.FileTypeArg
import org.jetbrains.kotlinx.dataframe.documentation.ReadDelim.FileTypeTitleArg
import org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readDelim
import org.jetbrains.kotlinx.dataframe.io.readTSV
import org.jetbrains.kotlinx.dataframe.io.readTsv
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams
import java.io.File
import java.io.InputStream
import java.net.URL

/**
 * ### Reading $[FileTypeTitleArg] Files to [DataFrame]
 *
 * _**NOTE**: This is a new set of functions, replacing the old $[OldFunctionLinkArg]`()` functions.
 * They'll hopefully prove faster and better in the future. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][ExperimentalCsv] to be able to use them._
 *
 * You can read any $[FileTypeArg] by [File][File], [URL][URL], or [InputStream][InputStream] to a [DataFrame][DataFrame]
 * using the set of $[FunctionLinkArg]`()` functions. Reading by file path or URL can also be done by passing
 * a [String].
 *
 * For example, $[FunctionLinkArg]`("input.$[FileExtensionArg]")`
 *
 * or $[FunctionLinkArg]`(`[File][File]`("input.$[FileExtensionArg]"))`
 *
 * ZIP (.zip) or GZIP (.gz) files are supported by default. \[compression\] is automatically detected.
 */
internal interface ReadDelim {

    /**
     * @include [ReadDelim]
     * @set [FileTypeTitleArg] CSV
     * @set [FileTypeArg] CSV
     * @set [FileExtensionArg] csv
     * @set [FunctionLinkArg] [`DataFrame.readCsv`][readCsv]
     * @set [OldFunctionLinkArg] [`DataFrame.readCSV`][readCSV]
     */
    interface CsvDocs

    /**
     * @include [ReadDelim]
     * @set [FileTypeTitleArg] TSV
     * @set [FileTypeArg] TSV
     * @set [FileExtensionArg] tsv
     * @set [FunctionLinkArg] [`DataFrame.readTsv`][readTsv]
     * @set [OldFunctionLinkArg] [`DataFrame.readTSV`][readTSV]
     */
    interface TsvDocs

    /**
     * @include [ReadDelim]
     * @set [FileTypeTitleArg] Delimiter-Separated Text
     * @set [FileTypeArg] delimiter-separated text
     * @set [FileExtensionArg] txt
     * @set [FunctionLinkArg] [`DataFrame.readDelim`][readDelim]
     * @set [OldFunctionLinkArg] [`DataFrame.readDelim`][readDelim]{@comment cannot differentiate between old and new}
     */
    interface DelimDocs

    /**
     * @include [DelimParams.HEADER]
     * @include [DelimParams.COMPRESSION]
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
    interface CommonParams

    // Like "CSV" or "TSV", capitalized
    interface FileTypeTitleArg

    // Like "CSV" or "TSV"
    interface FileTypeArg

    // like csv or txt
    interface FileExtensionArg

    // A link to the main function
    interface FunctionLinkArg

    // A link to the old function
    interface OldFunctionLinkArg
}
