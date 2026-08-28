package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS

/**
 * Defines how [<code>ColumnSchema</code>][ColumnSchema] or [<code>DataFrameSchema</code>][DataFrameSchema] are compared.
 *
 * Defines how differences between schemas are interpreted; affects the [<code>result</code>][CompareResult]
 * returned by [<code>ColumnSchema.compare</code>][ColumnSchema.compare] and [<code>DataFrameSchema.compare</code>][DataFrameSchema.compare].
 *
 * - [<code>LENIENT</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT]: Schemas may have different sets of columns. Missing or additional columns
 *   are reported as [<code>CompareResult.IsDerived</code>][org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsDerived] or [<code>CompareResult.IsSuper</code>][org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsSuper].
 * - [<code>STRICT</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT]: Schemas must have the same columns with the same names and types.
 *   Otherwise, the result is [<code>CompareResult.None</code>][org.jetbrains.kotlinx.dataframe.schema.CompareResult.None].
 * - [<code>STRICT_FOR_NESTED_SCHEMAS</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS]: Uses [<code>LENIENT</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT] comparison for the top-level schema
 *   and [<code>STRICT</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT] comparison for nested schemas.
 */
public enum class ComparisonMode {
    /**
     * In this mode, all [CompareResults][CompareResult] can occur.
     *
     * If this schema has columns the other has not, the other is considered [CompareResult.IsDerived].
     * If the other schema has columns this has not, this is considered [CompareResult.IsSuper].
     */
    LENIENT,

    /**
     * Columns must all be present in the other schema with the same name and type.
     * [CompareResult.IsDerived] and [CompareResult.IsSuper] will result in [CompareResult.None] in this mode.
     */
    STRICT,

    /** Works like [LENIENT] at the top-level, but turns to [STRICT] for nested schemas. */
    STRICT_FOR_NESTED_SCHEMAS,
}
