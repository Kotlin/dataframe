package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

public interface FrameColumn<out T> : DataColumn<DataFrame<T>> {

    public val schema: Lazy<DataFrameSchema>

    override fun distinct(): FrameColumn<T>

    override fun kind(): ColumnKind = ColumnKind.Frame

    override fun rename(newName: String): FrameColumn<T>

    override fun get(indices: Iterable<Int>): FrameColumn<T>
}
