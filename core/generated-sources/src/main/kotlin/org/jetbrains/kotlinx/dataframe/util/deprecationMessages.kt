package org.jetbrains.kotlinx.dataframe.util

/*
 * This file contains deprecation messages for the whole core module.
 * After each release, all messages should be reviewed and updated.
 * Level.WARNING -> Level.ERROR
 * Level.ERROR -> Remove
 */

// region WARNING in 0.12, ERROR in 0.13

private const val message_0_13 = "Will be removed in 0.13."

internal const val DIFF_DEPRECATION_MESSAGE = "Replaced to explicitly indicate nullable return value; added a new non-null overload. $message_0_13"
internal const val DIFF_REPLACE_MESSAGE = "this.diffOrNull(expression)"
internal const val DIFF_OR_NULL_IMPORT = "org.jetbrains.kotlinx.dataframe.api.diffOrNull"

internal const val UPDATE_AS_NULLABLE_MESSAGE = "This function is useless unless in combination with `withValue(null)`, but then you can just use `with { null }`. $message_0_13"
internal const val UPDATE_AS_NULLABLE_REPLACE = "this as Update<T, C?>"

internal const val UPDATE_WITH_VALUE = "Replaced in favor of `with { value }`. $message_0_13"
internal const val UPDATE_WITH_VALUE_REPLACE = "this.with { value }"

internal const val DATAFRAME_HTML_MESSAGE = "Deprecated to clarify difference with .toHTML(). $message_0_13"
internal const val DATAFRAME_HTML_REPLACE = "this.toStandaloneHTML().toString()"

internal const val TREENODE_TOPDFS_MESSAGE = "Use topmostChildren instead. $message_0_13"
internal const val TREENODE_TOPDFS_REPLACE = "topmostChildren(yieldCondition)"

internal const val TREENODE_DFS = "Use allChildren instead. $message_0_13"
internal const val TREENODE_DFS_REPLACE = "allChildren(enterCondition, yieldCondition)"

@PublishedApi
internal const val DFS_MESSAGE: String = "dfs is deprecated, use colsAtAnyDepth instead. $message_0_13 "

@PublishedApi
internal const val ALL_DFS_MESSAGE: String = "allDfs is deprecated, use colsAtAnyDepth instead. $message_0_13 "

@PublishedApi
internal const val DFS_OF_MESSAGE: String = "dfsOf is deprecated, use colsAtAnyDepth instead. $message_0_13 "

internal const val TOP_MESSAGE = "top is deprecated, use roots() instead. $message_0_13"

internal const val COL_SELECT_DSL_GROUP = "Use `colGroup()` instead. $message_0_13"
internal const val COL_SELECT_DSL_GROUP_REPLACE = "this.colGroup(name)"

internal const val COLS_SELECT_DSL_GROUP = "Use `colGroups` instead. $message_0_13"
internal const val COLS_SELECT_DSL_GROUP_REPLACE = "this.colGroups(filter)"

internal const val COL_SELECT_DSL_ALL_COLS = "Use `allCols()` instead. $message_0_13"
internal const val COL_SELECT_DSL_ALL_COLS_REPLACE = "this.allCols()"

internal const val COL_SELECT_DSL_ALL_COLS_AFTER = "Use `allColsAfter()` instead. $message_0_13"
internal const val COL_SELECT_DSL_ALL_COLS_AFTER_REPLACE = "this.allColsAfter(column)"

internal const val COL_SELECT_DSL_ALL_COLS_BEFORE = "Use `allColsBefore()` instead. $message_0_13"
internal const val COL_SELECT_DSL_ALL_COLS_BEFORE_REPLACE = "this.allColsBefore(column)"

internal const val COL_SELECT_DSL_ALL_FROM = "Use `allFrom()` instead. $message_0_13"
internal const val COL_SELECT_DSL_ALL_FROM_REPLACE = "this.allFrom(column)"

internal const val COL_SELECT_DSL_ALL_COLS_FROM = "Use `allColsFrom()` instead. $message_0_13"
internal const val COL_SELECT_DSL_ALL_COLS_FROM_REPLACE = "this.allColsFrom(column)"

internal const val COL_SELECT_DSL_ALL_UP_TO = "Use `allUpTo()` instead. $message_0_13"
internal const val COL_SELECT_DSL_ALL_UP_TO_REPLACE = "this.allUpTo(column)"

internal const val COL_SELECT_DSL_ALL_COLS_UP_TO = "Use `allColsUpTo()` instead. $message_0_13"
internal const val COL_SELECT_DSL_ALL_COLS_UP_TO_REPLACE = "this.allColsUpTo(column)"

internal const val COL_SELECT_DSL_AT_ANY_DEPTH = "This postfix notation is now deprecated. Use `colsAtAnyDepth().YOUR_OPERATION` instead (NOTE: ReplaceWith is incorrect). $message_0_13"

// TODO https://youtrack.jetbrains.com/issue/KTIJ-27052/ReplaceWith-Deprecated-this-after-call-bug
internal const val COL_SELECT_DSL_AT_ANY_DEPTH_REPLACE = "this"

internal const val COL_SELECT_DSL_CHILDREN = "Use colsInGroups {} instead. $message_0_13"
internal const val COL_SELECT_DSL_CHILDREN_REPLACE = "this.colsInGroups(predicate)"

internal const val COL_SELECT_DSL_CHILDREN_SINGLE_COL = "Use cols {} instead. $message_0_13"
internal const val COL_SELECT_DSL_CHILDREN_SINGLE_COL_REPLACE = "this.cols(predicate)"

// endregion

// region WARNING in 0.13, ERROR in 0.14

private const val message_0_14 = "Will be removed in 0.14."

internal const val COLUMN_WITH_PATH_MESSAGE = "`child` references are replaced with `col`. $message_0_14"

// endregion

// region keep across releases

internal const val IDENTITY_FUNCTION = "This overload is an identity function and can be omitted."

internal const val COL_REPLACE = "col"

// endregion
