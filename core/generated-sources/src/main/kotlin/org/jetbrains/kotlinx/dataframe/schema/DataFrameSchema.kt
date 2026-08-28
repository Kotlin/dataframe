package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

/**
 * Represents the schema of a dataframe,
 * i.e., an ordered map of column names to their types.
 *
 * Column types are represented by [<code>ColumnSchema</code>][org.jetbrains.kotlinx.dataframe.schema.ColumnSchema]:
 *
 * - For value columns, it contains the [<code>type</code>][kotlin.reflect.KType] of the column.
 * - For column groups, it contains the [<code>DataFrameSchema</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema] of the nested columns.
 * - For frame columns, it contains the [<code>DataFrameSchema</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema] of the contained dataframes.
 *
 * Use [<code>compare</code>][org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl.compare]
 * to compare this schema with another schema using different [<code>comparison modes</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode].
 * The comparison ignores column order and can report how the schemas are related.
 *
 * Use [<code>equals</code>][org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl.equals]
 * to check whether two schemas are exactly equal, including column order.
 */
public interface DataFrameSchema {
    public companion object;

    /**
     * A map that defines the schema of the columns within a [<code>DataFrameSchema</code>][DataFrameSchema].
     *
     * The key represents the name of the column, while the value is a [<code>ColumnSchema</code>][ColumnSchema] describing
     * the structure, type, and properties of the column.
     *
     * The value can be one of the following:
     * - [<code>ColumnSchema.Value</code>][ColumnSchema.Value]: Represents a value column, including its type and nullability.
     * - [<code>ColumnSchema.Group</code>][ColumnSchema.Group]: Represents a group of columns, containing their schemas as a nested [<code>DataFrameSchema</code>][DataFrameSchema].
     * - [<code>ColumnSchema.Frame</code>][ColumnSchema.Frame]: Represents a frame column (column of dataframes) containing their [<code>DataFrameSchema</code>][DataFrameSchema].
     *
     * This map provides an ordered representation of the schema that accurately describes the names and
     * corresponding structure of all columns in a dataframe.
     */
    public val columns: Map<String, ColumnSchema>

    /**
     * Compares this schema with [<code>other</code>][other] schema.
     *
     * Returns one of possible [<code>CompareResult</code>][CompareResult] based on the specified [<code>comparisonMode</code>][comparisonMode]:
     *
     * - [<code>LENIENT</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT]: Schemas may have different sets of columns. Missing or additional columns
     *   are reported as [<code>CompareResult.IsDerived</code>][org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsDerived] or [<code>CompareResult.IsSuper</code>][org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsSuper].
     * - [<code>STRICT</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT]: Schemas must have the same columns with the same names and types.
     *   Otherwise, the result is [<code>CompareResult.None</code>][org.jetbrains.kotlinx.dataframe.schema.CompareResult.None].
     * - [<code>STRICT_FOR_NESTED_SCHEMAS</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS]: Uses [<code>LENIENT</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT] comparison for the top-level schema
     *   and [<code>STRICT</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT] comparison for nested schemas.
     *
     * @param comparisonMode The [<code>mode</code>][ComparisonMode] to compare the schema's by.
     *   By default, generated markers for leafs aren't used as supertypes: `@DataSchema(isOpen = false)`
     *   Setting [<code>comparisonMode</code>][comparisonMode] to [<code>ComparisonMode.STRICT_FOR_NESTED_SCHEMAS</code>][ComparisonMode.STRICT_FOR_NESTED_SCHEMAS] takes this into account
     *   for internal codegen logic.
     *
     * @return a [<code>CompareResult</code>][CompareResult] that indicates whether this schema compared to [<code>other</code>][other] is
     *   [<code>matching</code>][CompareResult.Matches] (neglecting order),
     *   [<code>derived</code>][CompareResult.IsDerived], [<code>superset</code>][CompareResult.IsSuper], or [<code>incomparable</code>][CompareResult.None].
     */
    public fun compare(other: DataFrameSchema, comparisonMode: ComparisonMode = ComparisonMode.LENIENT): CompareResult
}
