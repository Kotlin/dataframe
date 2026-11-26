package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.kotlinx.dataframe.util.COMPARE_RESULT_EQUALS

public enum class CompareResult {

    // TODO can be reintroduced at 1.1 to support "equals exactly" as CompareResult
    @Deprecated(
        message = COMPARE_RESULT_EQUALS,
        replaceWith = ReplaceWith("Matches", "org.jetbrains.kotlinx.dataframe.schema.CompareResult.Matches"),
        level = DeprecationLevel.ERROR,
    )
    Equals,

    /**
     * If the other schema has columns this has not,
     * or their columns have a more specific type than in this schema,
     * this is considered "super".
     */
    IsSuper,

    /**
     * If this schema has columns the other has not,
     * or their columns have a less specific type than in this schema,
     * this is considered "derived".
     */
    IsDerived,

    /** The two schemas are incomparable. */
    None,

    /**
     * Both schemas contain exactly the same columns, column groups, and frame columns,
     * though their order might still be different.
     */
    Matches,
    ;

    /**
     * True if
     * - both schemas contain exactly the same columns, column groups, and frame columns
     * - or the other schema has columns this has not
     * - or their columns have a more specific type than in this schema
     *
     * The column order might still be different.
     */
    public fun isSuperOrMatches(): Boolean = this == Matches || this == IsSuper

    @Deprecated(
        message = COMPARE_RESULT_EQUALS,
        replaceWith = ReplaceWith("isSuperOrMatches()"),
        level = DeprecationLevel.ERROR,
    )
    public fun isSuperOrEqual(): Boolean = isSuperOrMatches()

    /**
     * True if both schemas contain exactly the same columns, column groups, and frame columns,
     * though their order might still be different.
     */
    public fun matches(): Boolean = this == Matches

    @Deprecated(
        message = COMPARE_RESULT_EQUALS,
        replaceWith = ReplaceWith("matches()"),
        level = DeprecationLevel.ERROR,
    )
    public fun isEqual(): Boolean = this.matches()

    /**
     * Temporary helper method to avoid breaking changes.
     */
    @Deprecated(
        message = COMPARE_RESULT_EQUALS,
        level = DeprecationLevel.WARNING,
    )
    private fun isDeprecatedEquals(): Boolean = this != IsSuper && this != IsDerived && this != None && this != Matches

    public fun combine(other: CompareResult): CompareResult =
        when (this) {
            Matches -> other
            None -> None
            IsDerived -> if (other == Matches || other == IsDerived || other.isDeprecatedEquals()) this else None
            IsSuper -> if (other == Matches || other == IsSuper || other.isDeprecatedEquals()) this else None
            else -> other
        }

    public companion object {
        public fun compareNullability(thisIsNullable: Boolean, otherIsNullable: Boolean): CompareResult =
            when {
                thisIsNullable == otherIsNullable -> Matches
                thisIsNullable -> IsSuper
                else -> IsDerived
            }
    }
}

public operator fun CompareResult.plus(other: CompareResult): CompareResult = this.combine(other)
