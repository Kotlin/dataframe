package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Entity that can be [resolved][resolveSingle] into [DataColumn].
 *
 * @param C Column [type][BaseColumn.type] of resolved column.
 */
public interface SingleColumn<out C> : ColumnSet<C> {

    override fun resolve(
        context: ColumnResolutionContext,
    ): List<ColumnWithPath<C>> = resolveSingle(context)?.let { listOf(it) } ?: emptyList()

    public fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>?
}

internal fun <C> SingleColumnWithRecursively<C>.recursivelyImpl(
    includeGroups: Boolean = true,
    includeTopLevel: Boolean = true,
): SingleColumn<C> = object : SingleColumn<C> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? =
        this@recursivelyImpl.resolveSingleRecursively(
            context = context,
            includeGroups = includeGroups,
            includeTopLevel = includeTopLevel,
        )
}

public interface SingleColumnWithRecursively<out C> : SingleColumn<C> {

    public fun resolveSingleRecursively(
        context: ColumnResolutionContext,
        includeGroups: Boolean = true,
        includeTopLevel: Boolean = true,
    ): ColumnWithPath<C>?
}


@OptIn(ExperimentalContracts::class)
public fun ColumnSet<*>.isSingleColumn(): Boolean {
    contract {
        returns(true) implies (this@isSingleColumn is SingleColumn<*>)
    }
    return this is SingleColumn<*>
}

public fun ColumnSet<*>.isSingleColumnGroup(cols: List<ColumnWithPath<*>>): Boolean =
    isSingleColumn() && cols.singleOrNull()?.isColumnGroup() == true
