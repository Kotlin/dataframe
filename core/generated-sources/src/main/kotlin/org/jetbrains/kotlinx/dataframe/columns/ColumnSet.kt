package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.toColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.dfs
import org.jetbrains.kotlinx.dataframe.impl.columns.wrap
import org.jetbrains.kotlinx.dataframe.impl.emptyPath

/**
 * Entity that can be resolved into a list of [columns][DataColumn].
 *
 * Used as a return type of [ColumnsSelector].
 * @param C common type of resolved columns
 */
public interface ColumnSet<out C> {
    public fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>>

    public fun resolveAfterTransform(
        context: ColumnResolutionContext,
        transform: (List<ColumnWithPath<C>>) -> List<ColumnWithPath<@UnsafeVariance C>>,
    ): List<ColumnWithPath<C>>
}

public class ColumnResolutionContext internal constructor(
    internal val df: DataFrame<*>,
    internal val unresolvedColumnsPolicy: UnresolvedColumnsPolicy,
) {

    public val allowMissingColumns: Boolean = unresolvedColumnsPolicy != UnresolvedColumnsPolicy.Fail
}

internal enum class UnresolvedColumnsPolicy { Fail, Skip, Create }

public fun <C> ColumnSet<C>.recursively(includeGroups: Boolean = true): ColumnSet<C> = object : ColumnSet<C> {

    private fun flatten(list: List<ColumnWithPath<C>>): List<ColumnWithPath<C>> =
        list
            .filter { it.isColumnGroup() } // TODO should I include this from dfs?
            .flatMap {
                it.children()
                    .dfs()
                    .filter { includeGroups || !it.isColumnGroup() } as List<ColumnWithPath<C>>
            }

    override fun resolve(
        context: ColumnResolutionContext,
    ): List<ColumnWithPath<C>> = this@recursively
        .resolveAfterTransform(context = context, transform = ::flatten)

    override fun resolveAfterTransform(
        context: ColumnResolutionContext,
        transform: (List<ColumnWithPath<C>>) -> List<ColumnWithPath<C>>,
    ): List<ColumnWithPath<C>> = this@recursively
        .transform(transform)
        .resolveAfterTransform(context = context, transform = ::flatten)
}

public fun <C> ColumnSet<C>.rec(includeGroups: Boolean = true): ColumnSet<C> = recursively(includeGroups)

public fun <C> ColumnSet<C>.allRecursively(includeGroups: Boolean = true): ColumnSet<C> =
    wrap().recursively(includeGroups = includeGroups)

public fun <C> ColumnSet<C>.allRec(includeGroups: Boolean = true): ColumnSet<C> =
    allRecursively(includeGroups = includeGroups)
