package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn

/**
 * Entity that can be [resolved][resolveSingle] into [DataColumn].
 *
 * @param C Column [type][BaseColumn.type] of resolved column.
 */
public interface SingleColumn<out C> : ColumnSet<C> {

    override fun resolve(
        context: ColumnResolutionContext,
    ): List<ColumnWithPath<C>> = resolveSingle(context)?.let { listOf(it) } ?: emptyList()

    override fun resolveAfterTransform(
        context: ColumnResolutionContext,
        transformer: ColumnSetTransformer,
    ): List<ColumnWithPath<C>> = resolveSingleAfterTransform(context, transformer)?.let { listOf(it) } ?: emptyList()

    public fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>?

    public fun resolveSingleAfterTransform(
        context: ColumnResolutionContext,
        transformer: ColumnSetTransformer,
    ): ColumnWithPath<C>?
}
