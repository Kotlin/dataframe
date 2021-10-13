package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.impl.GroupedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.asTable

public fun <T, G> DataFrame<T>.asGrouped(selector: ColumnSelector<T, DataFrame<G>?>): GroupedDataFrame<T, G> {
    val column = column(selector).asTable()
    return GroupedDataFrameImpl(this, column) { none() }
}
