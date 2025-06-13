package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS

public interface DataFrameSchema {

    public val columns: Map<String, ColumnSchema>

    /**
     * By default generated markers for leafs aren't used as supertypes: @DataSchema(isOpen = false)
     * [ComparisonMode.STRICT_FOR_NESTED_SCHEMAS] takes this into account for internal codegen logic
     */
    public fun compare(
        other: DataFrameSchema,
        comparisonMode: ComparisonMode = STRICT_FOR_NESTED_SCHEMAS,
    ): CompareResult
}
