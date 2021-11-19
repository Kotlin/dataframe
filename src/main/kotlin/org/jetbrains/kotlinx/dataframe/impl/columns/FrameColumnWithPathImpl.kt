package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn

internal class FrameColumnWithPathImpl<T> internal constructor(
    override val data: FrameColumn<T>,
    override val path: ColumnPath,
    val container: ColumnsContainer<*>?
) : ColumnWithPath<DataFrame<T>>, FrameColumn<T> by data {

    override val parent by lazy {
        if (path.isNotEmpty()) path.dropLast(1).let { host[it].addPath(it, host) } else null
    }

    override fun rename(newName: String) = if (newName == name()) this else FrameColumnWithPathImpl(
        data.rename(newName),
        path.dropLast(1) + newName,
        host
    )

    override val host: ColumnsContainer<*>
        get() = container!!

    override fun path() = path
}
