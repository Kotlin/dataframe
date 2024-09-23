package org.jetbrains.kotlinx.dataframe.util

/*
 * This file contains deprecation messages for the whole core module.
 * After each release, all messages should be reviewed and updated.
 * Level.WARNING -> Level.ERROR
 * Level.ERROR -> Remove
 */

// region WARNING in 0.14, ERROR in 0.15

private const val MESSAGE_0_15 = "Will be removed in 0.15."

// endregion

// region WARNING in 0.15, ERROR in 0.16

private const val MESSAGE_0_16 = "Will be removed in 0.16."

// endregion

// region WARNING in 0.16, ERROR in 0.17

private const val MESSAGE_0_17 = "Will be removed in 0.17."

// endregion

// region keep across releases

internal const val IDENTITY_FUNCTION = "This overload is an identity function and can be omitted."

internal const val COL_REPLACE = "col"

internal const val ALL_COLS_EXCEPT =
    "This overload is blocked to prevent issues with column accessors. Use the `{}` overload instead."
internal const val ALL_COLS_REPLACE = "allColsExcept { other }"
internal const val ALL_COLS_REPLACE_VARARG = "allColsExcept { others.toColumnSet() }"

// endregion
