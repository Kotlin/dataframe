@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentationCsv

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.QuoteMode.ALL
import java.io.File

/**
 * ### $[WRITE_OR_CONVERT] [DataFrame] to $[FILE_TYPE_TITLE] $[DATA_TITLE]
 *
 * ${[WRITE_OR_CONVERT]}s \[this\]\[this\] [DataFrame][DataFrame] to a $[FILE_TYPE] $[DATA].
 *
 * Parameters you can use to customize the process include, for instance, \[delimiter\],
 * \[includeHeader\], \[quoteMode\], and \[headerComments\].
 * See the param list below for all settings.
 *
 * The integration is built upon {@include [DocumentationUrls.ApacheCsv]}.
 *
 * ##### Similar Functions
 * With overloads of $[FUNCTION_LINK]`()`, you can write $[FILE_TYPE] to [File][File], [Path][java.nio.file.Path],
 * [Appendable], or [String].
 *
 * For example, $[FUNCTION_LINK]`("output.$[CommonWriteDelimDocs.FILE_EXTENSION]")`
 *
 * or $[FUNCTION_LINK]`(`[File][File]`("output.$[CommonWriteDelimDocs.FILE_EXTENSION]"), quoteMode = `[QuoteMode.ALL][ALL]`)`
 *
 * Converting to a [String] can be done like this:
 *
 * $[TO_STR_FUNCTION_LINK]`(delimiter = ",")`
 *
 * @comment Some helper arguments for the function links
 * @set [FUNCTION_LINK] \[DataFrame.${[FUNCTION_NAME]}\]\[${[FUNCTION_NAME]}\]
 * @set [TO_STR_FUNCTION_LINK] \[DataFrame.${[TO_STR_FUNCTION_NAME]}\]\[${[TO_STR_FUNCTION_NAME]}\]
 */
@Suppress("ClassName")
internal interface CommonWriteDelimDocs {

    /**
     * @include [CommonWriteDelimDocs]
     * @set [FILE_TYPE_TITLE] CSV
     * @set [FILE_TYPE] CSV
     * @set [FILE_EXTENSION] csv
     * @set [FUNCTION_NAME] writeCsv
     * @set [TO_STR_FUNCTION_NAME] toCsvStr
     */
    typealias CsvDocs = Nothing

    /**
     * @include [CommonWriteDelimDocs]
     * @set [FILE_TYPE_TITLE] TSV
     * @set [FILE_TYPE] TSV
     * @set [FILE_EXTENSION] tsv
     * @set [FUNCTION_NAME] writeTsv
     * @set [TO_STR_FUNCTION_NAME] toTsvStr
     */
    typealias TsvDocs = Nothing

    /**
     * @include [CommonWriteDelimDocs]
     * @set [FILE_TYPE_TITLE] Delimiter-Separated Text
     * @set [FILE_TYPE] delimiter-separated text
     * @set [FILE_EXTENSION] txt
     * @set [FUNCTION_NAME] writeDelim
     * @set [TO_STR_FUNCTION_NAME] toDelimStr
     */
    typealias DelimDocs = Nothing

    /**
     * @include [DelimParams.INCLUDE_HEADER]
     * @include [DelimParams.QUOTE]
     * @include [DelimParams.QUOTE_MODE]
     * @include [DelimParams.ESCAPE_CHAR]
     * @include [DelimParams.COMMENT_CHAR]
     * @include [DelimParams.HEADER_COMMENTS]
     * @include [DelimParams.RECORD_SEPARATOR]
     */
    typealias CommonWriteParams = Nothing

    // something like "Write" or "Convert"
    typealias WRITE_OR_CONVERT = Nothing

    // Like "CSV" or "TSV", capitalized
    typealias FILE_TYPE_TITLE = Nothing

    // something like "File" or "String"
    typealias DATA_TITLE = Nothing

    // something like "file" or "text"
    typealias DATA = Nothing

    // Like "CSV" or "TSV"
    typealias FILE_TYPE = Nothing

    // like "csv" or "txt"
    typealias FILE_EXTENSION = Nothing

    // Function name, like "readCsv"
    typealias FUNCTION_NAME = Nothing

    // Function name, like "toCsvStr"
    typealias TO_STR_FUNCTION_NAME = Nothing

    // A link to the main function, set by WriteDelim itself
    typealias FUNCTION_LINK = Nothing

    // A link to the str function, set by WriteDelim itself
    typealias TO_STR_FUNCTION_LINK = Nothing
}
