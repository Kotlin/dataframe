package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn

/**
 * ## SingleColumn
 * Entity that can be [resolved][resolveSingle] into a single [DataColumn].
 *
 * @param C Column [type][BaseColumn.type] of resolved column.
 * @see [ColumnsResolver]
 * @see [TransformableColumnSet]
 * @see [TransformableSingleColumn]
 */
public interface SingleColumn<out C> : ColumnsResolver<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
        resolveSingle(context)?.let { listOf(it) } ?: emptyList()

    public fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>?
}

public fun ColumnsResolver<*>.isSingleColumn(): Boolean = this is SingleColumn<*>

/**
 * Returns true if [this] is a [SingleColumn] and [cols] consists of a single column group.
 */
public fun ColumnsResolver<*>.isSingleColumnWithGroup(cols: List<ColumnWithPath<*>>): Boolean =
    isSingleColumn() && cols.singleOrNull()?.isColumnGroup() == true
