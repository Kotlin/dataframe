package org.jetbrains.kotlinx.dataframe.exceptions

public class DuplicateColumnNamesException(public val allColumnNames: List<String>) : IllegalArgumentException() {

    public val duplicatedNames: List<String> = allColumnNames.groupBy { it }.filter { it.key != "" && it.value.size > 1 }.map { it.key }

    override val message: String
        get() = "Duplicate column names: $duplicatedNames\nAll column names: $allColumnNames"
}
