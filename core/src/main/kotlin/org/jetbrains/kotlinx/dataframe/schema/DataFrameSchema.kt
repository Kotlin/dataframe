package org.jetbrains.kotlinx.dataframe.schema

public interface DataFrameSchema {

    public val columns: Map<String, ColumnSchema>

    /**
     * By default generated markers for leafs aren't used as supertypes: @DataSchema(isOpen = false)
     * strictlyEqualLeafs = true takes this into account for internal codegen logic
     */
    public fun compare(other: DataFrameSchema, strictlyEqualLeafs: Boolean = false): CompareResult
}
