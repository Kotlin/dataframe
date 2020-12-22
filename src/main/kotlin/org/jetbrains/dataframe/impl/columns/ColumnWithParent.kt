package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnWithPath

internal interface ColumnWithParent<C> : ColumnDef<C> {

    val parent: GroupedColumnDef?

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? {

        val parentDef = parent
        val (targetDf, pathPrefix) = when(parentDef) {
            null -> context.df to emptyList()
            else -> {
                val parentCol = parentDef.resolveSingle(context) ?: return null
                val group = parentCol.data.asGrouped()
                group.df to parentCol.path
            }
        }

        val data = targetDf.getColumn<C>(name, context.unresolvedColumnsPolicy)
        return data?.addPath(pathPrefix + name)
    }
}