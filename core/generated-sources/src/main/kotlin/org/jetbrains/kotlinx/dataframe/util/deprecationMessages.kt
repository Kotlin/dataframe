package org.jetbrains.kotlinx.dataframe.util

/*
 * This file contains deprecation messages for the whole core module.
 * After each release, all messages should be reviewed and updated.
 * Level.WARNING -> Level.ERROR
 * Level.ERROR -> Remove
 */

// region WARNING in 0.10.0, ERROR in 0.11.0

internal const val DF_READ_DEPRECATION_MESSAGE = "Replaced with `unfold` operation. Removed in 0.11.0."
internal const val DF_READ_REPLACE_MESSAGE = "this.unfold(*columns)"

internal const val ITERABLE_COLUMNS_DEPRECATION_MESSAGE = "Replaced with `toColumnSet()` operation. Removed in 0.11.0."

// endregion

// region WARNING in 0.11.0, ERROR in 0.12.0

internal const val DIFF_DEPRECATION_MESSAGE = "Replaced to explicitly indicate nullable return value; added a new non-null overload. Will be removed in 0.12.0."
internal const val DIFF_REPLACE_MESSAGE = "this.diffOrNull(expression)"
internal const val DIFF_OR_NULL_IMPORT = "org.jetbrains.kotlinx.dataframe.api.diffOrNull"

internal const val UPDATE_AS_NULLABLE_MESSAGE = "This function is useless unless in combination with `withValue(null)`, but then you can just use `with { null }`. Will be removed in 0.12.0."
internal const val UPDATE_AS_NULLABLE_REPLACE = "this as Update<T, C?>"

internal const val UPDATE_WITH_VALUE = "Replaced in favor of `with { value }`. Will be removed in 0.12.0."
internal const val UPDATE_WITH_VALUE_REPLACE = "this.with { value }"

// endregion

// region WARNING in 0.12.0, ERROR in 0.13.0

// endregion
