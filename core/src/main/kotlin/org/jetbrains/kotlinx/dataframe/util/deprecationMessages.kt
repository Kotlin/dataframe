package org.jetbrains.kotlinx.dataframe.util

/*
 * This file contains deprecation messages for the whole core module.
 * After each release, all messages should be reviewed and updated.
 * Level.WARNING -> Level.ERROR
 * Level.ERROR -> Remove
 */

// region WARNING in 0.15, ERROR in 1.0

private const val MESSAGE_1_0 = "Will be ERROR in 1.0."

internal const val DF_READ_NO_CSV = "This function is deprecated and should be replaced with `readCsv`. $MESSAGE_1_0"
internal const val DF_READ_NO_CSV_REPLACE =
    "this.readCsv(fileOrUrl = fileOrUrl, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"

internal const val CREATE_FRAME_COLUMN =
    "Removed from public API as this can likely better be solved by `DataFrame.chunked()`. Replaced by internal df.chunkedImpl(). $MESSAGE_1_0"
internal const val CREATE_FRAME_COLUMN_REPLACE = "df.chunkedImpl(startIndices, name)"
internal const val CHUNKED_IMPL_IMPORT = "org.jetbrains.kotlinx.dataframe.impl.api.chunkedImpl"

internal const val CREATE_WITH_TYPE_INFERENCE =
    "This function is deprecated and should be replaced by `createByInference()`. $MESSAGE_1_0"
internal const val CREATE_WITH_TYPE_INFERENCE_REPLACE =
    "createByInference(name, values, TypeSuggestion.Infer, nullable)"
internal const val CREATE_BY_INFERENCE_IMPORT = "org.jetbrains.kotlinx.dataframe.DataColumn.Companion.createByInference"
internal const val TYPE_SUGGESTION_IMPORT = "org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion"

internal const val CREATE = "This function is deprecated and should be replaced by `createByType()`. $MESSAGE_1_0"
internal const val CREATE_REPLACE = "createByType(name, values, type, infer)"
internal const val CREATE_INLINE_REPLACE = "createByType(name, values, infer)"
internal const val CREATE_BY_TYPE_IMPORT = "org.jetbrains.kotlinx.dataframe.DataColumn.Companion.createByType"

internal const val GUESS_VALUE_TYPE = "This function is just here for binary compatibility. $MESSAGE_1_0"

internal const val CREATE_COLUMN = "This function is just here for binary compatibility. $MESSAGE_1_0"

internal const val GUESS_COLUMN_TYPE = "This function is just here for binary compatibility. $MESSAGE_1_0"

public const val DF_READ_EXCEL: String = "This function is just here for binary compatibility. $MESSAGE_1_0"

internal const val PARSER_OPTIONS = "This constructor is only here for binary compatibility. $MESSAGE_1_0"

internal const val PARSER_OPTIONS_COPY = "This function is only here for binary compatibility. $MESSAGE_1_0"

internal const val IS_COMPARABLE =
    "This function is replaced by `valuesAreComparable()` to better reflect its purpose. $MESSAGE_1_0"
internal const val IS_COMPARABLE_REPLACE = "valuesAreComparable()"
internal const val IS_INTER_COMPARABLE_IMPORT = "org.jetbrains.kotlinx.dataframe.api.valuesAreComparable"

internal const val AS_URL = "This function is replaced by `asUrl()`. $MESSAGE_1_0"
internal const val AS_URL_REPLACE = "asUrl(fileOrUrl)"
internal const val AS_URL_IMPORT = "org.jetbrains.kotlinx.dataframe.io.asUrl"

internal const val IS_URL = "This function is replaced by `isUrl()`. $MESSAGE_1_0"
internal const val IS_URL_REPLACE = "isUrl(path)"
internal const val IS_URL_IMPORT = "org.jetbrains.kotlinx.dataframe.io.isUrl"

internal const val MINUS = "This minus overload will be removed in favor of `remove`. $MESSAGE_1_0"
internal const val MINUS_REPLACE = "this.remove(columns)"

internal const val READ_JSON = "This function is just here for binary compatibility. $MESSAGE_1_0"

internal const val MOVE_TO_LEFT = "This `moveToLeft` overload will be removed in favor of `moveToStart`. $MESSAGE_1_0"
internal const val MOVE_TO_LEFT_REPLACE = "this.moveToStart(columns)"

