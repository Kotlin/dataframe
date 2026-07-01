package org.jetbrains.kotlinx.dataframe.io.util

/*
 * This file contains deprecation messages for the whole core module.
 * After each release, all messages should be reviewed and updated.
 * Level.WARNING -> Level.ERROR
 * Level.ERROR -> Remove
 *
 * Please add info about all deprecations / removals to the Migration Guide
 * (./docs/StardustDocs/topics/MigrationTo_1_0.md)
 * in the same PR!
 */

// region WARNING in 0.15, ERROR in 1.0

private const val MESSAGE_1_0 = "Will be ERROR in 1.0."

private const val MESSAGE_REMOVE_1_1 = "Will be removed in 1.1."
internal const val READ_EXCEL_OLD = "This function is only here for binary compatibility. $MESSAGE_REMOVE_1_1"

// endregion

// region WARNING in 1.0, ERROR in 1.1

private const val MESSAGE_1_1 = "Will be ERROR in 1.1."

internal const val NAME_REPAIR_STRATEGY: String =
    "NameRepairStrategy is deprecated. Name repair is now always applied via ColumnNameGenerator, consistent with other IO readers. $MESSAGE_1_1"

// endregion

// region keep across releases

// endregion
