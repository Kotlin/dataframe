package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

/**
 * @include [DataFrameSchemaDoc]
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
     * @include [ComparisonModeOptionsSnippet]
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

/**
 * Represents the schema of a dataframe,
 * i.e., an ordered map of column names to their types.
 *
 * Column types are represented by [ColumnSchema]:
 *
 * - For value columns, it contains the [type][kotlin.reflect.KType] of the column.
 * - For column groups, it contains the [DataFrameSchema] of the nested columns.
 * - For frame columns, it contains the [DataFrameSchema] of the contained dataframes.
 *
 * Use [compare][org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl.compare]
 * to compare this schema with another schema using different [comparison modes][ComparisonMode].
 * The comparison ignores column order and can report how the schemas are related.
 *
 * Use [equals][org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl.equals]
 * to check whether two schemas are exactly equal, including column order.
 */
@ExcludeFromSources
internal typealias DataFrameSchemaDoc = Nothing
