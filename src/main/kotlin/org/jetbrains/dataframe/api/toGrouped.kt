package org.jetbrains.dataframe

import org.jetbrains.dataframe.impl.GroupedDataFrameImpl

fun <T, G> DataFrame<T>.toGrouped(selector: ColumnSelector<T, DataFrame<G>>): GroupedDataFrame<T, G> {
    val column = column(selector).asTable()
    return GroupedDataFrameImpl(this, column)
}