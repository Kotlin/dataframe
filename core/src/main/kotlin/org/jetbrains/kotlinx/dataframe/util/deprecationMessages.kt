package org.jetbrains.kotlinx.dataframe.util

/*
 * This file contains deprecation messages for the whole core module.
 * After each release, all messages should be reviewed and updated.
 * Level.WARNING -> Level.ERROR
 * Level.ERROR -> Remove
 */

// region WARNING in 0.15, ERROR in 0.16

private const val MESSAGE_0_16 = "Will be ERROR in 0.16."

internal const val DF_READ_NO_CSV = "This function is deprecated and should be replaced with `readCSV`. $MESSAGE_0_16"
internal const val DF_READ_NO_CSV_REPLACE =
    "this.readCSV(fileOrUrl, delimiter, header, colTypes, skipLines, readLines, duplicate, charset)"

internal const val CREATE_FRAME_COLUMN =
    "Removed from public API as this can likely better be solved by `DataFrame.chunked()`. Replaced by internal df.chunkedImpl(). $MESSAGE_0_16"
internal const val CREATE_FRAME_COLUMN_REPLACE = "df.chunkedImpl(startIndices, name)"
internal const val CHUNKED_IMPL_IMPORT = "org.jetbrains.kotlinx.dataframe.impl.api.chunkedImpl"

internal const val CREATE_WITH_TYPE_INFERENCE =
    "This function is deprecated and should be replaced by `createByInference()`. $MESSAGE_0_16"
internal const val CREATE_WITH_TYPE_INFERENCE_REPLACE =
    "createByInference(name, values, TypeSuggestion.Infer, nullable)"
internal const val CREATE_BY_INFERENCE_IMPORT = "org.jetbrains.kotlinx.dataframe.DataColumn.Companion.createByInference"
internal const val TYPE_SUGGESTION_IMPORT = "org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion"

internal const val CREATE = "This function is deprecated and should be replaced by `createByType()`. $MESSAGE_0_16"
internal const val CREATE_REPLACE = "createByType(name, values, type, infer)"
internal const val CREATE_INLINE_REPLACE = "createByType(name, values, infer)"
internal const val CREATE_BY_TYPE_IMPORT = "org.jetbrains.kotlinx.dataframe.DataColumn.Companion.createByType"

internal const val GUESS_VALUE_TYPE = "This function is just here for binary compatibility. $MESSAGE_0_16"

internal const val CREATE_COLUMN = "This function is just here for binary compatibility. $MESSAGE_0_16"

internal const val GUESS_COLUMN_TYPE = "This function is just here for binary compatibility. $MESSAGE_0_16"

public const val DF_READ_EXCEL: String = "This function is just here for binary compatibility. $MESSAGE_0_16"

internal const val PARSER_OPTIONS = "This constructor is only here for binary compatibility. $MESSAGE_0_16"

internal const val PARSER_OPTIONS_COPY = "This function is only here for binary compatibility. $MESSAGE_0_16"

internal const val IS_COMPARABLE =
    "This function is replaced by `valuesAreComparable()` to better reflect its purpose. $MESSAGE_0_16"
internal const val IS_COMPARABLE_REPLACE = "valuesAreComparable()"
internal const val IS_INTER_COMPARABLE_IMPORT = "org.jetbrains.kotlinx.dataframe.api.valuesAreComparable"

internal const val AS_URL = "This function is replaced by `asUrl()`. $MESSAGE_0_16"
internal const val AS_URL_REPLACE = "asUrl(fileOrUrl)"
internal const val AS_URL_IMPORT = "org.jetbrains.kotlinx.dataframe.io.asUrl"

internal const val IS_URL = "This function is replaced by `isUrl()`. $MESSAGE_0_16"
internal const val IS_URL_REPLACE = "isUrl(path)"
internal const val IS_URL_IMPORT = "org.jetbrains.kotlinx.dataframe.io.isUrl"

internal const val MINUS = "This minus overload will be removed in favor of `remove`. $MESSAGE_0_16"
internal const val MINUS_REPLACE = "this.remove(columns)"

// endregion

// region WARNING in 0.16, ERROR in 0.17

private const val MESSAGE_0_17 = "Will be ERROR in 0.17."

// endregion

// region keep across releases

internal const val IDENTITY_FUNCTION = "This overload is an identity function and can be omitted."

internal const val COL_REPLACE = "col"

internal const val ALL_COLS_EXCEPT =
    "This overload is blocked to prevent issues with column accessors. Use the `{}` overload instead."
internal const val ALL_COLS_REPLACE = "allColsExcept { other }"
internal const val ALL_COLS_REPLACE_VARARG = "allColsExcept { others.toColumnSet() }"

internal const val ALL_COLS_EXCEPT_COLUMN_PATH =
    "This overload is blocked because you cannot use `allColsExcept` for columns nested in this column group. " +
        "Use a String to refer to a column instead, or use DataFrame.remove {} to remove nested columns."

internal const val ALL_EXCEPT_COLUMN_PATH =
    "This overload is blocked because you cannot use `allExcept` for nested columns. " +
        "Use a String to refer to a column instead, or use DataFrame.remove {} to remove nested columns."

// endregion
