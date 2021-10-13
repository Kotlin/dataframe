package org.jetbrains.kotlinx.dataframe.impl.groupBy

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.GroupedDataRow
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn

internal class GroupedDataRowImpl<T, G>(private val row: DataRow<T>, private val frameCol: FrameColumn<G>) : GroupedDataRow<T, G>, DataRow<T> by row {

    override fun group() = groupOrNull()!!

    override fun groupOrNull() = frameCol[row.index()]
}
