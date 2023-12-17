package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.*

/**
 * Entity that can be [resolved][resolveSingle] into [DataColumn].
 *
 * @param C Column [type][BaseColumn.type] of resolved column.
 * @see [ColumnSet]
 * @see [TransformableColumnSet]
 * @see [TransformableSingleColumn]
 */
public interface SingleColumn<out C> : ColumnSet<C> {

    override fun resolve(
        context: ColumnResolutionContext,
    ): List<ColumnWithPath<C>> = resolveSingle(context)?.let { listOf(it) } ?: emptyList()

    public fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>?
}

public fun ColumnSet<*>.isSingleColumn(): Boolean = this is SingleColumn<*>

/**
 * Returns true if [this] is a [SingleColumn] and [cols] consists of a single column group.
 */
public fun ColumnSet<*>.isSingleColumnWithGroup(cols: List<ColumnWithPath<*>>): Boolean =
    isSingleColumn() && cols.singleOrNull()?.isColumnGroup() == true
