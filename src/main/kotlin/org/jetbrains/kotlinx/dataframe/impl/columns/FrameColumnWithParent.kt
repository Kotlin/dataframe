package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn

internal class FrameColumnWithParent<T>(
    override val parent: ColumnGroup<*>,
    override val source: FrameColumn<T>
) : ColumnWithParent<DataFrame<T>?>, FrameColumn<T> by source {

    override fun equals(other: Any?) = source.checkEquals(other)

    override fun hashCode() = source.hashCode()

    override fun path() = super<ColumnWithParent>.path()

    override fun resolve(context: ColumnResolutionContext) = super<ColumnWithParent>.resolve(context)

    override fun resolveSingle(context: ColumnResolutionContext) = super<ColumnWithParent>.resolveSingle(context)
}
