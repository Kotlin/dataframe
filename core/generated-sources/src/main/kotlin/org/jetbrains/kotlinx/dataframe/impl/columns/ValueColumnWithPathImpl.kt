package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn

internal class ValueColumnWithPathImpl<T> internal constructor(
    override val data: ValueColumn<T>,
    override val path: ColumnPath
) : ColumnWithPath<T>, ValueColumn<T> by data {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T> = this

    override fun rename(newName: String) = if (newName == name()) this else ValueColumnWithPathImpl(data.rename(newName), path.dropLast(1) + newName)

    override fun path() = path
}
