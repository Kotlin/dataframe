package org.jetbrains.kotlinx.dataframe.util

/*
 * This file contains deprecation messages for the whole core module.
 * After each release, all messages should be reviewed and updated.
 * Level.WARNING -> Level.ERROR
 * Level.ERROR -> Remove
 */

// region WARNING in 0.13, ERROR in 0.14

private const val MESSAGE_0_14 = "Will be removed in 0.14."

internal const val COLUMN_WITH_PATH_MESSAGE = "`child` references are replaced with `col`. $MESSAGE_0_14"

internal const val COLS_SELECT_DSL_GROUP = "Use `colGroups` instead. $MESSAGE_0_14"
internal const val COLS_SELECT_DSL_GROUP_REPLACE = "this.colGroups(filter)"

internal const val COL_SELECT_DSL_ALL_COLS = "Use `allCols()` instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_ALL_COLS_REPLACE = "this.allCols()"

internal const val COL_SELECT_DSL_ALL_COLS_AFTER = "Use `allColsAfter()` instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_ALL_COLS_AFTER_REPLACE = "this.allColsAfter(column)"

internal const val COL_SELECT_DSL_ALL_COLS_BEFORE = "Use `allColsBefore()` instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_ALL_COLS_BEFORE_REPLACE = "this.allColsBefore(column)"

internal const val COL_SELECT_DSL_ALL_FROM = "Use `allFrom()` instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_ALL_FROM_REPLACE = "this.allFrom(column)"

internal const val COL_SELECT_DSL_ALL_COLS_FROM = "Use `allColsFrom()` instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_ALL_COLS_FROM_REPLACE = "this.allColsFrom(column)"

internal const val COL_SELECT_DSL_ALL_UP_TO = "Use `allUpTo()` instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_ALL_UP_TO_REPLACE = "this.allUpTo(column)"

internal const val COL_SELECT_DSL_ALL_COLS_UP_TO = "Use `allColsUpTo()` instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_ALL_COLS_UP_TO_REPLACE = "this.allColsUpTo(column)"

internal const val COL_SELECT_DSL_AT_ANY_DEPTH = "This postfix notation is now deprecated. " +
    "Use `colsAtAnyDepth().YOUR_OPERATION` instead (NOTE: ReplaceWith is incorrect). $MESSAGE_0_14"

// TODO https://youtrack.jetbrains.com/issue/KTIJ-27052/ReplaceWith-Deprecated-this-after-call-bug
internal const val COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE = "this"

internal const val COL_SELECT_DSL_CHILDREN = "Use colsInGroups {} instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_CHILDREN_REPLACE = "this.colsInGroups(predicate)"

internal const val COL_SELECT_DSL_CHILDREN_SINGLE_COL = "Use cols {} instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_CHILDREN_SINGLE_COL_REPLACE = "this.cols(predicate)"

internal const val TOP_MESSAGE = "top is deprecated, use simplify() instead. $MESSAGE_0_14"

internal const val COL_SELECT_DSL_LIST_DATACOLUMN_GET = "This function is deprecated. " +
    "Use `.toColumnSet()[]` instead. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_LIST_DATACOLUMN_GET_REPLACE = "this.toColumnSet()[range]"

internal const val COL_SELECT_DSL_SELECT_COLS = "Nested select is reserved for " +
    "ColumnsSelector/ColumnsSelectionDsl behavior. Use myGroup.cols() to select columns by name from a ColumnGroup. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_SELECT_COLS_REPLACE = "this.cols(*columns)"

internal const val COL_SELECT_DSL_SINGLE_COL_EXCEPT = "This function is replaced with allColsExcept. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_SELECTOR = "this.allColsExcept(selector)"
internal const val COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_RESOLVERS = "this.allColsExcept { others.toColumnSet() }"
internal const val COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_RESOLVER = "this.allColsExcept { other }"
internal const val COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHERS = "this.allColsExcept(*others)"
internal const val COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHER = "this.allColsExcept(other)"

internal const val COL_SELECT_DSL_EXCEPT = "This function is replaced with allExcept. $MESSAGE_0_14"
internal const val COL_SELECT_DSL_EXCEPT_REPLACE_SELECTOR = "this.allExcept(selector)"
internal const val COL_SELECT_DSL_EXCEPT_REPLACE_RESOLVER = "this.allExcept(*others)"

// endregion

// region WARNING in 0.14, ERROR in 0.15

private const val MESSAGE_0_15 = "Will be removed in 0.15."

// endregion

// region WARNING in 0.15, ERROR in 0.16

private const val MESSAGE_0_16 = "Will be removed in 0.16."

// endregion

// region keep across releases

internal const val IDENTITY_FUNCTION = "This overload is an identity function and can be omitted."

internal const val COL_REPLACE = "col"

internal const val ALL_COLS_EXCEPT = "This overload is blocked to prevent issues with column accessors. " +
    "Use the `{}` overload instead."
internal const val ALL_COLS_REPLACE = "allColsExcept { other }"
internal const val ALL_COLS_REPLACE_VARARG = "allColsExcept { others.toColumnSet() }"

// endregion
