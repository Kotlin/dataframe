package org.jetbrains.dataframe.impl

internal class EmptyDataFrame<T>(override val nrow: Int) : DataFrameImpl<T>(emptyList())