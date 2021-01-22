package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.ColumnKind
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.NestedColumn

interface FrameColumn<out T> : DataColumn<DataFrame<T>>, NestedColumn<T> {

    override fun kind() = ColumnKind.Frame
}