package org.jetbrains.kotlinx.dataframe.util

internal const val DF_READ_DEPRECATION_MESSAGE = "Replaced with `unfold` operation."

internal const val DF_READ_REPLACE_MESSAGE = "this.unfold(*columns)"

internal const val ITERABLE_COLUMNS_DEPRECATION_MESSAGE = "Replaced with `toColumnSet()` operation."

internal const val DIFF_DEPRECATION_MESSAGE = "Replaced to explicitly indicate nullable return value; added a new non-null overload."

internal const val DIFF_REPLACE_MESSAGE = "this.diffOrNull(expression)"

internal const val DIFF_OR_NULL_IMPORT = "org.jetbrains.kotlinx.dataframe.api.diffOrNull"
