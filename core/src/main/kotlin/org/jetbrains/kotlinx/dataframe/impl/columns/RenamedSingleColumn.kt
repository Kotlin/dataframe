package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn

internal class RenamedSingleColumn<C>(val source: SingleColumn<C>, val name: String) : SingleColumn<C> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<C>? =
        source.resolveSingle(context)?.let {
            it.data.rename(name).addPath(it.path)
        }
}

internal fun <C> SingleColumn<C>.renamedColumn(newName: String): SingleColumn<C> = RenamedSingleColumn(this, newName)
