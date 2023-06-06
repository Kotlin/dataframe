package org.jetbrains.kotlinx.dataframe.io

/**
 * This strategy defines how the repeatable name column will be handled
 * during the creation new dataframe from the IO sources.
 */
public enum class NameRepairStrategy {
    /** No actions, keep as is. */
    DO_NOTHING,

    /** Check the uniqueness of the column names without any actions. */
    CHECK_UNIQUE,

    /** Check the uniqueness of the column names and repair it. */
    MAKE_UNIQUE
}
