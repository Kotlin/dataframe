package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS

/**
 * Defines how [ColumnSchema] or [DataFrameSchema] are compared.
 *
 * Defines how differences between schemas are interpreted; affects the [result][CompareResult]
 * returned by [ColumnSchema.compare] and [DataFrameSchema.compare].
 *
 * @include [ComparisonModeOptionsSnippet]
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

/**
 * - [LENIENT]: Schemas may have different sets of columns. Missing or additional columns
 *   are reported as [CompareResult.IsDerived] or [CompareResult.IsSuper].
 * - [STRICT]: Schemas must have the same columns with the same names and types.
 *   Otherwise, the result is [CompareResult.None].
 * - [STRICT_FOR_NESTED_SCHEMAS]: Uses [LENIENT] comparison for the top-level schema
 *   and [STRICT] comparison for nested schemas.
 */
@ExcludeFromSources
internal typealias ComparisonModeOptionsSnippet = Nothing
