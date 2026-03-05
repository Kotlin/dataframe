@file:JvmName("CsvDeprecationMessagesKt")

package org.jetbrains.kotlinx.dataframe.util

/*
 * This file contains deprecation messages for the whole core module.
 * After each release, all messages should be reviewed and updated.
 * Level.WARNING -> Level.ERROR
 * Level.ERROR -> Remove
 */

// region WARNING in 0.15, ERROR in 1.0

private const val MESSAGE_1_0 = "Will be ERROR in 1.0."

internal const val READ_CSV_BINARY_COMPATIBILITY = "This overload is here to maintain binary compatibility."
internal const val READ_TSV_BINARY_COMPATIBILITY = "This overload is here to maintain binary compatibility."
internal const val READ_DELIM_BINARY_COMPATIBILITY = "This overload is here to maintain binary compatibility."

// endregion

// region WARNING in 1.0, ERROR in 1.1

private const val MESSAGE_1_1 = "Will be ERROR in 1.1."

// endregion

// region keep across releases

// endregion