internal const val MOVE_TO_RIGHT = "This `moveToRight` overload will be removed in favor of `moveToEnd`. $MESSAGE_1_0"
internal const val MOVE_TO_RIGHT_REPLACE = "this.moveToEnd(columns)"

internal const val TO_LEFT = "This `toLeft` overload will be removed in favor of `toStart`. $MESSAGE_1_0"
internal const val TO_LEFT_REPLACE = "this.toStart()"

internal const val TO_RIGHT = "This `toRight` overload will be removed in favor of `toEnd`. $MESSAGE_1_0"
internal const val TO_RIGHT_REPLACE = "this.toEnd()"

internal const val ROW_MIN = "`rowMin` is deprecated in favor of `rowMinOf`. $MESSAGE_1_0"
internal const val ROW_MIN_OR_NULL = "`rowMinOrNull` is deprecated in favor of `rowMinOfOrNull`. $MESSAGE_1_0"

internal const val ROW_MAX = "`rowMax` is deprecated in favor of `rowMaxOf`. $MESSAGE_1_0"
internal const val ROW_MAX_OR_NULL = "`rowMaxOrNull` is deprecated in favor of `rowMaxOfOrNull`. $MESSAGE_1_0"

internal const val ROW_MEDIAN = "`rowMedian` is deprecated in favor of `rowMedianOf`. $MESSAGE_1_0"
internal const val ROW_MEDIAN_OR_NULL = "`rowMedianOrNull` is deprecated in favor of `rowMedianOfOrNull`. $MESSAGE_1_0"

internal const val ROW_PERCENTILE = "`rowPercentile` is deprecated in favor of `rowPercentileOf`. $MESSAGE_1_0"
internal const val ROW_PERCENTILE_OR_NULL =
    "`rowPercentileOrNull` is deprecated in favor of `rowPercentileOfOrNull`. $MESSAGE_1_0"

internal const val SUM_NO_SKIPNAN = "This function is just here for binary compatibility. $MESSAGE_1_0"
internal const val MAX_NO_SKIPNAN = "This function is just here for binary compatibility. $MESSAGE_1_0"
internal const val MIN_NO_SKIPNAN = "This function is just here for binary compatibility. $MESSAGE_1_0"
internal const val MEAN_NO_SKIPNAN = "This function is just here for binary compatibility. $MESSAGE_1_0"

internal const val CONVERT_TO = "This to overload will be removed in favor of `asColumn`. $MESSAGE_1_0"
internal const val CONVERT_TO_REPLACE = "this.asColumn(columnConverter)"

internal const val TO_HTML = "This function is replaced by `toHtml()`. $MESSAGE_1_0"
internal const val TO_HTML_REPLACE = "toHtml(configuration, cellRenderer, getFooter)"

internal const val TO_STANDALONE_HTML = "This function is replaced by `toStandaloneHtml()`. $MESSAGE_1_0"
internal const val TO_STANDALONE_HTML_REPLACE = "toStandaloneHtml(configuration, cellRenderer, getFooter)"

internal const val WRITE_HTML = "This function is replaced by `writeHtml()`. $MESSAGE_1_0"
internal const val WRITE_HTML_REPLACE = "writeHtml(destination)"

internal const val CONVERT_TO_URL = "This function is replaced by `convertToUrl()`. $MESSAGE_1_0"
internal const val CONVERT_TO_URL_REPLACE = "convertToUrl()"

internal const val TO_URL = "This function is replaced by `toUrl()`. $MESSAGE_1_0"
internal const val TO_URL_REPLACE = "toUrl()"

internal const val FILTER_BY = "This function  is deprecated in favor of `filter { }`. $MESSAGE_1_0"
internal const val FILTER_BY_REPLACE = "filter { column }"

// endregion

// region WARNING in 1.0, ERROR in 1.1

private const val MESSAGE_1_1 = "Will be ERROR in 1.1."

internal const val APACHE_CSV =
    "The Apache-based CSV/TSV reader is deprecated in favor of the new Deephaven CSV reader in dataframe-csv. $MESSAGE_1_1"
internal const val READ_CSV =
    "Apache-based readCSV() is deprecated in favor of Deephaven-based readCsv() in dataframe-csv. $MESSAGE_1_1"
