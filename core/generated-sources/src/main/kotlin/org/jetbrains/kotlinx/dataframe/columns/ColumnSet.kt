package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame

/**
 * Entity that can be resolved into a list of [columns][DataColumn].
 *
 * Used as a return type of [ColumnsSelector].
 * @param C common type of resolved columns
 */
public interface ColumnSet<out C> {

    public fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>
}

public class ColumnResolutionContext internal constructor (
    internal val df: DataFrame<*>,
    internal val unresolvedColumnsPolicy: UnresolvedColumnsPolicy
) {

    public val allowMissingColumns: Boolean = unresolvedColumnsPolicy != UnresolvedColumnsPolicy.Fail
}

internal enum class UnresolvedColumnsPolicy { Fail, Skip, Create }
