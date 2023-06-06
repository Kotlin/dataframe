package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.transform

/**
 * ## ColumnsResolver
 * Entity that can be resolved into a list of [columns][DataColumn].
 *
 * Used as a return type of [ColumnsSelector].
 *
 * Implemented by [SingleColumn] and [ColumnSet].
 *
 * @param C common type of resolved columns
 * @see [SingleColumn]
 * @see [ColumnSet]
 * @see [TransformableColumnSet]
 * @see [TransformableSingleColumn]
 */
public interface ColumnsResolver<out C> {

    /**
     * Resolves this [ColumnsResolver] as a [List]<[ColumnWithPath]<[C]>>.
     * In many cases this function [transforms][ColumnsResolver.transform] a parent [ColumnsResolver] to reach
     * the current [ColumnsResolver] result.
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