internal const val READ_CSV_IMPORT = "org.jetbrains.kotlinx.dataframe.io.readCsv"
internal const val READ_CSV_FILE_OR_URL_REPLACE =
    "this.readCsv(fileOrUrl = fileOrUrl, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_CSV_FILE_REPLACE =
    "this.readCsv(file = file, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_CSV_URL_REPLACE =
    "this.readCsv(url = url, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_CSV_STREAM_REPLACE =
    "this.readCsv(inputStream = stream, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"

internal const val READ_DELIM =
    "Apache-based readDelim() is deprecated in favor of Deephaven-based readDelim() in dataframe-csv. $MESSAGE_1_1"
internal const val READ_DELIM_STREAM_REPLACE =
    "this.readDelim(inputStream = inStream, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_DELIM_READER_REPLACE =
    "this.readDelimStr(text = reader.readText(), delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"

internal const val READ_TSV =
    "Apache-based readTSV() is deprecated in favor of Deephaven-based readTsv() in dataframe-csv. $MESSAGE_1_1"
internal const val READ_TSV_IMPORT = "org.jetbrains.kotlinx.dataframe.io.readTsv"
internal const val READ_TSV_FILE_OR_URL_REPLACE =
    "this.readTsv(fileOrUrl = fileOrUrl, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_TSV_FILE_REPLACE =
    "this.readTsv(file = file, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_TSV_URL_REPLACE =
    "this.readTsv(url = url, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_TSV_STREAM_REPLACE =
    "this.readTsv(inputStream = stream, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"

internal const val WRITE_CSV =
    "The writeCSV() functions are deprecated in favor of writeCsv() in dataframe-csv. $MESSAGE_1_1"
internal const val WRITE_CSV_IMPORT = "org.jetbrains.kotlinx.dataframe.io.writeCsv"
internal const val WRITE_CSV_FILE_REPLACE = "this.writeCsv(file = file)"
internal const val WRITE_CSV_PATH_REPLACE = "this.writeCsv(path = path)"
internal const val WRITE_CSV_WRITER_REPLACE = "this.writeCsv(writer = writer)"

internal const val TO_CSV = "toCsv() is deprecated in favor of toCsvStr() in dataframe-csv. $MESSAGE_1_1"
internal const val TO_CSV_IMPORT = "org.jetbrains.kotlinx.dataframe.io.toCsvStr"
internal const val TO_CSV_REPLACE = "this.toCsvStr()"

internal const val SPLIT_STR =
    "Please explicitly specify how the String should be split. Shortcut: $MESSAGE_1_1"

internal const val DATAFRAME_OF_WITH_VALUES =
    "Deprecated in favor of dataFrameOf(names).withValues(values). $MESSAGE_1_1"

internal const val COLS_AT_ANY_DEPTH = "Deprecated shortcut for better compiler plugin support. $MESSAGE_1_1"
internal const val COLS_AT_ANY_DEPTH_REPLACE = "this.colsAtAnyDepth().filter(predicate)"

internal const val COLS_IN_GROUPS = "Deprecated shortcut for better compiler plugin support. $MESSAGE_1_1"
internal const val COLS_IN_GROUPS_REPLACE = "this.colsInGroups().filter(predicate)"

internal const val SINGLE = "Deprecated shortcut for better compiler plugin support. $MESSAGE_1_1"
internal const val SINGLE_COL_REPLACE = "this.allCols().filter(condition).single()"
internal const val SINGLE_PLAIN_REPLACE = "this.cols().filter(condition).single()"
internal const val SINGLE_SET_REPLACE = "this.filter(condition).single()"

// endregion

// region keep across releases

internal const val DEPRECATED_ACCESS_API =
    "It's recommended to migrate to the String or Extension Properties Access API https://kotlin.github.io/dataframe/apilevels.html"

internal const val IDENTITY_FUNCTION = "This overload is an identity function and can be omitted."

internal const val COL_REPLACE = "col"

internal const val ALL_COLS_EXCEPT =
    "This overload is blocked to prevent issues with column accessors. Use the `{}` overload instead."
internal const val ALL_COLS_EXCEPT_REPLACE = "this.allColsExcept { other }"
internal const val ALL_COLS_EXCEPT_REPLACE_VARARG = "this.allColsExcept { others.toColumnSet() }"
internal const val EXCEPT_REPLACE = "this.except { other }"
internal const val EXCEPT_REPLACE_VARARG = "this.except { others.toColumnSet() }"
// endregion
