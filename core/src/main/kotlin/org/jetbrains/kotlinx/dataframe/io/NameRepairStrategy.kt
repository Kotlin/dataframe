package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.util.NAME_REPAIR_STRATEGY

/**
 * This strategy defines how the repeatable name column will be handled
 * during the creation new dataframe from the IO sources.
 */
@Deprecated(NAME_REPAIR_STRATEGY, level = DeprecationLevel.WARNING)
public enum class NameRepairStrategy {
    /** No actions, keep as is. */
    DO_NOTHING,

    /** Check the uniqueness of the column names without any actions. */
    CHECK_UNIQUE,

    /** Check the uniqueness of the column names and repair it. */
    MAKE_UNIQUE,
}
