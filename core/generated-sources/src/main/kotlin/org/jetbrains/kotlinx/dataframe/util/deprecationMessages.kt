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

internal const val GROUP_MESSAGE = "Use colGroup() instead. $message_0_13"
internal const val GROUP_REPLACE = "this.colGroup(name)"

@PublishedApi
internal const val DFS_MESSAGE: String = "dfs is deprecated, use recursively instead. $message_0_13 " +
    "NOTE: This function is actively being worked on and will be replaced again in the future."

@PublishedApi
internal const val ALL_DFS_MESSAGE: String = "allDfs is deprecated, use recursively instead. $message_0_13 " +
    "NOTE: This function is actively being worked on and will be replaced again in the future."

@PublishedApi
internal const val DFS_OF_MESSAGE: String = "dfsOf is deprecated, use recursively instead. $message_0_13 " +
    "NOTE: This function is actively being worked on and will be replaced again in the future."

internal const val TOP_MESSAGE = "top is deprecated, use roots() instead. $message_0_13"

// endregion

// region WARNING in 0.13, ERROR in 0.14

private const val message_0_14 = "Will be removed in 0.14."

// endregion

// region WARNING in 0.14, ERROR in 0.15

private const val message_0_15 = "Will be removed in 0.15."

// endregion
