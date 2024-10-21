@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.io.DelimParams
import org.jetbrains.kotlinx.dataframe.io.ExperimentalCsv
import org.jetbrains.kotlinx.dataframe.io.QuoteMode.ALL
import org.jetbrains.kotlinx.dataframe.io.toCsv
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

/**
 * ### $[WriteOrConvertArg] [DataFrame] to $[FileTypeTitleArg] $[DataTitleArg]
 *
 * ${[WriteOrConvertArg]}s [this] [DataFrame] to a $[FileTypeArg] $[DataArg].
 *
 * Parameters you can use to customize the process include, for instance, \[delimiter\],
 * \[includeHeader\], \[quoteMode\], and \[headerComments\].
 * See the param list below for all settings.
 *
 * The integration is built upon {@include [DocumentationUrls.ApacheCsv]}.
 *
 * ##### Similar Functions
 * With overloads of $[FunctionLinkArg]`()`, you can write $[FileTypeArg] to [File][File],
 * [Appendable], or [String].
 *
 * For example, $[FunctionLinkArg]`("output.$[CommonWriteDelimDocs.FileExtensionArg]")`
 *
 * or $[FunctionLinkArg]`(`[File][File]`("output.$[CommonWriteDelimDocs.FileExtensionArg]"), quoteMode = `[QuoteMode.ALL][ALL]`)`
 *
 * Converting to a [String] can be done like this:
 *
 * $[ToStrFunctionLinkArg]`(delimiter = ",")`
 *
 * _**NOTE EXPERIMENTAL**: This is a new set of functions, replacing the old
 * [DataFrame.writeCSV][writeCSV]`()` and [DataFrame.toCsv][toCsv]`()` functions.
 * They'll hopefully be better. Until they are proven to be so,
 * you'll need to [opt in][OptIn] to [ExperimentalCsv][ExperimentalCsv] to be able to use them._
 *
 * @comment Some helper arguments for the function links
 * @set [FunctionLinkArg] \[DataFrame.${[FunctionNameArg]}\]\[${[FunctionNameArg]}\]
 * @set [ToStrFunctionLinkArg] \[DataFrame.${[ToStrFunctionNameArg]}\]\[${[ToStrFunctionNameArg]}\]
 */
internal interface CommonWriteDelimDocs {

    /**
     * @include [CommonWriteDelimDocs]
     * @set [FileTypeTitleArg] CSV
     * @set [FileTypeArg] CSV
     * @set [FileExtensionArg] csv
     * @set [FunctionNameArg] writeCsv
     * @set [ToStrFunctionNameArg] toCsvStr
     */
    interface CsvDocs

    /**
     * @include [CommonWriteDelimDocs]
     * @set [FileTypeTitleArg] TSV
     * @set [FileTypeArg] TSV
     * @set [FileExtensionArg] tsv
     * @set [FunctionNameArg] writeTsv
     * @set [ToStrFunctionNameArg] toTsvStr
     */
    interface TsvDocs

    /**
     * @include [CommonWriteDelimDocs]
     * @set [FileTypeTitleArg] Delimiter-Separated Text
     * @set [FileTypeArg] delimiter-separated text
     * @set [FileExtensionArg] txt
     * @set [FunctionNameArg] writeDelim
     * @set [ToStrFunctionNameArg] toDelimStr
     */
    interface DelimDocs

    /**
     * @include [DelimParams.INCLUDE_HEADER]
     * @include [DelimParams.QUOTE]
     * @include [DelimParams.QUOTE_MODE]
     * @include [DelimParams.ESCAPE_CHAR]
     * @include [DelimParams.COMMENT_CHAR]
     * @include [DelimParams.HEADER_COMMENTS]
     * @include [DelimParams.RECORD_SEPARATOR]
     */
    interface CommonWriteParams

    // something like "Write" or "Convert"
    interface WriteOrConvertArg

    // Like "CSV" or "TSV", capitalized
    interface FileTypeTitleArg

    // something like "File" or "String"
    interface DataTitleArg

    // something like "file" or "text"
    interface DataArg

    // Like "CSV" or "TSV"
    interface FileTypeArg

    // like "csv" or "txt"
    interface FileExtensionArg

    // Function name, like "readCsv"
    interface FunctionNameArg

    // Function name, like "toCsvStr"
    interface ToStrFunctionNameArg

    // A link to the main function, set by WriteDelim itself
    interface FunctionLinkArg

    // A link to the str function, set by WriteDelim itself
    interface ToStrFunctionLinkArg
}
