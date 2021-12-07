package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import kotlin.reflect.KType

internal class FrameColumnWithParent<T>(
    override val parent: ColumnGroup<*>,
    override val source: FrameColumn<T>
) : ColumnWithParent<DataFrame<T>>, FrameColumn<T> by source, DataColumnInternal<DataFrame<T>> {

    override fun equals(other: Any?) = source.checkEquals(other)

    override fun hashCode() = source.hashCode()

    override fun path() = super<ColumnWithParent>.path()

    override fun resolve(context: ColumnResolutionContext) = super<ColumnWithParent>.resolve(context)

    override fun resolveSingle(context: ColumnResolutionContext) = super<ColumnWithParent>.resolveSingle(context)

    override fun forceResolve() = ResolvingFrameColumn(this)

    override fun changeType(type: KType) = FrameColumnWithParent(parent, source.internal().changeType(type).asFrameColumn())

    override fun addParent(parent: ColumnGroup<*>) = FrameColumnWithParent(parent, source)

    override fun rename(newName: String) = FrameColumnWithParent(parent, source.rename(newName))
}
