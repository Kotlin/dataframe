package org.jetbrains.kotlinx.dataframe.schema

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
