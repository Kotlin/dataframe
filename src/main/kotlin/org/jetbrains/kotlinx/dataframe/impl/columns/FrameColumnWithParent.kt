package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext

internal class FrameColumnWithParent<T>(
    override val parent: ColumnGroup<*>,
    override val source: FrameColumnInternal<T>
) : ColumnWithParent<DataFrame<T>?>, FrameColumnInternal<T> by source {

    override fun equals(other: Any?) = source.checkEquals(other)

    override fun hashCode() = source.hashCode()

    override fun path() = super<ColumnWithParent>.path()

    override fun resolve(context: ColumnResolutionContext) = super<ColumnWithParent>.resolve(context)

    override fun resolveSingle(context: ColumnResolutionContext) = super<ColumnWithParent>.resolveSingle(context)
}
