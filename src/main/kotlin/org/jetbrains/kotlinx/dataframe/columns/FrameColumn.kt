package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.ColumnKind
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame

public interface FrameColumn<out T> : DataColumn<DataFrame<T>?> {

    override fun distinct(): FrameColumn<T>

    override fun kind(): ColumnKind = ColumnKind.Frame
}
