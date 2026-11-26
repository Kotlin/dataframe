package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn

/**
 * ## SingleColumn
 * Entity that can be [<code>resolved</code>][resolveSingle] into a single [<code>DataColumn</code>][DataColumn].
 *
 * @param C Column [<code>type</code>][BaseColumn.type] of resolved column.
 * @see [ColumnsResolver]
 * @see [TransformableColumnSet]
 * @see [TransformableSingleColumn]
 */
public interface SingleColumn<out C> : ColumnsResolver<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
        resolveSingle(context)
            ?.let { listOf(it) }
            ?: emptyList()

    public fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>?
}
