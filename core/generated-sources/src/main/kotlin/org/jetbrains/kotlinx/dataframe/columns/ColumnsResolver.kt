package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.transform

/**
 * ## ColumnsResolver
 * Entity that can be resolved into a list of [<code>columns</code>][DataColumn].
 *
 * Used as a return type of [<code>ColumnsSelector</code>][ColumnsSelector].
 *
 * Implemented by [<code>SingleColumn</code>][SingleColumn] and [<code>ColumnSet</code>][ColumnSet].
 *
 * @param C common type of resolved columns
 * @see [SingleColumn]
 * @see [ColumnSet]
 * @see [TransformableColumnSet]
 * @see [TransformableSingleColumn]
 */
public sealed interface ColumnsResolver<out C> {

    /**
     * Resolves this [<code>ColumnsResolver</code>][ColumnsResolver] as a [<code>List</code>][List]<[<code>ColumnWithPath</code>][ColumnWithPath]<[<code>C</code>][C]>>.
     * In many cases this function [<code>transforms</code>][ColumnsResolver.transform] a parent [<code>ColumnsResolver</code>][ColumnsResolver] to reach
     * the current [<code>ColumnsResolver</code>][ColumnsResolver] result.
     */
    public fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>
}

public class ColumnResolutionContext(
    public val df: DataFrame<*>,
    public val unresolvedColumnsPolicy: UnresolvedColumnsPolicy,
) {

    public val allowMissingColumns: Boolean = unresolvedColumnsPolicy != UnresolvedColumnsPolicy.Fail
}

public enum class UnresolvedColumnsPolicy { Fail, Skip, Create }
