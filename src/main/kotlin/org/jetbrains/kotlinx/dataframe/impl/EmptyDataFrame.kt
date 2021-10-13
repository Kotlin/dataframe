package org.jetbrains.kotlinx.dataframe.impl

internal class EmptyDataFrame<T>(val nrow: Int) : DataFrameImpl<T>(emptyList()) {
    override fun nrow() = nrow
}
