package org.jetbrains.kotlinx.dataframe.schema

public interface DataFrameSchema {

    public val columns: Map<String, ColumnSchema>

    /**
     * @param comparisonMode The [mode][ComparisonMode] to compare the schema's by.
     *   By default, generated markers for leafs aren't used as supertypes: `@DataSchema(isOpen = false)`
     *   Setting [comparisonMode] to [ComparisonMode.STRICT_FOR_NESTED_SCHEMAS] takes this into account
     *   for internal codegen logic.
     */
    public fun compare(other: DataFrameSchema, comparisonMode: ComparisonMode = ComparisonMode.LENIENT): CompareResult
}
