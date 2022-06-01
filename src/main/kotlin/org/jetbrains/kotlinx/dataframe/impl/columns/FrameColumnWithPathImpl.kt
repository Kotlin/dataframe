package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn

internal class FrameColumnWithPathImpl<T> internal constructor(
    override val data: FrameColumn<T>,
    override val path: ColumnPath
) : ColumnWithPath<DataFrame<T>>, FrameColumn<T> by data {

    override fun rename(newName: String) = if (newName == name()) this else FrameColumnWithPathImpl(
        data.rename(newName),
        path.dropLast(1) + newName
    )

    override fun path() = path
}
