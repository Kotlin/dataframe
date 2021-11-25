package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * A set of columns. Used in column selectors API
 * @param C common type of columns
 */
public interface ColumnSet<out C> {

    public fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>
}

public class ColumnResolutionContext internal constructor (
    internal val df: DataFrame<*>,
    internal val unresolvedColumnsPolicy: UnresolvedColumnsPolicy
) {

    public val allowMissingColumns: Boolean = unresolvedColumnsPolicy == UnresolvedColumnsPolicy.Skip
}

internal enum class UnresolvedColumnsPolicy { Fail, Skip, Create }
