package org.jetbrains.kotlinx.dataframe.schema

public interface DataFrameSchema {
    public companion object;

    public val columns: Map<String, ColumnSchema>

    /**
     * Compares this schema with [<code>other</code>][other] schema.
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
