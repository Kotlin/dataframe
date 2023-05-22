package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.*

/**
 * ## ColumnSet
 * Entity that can be resolved into a list of [columns][DataColumn].
 *
 * Used as a return type of [ColumnsSelector].
 * @param C common type of resolved columns
 * @see [SingleColumn]
 * @see [TransformableColumnSet]
 * @see [TransformableSingleColumn]
 */
public interface ColumnSet<out C> {

    /**
     * Resolves this [ColumnSet] as a [List]<[ColumnWithPath]<[C]>>.
     * In many cases this function [transforms][ColumnSet.transform] a parent [ColumnSet] to reach
     * the current [ColumnSet] result.
     */
    public fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>
}

public class ColumnResolutionContext internal constructor(
    internal val df: DataFrame<*>,
    internal val unresolvedColumnsPolicy: UnresolvedColumnsPolicy,
) {

    public val allowMissingColumns: Boolean = unresolvedColumnsPolicy != UnresolvedColumnsPolicy.Fail
}

internal enum class UnresolvedColumnsPolicy { Fail, Skip, Create }
