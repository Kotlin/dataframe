package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

/**
 * Represents the schema of a dataframe,
 * i.e., an ordered map of column names to their types.
 *
 * Column types are represented by [ColumnSchema][org.jetbrains.kotlinx.dataframe.schema.ColumnSchema]:
 *
 * - For value columns, it contains the [type][kotlin.reflect.KType] of the column.
 * - For column groups, it contains the [DataFrameSchema][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema] of the nested columns.
 * - For frame columns, it contains the [DataFrameSchema][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema] of the contained dataframes.
 *
 * Use [compare][org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl.compare]
 * to compare this schema with another schema using different [comparison modes][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode].
 * The comparison ignores column order and can report how the schemas are related.
 *
 * Use [equals][org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl.equals]
 * to check whether two schemas are exactly equal, including column order.
 */
public interface DataFrameSchema {
    public companion object;

    /**
     * A map that defines the schema of the columns within a [DataFrameSchema].
     *
     * The key represents the name of the column, while the value is a [ColumnSchema] describing
     * the structure, type, and properties of the column.
     *
     * The value can be one of the following:
     * - [ColumnSchema.Value]: Represents a value column, including its type and nullability.
     * - [ColumnSchema.Group]: Represents a group of columns, containing their schemas as a nested [DataFrameSchema].
     * - [ColumnSchema.Frame]: Represents a frame column (column of dataframes) containing their [DataFrameSchema].
     *
     * This map provides an ordered representation of the schema that accurately describes the names and
     * corresponding structure of all columns in a dataframe.
     */
    public val columns: Map<String, ColumnSchema>

    /**
     * Compares this schema with [other] schema.
     *
     * Returns one of possible [CompareResult] based on the specified [comparisonMode]:
     *
     * - [LENIENT][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT]: Schemas may have different sets of columns. Missing or additional columns
     *   are reported as [CompareResult.IsDerived][org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsDerived] or [CompareResult.IsSuper][org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsSuper].
     * - [STRICT][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT]: Schemas must have the same columns with the same names and types.
     *   Otherwise, the result is [CompareResult.None][org.jetbrains.kotlinx.dataframe.schema.CompareResult.None].
     * - [STRICT_FOR_NESTED_SCHEMAS][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS]: Uses [LENIENT][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT] comparison for the top-level schema
     *   and [STRICT][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT] comparison for nested schemas.
     *
     * @param comparisonMode The [mode][ComparisonMode] to compare the schema's by.
     *   By default, generated markers for leafs aren't used as supertypes: `@DataSchema(isOpen = false)`
     *   Setting [comparisonMode] to [ComparisonMode.STRICT_FOR_NESTED_SCHEMAS] takes this into account
     *   for internal codegen logic.
     *
     * @return a [CompareResult] that indicates whether this schema compared to [other] is
     *   [matching][CompareResult.Matches] (neglecting order),
     *   [derived][CompareResult.IsDerived], [superset][CompareResult.IsSuper], or [incomparable][CompareResult.None].
     */
    public fun compare(other: DataFrameSchema, comparisonMode: ComparisonMode = ComparisonMode.LENIENT): CompareResult
}
