package org.jetbrains.kotlinx.dataframe.schema

public interface DataFrameSchema {

    public val columns: Map<String, ColumnSchema>

    public fun compare(other: DataFrameSchema): CompareResult
}
