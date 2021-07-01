package org.jetbrains.dataframe.internal.schema

public enum class CompareResult {
    Equals,
    IsSuper,
    IsDerived,
    None;

    public fun isSuperOrEqual(): Boolean = this == Equals || this == IsSuper

    public fun isEqual(): Boolean = this == Equals

    public fun combine(other: CompareResult): CompareResult =
        when (this) {
            Equals -> other
            None -> None
            IsDerived -> if (other == Equals || other == IsDerived) this else None
            IsSuper -> if (other == Equals || other == IsSuper) this else None
        }

    public companion object {
        public fun compareNullability(thisIsNullable: Boolean, otherIsNullable: Boolean): CompareResult = when {
            thisIsNullable == otherIsNullable -> Equals
            thisIsNullable -> IsSuper
            else -> IsDerived
        }
    }
}
